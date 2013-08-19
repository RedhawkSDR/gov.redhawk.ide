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
package gov.redhawk.ide.snapshot.datareceiver.bin;

import gov.redhawk.bulkio.util.BulkIOType;
//import nxm.sys.inc.Units;
//import nxm.sys.lib.*;




import java.io.File;
import java.io.IOException;
//import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
//import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.concurrent.CancellationException;

//import nxm.redhawk.prim.corbareceiver;

//import mil.jpeojtrs.sca.util.AnyUtils;


import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;

//import org.eclipse.core.runtime.IProgressMonitor;

//import org.eclipse.emf.common.util.URI;
//import org.eclipse.emf.ecore.resource.Resource;
//import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;


import org.eclipse.jdt.annotation.NonNull;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataCharOperations;
import BULKIO.dataDoubleOperations;
import BULKIO.dataFloatOperations;
import BULKIO.dataLongLongOperations;
import BULKIO.dataLongOperations;
import BULKIO.dataOctetOperations;
import BULKIO.dataShortOperations;
import BULKIO.dataUlongLongOperations;
import BULKIO.dataUlongOperations;
import BULKIO.dataUshortOperations;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
/*import CF.DataType;
import CF.DataTypeHelper;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value;*/
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;

//TODO: DELETE ME
@Deprecated
public class DataReceiver extends SuperBinReceiver implements dataDoubleOperations, dataFloatOperations, dataLongLongOperations, dataLongOperations,
		dataShortOperations, dataCharOperations, dataOctetOperations, dataUlongLongOperations, dataUlongOperations, dataUshortOperations, IDataReceiver {

	/**The total number of samples to capture*/
	private long totalNumSamples;
	/**the total time for capturing samples*/
	private final double totalTime;
	/**the time that has elapsed in sample time*/
	private double currentTimeDuration;
	/**the number of samples that have been captured so far*/
	private double currentSampleDelta;
	/**the Selected capture method, only supports NUMBER and SAMPLE_TIME*/
	private CaptureMethod captureMethod;
	private boolean eos = false;;
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	public DataReceiver(File file, long numSamples, double sampleDurationTime, BulkIOType type, CaptureMethod method) throws IOException {
		super(file, type);
		this.currentSampleDelta = 1;
		switch (method) {
		case NUMBER:
			this.totalNumSamples = numSamples;
			this.totalTime = numSamples * this.currentSampleDelta;
			break;
		case SAMPLE_TIME:
			this.totalTime = sampleDurationTime;
			this.totalNumSamples = (long) (this.totalTime / this.currentSampleDelta + .5);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Capture Type");
		}
		this.currentTimeDuration = 0;
		this.captureMethod = method;

	}

	@Override
	public void saveXML() throws IOException {
		if (this.totalNumSamples < super.getCurrentSamples()) {
			super.getMetaDataModel().setNumberOfSamples(this.totalNumSamples - this.startSample);
			this.startSample = this.totalNumSamples;
		} else {
			super.getMetaDataModel().setNumberOfSamples(super.getCurrentSamples() - this.startSample);
			this.startSample = super.getCurrentSamples();
		}

		super.getMetaDataModel().getTime().setEndTime(new SimpleDateFormat(super.getTimeFormat()).format(new Date()));
		super.saveXML();
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (totalNumSamples > super.getCurrentSamples() && !this.eos) {
			if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
				this.currentSampleDelta = (sri.xdelta != 0) ? sri.xdelta : 1;
				totalNumSamples = ((long) (((this.totalTime - this.currentTimeDuration) / currentSampleDelta) + .5)) + super.getCurrentSamples();
			}
			super.pushSRI(sri);
		}

	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		DoubleBuffer tBuff = bBuffer.asDoubleBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);

	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		CharBuffer tBuff = bBuffer.asCharBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		bBuffer.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);

	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
