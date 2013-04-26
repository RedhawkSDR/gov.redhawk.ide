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
package gov.redhawk.ide.debug;

import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.externaltools.internal.launchConfigurations.ProgramLaunchDelegate;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * 
 */
public class LocalComponentProgramLaunchDelegate extends ProgramLaunchDelegate {
	public static final String ID = "gov.redhawk.ide.debug.localComponentProgram";

	public static void setExternalToolsConfiguration(final ILaunchConfigurationWorkingCopy configuration, final String location, final String workingDirectory) {
		configuration.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		configuration.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		configuration.setAttribute(IExternalToolConstants.ATTR_BUILDER_SCOPE, "${none}");

		configuration.setAttribute(IExternalToolConstants.ATTR_LOCATION, location);
		configuration.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, workingDirectory);
	}

	public static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final String instID, final String implID,
	        Map<String, String> overrideMap, final URI spdUri) throws CoreException {
		if (instID != null) {
			if (overrideMap == null) {
				overrideMap = new HashMap<String, String>(Collections.singletonMap(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER, instID));
			} else {
				overrideMap.put(ScaDebugLaunchConstants.ARG_COMPONENT_IDENTIFIER, instID);
			}
		}
		return LocalComponentProgramLaunchDelegate.createLaunchConfiguration(name, implID, overrideMap, spdUri);
	}

	/**
	 * @since 1.2
	 */
	public static ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final String implID, final Map<String, String> overrideMap,
	        final URI spdUri) throws CoreException {
		Assert.isNotNull(name);
		Assert.isNotNull(implID);
		Assert.isNotNull(spdUri);
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName(name);
		final ILaunchConfigurationType configType = LocalComponentProgramLaunchDelegate.getLaunchConfigType();
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);
		retVal.setAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, implID);
		final File file = LocalComponentProgramLaunchDelegate.getFile(spdUri);
		if (file == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Could not resolve local file to launch for " + spdUri, null));
		}
		retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, file.getAbsolutePath());
		retVal.setAttribute(ScaDebugLaunchConstants.ATT_WORKSPACE_PROFILE, false);
		retVal.setAttribute(ScaDebugLaunchConstants.ATT_OVERRIDE_MAP, overrideMap);

		final SoftPkg spd = LocalComponentProgramLaunchDelegate.loadSpd(spdUri);
		final Implementation impl = spd.getImplementation(implID);
		if (impl == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "No implementation of ID: " + implID + " for spd file " + file, null));
		}
		Code code = impl.getCode();
		if (code == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "No Code entry for " + file + " and implementation " + implID, null));
		}
		CodeFileType type = code.getType();
		if (type != CodeFileType.EXECUTABLE) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Code not executable for " + file + " and implementation " + implID, null));
		}
		String entryPoint = code.getEntryPoint();
		if (entryPoint  == null) {
			throw new CoreException(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "No entry point for implementation " + implID + " in file " + file, null));
		}
		final String location = new File(file.getParent(), entryPoint).toString();
		final String wd = file.getParent();
		LocalComponentProgramLaunchDelegate.setExternalToolsConfiguration(retVal, location, wd);
		
		final Map<String, String> envVar = ScaEnvironmentUtil.getLauncherEnvMap(impl);

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, envVar);
		//		retVal.setAttribute(ILaunchManager.ATTR_PRIVATE, true);
		return retVal;
	}

	private static File getFile(final URI spdUri) throws CoreException {
		if (spdUri.isFile()) {
			return new File(spdUri.toFileString());
		} else if (spdUri.isPlatform()) {
			final URI uri = CommonPlugin.resolve(spdUri);
			final IFileStore store = EFS.getStore(java.net.URI.create(uri.toString()));
			return store.toLocalFile(0, null);
		} else {
			final IFileStore store = EFS.getStore(java.net.URI.create(spdUri.toString()));
			return store.toLocalFile(0, null);
		}
	}

	private static ILaunchConfigurationType getLaunchConfigType() {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurationType(LocalComponentProgramLaunchDelegate.ID);
	}

	private static SoftPkg loadSpd(final URI spd) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		return (SoftPkg) resourceSet.getEObject(spd.appendFragment(SoftPkg.EOBJECT_PATH), true);
	}

	@Override
	public void launch(final ILaunchConfiguration configuration, final String mode, final ILaunch launch, final IProgressMonitor monitor) throws CoreException {
		final ILaunchConfigurationWorkingCopy workingCopy = configuration.getWorkingCopy();
		final URI spdURI;
		if (configuration.getAttribute(ScaDebugLaunchConstants.ATT_WORKSPACE_PROFILE, ScaDebugLaunchConstants.DEFAULT_ATT_WORKSPACE_PROFILE)) {
			spdURI = URI.createPlatformResourceURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""), true);
		} else {
			spdURI = URI.createFileURI(configuration.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, ""));
		}

		insertProgramArguments(spdURI, launch, workingCopy);
		super.launch(workingCopy, mode, launch, monitor);
		ScaLauncherUtil.postLaunch(launch);
	}

	protected void insertProgramArguments(final URI spdURI, final ILaunch launch, final ILaunchConfigurationWorkingCopy configuration) throws CoreException {
		final String args = configuration.getAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "");
		final String scaArgs = ScaLauncherUtil.getSpdProgramArguments(spdURI, launch, configuration);
		configuration.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, args + " " + scaArgs);
	}

}
