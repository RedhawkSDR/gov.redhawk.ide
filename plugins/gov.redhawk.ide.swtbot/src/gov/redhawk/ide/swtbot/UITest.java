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
package gov.redhawk.ide.swtbot;

import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keyboard;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class UITest extends StandardTestActions {

	/**
	 * 
	 * @param item
	 * @param column Column to write to, NOTE the first column is '1'
	 * @param text
	 */
	protected void writeToCell(final SWTBotTreeItem item, final int column, final String text) {
		item.click(column);
		
		// Wait for cell editor to appear
		bot.sleep(500);
		
		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.typeText(text);
		keyboard.pressShortcut(Keystrokes.CR);
		
		// Wait for cell editor to close
		bot.sleep(100);

	}

	/**
	 * Writes the text to the specified cell of parent table in loverCase letters by alphanumeric keys pressing.
	 * 
	 * @param table
	 * - the parent table.
	 * @param row
	 * - the row number.
	 * @param column
	 * - the column number.
	 * @param text
	 * - the alphanumeric text.
	 * @param editorActivationType
	 * - the editor activation type.
	 */
	protected void writeToCell(SWTBotTable table, final int row, final int column, final String text) {
		table.click(row, column);
		
		// Wait for cell editor to appear
		bot.sleep(500);
		
		Keyboard keyboard = KeyboardFactory.getSWTKeyboard();
		keyboard.typeText(text);
		keyboard.pressShortcut(Keystrokes.CR);
		
		// Wait for cell editor to close
		bot.sleep(100);
	}

}
