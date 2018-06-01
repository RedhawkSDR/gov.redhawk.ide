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
package gov.redhawk.ide.snapshot.tests.internal.writer.helpers;

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

public class OctetTestHelper implements ITestHelper {

	/**
	 * This is the way we receive them from BulkIO ports. They're really unsigned though.
	 */
	private static final byte[] EXPECTED_OCTETS_BIO = new byte[] { 1, -2, 3, 4, -5, 6, 0, -1 };

	/**
	 * Same data as {@link #EXPECTED_OCTETS_BIO}, but using a primitive that can express the full range of values
	 * accurately without an unsigned primitive type.
	 */
	private static final short[] EXPECTED_OCTETS_WIDENED = new short[] { 1, 254, 3, 4, 251, 6, 0, 0xFF };

	@Override
	public BulkIOType getType() {
		return BulkIOType.OCTET;
	}

	@Override
	public int getSampleCount() {
		return EXPECTED_OCTETS_BIO.length;
	}

	@Override
	public void write(IDataWriter writer) throws IOException {
		// Write a sequence of various-sized buffers
		final String streamID = "my_octet_stream";
		writer.pushSRI(new StreamSRI(1, 0, 0.1, BULKIO.UNITS_TIME.value, 0, 0, 0, BULKIO.UNITS_NONE.value, (short) 0, streamID, true, new DataType[0]));
		writer.open();
		int sampleOffset = 0;
		writer.pushPacket(new byte[] { 1 }, createTime(sampleOffset), false, streamID);
		sampleOffset += 1;
		writer.pushPacket(new byte[] { -2, 3 }, createTime(sampleOffset), false, streamID);
		sampleOffset += 2;
		writer.pushPacket(new byte[] { 4, -5, 6, 0, -1 }, createTime(sampleOffset), false, streamID);
		sampleOffset += 5;
		writer.close();
	}

	private PrecisionUTCTime createTime(int sampleOffset) {
		double timeSec = sampleOffset * 0.1;
		return new PrecisionUTCTime(TCM_CPU.value, TCS_VALID.value, 0, (int) timeSec, timeSec - (int) timeSec);
	}

	@Override
	public void assertData(Data data) {
		short[] array = data.castI(true);
		try {
			Assert.assertArrayEquals(EXPECTED_OCTETS_WIDENED, array);
		} finally {
			data.uncast(array, false);
		}
	}

	@Override
	public void assertData(ByteBuffer buffer) {
		Assert.assertArrayEquals(EXPECTED_OCTETS_BIO, buffer.array());
	}

}
