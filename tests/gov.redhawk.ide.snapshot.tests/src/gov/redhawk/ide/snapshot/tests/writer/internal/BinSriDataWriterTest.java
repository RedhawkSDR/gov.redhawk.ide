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
package gov.redhawk.ide.snapshot.tests.writer.internal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.SnapshotActivator;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.BitTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.DoubleTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.FloatTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.ITestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.LongLongTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.LongTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.OctetTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.ShortTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.ULongLongTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.ULongTestHelper;
import gov.redhawk.ide.snapshot.tests.writer.internal.helpers.UShortTestHelper;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;

public class BinSriDataWriterTest {

	private File dataFile;
	private File sriFile;

	@Before
	public void before() throws IOException {
		// Output files
		dataFile = File.createTempFile(BinSriDataWriterTest.class.getSimpleName(), ".bin");
		int lastDot = dataFile.getAbsolutePath().lastIndexOf('.');
		sriFile = new File(dataFile.getAbsolutePath().substring(0, lastDot) + ".sri");
	}

	private IDataWriter createWriter(BulkIOType type) throws CoreException {
		IDataWriterDesc desc = SnapshotActivator.getDataReceiverRegistry().getReceiverDesc("gov.redhawk.ide.snapshot.writer.BinSriDataWriter");
		IDataWriterSettings settings = desc.createWriterSettings();
		settings.setType(type);
		settings.setDestination(dataFile);

		IDataWriter writer = desc.createWriter();
		writer.setSettings(settings);
		return writer;
	}

	@Test
	public void writeBits() throws IOException, CoreException {
		ITestHelper helper = new BitTestHelper();

		// Write the data
		IDataWriter writer = createWriter(helper.getType());
		helper.write(writer);

		// TODO: Verify metadata

		// Verify the binary file contents
		try (SeekableByteChannel chan = Files.newByteChannel(dataFile.toPath())) {
			// Verify data
			ByteBuffer buffer = ByteBuffer.allocate((int) Math.ceil(helper.getSampleCount() / 8.0));
			Assert.assertEquals(buffer.limit(), chan.read(buffer));
			helper.assertData(buffer);

			// We shouldn't be able to read any more data
			buffer.position(0);
			Assert.assertEquals(-1, chan.read(buffer));
		}
	}

	@Test
	public void writeFloats() throws IOException, CoreException {
		ITestHelper helper = new FloatTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeDoubles() throws IOException, CoreException {
		ITestHelper helper = new DoubleTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeLongs() throws IOException, CoreException {
		ITestHelper helper = new LongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeLongLongs() throws IOException, CoreException {
		ITestHelper helper = new LongLongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeOctets() throws CoreException, IOException {
		ITestHelper helper = new OctetTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeShorts() throws IOException, CoreException {
		ITestHelper helper = new ShortTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeULongs() throws CoreException, IOException {
		ITestHelper helper = new ULongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeULongLongs() throws CoreException, IOException {
		ITestHelper helper = new ULongLongTestHelper();
		commonTest(helper);
	}

	@Test
	public void writeUShorts() throws IOException, CoreException {
		ITestHelper helper = new UShortTestHelper();
		commonTest(helper);
	}

	private void commonTest(ITestHelper helper) throws CoreException, IOException {
		// Write the data
		IDataWriter writer = createWriter(helper.getType());
		helper.write(writer);

		// TODO: Verify metadata

		// Verify the binary file contents
		try (SeekableByteChannel chan = Files.newByteChannel(dataFile.toPath())) {
			// Verify data
			ByteBuffer buffer = ByteBuffer.allocate(helper.getSampleCount() * helper.getType().getBytePerAtom());
			buffer.order(ByteOrder.nativeOrder());
			Assert.assertEquals(buffer.limit(), chan.read(buffer));
			helper.assertData(buffer);

			// We shouldn't be able to read any more data
			buffer.position(0);
			Assert.assertEquals(-1, chan.read(buffer));
		}
	}

	@After
	public void after() {
		if (dataFile != null && dataFile.exists()) {
			dataFile.delete();
			dataFile = null;
		}
		if (sriFile != null && sriFile.exists()) {
			sriFile.delete();
			sriFile = null;
		}
	}
}
