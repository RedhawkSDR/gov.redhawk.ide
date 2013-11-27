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
package gov.redhawk.ide.sdr.ui.util;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * @since 3.3
 * 
 */
public class DeviceManagerLaunchConfiguration {
	
	public static final String PROP_DOMAIN_NAME = "domainName";
	public static final String PROP_DCD = "dcd";
	public static final String PROP_DEBUG_LEVEL = "debugLevel";
	public static final String PROP_ARGUMENTS = "additionalArguments";

	private String domainName;
	private DeviceConfiguration dcd;
	private DebugLevel debugLevel = DebugLevel.Info;
	private String additionalArguments;

	public DeviceManagerLaunchConfiguration() {

	}

	/**
	 * 
	 * @param domainName
	 * @param dcd
	 * @param debugLevel
	 * @param additionalArguments
	 * @deprecated Use {@link #DeviceManagerLaunchConfiguration(String, DeviceConfiguration, DebugLevel, String)}
	 */
	@Deprecated
	public DeviceManagerLaunchConfiguration(String domainName, DeviceConfiguration dcd, Integer debugLevel, String additionalArguments) {
		super();
		this.domainName = domainName;
		this.dcd = dcd;
		if (debugLevel != null) {
			setDebugLevel(DebugLevel.values()[debugLevel]);
		}
		this.additionalArguments = additionalArguments;
	}

	public DeviceManagerLaunchConfiguration(String domainName, DeviceConfiguration dcd, DebugLevel debugLevel, String additionalArguments) {
		super();
		this.domainName = domainName;
		this.dcd = dcd;
		setDebugLevel(debugLevel);
		this.additionalArguments = additionalArguments;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public DeviceConfiguration getDcd() {
		return dcd;
	}

	public void setDcd(DeviceConfiguration dcd) {
		this.dcd = dcd;
	}

	public DebugLevel getDebugLevel() {
		return debugLevel;
	}

	public void setDebugLevel(DebugLevel debugLevel) {
		if (debugLevel == null) {
			debugLevel = DebugLevel.Info;
		}
		this.debugLevel = debugLevel;
	}

	public String getAdditionalArguments() {
		return additionalArguments;
	}

	public void setAdditionalArguments(String additionalArguments) {
		this.additionalArguments = additionalArguments;
	}

}
