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
package gov.redhawk.ide.snapshot.tests.writer.internal.helpers;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.junit.Assert;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.TCM_CPU;
import BULKIO.TCS_VALID;
import CF.DataType;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import nxm.sys.lib.Data;

public class FloatTestHelper implements ITestHelper {

	private static final float[] EXPECTED_FLOATS = new float[] { 1.0f, -2.0f, 3.25f, 4.0f, -5.0f, 6.0f, Float.MIN_VALUE, Float.MAX_VALUE };

	@Override
	public BulkIOType getType() {
		return BulkIOType.FLOAT;
	}

	@Override
	public int getSampleCount() {
		return EXPECTED_FLOATS.length;
	}

	@Override
	public void write(IDataWriter writer) throws IOException {
		// Write a sequence of various-sized buffers
		final String streamID = "my_float_stream";
		writer.pushSRI(new StreamSRI(1, 0, 0.1, BULKIO.UNITS_TIME.value, 0, 0, 0, BULKIO.UNITS_NONE.value, (short) 0, streamID, true, new DataType[0]));
		writer.open();
		int sampleOffset = 0;
		writer.pushPacket(new float[] { 1.0f }, createTime(sampleOffset), false, streamID);
		sampleOffset += 1;
		writer.pushPacket(new float[] { -2.0f, 3.25f }, createTime(sampleOffset), false, streamID);
		sampleOffset += 2;
		writer.pushPacket(new float[] { 4.0f, -5.0f, 6.0f, Float.MIN_VALUE, Float.MAX_VALUE }, createTime(sampleOffset), false, streamID);
		sampleOffset += 5;
		writer.close();
	}

	private static PrecisionUTCTime createTime(int sampleOffset) {
		double timeSec = sampleOffset * 0.1;
		return new PrecisionUTCTime(TCM_CPU.value, TCS_VALID.value, 0, (int) timeSec, timeSec - (int) timeSec);
	}

	@Override
	public void assertData(Data data) {
		float[] array = data.castF(true);
		try {
			Assert.assertArrayEquals(EXPECTED_FLOATS, array, 0);
		} finally {
			data.uncast(array, false);
		}
	}

	@Override
	public void assertData(ByteBuffer buffer) {
		float[] dataFromFile = new float[EXPECTED_FLOATS.length];
		buffer.position(0);
		buffer.asFloatBuffer().get(dataFromFile);
		Assert.assertArrayEquals(EXPECTED_FLOATS, dataFromFile, 0);
	}

}
