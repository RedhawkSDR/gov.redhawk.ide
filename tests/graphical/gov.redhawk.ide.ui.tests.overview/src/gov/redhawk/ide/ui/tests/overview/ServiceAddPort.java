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
package gov.redhawk.ide.ui.tests.overview;

import gov.redhawk.ide.swtbot.ServiceUtils;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.Assert;
import org.junit.Test;

/**
 * IDE-980. Ensures that if a service implements an interface that derives from CF/PortSupplier then the button to add a
 * port in the component editor will be enabled.
 */
public class ServiceAddPort extends UITest {

	/**
	 * Tests that the add button is enabled for a service project with the CF/PortSupplier IDL.
	 */
	@Test
	public void testAddForPortSupplier() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/PortSupplier:1.0", "C++");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add...").isEnabled());
	}

	/**
	 * Tests that the add button is enabled for a service project with an IDL type that inherits from CF/PortSupplier.
	 */
	@Test
	public void testAddForPortSupplierChild() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/Device:1.0", "Python");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertTrue("Add port button should be enabled", editorBot.button("Add...").isEnabled());
	}

	/**
	 * Tests that the add button is disabled for a service project with an IDL type that does not inherit from
	 * CF/PortSupplier.
	 */
	@Test
	public void testAddForNonPortSupplier() {
		ServiceUtils.createServiceProject(bot, "TestProject", "IDL:CF/LifeCycle:1.0", "Java");
		bot.waitUntil(new WaitForEditorCondition());
		SWTBot editorBot = bot.activeEditor().bot();
		editorBot.cTabItem("Overview").activate();
		Assert.assertFalse("Add port button should be disabled", editorBot.button("Add...").isEnabled());
	}

}
