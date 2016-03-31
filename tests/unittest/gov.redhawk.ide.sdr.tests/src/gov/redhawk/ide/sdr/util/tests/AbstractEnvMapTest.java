/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.sdr.util.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.tests.SdrTestsUtil;
import gov.redhawk.ide.sdr.util.AbstractEnvMap;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class AbstractEnvMapTest extends AbstractEnvMap {

	/**
	 * Tests that a complex recursion happens in the correct order
	 */
	@Test
	public void getDependencyImplementations_recursion1() throws URISyntaxException, IOException, CoreException {
		SdrRoot sdr = SdrTestsUtil.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:2fc3c8c5-a984-4be7-87c5-16ff9f0d0c8f");
		Assert.assertEquals("CppComponentWithDeps", spd.getName());

		List<Implementation> impls = getDependencyImplementations(spd.getImplementation("cpp"));
		String[] expectedSpds = { "CppDepD", "CppDepDE", "CppDepA", "CppDepAC", "CppDepAB" };
		Assert.assertEquals(expectedSpds.length, impls.size());
		for (int i = 0; i < expectedSpds.length; i++) {
			Assert.assertEquals(expectedSpds[i], impls.get(i).getSoftPkg().getName());
		}
	}

	/**
	 * Tests an issue with repeated references to the same softpkg
	 */
	@Test
	public void getDependencyImplementations_recursion2() throws URISyntaxException, IOException, CoreException {
		SdrRoot sdr = SdrTestsUtil.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:ff5ee7ed-b419-4c34-8dd1-b6904a377b64");
		Assert.assertEquals("CppComponentWithDeps2", spd.getName());

		List<Implementation> impls = getDependencyImplementations(spd.getImplementation("cpp"));
		String[] expectedSpds = { "CppDepD", "CppDepDE" };
		Assert.assertEquals(expectedSpds.length, impls.size());
		for (int i = 0; i < expectedSpds.length; i++) {
			Assert.assertEquals(expectedSpds[i], impls.get(i).getSoftPkg().getName());
		}
	}

	@Override
	protected String createPath(String relativeCodePath, URI spdUri) throws CoreException {
		return null;
	}

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
	}

}
