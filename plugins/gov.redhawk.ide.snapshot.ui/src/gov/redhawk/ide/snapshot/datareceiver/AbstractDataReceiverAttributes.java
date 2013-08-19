/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.snapshot.datareceiver;

import gov.redhawk.bulkio.util.BulkIOType;

import java.io.File;
import java.io.IOException;

import BULKIO.StreamSRI;

public abstract class AbstractDataReceiverAttributes implements IDataReceiverFactory {

	/**
	 * 
	 * @param file : The file to write to
	 * @param data : The data to be written to the file
	 * @param type : The BulkIOType of the port
	 * @param upcastUnsigned : whether or not unsigned data should be upcast
	 * @param sri : The data's sri
	 * @throws IOException
	 */
	public String[][] writeFile(File file, Object[] data, BulkIOType type, boolean upcastUnsigned, StreamSRI sri) throws IOException {
		IDataReceiver receiver = newInstance(file, data.length, 0, type, upcastUnsigned, CaptureMethod.NUMBER);
		receiver.writeFile(data, sri);
		String[][] outputFiles = receiver.getOutputFiles();
		receiver.dispose();
		return outputFiles;
	}
}