// ByteBuffer byteBuf = ByteBuffer.wrap(new byte[0]); // API
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		ShortBuffer tBuff = bBuffer.asShortBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		IntBuffer tBuff = bBuffer.asIntBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		LongBuffer tBuff = bBuffer.asLongBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (!super.pushPacket(data.length, time, eos, streamID)) {
			return;
		}
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		if (getSRI() == null) {
			return; // ignore data until we get the first SRI
		}
		final long numDataLeftToProcess;
		// = this.totalNumSamples - super.getCurrentSamples();
		if (getSRI().mode == 1) { // complex data
			numDataLeftToProcess = (this.totalNumSamples - super.getCurrentSamples()) * 2;
		} else {
			numDataLeftToProcess = this.totalNumSamples - super.getCurrentSamples();
		}
		
		int numDataElementsToProcess = Math.min(data.length, (int) numDataLeftToProcess);
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * numDataElementsToProcess);
		FloatBuffer tBuff = bBuffer.asFloatBuffer();
		tBuff.put(data, 0, numDataElementsToProcess);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(numDataElementsToProcess));
		this.incrementTime(super.deriveNumberOfSamples(numDataElementsToProcess) * this.currentSampleDelta);
	}

	private synchronized void incrementTime(double time) {
		this.currentTimeDuration += time;
	}

	@Override
	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException {
		super.getMetaDataModel().getTime().setStartTime(new SimpleDateFormat(super.getTimeFormat()).format(new Date()));
		int work;
		if (captureMethod == CaptureMethod.NUMBER) {
			if (totalNumSamples > Integer.MAX_VALUE) {
				work = IProgressMonitor.UNKNOWN;
			} else {
				work = (int) totalNumSamples;
			}
		} else {
			//work = IProgressMonitor.UNKNOWN;
			if (this.totalTime > Integer.MAX_VALUE) {
				work = IProgressMonitor.UNKNOWN;
			} else {
				work = (int) (this.totalTime + .5);
			}
		}
		monitor.beginTask("Acquiring samples...", work);
		try {
			long lastWorked = super.getCurrentSamples();
			double lastTime = this.currentTimeDuration;
			double deltaProgress;
			if (captureMethod == CaptureMethod.NUMBER) {
				deltaProgress = super.getCurrentSamples();
			} else {
				deltaProgress = this.currentTimeDuration;
			}
			double workedProgress = 0;
			while (totalNumSamples > super.getCurrentSamples()) {
				if (super.getWriteException() != null) {
					throw super.getWriteException();
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
						deltaProgress = super.getCurrentSamples() - lastWorked;
						lastWorked = super.getCurrentSamples();
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
			this.saveXML();
		} finally {
			// truncate file to actual size for case when we are overwriting an existing file
			try {
				long fileSize = getCurrentSamples() * getType().getBytePerAtom();
				StreamSRI sri = getSRI();
				if (sri != null) {
					if (sri.mode == 1) {         // complex data
						fileSize *= 2;           // file size should be double
					}
					if (sri.subsize > 1) {       // framed data
						fileSize *= sri.subsize; // file size should be multiplied by subsize ? 
					}
				}
				getChannel().truncate(fileSize);
			} catch (IOException e) {
				// PASS
			}
			monitor.done();
		}
	}

	//TODO add function to process samples indefinitely

	public void writeFile(Object[] data, StreamSRI sri) throws IOException {
		super.getMetaDataModel().getTime().setStartTime(new SimpleDateFormat(super.getTimeFormat()).format(new Date()));
		this.pushSRI(sri);
		String[] typeMismatch = { " does not correspond the port type ", ", it does correspond to the port type " };
		try {
			if (data instanceof Double[]) {
				if (super.getType() == BulkIOType.DOUBLE) {
					this.pushPacket(ArrayUtils.toPrimitive((Double[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Double array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.DOUBLE.name());
				}
			} else if (data instanceof Float[]) {
				if (super.getType() == BulkIOType.FLOAT) {
					this.pushPacket(ArrayUtils.toPrimitive((Float[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Float array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.FLOAT.name());
				}
			} else if (data instanceof Character[]) {
				if (super.getType() == BulkIOType.CHAR) {
					this.pushPacket(ArrayUtils.toPrimitive((Character[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Character array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.CHAR.name());
				}
			} else if (data instanceof Byte[]) {
				if (super.getType() == BulkIOType.OCTET) {
					this.pushPacket(ArrayUtils.toPrimitive((Byte[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Byte array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.OCTET.name());
				}
			} else if (data instanceof Short[]) {
				if (super.getType() == BulkIOType.SHORT || super.getType() == BulkIOType.USHORT) {
					this.pushPacket(ArrayUtils.toPrimitive((Short[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Short array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.SHORT.name() + " or "
						+ BulkIOType.USHORT.name());
				}
			} else if (data instanceof Integer[]) {
				if (super.getType() == BulkIOType.LONG || super.getType() == BulkIOType.ULONG) {
					this.pushPacket(ArrayUtils.toPrimitive((Integer[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("An Integer array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.LONG.name()
						+ " or " + BulkIOType.ULONG.name());
				}
			} else if (data instanceof Long[]) {
				if (super.getType() == BulkIOType.LONG_LONG || super.getType() == BulkIOType.ULONG_LONG) {
					this.pushPacket(ArrayUtils.toPrimitive((Long[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Long array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.LONG_LONG.name()
						+ " or " + BulkIOType.ULONG_LONG.name());
				}
			} else {
				throw new IllegalArgumentException("Data type not supported by this receiver");
			}
			this.saveXML();
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				super.getChannel().truncate(super.getCurrentSamples() * super.getType().getBytePerAtom());
			} catch (IOException e) {
				// PASS
			}
		}

	}

	@Override
	@NonNull
	public List<FilePair> getOutpuFileList() {
		// TODO Auto-generated method stub
		return null;
	}
}
