package gov.redhawk.ide.ui.tests.properties;

import java.util.concurrent.CountDownLatch;

import gov.redhawk.ide.spd.internal.ui.editor.ComponentEditor;
import gov.redhawk.ide.swtbot.StandardTestActions;
import gov.redhawk.prf.internal.ui.editor.composite.BasicSimplePropertyComposite;
import gov.redhawk.prf.ui.editor.page.PropertiesFormPage;
import gov.redhawk.sca.ui.ScaUI;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PropertiesTabSimpleTest extends AbstractBasicTest {
	
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
	}
	
	@After
	@Override
	public void after() throws Exception {
		super.after();
		bot.sleep(1000);
	}
	
	@Test
	public void testSimpleEnum() throws CoreException {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		
		bot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		editorBot.button("Add...").click();
		bot.textWithLabel("Label:").setText("lab");
		bot.textWithLabel("Value:").setText("asf");
		bot.button("Finish").click();
		assertFormValid(editor);
		
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
		assertFormValid(editor);
		Assert.assertEquals(0, enumTable.rowCount());
	}
	
	@Test
	public void addSimple() throws CoreException {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		bot.button("Add Simple").click();
		assertFormInvalid(editor);
		
		bot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleValues() throws CoreException {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		editorBot.textWithLabel("Value:").setText("stringValue");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("\"\"");
		assertFormValid(editor);
		
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid(editor);

		bot.comboBox().setSelection("boolean");
		bot.comboBox(1).setSelection("complex");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("");
		bot.textWithLabel("Value:").setText("true");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("badValue");
		assertFormInvalid(editor);
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("char");
		bot.textWithLabel("Value:").setText("1");
		bot.textWithLabel("Value:").setText("badValue");
		assertFormInvalid(editor);
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("double (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		
		bot.comboBox().setSelection("float (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		
		bot.comboBox().setSelection("longlong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("long (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("short (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("ulong (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("ulonglong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("ushort (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid(editor);
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid(editor);
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid(editor);
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("objref");
		bot.textWithLabel("Value:").setText("1");
		assertFormInvalid(editor);
	}
	
	@Test
	public void testSimpleUnits() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		editorBot.textWithLabel("Units:").setText("m");
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleDescription() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		editorBot.textWithLabel("Description:").setText("This is a test");
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleName() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		editorBot.textWithLabel("Name:").setText("Name1");
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleMode() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Mode:").setSelection("writeonly");
		assertFormValid(editor);
		bot.comboBoxWithLabel("Mode:").setSelection("readonly");
		assertFormValid(editor);
		bot.comboBoxWithLabel("Mode:").setSelection("readwrite");
	}
	
	@Test
	public void testSimpleKind() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		SWTBotTable kindTable = bot.tableWithLabel("Kind:");
		kindTable.getTableItem("configure (default)").check();
		assertFormValid(editor);
		kindTable.getTableItem("execparam").check();
		assertFormValid(editor);
		kindTable.getTableItem("allocation").check();
		assertFormValid(editor);
		kindTable.getTableItem("event").check();
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid(editor);
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid(editor);
		kindTable.getTableItem("event").check();
		assertFormValid(editor);
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid(editor);
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("execparam").uncheck();
		kindTable.getTableItem("configure (default)").uncheck();
		assertFormValid(editor);
		
		kindTable.getTableItem("allocation").check();
		assertFormValid(editor);
		kindTable.getTableItem("event").check();
		assertFormValid(editor);
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		assertFormValid(editor);
		kindTable.getTableItem("event").uncheck();
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("allocation").uncheck();
		assertFormValid(editor);

		
		kindTable.getTableItem("event").check();
		assertFormValid(editor);
		kindTable.getTableItem("message").check();
		assertFormInvalid(editor);
		kindTable.getTableItem("message").uncheck();
		kindTable.getTableItem("event").uncheck();
		assertFormValid(editor);
		
		kindTable.getTableItem("configure (default)").uncheck();
		kindTable.getTableItem("message").check();
		assertFormValid(editor);
		kindTable.getTableItem("message").uncheck();
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleAction() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("eq");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("ge");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("gt");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("le");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("lt");
		assertFormValid(editor);
		
		bot.comboBoxWithLabel("Action:").setSelection("ne");
		assertFormValid(editor);
	}
	
	@Test
	public void testSimpleRange() {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("ID*:").setText("ID");
		assertFormValid(editor);
		
		bot.comboBox().setSelection("boolean");
		assertFormValid(editor);
		
		bot.checkBox("Enable").click();
		assertFormInvalid(editor);
		
		bot.textWithLabel("Min:").setText("true");
		bot.textWithLabel("Max:").setText("true");
		assertFormValid(editor);
		
		bot.textWithLabel("Min:").setText("asopina");
		assertFormInvalid(editor);
		
		bot.comboBox().setSelection("double (64-bit)");
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Max:").setText("20");
		bot.textWithLabel("Min:").setText("-1.1");
		assertFormValid(editor);
		
		bot.textWithLabel("Max:").setText("20+i10sad");
		assertFormInvalid(editor);
		
		bot.textWithLabel("Max:").setText("20");
		assertFormValid(editor);
		
		bot.textWithLabel("Min:").setText("-1.1+ja");
		assertFormInvalid(editor);
		
		bot.textWithLabel("Min:").setText("-1.1");
		assertFormValid(editor);
		
		bot.textWithLabel("Max:").setText("10+j10.5");
		assertFormValid(editor);
		
		bot.textWithLabel("Min:").setText("-1.1+j1");
		assertFormValid(editor);
		
		bot.textWithLabel("Min:").setText("bad");
		bot.textWithLabel("Max:").setText("bad");
		assertFormInvalid(editor);
		
		bot.checkBox("Enable").click();
		assertFormValid(editor);
	}
	
	
	@Test
	public void componentSimple() throws CoreException {
		SWTBotEditor editor = bot.activeEditor();
		editor.setFocus();
		SWTBot editorBot = editor.bot();
		editorBot.cTabItem("Properties").activate();
		assertFormValid(editor);
		
		editorBot.button("Add Simple").click();
		editorBot.textWithLabel("Units:").setText("m");
		editorBot.textWithLabel("Description:").setText("This is a test");
		editorBot.textWithLabel("ID*:").setText("ID");
		editorBot.textWithLabel("Name:").setText("Name1");
		assertFormValid(editor);
	}

	private void assertFormValid(SWTBotEditor editor) {
		int messageType = getValidationState(editor);
		Assert.assertNotEquals("Form should be valid", IMessageProvider.ERROR, messageType);
	}

	private int getValidationState(SWTBotEditor editor) {
		ComponentEditor spdEditor = (ComponentEditor) editor.getReference().getEditor(false);
		bot.sleep(600);

		PropertiesFormPage propertiesPage = spdEditor.getPropertiesPage();
		int messageType = propertiesPage.getManagedForm().getForm().getMessageType();
		return messageType;
	}
	
	private void assertFormInvalid(SWTBotEditor editor) {
		int messageType = getValidationState(editor);
		Assert.assertEquals("Form should not be valid", IMessageProvider.ERROR, messageType);
	}
}
