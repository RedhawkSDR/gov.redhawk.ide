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

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.StringContains.containsString;

import java.util.List;

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
import gov.redhawk.ide.debug.internal.variables.ExecParamResolver;
import gov.redhawk.model.sca.ScaSimpleProperty;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.spd.Implementation;
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
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/" + spdPath, true);
		SoftPkg softPkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdUri, true));

		// Create a launch configuration
		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(softPkg, implName);
		ILaunchConfiguration launchConfig = factory.createLaunchConfiguration("test", implName, softPkg);

		// Create a launch and then resolve exec params
		ILaunch launch = new Launch(launchConfig, "debug", null);
		return fixture.resolveValue(null, softPkg, launch, launchConfig);
	}

	/**
	 * IDE-1006 Ensure that non-null 'execparam' kind properties are passed on the command line to the component
	 */
	@Test
	public void execparamsWithValue() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/execparam/execparam.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		for (String substring : new String[] { //
			"boolean_withvalue \"true\"", "boolean_withvalue_configtoo \"true\"", //
			"char_withvalue \"a\"", "char_withvalue_configtoo \"a\"", //
			"double_withvalue \"1250.0\"", "double_withvalue_configtoo \"1250.0\"", //
			"float_withvalue \"25000.0\"", "float_withvalue_configtoo \"25000.0\"", //
			"short_withvalue \"-1\"", "short_withvalue_configtoo \"-1\"", //
			"long_withvalue \"-2\"", "long_withvalue_configtoo \"-2\"", //
			"longlong_withvalue \"-3\"", "longlong_withvalue_configtoo \"-3\"", //
			"octet_withvalue \"1\"", "octet_withvalue_configtoo \"1\"", //
			"string_withvalue \"abc\"", "string_withvalue_configtoo \"abc\"", //
			"ulong_withvalue \"2\"", "ulong_withvalue_configtoo \"2\"", //
			"ulonglong_withvalue \"3\"", "ulonglong_withvalue_configtoo \"3\"", //
			"ushort_withvalue \"4\"", "ushort_withvalue_configtoo \"4\"" }) {
			Assert.assertThat(execParams, containsString(substring));
		}
	}

	/**
	 * IDE-1006 Ensure that null 'execparam' kind properties are not passed on the command line to the component
	 */
	@Test
	public void execparamsWithoutValue() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/execparam/execparam.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		for (String substring : new String[] { //
			"boolean_novalue", "boolean_novalue_configtoo", //
			"char_novalue", "char_novalue_configtoo", //
			"double_novalue", "double_novalue_configtoo", //
			"float_novalue", "float_novalue_configtoo", //
			"short_novalue", "short_novalue_configtoo", //
			"long_novalue", "long_novalue_configtoo", //
			"longlong_novalue", "longlong_novalue_configtoo", //
			"octet_novalue", "octet_novalue_configtoo", //
			"string_novalue", "string_novalue_configtoo", //
			"ulong_novalue", "ulong_novalue_configtoo", //
			"ulonglong_novalue", "ulonglong_novalue_configtoo", //
			"ushort_novalue", "ushort_novalue_configtoo" }) {
			Assert.assertThat(execParams, not(containsString(substring)));
		}
	}

	/**
	 * IDE-1392 Ensure that non-null 'commandline=true' 'property' kind properties are passed on the command line to
	 * the component
	 * @throws CoreException
	 */
	@Test
	public void commandlineWithValue() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/commandline/commandline.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		for (String substring : new String[] { //
			"boolean_withvalue \"true\"", //
			"char_withvalue \"a\"", //
			"double_withvalue \"1250.0\"", //
			"float_withvalue \"25000.0\"", //
			"short_withvalue \"-1\"", //
			"long_withvalue \"-2\"", //
			"longlong_withvalue \"-3\"", //
			"octet_withvalue \"1\"", //
			"string_withvalue \"abc\"", //
			"ulong_withvalue \"2\"", //
			"ulonglong_withvalue \"3\"", //
			"ushort_withvalue \"4\"" }) {
			Assert.assertThat(execParams, containsString(substring));
		}
	}

	/**
	 * IDE-1392 Ensure that null 'commandline=true' 'property' kind proeprties are not passed on the command line to
	 * the component
	 * @throws CoreException
	 */
	@Test
	public void commandlineWithoutValue() throws CoreException {
		final String EXEC_PARAM_SPD_PATH = "resources/commandline/commandline.spd.xml";
		final String IMPL_NAME = "cpp";
		String execParams = common(EXEC_PARAM_SPD_PATH, IMPL_NAME);

		Assert.assertNotNull(execParams);
		for (String substring : new String[] { //
			"boolean_novalue", //
			"char_novalue", //
			"double_novalue", //
			"float_novalue", //
			"short_novalue", //
			"long_novalue", //
			"longlong_novalue", //
			"octet_novalue", //
			"string_novalue", //
			"ulong_novalue", //
			"ulonglong_novalue", //
			"ushort_novalue" }) {
			Assert.assertThat(execParams, not(containsString(substring)));
		}
	}

	/**
	 * IDE-1827 - Ensure expected number of execParams are returned
	 * @throws CoreException
	 */
	@Test
	public void getExecParamsTest() throws CoreException {
		final String SHARED_ADDRESS_COMP_SPD_PATH = "testFiles/sdr/dom/components/SharedAddressComponent/SharedAddyComp.spd.xml";
		final String IMPL_NAME = "cpp";

		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/" + SHARED_ADDRESS_COMP_SPD_PATH, true);
		SoftPkg softPkg = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdUri, true));

		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(softPkg, IMPL_NAME);
		ILaunchConfiguration launchConfig = factory.createLaunchConfiguration("test", IMPL_NAME, softPkg);

		Implementation impl = softPkg.getImplementation(IMPL_NAME);

		List<ScaSimpleProperty> props = ExecParamResolver.getExecParams(launchConfig, softPkg, impl);
		Assert.assertEquals(6, props.size());
		for (ScaSimpleProperty prop : props) {
			if (prop.getDefinition().isKind(PropertyConfigurationType.PROPERTY)) {
				Assert.assertTrue(prop.getId().matches("cmdline.*"));
			} else {
				Assert.assertTrue(prop.getId().matches("execparam.*"));
			}
		}
	}
}
