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

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.writer.IDataWriter;
import nxm.sys.lib.Data;

public interface ITestHelper {

	/**
	 * @return The BulkIO data type for the test.
	 */
	BulkIOType getType();

	/**
	 * @return The number of test samples that are written in {@link #write(IDataWriter)}.
	 */
	int getSampleCount();

	/**
	 * Write test data with the writer
	 * @param writer
	 * @throws IOException
	 */
	void write(IDataWriter writer) throws IOException;

	/**
	 * Assert the data in the NeXtMidas data buffer matches the test data.
	 * @param data
	 */
	void assertData(Data data);

	/**
	 * Assert the data in the byte buffer matches the test data.
	 * @param buffer
	 */
	void assertData(ByteBuffer buffer);

}
