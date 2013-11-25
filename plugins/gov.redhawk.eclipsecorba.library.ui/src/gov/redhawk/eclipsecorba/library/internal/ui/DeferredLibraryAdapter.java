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
package gov.redhawk.eclipsecorba.library.internal.ui;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.ui.LibraryUIPlugin;
import gov.redhawk.sca.util.Debug;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.IElementCollector;

/**
 * Provides deferred loading ability of the IDL library folder under an {@link IProject}.
 */
public class DeferredLibraryAdapter implements IDeferredWorkbenchAdapter {
	
	private static final Debug DEBUG = new Debug(LibraryUIPlugin.PLUGIN_ID, "deferredLibraryAdapter");

	/**
	 * A mapping of {@link IProject}s to their associated {@link IdlLibrary}. Entries are populated upon demand load.
	 */
	private final Map<IProject, IdlLibrary> libraryMap = new HashMap<IProject, IdlLibrary>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fetchDeferredChildren(final Object object, final IElementCollector collector, final IProgressMonitor monitor) {
		final IProject project = (IProject) object;
		final IdlLibrary library = getLibrary(project);
		if (library != null) {
			try {
				library.load(monitor);
			} catch (final CoreException e) {
				LibraryUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, "Failed to load library.", e));
			} finally {
				collector.add(library, monitor);
				collector.done();
				monitor.done();
			}
		}
	}

	/**
	 * @return the libraryMap
	 */
	public Map<IProject, IdlLibrary> getLibraryMap() {
		return this.libraryMap;
	}

	/**
	 * @param project
	 * @return
	 */
	private IdlLibrary getLibrary(final IProject project) {
		IdlLibrary library = this.libraryMap.get(project);
		if (library == null) {
			final TransactionalEditingDomain editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
			final ResourceSet resourceSet = editingDomain.getResourceSet();
			final IFile libraryFile = project.getFile(".library");
			if (!libraryFile.exists()) {
				return null;
			}
			try {
				final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(libraryFile.getFullPath().toString(), true), true);
				library = (IdlLibrary) resource.getEObject("/");
			} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged error in Trace log
				if (DEBUG.enabled) {
					DEBUG.catching("Failed to get IDL library resource.", e);
				}
				return null;
			}
			this.libraryMap.put(project, library);
		}
		return library;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ISchedulingRule getRule(final Object object) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isContainer() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object o) {
		return new Object[] { this.libraryMap.get(o) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImageDescriptor getImageDescriptor(final Object object) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(LibraryUIPlugin.PLUGIN_ID, "icons/IdlLibrary.gif");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel(final Object o) {
		return null; // Handled elsewhere
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(final Object o) {
		return null;
	}

}
