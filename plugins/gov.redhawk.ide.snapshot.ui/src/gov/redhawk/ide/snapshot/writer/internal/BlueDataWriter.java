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
package gov.redhawk.ide.snapshot.writer.internal;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.writer.BaseDataWriter;

import java.io.File;
import java.io.IOException;

import mil.jpeojtrs.sca.util.UnsignedUtils;
import nxm.sys.lib.Data;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

/**
 * 
 */
public class BlueDataWriter extends BaseDataWriter {

	/** the Object used to write data to a Midas .BLUE file */
	private DataFile df;
	/** the format specification of a .BLUE file*/
	private Data dataFormat;

	private StreamSRI sri;

	/**
	 * This method maps a BulkIOType to a Data
	 * @param t : the BulkIOType to map to the DataTypes type
	 * @return the equivalent Data Type
	 */
	private Data getMidasDataType(BulkIOType t) {
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
		case OCTET:
			//return 'I';
			data.setFormatType(Data.INT);
			break;
		case ULONG:
			//return 'X';
			data.setFormatType(Data.XLONG);
			break;
		case ULONG_LONG:
			//return 'X';
			data.setFormatType(Data.XLONG);
			break;
		case USHORT:
			//return 'L';
			data.setFormatType(Data.LONG);
			break;
		default:
			throw new IllegalArgumentException("The BulkIOType was not a recognized Type");
		}
		return data;
	}

	@Override
	public void open() throws IOException {
		File file = getFileDestination();
		BulkIOType type = getSettings().getType();

		Midas m = NeXtMidas.getGlobalInstance().getMidasContext();
		this.df = new DataFile(m, file.getAbsolutePath());
		this.df.open(DataFile.OUTPUT);
		this.dataFormat = this.getMidasDataType(type);

		dataFormat.setFormatMode((sri.mode == 0) ? 'S' : 'C');
		df.setFormat(dataFormat.getFormat());
		double xdelta = (sri.xdelta == 0) ? 1.0 : sri.xdelta; // delta should NOT be zero
		double ydelta = (sri.ydelta == 0) ? 1.0 : sri.ydelta; // delta should NOT be zero
		df.setXStart(sri.xstart);
		df.setXDelta(xdelta);
		df.setXUnits(sri.xunits);
		df.setYStart(sri.ystart);
		df.setYDelta(ydelta);
		df.setYUnits(sri.yunits);
		df.setFrameSize(sri.subsize);
		df.getKeywordsObject().put("hversion", sri.hversion + "");
		df.getKeywordsObject().put("streamID", sri.streamID + "");
		df.getKeywordsObject().put("blocking", sri.blocking + "");
		
		setOpen(true);
	}

	@Override
	public void pushSRI(StreamSRI sri) throws IOException {
		this.sri = sri;
	}

	@Override
	public void pushPacket(char[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		char[] subArray = new char[length];
		System.arraycopy(data, offset, subArray, 0, length);
		df.write(new Data(subArray));
	}

	@Override
	public void pushPacket(double[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		double[] subArray = new double[length];
		System.arraycopy(data, offset, subArray, 0, length);
		df.write(new Data(subArray));
	}

	@Override
	public void pushPacket(float[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		float[] subArray = new float[length];
		System.arraycopy(data, offset, subArray, 0, length);
		df.write(new Data(subArray));
	}

	@Override
	public void pushPacket(long[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			throw new IOException("Can not upcast unsigned long");
		} else {
			long[] subArray = new long[length];
			System.arraycopy(data, offset, subArray, 0, length);
			df.write(new Data(subArray));
		}
	}

	@Override
	public void pushPacket(int[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			long[] subArray = new long[length];
			for (int i = 0; i < length; i++) {
				subArray[i] = UnsignedUtils.toSigned(data[i]);
			}
			df.write(new Data(subArray));
		} else {
			int[] subArray = new int[length];
			System.arraycopy(data, offset, subArray, 0, length);
			df.write(new Data(subArray));
		}
	}

	@Override
	public void pushPacket(byte[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			short[] subArray = new short[length];
			for (int i = 0; i < length; i++) {
				subArray[i] = UnsignedUtils.toSigned(data[i]);
			}
			df.write(new Data(subArray));
		} else {
			byte[] subArray = new byte[length];
			System.arraycopy(data, offset, subArray, 0, length);
			df.write(new Data(subArray));
		}
	}

	@Override
	public void pushPacket(short[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			int[] subArray = new int[length];
			for (int i = 0; i < length; i++) {
				subArray[i] = UnsignedUtils.toSigned(data[i]);
			}
			df.write(new Data(subArray));
		} else {
			short[] subArray = new short[length];
			System.arraycopy(data, offset, subArray, 0, length);
			df.write(new Data(subArray));
		}
	}

	@Override
	public void close() throws IOException {
		if (df != null) {
			setOpen(false);
			df.close();
			df = null;
		}
	}

}
