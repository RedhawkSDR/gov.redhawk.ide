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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class DeviceUtils {

	public static final String NEW_DEVICE_WIZARD_NAME = "REDHAWK Device Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private DeviceUtils() {
	}

	/**
	 * Create a Redhawk device using the new project wizard.
	 * @param bot
	 * @param deviceProjectName
	 * @param progLanguage
	 */
	public static void createDeviceProject(SWTBot bot, String deviceProjectName, String progLanguage) {
		SWTBotShell origShell = bot.activeShell();

		StandardTestActions.configurePyDev(bot);

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_DEVICE_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(deviceProjectName);
		wizardBot.button("Next >").click();

		wizardBot.comboBoxWithLabel("Prog. Lang:").setSelection(progLanguage);
		wizardBot.button("Next >").click();
		wizardBot.button("Finish").click();

		bot.waitUntil(Conditions.shellCloses(wizardShell), CREATE_NEW_PROJECT_DELAY);

		// For some reason, the main shell doesn't always receive focus back when using SWTBot
		if (!origShell.isActive()) {
			origShell.activate();
		}
	}

}
