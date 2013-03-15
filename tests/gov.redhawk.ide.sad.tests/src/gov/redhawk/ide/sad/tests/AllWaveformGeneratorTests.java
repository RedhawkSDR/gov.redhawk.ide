/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5733 $
 */

package gov.redhawk.ide.sad.tests;

import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreatorTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { SadFileTemplateTest.class, WaveformProjectCreatorTest.class })
public class AllWaveformGeneratorTests {
}
