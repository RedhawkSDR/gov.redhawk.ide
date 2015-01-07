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

package gov.redhawk.ide.dcd.tests;

import gov.redhawk.sca.efs.ScaFileSystemPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestUtils {

	private TestUtils() {
	}

	/**
	 * Writes content to a temporary file (deleted when the JVM exits) and
	 * returns the associated File object.
	 * 
	 * @param content The content to write.
	 * @param extension The extension of the temporary file (e.g. ".scd.xml")
	 * @return The temporary file created.
	 * @throws IOException
	 */
	public static File createFile(String content, String extension) throws IOException {
		File tempDir = ScaFileSystemPlugin.getDefault().getTempDirectory();
		File file = File.createTempFile(TestUtils.class.getPackage().getName(), extension, tempDir);
		file.deleteOnExit();
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.write(content);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
		return file;
	}

}
