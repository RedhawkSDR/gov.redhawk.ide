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
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver.CaptureMethod;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import nxm.sys.inc.Units;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public abstract class BaseDataReceiver extends AbstractBulkIOPort implements IDataReceiverAttributes {

	/** Data capture method */
	private IDataReceiver.CaptureMethod captureMethod;
	
	/** Number of samples to capture. */
	private long desiredSamples;
	/** Number of samples that have been captured so far. */
	private long capturedSamples;
	
	/** Desired sample time duration (seconds) to capture. */
	private double desiredSampleTimeDuration;
	/** Sample time duration that has elapsed so far. */ 
	private double elapsedSampleTimeDuration;
	
	/** System clock time duration (seconds) to capture data. */  
	private double desiredClockTimeDuration;
	/** System clock time duration that has elapsed so far. */
	private double elapsedClockTimeDuration;
	
	/** BulkIO data type that we are capturing. */
	private BulkIOType dataType;
	/** SRI of the current data stream / file. */
	private StreamSRI sri = null;

	/** signals end of stream. */
	private boolean eos = false;;
	
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	/** start system clock time of data capture. */
	private Date startTime;
	/** current system clock time? */
	private Date currentTime; // 
	
	/** initial file to write captured data to. */
	private File initialFile;

	/** number of file(s) or set of file(s) (when have separate metadata file) that have been saved. */
	private int fileCount = 1;
	
	private Exception writeException;

	/** scalars (primitive data type, e.g. float, long, etc.). 2 if complex data (i.e. sri.mode == 1), otherwise 1. */
	private int scalarsPerAtom;
	private int atomsPerSample;
	private double sampleTimeDelta;

	
	public BaseDataReceiver(BulkIOType type, File file, IDataReceiver.CaptureMethod method,
		long numberSamples, double durationTime) throws IOException {
		super(type);
		this.initialFile = file;
		switch (method) {
		case NUMBER:
			this.desiredSamples = numberSamples;
			break;
		case SAMPLE_TIME:
			this.desiredSampleTimeDuration = durationTime;
			break;
		case CLOCK_TIME:
			this.desiredClockTimeDuration = durationTime;
			break;
		case INDEFINITELY:
			this.desiredClockTimeDuration = Double.POSITIVE_INFINITY; // until user cancels/stops capture
			break;
		default:
			throw new IllegalArgumentException("Unsupported capture type: " + method);
		}
	}
	
	// ===== getter/setter methods for fields =====
	
	public Exception getWriteException() {
		return writeException;
	}

	public void setWriteException(Exception writeException) {
		this.writeException = writeException;
	}

	protected boolean hasWriteException() {
		return (writeException != null);
	}
	
	// ===== utility methods for subclasses to use =====
	
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
	
	private final boolean shouldProcessPacket(int length, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!super.pushPacket(length, time, eos, streamID)  // 1. super class said not to process packet
			|| this.eos                                     // 2. we were told previously that the stream ended
			|| hasWriteException()                          // 3. there was a exception writing to file
			|| this.sri == null) {                          // 4. have not received SRI
			return false;                                   // ignore data packet
		}
		if (eos) {
			this.eos = true;
		}
		return true;
	}
	
	/** get number of primitive (double, float, long, byte,etc.) data elements to capture.
	 * @param length   incoming data array (packet) length
	 * @param time     precision time of data 
	 * @param eos      end of stream
	 * @param streamID stream id of this data packet
	 * @return number of elements in data array to process/acquire/capture. data packet should be ignored if <= 0 is returned.
	 */
	protected final int numberOfDataToProcess(int length, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!shouldProcessPacket(length, time, eos, streamID)) {
			return -1;
		}
		
		final int  numArrayElementsToProcess;
		
		if (captureMethod == CaptureMethod.NUMBER || captureMethod == CaptureMethod.SAMPLE_TIME) {
//		case NUMBER:      // fall-through
//		case SAMPLE_TIME: 
			long numArrayElementsLeftToProcess = (this.desiredSamples - this.capturedSamples) * atomsPerSample * scalarsPerAtom;
			numArrayElementsToProcess = Math.min(length, (int) numArrayElementsLeftToProcess);
//			this.desiredSampleTimeDuration = durationTime;
		} else { // clock time, indefinite, etc.
			numArrayElementsToProcess = length;
		}
		
		return numArrayElementsToProcess;
	}
	
	/** update data capture statistics 
	 * @param length number of primitive data types (scalars, e.g. float, int) captured 
	 */
	protected synchronized void updateCapturedSamples(long length) {
		long numSamples = length / this.atomsPerSample / this.scalarsPerAtom;
		this.capturedSamples += numSamples;
		this.elapsedSampleTimeDuration += numSamples * this.sampleTimeDelta; 
		notifyAll();
	}
	
	// ===== override super classe's methods =====
	
	@Override
	protected void handleStreamSRIChanged(StreamSRI oldSri, StreamSRI newSri) {
		if (this.sri != null) {
			// TODO: save previous SRI, spawn new file, etc.
			
			this.sri = newSri; 
			this.scalarsPerAtom = (this.sri.mode == 1) ? 2 : 1; // 2=complex, otherwise 1=scalar
			if (this.sri.subsize > 0) {                     // have two dimensional data (framed)
				if (this.sri.yunits == Units.TIME_S) {      // if yunits is time
					this.sampleTimeDelta = this.sri.ydelta; //   use ydelta for time delta
				} else {
					this.sampleTimeDelta = 1;               // else unknown units, fallback to 1 sec 
				}
			} else {                                        // have one dimensional data
				if (this.sri.xunits == Units.TIME_S) {      // if xunits is time
					this.sampleTimeDelta = this.sri.xunits; //   use xdelta for time delta 
				} else {
					this.sampleTimeDelta = 1;               // else unknown units, fallback to 1 sec 
				}
			}
			if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
				long numSamplesLeft = (long) ((this.desiredSampleTimeDuration - this.elapsedSampleTimeDuration) + 0.5); // round up
				this.desiredSamples = this.capturedSamples + numSamplesLeft;
			}
			this.atomsPerSample = Math.min(1, this.sri.subsize);
		}
	}
	
}
