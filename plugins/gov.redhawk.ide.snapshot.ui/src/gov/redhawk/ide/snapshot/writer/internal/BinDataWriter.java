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
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value;
import gov.redhawk.ide.snapshot.writer.BaseDataWriter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import mil.jpeojtrs.sca.util.AnyUtils;
import mil.jpeojtrs.sca.util.UnsignedUtils;
import BULKIO.PrecisionUTCTime;
import CF.DataType;
import CF.DataTypeHelper;

/**
 * 
 */
public abstract class BinDataWriter extends BaseDataWriter {

	private RandomAccessFile raf;
	private FileChannel fileChannel;

	private long numSamples;

	/**
	 * This method collects the information in the CF.DataType and stores it 
	 * in the EMF model CFDataType
	 * @param cfType : the EMF model for the CF.DataType
	 * @param data	:a CF.DataType to be processed
	 * @return the EMF model containing the data from the CF.DataType
	 */
	protected CFDataType readCFDataType(CFDataType cfType, DataType data) {
		Object temp;
		if (DataTypeHelper.type().equivalent(data.value.type())) {
			temp = DataTypeHelper.extract(data.value);
		} else {
			temp = AnyUtils.convertAny(data.value);
		}
		cfType.setId(data.id);
		if (temp instanceof DataType) {
			cfType.setValue(readCFDataType(SnapshotMetadataFactory.eINSTANCE.createCFDataType(), (DataType) temp));
		} else {
			Value value = SnapshotMetadataFactory.eINSTANCE.createValue();
			value.setValue(temp.toString());
			//value.setJavaType(DataReceiverUtils.getAnyTypeDataType(temp));
			value.setJavaType(temp.getClass().getName());
			cfType.setValue(value);
		}
		return cfType;
	}
	
	
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
		setOpen(true);
	}

	@Override
	public void pushPacket(char[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			buffer.putChar(data[i]);
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(double[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			buffer.putDouble(data[i]);
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(float[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			buffer.putFloat(data[i]);
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(long[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			if (getSettings().isUpcastUnsigned() && isUnsignedData()) {
				throw new IOException("Can not store ulong long as upcasted value.");
			} else {
				buffer.putLong(data[i]);
			}
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(int[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			if (getSettings().isUpcastUnsigned() && isUnsignedData()) {
				buffer.putLong(UnsignedUtils.toSigned(data[i]));
			} else {
				buffer.putInt(data[i]);
			}
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(byte[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			if (getSettings().isUpcastUnsigned() && isUnsignedData()) {
				buffer.putShort(UnsignedUtils.toSigned(data[i]));
			} else {
				buffer.put(data[i]);
			}
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void pushPacket(short[] data, int offset, int length, PrecisionUTCTime time) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocateDirect(length * getSettings().getType().getBytePerAtom());
		for (int i = offset; i < length; i++) {
			if (getSettings().isUpcastUnsigned() && isUnsignedData()) {
				buffer.putInt(UnsignedUtils.toSigned(data[i]));
			} else {
				buffer.putShort(data[i]);
			}
		}
		fileChannel.write(buffer);
		numSamples += length;
	}

	@Override
	public void close() throws IOException {
		setOpen(false);
		if (fileChannel != null) {

			saveMetaData();

			// truncate file to actual size for case when we are overwriting an existing file
			BulkIOType type = getSettings().getType();
			long fileSizeInBytes = numSamples * type.getBytePerAtom();

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

	protected abstract void saveMetaData() throws IOException;
	
	public File getMetaDataFile() {
		File destination = getFileDestination();
		int iLastDot = destination.getName().lastIndexOf('.');
		if (iLastDot > 0) {
			return new File(destination.getParentFile(), destination.getName().substring(0, iLastDot) + "." + getMetaDataFileExtension());
		} else {
			return new File(destination.getParentFile(), destination.getName() + "." + getMetaDataFileExtension());
		}
	}


	protected abstract String getMetaDataFileExtension();

}
