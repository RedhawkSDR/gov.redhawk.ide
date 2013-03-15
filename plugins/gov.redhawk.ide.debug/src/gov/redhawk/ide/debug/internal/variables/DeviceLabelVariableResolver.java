/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.model.sca.ScaDevice;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public class DeviceLabelVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		return getDeviceUniqueName(spd);
	}

	private String getDeviceUniqueName(final SoftPkg spd) {
		final LocalScaDeviceManager devMgr = ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager();
		String name;
		for (int i = 1; true; i++) {
			name = spd.getName() + "_" + i;
			boolean contains = false;
			for (final ScaDevice< ? > d : devMgr.getAllDevices()) {
				if (name.equals(d.fetchLabel(null))) {
					contains = true;
				}
			}
			if (!contains) {
				break;
			}
		}
		return name;
	}

}
