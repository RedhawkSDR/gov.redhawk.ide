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

import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Test;

/**
 * IDE-1219 TODO: this should move to redhawk-ide-uitests repo to test against IDE product
 */
public class NodeCreationWizardTest extends AbstractCreationWizardTest {

	private static final String DOMAIN_COMBO_LABEL = "Domain Manager:";
	/* (non-Javadoc)
	 * @see gov.redhawk.ide.ui.tests.projectCreation.AbstractCreationWizardTest#getProjectType()
	 */
	@Override
	protected String getProjectType() {
		return "SCA Node Project";
	}

	/**
	 * IDE-1111: Test creation of node with dots in the name
	 */
	@Test
	public void testNamespacedNodeCreation() {
		final String nodeName = "namespaced.node.IDE1111";
		createNodeWithDevice(nodeName, null, "name.spaced.device");

		checkFiles(nodeName);
	}
	
	void setDomainName(String domainName) {
		SWTBotCombo combo = bot.comboBoxWithLabel(DOMAIN_COMBO_LABEL);
		if (domainName != null && domainName.length() > 0) {
			combo.setSelection(domainName);
		} else {
			combo.setSelection(0);
			if ("".equals(combo.getText())) { // allow test case to proceed if no items in drop down selection
				combo.setText("RHIDE_NodeCreationWizardTest");
			}
		}
	}
	
	protected void createBasicNode(String projectName, String domainName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		setDomainName(domainName);
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

		// IDE-1219 TODO: skip below check, until moved to redhawk-ide-uitests repo to test against IDE product
		// Assert.assertEquals(projectName, editorBot.bot().textWithLabel("Name:").getText());
	}
	
	protected void createNodeWithDevice(String projectName, String domainName, String deviceName) {
		bot.textWithLabel("&Project name:").setText(projectName);
		setDomainName(domainName);
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
		setDomainName(null);
		super.testNonDefaultLocation();
	}
	
	@Override
	public void testUUID() {
		setDomainName(null);
		super.testUUID();
	}
}
