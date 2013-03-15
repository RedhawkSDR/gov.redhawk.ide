/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5722 $
 */

package gov.redhawk.ide.dcd.generator.newdevice.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { DeviceProjectCreatorTest.class, PrfFileTemplateTest.class, ScdFileTemplateTest.class, SpdFileTemplateTest.class, TestFileTemplateTest.class })
public class AllDeviceGeneratorTests {
}
