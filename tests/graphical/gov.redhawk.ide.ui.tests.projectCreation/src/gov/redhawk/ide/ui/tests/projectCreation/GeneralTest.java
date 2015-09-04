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

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.junit.Assert;
import org.junit.Test;

public class GeneralTest extends UITest {
	
	private void testNewProjectWizardAccess(String menuItem, String shellTitle) {
		
		SWTBotShell wizardShell;

		// Check SCA Category
		bot.menu("File").menu("New").menu("Project...").click();
		wizardShell = bot.shell("New Project");
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.bot().tree().getTreeItem("REDHAWK").expand().getNode(menuItem).select();
		wizardShell.bot().button("Next >").click();
		wizardShell = bot.shell(shellTitle);
		Assert.assertTrue(wizardShell.isActive());
		wizardShell.close();
	}
	
	@Test
	public void testNewComponentProject() {
		testNewProjectWizardAccess("REDHAWK Component Project", "New Component Project");
	}
	
	@Test
	public void testNewWaveformProject() {
		testNewProjectWizardAccess("REDHAWK Waveform Project", "New Waveform Project");
	}
	
	@Test
	public void testNewDeviceProject() {
		testNewProjectWizardAccess("REDHAWK Device Project", "New Device Project");
	}
	
	@Test
	public void testNewServiceProject() {
		testNewProjectWizardAccess("REDHAWK Service Project", "New Service Project");
	}
	
	@Test
	public void testNewNodeProject() {
		testNewProjectWizardAccess("REDHAWK Node Project", "Node Project");
	}
	
	@Test
	public void testNewControlPanelProject() {
		testNewProjectWizardAccess("REDHAWK Control Panel Project", "New Plug-in Project");
	}
	
}
