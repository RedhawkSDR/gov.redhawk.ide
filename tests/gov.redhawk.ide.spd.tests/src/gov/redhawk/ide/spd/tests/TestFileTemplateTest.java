/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 6580 $
 */

package gov.redhawk.ide.spd.tests;

import gov.redhawk.ide.spd.generator.newcomponent.GeneratorArgs;
import gov.redhawk.ide.spd.generator.newcomponent.TestFileTemplate;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * A class to test {@link TestFileTemplateTest}.
 */
public class TestFileTemplateTest {

	/**
	 * Tests generating the test file
	 * 
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		// Generate XML using the template
		final GeneratorArgs args = new GeneratorArgs();
		args.setSoftPkgFile("test.spd.xml");
		args.setProjectName("TestFileTemplateTestProject");
		TestFileTemplate testTemplate = TestFileTemplate.create(null);
		String testContent = testTemplate.generate(args);

		// Create an XML file with the content
		Assert.assertTrue(testContent.contains("TestFileTemplateTestProject"));
		
	}
}
