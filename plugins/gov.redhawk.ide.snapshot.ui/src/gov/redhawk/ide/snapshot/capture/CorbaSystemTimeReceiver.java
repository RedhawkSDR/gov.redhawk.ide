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
package gov.redhawk.ide.snapshot.capture;

import gov.redhawk.sca.util.SubMonitor;

import java.io.IOException;

import nxm.sys.lib.Time;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 
 */
public class CorbaSystemTimeReceiver extends CorbaDataReceiver {

	private Time startTime;
	private double timeInSeconds;
	private Time endTime;
	
	public void setTimeInSeconds(double timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}
	
	public double getTimeInSeconds() {
		return timeInSeconds;
	}
	
	@Override
	protected void open(SubMonitor child) throws IOException {
		super.open(child);
		startTime = Time.currentTime();
		endTime = startTime.addSec(this.timeInSeconds);
	}
	
	@Override
	protected void processSamples(IProgressMonitor monitor) {
		super.processSamples(monitor);
		if (Time.currentTime().diff(endTime) >= 0) {
			setProcessing(false);
		}
	}

}
