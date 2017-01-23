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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;

import CF.DataType;
import CF.ExecutableDevice;
import gov.redhawk.ide.debug.LocalScaExecutableDevice;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.CorbaUtils;

public class LocalContainedComponentProgramLaunchDelegate extends LocalComponentProgramLaunchDelegate {

	@Override
	protected void launchComponent(SoftPkg spd, ILaunchConfigurationWorkingCopy workingCopy, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {
		final int WORK_CONTAINER_LAUNCH = 10;
		final int WORK_LAUNCH = 10;
		final int WORK_POST_LAUNCH = 100;
		SubMonitor subMonitor = SubMonitor.convert(monitor, WORK_CONTAINER_LAUNCH + WORK_LAUNCH + WORK_POST_LAUNCH);

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

		// Grab an existing component host, or launch a new one
 		LocalScaExecutableDevice componentHost = waveform.fetchComponentHost(mode, subMonitor.split(WORK_CONTAINER_LAUNCH));
		final ExecutableDevice execDev = CF.ExecutableDeviceHelper.narrow(componentHost.getCorbaObj());

		// Get path to the component's .so
		String spdFileName = spd.eResource().getURI().lastSegment();
		final String entryPoint = spd.eResource().getURI().toFileString().replace(spdFileName, "cpp/" + spd.getName() + ".so");

		// TODO: Move this to another method
		// Build component options, parameters, and dependencies
		ORB orb = ORB.init();
		final List<DataType> parameters = new ArrayList<DataType>();
		final Any compId = orb.create_any();
		compId.insert_string(launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER));
		Any nameBinding = orb.create_any();
		nameBinding.insert_string(launch.getAttribute(LaunchVariables.NAME_BINDING));
		Any profileName = orb.create_any();
		profileName.insert_string(launch.getAttribute(LaunchVariables.PROFILE_NAME));
		Any namingContextIOR = orb.create_any();
		namingContextIOR.insert_string(launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR));
		parameters.add(new DataType(LaunchVariables.COMPONENT_IDENTIFIER, compId));
		parameters.add(new DataType(LaunchVariables.NAME_BINDING, nameBinding));
		parameters.add(new DataType(LaunchVariables.PROFILE_NAME, profileName));
		parameters.add(new DataType(LaunchVariables.NAMING_CONTEXT_IOR, namingContextIOR));

		// Use ComponentHost to launch the component in a thread
		try {
			CorbaUtils.invoke(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					execDev.executeLinked(entryPoint, new DataType[0], parameters.toArray(new DataType[0]), new String[0]);
					return null;
				}
			}, subMonitor);
			subMonitor.worked(WORK_LAUNCH);
		} catch (InterruptedException e) {
			throw new OperationCanceledException(e.getMessage());
		}

		SpdLauncherUtil.postLaunch(spd, workingCopy, mode, launch, subMonitor.split(WORK_POST_LAUNCH));

	}
}
