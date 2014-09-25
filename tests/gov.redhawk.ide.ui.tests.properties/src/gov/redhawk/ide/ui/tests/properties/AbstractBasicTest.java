package gov.redhawk.ide.ui.tests.properties;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.ide.swtbot.UITest;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class AbstractBasicTest extends UITest {
	
	protected SWTBotEditor editor;


	@Before
	public void before() throws Exception {
		super.before();
		bot = new SWTWorkbenchBot();
		StandardTestActions.beforeTest(bot);
		
		StandardTestActions.clearWorkspace();
		StandardTestActions.importProject(PropertiesUITestsActivator.getInstance().getBundle(), new Path("workspace/PropTest_Comp"), null);
		bot.tree().getTreeItem("PropTest_Comp").select();
		bot.tree().getTreeItem("PropTest_Comp").expand();
		bot.tree().getTreeItem("PropTest_Comp").getNode("PropTest_Comp.spd.xml").doubleClick();
		
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				bot.activeEditor();
				return true;
			}

			@Override
			public void init(SWTBot bot) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getFailureMessage() {
				return "no editor available";
			}
			
		}, 30000);
		
		editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();

		createType();
		editorBot.textWithLabel("ID*:").setText("ID");
	}
	
	@After
	public void after() throws Exception {
		editor = null;
		bot.sleep(500);
		super.afterTest();
	}
	
	protected abstract void createType();

	protected void assertFormValid() {
		int messageType = getValidationState(editor);
		Assert.assertNotEquals("Form should be valid", IMessageProvider.ERROR, messageType);
	}

	protected int getValidationState(SWTBotEditor editor) {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);
		bot.sleep(600);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		int messageType = propertiesPage.getManagedForm().getForm().getMessageType();
		return messageType;
	}
	
	protected void assertFormInvalid() {
		int messageType = getValidationState(editor);
		Assert.assertEquals("Form should not be valid", IMessageProvider.ERROR, messageType);
	}
	
	@Test
	public void testCreate() throws CoreException {
		assertFormValid();
	}
	
	@Test
	public void testID() throws CoreException {
		bot.textWithLabel("ID*:").setText("");
		assertFormInvalid();
		bot.textWithLabel("ID*:").setText("hello");
		assertFormValid();
	}
	
	@Test
	public void testUnqueID() {
		assertFormValid();
		createType();
		bot.textWithLabel("ID*:").setText("ID");
		assertFormInvalid();
		bot.textWithLabel("ID*:").setText("ID2");
		assertFormValid();
	}
	
	@Test
	public void testUnits() {
		bot.textWithLabel("Units:").setText("m");
		bot.textWithLabel("Units:").setText("");
		assertFormValid();
	}
	
	@Test
	public void testAction() {	
		bot.comboBoxWithLabel("Action:").setSelection("eq");
		assertFormValid();
		
		bot.comboBoxWithLabel("Action:").setSelection("ge");
		assertFormValid();
		
		bot.comboBoxWithLabel("Action:").setSelection("gt");
		assertFormValid();
		
		bot.comboBoxWithLabel("Action:").setSelection("le");
		assertFormValid();
		
		bot.comboBoxWithLabel("Action:").setSelection("lt");
		assertFormValid();
		
		bot.comboBoxWithLabel("Action:").setSelection("ne");
		assertFormValid();
	}
	
	@Test
	public void testRange() {
		bot.comboBox().setSelection("boolean");
		assertFormValid();
		
		bot.checkBox("Enable").click();
		assertFormInvalid();
		
		bot.textWithLabel("Min:").setText("true");
		bot.textWithLabel("Max:").setText("true");
		assertFormValid();
		
		bot.textWithLabel("Min:").setText("asopina");
		assertFormInvalid();
		
		bot.comboBox().setSelection("double (64-bit)");
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Max:").setText("20");
		bot.textWithLabel("Min:").setText("-1.1");
		assertFormValid();
		
		bot.textWithLabel("Max:").setText("20+i10sad");
		assertFormInvalid();
		
		bot.textWithLabel("Max:").setText("20");
		assertFormValid();
		
		bot.textWithLabel("Min:").setText("-1.1+ja");
		assertFormInvalid();
		
		bot.textWithLabel("Min:").setText("-1.1");
		assertFormValid();
		
		bot.textWithLabel("Max:").setText("10+j10.5");
		assertFormValid();
		
		bot.textWithLabel("Min:").setText("-1.1+j1");
		assertFormValid();
		
		bot.textWithLabel("Min:").setText("bad");
		bot.textWithLabel("Max:").setText("bad");
		assertFormInvalid();
		
		bot.checkBox("Enable").click();
		assertFormValid();
	}
	
	@Test
	public void testKind() {
		assertFormValid();
		
		SWTBotTable kindTable = bot.tableWithLabel("Kind:");
		kindTable.getTableItem("configure (default)").check();
		assertFormValid();
		kindTable.getTableItem("execparam").check();
		assertFormValid();
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("execparam").uncheck();
		kindTable.getTableItem("configure (default)").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("allocation").check();
		assertFormValid();
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
		kindTable.getTableItem("event").uncheck();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid();

		
		kindTable.getTableItem("event").check();
		assertFormValid();
		kindTable.getTableItem("message").check();
		assertFormInvalid();
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid();
		
		kindTable.getTableItem("configure (default)").uncheck();
		kindTable.getTableItem("message").check();
		assertFormValid();
		kindTable.getTableItem("message").uncheck();
		assertFormValid();
	}
	
	@Test
	public void testEnum() throws CoreException {	
		bot.button("Add...").click();
		bot.textWithLabel("Label:").setText("lab");
		bot.textWithLabel("Value:").setText("asf");
		bot.button("Finish").click();
		assertFormValid();
		
		SWTBotTable enumTable = bot.tableWithLabel("Enumerations:");
		SWTBotTableItem item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));
			
		item.select();
		bot.button("Edit").click();
		Assert.assertEquals("lab", bot.textWithLabel("Label:").getText());
		Assert.assertEquals("asf", bot.textWithLabel("Value:").getText());
		bot.button("Cancel").click();
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("asf", item.getText(1));
		
		bot.button("Edit").click();
		bot.textWithLabel("Value:").setText("abc");
		bot.button("Finish").click();
		item = enumTable.getTableItem(0);
		Assert.assertEquals("lab", item.getText(0));
		Assert.assertEquals("abc", item.getText(1));
		
		
		item = enumTable.getTableItem(0);
		item.select();
		bot.button("Remove", 1).click();
		assertFormValid();
		Assert.assertEquals(0, enumTable.rowCount());
	}
	
	@Test
	public void testDescription() {
		bot.textWithLabel("Description:").setText("This is a test");
		assertFormValid();
	}
	
	@Test
	public void testName() {	
		bot.textWithLabel("Name:").setText("Name1");
		assertFormValid();
	}
	
	@Test
	public void testMode() {
		bot.comboBoxWithLabel("Mode:").setSelection("writeonly");
		assertFormValid();
		bot.comboBoxWithLabel("Mode:").setSelection("readonly");
		assertFormValid();
		bot.comboBoxWithLabel("Mode:").setSelection("readwrite");
	}

}
