/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 4203 $
 */
package gov.redhawk.ide.spd.ui.tests;

import gov.redhawk.ide.spd.internal.ui.handlers.AddPortHandlerTests;
import gov.redhawk.ide.spd.internal.ui.handlers.EditPortHandlerTests;
import gov.redhawk.ide.spd.internal.ui.handlers.RemovePortsHandlerTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ AddPortHandlerTests.class, RemovePortsHandlerTests.class, EditPortHandlerTests.class })
public class SpdUiTestSuite {
}
