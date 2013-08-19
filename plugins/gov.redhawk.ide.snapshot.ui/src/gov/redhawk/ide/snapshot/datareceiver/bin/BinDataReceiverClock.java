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

import nxm.sys.lib.Time;

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

// TODO: DELETE ME
@Deprecated
public class BinDataReceiverClock extends SuperBinReceiver implements dataDoubleOperations, dataFloatOperations, dataLongLongOperations, dataLongOperations,
		dataShortOperations, dataCharOperations, dataOctetOperations, dataUlongLongOperations, dataUlongOperations, dataUshortOperations, IDataReceiver {

	/**The total time to capture samples in clock time*/
	private double totalTime;
	/**The time samples started being captured*/
	private Time startTime;
	/**The current Time*/
	private Time currentTime;
	/**boolean for whether or not an end of stream has occurred*/
	private boolean eos = false;
	/** number of samples saved when this file was opened */
	private long startSample = 0;

	public BinDataReceiverClock(File file, double time, BulkIOType type, CaptureMethod method) throws IOException {
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
		if (Time.toTime(new Date()).diff(startTime) < this.totalTime) {
			super.pushSRI(sri);
		}
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) data.length;
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

	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) data.length;
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
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) data.length;
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		bBuffer.put(data, 0, length);
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
		int length = (int) data.length;
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
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) data.length;
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
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
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

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime || super.getWriteException() != null || this.eos) {
			return;
		}
		if (eos) {
			this.eos = true;
		}
		int length = (int) data.length;
		ByteBuffer bBuffer = ByteBuffer.allocateDirect(super.getType().getBytePerAtom() * length);
		FloatBuffer tBuff = bBuffer.asFloatBuffer();
		tBuff.put(data, 0, length);
		try {
			super.getChannel().write(bBuffer);
		} catch (IOException e) {
			super.writeException(e);
			return;
		}
		super.incrementSamples(super.deriveNumberOfSamples(length));
	}

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
				if (super.getWriteException() != null) {
					throw super.getWriteException();
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

	@Override
	@NonNull
	public List<FilePair> getOutpuFileList() {
		// TODO Auto-generated method stub
		return null;
	}

}
