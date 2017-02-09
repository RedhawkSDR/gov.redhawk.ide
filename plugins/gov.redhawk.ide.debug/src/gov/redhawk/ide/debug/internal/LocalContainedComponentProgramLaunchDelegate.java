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

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
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
import org.eclipse.emf.common.util.URI;
import org.omg.CORBA.Any;

import CF.DataType;
import CF.ExecutableDevice;
import gov.redhawk.ide.debug.LocalScaExecutableDevice;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.internal.variables.ExecParamResolver;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.ide.sdr.util.AbstractEnvMap;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.CorbaUtils;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * An Eclipse launch delegate which handles launching shared address-space components
 */
public class LocalContainedComponentProgramLaunchDelegate extends LocalComponentProgramLaunchDelegate {

	@Override
	protected void launchComponent(SoftPkg spd, ILaunchConfigurationWorkingCopy workingCopy, String mode, ILaunch launch, IProgressMonitor monitor)
		throws CoreException {
		final int WORK_CONTAINER_LAUNCH = 10;
		final int WORK_LAUNCH = 10;
		final int WORK_DEPS = 1;
		final int WORK_POST_LAUNCH = 100;
		SubMonitor subMonitor = SubMonitor.convert(monitor, WORK_CONTAINER_LAUNCH + WORK_LAUNCH + WORK_DEPS + WORK_POST_LAUNCH);

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

		String implID = launch.getLaunchConfiguration().getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, (String) null);
		Implementation impl = spd.getImplementation(implID);
		final DataType[] options = getComponentOptions(spd, impl, launch);
		final DataType[] parameters = getComponentParameters(spd, impl, launch);
		final String[] deps = getComponentDependencies(impl, subMonitor.split(WORK_DEPS));

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

	private DataType[] getComponentOptions(SoftPkg spd, Implementation impl, ILaunch launch) {
		List<DataType> options = new ArrayList<DataType>();

		Code code = impl.getCode();
		if (code.getStackSize() != null) {
			Any stackSize = AnyUtils.toAny(code.getStackSize(), "ulong", false);
			options.add(new DataType(ExecutableDevice.STACK_SIZE_ID, stackSize));
		}
		if (code.getPriority() != null) {
			Any priority = AnyUtils.toAny(code.getPriority(), "ulong", false);
			options.add(new DataType(ExecutableDevice.PRIORITY_ID, priority));
		}

		// TODO: Handle affinity

		return options.toArray(new DataType[0]);
	}

	private DataType[] getComponentParameters(SoftPkg spd, Implementation impl, ILaunch launch) throws CoreException {

		Any compId = AnyUtils.toAny(launch.getAttribute(LaunchVariables.COMPONENT_IDENTIFIER), "string", false);
		Any nameBinding = AnyUtils.toAny(launch.getAttribute(LaunchVariables.NAME_BINDING), "string", false);
		Any profileName = AnyUtils.toAny(launch.getAttribute(LaunchVariables.PROFILE_NAME), "string", false);
		Any namingContextIOR = AnyUtils.toAny(launch.getAttribute(LaunchVariables.NAMING_CONTEXT_IOR), "string", false);

		List<DataType> parameters = new ArrayList<DataType>();
		parameters.add(new DataType(LaunchVariables.COMPONENT_IDENTIFIER, compId));
		parameters.add(new DataType(LaunchVariables.NAME_BINDING, nameBinding));
		parameters.add(new DataType(LaunchVariables.PROFILE_NAME, profileName));
		parameters.add(new DataType(LaunchVariables.NAMING_CONTEXT_IOR, namingContextIOR));

		List<ScaSimpleProperty> execParamList = ExecParamResolver.getExecParams(launch.getLaunchConfiguration(), spd, impl);
		for (ScaSimpleProperty simple : execParamList) {
			parameters.add(new DataType(simple.getId(), simple.toAny()));
		}

		return parameters.toArray(new DataType[0]);
	}

	private String[] getComponentDependencies(Implementation impl, IProgressMonitor monitor) throws CoreException {
		List<String> deps = new ArrayList<String>();

		List<Implementation> implList = AbstractEnvMap.getDependencyImplementations(impl);
		for (Implementation depImpl : implList) {
			String depLocalFile = ScaEcoreUtils.getFeature(depImpl, SpdPackage.Literals.IMPLEMENTATION__CODE, SpdPackage.Literals.CODE__LOCAL_FILE,
				SpdPackage.Literals.LOCAL_FILE__NAME);
			URI depSpdURI = depImpl.eResource().getURI();
			IFileStore fileStore;
			try {
				fileStore = EFS.getStore(new java.net.URI(depSpdURI.toString()));
			} catch (URISyntaxException e) {
				throw new CoreException(new Status(Status.ERROR, ScaDebugPlugin.ID, e.getMessage(), e));
			}
			File file = fileStore.toLocalFile(EFS.NONE, monitor);
			IPath depPath = new Path(file.getAbsolutePath()).removeLastSegments(1).append(depLocalFile);
			deps.add(depPath.toString());
		}

		return deps.toArray(new String[0]);
	}
}
