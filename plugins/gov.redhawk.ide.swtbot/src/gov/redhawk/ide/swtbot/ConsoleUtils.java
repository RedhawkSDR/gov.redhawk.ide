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

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import gov.redhawk.ide.swtbot.condition.WaitForWidgetEnablement;

public class ConsoleUtils {

	/** private to prevent instantiation since all functions are static. */
	private ConsoleUtils() {
	}

	public static class AnyMenuItemMatcher< T extends MenuItem > extends AbstractMatcher<T> {

		AnyMenuItemMatcher() {
		}

		@Override
		public void describeTo(Description description) {
		}

		@Override
		protected boolean doMatch(Object item) {
			return true;
		}
	}

	/**
	 * Shows the console for a particular process.
	 * @param bot
	 * @param processName
	 * @return The SWTBot console view
	 */
	public static SWTBotView showConsole(SWTWorkbenchBot bot, final String processName) {
		SWTBotView view = ViewUtils.getConsoleView(bot);
		view.show();

		// We could already be on the correct console
		String text = view.bot().label().getText();
		if (text.contains(processName)) {
			return view;
		}

		// Switch consoles until we hit the right one
		SWTBotToolbarDropDownButton consoleButton = (SWTBotToolbarDropDownButton) view.toolbarButton("Display Selected Console");
		int consoles = consoleButton.menuItems(new AnyMenuItemMatcher<MenuItem>()).size();
		for (int i = 0; i < consoles; i++) {
			if (view.bot().label(0).getText().contains(processName)) {
				return view;
			}
			consoleButton.click();
		}
		throw new WidgetNotFoundException(String.format("Can't find console for %s", processName));
	}

	/**
	 * Terminate a process via the console's terminate button
	 * @param bot
	 * @param processName
	 */
	public static void terminateProcess(SWTWorkbenchBot bot, final String processName) {
		SWTBotView view = showConsole(bot, processName);

		// Click terminate, wait for it to disable (indicating process ended)
		final SWTBotToolbarButton terminateButton = view.toolbarButton("Terminate");
		terminateButton.click();
		bot.waitUntil(new WaitForWidgetEnablement(terminateButton, false));
	}

	/**
	 * Terminate all processes in the console
	 * @param bot
	 */
	public static void terminateAllProcesses(SWTWorkbenchBot bot) {
		final SWTBotView view = ViewUtils.getConsoleView(bot);
		view.show();

		final Matcher<MenuItem> matcher = new AnyMenuItemMatcher<MenuItem>();

		// Cycle through each process, terminating it and removing it from the view
		SWTBotToolbarDropDownButton consoleButton = (SWTBotToolbarDropDownButton) view.toolbarButton("Display Selected Console");
		while (consoleButton.isEnabled()) {
			int consoles = consoleButton.menuItems(matcher).size();
			if (consoles <= 0) {
				break;
			}

			final SWTBotToolbarButton terminateButton = view.toolbarButton("Terminate");
			terminateButton.click();

			bot.waitUntil(new DefaultCondition() {

				@Override
				public boolean test() throws Exception {
					SWTBotToolbarButton removeTerminatedButton = view.toolbarButton("Remove All Terminated Launches");
					if (removeTerminatedButton.isEnabled()) {
						removeTerminatedButton.click();
						return true;
					}
					return false;
				}

				@Override
				public String getFailureMessage() {
					return "Remove all terminated launches button never enabled";
				}
			}, 10000);
		}
	}
}
