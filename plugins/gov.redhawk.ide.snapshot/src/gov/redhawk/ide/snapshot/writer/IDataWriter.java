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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import BULKIO.BitSequence;
import BULKIO.StreamSRI;

/**
 * @since 1.0
 */
public interface IDataWriter extends Closeable {

	void setSettings(IDataWriterSettings settings);

	IDataWriterSettings getSettings();

	void open() throws IOException;

	boolean isOpen();

	void pushSRI(StreamSRI sri) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(BitSequence data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(char[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(double[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(float[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(long[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(int[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(byte[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @since 1.0
	 */
	void pushPacket(short[] data, BULKIO.PrecisionUTCTime time, boolean eos, String streamID) throws IOException;

	/**
	 * @return files written to by the data receiver
	 */
	List<File> getOutputFileList();

}
