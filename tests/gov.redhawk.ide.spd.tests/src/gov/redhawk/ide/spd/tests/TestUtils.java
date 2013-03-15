/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 4852 $
 */

package gov.redhawk.ide.spd.tests;

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
	 * @throws IOException The file can't be written to disk
	 */
	public static File createFile(String content, String extension) throws IOException {
		File xmlFile = File.createTempFile(TestUtils.class.getPackage().getName(), extension);
		xmlFile.deleteOnExit();
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(xmlFile);
			fileWriter.write(content);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
		return xmlFile;
	}

}
