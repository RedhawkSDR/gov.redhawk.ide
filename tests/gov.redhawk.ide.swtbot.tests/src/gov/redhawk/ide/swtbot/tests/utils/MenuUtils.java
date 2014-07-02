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
package gov.redhawk.ide.swtbot.tests.utils;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

public class MenuUtils {

	protected MenuUtils() {
	}

	public static final String CONSOLE = "Console", CORBA_NAME_BROWSER = "CORBA Name Browser", ERROR_LOG = "Error Log", 
			EVENT_VIEWER = "Event Viewer", OUTLINE = "Outline", PROBLEMS = "Problems", PROJECT_EXPLORER = "Project Explorer", 
			PROPERTIES = "Properties", SCA_EXPLORER = "SCA Explorer";

	public static final String NO = "No", YES = "Yes", DESELECT_ALL = "Deselect All", OK = "OK", DELETE = "Delete";

	/**
	 * Opens the indicated view. If already open, brings view into focus.
	 * @param bot
	 * @param view
	 */
	public static void showView(SWTBot bot, String view) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("Window");
		SWTBotMenu showViewMenu = fileMenu.menu("Show View");
		SWTBotMenu viewMenu = showViewMenu.menu(view);
		viewMenu.click();
	}

	public static void closeAllWithoutSave(SWTBot bot) {
		closeAll(bot, false);
	}

	public static void closeAll(SWTBot bot, boolean save) {
		bot.menu("File").menu("Close All").click();
		if (buttonExists(bot, NO)) { 
			// One unsaved resource
			if (!save) {
				bot.button(NO).click();
			} else {
				bot.button(YES).click();
			}
		} else if (buttonExists(bot, DESELECT_ALL)) { 
			// Multiple unsaved resources
			if (!save) {
				bot.button(DESELECT_ALL).click();
			}
			bot.button(OK).click();
		}
	}
	
	public static void save(SWTBot bot) {
		bot.menu("File").menu("Save");
	}

	private static boolean buttonExists(SWTBot bot, String buttonText) {
		try {
			bot.button(buttonText);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}
	
	public static void deleteNodeInProjectExplorer(SWTBot bot, String projectName) {
		bot.tree().getTreeItem(projectName).select().pressShortcut(Keystrokes.DELETE);
		bot.button(OK).click();
		bot.sleep(500);
	}
}
