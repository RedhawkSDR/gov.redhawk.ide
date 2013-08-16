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
package gov.redhawk.ide.snapshot.datareceiver;

import gov.redhawk.bulkio.util.AbstractBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import BULKIO.StreamSRI;

public abstract class BaseDataReceiver extends AbstractBulkIOPort implements IDataReceiverAttributes {

	/** Data capture method */
	private IDataReceiver.CaptureMethod captureMethod;
	
	/** Number of samples to capture. */
	private long desiredSamples;
	/** Number of samples that have been captured so far. */
	private long currentSamples;
	
	/** Desired sample time duration (seconds) to capture. */
	private double desiredSampleTimeDuration;
	/** Sample time duration that has elapsed so far. */ 
	private double currentSampleTimeDuration;
	
	/** System clock time duration (seconds) to capture data. */  
	private double desiredClockTimeDuration;
	/** System clock time duration that has elapsed so far. */
	private double currentClockTimeDuration;
	
	/** BulkIO data type pushed by the port to capture from. */
	private BulkIOType dataType;
	/** SRI of the current file. */
	private StreamSRI currentSri = null;

	private boolean eos = false;;
	
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	/** start system clock time of data captured. */
	private Date startTime;
	/** current system clock time? */
	private Date currentTime; // 
	
	/** initial file to write captured data to. */
	private File initialFile;

	/** number of file(s) or set of file(s) (when have separate metadata file) that have been saved. */
	private int fileCount = 1;
	
	public BaseDataReceiver(BulkIOType type, File file, IDataReceiver.CaptureMethod method,
		long numSamples, double sampleDurationTime) throws IOException {
		super(type);
		this.initialFile = file;
	}
	
	/**
	 * This method determines the next file name to use based on the base file name
	 * stored in {@link #initialFile} an the counter {@link #fileCount}.
	 * @return a String of the path to the next file to write to
	 */
	protected String getNextFileName() {
		String filePath = initialFile.getAbsolutePath();
		String extension = getReceiverFilenameExtensions()[0];
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + fileCount + extension;
		return filePath;
	}
	
	@Override
	protected void handleStreamSRIChanged(StreamSRI oldSri, StreamSRI newSri) {
		if (this.currentSri != null) {
			this.currentSri = newSri; 
		}
	}
}
