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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.ide.debug.ILaunchConfigurationFactory;
import gov.redhawk.ide.debug.ILaunchConfigurationFactoryRegistry;
import gov.redhawk.ide.debug.ILauncherVariableDesc;
import gov.redhawk.ide.debug.ILauncherVariableRegistry;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class ExecParamResolverTest {

	protected ILauncherVariableDesc fixture = null; // SUPPRESS CHECKSTYLE VisibilityModifier

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";

	@Before
	public void setUp() throws Exception {
		ILauncherVariableRegistry registry = ScaDebugPlugin.getInstance().getLauncherVariableRegistry();
		fixture = registry.getDesc("EXEC_PARAMS");
	}

	private String common(String spdPath, String implName) throws CoreException {
		// Load SPD
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/" + spdPath, true).appendFragment(SoftPkg.EOBJECT_PATH);
		SoftPkg softPkg = (SoftPkg) resourceSet.getEObject(spdUri, true);

		// Create a launch configuration
		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(softPkg, implName);
		ILaunchConfiguration launchConfig = factory.createLaunchConfiguration("test", implName, softPkg);

		// Create a launch and then resolve exec params
		ILaunch launch = new Launch(launchConfig, "debug", null);
		return fixture.resolveValue(null, softPkg, launch, launchConfig);
	}

	/**
	 * Ensure that non-null execparams get expanded when launching a component.
	 * IDE-1006
	 */
	@Test
	public void execparams_expanded() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/execparam/execparam.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		Assert.assertTrue("Didn't find exec param 'with_value'", execParams.contains("with_value \"my string\""));
		Assert.assertTrue("Didn't find exec param 'with_value_config_too'", execParams.contains("with_value_config_too \"my other value\""));
		Assert.assertFalse("Property 'no_value' should not have been passed", execParams.contains("no_value"));
		Assert.assertFalse("Property 'no_value_config_too' should not have been passed", execParams.contains("no_value_config_too"));
	}

	/**
	 * Ensure that non-null commandline 'property' kinds get expanded when launching a component.
	 * IDE-1392
	 * @throws CoreException
	 */
	@Test
	public void commandline_properties_expanded() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/commandline/commandline.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		Assert.assertTrue("Didn't find exec param 'commandline_property_with_value'",
			execParams.contains("commandline_property_with_value \"commandline attribute\""));
		Assert.assertFalse("Property 'commandline_property_no_value' should not have been passed", execParams.contains("commandline_property_no_value"));
	}
}
