/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.debug.internal.variables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

import gov.redhawk.ide.debug.SpdLauncherUtil;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides the command-line arguments for a {@link SoftPkg}'s exec param properties.
 */
public class ExecParamResolver extends AbstractLauncherResolver {

	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {

		List<ScaSimpleProperty> simples = getExecParams(config, spd, impl);
		Map<String, Object> execParams = new HashMap<>();
		for (ScaSimpleProperty simple : simples) {
			execParams.put(simple.getId(), simple.getValue());
		}
		final String retVal = SpdLauncherUtil.createExecParamString(execParams);
		if (retVal != null && retVal.trim().length() != 0) {
			return retVal;
		}
		return null;
	}

	public static List<ScaSimpleProperty> getExecParams(final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl) throws CoreException {
		final List<ScaSimpleProperty> execParams = new ArrayList<>();

		if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			final ScaComponent tmp = ScaFactory.eINSTANCE.createScaComponent();
			tmp.setProfileObj(spd);

			for (final ScaAbstractProperty< ? > prop : tmp.fetchProperties(null)) {
				prop.setIgnoreRemoteSet(true);
			}

			ScaLaunchConfigurationUtil.loadProperties(config, tmp);
			for (final ScaAbstractProperty< ? > prop : tmp.getProperties()) {
				if (prop instanceof ScaSimpleProperty && prop.getDefinition() != null) {
					if ((prop.getDefinition().isKind(PropertyConfigurationType.PROPERTY) && ((Simple) prop.getDefinition()).isCommandLine())
						|| prop.getDefinition().isKind(PropertyConfigurationType.EXECPARAM)) {
						execParams.add((ScaSimpleProperty) prop);
					}
				}
			}
		}
		return execParams;
	}

}
