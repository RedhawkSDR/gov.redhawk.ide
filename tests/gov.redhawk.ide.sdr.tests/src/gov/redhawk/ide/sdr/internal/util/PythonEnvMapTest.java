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
public class PythonEnvMapTest {

	private static final String SUFFIX = "${OssieHome}/lib/python:${env_var:PYTHONPATH}";

	private PythonEnvMap mapper = new PythonEnvMap();

	@Test
	public void initEnv_null() throws CoreException {
		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(null, map);
		Assert.assertEquals(1, map.size());
		checkPythonPath(map, new String[0]);
	}

	@Test
	public void initEnv_component() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:e8f986a9-e717-4792-bb06-d1c6f34650c6");
		Assert.assertEquals("PythonComponent", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("python"), map);
		Assert.assertEquals(1, map.size());
		checkPythonPath(map, new String[0]);
	}

	@Test
	public void initEnv_componentWithDeps() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:263456f0-5789-4b62-bc4b-3599f30fe86d");
		Assert.assertEquals("PythonComponentWithDeps", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("python"), map);
		Assert.assertEquals(1, map.size());
		String sdrDom = getSdrDomLocation();
		String[] paths = { sdrDom + "/deps/PythonDepD/python/lib", sdrDom + "/deps/PythonDepDE/python/lib", sdrDom + "/deps/PythonDepA/python/lib",
			sdrDom + "/deps/PythonDepAC/python/lib", sdrDom + "/deps/PythonDepAB/python/lib" };
		checkPythonPath(map, paths);
	}

	private void checkPythonPath(Map<String, String> map, String[] paths) {
		Assert.assertTrue(map.containsKey("PYTHONPATH"));
		String path = map.get("PYTHONPATH");

		if (path.endsWith(SUFFIX)) {
			path = path.substring(0, path.length() - SUFFIX.length());
		} else {
			Assert.fail("Required PYTHONPATH suffix not found");
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
				Assert.fail(String.format("PYTHONPATH contains more elements (%d) than expected (%d)", pathElements.length, paths.length));
			}
			String errorMsg = String.format("PYTHONPATH element %d did not match expected element", index);
			Assert.assertEquals(errorMsg, pathElements[index], paths[index]);
		}
	}

	private String getSdrDomLocation() throws URISyntaxException, IOException, CoreException {
		String domUri = SdrTests.getSdrTestsSdrRoot().getDomFileSystemRoot().toString();
		URI javaDomUri = URI.create(domUri);
		return EFS.getStore(javaDomUri).toLocalFile(0, null).toString();
	}
}
