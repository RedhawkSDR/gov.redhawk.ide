/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 5722 $
 */
package gov.redhawk.ide.dcd.tests;

import gov.redhawk.ide.dcd.generator.newdevice.tests.AllDeviceGeneratorTests;
import gov.redhawk.ide.dcd.generator.newnode.tests.AllNodeGeneratorTests;
import gov.redhawk.ide.dcd.generator.newservice.tests.AllServiceGeneratorTests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * 
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ AllDeviceGeneratorTests.class, AllNodeGeneratorTests.class, AllServiceGeneratorTests.class })
public class AllDcdGeneratorTests {

}
