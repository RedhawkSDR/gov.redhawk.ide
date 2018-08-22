/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.tests.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import gov.redhawk.ide.sdr.preferences.IdeSdrPreferenceConstants;

public class SdrRootResolverTest {

	private IDynamicVariable variable;

	@Before
	public void before() throws Exception {
		variable = VariablesPlugin.getDefault().getStringVariableManager().getDynamicVariable("SdrRoot");
	}

	@Test
	public void resolve() throws CoreException {
		// If we set the preference value, we should get that value back from the variable resolver
		InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID).put(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE, "/foo");
		Assert.assertEquals("/foo", variable.getValue(null));
	}

	@After
	public void after() throws CoreException {
		InstanceScope.INSTANCE.getNode(IdeSdrActivator.PLUGIN_ID).remove(IdeSdrPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE);
	}
}
