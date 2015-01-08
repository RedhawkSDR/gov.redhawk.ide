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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.SpdLauncherUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.externaltools.internal.launchConfigurations.ProgramLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * An Eclipse launch delegate which handles launching a SoftPkg (component, device, etc) locally.
 */
public class LocalComponentProgramLaunchDelegate extends ProgramLaunchDelegate {
	public static final String ID = "gov.redhawk.ide.debug.localComponentProgram";

	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		final SoftPkg spd = SpdLauncherUtil.getSpd(configuration);
		insertProgramArguments(spd, launch, workingCopy);
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
		try {
			super.launch(workingCopy, mode, launch, subMonitor.newChild(90));
			SpdLauncherUtil.postLaunch(spd, workingCopy, mode, launch, subMonitor.newChild(10));
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}
	
	@Override
	protected boolean saveBeforeLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return true;
	}

	/**
	 * Retrieves the command-line arguments for the executable from the launch configuration and expands variable
	 * references. This ensures variables related to running the SoftPkg (naming context, exec params, etc) are expanded.
	 *
	 * @param spd The SoftPkg being executed
	 * @param launch The launch that's about to occur
	 * @param configuration A working copy of the launch's configuration
	 * @throws CoreException
	 */
	protected void insertProgramArguments(final SoftPkg spd, final ILaunch launch, final ILaunchConfigurationWorkingCopy configuration) throws CoreException {
		final String args = configuration.getAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "");
		final String scaArgs = SpdLauncherUtil.insertProgramArguments(spd, args, launch, configuration);
		configuration.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, scaArgs);
	}

}
