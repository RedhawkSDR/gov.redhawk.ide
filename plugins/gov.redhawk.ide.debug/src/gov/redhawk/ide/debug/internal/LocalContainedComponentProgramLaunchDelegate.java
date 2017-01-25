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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.EList;
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
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.CorbaUtils;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

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
		final LocalScaExecutableDevice componentHost = waveform.fetchComponentHost(mode, subMonitor.split(WORK_CONTAINER_LAUNCH));
		final ExecutableDevice execDev = CF.ExecutableDeviceHelper.narrow(componentHost.getCorbaObj());

		// Get path to the component's .so
		String spdFileName = spd.eResource().getURI().lastSegment();
		final String entryPoint = spd.eResource().getURI().toFileString().replace(spdFileName, "cpp/" + spd.getName() + ".so");

		ORB orb = ORB.init();
		// TODO: Get Component Options
		final DataType[] options = getComponentOptions(spd, launch, orb);
		final DataType[] parameters = getComponentParameters(launch, orb);
		// TODO: Get Component Dependencies
		final String[] deps = getComponentDependencies(spd, launch, orb);

		// Use ComponentHost to launch the component in a thread
		try {
			CorbaUtils.invoke(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					execDev.executeLinked(entryPoint, options, parameters, deps);
					return null;
				}
			}, subMonitor);

			subMonitor.worked(WORK_LAUNCH);
		} catch (InterruptedException e) {
			throw new OperationCanceledException(e.getMessage());
		}
		SpdLauncherUtil.postLaunch(spd, workingCopy, mode, launch, subMonitor.split(WORK_POST_LAUNCH));
		if (launch instanceof ComponentLaunch) {
			((ComponentLaunch) launch).setParent(componentHost.getLaunch());
		}
	}

	private DataType[] getComponentOptions(SoftPkg spd, ILaunch launch, ORB orb) {
		List<DataType> options = new ArrayList<DataType>();

		// TODO: See Deployment.cpp getOptions()
		// TODO: Affinity?
		// TODO: Will these ever be populated?
		Code code = spd.getImplementation().get(0).getCode();
		if (code.getStackSize() != null) {
			Any stackSize = orb.create_any();
			stackSize.insert_longlong(code.getStackSize().longValue());
			options.add(new DataType(ExecutableDevice.STACK_SIZE_ID, stackSize));
		}
		if (code.getPriority() != null) {
			Any priority = orb.create_any();
			priority.insert_longlong(code.getPriority().longValue());
			options.add(new DataType(ExecutableDevice.PRIORITY_ID, priority));
		}
		return options.toArray(new DataType[0]);
	}

	private DataType[] getComponentParameters(ILaunch launch, ORB orb) {

		Any compId = orb.create_any();
		compId.insert_string(launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER));

		Any nameBinding = orb.create_any();
		nameBinding.insert_string(launch.getAttribute(LaunchVariables.NAME_BINDING));

		Any profileName = orb.create_any();
		profileName.insert_string(launch.getAttribute(LaunchVariables.PROFILE_NAME));

		Any namingContextIOR = orb.create_any();
		namingContextIOR.insert_string(launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR));

		List<DataType> parameters = new ArrayList<DataType>();
		parameters.add(new DataType(LaunchVariables.COMPONENT_IDENTIFIER, compId));
		parameters.add(new DataType(LaunchVariables.NAME_BINDING, nameBinding));
		parameters.add(new DataType(LaunchVariables.PROFILE_NAME, profileName));
		parameters.add(new DataType(LaunchVariables.NAMING_CONTEXT_IOR, namingContextIOR));

		return parameters.toArray(new DataType[0]);
	}

	private String[] getComponentDependencies(SoftPkg spd, ILaunch launch, ORB orb) {
		List<String> deps = new ArrayList<String>();
		// TODO: How to find SDRROOT? Is there an existing method that can be used for this?
		// We may need to provide the absolute location
		// CF does relative path = e.g. /dom/deps/rh/dsp/cpp/lib
		EList<Dependency> depList = spd.getImplementation().get(0).getDependency();
		for (Dependency dep : depList) {
			String localFile = ScaEcoreUtils.getFeature(dep, SpdPackage.Literals.DEPENDENCY__SOFT_PKG_REF, SpdPackage.Literals.SOFT_PKG_REF__LOCAL_FILE,
				SpdPackage.Literals.LOCAL_FILE__NAME);
			IPath path = new Path("/var/redhawk/sdr/dom" + localFile);

//			IPath path = new Path("../../../" + localFile);  // TODO: Relative also works.  
			// Maybe check if starts with dep and use relative or absolute SDRROOT path? otherwise use what is in the
			// spd.xml?
			// Are sharedlibs required to be installed in a predefined location?

			deps.add(path.removeLastSegments(1).append("cpp/lib").toString());
		}

		return deps.toArray(new String[0]);
	}
}
