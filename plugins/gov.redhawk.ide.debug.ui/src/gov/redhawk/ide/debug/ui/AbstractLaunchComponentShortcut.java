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

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.sad.util.SadResourceImpl;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public abstract class AbstractLaunchComponentShortcut implements ILaunchShortcut {

	protected ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final SoftPkg spd, final Implementation impl) throws CoreException {
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName(name);
		final ILaunchConfigurationType configType = getLaunchConfigType();
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);

		retVal.setAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, impl.getId());
		retVal.setAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, getProfile(spd));

		// Setup Environment variables for override locations of OSSIEHOME and SDRROOT
		final Map<String, String> envVar = ScaEnvironmentUtil.getLauncherEnvMap(impl);

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, envVar);

		return retVal;
	}

	protected String getProfile(final SoftPkg spd) {
		return spd.eResource().getURI().path();
	}

	protected ILaunchConfiguration[] findLaunchConfigurations(final SoftPkg spd, final String implID) throws CoreException {
		final ILaunchConfigurationType configType = getLaunchConfigType();
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfiguration[] launchers = launchManager.getLaunchConfigurations(configType);
		final List<ILaunchConfiguration> retVal = new ArrayList<ILaunchConfiguration>(1);
		for (final ILaunchConfiguration config : launchers) {
			if (config.getAttribute(ScaLaunchConfigurationConstants.ATT_PROFILE, "").equals(getProfile(spd))
			        && config.getAttribute(ScaDebugLaunchConstants.ATT_IMPL_ID, "").equals(implID)) {
				retVal.add(config);
			}
		}
		if (retVal.isEmpty()) {
			return null;
		}
		return retVal.toArray(new ILaunchConfiguration[retVal.size()]);
	}

	/**
	 * Get the type of Launch configuration
	 * @return
	 */
	protected abstract ILaunchConfigurationType getLaunchConfigType();

	protected void launch(final SoftPkg spd, final String mode) throws CoreException {
		final Implementation impl = getImplementation(spd, mode);
		final ILaunchConfiguration[] configs = findLaunchConfigurations(spd, impl.getId());
		if (configs != null && configs.length > 0) {
			if (configs.length > 1) {
				final ILaunchConfiguration config = LaunchUtil.chooseConfiguration(mode, configs, getShell());
				if (config != null) {
					launch(config, mode);
				}
			} else {
				launch(configs[0], mode);
			}
		} else {
			final ILaunchConfigurationWorkingCopy config = createLaunchConfiguration(getName(spd, impl), spd, impl);
			launch(config.doSave(), mode);
		}
	}

	protected String getName(final SoftPkg spd, final Implementation impl) {
		return spd.getName();
	}

	protected void launch(final ILaunchConfiguration config, final String mode) throws CoreException {
		DebugUITools.launch(config, mode);

	}

	protected Shell getShell() {
		return PlatformUI.getWorkbench().getDisplay().getActiveShell();
	}

	protected Implementation getImplementation(final SoftPkg spd, final String mode) {

		return LaunchUtil.chooseImplementation(getSupportedImplementations(spd, mode), mode, getShell());
	}

	protected Collection<Implementation> getSupportedImplementations(final SoftPkg spd, final String mode) {
		return spd.getImplementation();
	}

	public void launch(final IEditorPart editor, final String mode) {
		if (editor instanceof SCAFormEditor) {
			final SCAFormEditor formEditor = (SCAFormEditor) editor;
			if (formEditor.getMainResource() instanceof SadResourceImpl) {
				final SoftPkg spd = SoftPkg.Util.getSoftPkg(formEditor.getMainResource());
				try {
					launch(spd, mode);
				} catch (final CoreException e) {
					final Status status = new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e.getStatus().getMessage(), e.getStatus().getException());
					StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
				}
			}
		}
	}

}
