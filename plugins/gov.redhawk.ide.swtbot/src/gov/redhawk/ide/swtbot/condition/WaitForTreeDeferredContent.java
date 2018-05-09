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

import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.BoolResult;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.ui.progress.PendingUpdateAdapter;

public class WaitForTreeDeferredContent extends DefaultCondition {

	private SWTBotTree tree;
	private String errorMessage;

	public WaitForTreeDeferredContent(SWTBotTree tree) {
		this.tree = tree;
	}

	@Override
	public boolean test() throws Exception {
		return UIThreadRunnable.syncExec(tree.display, new BoolResult() {
			@Override
			public Boolean run() {
				if (tree.widget.isDisposed()) {
					errorMessage = "Tree was disposed";
					return false;
				}

				TreeItem[] treeItems = tree.widget.getItems();
				if (treeItems.length == 0) {
					errorMessage = "Tree had zero tree items";
					return false;
				}

				Object firstItemData = treeItems[0].getData();
				if (firstItemData == null) {
					errorMessage = "First tree item's data is null";
					return false;
				} else if (firstItemData instanceof PendingUpdateAdapter) {
					errorMessage = "First item in tree was a PendingUpdateAdapater";
					return false;
				} else {
					errorMessage = "";
					return true;
				}
			}
		});
	}

	@Override
	public String getFailureMessage() {
		return errorMessage;
	}

}
