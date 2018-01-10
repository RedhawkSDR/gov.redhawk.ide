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
package gov.redhawk.ide.swtbot.diagram;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;

public class FindByUtils {

	public static final String FIND_BY_NAME = "Find By Name", FIND_BY_SERVICE = "Service", FIND_BY_DOMAIN_MANAGER = "Domain Manager",
			FIND_BY_EVENT_CHANNEL = "Event Channel", FIND_BY_FILE_MANAGER = "File Manager";

	private FindByUtils() {
	}

	public static void completeFindByWizard(SWTBot bot, String findByType, String name, String[] provides, String[] uses) {
		completeFindByWizard(bot, findByType, name, provides, uses, false);
	}

	/**
	 * Add a FindBy object to the Diagram from the palette
	 * @param editor
	 * @param findByType
	 * @param name - Name of FindBy element. If null, defaults to FindBy type
	 * @param provides - Array of provides port names. Can be null if no ports desired, or FindBy type doesn't support ports
	 * @param uses - Array of uses port names. Can be null if no ports desired, or FindBy type doesn't support ports
	 * @param isServiceIdl - Denotes if the the FindByService wizard should be completed using the Service Type field (true) or the Service Name field (false)
	 */
	public static void completeFindByWizard(SWTBot bot, String findByType, String name, String[] provides, String[] uses, boolean isServiceIdl) {
		if (name == null) {
			name = removeSpaces(findByType);
		}
		if (provides == null) {
			provides = new String[0];
		}
		if (uses == null) {
			uses = new String[0];
		}

		SWTBotShell shell;

		switch (findByType) {
		case FIND_BY_NAME:
			shell = bot.shell("Find By Name");
			addFindByCorbaName(shell.bot(), name, provides, uses);
			break;
		case FIND_BY_EVENT_CHANNEL:
			shell = bot.shell("Event Channel");
			addFindByEventChannel(shell.bot(), name);
			break;
		case FIND_BY_SERVICE:
			shell = bot.shell("Service");
			if (isServiceIdl) {
				addFindByServiceType(shell.bot(), name, provides, uses);
			} else {
				addFindByServiceName(shell.bot(), name, provides, uses);
			}
			break;
		default:
			// The find by does not have a dialog/wizard
			return;
		}

		bot.waitUntil(Conditions.shellCloses(shell));
	}

	private static void addFindByCorbaName(SWTBot bot, String name, String[] providesPortNames, String[] usesPortNames) {
		bot.textWithLabel("Component Name:").setText(name);
		for (String s : providesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 0).setText(s);
			bot.button("Add Provides Port").click();
		}
		for (String s : usesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 1).setText(s);
			bot.button("Add Uses Port").click();
		}
		bot.button("Finish").click();
	}

	private static void addFindByServiceName(SWTBot bot, String name, String[] providesPortNames, String[] usesPortNames) {
		bot.textWithLabel("Service Name:").setText(name);
		for (String s : providesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 0).setText(s);
			bot.button("Add Provides Port").click();
		}
		for (String s : usesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 1).setText(s);
			bot.button("Add Uses Port").click();
		}
		bot.button("Finish").click();
	}

	private static void addFindByServiceType(SWTBot bot, String name, String[] providesPortNames, String[] usesPortNames) {
		bot.radio(1).click();
		bot.button("Browse").click();
		bot.waitUntil(Conditions.shellIsActive("Select an interface"));
		final SWTBot idlBot = bot.shell("Select an interface").bot();
		idlBot.text(0).setText(name);

		if (name.indexOf("/") > -1) {
			name = name.substring(name.indexOf("/") + 1);
		}
		if (name.indexOf(":") > -1) {
			name = name.substring(0, name.indexOf(":"));
		}

		final String shortName = name;

		bot.waitUntil(new DefaultCondition() {

			@Override
			public boolean test() throws Exception {
				idlBot.tree().getTreeItem("CF").getNode(shortName).select();
				return true;
			}

			@Override
			public String getFailureMessage() {
				return shortName + "was not a selectable IDL";
			}
		});

		idlBot.button("OK").click();

		for (String s : providesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 0).setText(s);
			bot.button("Add Provides Port").click();
		}
		for (String s : usesPortNames) {
			bot.textInGroup("Port(s) to use for connections", 1).setText(s);
			bot.button("Add Uses Port").click();
		}
		bot.button("Finish").click();
	}

	private static void addFindByEventChannel(SWTBot bot, String name) {
		bot.textWithLabel("Name:").setText(name);
		bot.button("OK").click();
	}

	public static String getFindByDefaultName(String findByType) {
		if (findByType.equals(FIND_BY_NAME) || findByType.equals(FIND_BY_EVENT_CHANNEL) || findByType.equals(FIND_BY_SERVICE)) {
			return removeSpaces(findByType);
		}
		return findByType;
	}

	private static String removeSpaces(String str) {
		if (str.contains(" ")) {
			return str.replaceAll(" ", "");
		}
		return str;
	}

}
