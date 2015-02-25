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

import gov.redhawk.ide.swtbot.WaitForEditorCondition;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class NodeCreationWizardTest extends AbstractCreationWizardTest {

	private static final String DOMAIN_COMBO_LABEL = "Domain Manager:";
	/* (non-Javadoc)
	 * @see gov.redhawk.ide.ui.tests.projectCreation.AbstractCreationWizardTest#getProjectType()
	 */
	@Override
	protected String getProjectType() {
		// TODO Auto-generated method stub
		return "SCA Node Project";
	}

	/**
	 * IDE-1111: Test creation of node with dots in the name
	 */
	@Test
	public void testNamespacedNodeCreation() {
		final String nodeName = "namespaced.node.IDE1111";
		createNodeWithDevice(nodeName, null, null);

		checkFiles(nodeName);
	}
	
	protected void createBasicNode(String projectName, String domainName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		if (domainName != null) {
			bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(domainName);
		} else {
			bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(0);
		}
		bot.button("Finish").click();
	}
	
	protected void checkFiles(String projectName) {
		// Ensure DCD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem(projectName).select();
		view.bot().tree().getTreeItem(projectName).expand();
		view.bot().tree().getTreeItem(projectName).getNode("DeviceManager.dcd.xml");
		bot.waitUntil(new WaitForEditorCondition(), 30000, 500);

		SWTBotEditor editorBot = bot.activeEditor();
		editorBot.bot().cTabItem("Overview").activate();

		Assert.assertEquals(projectName, editorBot.bot().textWithLabel("Name:").getText());
	}
	
	protected void createNodeWithDevice(String projectName, String domainName, String deviceName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		if (domainName != null) {
			bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(domainName);
		} else {
			bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(0);
		}
		bot.button("Next >").click();
		SWTBotTable deviceTable = bot.table();
		if (deviceName != null) {
			for (int index = 0; index < deviceTable.rowCount(); ++index) {
				if (deviceTable.getTableItem(index).getText().startsWith(deviceName)) {
					deviceTable.select(index);
					break;
				}
			}
		} else {
			bot.table().select(0);
		}
		bot.button("Finish").click();
	}
	
	@Override
	public void testNonDefaultLocation() throws IOException {
		bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(0);
		super.testNonDefaultLocation();
	}
	
	@Override
	public void testUUID() {
		bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL).setSelection(0);
		super.testUUID();
	}
}
