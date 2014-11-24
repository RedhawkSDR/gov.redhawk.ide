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

import java.util.List;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ConsoleUtils {

	protected ConsoleUtils() {
		
	}

	
	public static class AnyMenuItemMatcher<T extends MenuItem> extends AbstractMatcher<T> {

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
	public static void terminateProcess(SWTWorkbenchBot bot, String processName) {
		SWTBotView view = ViewUtils.getConsoleView(bot);
		
		final Matcher<MenuItem> matcher = new AnyMenuItemMatcher<MenuItem>();
		
		//switch to the desired process
		for (SWTBotToolbarButton button: view.getToolbarButtons()) {
			if ("Display Selected Console".equalsIgnoreCase(button.getToolTipText())) {
				SWTBotToolbarDropDownButton dropDownButton = (SWTBotToolbarDropDownButton) button;
				List< ? extends SWTBotMenu> menuItems = dropDownButton.menuItems(matcher);
				for (SWTBotMenu swtBotMenu: menuItems) {
					if (swtBotMenu.getText().contains(processName)) {
						swtBotMenu.click();
						view.getToolbarButtons().get(0).click(); //terminate button
						return;
					}
				}
			}
		}
		
		
	}
	
}
