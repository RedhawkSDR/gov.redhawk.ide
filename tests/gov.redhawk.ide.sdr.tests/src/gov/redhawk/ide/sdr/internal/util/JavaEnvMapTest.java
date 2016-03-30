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
public class JavaEnvMapTest {

	private static final String SUFFIX = "${OssieHome}/lib/*:${env_var:CLASSPATH}";

	private JavaEnvMap mapper = new JavaEnvMap();

	@Test
	public void initEnv_null() throws CoreException {
		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(null, map);
		Assert.assertEquals(1, map.size());
		checkClassPath(map, new String[0]);
	}

	@Test
	public void initEnv_component() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:0237f7ac-ff28-4a22-b203-5bf755fbd5dc");
		Assert.assertEquals("JavaComponent", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("java"), map);
		Assert.assertEquals(1, map.size());
		checkClassPath(map, new String[0]);
	}

	@Test
	public void initEnv_componentWithDeps() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:351db6e5-4fa7-4950-9218-797a46485f7a");
		Assert.assertEquals("JavaComponentWithDeps", spd.getName());

		Map<String, String> map = new HashMap<String, String>();
		mapper.initEnv(spd.getImplementation("java"), map);
		Assert.assertEquals(1, map.size());
		String sdrDom = getSdrDomLocation();
		String[] paths = { sdrDom + "/deps/JavaDepD/java/lib/JavaDepD.jar", sdrDom + "/deps/JavaDepDE/java/lib/JavaDepDE.jar",
			sdrDom + "/deps/JavaDepA/java/lib/JavaDepA.jar", sdrDom + "/deps/JavaDepAC/java/lib/JavaDepAC.jar", sdrDom + "/deps/JavaDepAB/java/lib/JavaDepAB.jar" };
		checkClassPath(map, paths);
	}

	private void checkClassPath(Map<String, String> map, String[] paths) {
		Assert.assertTrue(map.containsKey("CLASSPATH"));
		String path = map.get("CLASSPATH");

		if (path.endsWith(SUFFIX)) {
			path = path.substring(0, path.length() - SUFFIX.length());
		} else {
			Assert.fail("Required CLASSPATH suffix not found");
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
				Assert.fail(String.format("CLASSPATH contains more elements (%d) than expected (%d)", pathElements.length, paths.length));
			}
			String errorMsg = String.format("CLASSPATH element %d did not match expected element", index);
			Assert.assertEquals(errorMsg, pathElements[index], paths[index]);
		}
	}

	private String getSdrDomLocation() throws URISyntaxException, IOException, CoreException {
		String domUri = SdrTests.getSdrTestsSdrRoot().getDomFileSystemRoot().toString();
		URI javaDomUri = URI.create(domUri);
		return EFS.getStore(javaDomUri).toLocalFile(0, null).toString();
	}

}
