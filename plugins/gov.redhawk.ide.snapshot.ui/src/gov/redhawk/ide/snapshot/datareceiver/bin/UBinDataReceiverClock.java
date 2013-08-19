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
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

//import nxm.redhawk.prim.corbareceiver;


import java.util.List;

import mil.jpeojtrs.sca.util.UnsignedUtils;
import nxm.sys.lib.Time;

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
public class UBinDataReceiverClock extends SuperBinReceiver implements dataOctetOperations, dataUlongLongOperations, dataUlongOperations, dataUshortOperations,
		IDataReceiver {

	/**The total time to capture samples in clock time*/
	private double totalTime;
	/**The time samples started being captured*/
	private Time startTime;
	/**The current Time*/
	private Time currentTime;
	private boolean eos = false;
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	public UBinDataReceiverClock(File file, double time, BulkIOType type, CaptureMethod method) throws IOException {
		super(file, type);
		switch (method) {
		case CLOCK_TIME:
			totalTime = time;
			break;
		case INDEFINITELY:
			totalTime = Double.POSITIVE_INFINITY;
			break;
		default:
			throw new IllegalArgumentException("Unsupported Processing Type");
		}
	}

	@Override
	public void saveXML() throws IOException {
		super.getMetaDataModel().setNumberOfSamples(super.getCurrentSamples() - this.startSample);
		this.startSample = super.getCurrentSamples();
		super.getMetaDataModel().getTime().setEndTime(new SimpleDateFormat(super.getTimeFormat()).format(currentTime.toDateObject()));
		super.saveXML();
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (Time.toTime(new Date()).diff(startTime) < this.totalTime && !this.eos) {
			super.pushSRI(sri);
		}
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		short[] octet = UnsignedUtils.toSigned(data);
		int length = (int) data.length;
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.SHORT.getBytePerAtom() * length);
		ShortBuffer tBuff = bBuffer.asShortBuffer();
		tBuff.put(octet, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));

	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int[] unsignedShort = UnsignedUtils.toSigned(data);
		int length = (int) data.length;
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.LONG.getBytePerAtom() * length);
		IntBuffer tBuff = bBuffer.asIntBuffer();
		tBuff.put(unsignedShort, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		long[] unsignedLong = UnsignedUtils.toSigned(data);
		int length = (int) data.length;
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(BulkIOType.LONG_LONG.getBytePerAtom() * length);
		LongBuffer tBuff = bBuffer.asLongBuffer();
		tBuff.put(unsignedLong, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		long[] unsignedLongLong = new long[data.length];
		for (int i = 0; i < unsignedLongLong.length; i++) {
			if (data[i] < 0) {
				unsignedLongLong[i] = Long.MAX_VALUE;
			} else {
				unsignedLongLong[i] = data[i];
			}
		}
		int length = (int) data.length;
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
	}

	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException {
		startTime = Time.toTime(new Date());
		super.getMetaDataModel().getTime().setStartTime(new SimpleDateFormat(super.getTimeFormat()).format(startTime.toDateObject()));
		int work;
		if (this.totalTime > Integer.MAX_VALUE) {
			work = IProgressMonitor.UNKNOWN;
		} else {
			work = (int) this.totalTime;
		}
		monitor.beginTask("Acquiring samples...", work);
		try {
			Time lastIncrement = startTime;
			this.currentTime = startTime;
			while (this.currentTime.diff(startTime) < this.totalTime) {
				if (getWriteException() != null) {
					throw getWriteException();
				}
				double diff = this.currentTime.diff(lastIncrement);
				if ((int) diff > 0) {
					monitor.worked((int) diff);
					lastIncrement = this.currentTime;
				}
				synchronized (this) {
					wait(500);
					if (monitor.isCanceled()) {
						//throw new CancellationException();
						this.eos = true;
						break;
					}
				}
				if (this.eos) {
					break;
				}
				this.currentTime = Time.toTime(new Date());
			}
			this.saveXML();
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
		super.getMetaDataModel().getTime().setStartTime(new SimpleDateFormat(super.getTimeFormat()).format(new Date()));
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
