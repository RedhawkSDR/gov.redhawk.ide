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

import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ScaDebugLaunchConstants extends ScaLaunchConfigurationConstants { // SUPPRESS CHECKSTYLE Ignore

	public static final String ATT_LW_IMPLS = ScaDebugPlugin.ID + ".localWaveform.impls";
	public static final String ATT_OVERRIDE_MAP = ScaDebugPlugin.ID + ".localWaveform.override";
	public static final String ATT_IMPL_ID = ScaDebugPlugin.ID + ".implementationID";
	public static final String ATT_WORKSPACE_PROFILE = ScaDebugPlugin.ID + ".workspaceProfile";
	public static final String ATT_DEBUG_LEVEL = ScaDebugPlugin.ID + ".debugLevel";
	/**
	 * @since 1.2
	 */
	public static final String ATT_LAUNCH_TIMEOUT = ScaDebugPlugin.ID + ".launchTimeout";
	public static final boolean DEFAULT_ATT_WORKSPACE_PROFILE = true;
	/**
	 * @since 1.2
	 */
	public static final int DEFAULT_ATT_LAUNCH_TIMEOUT = 5;

	public static final String ARG_NAMING_CONTEXT_IOR = "NAMING_CONTEXT_IOR";
	public static final String ARG_NAME_BINDING = "NAME_BINDING";
	public static final String ARG_COMPONENT_IDENTIFIER = "COMPONENT_IDENTIFIER";
	public static final String ARG_EXEC_PARAMS = "EXEC_PARAMS";

	public static final String ARG_DEVICE_MGR_IOR = "DEVICE_MGR_IOR";
	public static final String ARG_PROFILE_NAME = "PROFILE_NAME";
	public static final String ARG_DEVICE_ID = "DEVICE_ID";
	public static final String ARG_DEVICE_LABEL = "DEVICE_LABEL";

	public static final String ARG_SERVICE_NAME = "SERVICE_NAME";

	public static final String ID_LOCAL_WAVEFORM_LAUNCH = "gov.redhawk.ide.debug.launchLocalWaveform";

}
