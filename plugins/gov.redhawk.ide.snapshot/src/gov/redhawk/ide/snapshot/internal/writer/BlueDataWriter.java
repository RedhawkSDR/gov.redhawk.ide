/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.snapshot.internal.writer;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.writer.BaseDataWriter;

import java.io.File;
import java.io.IOException;
import java.nio.ByteOrder;

import mil.jpeojtrs.sca.util.UnsignedUtils;
import nxm.sys.lib.Convert;
import nxm.sys.lib.Data;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;
import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public class BlueDataWriter extends BaseDataWriter {

	/**
	 * the Object used to write data to a Midas .BLUE file
	 */
	private DataFile df;

	private StreamSRI currentSRI;

	private BitStream bitStream;

	/**
	 * This method maps a BulkIO type from SRI to an X-Midas digraph format (e.g. "SI", "CF", etc)
	 * @param sri The BULKIO SRI (used to determine scalar / complex)
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

		df.setFormat(getMidasDataType(currentSRI));
		if (getSettings().getType() == BulkIOType.BIT) {
			bitStream = new BitStream();
			// Per "Midas BLUE File Format" version 1.1.0, "Bits are packed into bytes starting with the most
			// significant bit (MSB0): mask = 0x80 >> (idx mod 8)". However, NeXtMidas (unlike X-Midas) uses the
			// data_rep to determine the *bit* ordering. We thus adjust the data_rep purely for NeXtMidas. Note
			// however that due to NeXtMidas's preference for LSB0, it appears to incorrectly read the bits in the
			// final byte if that byte isn't fully packed. X-Midas doesn't have this problem.
			df.setDataRep((ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN) ? "EEEI" : "IEEE");
		} else {
			bitStream = null;
		}
		df.setXStart(currentSRI.xstart);
		df.setXDelta(currentSRI.xdelta);
		df.setXUnits(currentSRI.xunits);
		df.setYStart(currentSRI.ystart);
		df.setYDelta(currentSRI.ydelta);
		df.setYUnits(currentSRI.yunits);
		df.setFrameSize(currentSRI.subsize);
		df.getKeywordsObject().put("hversion", Integer.toString(currentSRI.hversion)); //$NON-NLS-1$
		df.getKeywordsObject().put("streamID", currentSRI.streamID); //$NON-NLS-1$
		df.getKeywordsObject().put("blocking", Boolean.toString(currentSRI.blocking)); //$NON-NLS-1$

		setOpen(true);
	}

	@Override
	public void pushSRI(StreamSRI sri) throws IOException {
		this.currentSRI = sri;
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		BitSequence newBuffer = bitStream.handleBitBuffer(data);
		df.write(newBuffer.data, 0, newBuffer.bits / 8);
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		final byte type = Data.INT;
		final int bufferSize = data.length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		final byte type = Data.DOUBLE;
		final int bufferSize = data.length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		final byte type = Data.FLOAT;
		final int bufferSize = data.length * Data.getBPS(type);
		byte[] byteBuffer = new byte[bufferSize];
		Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
		df.write(byteBuffer, 0, bufferSize);
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		if (isUnsignedData()) {
			throw new IOException("Can not upcast unsigned long");
			// should we clip to Long.MAX_VALUE like corbareceiver?
		} else {
			final byte type = Data.XLONG;
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.XLONG; // upcast to next larger signed type
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			final byte type = Data.LONG;
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.LONG; // upcast to next larger signed type
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			final byte type = Data.INT;
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(data, 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		}
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		if (isUnsignedData()) {
			final byte type = Data.INT; // upcast to next larger signed type
			final int bufferSize = data.length * Data.getBPS(type);
			byte[] byteBuffer = new byte[bufferSize];
			Convert.ja2bb(UnsignedUtils.toSigned(data), 0, type, byteBuffer, 0, type, data.length);
			df.write(byteBuffer, 0, bufferSize);
		} else {
			df.write(data, 0, data.length);
		}
	}

	@Override
	public void close() throws IOException {
		if (bitStream != null) {
			// Write any left over bits
			BitSequence finalBits = bitStream.getFinalBits();
			if (finalBits.bits > 0) {
				df.write(finalBits.data, 0, 1);
				df.setSize(df.getOffset() - (8 - finalBits.bits));
			}
			finalBits = null;
		}

		if (df != null) {
			setOpen(false);
			df.close();
			df = null;
		}
	}

}
