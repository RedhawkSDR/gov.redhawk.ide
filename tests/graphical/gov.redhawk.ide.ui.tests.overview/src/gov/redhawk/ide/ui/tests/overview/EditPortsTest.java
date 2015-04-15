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

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.ProjectExplorerUtils;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.condition.WaitForEditorCondition;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EditPortsTest extends UITest {

	private SWTBot editorBot;

	private SoftPkg spd;

	private ICondition interfaceWait = new ICondition() {

		@Override
		public boolean test() throws Exception {
			bot.tree().getTreeItem("SAMPLE").expand().getNode("SampleInterface");
			return true;
		}

		@Override
		public void init(SWTBot bot) {
		}

		@Override
		public String getFailureMessage() {
			return "Failed to find sampleInterface";
		}

	};

	@Before
	public void before() throws Exception {
		super.before();

		StandardTestActions.importProject(OverviewTabTestsActivator.getInstance().getBundle(), new Path("workspace/CppComTest"), null);
		ProjectExplorerUtils.waitUntilNodeAppears(bot, "CppComTest");

		ProjectExplorerUtils.openProjectInEditor(bot, "CppComTest", "CppComTest.spd.xml");
		bot.waitUntil(new WaitForEditorCondition());

		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		editorBot = editor.bot();
		editorBot.cTabItem("Overview").activate();

		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);
		spd = SoftPkg.Util.getSoftPkg(spdEditor.getMainResource());
	}

	@Test
	public void testAddPort() {
		editorBot.button("Add...").click();
		Assert.assertEquals("Add Port", bot.activeShell().getText());

		bot.textWithLabel("Name:").setText("inTestPort");
		bot.comboBoxWithLabel("Direction:").setSelection("in <provides>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").expand().getNode("SampleInterface").select();

		// Check type table
		SWTBotTable typeTable = bot.tableWithLabel("Type:");
		Assert.assertEquals(3, typeTable.rowCount());
		typeTable.getTableItem("data").check();
		typeTable.getTableItem("responses").check();
		typeTable.getTableItem("control").check();
		typeTable.getTableItem("control").uncheck();
		typeTable.getTableItem("responses").uncheck();
		typeTable.getTableItem("data").uncheck();

		bot.button("Finish").click();

		editorBot.button("Add...").click();
		bot.sleep(600);
		bot.textWithLabel("Name:").setText("inTestPort");
		Assert.assertFalse("Wizard should be in error", bot.button("Finish").isEnabled());
		bot.textWithLabel("Name:").setText("outTestPort");
		bot.comboBox().setSelection("out <uses>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").expand().getNode("SampleInterface").select();
		bot.button("Finish").click();

		bot.button("Add...").click();
		bot.sleep(600);
		bot.textWithLabel("Name:").setText("bidirTestPort");
		bot.comboBox().setSelection("bidir <uses/provides>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").getNode("SampleInterface").select();
		bot.button("Finish").click();

		Assert.assertEquals(3, editorBot.table().rowCount());
		Ports ports = spd.getDescriptor().getComponent().getComponentFeatures().getPorts();

		Assert.assertEquals(4, ports.getAllPorts().size());
	}

	@Test
	public void testEditPort() {
		Assert.assertFalse("Edit should be disabled", bot.button("Edit").isEnabled());
		editorBot.button("Add...").click();
		Assert.assertEquals("Add Port", bot.activeShell().getText());
		bot.textWithLabel("Name:").setText("inTestPort");
		bot.comboBoxWithLabel("Direction:").setSelection("in <provides>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").expand().getNode("SampleInterface").select();
		bot.button("Finish").click();

		// Test double-clicking on the port in the table, then bailing out
		bot.table().doubleClick(0, 0);
		Assert.assertEquals("Edit Port", bot.activeShell().getText());
		bot.activeShell().close();

		SWTBotTableItem item = bot.table().getTableItem(0);
		Assert.assertEquals("<provides> inTestPort", item.getText());
		Assert.assertEquals("IDL:SAMPLE/SampleInterface:1.0", item.getText(1));

		// Test clicking the edit button and making a change
		bot.table().select(0);
		bot.button("Edit").click();
		Assert.assertEquals("Edit Port", bot.activeShell().getText());
		bot.textWithLabel("Name:").setText("outTestPort");
		bot.comboBox().setSelection("out <uses>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").expand().getNode("SampleInterface2").select();
		bot.button("Finish").click();

		item = bot.table().getTableItem(0);
		Assert.assertEquals("<uses> outTestPort", item.getText());
		Assert.assertEquals("IDL:SAMPLE/SampleInterface2:1.0", item.getText(1));

		// Test clicking the edit button and NOT making a change (IDE-1230)
		bot.table().select(0);
		bot.button("Edit").click();
		Assert.assertEquals("Edit Port", bot.activeShell().getText());
		bot.button("Finish").click();
		Assert.assertEquals("<uses> outTestPort", item.getText());
		Assert.assertEquals("IDL:SAMPLE/SampleInterface2:1.0", item.getText(1));

		item = bot.table().getTableItem(0);
		Assert.assertEquals("<uses> outTestPort", item.getText());
		Assert.assertEquals("IDL:SAMPLE/SampleInterface2:1.0", item.getText(1));
	}

	@Test
	public void testRemovePort() {
		Assert.assertFalse("Remove should be disabled", bot.button("Remove").isEnabled());
		editorBot.button("Add...").click();
		Assert.assertEquals("Add Port", bot.activeShell().getText());
		bot.textWithLabel("Name:").setText("inTestPort");
		bot.comboBoxWithLabel("Direction:").setSelection("in <provides>");
		bot.waitUntil(interfaceWait);
		bot.tree().getTreeItem("SAMPLE").getNode("SampleInterface").select();
		bot.button("Finish").click();

		bot.table().select(0);
		bot.button("Remove").click();

		Assert.assertEquals(0, bot.table().rowCount());
	}

}
