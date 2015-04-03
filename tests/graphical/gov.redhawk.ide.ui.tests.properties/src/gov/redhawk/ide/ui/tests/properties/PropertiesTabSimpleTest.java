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


import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCombo;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesTabSimpleTest extends AbstractBasicPropertyTest {

	@Override
	protected void createType() {
		bot.button("Add Simple").click();
	}
	
	@Test
	public void testKind() {
		testKind(true);
	}
	
	
	@Test
	public void testValue() throws CoreException {
		testValue("Value:");
	}
	
	protected void testValue(String valueLabel) throws CoreException {	
		SWTBot editorBot = editor.bot();
		editorBot.textWithLabel(valueLabel).setText("stringValue");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("\"\"");
		assertFormValid();
		
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();

		bot.comboBox().setSelection("boolean");
		bot.comboBox(1).setSelection("complex");
		assertFormInvalid();
		bot.comboBox(1).setSelection("");
		bot.textWithLabel(valueLabel).setText("true");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();
		
		bot.comboBox().setSelection("char");
		bot.textWithLabel(valueLabel).setText("1");
		bot.textWithLabel(valueLabel).setText("badValue");
		assertFormInvalid();
		editorBot.textWithLabel(valueLabel).setText("");
		assertFormValid();
		
		bot.comboBox().setSelection("double (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		
		bot.comboBox().setSelection("float (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		
		bot.comboBox().setSelection("longlong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("long (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("short (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("-1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ulong (32-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ulonglong (64-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("ushort (16-bit)");
		bot.comboBox(1).setSelection("real");
		bot.textWithLabel(valueLabel).setText("-1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("-1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormInvalid();
		bot.comboBox(1).setSelection("complex");
		bot.textWithLabel(valueLabel).setText("1.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("1.1+j10.1");
		assertFormInvalid();
		bot.textWithLabel(valueLabel).setText("1+j1");
		assertFormValid();
		bot.textWithLabel(valueLabel).setText("");
		bot.comboBox(1).setSelection("");
		assertFormValid();
		
		bot.comboBox().setSelection("objref");
		bot.textWithLabel(valueLabel).setText("1");
		assertFormInvalid();
	}

	@Test
	public void testOptional() {
		testOptional(false);
	}
	
	protected void testOptional(boolean shouldExist) {
		boolean found;
		try {
			SWTBotCombo combo = bot.comboBoxWithLabel("Optional:");
			found = true;
			if (shouldExist) {
				String[] testScenarios = { "false", "true", "" };
				String text = combo.getText();
				Assert.assertEquals("default optional selection", "", text);
				for (String test : testScenarios) {
					combo.setSelection(test);
					text = combo.getText();
					Assert.assertEquals("text after selection", test, text);
					assertFormValid();
				}
			}
		} catch (WidgetNotFoundException e) {
			found = false;
		}
		Assert.assertEquals("optional field exist", shouldExist, found);
	}
}
