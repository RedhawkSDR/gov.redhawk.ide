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

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

/**
 * 
 */
public class SharedLibraryUtils {

	public static final String SHARED_LIBRARY_MENU_NAME = "REDHAWK Shared Library Project";

	/** private to prevent instantiation since all functions are static. */
	private SharedLibraryUtils() {
	}

	public static void createSharedLibraryProject(SWTBot bot, String sharedLibraryProjectName, String projectType) {
		StandardTestActions.configurePyDev(bot);

		bot.menu().menu("File", "New", "Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();

		SWTBotTreeItem treeItem = StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", SHARED_LIBRARY_MENU_NAME));
		treeItem.select();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(sharedLibraryProjectName);
		wizardBot.comboBoxWithLabel("Type:").setSelection(projectType);
		wizardBot.button("Finish").click();

		bot.waitUntil(Conditions.shellCloses(wizardShell));
	}
}
