/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.ILauncherVariableDesc;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * 
 */
public class NameBindingResolver extends AbstractLauncherResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		final ILauncherVariableDesc desc = ScaDebugPlugin.getInstance().getLauncherVariableRegistry().getDesc(LaunchVariables.NAMING_CONTEXT_IOR);
		return getUniqueName(spd, desc.resolveValue(null, spd, launch, config));
	}

	private String getUniqueName(final SoftPkg spd, final String namingContextIOR) {
		final NamingContextExt namingContext = NamingContextExtHelper.narrow(ScaDebugPlugin.getInstance()
		        .getLocalSca()
		        .getOrb()
		        .string_to_object(namingContextIOR));
		final String name = spd.getName();
		String retVal = name;
		for (int i = 1; true; i++) {
			try {
				namingContext.resolve_str(retVal);
				retVal = name + "_" + i;
			} catch (final NotFound e) {
				return retVal;
			} catch (final CannotProceed e) {
				throw new IllegalStateException(e);
			} catch (final InvalidName e) {
				throw new IllegalStateException(e);
			}
		}
	}

}
