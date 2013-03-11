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

import gov.redhawk.ide.debug.LocalComponentProgramLaunchDelegate;
import gov.redhawk.ide.debug.ui.AbstractLaunchWorkspaceComponentShortcut;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;

/**
 * 
 */
public class LocalComponentProgramLaunchShortcut extends AbstractLaunchWorkspaceComponentShortcut {

	@Override
	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final SoftPkg spd, final Implementation impl) throws CoreException {
		final ILaunchConfigurationWorkingCopy retVal = super.createLaunchConfiguration(name, spd, impl);
		final IFile spdFile = getResource(spd.eResource().getURI());
		final String entryPoint = impl.getCode().getEntryPoint();
		final IFile entryFile = spdFile.getParent().getFile(new Path(entryPoint));

		final String location = "${workspace_loc:" + entryFile.getFullPath().toPortableString() + "}";
		final String wd = "${workspace_loc:" + entryFile.getParent().getFullPath().toPortableString() + "}";
		LocalComponentProgramLaunchDelegate.setExternalToolsConfiguration(retVal, location, wd);
		return retVal;
	}

	@Override
	protected ILaunchConfigurationType getLaunchConfigType() {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(LocalComponentProgramLaunchDelegate.ID);
	}

}
