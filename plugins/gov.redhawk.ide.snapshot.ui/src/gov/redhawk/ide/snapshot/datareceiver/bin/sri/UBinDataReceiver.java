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
package gov.redhawk.ide.snapshot.datareceiver.bin.sri;

import gov.redhawk.bulkio.util.BulkIOType;
//import nxm.sys.inc.Units;
//import nxm.sys.lib.*;




import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.util.List;

import mil.jpeojtrs.sca.util.UnsignedUtils;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataOctetOperations;
import BULKIO.dataUlongLongOperations;
import BULKIO.dataUlongOperations;
import BULKIO.dataUshortOperations;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;

//TODO: DELETE ME
@Deprecated
public class UBinDataReceiver extends SuperBinSriReceiver implements dataOctetOperations, dataUlongLongOperations, dataUlongOperations, dataUshortOperations,
		IDataReceiver {

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
	private boolean eos = false;
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	public UBinDataReceiver(File file, long samples, double time, BulkIOType type, CaptureMethod method) throws IOException {
		super(file, type);
		this.currentSampleDelta = 1;
		switch (method) {
		case NUMBER:
			this.totalNumSamples = samples;
			this.totalTime = samples * this.currentSampleDelta;
			break;
		case SAMPLE_TIME:
			this.totalTime = time;
			this.totalNumSamples = (long) (this.totalTime / this.currentSampleDelta + .5);
			break;
		default:
			throw new IllegalArgumentException("Unsupported Capture Type");
		}
		this.currentTimeDuration = 0;
		this.captureMethod = method;
	}

	@Override
	public void saveSRI() throws IOException {
		if (this.totalNumSamples < super.getCurrentSamples()) {
			super.setPrintedSamples(this.totalNumSamples - this.startSample);
			this.startSample = this.totalNumSamples;
		} else {
			super.setPrintedSamples(super.getCurrentSamples() - this.startSample);
			this.startSample = super.getCurrentSamples();
		}

		super.saveSRI();
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
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (this.totalNumSamples <= super.getCurrentSamples() || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		short[] octet = UnsignedUtils.toSigned(data);
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.SHORT.getBytePerAtom() * length);
		ShortBuffer tBuff = bBuffer.asShortBuffer();
		tBuff.put(octet, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.setTimestamp(time);
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
		int[] unsignedShort = UnsignedUtils.toSigned(data);
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.LONG.getBytePerAtom() * length);
		IntBuffer tBuff = bBuffer.asIntBuffer();
		tBuff.put(unsignedShort, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.setTimestamp(time);
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
		long[] unsignedLong = UnsignedUtils.toSigned(data);
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.LONG_LONG.getBytePerAtom() * length);
		LongBuffer tBuff = bBuffer.asLongBuffer();
		tBuff.put(unsignedLong, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.setTimestamp(time);
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
		long[] unsignedLongLong = new long[data.length];
		for (int i = 0; i < unsignedLongLong.length; i++) {
			if (data[i] < 0) {
				unsignedLongLong[i] = Long.MAX_VALUE;
			} else {
				unsignedLongLong[i] = data[i];
			}
		}
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		LongBuffer tBuff = bBuffer.asLongBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.setTimestamp(time);
		super.incrementSamples(super.deriveNumberOfSamples(length));
		this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
	}

	// increament increment asdfa
	private synchronized void incrementTime(double time) {
		this.currentTimeDuration += time;
	}

	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException {
		int work;
		super.setStartTime();
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
			this.saveSRI();
		} finally {
			// truncate for case when we are overwriting an existing file
			try {
				super.getChannel().truncate(super.getCurrentSamples() * super.getType().getBytePerAtom());
			} catch (IOException e) {
				// PASS
			}
			monitor.done();
		}
	}

	public void writeFile(Object[] data, StreamSRI sri) throws IOException {
		super.setStartTime();
		this.pushSRI(sri);
		String[] typeMismatch = { " does not correspond the port type ", ", it does correspond to the port type " };
		try {
			if (data instanceof Byte[]) {
				if (super.getType() == BulkIOType.OCTET) {
					this.pushPacket(ArrayUtils.toPrimitive((Byte[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Byte array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.OCTET.name());
				}
			} else if (data instanceof Short[]) {
				if (super.getType() == BulkIOType.USHORT) {
					this.pushPacket(ArrayUtils.toPrimitive((Short[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Short array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.USHORT.name());
				}
			} else if (data instanceof Integer[]) {
				if (super.getType() == BulkIOType.ULONG) {
					this.pushPacket(ArrayUtils.toPrimitive((Integer[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("An Integer array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.ULONG.name());
				}
			} else if (data instanceof Long[]) {
				if (super.getType() == BulkIOType.ULONG_LONG) {
					this.pushPacket(ArrayUtils.toPrimitive((Long[]) data), new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Long array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.ULONG_LONG.name());
				}
			} else {
				throw new IllegalArgumentException("Data type not supported by this receiver");
			}
			this.saveSRI();
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
