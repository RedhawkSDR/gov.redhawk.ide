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
package gov.redhawk.ide.ui.tests.projectcreation;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GeneralTest extends UITest {
	
	private void testNewProjectWizardAccess(String menuItem, String shellTitle) {
		
		SWTBotShell wizardShell;
		/**
		 * Wizard shortcut only available when product is installed
		// Check File new menu shortcut
		bot.menu("File").menu("New").menu(menuItem).click();
		SWTBotShell wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();	
		
		// Check wizard shortcut
		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
		
		// Check context menu
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		SWTBotMenu menu = new SWTBotMenu(ContextMenuHelper.contextMenu(view.bot().tree(), "New", "Project..."));
		menu.click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
		*/	
		
		// Check SCA Category
		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem("SCA").expand().getNode(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
	}
	
	@Test
	public void testNewComponentProject() {
		testNewProjectWizardAccess("SCA Component Project", "New Component Project");
	}
	
	@Test
	public void testNewWaveformProject() {
		testNewProjectWizardAccess("SCA Waveform Project", "New Waveform Project");
	}
	
	@Test
	public void testNewDeviceProject() {
		testNewProjectWizardAccess("SCA Device Project", "New Device Project");
	}
	
	@Test
	public void testNewServiceProject() {
		testNewProjectWizardAccess("SCA Service Project", "New Service Project");
	}
	
	@Test
	public void testNewNodeProject() {
		testNewProjectWizardAccess("SCA Node Project", "Node Project");
	}
	
	@Test
	public void testNewControlPanelProject() {
		testNewProjectWizardAccess("SCA Control Panel Project", "New Plug-in Project");
	}
	
}
