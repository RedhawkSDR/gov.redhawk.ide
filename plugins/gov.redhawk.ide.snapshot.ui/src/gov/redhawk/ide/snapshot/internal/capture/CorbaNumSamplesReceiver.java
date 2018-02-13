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
package gov.redhawk.ide.snapshot.internal.capture;

import gov.redhawk.sca.util.SubMonitor;

import org.eclipse.core.runtime.IProgressMonitor;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

/**
 * 
 */
public class CorbaNumSamplesReceiver extends CorbaDataReceiver {

	private long samples;
	private long currentSamples;

	private SubMonitor subMonitor;

	public void setSamples(long samples) {
		this.samples = samples;
	}

	@Override
	protected void processSamples(IProgressMonitor monitor) {
		if (samples > Integer.MAX_VALUE) {
			subMonitor = SubMonitor.convert(monitor, "Capturing Samples...", IProgressMonitor.UNKNOWN);
		} else {
			subMonitor = SubMonitor.convert(monitor, "Capturing Samples...", (int) samples);
		}
		setProcessing(true);
		while (isProcessing() && !monitor.isCanceled()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// PASS
			}
		}
		subMonitor.done();
		subMonitor = null;
	}

	private void incrementCurrentSamples(int samplesProcessed) {
		if (this.subMonitor != null) {
			this.subMonitor.worked(samplesProcessed);
			this.currentSamples += samplesProcessed;
			if (this.currentSamples >= this.samples) {
				setProcessing(false);
			}
		}
	}

	@Override
	public int getSamplesToProcess(int length, PrecisionUTCTime time) {
		StreamSRI streamSri = getStreamSRI();
		if (streamSri != null) {
			int sampleSize = (streamSri.mode == 1) ? 2 : 1;
			int receivedSamples = length / sampleSize;
			int samplesToProcess = (int) Math.min(receivedSamples, this.samples - currentSamples);
			return samplesToProcess * sampleSize;
		} else {
			return 0;
		}
	}

	@Override
	protected void postPushPacket(int samplesProcessed, PrecisionUTCTime time) {
		super.postPushPacket(samplesProcessed, time);
		incrementCurrentSamples(samplesProcessed);
	}

}
