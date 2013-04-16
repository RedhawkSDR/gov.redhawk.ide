/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

/**
 * @since 2.0
 */
public interface ILaunchConfigurationFactory {

	boolean supports(SoftPkg spd, String implId);

	ILaunchConfigurationWorkingCopy createLaunchConfiguration(String name, String implId, SoftPkg spd) throws CoreException;

}
