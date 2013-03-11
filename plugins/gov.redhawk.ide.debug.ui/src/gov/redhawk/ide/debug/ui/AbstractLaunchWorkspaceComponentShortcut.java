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
package gov.redhawk.ide.debug.ui;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public abstract class AbstractLaunchWorkspaceComponentShortcut extends AbstractLaunchComponentShortcut {

	@Override
	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final SoftPkg spd, final Implementation impl) throws CoreException {
		final ILaunchConfigurationWorkingCopy retVal = super.createLaunchConfiguration(name, spd, impl);

		final IFile resource = getResource(spd.eResource().getURI());
		retVal.setMappedResources(new IResource[] {
			resource.getProject()
		});

		return retVal;
	}

	@Override
	protected String getProfile(final SoftPkg spd) {
		return spd.eResource().getURI().toPlatformString(true);
	}

	protected IFile getResource(final URI uri) {
		final String path = uri.toPlatformString(true);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
	}

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
			}
		}
	}

	protected void launch(final IFile element, final String mode) throws CoreException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		SoftPkg spd;
		try {
			final Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(element.getFullPath().toString(), true), true);
			spd = SoftPkg.Util.getSoftPkg(resource);
			launch(spd, mode);
		} catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to launch: " + element, e),
			        StatusManager.SHOW | StatusManager.LOG);
			return;
		}
	}
}
