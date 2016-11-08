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
package gov.redhawk.ide.codegen.tests;

import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.codegen.jet.TopLevelDcdRpmSpecTemplate;
import gov.redhawk.ide.codegen.jet.TopLevelSadRpmSpecTemplate;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;
import mil.jpeojtrs.sca.util.SdrURIHandler;

@SuppressWarnings("restriction")
public class GenerateSpecFileTest {

	@Test
	public void missingSadSpdTest() throws URISyntaxException {
		TopLevelSadRpmSpecTemplate sadSpecTemplate = TopLevelSadRpmSpecTemplate.create(null);
		SoftwareAssembly sad = getSad("/waveforms/SpecFileTest/SpecFileTest.sad.xml");

		// Test referencing a non-existent spd.xml
		try {
			sadSpecTemplate.generate(sad);
			Assert.fail("Test is expected to throw a CoreException for referencing an non-existent spd.xml in the sad.xml ComponentFile block");
		} catch (CoreException e) {
			Assert.assertTrue(e.getMessage() != null && e.getMessage().matches(".*Unable to locate component file.*"));
			return;
		}
	}

	@Test
	public void missingDcdSpdTest() throws URISyntaxException {
		TopLevelDcdRpmSpecTemplate dcdSpecTemplate = TopLevelDcdRpmSpecTemplate.create(null);
		DeviceConfiguration dcd = getDcd("/nodes/SpecFileTest/DeviceManager.dcd.xml");

		// Test referencing a non-existent spd.xml
		try {
			dcdSpecTemplate.generate(dcd);
			Assert.fail("Test is expected to throw a CoreException for referencing an non-existent spd.xml in the dcd.xml ComponentFile block");
		} catch (CoreException e) {
			Assert.assertTrue(e.getMessage() != null && e.getMessage().matches(".*Unable to locate component file.*"));
			return;
		}
	}

	private SoftwareAssembly getSad(String projectPath) throws URISyntaxException {
		URI uri = URI.createURI(ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM + "://" + projectPath);
		return SoftwareAssembly.Util.getSoftwareAssembly(getResource(uri));
	}

	private DeviceConfiguration getDcd(String projectPath) throws URISyntaxException {
		URI uri = URI.createURI(ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV + "://" + projectPath);
		return DeviceConfiguration.Util.getDeviceConfiguration(getResource(uri));
	}

	private Resource getResource(URI uri) throws URISyntaxException {

		ResourceSet resourceSet = new ResourceSetImpl();
		URL url = FileLocator.find(Platform.getBundle("gov.redhawk.ide.codegen.tests"), new Path("sdr"), null);
		SdrURIHandler handler = new SdrURIHandler(URI.createURI(url.toURI().toString()));
		resourceSet.getURIConverter().getURIHandlers().add(0, handler);
		return resourceSet.getResource(uri, true);
	}

}
