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
package gov.redhawk.ide.sdr.internal.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.tests.SdrTests;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.junit.Assert;
import org.junit.Test;

public class CppEnvMapTest {

	@Test
	public void testCreatePath() throws CoreException, URISyntaxException, IOException {
		SdrRoot sdr = SdrTests.getSdrTestsSdrRoot();
		SoftPkg spd = sdr.getComponentsContainer().getSoftPkg("DCE:38279be0-4650-40c4-9084-352e6ebeedeb");
		CppEnvMap map = new CppEnvMap();
		Assert.assertNull(map.createPath(null, null));
		File file = EFS.getStore(java.net.URI.create(spd.eResource().getURI().trimSegments(1).toString())).toLocalFile(0, null);
		Assert.assertEquals(new File(file, "lib").toString(), map.createPath("lib", spd.eResource().getURI()));
		Assert.assertEquals(new File(file, "lib").toString(), map.createPath("lib/", spd.eResource().getURI()));
		Assert.assertEquals(new File(file, "lib").toString(), map.createPath("lib/test.so", spd.eResource().getURI()));
	}

}
