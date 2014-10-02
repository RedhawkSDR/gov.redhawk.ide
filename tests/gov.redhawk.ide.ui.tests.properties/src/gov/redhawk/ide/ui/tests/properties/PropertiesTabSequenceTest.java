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

import gov.redhawk.ide.swtbot.StandardTestActions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.junit.Assert;
import org.junit.Test;

public class PropertiesTabSequenceTest extends AbstractBasicPropertyTest {

	@Test
	public void testValues() throws CoreException {
		SWTBotTable valuesViewer = editor.bot().tableWithLabel("Values:");
		// Start with type selected as string
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("true");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "a");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "true");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("true");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("char");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		bot.button("OK").click();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "abc");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("double (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "al");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("float (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("abc");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1+jjak");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1.1+j10.1");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-1.1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-1.1+j10.1");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("long (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("longlong (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1.1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("short (16-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("-11-j2");
		bot.button("OK").click();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1+100iada");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-11-j2");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("-11-j2");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ulong (32-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ulonglong (64-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "-1");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("ushort (16-bit)");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("complex");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "1+j1ada");
		assertFormInvalid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11");
		assertFormValid();
		StandardTestActions.writeToCell(bot, valuesViewer, 0, 0, "11+j2");
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("11");
		Assert.assertTrue("OK should be enabled", bot.button("OK").isEnabled());
		bot.textWithLabel("Value:").setText("11+j2");
		bot.button("OK").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("objref");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("1");
		Assert.assertFalse("OK should not be enabled", bot.button("OK").isEnabled());
		bot.button("Cancel").click();
		assertFormValid();
		clearValues();

		bot.comboBoxWithLabel("Type*:").setSelection("string");
		bot.comboBoxWithLabel("Type*:", 1).setSelection("");
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("abcd");
		bot.button("OK").click();
		assertFormValid();
		bot.button("Add...").click();
		bot.textWithLabel("Value:").setText("efg");
		bot.button("OK").click();
		assertFormValid();
		clearValues();
	}

	private void clearValues() {
		SWTBotTable valuesTable = bot.tableWithLabel("Values:");
		if (valuesTable.rowCount() > 0) {
			for (int i = 0; i <= valuesTable.rowCount(); i++) {
				valuesTable.select(0);
				SWTBotButton removeButton = bot.button("Remove", 1);
				if (removeButton.isEnabled()) {
					bot.button("Remove", 1).click();
				} else {
					break;
				}
			}
		}
	}

	@Override
	protected void createType() {
		bot.button("Add Sequence").click();
	}

	@Override
	public void testEnum() throws CoreException {
		// Override to do nothing since Simple Sequences do not have enums
	}
	
	@Test
	public void testKind() {
		assertFormValid();
		
		testKind(false);
	}
}
