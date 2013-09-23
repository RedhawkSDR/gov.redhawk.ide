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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.util.SadResourceImpl;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class SadLaunchShortcut implements ILaunchShortcut {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void launch(final ISelection selection, final String mode) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			final Object element = ss.getFirstElement();
			if (element instanceof IFile) {
				try {
					launch((IFile) element, mode);
				} catch (final CoreException e) {
					final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
					StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
				}
			} else if (element instanceof IProject) {
				final IProject project = (IProject) element;
				final IFile file = project.getFile(new Path(project.getName() + SadPackage.FILE_EXTENSION));
				if (file.exists()) {
					try {
						launch((IFile) element, mode);
					} catch (final CoreException e) {
						final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
						StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
					}
				}
			}
		}

	}

	private void launch(final IFile element, final String mode) throws CoreException {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		SoftwareAssembly sad;
		try {
			final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(element.getFullPath().toString(), true), true);
			sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		} catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to launch: " + element, e),
			        StatusManager.SHOW | StatusManager.LOG);
			return;
		}
		Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
		ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(sad, shell);
		ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, LaunchUtil.findLaunchConfigurations(newConfig), shell);
		if (config == null) {
			config = newConfig.doSave();
		}
		LaunchUtil.launch(config, mode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void launch(final IEditorPart editor, final String mode) {
		if (editor instanceof SCAFormEditor) {
			final SCAFormEditor formEditor = (SCAFormEditor) editor;
			if (formEditor.getMainResource() instanceof SadResourceImpl) {
				final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(formEditor.getMainResource());
				try {
					Shell shell = PlatformUI.getWorkbench().getDisplay().getActiveShell();
					ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(sad, shell);
					ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, LaunchUtil.findLaunchConfigurations(newConfig), shell);
					if (config == null) {
						config = newConfig.doSave();
					}
					LaunchUtil.launch(config, mode);
				} catch (final CoreException e) {
					final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
					StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
				}
			}
		}
	}

}
