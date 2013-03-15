/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 2.0
 */
public final class SadLauncherUtil {
	private SadLauncherUtil() {

	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getImplementationMap(final ILaunchConfiguration config) throws CoreException {
		if (config == null) {
			return Collections.emptyMap();
		}
		return config.getAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, Collections.emptyMap());
	}
}
