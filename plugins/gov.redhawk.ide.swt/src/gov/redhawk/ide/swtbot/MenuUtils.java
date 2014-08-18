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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;

public class MenuUtils {

	protected MenuUtils() {
	}

	private static final String NO = "No", YES = "Yes", DESELECT_ALL = "Deselect All", OK = "OK";

	private static boolean buttonExists(SWTBot bot, String buttonText) {
		try {
			bot.button(buttonText);
			return true;
		} catch (WidgetNotFoundException e) {
			return false;
		}
	}

	/**
	 * Close all open editors and delete the specified project from the file system
	 * @param bot
	 * @param waveformName
	 */
	public static void closeAndDelete(SWTBot bot, String waveformName) {
		closeAllWithoutSave(bot);
		deleteNodeInProjectExplorer(bot, waveformName);
	}

	/**
	 * Close all open editors without saving changes
	 * @param bot - SWTBot
	 */
	public static void closeAllWithoutSave(SWTBot bot) {
		closeAll(bot, false);
	}

	/**
	 * Close all open editors
	 * @param bot - SWTBot
	 * @param save - True if you want to save editor contents before closing
	 */
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

	/**
	 * Delete specified project from the file system
	 * @param bot
	 * @param projectName
	 */
	public static void deleteNodeInProjectExplorer(SWTBot bot, String projectName) {
		bot.tree().getTreeItem(projectName).select().pressShortcut(Keystrokes.DELETE).pressShortcut(Keystrokes.SPACE);
		bot.button(OK).click();
		bot.sleep(500);
	}

	/**
	 * Save all editors
	 * @param bot
	 */
	public static void save(SWTWorkbenchBot bot) {
		bot.saveAllEditors();
	}

	/**
	 * Opens the indicated view. If already open, brings view into focus.
	 * @param bot
	 * @param view
	 */
	public static void showView(SWTWorkbenchBot bot, String viewID) {
		// Open the new waveform project wizard
		SWTBotView viewToOpen = bot.viewById(viewID);
		viewToOpen.show();
	}

}
