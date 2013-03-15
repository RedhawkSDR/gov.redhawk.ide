/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * 
 */
public class NamingContextIORResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		final String namingContext;
		final ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
		switch (type) {
		case DEVICE:
		case EVENT_SERVICE:
		case SERVICE: {
			final NotifyingNamingContext nc = getDevNamingContext(spd);
			namingContext = nc.getNamingContext().toString();
			break;
		}
		default: {
			final NotifyingNamingContext nc = getDomNamingContext(spd);
			namingContext = nc.getNamingContext().toString();
			break;
		}
		}
		return namingContext;
	}

	private NotifyingNamingContext getDomNamingContext(final SoftPkg spd) {
		return ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform().getNamingContext().getResourceContext(spd.eResource().getURI());
	}

	private NotifyingNamingContext getDevNamingContext(final SoftPkg spd) {
		return ScaDebugPlugin.getInstance().getLocalSca().getSandboxDeviceManager().getNamingContext().getResourceContext(spd.eResource().getURI());
	}
}
