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

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunch;
import org.eclipse.cdt.dsf.gdb.launching.GdbLaunchDelegate;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ISourceLocator;

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * An Eclipse launch delegate which handles launching a SoftPkg installed in the SDRROOT in debug mode. Used mostly for
 * launching a ComponentHost program as part of launching a shared-address-space component in debug mode from the
 * workspace.
 */
public class LocalComponentDebugLaunchDelegate extends GdbLaunchDelegate {

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		final int WORK_LAUNCH = 10;
		final int WORK_POST_LAUNCH = 100;
		SubMonitor subMonitor = SubMonitor.convert(monitor, WORK_LAUNCH + WORK_POST_LAUNCH);

		// Validate all XML before doing anything else
		final SoftPkg spd = SpdLauncherUtil.getSpd(config);
		IStatus status = SpdLauncherUtil.validateAllXML(spd);
		if (!status.isOK()) {
			throw new CoreException(status);
		}

		// We used a working copy when constructing the ILaunch in getLaunch(ILaunchConfiguration, String)
		ILaunchConfigurationWorkingCopy workingCopy = (ILaunchConfigurationWorkingCopy) launch.getLaunchConfiguration();
		insertProgramArguments(spd, launch, workingCopy);

		// TODO: Where should we _actually_ be setting program name
		// Set the 'Program Name' attribute to the entry-point absolute path
		String implID = workingCopy.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		String entryPoint = spd.getImplementation(implID).getCode().getEntryPoint();
		String executable = spd.eResource().getURI().trimSegments(1).appendSegment(entryPoint).toFileString();
		workingCopy.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, executable);

		ComponentProgramLaunchUtils.insertProgramArguments(spd, launch, workingCopy);

		super.launch(workingCopy, mode, launch, subMonitor.split(WORK_LAUNCH));
		SpdLauncherUtil.postLaunch(spd, workingCopy, mode, launch, subMonitor.split(WORK_POST_LAUNCH));
	}

	/**
	 * Create a copy of the launch configuration, and use that to create the ILaunch. This is required for
	 * GdbLaunchDelegate because some areas of the code get the configuration from the ILaunch rather than using
	 * the configuration passed as argument #1 to launch(ILaunchConfiguration, String, ILaunch, IProgressMonitor).
	 * We modify the configuration at launch time with dynamic information (such as the naming context).
	 */
	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		final ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		return super.getLaunch(workingCopy, mode);
	}

	@Override
	protected GdbLaunch createGdbLaunch(ILaunchConfiguration configuration, String mode, ISourceLocator locator) throws CoreException {
		return new ComponentHostDebugLaunch(configuration, mode, locator);
	}

	protected void insertProgramArguments(final SoftPkg spd, final ILaunch launch, final ILaunchConfigurationWorkingCopy configuration) throws CoreException {
		final String args = configuration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, "");
		final String newArgs = SpdLauncherUtil.insertProgramArguments(spd, args, launch, configuration);
		configuration.setAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, newArgs);
	}
}
