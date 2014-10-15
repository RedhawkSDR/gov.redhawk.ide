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

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ViewUtils {

	protected ViewUtils() {

	}

	/**
	 * Check to see if a specified tree item exists
	 * @param treeItem - The base tree object
	 * @param treePath - Array of strings to define path to tree item to be checked. Ex: "tree root", "node parent", "node to check for")
	 * @returns true if entire treePath exists, false if one or more elements in the treePath does not exist
	 */
	public static boolean checkIfTreeItemExistsEntry(SWTBotTree treeItem, String... treePath) {
		try {
			// Expand down to the depth of the tree item we are looking for
			String[] expandPath = new String[treePath.length - 1];
			for (int i = 0; i < expandPath.length; i++) {
				expandPath[i] = treePath[i];
			}
			SWTBotTreeItem parent = treeItem.expandNode(expandPath);

			// Check if tree item exists
			parent.getNode(treePath[treePath.length - 1]);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}

}
