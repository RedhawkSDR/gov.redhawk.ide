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
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class WaitForCellValue extends DefaultCondition {

	private SWTBotTable table;
	private int tableRow;
	private int tableColumn;

	private SWTBotTreeItem treeItemRow;
	private int treeItemColumn;

	private String expectedValue;
	private String cellValue;

	public WaitForCellValue(SWTBotTable table, int row, int column, String expectedValue) {
		this.table = table;
		this.tableRow = row;
		this.tableColumn = column;
		this.expectedValue = expectedValue;
		cellValue = null;
		Assert.isNotNull(expectedValue);
	}

	public WaitForCellValue(SWTBotTreeItem row, int column, String expectedValue) {
		this.treeItemRow = row;
		this.treeItemColumn = column;
		this.expectedValue = expectedValue;
		cellValue = null;
		Assert.isNotNull(expectedValue);
	}

	@Override
	public boolean test() throws Exception {
		if (table != null) {
			cellValue = table.cell(tableRow, tableColumn);
			return expectedValue.equals(cellValue);
		} else {
			cellValue = treeItemRow.cell(treeItemColumn);
			return expectedValue.equals(cellValue);
		}
	}

	@Override
	public String getFailureMessage() {
		return String.format("Expected cell value is '%s' not '%s'", cellValue, expectedValue);
	}

}
