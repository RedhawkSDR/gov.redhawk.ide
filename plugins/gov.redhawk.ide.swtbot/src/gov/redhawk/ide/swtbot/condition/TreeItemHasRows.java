/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.swtbot.condition;

import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class TreeItemHasRows extends DefaultCondition {

	private SWTBotTreeItem treeItem;

	public TreeItemHasRows(SWTBotTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	@Override
	public boolean test() throws Exception {
		treeItem.expand();
		if (treeItem.rowCount() == 0) {
			treeItem.collapse();
			return false;
		}
		return true;
	}

	@Override
	public String getFailureMessage() {
		return "Tree item had no child rows";
	}

}
