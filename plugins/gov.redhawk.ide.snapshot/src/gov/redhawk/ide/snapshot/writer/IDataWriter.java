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
package gov.redhawk.ide.snapshot.writer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import BULKIO.StreamSRI;

/**
 * @since 1.0
 */
public interface IDataWriter {

	void setSettings(IDataWriterSettings settings);

	IDataWriterSettings getSettings();

	void open() throws IOException;

	boolean isOpen();

	void pushSRI(StreamSRI sri) throws IOException;

	void pushPacket(char[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(double[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(float[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(long[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(int[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(byte[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void pushPacket(short[] data, int offset, int length, BULKIO.PrecisionUTCTime time) throws IOException;

	void close() throws IOException;

	/**
	 * @return files written to by the data receiver
	 */
	List<File> getOutputFileList();

}
