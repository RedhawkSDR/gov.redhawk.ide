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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nxm.sys.inc.Units;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public abstract class BaseDataReceiver extends AbstractBulkIOPort 
	implements IDataReceiverAttributes, IDataReceiver {

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
	
	/**the time that has elapsed in sample time*/
	private double currentTimeDuration;

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
	
	private IOException writeException;

	/** scalars (primitive data type, e.g. float, long, etc.). 2 if complex data (i.e. sri.mode == 1), otherwise 1. MUST NOT be zero. */
	private int scalarsPerAtom;
	/** frame size (min of 1). MUST NOT be zero. */
	private int atomsPerSample; // 
	private double sampleTimeDelta;
	
	private List<FilePair> outputFileList = new ArrayList<FilePair>(); 
	
	public BaseDataReceiver(BulkIOType type, File file, IDataReceiver.CaptureMethod method,
		long numberSamples, double durationTime) throws IOException {
		super(type);
		this.atomsPerSample = 1;
		this.scalarsPerAtom = 1;
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

	public IOException getWriteException() {
		return writeException;
	}

	/**
	 * @return the desiredSamples
	 */
	public long getDesiredSamples() {
		return desiredSamples;
	}

	/**
	 * @return the capturedSamples
	 */
	public long getCapturedSamples() {
		return capturedSamples;
	}

	public void setWriteException(IOException writeException) {
		this.writeException = writeException;
	}

	/**
	 * @return the scalarsPerAtom
	 */
	public int getScalarsPerAtom() {
		return scalarsPerAtom;
	}

	/**
	 * @return the atomsPerSample
	 */
	public int getAtomsPerSample() {
		return atomsPerSample;
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
		synchronized (this) {
			filePath = filePath.substring(0, filePath.lastIndexOf(".")) + fileCount + extension;
			fileCount++;
		}
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
	 *  NOTE: subclass MUST call this first thing in their pushPacket(..) method.
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
			long numArrayElementsLeftToProcess = (this.desiredSamples - this.capturedSamples) * atomsPerSample * scalarsPerAtom;
			numArrayElementsToProcess = Math.min(length, (int) numArrayElementsLeftToProcess);
		} else { // clock time, indefinite, etc.
			numArrayElementsToProcess = length;
		}
		
		return numArrayElementsToProcess;
	}
	
	/** update data capture statistics (should be called after a successful pushPacket(..)
	 * @param length number of primitive data types (scalars, e.g. float, int) captured 
	 */
	protected synchronized void updateCapturedSamples(long length) {
		long numSamples = length / this.atomsPerSample / this.scalarsPerAtom;
		this.capturedSamples += numSamples;
		this.elapsedSampleTimeDuration += numSamples * this.sampleTimeDelta; 
		notifyAll();
	}
	
	// ===== override super classe's methods =====

	/** handle when getting first SRI (oldSri == null), or SRI has changed. */
	@Override
	protected void handleStreamSRIChanged(StreamSRI oldSri, StreamSRI newSri) {
		if (this.sri != null) {
			try {
				String metadataFile = saveMetadata();
				String snapshotFile = startNewFile(this.getNextFileName(), newSri);
				outputFileList.add(new FilePair(snapshotFile, metadataFile));
			} catch (IOException ioe) {
				this.setWriteException(ioe);
				return;
			}
		}
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
		this.atomsPerSample = Math.max(1, this.sri.subsize);
	}
	
    /* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiver#getOutpuFileList()
	 */
	@Override
	@NonNull public List<FilePair> getOutpuFileList() {
		return Collections.unmodifiableList(this.outputFileList);
	}

	@Override
	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException {
		// TODO Auto-generated method stub
		// subclass before start of process/aquire data method
		this.startTime = new Date();
		doTaskBeforeAquireData(startTime);
		
		int work;
		if (captureMethod == CaptureMethod.NUMBER) {
			if (this.desiredSamples > Integer.MAX_VALUE) {
				work = IProgressMonitor.UNKNOWN;
			} else {
				work = (int) this.desiredSamples;
			}
		} else if (captureMethod == CaptureMethod.SAMPLE_TIME) {
			work = (int) this.desiredSampleTimeDuration;
		} else { // system/clock time based
			//work = IProgressMonitor.UNKNOWN;
			if (this.desiredClockTimeDuration > Integer.MAX_VALUE) {
				work = IProgressMonitor.UNKNOWN;
			} else {
				work = (int) (this.desiredClockTimeDuration + .5);
			}
		}
		monitor.beginTask("Acquiring samples...", work);
		try {
			long lastWorked = this.capturedSamples;
			double lastTime = this.currentTimeDuration;
			double deltaProgress;
			if (captureMethod == CaptureMethod.NUMBER) {
				deltaProgress = this.capturedSamples;
			} else {
				deltaProgress = this.currentTimeDuration;
			}
			double workedProgress = 0;
			while (this.capturedSamples < this.desiredSamples) {
				if (hasWriteException()) {
					throw getWriteException();
				}
				workedProgress += deltaProgress;
				int worked = (int) workedProgress;
				if (worked > 0) {
					monitor.worked(worked);
					workedProgress -= worked;
				}
				synchronized (this) {
					wait(500);
					if (monitor.isCanceled()) {
						//throw new CancellationException();
						this.eos = true;
						break;
					}
					if (captureMethod == CaptureMethod.NUMBER) {
						deltaProgress = this.capturedSamples - lastWorked;
						lastWorked = this.capturedSamples;
					} else {
						deltaProgress = this.currentTimeDuration - lastTime;
						lastTime = this.currentTimeDuration;
					}
					//this.time += deltaProgress;
				}
				if (this.eos) {
					break;
				}
			}
			// done with capture of 
			saveMetadata();
			
		} finally {
			
			doTaskAfterAquireData(); // subclass should override this method
			

			monitor.done();
		}

	}
	
	
	// ===== subclasses MUST override these methods =====
	
	/** subclasses can optinall override this */
	protected void doTaskAfterAquireData() {
	}

	protected abstract void doTaskBeforeAquireData(Date startTime);

	/**
     * @return metadata filename (null if none, i.e. a separate metadata file is not needed)
     * @throws IOException
     */
	@Nullable protected abstract String saveMetadata() throws IOException;
	
	/**
	 * capture future data packets into new file using specified file name.
	 * @param nextFileName
	 * @return previous snapshot filename (full path)
	 * @throws IOException
	 */
	@NonNull protected abstract String startNewFile(String nextFileName, StreamSRI sri) throws IOException;
}
