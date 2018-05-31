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
package gov.redhawk.ide.snapshot.writer.internal;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.writer.BaseDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;
import mil.jpeojtrs.sca.util.UnsignedUtils;

/**
 * NOTE1: the BulkIO data type MUST NOT be changing during a Port snapshot.
 * NOTE2: this class is NOT meant to have pushPacket(..) calls from different threads simultaneously,
 * that should never happen in normal operation.
 */
public abstract class BinDataWriter extends BaseDataWriter {

	private RandomAccessFile raf;
	private FileChannel fileChannel;
	private ByteBuffer byteBuffer;
	private ByteOrder byteOrder;
	private long numSamples;
	private BitStream bitStream;

	public long getNumSamples() {
		return numSamples;
	}

	@Override
	public void open() throws IOException {
		if (isOpen()) {
			return;
		}
		raf = new RandomAccessFile(getFileDestination(), "rw");
		fileChannel = raf.getChannel();

		IDataWriterSettings settings = getSettings();
		if (settings.getType() == BulkIOType.BIT) {
			bitStream = new BitStream();
		} else {
			bitStream = null;
		}
		if (settings instanceof BinDataWriterSettings) {
			BinDataWriterSettings binSettings = (BinDataWriterSettings) settings;
			byteOrder = binSettings.getByteOrder();
		} else {
			byteOrder = BinDataWriterSettings.DEFAULT_BYTE_ORDER;
		}
		
		setOpen(true);
	}

	private ByteBuffer allocateByteBuffer(int byteBufferSize) {
		ByteBuffer buffer = this.byteBuffer;
		if ((buffer == null) || (buffer.capacity() < byteBufferSize)) {
			buffer = ByteBuffer.allocateDirect(byteBufferSize);
			buffer.order(byteOrder);
			this.byteBuffer = buffer;
		}
		buffer.position(0); // reset buffer's position 
		buffer.limit(byteBufferSize);
		return buffer;
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		BitSequence newBuffer = bitStream.handleBitBuffer(data);
		fileChannel.write(ByteBuffer.wrap(newBuffer.data, 0, newBuffer.bits / 8));
		numSamples += newBuffer.bits;
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
		for (int i = 0; i < data.length; i++) {
			buffer.putChar(data[i]);
		}
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
		DoubleBuffer tBuff = buffer.asDoubleBuffer();
		tBuff.put(data, 0, data.length);
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
		FloatBuffer tBuff = buffer.asFloatBuffer();
		tBuff.put(data, 0, data.length);
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer;
		boolean upcastUnsignedType = getSettings().isUpcastUnsigned() && getSettings().getType().isUnsigned();
		if (upcastUnsignedType) {
			throw new IOException("Can not store ulong long as upcasted value.");
			// TODO -should we still have to signed 64-bit integer and cap upper value like in corbareceiver?
		} else {
			buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
			LongBuffer tBuff = buffer.asLongBuffer();
			tBuff.put(data, 0, data.length);
		}
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer;
		boolean upcastUnsignedType = getSettings().isUpcastUnsigned() && getSettings().getType().isUnsigned();
		if (upcastUnsignedType) {
			buffer = allocateByteBuffer(data.length * BulkIOType.LONG_LONG.getBytePerAtom());
			LongBuffer tBuff = buffer.asLongBuffer();
			for (int i = 0; i < data.length; i++) {
				tBuff.put(UnsignedUtils.toSigned(data[i]));
			}
		} else {
			buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
			IntBuffer tBuff = buffer.asIntBuffer();
			tBuff.put(data, 0, data.length);
		}
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer;
		boolean upcastUnsignedType = getSettings().isUpcastUnsigned() && getSettings().getType().isUnsigned();
		if (upcastUnsignedType) {
			buffer = allocateByteBuffer(data.length * BulkIOType.LONG.getBytePerAtom());
			IntBuffer tBuff = buffer.asIntBuffer();
			for (int i = 0; i < data.length; i++) {
				tBuff.put(UnsignedUtils.toSigned(data[i]));
			}
		} else {
			buffer = allocateByteBuffer(data.length * getSettings().getType().getBytePerAtom());
			ShortBuffer tBuf = buffer.asShortBuffer();
			tBuf.put(data, 0, data.length);
		}
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		ByteBuffer buffer;
		boolean upcastUnsignedType = getSettings().isUpcastUnsigned() && getSettings().getType().isUnsigned();
		if (upcastUnsignedType) {
			buffer = allocateByteBuffer(data.length * BulkIOType.SHORT.getBytePerAtom());
			ShortBuffer tBuff = buffer.asShortBuffer();
			for (int i = 0; i < data.length; i++) {
				tBuff.put(UnsignedUtils.toSigned(data[i]));
			}
		} else {
			buffer = ByteBuffer.wrap(data, 0, data.length);
		}
		fileChannel.write(buffer);
		numSamples += data.length;
	}

	@Override
	public void close() throws IOException {
		setOpen(false);
		if (fileChannel != null) {
			// Write any left over bits (if applicable)
			if (bitStream != null) {
				BitSequence finalBits = bitStream.getFinalBits();
				if (finalBits.bits > 0) {
					fileChannel.write(ByteBuffer.wrap(finalBits.data, 0, 1));
					numSamples += finalBits.bits;
				}
				bitStream = null;
			}

			saveMetaData();

			// truncate file to actual size for case when we are overwriting an existing file
			BulkIOType type = getSettings().getType();
			long fileSizeInBytes;
			if (type == BulkIOType.BIT) {
				fileSizeInBytes = (long) Math.ceil(numSamples / 8.0);
			} else {
				fileSizeInBytes = numSamples * type.getBytePerAtom();
			}

			fileChannel.truncate(fileSizeInBytes);
			fileChannel.close();
			fileChannel = null;
		}
		if (raf != null) {
			raf.close();
			raf = null;
		}
		numSamples = 0;
	}

	public File getMetaDataFile() {
		File destination = getFileDestination();
		int iLastDot = destination.getName().lastIndexOf('.');
		if (iLastDot > 0) {
			return new File(destination.getParentFile(), destination.getName().substring(0, iLastDot) + "." + getMetaDataFileExtension());
		} else {
			return new File(destination.getParentFile(), destination.getName() + "." + getMetaDataFileExtension());
		}
	}
	
	protected ByteOrder getByteOrder() {
		return byteOrder;
	}
	
	protected abstract void saveMetaData() throws IOException;

	protected abstract String getMetaDataFileExtension();

}
