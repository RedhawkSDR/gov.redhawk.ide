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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * An Eclipse launch delegate which handles launching shared address-space components
 */
public class LocalContainedComponentProgramLaunchDelegate extends LocalComponentProgramLaunchDelegate {

	@Override
	protected void launchComponent(SoftPkg spd, ILaunchConfigurationWorkingCopy workingCopy, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {

		// Find the application we are working with
		String waveformName = launch.getLaunchConfiguration().getAttribute(LaunchVariables.WAVEFORM_NAME, (String) null);
		LocalScaWaveform waveform = null;
		if (waveformName == null) {
			waveform = ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform();
		} else {
			for (ScaWaveform w : ScaDebugPlugin.getInstance().getLocalSca().getWaveforms()) {
				if (waveformName.equals(w.getName()) && w instanceof LocalScaWaveform) {
					waveform = (LocalScaWaveform) w;
				}
			}
		}

		if (waveform == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Unable to locate target waveform for " + spd.getName()));
		}

		String implID = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		Implementation impl = spd.getImplementation(implID);

		ComponentProgramLaunchUtils.launch(waveform, workingCopy, (ComponentLaunch) launch, spd, impl, mode, monitor);
	}
}
