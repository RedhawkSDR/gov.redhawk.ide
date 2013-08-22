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

import BULKIO.PrecisionUTCTime;

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
	protected void postPushPacket(int samplesProcessed, PrecisionUTCTime time) {
		super.postPushPacket(samplesProcessed, time);
		if (Time.currentTime().diff(endTime) >= 0) {
			setProcessing(false);
		}
	}
	
	@Override
	protected void processSamples(IProgressMonitor monitor) {
		int work = (int) Math.ceil(timeInSeconds);
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Capturing Samples...", work);
		setProcessing(true);
		while (isProcessing() && !monitor.isCanceled()) {
			try {
				Thread.sleep(1000);
				subMonitor.worked(1);
			} catch (InterruptedException e) {
				// PASS
			}
		}
		subMonitor.done();
		
	}

}
