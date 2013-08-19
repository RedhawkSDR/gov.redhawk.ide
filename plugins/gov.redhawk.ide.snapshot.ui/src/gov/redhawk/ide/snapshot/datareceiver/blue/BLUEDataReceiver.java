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
package gov.redhawk.ide.snapshot.datareceiver.blue;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;
import nxm.sys.lib.Convert;
import nxm.sys.lib.Data;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.NonNull;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataCharOperations;
import BULKIO.dataDoubleOperations;
import BULKIO.dataFloatOperations;
import BULKIO.dataLongLongOperations;
import BULKIO.dataLongOperations;
import BULKIO.dataShortOperations;

//TODO: DELETE ME
@Deprecated
public class BLUEDataReceiver extends SuperBLUEReceiver implements dataDoubleOperations, dataFloatOperations, dataLongLongOperations, dataLongOperations,
		dataShortOperations, dataCharOperations, IDataReceiver {

	
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
	/**boolean for whether or not an end of stream has occurred*/
	private boolean eos = false;

	public BLUEDataReceiver(File file, long numSamples, double sampleDurationTime, BulkIOType type, CaptureMethod method) throws IOException {
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
	public void pushSRI(StreamSRI sri) {
		if (totalNumSamples > super.getCurrentSamples() && !this.eos) {
			if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
				this.currentSampleDelta = (sri.xdelta != 0) ? sri.xdelta : 1;
				totalNumSamples = ((long) (((this.totalTime - this.currentTimeDuration) / currentSampleDelta) + .5)) + super.getCurrentSamples();
			}
			super.pushSRI(sri);
		}
	}

	/**
	 * This method maps a BulkIOType to a Data
	 * @param t : the BulkIOType to map to the DataTypes super.getType() 
	 * @return the equivalent Data Type
	 */
	@Override
	protected Data getDataType(BulkIOType t) {
		Data data = new Data();
		switch (t) {
		case CHAR:
			data.setFormatType('I');
			break;
		case DOUBLE:
			//return 'D';
			data.setFormatType('D');
			break;
		case FLOAT:
			//return 'F';
			data.setFormatType('F');
			break;
		case LONG:
			//return 'L';
			data.setFormatType('L');
			break;
		case LONG_LONG:
			//return 'X';
			data.setFormatType('X');
			break;
		case SHORT:
			//return 'I';
			data.setFormatType('I');
			break;
		default:
			throw new IllegalArgumentException("The BulkIOType was not a recognized signed super.getType() ");
		}
		return data;
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
		try {
			int byteBufferLen = super.getType().getBytePerAtom() * length;
			byte[] byteBuffer = new byte[byteBufferLen];
			// convert double[] to byte[]
			Convert.ja2bb(data, Data.DOUBLE, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
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
		/*Data dataFile = super.getDataFile() .getDataBuffer(data.length, Data.INT);
		dataFile.uncast((short[])data, true);
		super.getDataFile() .write(dataFile);*/
		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
		try {
			int byteBufferLen = super.getType().getBytePerAtom() * length;
			byte[] byteBuffer = new byte[byteBufferLen];
			// convert int(char)[] to byte[]
			Convert.ja2bb(data, Data.INT, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
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
		try {
			int byteBufferLen = super.getType().getBytePerAtom() * length;
			byte[] byteBuffer = new byte[byteBufferLen];
			// convert int(short)[] to byte[]
			Convert.ja2bb(data, Data.INT, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
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
		try {
			int byteBufferLen = super.getType().getBytePerAtom() * length;
			byte[] byteBuffer = new byte[byteBufferLen];
			// convert long(int)[] to byte[]
			Convert.ja2bb(data, Data.LONG, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
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
		try {
			int byteBufferLen = super.getType().getBytePerAtom() * length;
			byte[] byteBuffer = new byte[byteBufferLen];
			// convert XLong(long)[] to byte[]
			Convert.ja2bb(data, Data.XLONG, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
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
//		int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
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
		
		int numFloatsToProcess = Math.min(data.length, (int) numDataLeftToProcess);

		try {
			int byteBufferLen = super.getType().getBytePerAtom() * numFloatsToProcess;
			byte[] byteBuffer = new byte[byteBufferLen];
// BEGIN DEBUG CODE
ByteBuffer bBuf = ByteBuffer.wrap(byteBuffer); // DEBUG
byte[] array = bBuf.array(); // DEBUG
boolean isSame = (array == byteBuffer); // DEBUG
System.out.println("ByteBuffer.array() is same as our byte[] = " + isSame);// DEBUG
// END DEBUG CODE
			// convert float[] to byte[]
			Convert.ja2bb(data, Data.FLOAT, byteBuffer, super.getDataFile().dataType, data.length);
			super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
		} catch (Exception e) {
			super.writeException(new IOException(e));
		}
		super.incrementSamples(super.deriveNumberOfSamples(numFloatsToProcess));
		this.incrementTime(super.deriveNumberOfSamples(numFloatsToProcess) * this.currentSampleDelta);
	}

	// increament increment assuper.getDataFile() a
	private synchronized void incrementTime(double time) {
		this.currentTimeDuration += time;
	}

	public void processSamples(IProgressMonitor monitor) throws InterruptedException, IOException {
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

				}
				if (this.eos) {
					break;
				}
			}
		} finally {
			//TODO truncate if desired
			monitor.done();
		}
	}

	@Override
	public void writeFile(Object[] data, StreamSRI sri) throws IOException {
		this.pushSRI(sri);
		String[] typeMismatch = { " does not correspond the port type ", ", it does correspond to the port type " };
		//try {
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
		} else if (data instanceof Short[]) {
			if (super.getType() == BulkIOType.SHORT) {
				this.pushPacket(ArrayUtils.toPrimitive((Short[]) data), new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Short array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.SHORT.name());
			}
		} else if (data instanceof Integer[]) {
			if (super.getType() == BulkIOType.LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Integer[]) data), new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("An Integer array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.LONG.name());
			}
		} else if (data instanceof Long[]) {
			if (super.getType() == BulkIOType.LONG_LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Long[]) data), new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Long array" + typeMismatch[0] + super.getType() + typeMismatch[1] + BulkIOType.LONG_LONG.name());
			}
		} else {
			throw new IllegalArgumentException("Data super.getType() not supported by this receiver");
		}
		/*} finally {
			try {
		        channel.truncate(super.getCurrentSamples() * super.getType() .getBytePerAtom());
		    } catch (IOException e) {
		        // PASS
		    }
		}*/
	}

	@Override
	@NonNull
	public List<FilePair> getOutpuFileList() {
		// TODO Auto-generated method stub
		return null;
	}

}
