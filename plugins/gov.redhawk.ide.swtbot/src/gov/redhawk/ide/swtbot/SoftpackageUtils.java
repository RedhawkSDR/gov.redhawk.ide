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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * 
 */
public class SoftpackageUtils {

	public static final String SOFTPACKAGE_MENU_NAME = "SCA Softpackage Project";

	/** private to prevent instantiation since all functions are static. */
	private SoftpackageUtils() {
	}

	public static void createSoftpackageProject(SWTBot bot, String softpackageProjectName, String projectType) {
		SWTBotShell origShell = bot.activeShell();
		StandardTestActions.configurePyDev();

		if (projectType == null) {
			projectType = "C++ Library";
		}

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		wizardBot.waitUntil(new DefaultCondition() {

			@Override
			public String getFailureMessage() {
				return "Could not find menu option for: " + SOFTPACKAGE_MENU_NAME;
			}

			@Override
			public boolean test() throws Exception {
				try {
					wizardBot.tree().getTreeItem("SCA").expand().getNode(SOFTPACKAGE_MENU_NAME).select();
					return true;
				} catch (WidgetNotFoundException e) {
					return false;
				}
			}

		});
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(softpackageProjectName);
		wizardBot.comboBoxWithLabel("Type:").setSelection(projectType);
		wizardBot.button("Finish").click();

		origShell.activate();
	}
}
