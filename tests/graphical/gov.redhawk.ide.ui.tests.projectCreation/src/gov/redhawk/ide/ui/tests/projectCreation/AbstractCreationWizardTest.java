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

import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * 
 */
public abstract class AbstractCreationWizardTest extends UITest {
	protected SWTBotShell wizardShell;
	protected SWTBot wizardBot;

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	
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
		wizardBot.tree().getTreeItem("SCA").expand().getNode(getProjectType()).select();
		wizardBot.button("Next >").click();
	}
	
	protected abstract String getProjectType();

	@Test
	public void testNonDefaultLocation() throws IOException {
		bot.textWithLabel("&Project name:").setText("ProjectName");
		bot.checkBox("Use default location").click();

		bot.textWithLabel("&Location:").setText("Bad location");
		Assert.assertFalse(bot.button("Finish").isEnabled());

		File createdFolder = folder.newFolder("ProjectName");
		bot.textWithLabel("&Location:").setText(createdFolder.getAbsolutePath());
		bot.button("Finish").click();

		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("ProjectName");
		IPath location = project.getLocation();
		Assert.assertEquals(createdFolder.getAbsolutePath(), location.toOSString());
	}

	protected String getBaseFilename(String projectName) {
		if (projectName == null) {
			return null;
		}
		if (!projectName.contains(".")) {
			return projectName;
		}
		String[] segments = projectName.split("\\.");
		return segments[segments.length - 1];
	}
	
}
