/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 3.0
 * 
 */
public interface ILaunchConfigurationFactoryRegistry {
	ILaunchConfigurationFactory getFactory(SoftPkg spd, String implID);
}
