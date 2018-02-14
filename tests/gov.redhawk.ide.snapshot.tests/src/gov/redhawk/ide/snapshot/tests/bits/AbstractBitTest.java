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
package gov.redhawk.ide.snapshot.tests.bits;

import java.io.IOException;

import BULKIO.BitSequence;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.TCM_CPU;
import BULKIO.TCS_VALID;
import CF.DataType;
import gov.redhawk.ide.snapshot.writer.IDataWriter;

public class AbstractBitTest {

	protected static final byte[] EXPECTED_BITS = new byte[] { (byte) 0b10101010, (byte) 0b11111111, 0b00000000, (byte) 0b10101010, (byte) 0b11100110, 0b01010000 };
	protected static final int TOTAL_SAMPLES = 45;

	protected void write(IDataWriter writer) throws IOException {
		// Write a sequence of various-sized bit buffers
		final String streamID = "abc";
		writer.pushSRI(new StreamSRI(1, 0, 0.1, BULKIO.UNITS_TIME.value, 0, 0, 0, BULKIO.UNITS_NONE.value, (short) 0, streamID, true, new DataType[0]));
		writer.open();
		int sampleOffset = 0;
		writer.pushPacket(new BitSequence(new byte[] { (byte) 0b10000000 }, 1), createTime(sampleOffset), false, streamID);
		sampleOffset += 1;
		writer.pushPacket(new BitSequence(new byte[] { (byte) 0b01000000 }, 2), createTime(sampleOffset), false, streamID);
		sampleOffset += 2;
		writer.pushPacket(new BitSequence(new byte[] { 0b01010000 }, 5), createTime(sampleOffset), false, streamID);
		sampleOffset += 5;
		writer.pushPacket(new BitSequence(new byte[] { (byte) 0b11111111, 0b00000000 }, 16), createTime(sampleOffset), false, streamID);
		sampleOffset += 16;
		writer.pushPacket(new BitSequence(new byte[] { (byte) 0b10101010, (byte) 0b10000000 }, 9), createTime(sampleOffset), false, streamID);
		sampleOffset += 9;
		writer.pushPacket(new BitSequence(new byte[] { (byte) 0b11001100, (byte) 0b10000000 }, 9), createTime(sampleOffset), false, streamID);
		sampleOffset += 9;
		writer.pushPacket(new BitSequence(new byte[] { 0b01000000 }, 3), createTime(sampleOffset), false, streamID);
		sampleOffset += 3;
		writer.close();
	}

	private PrecisionUTCTime createTime(int sampleOffset) {
		double timeSec = sampleOffset * 0.1;
		return new PrecisionUTCTime(TCM_CPU.value, TCS_VALID.value, 0, (int) timeSec, timeSec - (int) timeSec);
	}

}
