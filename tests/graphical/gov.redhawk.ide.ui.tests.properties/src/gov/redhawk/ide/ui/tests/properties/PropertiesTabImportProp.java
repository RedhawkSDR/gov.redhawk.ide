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
package gov.redhawk.ide.ui.tests.properties;

import org.junit.Assert;
import org.junit.Test;

public class PropertiesTabImportProp extends AbstractBasicTest {
	
	@Test
	public void testAddPropertyFromBrowse() {
		bot.button("Browse...").click();
		bot.text().setText("frequency");
		bot.tree().getTreeItem("Target SDR").select();
		bot.tree().getTreeItem("Target SDR").getNode("Components").getNode("SigGen").getNode("frequency").select();
		bot.button("Finish").click();
		assertFormValid();
		editor.bot().tree().getTreeItem("frequency");
	}
	
	@Test
	public void testBug1295_BrowseWizardValidation() {
		bot.button("Browse...").click();
		bot.tree().getTreeItem("Target SDR").select();
		Assert.assertFalse("Finish button should not be enabled.", bot.button("Finish").isEnabled());
		bot.tree().getTreeItem("Target SDR").expand();
		bot.tree().getTreeItem("Target SDR").getNode("Components").select();
		Assert.assertFalse("Finish button should not be enabled.", bot.button("Finish").isEnabled());
		bot.tree().getTreeItem("Target SDR").getNode("Components").expand();
		bot.tree().getTreeItem("Target SDR").getNode("Components").getNode("SigGen").select();
		Assert.assertFalse("Finish button should not be enabled.", bot.button("Finish").isEnabled());
		bot.tree().getTreeItem("Target SDR").getNode("Components").getNode("SigGen").expand();
		bot.tree().getTreeItem("Target SDR").getNode("Components").getNode("SigGen").getNode("frequency").select();
		Assert.assertTrue("Finish button should be enabled.", bot.button("Finish").isEnabled());
		bot.button("Cancel").click();
		assertFormValid();
		Assert.assertFalse("Properties should be empty.", editor.bot().tree().hasItems());
	}

}
