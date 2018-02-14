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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.SnapshotActivator;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ide.snapshot.writer.IDataWriterSettings;

public class BinSriDataWriterTest extends AbstractBitTest {

	private IDataWriter writer;
	private File file;

	@Before
	public void before() throws CoreException, IOException {
		IDataWriterDesc desc = SnapshotActivator.getDataReceiverRegistry().getReceiverDesc("gov.redhawk.ide.snapshot.writer.BinSriDataWriter");
		IDataWriterSettings settings = desc.createWriterSettings();
		settings.setType(BulkIOType.BIT);
		file = File.createTempFile(BinSriDataWriterTest.class.getSimpleName(), ".bin");
		settings.setDestination(file);
		writer = desc.createWriter();
		writer.setSettings(settings);
	}

	@Test
	public void writeBits() throws IOException {
		write(writer);

		// Open the data file
		try (FileInputStream is = new FileInputStream(file)) {
			// Try to read all the bytes back
			byte[] buffer = new byte[(int) Math.ceil(TOTAL_SAMPLES / 8.0)];
			int bytesRead = is.read(buffer, 0, buffer.length);
			Assert.assertEquals(buffer.length, bytesRead);

			// Verify the binary data matches
			Assert.assertArrayEquals(EXPECTED_BITS, buffer);

			// We shouldn't be able to read any more data
			Assert.assertEquals(-1, is.read(new byte[1], 0, 1));
		}
	}

	@After
	public void after() {
		if (file != null) {
			file.delete();
			file = null;
		}
	}
}
