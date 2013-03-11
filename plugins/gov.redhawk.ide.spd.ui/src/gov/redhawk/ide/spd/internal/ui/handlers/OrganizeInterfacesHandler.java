/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import mil.jpeojtrs.sca.scd.InheritsInterface;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Interfaces;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdDocumentRoot;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdDocumentRoot;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @since 6.0
 */
public class OrganizeInterfacesHandler extends AbstractHandler implements IHandler {

	private class CleanUpJob extends Job {

		/**
		 * @param name
		 */
		public CleanUpJob() {
			super("Clean Up Component Files Job");
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			for (final Object obj : OrganizeInterfacesHandler.this.fSelection.toArray()) {
				if (obj instanceof IFile) {
					final IFile file = (IFile) obj;
					final ResourceSet set = new ResourceSetImpl();
					final Resource tempRes = set.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true);

					// Load the resource and get the root object
					try {
						tempRes.load(null);
					} catch (final IOException e1) {
						return new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Failed to clean up Component Files in " + file.getName(), e1);
					}
					final EObject eObj = tempRes.getEObject("/");

					// Get the SCD, either through the SPD or from the SCD file
					if (eObj instanceof SoftPkg) {
						final SoftPkg spd = (SoftPkg) eObj;
						OrganizeInterfacesHandler.this.resource = spd.getDescriptor().getComponent();
					} else if (eObj instanceof SoftwareComponent) {
						OrganizeInterfacesHandler.this.resource = (SoftwareComponent) eObj;
					} else if (eObj instanceof SpdDocumentRoot) {
						final SpdDocumentRoot root = (SpdDocumentRoot) eObj;
						OrganizeInterfacesHandler.this.resource = root.getSoftpkg().getDescriptor().getComponent();
					} else if (eObj instanceof ScdDocumentRoot) {
						final ScdDocumentRoot root = (ScdDocumentRoot) eObj;
						OrganizeInterfacesHandler.this.resource = root.getSoftwarecomponent();
					} else {
						continue;
					}

					// Load the IDL library, used for adding missing interfaces
					try {
						OrganizeInterfacesHandler.this.loadIdlLibrary(monitor, file.getProject());
					} catch (final CoreException e1) {
						ComponentUiPlugin.logException(e1, "Unable to load IDL Library, cannot organize interfaces.");
					}

					// Get the list of interfaces and port Rep IDs
					final EList<Interface> interfaces = OrganizeInterfacesHandler.this.resource.getInterfaces().getInterface();
					final Ports ports = getPorts();
					final HashSet<String> ints = new HashSet<String>();
					for (final Provides p : ports.getProvides()) {
						ints.add(p.getRepID());
					}
					for (final Uses u : ports.getUses()) {
						ints.add(u.getRepID());
					}

					// Add any missing port interfaces
					for (final String repId : ints) {
						// Only add the interface if it's not already there.
						if (!hasInterface(repId)) {
							addInterface(repId, interfaces);
						}
					}

					// Check each interface in the list to see if it's still
					// used, remove it if not.
					final List<Interface> badIfaces = new ArrayList<Interface>();
					for (final Interface inter : interfaces) {
						final Collection<Setting> references = EcoreUtil.UsageCrossReferencer.find(inter, inter.eResource());
						if (references.size() == 0) {
							badIfaces.add(inter);
						}
					}

					// Remove any unused interfaces
					interfaces.removeAll(badIfaces);

					// Save the modified SCD resource
					try {
						OrganizeInterfacesHandler.this.resource.eResource().save(null);
					} catch (final IOException e) {
						ComponentUiPlugin.logException(e, "Unable to save resource after organizing interfaces");
					}
				}
			}
			return Status.OK_STATUS;
		}
	}

	private IStructuredSelection fSelection;

	private SoftwareComponent resource;

	private IdlLibrary idlLibrary;

	private IWorkbenchSiteProgressService getActiveProgressService() {
		IWorkbenchSiteProgressService service = null;
		if (PlatformUI.isWorkbenchRunning()) {
			final IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null) {
				final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if (activePage != null) {
					final IWorkbenchPart activePart = activePage.getActivePart();
					if (activePart != null) {
						service = (IWorkbenchSiteProgressService) activePart.getSite().getAdapter(IWorkbenchSiteProgressService.class);
					}
				}
			}
		}
		return service;
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getSelectionService().getSelection();

		if (selection instanceof IStructuredSelection) {
			this.fSelection = (IStructuredSelection) selection;
		}

		if (this.fSelection != null && !this.fSelection.isEmpty()) {
			final Job job = getJob();
			if (job != null) {
				final IWorkbenchSiteProgressService progressService = getActiveProgressService();
				if (progressService != null) {
					progressService.schedule(job);
				} else {
					job.schedule();
				}
			}
		}

		return null;
	}

	/**
	 * @return
	 */
	private Job getJob() {
		return new CleanUpJob();
	}

	/**
	 * This method checks if there is an interface with the specified RepID.
	 * 
	 * @param repId the Rep ID to check
	 */
	private boolean hasInterface(final String repId) {
		final Interfaces ints = this.resource.getInterfaces();
		for (final EObject e : ints.eContents()) {
			if (repId.equals(((Interface) e).getRepid())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method returns an interface, corresponding to the passed in Rep ID,
	 * to add to the interface list. It recurses itself adding missing inherits
	 * interfaces.
	 * 
	 * @param newRep the Rep ID to add
	 * @param interfaces the interfaces object to add the interface to
	 */
	private void addInterface(final String repId, final EList<Interface> interfaces) {
		final Interface i = ScdFactory.eINSTANCE.createInterface();
		final IdlInterfaceDcl idlInter = (IdlInterfaceDcl) this.idlLibrary.find(repId);

		// Make sure we found the interface object
		if (idlInter != null) {
			i.setName(idlInter.getName());
			i.setRepid(repId);

			// Loop through the inherited interfaces for this interface and add
			// any required interfaces
			for (final IdlInterfaceDcl inherited : idlInter.getInheritedInterfaces()) {
				final InheritsInterface iface = ScdFactory.eINSTANCE.createInheritsInterface();
				iface.setRepid(inherited.getRepId());
				i.getInheritsInterfaces().add(iface);
				addInterface(inherited.getRepId(), interfaces);
			}

			interfaces.add(i);
		}
	}

	/**
	 * This method returns the list of ports for the component.
	 * 
	 * @return the list of ports
	 */
	private Ports getPorts() {
		return this.resource.getComponentFeatures().getPorts();
	}

	/**
	 * This method loads the IDL library from the current project.
	 * 
	 * @param monitor the progress monitor to show loading progress
	 * @param project the project to load the IDL for
	 * @throws CoreException
	 */
	public IdlLibrary loadIdlLibrary(final IProgressMonitor monitor, final IProject project) throws CoreException {
		if (this.idlLibrary == null) {
			final ResourceSet set = new ResourceSetImpl();
			final IFile libraryFile = project.getFile(".library");
			if (libraryFile.exists()) {
				final Resource res = set.getResource(URI.createPlatformResourceURI(libraryFile.getFullPath().toString(), true), true);
				final EObject obj = res.getEObject("/");
				if (obj instanceof IdlLibrary) {
					this.idlLibrary = (IdlLibrary) obj;
					this.idlLibrary.load(monitor);
				}
			}
		}

		return this.idlLibrary;
	}

}
