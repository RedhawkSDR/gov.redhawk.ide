/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5735 $
 */

package gov.redhawk.ide.spd.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { ComponentProjectCreatorTest.class, PrfFileTemplateTest.class, ScdFileTemplateTest.class, SpdFileTemplateTest.class, TestFileTemplateTest.class })
public class AllComponentGeneratorTests {
}
