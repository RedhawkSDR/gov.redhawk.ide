/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ExecParamResolver extends AbstractLauncherResolver {

	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			final ScaComponent tmp = ScaFactory.eINSTANCE.createScaComponent();
			tmp.setProfileObj(spd);
			for (final ScaAbstractProperty< ? > prop : tmp.fetchProperties(null)) {
				prop.setIgnoreRemoteSet(true);
			}
			ScaLaunchConfigurationUtil.loadProperties(config, tmp);

			final Map<String, Object> execParams = new HashMap<String, Object>();
			for (final ScaAbstractProperty< ? > prop : tmp.getProperties()) {
				if (prop instanceof ScaSimpleProperty && !prop.isDefaultValue() && prop.getDefinition().isKind(PropertyConfigurationType.EXECPARAM)) {
					final ScaSimpleProperty simple = (ScaSimpleProperty) prop;
					execParams.put(simple.getId(), simple.getValue());
				}
			}
			final String retVal = SpdLauncherUtil.createExecParamString(execParams);
			if (retVal != null && retVal.trim().length() != 0) {
				return retVal;
			}
		}
		return null;
	}

}
