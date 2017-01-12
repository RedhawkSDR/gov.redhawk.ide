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
package gov.redhawk.ide.debug.internal.variables.tests;

import java.io.IOException;

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

/**
 * Tests the PROFILE_NAME that is constructed for various URIs.
 */
public class ProfileNameVariableResolverTest {

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";

	private ILauncherVariableDesc fixture = null;
	private SoftPkg softPkg = null;

	@Before
	public void setUp() throws Exception {
		ILauncherVariableRegistry registry = ScaDebugPlugin.getInstance().getLauncherVariableRegistry();
		fixture = registry.getDesc("PROFILE_NAME");

		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/resources/simplecomponent/simplecomponent.spd.xml", true).appendFragment(
			SoftPkg.EOBJECT_PATH);
		softPkg = (SoftPkg) resourceSet.getEObject(spdUri, true);
	}

	private String common() throws CoreException {
		// Create a launch configuration
		ILaunchConfigurationFactoryRegistry registry = ScaDebugPlugin.getInstance().getLaunchConfigurationFactoryRegistry();
		ILaunchConfigurationFactory factory = registry.getFactory(softPkg, "python");
		ILaunchConfiguration launchConfig = factory.createLaunchConfiguration("test", "python", softPkg);

		// Create a launch and then resolve exec params
		ILaunch launch = new Launch(launchConfig, "debug", null);

		return fixture.resolveValue(null, softPkg, launch, launchConfig);
	}

	/**
	 * Test a file URI's profile name
	 * @throws CoreException
	 * @throws IOException
	 */
	@Test
	public void file() throws CoreException, IOException {
		// Switch the URI to a file URI
		final String ABSOLUTE_FILE_PATH = "/foo/a/b/c/c.spd.xml";
		softPkg.eResource().setURI(URI.createFileURI(ABSOLUTE_FILE_PATH));
		Assert.assertEquals(ABSOLUTE_FILE_PATH, common());
	}
}
