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
package gov.redhawk.ide.ui.tests.projectCreation;


import gov.redhawk.model.sca.util.ModelUtil;
import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.SupportsInterface;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.URI;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class DeviceCreationWizardTest extends ComponentWizardTest {

	@Override
	protected String getProjectType() {
		return "SCA Device Project";
	}
	
	@Test
	public void testStubCppDeviceCreation() {
		wizardBot.comboBox().setSelection("Device");
		super.testStubCppCreation();
		
		assertDeviceInterface("IDL:CF/Device:1.0");
	}

	public void assertDeviceInterface(String repid) {
		URI uri = URI.createPlatformResourceURI("/ComponentWizardTest01/ComponentWizardTest01.spd.xml", true);
		SoftPkg spd = ModelUtil.loadSoftPkg(uri);
		ComponentFeatures feature = spd.getDescriptor().getComponent().getComponentFeatures();
		boolean found = false;
		for (SupportsInterface i : feature.getSupportsInterface()) {
			if (repid.equals(i.getRepId())) {
				found = true;
			}
		}
		Assert.assertTrue("Did not find correct supports interface:" + repid, found);
	}


	@Test
	public void testStubCppExecutableDeviceCreation() {
		wizardBot.comboBox().setSelection("Executable");
		super.testStubCppCreation();
		
		assertDeviceInterface("IDL:CF/ExecutableDevice:1.0");
	}
	
	@Test
	public void testStubCppAggregateDeviceCreation() {
		bot.checkBox("Aggregate device").click();
		super.testStubCppCreation();
		
		assertDeviceInterface("IDL:CF/AggregateDevice:1.0");
	}
	
	@Test
	public void testStubCppLoadableDeviceCreation() {
		wizardBot.comboBox().setSelection("Loadable");
		super.testStubCppCreation();
		
		assertDeviceInterface("IDL:CF/LoadableDevice:1.0");
	}

	/**
	 * IDE-1111: Test creation of device with dots in the name
	 */
	@Test
	@Override
	public void testNamespacedObjectCreation() {
		super.testProjectCreation("namespaced.device.IDE1111", "Python", null, null);
		verifyEditorTabPresent("IDE1111.prf.xml");
		verifyEditorTabPresent("IDE1111.scd.xml");
	}
	
}
