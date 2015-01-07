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
package gov.redhawk.ide.debug.internal.variables.tests;

import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.ILaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.ILauncherVariableDesc;
import gov.redhawk.ide.debug.ILauncherVariableRegistry;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import junit.framework.TestCase;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class ExecParamResolverTest extends TestCase {

	protected ILauncherVariableDesc fixture = null;

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";

	protected void setUp() throws Exception {
		ILauncherVariableRegistry registry = ScaDebugPlugin.getInstance().getLauncherVariableRegistry();
		fixture = registry.getDesc("EXEC_PARAMS");
	}

	/**
	 * Ensure that exec params get expanded when launching a component
	 */
	public void testIDE1066() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/execparam/execparam.spd.xml";
		final String IMPL_NAME = "cpp";

		String arg = null;

		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/" + EXEC_PARAM_SPD_PATH, true).appendFragment(SoftPkg.EOBJECT_PATH);
		SoftPkg softPkg = (SoftPkg) resourceSet.getEObject(spdUri, true);

		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(softPkg, IMPL_NAME);
		ILaunchConfiguration launchConfig = factory.createLaunchConfiguration("test", IMPL_NAME, softPkg);

		ILaunch launch = new Launch(launchConfig, "debug", null);

		String execParams = fixture.resolveValue(arg, softPkg, launch, launchConfig);
		assertNotNull(execParams);
		assertTrue("Didn't find exec param 'with_value'", execParams.contains("with_value \"my string\""));
		assertTrue("Didn't find exec param 'with_value_config_too'", execParams.contains("with_value_config_too \"my other value\""));
	}

}
