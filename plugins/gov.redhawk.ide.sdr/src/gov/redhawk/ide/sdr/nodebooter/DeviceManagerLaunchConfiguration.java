/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.sdr.nodebooter;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

public class DeviceManagerLaunchConfiguration {

	private String domainName;
	private DeviceConfiguration dcd;
	private DebugLevel debugLevel;
	private String additionalArguments;
	private String launchConfigName;

	public DeviceManagerLaunchConfiguration() {
	}

	public DeviceManagerLaunchConfiguration(String domainName, DeviceConfiguration dcd, DebugLevel debugLevel, String additionalArguments, String launchConfigName) {
		super();
		this.domainName = domainName;
		this.dcd = dcd;
		setDebugLevel(debugLevel);
		this.additionalArguments = additionalArguments;
		this.launchConfigName = launchConfigName;
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

	public String getLaunchConfigName() {
		return launchConfigName;
	}

	public void setLaunchConfigName(String launchConfigName) {
		this.launchConfigName = launchConfigName;
	}

}
