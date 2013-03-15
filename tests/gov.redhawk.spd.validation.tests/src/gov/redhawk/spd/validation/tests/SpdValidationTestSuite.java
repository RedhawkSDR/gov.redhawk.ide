/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 2708 $
 */
package gov.redhawk.spd.validation.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ EntryPointContstraintTest.class, LocalFileConstraintTest.class, DependencyConstraintTest.class })
public class SpdValidationTestSuite {

}
