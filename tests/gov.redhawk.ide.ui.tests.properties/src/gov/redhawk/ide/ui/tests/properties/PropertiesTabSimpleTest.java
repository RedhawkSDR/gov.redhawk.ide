package gov.redhawk.ide.ui.tests.properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PropertiesTabSimpleTest extends AbstractBasicTest {

	@Override
	protected void createType() {
		bot.button("Add Simple").click();
	}
	
	@Test
	public void testValue() throws CoreException {	
		SWTBot editorBot = editor.bot();
		editorBot.textWithLabel("Value:").setText("stringValue");
		assertFormValid();
		bot.textWithLabel("Value:").setText("\"\"");
		assertFormValid();
		
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid();

		bot.comboBox().setSelection("boolean");
		bot.comboBox(1).setSelection("complex");
		assertFormInvalid();
		bot.comboBox(1).setSelection("");
		bot.textWithLabel("Value:").setText("true");
		assertFormValid();
		bot.textWithLabel("Value:").setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid();
		
		bot.comboBox().setSelection("char");
		bot.textWithLabel("Value:").setText("1");
		bot.textWithLabel("Value:").setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel("Value:").setText("");
		assertFormValid();
		
		bot.comboBox().setSelection("double (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		
		bot.comboBox().setSelection("float (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		
		bot.comboBox().setSelection("longlong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("long (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("short (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ulong (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ulonglong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ushort (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel("Value:").setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("-1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel("Value:").setText("1.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel("Value:").setText("1+j1");
		assertFormValid();
		bot.textWithLabel("Value:").setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("objref");
		bot.textWithLabel("Value:").setText("1");
		assertFormInvalid();
	}
	
	@Test
	public void testCompleteSimple() throws CoreException {
		bot.textWithLabel("Units:").setText("m");
		bot.textWithLabel("Description:").setText("This is a test");
		bot.textWithLabel("ID*:").setText("ID");
		bot.textWithLabel("Name:").setText("Name1");
		assertFormValid();
		
	}

	
}
