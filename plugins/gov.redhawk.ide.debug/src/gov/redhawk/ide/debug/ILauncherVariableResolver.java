/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 3.0
 */
public interface ILauncherVariableResolver extends IDynamicVariableResolver {
	String resolveValue(String arg, SoftPkg spd, ILaunch launch, ILaunchConfiguration config) throws CoreException;
}
