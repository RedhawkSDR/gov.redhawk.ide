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

public class SharedLibraryUtils {

	public static final String NEW_SHARED_LIBRARY_WIZARD_NAME = "REDHAWK Shared Library Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private SharedLibraryUtils() {
	}

	/**
	 * Create a Redhawk shared library project using the new project wizard.
	 * @param bot
	 * @param sharedLibraryProjectName
	 * @param projectType
	 */
	public static void createSharedLibraryProject(SWTBot bot, String sharedLibraryProjectName, String projectType) {
		StandardTestActions.configurePyDev(bot);

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_SHARED_LIBRARY_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(sharedLibraryProjectName);
		wizardBot.comboBoxWithLabel("Type:").setSelection(projectType);
		wizardBot.button("Finish").click();

		bot.waitUntil(Conditions.shellCloses(wizardShell), CREATE_NEW_PROJECT_DELAY);
	}
}
