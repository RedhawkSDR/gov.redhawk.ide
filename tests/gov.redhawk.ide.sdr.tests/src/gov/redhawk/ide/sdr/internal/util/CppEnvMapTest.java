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
package gov.redhawk.ide.sdr.internal.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.tests.SdrTests;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * IDE-1407 Ensure proper paths are used for chalkboard launches
 */
public class CppEnvMapTest {

	private static final String SUFFIX_1 = "${OssieHome}/lib:${env_var:LD_LIBRARY_PATH}";
	private static final String SUFFIX_2 = "${OssieHome}/lib64:" + SUFFIX_1;

	private CppEnvMap mapper = new CppEnvMap();

	@Test
	public void initEnv_null() throws CoreException {
		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(null, map);
		Assert.assertEquals(1, map.size());
		checkLdLibPath(map, new String[0]);
	}

	@Test
	public void initEnv_component() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:4f46ef40-8c58-47e3-904b-e725b366808b");
		Assert.assertEquals("CppComponent", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("cpp"), map);
		Assert.assertEquals(1, map.size());
		checkLdLibPath(map, new String[0]);
	}

	@Test
	public void initEnv_componentWithDeps() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:2fc3c8c5-a984-4be7-87c5-16ff9f0d0c8f");
		Assert.assertEquals("CppComponentWithDeps", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("cpp"), map);
		Assert.assertEquals(1, map.size());
		String sdrDom = getSdrDomLocation();
		String[] paths = { sdrDom + "/deps/CppDepD/cpp/lib", sdrDom + "/deps/CppDepDE/cpp/lib", sdrDom + "/deps/CppDepA/cpp/lib",
			sdrDom + "/deps/CppDepAC/cpp/lib", sdrDom + "/deps/CppDepAB/cpp/lib" };
		checkLdLibPath(map, paths);
	}

	private void checkLdLibPath(Map<String, String> map, String[] paths) {
		Assert.assertTrue(map.containsKey("LD_LIBRARY_PATH"));
		String path = map.get("LD_LIBRARY_PATH");

		if (path.endsWith(SUFFIX_2)) {
			path = path.substring(0, path.length() - SUFFIX_2.length());
		} else if (path.endsWith(SUFFIX_1)) {
			path = path.substring(0, path.length() - SUFFIX_1.length());
		} else {
			Assert.fail("Required LD_LIBRARY_PATH suffix not found");
		}
		if (path.endsWith(":")) {
			path = path.substring(0, path.length() - 1);
		}

		if (path.length() == 0) {
			Assert.assertTrue("Didn't expect any more path elements", paths.length == 0);
			return;
		}

		String[] pathElements = path.split(":");
		for (int index = 0; index < pathElements.length; index++) {
			if (index >= paths.length) {
				Assert.fail(String.format("LD_LIBRARY_PATH contains more elements (%d) than expected (%d)", pathElements.length, paths.length));
			}
			String errorMsg = String.format("LD_LIBRARY_PATH element %d did not match expected element", index);
			Assert.assertEquals(errorMsg, pathElements[index], paths[index]);
		}
	}

	private String getSdrDomLocation() throws URISyntaxException, IOException, CoreException {
		String domUri = SdrTests.getSdrTestsSdrRoot().getDomFileSystemRoot().toString();
		URI javaDomUri = URI.create(domUri);
		return EFS.getStore(javaDomUri).toLocalFile(0, null).toString();
	}
}
