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
package gov.redhawk.ide.debug.internal.ui.launchshortcuts;

import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.spd.util.SpdResourceImpl;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * This launch shortcut gives the "Run As -> Component in the Sandbox" (also used by devices/services). It can be
 * invoked from:
 * <ul>
 *   <li>An SPD file's context menu</li>
 *   <li>A project's context menu</li>
 *   <li>By clicking the Eclipse run button while the project, XML or SPD editor are selected</li>
 * </ul>
 */
public class ComponentLaunchShortcut implements ILaunchShortcut {

	@Override
	public void launch(final ISelection selection, final String mode) {
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) selection;
			final Object element = ss.getFirstElement();
			if (element instanceof IFile) {
				final IFile file = (IFile) element;
				if (file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
					launch(mode, file);
				}
			} else if (element instanceof IProject) {
				final IProject project = (IProject) element;
				final IFile file = project.getFile(new Path(project.getName() + SpdPackage.FILE_EXTENSION));
				if (file.exists()) {
					launch(mode, file);
				}
			}
		}

	}

	private void launch(final String mode, final IFile file) {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		try {
			final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(file.getFullPath().toPortableString(), true), true);
			final SoftPkg spd = SoftPkg.Util.getSoftPkg(resource);
			launch(spd, mode);
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to load SPD: " + file, e);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	private void launch(final SoftPkg spd, final String mode) {
		final Shell shell = Display.getCurrent().getActiveShell();
		try {
			ILaunchConfigurationWorkingCopy newConfig = LaunchUtil.createLaunchConfiguration(spd, shell);
			ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, LaunchUtil.findLaunchConfigurations(newConfig), shell);
			if (config == null) {
				config = newConfig.doSave();
			}
			
			DebugUITools.launch(config, mode);
		} catch (final CoreException e) {
			final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e);
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
		}
	}

	@Override
	public void launch(final IEditorPart editor, final String mode) {
		if (editor instanceof SCAFormEditor) {
			final SCAFormEditor formEditor = (SCAFormEditor) editor;
			if (formEditor.getMainResource() instanceof SpdResourceImpl) {
				launch(SoftPkg.Util.getSoftPkg(formEditor.getMainResource()), mode);
			}
		}
	}

}
