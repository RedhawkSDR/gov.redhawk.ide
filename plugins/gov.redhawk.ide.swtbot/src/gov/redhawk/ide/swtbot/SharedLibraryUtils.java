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

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

/**
 * 
 */
public class SharedLibraryUtils {

	public static final String SHARED_LIBRARY_MENU_NAME = "SCA Shared Library Project";

	/** private to prevent instantiation since all functions are static. */
	private SharedLibraryUtils() {
	}

	public static void createSharedLibraryProject(SWTBot bot, String sharedLibraryProjectName, String projectType) {
		StandardTestActions.configurePyDev();

		SWTBotShell origShell = bot.activeShell();

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
				return "Could not find menu option for: " + SHARED_LIBRARY_MENU_NAME;
			}

			@Override
			public boolean test() throws Exception {
				try {
					wizardBot.tree().getTreeItem("SCA").expand().getNode(SHARED_LIBRARY_MENU_NAME).select();
					return true;
				} catch (WidgetNotFoundException e) {
					return false;
				}
			}

		});
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(sharedLibraryProjectName);
		wizardBot.comboBoxWithLabel("Type:").setSelection(projectType);
		SWTBotButton finishButton = wizardBot.button("Finish");
		wizardBot.waitUntil(Conditions.widgetIsEnabled(finishButton));
		finishButton.click();

		origShell.activate();
	}

	/**
	 * Generates the project using the Generate button in the overview tab
	 * Generates all files
	 */
	public static void generateSharedLibraryProject(SWTBot bot, SWTBotEditor editor) {
		DiagramTestUtils.openTabInEditor(editor, DiagramTestUtils.OVERVIEW_TAB);

		editor.bot().toolbarButton(0).click();
		bot.waitUntil(Conditions.shellIsActive("Regenerate Files"), 10000);
		SWTBotShell fileShell = bot.shell("Regenerate Files");
		fileShell.bot().button("OK").click();
	}
}
