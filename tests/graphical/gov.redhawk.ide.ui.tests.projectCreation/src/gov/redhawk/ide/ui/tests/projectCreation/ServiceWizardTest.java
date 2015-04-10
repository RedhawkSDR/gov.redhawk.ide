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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class ServiceWizardTest extends ComponentWizardTest {
	

	@Override
	protected String getProjectType() {
		return "SCA Service Project";
	}

	protected void testProjectCreation(String name, String lang, String generator, String template) {
		setServiceInWizard();
		super.testProjectCreation(name, lang, generator, template);
	}

	public void setServiceInWizard() {
		wizardBot.textWithLabel("Service Interface").setText("IDL:SAMPLE/SampleInterface:1.0");
	}
	
	@Test
	@Override
	public void testStubPythonCreation() {
		testProjectCreation("ComponentWizardTest01", "Python", "Stub Python Code Generator", "Default Service");
		wizardShell.close();
	}

	@Test
	@Override
	public void testStubCppCreation() {
		testProjectCreation("ComponentWizardTest01", "C++", "Stub C++ Code Generator", "Default Service");
		wizardShell.close();
	}

	@Test
	@Override
	public void testStubJavaCreation() {
		testProjectCreation("ComponentWizardTest01", "Java", "Stub Java Code Generator", "Default Service");
		wizardShell.close();
	}
	
	@Test
	@Override
	public void testBackNext() {
		setServiceInWizard();
		super.testBackNext();
	}
	
	@Test
	@Override
	public void testContributedPropertiesUI() {
		setServiceInWizard();
		super.testContributedPropertiesUI();
	}
	
	@Test
	@Override
	public void testNonDefaultLocation() throws IOException {
		setServiceInWizard();
		super.testNonDefaultLocation();
	}
	
	@Test
	@Override
	public void testUUID() {
		setServiceInWizard();
		super.testUUID();
	}
	
	/**
	 * IDE-1111: Test creation of service with dots in the name
	 */
	@Test
	@Override
	public void testNamespacedObjectCreation() {
		testProjectCreation("namespaced.service.IDE1111", "Python", null, null);
		// IDE-1219 TODO: skip below check, until moved to redhawk-ide-uitests repo to test against IDE product
//		Assert.assertEquals("code.entrypoint", "customOutput/IDE1111.py", bot.activeEditor().bot().textWithLabel("Entry Point:").getText());
		verifyEditorTabPresent("IDE1111.scd.xml");
	}
	
}
