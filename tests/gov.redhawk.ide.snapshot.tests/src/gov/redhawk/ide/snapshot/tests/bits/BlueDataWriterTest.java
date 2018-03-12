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
import nxm.sys.inc.Units;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;

public class BlueDataWriterTest extends AbstractBitTest {

	private IDataWriter writer;
	private File file;

	@Before
	public void before() throws CoreException, IOException {
		IDataWriterDesc desc = SnapshotActivator.getDataReceiverRegistry().getReceiverDesc("gov.redhawk.ide.snapshot.writer.BlueDataWriter");
		IDataWriterSettings settings = desc.createWriterSettings();
		settings.setType(BulkIOType.BIT);
		file = File.createTempFile(BlueDataWriterTest.class.getSimpleName(), ".tmp");
		settings.setDestination(file);
		writer = desc.createWriter();
		writer.setSettings(settings);
	}

	@Test
	public void writeBits() throws IOException {
		write(writer);

		// Open the file with NeXtMidas
		Midas m = NeXtMidas.getGlobalInstance().getMidasContext();
		DataFile df = new DataFile(m, file.getAbsolutePath());
		df.open(DataFile.INPUT);

		// Verify basic info
		Assert.assertEquals("SP", df.getFormat());
		Assert.assertEquals(0, df.getXStart(), 0);
		Assert.assertEquals(0.1, df.getXDelta(), 0);
		Assert.assertEquals(Units.TIME, df.getXUnits());
		Assert.assertEquals(0, df.getYStart(), 0);
		Assert.assertEquals(0, df.getYDelta(), 0);
		Assert.assertEquals(Units.NONE, df.getYUnits());
		Assert.assertEquals(0, df.getFrameSize());
		Assert.assertEquals(TOTAL_SAMPLES / 8.0, df.getDataSize(), 0);
		Assert.assertEquals(TOTAL_SAMPLES, df.getSize(), 0);

		// Try to read all the bytes back. Two issues we have to work around:
		// 1. We must read the data as a byte array; if we use the Data class then NeXtMidas ends up messing up the
		// the final bits since the byte they are in isn't fully packed (this is a NeXtMidas-specific problem).
		// 2. NeXtMidas will report that it read 1 less byte than it actually did, probably because it is rounding
		// down to the nearest whole byte
		byte[] buffer = new byte[(int) Math.ceil(TOTAL_SAMPLES / 8.0)];
		int bytesRead = df.read(buffer, 0, buffer.length);
		Assert.assertEquals(buffer.length - 1, bytesRead);

		// Verify the binary data matches
		Assert.assertArrayEquals(EXPECTED_BITS, buffer);

		// We shouldn't be able to read any more data
		Assert.assertEquals(0, df.read(new byte[1], 0, 1));
	}

	@After
	public void after() {
		if (file != null) {
			file.delete();
			file = null;
		}
	}
}
