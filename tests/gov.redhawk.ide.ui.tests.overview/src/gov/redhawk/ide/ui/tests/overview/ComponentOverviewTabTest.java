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
import gov.redhawk.ide.spd.internal.ui.editor.ComponentOverviewPage;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.Path;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class ComponentOverviewTabTest extends UITest {

	private SWTBotEditor editor;
	private ComponentOverviewPage overviewPage;
	private SoftPkg spd;
	private SWTBot editorBot;
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
		// Ensure SPD file was created
		SWTBotView view = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		view.show();
		view.bot().tree().setFocus();
		view.bot().tree().getTreeItem("CppComTest").select();
		view.bot().tree().getTreeItem("CppComTest").expand();
		view.bot().tree().getTreeItem("CppComTest").getNode("CppComTest.spd.xml").doubleClick();
		
		bot.waitUntil(new WaitForEditorCondition(), 30000);

		editor = bot.activeEditor();
		editor.setFocus();
		editorBot = editor.bot();
		editorBot.cTabItem("Overview").activate();

		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);
		overviewPage = spdEditor.getOverviewPage();
		spd = SoftPkg.Util.getSoftPkg(spdEditor.getMainResource());
	}

	protected void assertFormValid() {
		StandardTestActions.assertFormValid(editorBot, overviewPage);
	}

	protected void assertFormInvalid() {
		StandardTestActions.assertFormInvalid(editorBot, overviewPage);
	}

	@Test
	public void testIDField() {
		editorBot.button("Generate").click();

		String idText = bot.textWithLabel("ID*:").getText();
		editorBot.sleep(600);
		Assert.assertTrue("Not valid DCE UUID", DceUuidUtil.isValid(idText));
		assertFormValid();
		Assert.assertEquals(spd.getId(), idText);

		editorBot.textWithLabel("ID*:").setText("DCE");
		editorBot.sleep(600);
		assertFormInvalid();

		editorBot.textWithLabel("ID*:").setText("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertEquals("DCE:8745512e-cdaf-41ad-93e4-a404d5e8e6db", spd.getId());

		editorBot.textWithLabel("ID*:").setText("");
		editorBot.sleep(600);
		assertFormInvalid();
	}

	@Test
	public void testName() {
		editorBot.textWithLabel("Name*:").setText("TestComponent");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertEquals(spd.getName(), "TestComponent");

		editorBot.textWithLabel("Name*:").setText("");
		editorBot.sleep(600);
		assertFormInvalid();
	}

	@Test
	public void testDescription() {
		editorBot.textWithLabel("Description:").setText("A component created for testing.");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertEquals(spd.getDescription(), "A component created for testing.");

		editorBot.textWithLabel("Description:").setText("");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertNull(spd.getDescription());
	}

	@Test
	public void testVersion() {
		editorBot.textWithLabel("Version:").setText("1.0.0");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertEquals(spd.getVersion(), "1.0.0");

		editorBot.textWithLabel("Version:").setText("");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertNull(spd.getVersion());

		editorBot.textWithLabel("Version:").setText("asd23r asd");
		editorBot.sleep(600);
		assertFormValid();
	}

	@Test
	public void testTitle() {
		editorBot.textWithLabel("Title:").setText("Test Component");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertEquals(spd.getTitle(), "Test Component");

		editorBot.textWithLabel("Title:").setText("");
		editorBot.sleep(600);
		assertFormValid();
		Assert.assertNull(spd.getTitle());
	}

	@Test
	public void testPrf() {
		editorBot.button("Browse...").click();
		bot.tree().getTreeItem("CppComTest").getNode("CppComTest.prf.xml").select();
		bot.button("OK").click();
		assertFormValid();
		editorBot.textWithLabel("Title:", 1).setText("CppComTest.prf.xml");
		editorBot.sleep(600);
		assertFormValid();
		editorBot.textWithLabel("Title:", 1).setText("blah.prf.xml");
		editorBot.sleep(600);
		assertFormInvalid();
		editorBot.textWithLabel("Title:", 1).setText("CppComTest.prf.xml");
		editorBot.sleep(600);
		assertFormValid();
	}

	@Test
	public void testScd() {
		editorBot.button("Browse...", 1).click();
		bot.tree().getTreeItem("CppComTest").getNode("CppComTest.scd.xml").select();
		bot.button("OK").click();
		assertFormValid();
		editorBot.textWithLabel("Title:", 2).setText("CppComTest.scd.xml");
		editorBot.sleep(600);
		assertFormValid();
		editorBot.textWithLabel("Title:", 2).setText("blah.scd.xml");
		editorBot.sleep(600);
		assertFormInvalid();
		editorBot.textWithLabel("Title:", 2).setText("CppComTest.scd.xml");
		editorBot.sleep(600);
		assertFormValid();
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

		bot.table().doubleClick(0, 0);
		Assert.assertEquals("Edit Port", bot.activeShell().getText());
		bot.activeShell().close();

		bot.table().select(0);
		bot.button("Edit").click();
		Assert.assertEquals("Edit Port", bot.activeShell().getText());
		bot.textWithLabel("Name:").setText("outTestPort");
		bot.comboBox().setSelection("out <uses>");
		bot.waitUntil(interfaceWait);
		bot.button("Finish").click();

		SWTBotTableItem item = bot.table().getTableItem(0);
		Assert.assertEquals("<uses> outTestPort", item.getText());
		Assert.assertEquals("IDL:SAMPLE/SampleInterface:1.0", item.getText(1));
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

	@Test
	public void testGenerateButton() {
		Assert.assertTrue("Generate button should not be disabled.", editorBot.toolbarButtonWithTooltip("Generate All Implementations").isEnabled());
	}

	@Test
	public void testControlPanelButton() {
		Assert.assertTrue("Control panel button should not be disabled.", editorBot.toolbarButtonWithTooltip("New Control Panel Project").isEnabled());
	}
}
