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
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * 
 */
public class ServiceUtils {

	public static final String NEW_SERVICE_WIZARD_NAME = "REDHAWK Service Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private ServiceUtils() {
	}

	/** create REDHAWK Service in Workspace using default location */
	public static void createServiceProject(SWTBot bot, String serviceProjectName, String interfaceName, String progLanguage) {
		SWTBotShell origShell = bot.activeShell();

		StandardTestActions.configurePyDev(bot);

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_SERVICE_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(serviceProjectName);
		setServiceIdl(bot, interfaceName);

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

	public static void setServiceIdl(SWTBot bot, String interfaceName) {
		final String shortName = interfaceName.substring(interfaceName.indexOf("/") + 1, interfaceName.lastIndexOf(":"));
		final String folder = interfaceName.substring(interfaceName.indexOf(":") + 1, interfaceName.indexOf("/"));
		bot.button("Browse...", 1).click();
		SWTBotShell idlShell = bot.shell("Select an interface");
		final SWTBot idlBot = idlShell.bot();
		idlBot.text().typeText(interfaceName);
		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				idlBot.tree().getTreeItem(folder).select(shortName);
				return true;
			}

			@Override
			public String getFailureMessage() {
				return "Could not find IDL in selection wizard";
			}
		});
		idlBot.button("OK").click();
	}
}
