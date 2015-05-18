/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot.diagram;

/**
 * Used to specify values to complete the FrontEnd tuner wizard.
 */
public class FETunerControl {

	private String tunerType;
	private String newAllocationID;
	private String centerFrequency;
	private String bandwidth;
	private String sampleRate;
	private boolean deviceControl;
	private String rfFlowID;
	private String groupID;

	public FETunerControl(String tunerType, String newAllocationID, String centerFrequency, String bandwidth, String sampleRate, boolean deviceControl,
		String rfFlowID, String groupID) {
		this.tunerType = tunerType;
		this.newAllocationID = newAllocationID;
		this.centerFrequency = centerFrequency;
		this.bandwidth = bandwidth;
		this.sampleRate = sampleRate;
		this.deviceControl = deviceControl;
		this.rfFlowID = rfFlowID;
		this.groupID = groupID;
	}

	/**
	 * @see gov.redhawk.frontend.ui.FrontEndUIActivator#SUPPORTED_TUNER_TYPES
	 */
	public String getTunerType() {
		return tunerType;
	}

	public String getNewAllocationID() {
		return newAllocationID;
	}

	public String getCenterFrequency() {
		return centerFrequency;
	}

	public String getBandwidth() {
		return bandwidth;
	}

	public String getSampleRate() {
		return sampleRate;
	}

	public boolean getDeviceControl() {
		return deviceControl;
	}

	public String getRFFlowID() {
		return rfFlowID;
	}

	public String getGroupID() {
		return groupID;
	}

}
