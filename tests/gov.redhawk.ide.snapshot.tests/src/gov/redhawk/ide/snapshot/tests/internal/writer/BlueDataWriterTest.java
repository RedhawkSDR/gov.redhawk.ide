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
package gov.redhawk.ide.snapshot.tests.internal.writer;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.SnapshotActivator;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.BitTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.DoubleTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.FloatTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.ITestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.LongLongTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.LongTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.OctetTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.ShortTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.ULongLongTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.ULongTestHelper;
import gov.redhawk.ide.snapshot.tests.internal.writer.helpers.UShortTestHelper;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;
import nxm.sys.inc.Units;
import nxm.sys.lib.Data;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;

public class BlueDataWriterTest {

	private File midasFile;

	@Before
	public void before() throws CoreException, IOException {
		// Output file
		midasFile = File.createTempFile(BlueDataWriterTest.class.getSimpleName(), ".tmp");
	}

	private IDataWriter createWriter(BulkIOType type) throws CoreException {
		IDataWriterDesc desc = SnapshotActivator.getDataReceiverRegistry().getReceiverDesc("gov.redhawk.ide.snapshot.writer.BlueDataWriter");
		IDataWriterSettings settings = desc.createWriterSettings();
		settings.setType(type);
		settings.setDestination(midasFile);

		IDataWriter writer = desc.createWriter();
		writer.setSettings(settings);
		return writer;
	}

	@Test
	public void writeBits() throws CoreException, IOException {
		ITestHelper helper = new BitTestHelper();

		// Write the data
		IDataWriter writer = createWriter(helper.getType());
		helper.write(writer);

		// Verify the BLUE file contents
		DataFile df = open();
		try {
			// Verify basic info
			assertSampleInfo(helper, df);
			assertX(0, 0.1, Units.TIME, df);
			assertY(0, 0, Units.NONE, 0, df);

			// Try to read all the bytes back. Two issues we have to work around:
			// 1. We must read the data as a byte array; if we use the Data class then NeXtMidas ends up messing up the
			// the final bits since the byte they are in isn't fully packed (this is a NeXtMidas-specific problem).
			// 2. NeXtMidas will report that it read 1 less byte than it actually did, probably because it is rounding
			// down to the nearest whole byte
			byte[] buffer = new byte[(int) Math.ceil(helper.getSampleCount() / 8.0)];
			int bytesRead = df.read(buffer, 0, buffer.length);
			Assert.assertEquals(buffer.length - 1, bytesRead);

			// Verify the binary data matches
			Assert.assertArrayEquals(BitTestHelper.EXPECTED_BITS, buffer);

			// We shouldn't be able to read any more data
			Assert.assertEquals(0, df.read(new byte[1], 0, 1));
		} finally {
			df.close();
		}
	}

	@Test
	public void writeFloats() throws CoreException, IOException {
		ITestHelper helper = new FloatTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeDoubles() throws CoreException, IOException {
		ITestHelper helper = new DoubleTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeLongs() throws CoreException, IOException {
		ITestHelper helper = new LongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeLongLongs() throws CoreException, IOException {
		ITestHelper helper = new LongLongTestHelper();
		commonTest(helper);
	}

	@Test
	@Ignore("Need to correct BulkIOType.OCTET to unsigned, Midas type 'I' before this test can be used")
	public void writeOctets() throws CoreException, IOException {
		ITestHelper helper = new OctetTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeShorts() throws CoreException, IOException {
		ITestHelper helper = new ShortTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeULongs() throws CoreException, IOException {
		ITestHelper helper = new ULongTestHelper();
		commonTest(helper);
	}

	/**
	 * X-Midas has no ability to store 64-bit unsigned integers, and no larger size that can hold them.
	 * @throws CoreException
	 * @throws IOException
	 */
	@Test(expected = IOException.class)
	public void writeULongLongs() throws CoreException, IOException {
		ITestHelper helper = new ULongLongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeUShorts() throws CoreException, IOException {
		ITestHelper helper = new UShortTestHelper();
		commonTest(helper);
	}

	private void commonTest(ITestHelper helper) throws CoreException, IOException {
		// Write the data
		IDataWriter writer = createWriter(helper.getType());
		helper.write(writer);

		// Verify the BLUE file contents
		DataFile df = open();
		try {
			// Verify meta data
			assertSampleInfo(helper, df);
			assertX(0, 0.1, Units.TIME, df);
			assertY(0, 0, Units.NONE, 0, df);

			// Verify data
			Data data = df.getDataBuffer(helper.getSampleCount());
			Assert.assertEquals(helper.getSampleCount(), df.read(data));
			helper.assertData(data);

			// We shouldn't be able to read any more data
			Assert.assertEquals(0, df.avail(), 0);
		} finally {
			df.close();
		}
	}

	/**
	 * @return The file opened with NeXtMidas
	 */
	private DataFile open() {
		Midas m = NeXtMidas.getGlobalInstance().getMidasContext();
		DataFile df = new DataFile(m, midasFile.getAbsolutePath());
		df.open(DataFile.INPUT);
		return df;
	}

	private void assertSampleInfo(ITestHelper helper, DataFile actual) {
		Assert.assertEquals("S" + helper.getType().getMidasType(), actual.getFormat());

		// Size in bytes
		if (helper.getType() == BulkIOType.BIT) {
			Assert.assertEquals(helper.getSampleCount() / 8.0, actual.getDataSize(), 0);
		} else {
			int upcastFactor = helper.getType().isUnsigned() ? 2 : 1;
			Assert.assertEquals(helper.getSampleCount() * helper.getType().getBytePerAtom() * upcastFactor, actual.getDataSize(), 0);
		}

		// Sample count
		Assert.assertEquals(helper.getSampleCount(), actual.getSize(), 0);
	}

	private void assertX(int xstart, double xdelta, int xunits, DataFile actual) {
		Assert.assertEquals(xstart, actual.getXStart(), 0);
		Assert.assertEquals(xdelta, actual.getXDelta(), 0);
		Assert.assertEquals(xunits, actual.getXUnits());
	}

	private void assertY(int ystart, int ydelta, int yunits, int subsize, DataFile actual) {
		Assert.assertEquals(ystart, actual.getYStart(), 0);
		Assert.assertEquals(ydelta, actual.getYDelta(), 0);
		Assert.assertEquals(yunits, actual.getYUnits());
		Assert.assertEquals(subsize, actual.getFrameSize());
	}

	@After
	public void after() {
		if (midasFile != null) {
			midasFile.delete();
			midasFile = null;
		}
	}
}
