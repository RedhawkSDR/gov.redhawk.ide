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
