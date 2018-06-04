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
package gov.redhawk.ide.snapshot.tests.internal.capture;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;

public class ReceiverBuffer implements IDataWriter {

	// CHECKSTYLE:OFF

	public class Packet {
		public Packet(Object data, PrecisionUTCTime time, boolean eos, String streamID) {
			this.data = data;
			this.time = time;
			this.eos = eos;
			this.streamID = streamID;
		}

		Object data;
		PrecisionUTCTime time;
		boolean eos;
		String streamID;
	}

	// CHECKSTYLE:ON

	private List<Object> buffer = Collections.synchronizedList(new ArrayList<>());
	private IDataWriterSettings settings = null;
	private boolean open = false;

	public List<Object> getBuffer() {
		return buffer;
	}

	@Override
	public void close() throws IOException {
		open = false;
	}

	@Override
	public void setSettings(IDataWriterSettings settings) {
		this.settings = settings;
	}

	@Override
	public IDataWriterSettings getSettings() {
		return settings;
	}

	@Override
	public void open() throws IOException {
		open = true;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void pushSRI(StreamSRI sri) throws IOException {
		buffer.add(sri);
	}

	@Override
	public void pushPacket(BitSequence data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) throws IOException {
		buffer.add(new Packet(data, time, eos, streamID));
	}

	@Override
	public List<File> getOutputFileList() {
		return Collections.emptyList();
	}

}
