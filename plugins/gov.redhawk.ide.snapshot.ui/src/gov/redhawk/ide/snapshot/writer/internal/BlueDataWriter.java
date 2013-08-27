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
import nxm.sys.lib.Convert;
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
			data.setFormatType(Data.INT);  // treat char as 16-bit integer
			break;
		case DOUBLE:
			data.setFormatType(Data.DOUBLE);
			break;
		case FLOAT:
			data.setFormatType(Data.FLOAT);
			break;
		case LONG:
			data.setFormatType(Data.LONG);
			break;
		case LONG_LONG:
			data.setFormatType(Data.XLONG);
			break;
		case SHORT:
			data.setFormatType(Data.INT);
			break;
		case OCTET:
			data.setFormatType(Data.INT);   // upcast to next larger signed type (TODO: give option not to upcast?)
			break;
		case ULONG:
			data.setFormatType(Data.XLONG); // upcast to next larger signed type
			break;
		case ULONG_LONG:
			data.setFormatType(Data.XLONG); // CANNOT upcast
			break;
		case USHORT:
			data.setFormatType(Data.LONG);  // upcast to next larger signed type
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
		final byte type = Data.INT; 
		final int bufferSize = length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(double[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		final byte type = Data.DOUBLE;
		final int bufferSize = length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(float[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		final byte type = Data.FLOAT;
		final int bufferSize = length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(long[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			throw new IOException("Can not upcast unsigned long");
			// should we clip to Long.MAX_VALUE like corbareceiver?
		} else {
			final byte type = Data.XLONG;
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}

	@Override
	public void pushPacket(int[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.XLONG; // upcast to next larger signed type
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			final byte type = Data.LONG;
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}

	@Override
	public void pushPacket(short[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.LONG; // upcast to next larger signed type
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			final byte type = Data.INT;
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}
	
	@Override
	public void pushPacket(byte[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.INT; // upcast to next larger signed type
			final int bufferSize = length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), offset, type, byteBuffer, 0, type, length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			df.write(data, offset, length);
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
