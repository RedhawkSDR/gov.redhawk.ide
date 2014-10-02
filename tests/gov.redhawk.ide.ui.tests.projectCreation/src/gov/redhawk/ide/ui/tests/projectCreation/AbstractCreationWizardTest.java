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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;

/**
 * 
 */
public abstract class AbstractCreationWizardTest extends UITest {
	protected SWTBotShell wizardShell;
	protected SWTBot wizardBot;
	private final String projectType;
	
	public AbstractCreationWizardTest(String projectType) {
		this.projectType = projectType;
	}
	
	@BeforeClass
	public static void setupPyDev() throws Exception {
		StandardTestActions.configurePyDev();
	}
	
	@Test
	public void testUUID() {
		wizardBot.textWithLabel("&Project name:").setText("WizardTest02");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());

		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		wizardBot.textWithLabel("DCE UUID:").setText("187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595dz");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		wizardBot.textWithLabel("DCE UUID:").setText("DCE");
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());

		wizardBot.radio("Generate an ID").click();
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());

		wizardBot.radio("Provide an ID").click();
		Assert.assertFalse(wizardBot.button("Next >").isEnabled());
		wizardBot.textWithLabel("DCE UUID:").setText("DCE:187ca38e-ef38-487f-8f9b-935dca8595da");
		Assert.assertTrue(wizardBot.button("Next >").isEnabled());
		
		wizardShell.close();
	}
	
	@Before
	@Override
	public void before() throws Exception {
		super.before();

		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardBot = wizardShell.bot();
		wizardBot.tree().getTreeItem("SCA").expand().getNode(projectType).select();
		wizardBot.button("Next >").click();
	}

}
