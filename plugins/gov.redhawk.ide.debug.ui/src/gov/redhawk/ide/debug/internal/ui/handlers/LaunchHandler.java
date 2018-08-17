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
package gov.redhawk.ide.debug.internal.ui.handlers;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.internal.ui.wizards.LaunchComponentWizard;
import gov.redhawk.ide.debug.internal.ui.wizards.LaunchLocalWaveformWizard;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.ide.sdr.impl.ServicesContainerImpl;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Handles launching something in the sandbox. The event can indicate a default launch, or an advanced launch wizard.
 * It can be for a component, device, service or waveform.
 */
public class LaunchHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// Check if we're supposed to use an advanced launch wizard
		String value = event.getParameter("gov.redhawk.ide.debug.ui.wizardType");
		if ("waveform".equalsIgnoreCase(value)) {
			handleLaunchWaveform(event);
		} else if ("component".equalsIgnoreCase(value) || "device".equalsIgnoreCase(value) || "service".equalsIgnoreCase(value)) {
			try {
				handleLaunchComponentType(event);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		} else {
			// Launch with defaults (no wizard)
			ISelection sel = HandlerUtil.getActiveMenuSelection(event);
			if (sel == null) {
				sel = HandlerUtil.getCurrentSelection(event);
			}
			if (sel instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) sel;
				for (final Object obj : ss.toList()) {
					try {
						handleLaunch(obj, event);
					} catch (final CoreException e) {
						StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getLocalizedMessage(), e),
							StatusManager.LOG | StatusManager.SHOW);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Handles launching a component/device/service locally based on the results of the advanced launch wizard.
	 * @param event The event details
	 * @throws CoreException
	 */
	private void handleLaunchComponentType(ExecutionEvent event) throws CoreException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel == null) {
			sel = HandlerUtil.getCurrentSelection(event);
		}
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object element = ss.getFirstElement();
			LaunchComponentWizard wizard = new LaunchComponentWizard();
			if (ss instanceof TreeSelection) {
				TreeSelection ts = (TreeSelection) ss;
				TreePath[] tps = ts.getPathsFor(element);
				if (tps != null && tps.length > 0 && tps[0].getSegmentCount() > 1 && tps[0].getSegment(1) instanceof ServicesContainerImpl) {
					wizard.hideAutoStartControl();
				}
			}

			setLaunchValues(event, wizard, element);

			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			dialog.open();
		}
	}

	private void setLaunchValues(ExecutionEvent event, LaunchComponentWizard wizard, Object element) throws CoreException {
		String type = event.getParameter("gov.redhawk.ide.debug.ui.wizardType");
		if (element instanceof SoftPkgRegistry) {
			wizard.setWindowTitle("Launch");
			wizard.setSpdContainer((SoftPkgRegistry) element);
		} else if (element instanceof SoftPkg) {
			wizard.setWindowTitle("Launch");
			wizard.setSoftPkg((SoftPkg) element);
		} else if (element instanceof Implementation) {
			wizard.setWindowTitle("Launch");
			wizard.setImplementation((Implementation) element);
		} else if (element instanceof SdrRoot) {
			SdrRoot root = (SdrRoot) element;
			if ("device".equalsIgnoreCase(type)) {
				wizard.setWindowTitle("Launch Device");
				wizard.setSpdContainer(root.getDevicesContainer());
			} else if ("service".equalsIgnoreCase(type)) {
				wizard.setWindowTitle("Launch Service");
				wizard.setSpdContainer(root.getServicesContainer());
				wizard.hideAutoStartControl();
			} else {
				wizard.setWindowTitle("Launch Component");
				wizard.setSpdContainer(root.getComponentsContainer());
			}
		} else if (element instanceof LocalSca) {
			SdrRoot root = TargetSdrRoot.getSdrRoot();
			if ("device".equalsIgnoreCase(type)) {
				wizard.setWindowTitle("Launch Device");
				wizard.setSpdContainer(root.getDevicesContainer());
			} else if ("service".equalsIgnoreCase(type)) {
				wizard.setWindowTitle("Launch Service");
				wizard.setSpdContainer(root.getServicesContainer());
			} else {
				wizard.setWindowTitle("Launch Component");
				wizard.setSpdContainer(root.getComponentsContainer());
			}
		}
	}

	/**
	 * Handles launching a waveform locally based on the results of the advanced launch wizard.
	 * @param event The event details
	 */
	private void handleLaunchWaveform(ExecutionEvent event) {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel == null) {
			sel = HandlerUtil.getCurrentSelection(event);
		}
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) sel;
			Object element = ss.getFirstElement();
			LaunchLocalWaveformWizard wizard = new LaunchLocalWaveformWizard();
			if (element instanceof SoftwareAssembly) {
				wizard.setSoftwareAssembly((SoftwareAssembly) element);
			} else if (element instanceof WaveformsContainer) {
				WaveformsContainer container = (WaveformsContainer) element;
				wizard.setWaveformsContainer(container);
			} else if (element instanceof SdrRoot) {
				SdrRoot root = (SdrRoot) element;
				wizard.setWaveformsContainer(root.getWaveformsContainer());
			} else if (element instanceof LocalSca) {
				SdrRoot root = TargetSdrRoot.getSdrRoot();
				wizard.setWaveformsContainer(root.getWaveformsContainer());
			}
			WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
			dialog.open();
		}
	}

	@Override
	public void setEnabled(Object evaluationContext) {
		final IEvaluationContext context = (IEvaluationContext) evaluationContext;
		final IWorkbenchWindow window = (IWorkbenchWindow) context.getVariable("activeWorkbenchWindow");
		if (window == null) {
			setBaseEnabled(false);
			return;
		}

		if (context.getVariable("activeMenuSelection") != null && context.getVariable("activeMenuSelection") instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) context.getVariable("activeMenuSelection");
			boolean enabled = true;
			out: for (final Object obj : ss.toList()) {
				if (obj instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) obj;
					for (Implementation impl : spd.getImplementation()) {
						if (!impl.isExecutable()) {
							break out;
						}
					}
				} else if (obj instanceof Implementation) {
					Implementation impl = (Implementation) obj;
					if (!impl.isExecutable()) {
						break out;
					}
				}
			}
			setBaseEnabled(enabled);
		}
	}

	/**
	 * Handles launching something with default parameters.
	 * @param element An {@link IFile} to an SPD/SAD, a {@link SoftPkg}, a {@link SoftwareAssembly}, or an
	 * {@link Implementation}
	 * @param event The event details
	 * @throws CoreException
	 */
	private void handleLaunch(Object element, final ExecutionEvent event) throws CoreException {
		if (element instanceof IFile) {
			element = loadFile((IFile) element);
		}
		Shell shell = HandlerUtil.getActiveShell(event);
		ILaunchConfiguration config = null;
		if (element instanceof SoftPkg) {
			config = LaunchUtil.createLaunchConfiguration((SoftPkg) element, shell);
		} else if (element instanceof SoftwareAssembly) {
			config = LaunchUtil.createLaunchConfiguration((SoftwareAssembly) element, shell);
		} else if (element instanceof Implementation) {
			config = LaunchUtil.createLaunchConfiguration((Implementation) element);
		}
		if (config != null) {
			LaunchUtil.launch(config, ILaunchManager.RUN_MODE);
		}
	}

	/**
	 * Loads the model object for an SPD or SAD file.
	 * @param element
	 * @return The model object
	 * @throws CoreException The model object can't be loaded/created
	 */
	private Object loadFile(final IFile element) throws CoreException {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		try {
			final Resource resource = resourceSet.getResource(URI.createURI(URIUtil.toURI(element.getLocation()).toString()), true);
			if (element.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				return SoftPkg.Util.getSoftPkg(resource);
			} else if (element.getName().endsWith(SadPackage.FILE_EXTENSION)) {
				return SoftwareAssembly.Util.getSoftwareAssembly(resource);
			} else {
				throw new CoreException(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Unrecognized file extension for file " + element.getFullPath()));
			}
		} catch (CoreException e) {
			throw e;
		} catch (final WrappedException e) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Unable to load XML file " + element.getFullPath()));
		} catch (RuntimeException e) { // SUPPRESS CHECKSTYLE getResource() has a broad throw definition
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Error while creating resource for file " + element.getFullPath()));
		}
	}

}
