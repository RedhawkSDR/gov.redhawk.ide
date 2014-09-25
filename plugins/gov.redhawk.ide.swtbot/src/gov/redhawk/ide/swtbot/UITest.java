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

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swtbot.swt.finder.keyboard.KeyboardFactory;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

public class UITest extends StandardTestActions {

	/**
	 * Gets a KeyStroke for the key defined by the keyCode.
	 * 
	 * @param keyCode
	 * - any SWT keyCode.
	 * @return The new KeyStroke instance for the specified key.
	 */
	protected KeyStroke getKey(final int keyCode) {
		return KeyStroke.getInstance(keyCode);
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
	 */
	protected void writeToCell(final TableViewer viewer, final int row, final int column, final String text) {
		writeToCell(viewer, row, column, text, UITestConstants.MOUSE_CLICK_ACTIVATION);
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
	protected void writeToCell(final TableViewer viewer, final int row, final int column, final String text, final int editorActivationType) {

		final SWTBotTable table = new SWTBotTable(viewer.getTable());

		table.select(row);

		SWTBotTableItem item = table.getTableItem(row);

		startEditing(viewer, row, column, editorActivationType);

		KeyboardFactory.getSWTKeyboard().typeText(text);
		KeyboardFactory.getSWTKeyboard().pressShortcut(Keystrokes.CR);
	}

	/**
	 * Starts the cell editor for the cell specified by row and column number.
	 * 
	 * @param table
	 * - the parent table.
	 * @param row
	 * - the row number.
	 * @param column
	 * - the column number.
	 * @param editorActivationType
	 * - the editor activation type.
	 * @throws IllegalArgumentException
	 * if an invalid editor activation type specified.
	 * @see MOUSE_CLICK_ACTIVATION
	 */
	protected void startEditing(final TableViewer viewer, final int row, final int column, final int editorActivationType) {

		final SWTBotTable table = new SWTBotTable(viewer.getTable());

		switch (editorActivationType) {
		case UITestConstants.MOUSE_CLICK_ACTIVATION:
			table.click(row, column);
			break;
		case UITestConstants.MOUSE_DOUBLE_CLICK_ACTIVATION:
			table.doubleClick(row, column);
			break;
		case UITestConstants.F2_KEY_ACTIVATION:
			table.click(row, column);
			table.pressShortcut(getKey(SWT.F2));
			break;
		default:
			throw new IllegalArgumentException("Can`t support the specified editor activation type.");
		}
		waitForCellEditorState(viewer, true);
	}

	/**
	 * Generates a Random integer number within a given range.
	 * 
	 * @param begin
	 * - the range begin.
	 * @param end
	 * - the range end.
	 * @return random number that is in the given range.
	 */
	protected int randomInt(final int begin, final int end) {
		return (int) (begin + (Math.random() * ((end - begin) + 1)));
	}

	/**
	 * Waits until the default timeout is reached or until the TableViewer cell editor`s "isActive()" state becomes
	 * equal to the specified state (true/false).
	 * 
	 * @param tableWiewer
	 * - the TableViewer to retrieve the cell editor state.
	 * @param condition
	 * -
	 */
	protected void waitForCellEditorState(final TableViewer tableWiewer, final boolean condition) {
		bot.waitUntil(new CellEditorWaiter(tableWiewer, condition), UITestConstants.DEFAULT_CELL_EDITOR_TIMEOUT);
	}

	/**
	 * Waits for UI repaint.
	 */
	protected void waitForUIRepaint() {
		bot.sleep(100);
	}

	/**
	 * Causes the bot to wait.
	 * 
	 * @param millis
	 * - wait time in milliseconds.
	 * */
	protected void sleep(final int millis) {
		bot.sleep(millis);
	}

	class CellEditorWaiter extends DefaultCondition {

		private final TableViewer tableWiewer;
		private final boolean condition;

		// initialize
		CellEditorWaiter(final TableViewer tableWiewer, final boolean condition) {
			this.tableWiewer = tableWiewer;
			this.condition = condition;
		}

		// return true if the condition matches, false otherwise
		@Override
		public boolean test() {
			return tableWiewer.isCellEditorActive() == condition;
		}

		// provide a human readable error message
		@Override
		public String getFailureMessage() {
			return "Timed out while waiting for the cell editor.";
		}
	}

}
