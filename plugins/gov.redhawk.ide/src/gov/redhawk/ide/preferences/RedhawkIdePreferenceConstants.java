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
package gov.redhawk.ide.preferences;

// TODO: Auto-generated Javadoc
/**
 * The Class RedhawkIdePreferenceConstants.
 */
public final class RedhawkIdePreferenceConstants {

	/** The Constant RH_IDE_RUNTIME_PATH_PREFERENCE. */
	public static final String RH_IDE_RUNTIME_PATH_PREFERENCE = "RuntimePath";

	/** The Constant RH_IDE_IDL_INCLUDE_PATH_PREFERENCE. */
	public static final String RH_IDE_IDL_INCLUDE_PATH_PREFERENCE = "IdlIncludePath";

	/** The Constant RH_IDE_IDL_INCLUDE_PATH_PREFERENCE. 
	 * @since 3.0*/
	public static final String RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER = ";";

	/** The Constant DEVICE. */
	public static final String DEVICE = "Device";

	/** The Constant AGGREGATE_DEVICE. */
	public static final String AGGREGATE_DEVICE = "Aggregate";

	/** The Constant LOADABLE_DEVICE. */
	public static final String LOADABLE_DEVICE = "Loadable";

	/** The Constant EXECUTABLE_DEVICE. */
	public static final String EXECUTABLE_DEVICE = "Executable";

	/** The Constant DEVICE_TYPES. */
	public static final String[] DEVICE_TYPES = { RedhawkIdePreferenceConstants.DEVICE, RedhawkIdePreferenceConstants.LOADABLE_DEVICE,
	        RedhawkIdePreferenceConstants.EXECUTABLE_DEVICE };

	/**
	 * Hidden constructor.
	 */
	private RedhawkIdePreferenceConstants() {
	}
}
