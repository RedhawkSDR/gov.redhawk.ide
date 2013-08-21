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

import BULKIO.StreamSRI;

/**
 * 
 */
public class CorbaSignalTimeReceiver extends CorbaNumSamplesReceiver {

	private double timeInSeconds;
	
	public void setTimeInSeconds(double timeInSeconds) {
		this.timeInSeconds = timeInSeconds;
	}
	
	public double getTimeInSeconds() {
		return timeInSeconds;
	}
	
	@Override
	protected void handleStreamSRIChanged(StreamSRI oldSri, StreamSRI newSri) {
		super.handleStreamSRIChanged(oldSri, newSri);
		long samples = (long) Math.ceil(timeInSeconds / newSri.xdelta);
		setSamples(samples);
	}

}
