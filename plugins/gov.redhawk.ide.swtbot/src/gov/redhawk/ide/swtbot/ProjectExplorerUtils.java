/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot;

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

public class ProjectExplorerUtils {

	public static final String PROJECT_EXPLORER_VIEW_ID = "org.eclipse.ui.navigator.ProjectExplorer";

	/** private to prevent instantiation since all functions are static. */
	private ProjectExplorerUtils() {
	}

	/**
	 * Double-clicks a project file to open it in it's default editor view
	 */
	public static void openProjectInEditor(SWTWorkbenchBot bot, String... nodes) {
		SWTBotTreeItem projectItem = selectNode(bot, nodes);
		projectItem.doubleClick();
	}

	/**
	 * Expands nodes in the project explorer to the given point, and selects the lowest level tree item
	 * @return
	 */
	public static SWTBotTreeItem selectNode(SWTWorkbenchBot bot, String... nodes) {
		SWTBotView view = bot.viewById(PROJECT_EXPLORER_VIEW_ID);
		view.setFocus();
		SWTBotTreeItem node = StandardTestActions.waitForTreeItemToAppear(view.bot(), view.bot().tree(), Arrays.asList(nodes));
		node.select();
		return node;
	}

	public static SWTBotTreeItem waitUntilNodeAppears(SWTWorkbenchBot bot, final String... nodePath) {
		SWTBotView view = bot.viewById(PROJECT_EXPLORER_VIEW_ID);
		view.setFocus();

		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return nodePath + " did not load into Project Explorer";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotTreeItem treeItem = selectNode((SWTWorkbenchBot) bot, nodePath);
				if (treeItem != null) {
					return true;
				}
				return false;
			}
		});

		return selectNode((SWTWorkbenchBot) bot, nodePath);
	}
}
