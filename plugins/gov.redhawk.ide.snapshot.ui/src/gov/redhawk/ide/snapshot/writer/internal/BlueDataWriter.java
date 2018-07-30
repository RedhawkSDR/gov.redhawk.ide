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

	private StreamSRI currentSri;

	/**
	 * This method maps a BulkIO type from SRI to an X-Midas digraph string.
	 * @param sri
	 * @return The appropriate X-Midas type to use (includes upcasting for unsigned types)
	 */
	private String getMidasDataType(StreamSRI sri) {
		char[] format = new char[2];
		switch (sri.mode) {
		case 0:
			format[0] = 'S';
			break;
		case 1:
			format[1] = 'C';
			break;
		default:
			throw new IllegalArgumentException("Unknown BulkIO SRI mode: " + sri.mode);
		}
		format[1] = getSettings().getType().getMidasType();
		return new String(format);
	}

	@Override
	public void open() throws IOException {
		File file = getFileDestination();

		Midas m = NeXtMidas.getGlobalInstance().getMidasContext();
		this.df = new DataFile(m, file.getAbsolutePath());
		this.df.open(DataFile.OUTPUT);

		df.setFormat(getMidasDataType(currentSri));
		double xdelta = (currentSri.xdelta == 0) ? 1.0 : currentSri.xdelta; // delta should NOT be zero
		double ydelta = (currentSri.ydelta == 0) ? 1.0 : currentSri.ydelta; // delta should NOT be zero
		df.setXStart(currentSri.xstart);
		df.setXDelta(xdelta);
		df.setXUnits(currentSri.xunits);
		df.setYStart(currentSri.ystart);
		df.setYDelta(ydelta);
		df.setYUnits(currentSri.yunits);
		df.setFrameSize(currentSri.subsize);
		df.getKeywordsObject().put("hversion", currentSri.hversion + "");
		df.getKeywordsObject().put("streamID", currentSri.streamID + "");
		df.getKeywordsObject().put("blocking", currentSri.blocking + "");

		setOpen(true);
	}

	@Override
	public void pushSRI(StreamSRI sri) throws IOException {
		this.currentSri = sri;
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
