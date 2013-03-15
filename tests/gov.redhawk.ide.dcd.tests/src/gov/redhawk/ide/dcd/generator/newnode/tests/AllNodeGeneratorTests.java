/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5722 $
 */

package gov.redhawk.ide.dcd.generator.newnode.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { DcdFileTemplateTest.class, ImplPrfFileTemplateTest.class, NodeProjectCreatorTest.class, PrfFileTemplateTest.class, ScdFileTemplateTest.class, SpdFileTemplateTest.class })
public class AllNodeGeneratorTests {
}
