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
package gov.redhawk.ide.dcd.generator.newdevice;

import gov.redhawk.ide.codegen.args.GeneratorArgsBase;

/**
 * The properties that can be set for the New Device generator.
 */
public class GeneratorArgs extends GeneratorArgsBase {
	private String deviceType;
	private boolean aggregateDevice;
	
	// TODO: Clean this up some.  It should really extend the GeneratorArgs used by the new component and then add to it the device
	// specifics.
	
	public static final String SOFTPKG_NAME_KEY = "softpkg_name";
	public static final String SOFTPKG_ID_KEY = "softpkg_id";
	public static final String SOFTPKG_FILE_KEY = "softpkg_file";
	
	public void setSoftPkgName(final String softPkgName) {
		this.setProperty(SOFTPKG_NAME_KEY, softPkgName);
	}
	
	public String getSoftPkgName() {
		return this.getProperty(SOFTPKG_NAME_KEY);
	}

	public void setSoftPkgFile(final String softPkgFile) {
		this.setProperty(SOFTPKG_FILE_KEY, softPkgFile);
	}

	public String getSoftPkgFile() {
		return this.getProperty(SOFTPKG_FILE_KEY);
	}
	
	@Override
	public void setSoftPkgId(final String softPkgFile) {
		this.setProperty(SOFTPKG_ID_KEY, softPkgFile);
	}

	@Override
	public String getSoftPkgId() {
		return this.getProperty(SOFTPKG_ID_KEY);
	}

	public String getDeviceType() {
    	return deviceType;
    }

	public void setDeviceType(String deviceType) {
    	this.deviceType = deviceType;
    }

	public boolean isAggregateDevice() {
    	return aggregateDevice;
    }

	public void setAggregateDevice(boolean aggregateDevice) {
    	this.aggregateDevice = aggregateDevice;
    }
}
