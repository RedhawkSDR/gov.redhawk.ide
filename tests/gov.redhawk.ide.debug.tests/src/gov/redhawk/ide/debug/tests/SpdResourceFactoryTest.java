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
package gov.redhawk.ide.debug.tests;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.debug.LocalComponentFactory;
import gov.redhawk.ide.debug.LocalDeviceFactory;
import gov.redhawk.ide.debug.SpdResourceFactory;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

public class SpdResourceFactoryTest {

	private static final String PLUGIN_ID = "gov.redhawk.ide.debug.tests";

	private SoftPkg getSpd(String spdPath) {
		ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		URI spdUri = URI.createPlatformPluginURI("/" + PLUGIN_ID + "/" + spdPath, true).appendFragment(SoftPkg.EOBJECT_PATH);
		return (SoftPkg) resourceSet.getEObject(spdUri, true);
	}

	@Test(expected = IllegalArgumentException.class)
	public void createResourceFactory_nullDescriptor() {
		SoftPkg spd = getSpd("testFiles/sdr/dom/components/SpdMissingPrfAndScd/SpdMissingPrfAndScd.spd.xml");
		SpdResourceFactory.createResourceFactory(spd);
	}

	@Test
	public void createResourceFactory_component() {
		SoftPkg spd = getSpd("resources/simplecomponent/simplecomponent.spd.xml");
		SpdResourceFactory resFactory = SpdResourceFactory.createResourceFactory(spd);
		Assert.assertTrue("Incorrect resource factory created", resFactory instanceof LocalComponentFactory);
		Assert.assertEquals("DCE:54711de8-bc24-4c23-a4b1-baca3e3361e4", resFactory.identifier());
	}

	@Test
	public void createResourceFactory_device() {
		SoftPkg spd = getSpd("resources/simpledevice/simpledevice.spd.xml");
		SpdResourceFactory resFactory = SpdResourceFactory.createResourceFactory(spd);
		Assert.assertTrue("Incorrect resource factory created", resFactory instanceof LocalDeviceFactory);
		Assert.assertEquals("DCE:c1364c5d-97f6-4140-83fb-190a8054cfc5", resFactory.identifier());
	}

	@Test
	public void createResourceFactory_service() {
		SoftPkg spd = getSpd("resources/simpleservice/simpleservice.spd.xml");
		SpdResourceFactory resFactory = SpdResourceFactory.createResourceFactory(spd);
		Assert.assertTrue("Incorrect resource factory created", resFactory instanceof SpdResourceFactory);
		Assert.assertEquals("DCE:65bcb9f1-9b16-4503-b268-50c282c66fcf", resFactory.identifier());
	}

	@Test(expected = IllegalArgumentException.class)
	public void createResourceFactory_other() {
		SoftPkg spd = getSpd("resources/simpleother/simpleother.spd.xml");
		SpdResourceFactory.createResourceFactory(spd);
	}
}
