/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public class DebugLevelResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		final String debugLevel = config.getAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, (String) null);
		if (debugLevel != null) {
			int level;
			try {
				level = Integer.valueOf(debugLevel);
			} catch (final NumberFormatException e) {
				if ("Fatal".equalsIgnoreCase(debugLevel)) {
					level = 0;
				} else if ("Error".equalsIgnoreCase(debugLevel)) {
					level = 1;
				} else if ("Warn".equalsIgnoreCase(debugLevel)) {
					level = 2;
				} else if ("Info".equalsIgnoreCase(debugLevel)) {
					level = 3;
				} else if ("Debug".equalsIgnoreCase(debugLevel)) {
					level = 4;
				} else {
					level = 5;
				}
			}
			return "DEBUG_LEVEL " + String.valueOf(level);
		}
		return null;
	}

}
