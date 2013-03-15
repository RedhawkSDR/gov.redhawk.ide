/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5722 $
 */

package gov.redhawk.ide.dcd.tests;

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
		File file = File.createTempFile(TestUtils.class.getPackage().getName(), extension);
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
