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

public class ConsoleUtils {

	protected ConsoleUtils() {

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
	 * Terminate process in console
	 * @param bot
	 * @param processName
	 */
	public static void terminateProcess(SWTWorkbenchBot bot, final String processName) {
		SWTBotView view = ViewUtils.getConsoleView(bot);
		view.show();

		final Matcher<MenuItem> matcher = new AnyMenuItemMatcher<MenuItem>();

		// Switch consoles until we hit the right one
		SWTBotToolbarDropDownButton consoleButton = (SWTBotToolbarDropDownButton) view.toolbarButton("Display Selected Console");
		int consoles = consoleButton.menuItems(matcher).size();
		boolean found = false;
		for (int i = 0; i < consoles; i++) {
			if (view.bot().label(0).getText().contains(processName)) {
				found = true;
				break;
			}
			consoleButton.click();
		}
		if (!found) {
			throw new WidgetNotFoundException(String.format("Can't find console for %s", processName));
		}

		// Click terminate, wait for it to disable (indicating process ended)
		final SWTBotToolbarButton terminateButton = view.toolbarButton("Terminate");
		terminateButton.click();
		bot.waitUntil(new DefaultCondition() {
			@Override
			public boolean test() throws Exception {
				return !terminateButton.isEnabled();
			}

			@Override
			public String getFailureMessage() {
				return String.format("Process %s failed to terminate", processName);
			}
		});
	}

}
