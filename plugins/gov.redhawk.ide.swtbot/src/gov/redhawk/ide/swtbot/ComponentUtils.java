/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.swtbot;

import java.util.Arrays;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import gov.redhawk.ide.swtbot.condition.SelectIDL;

public class ComponentUtils {

	public static final String NEW_COMPONENT_WIZARD_NAME = "REDHAWK Component Project";
	private static final long CREATE_NEW_PROJECT_DELAY = 10000;

	private ComponentUtils() {
	}

	/**
	 * Create a Redhawk component using the new project wizard.
	 * @param bot
	 * @param componentProjectName
	 * @param progLanguage
	 */
	public static void createComponentProject(SWTBot bot, String componentProjectName, String progLanguage) {
		SWTBotShell origShell = bot.activeShell();

		StandardTestActions.configurePyDev(bot);

		bot.menu("File").menu("New").menu("Project...").click();
		SWTBotShell wizardShell = bot.shell("New Project");
		wizardShell.activate();
		final SWTBot wizardBot = wizardShell.bot();
		StandardTestActions.waitForTreeItemToAppear(wizardBot, wizardBot.tree(), Arrays.asList("REDHAWK", NEW_COMPONENT_WIZARD_NAME)).select();
		wizardBot.button("Next >").click();

		wizardBot.textWithLabel("Project name:").setText(componentProjectName);
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

	public static void addComponentPort(SWTBot editorBot, String portName, PortDirection portDirection) {
		addComponentPort(editorBot, portName, portDirection, "BULKIO", "dataDouble");
	}

	public static void addComponentPort(SWTBot editorBot, String portName, PortDirection portDirection, String idlModule, String idlInterface) {
		editorBot.cTabItem("Ports").activate();
		editorBot.button("Add").click();
		editorBot.textWithLabel("Name*:").setText(portName);
		editorBot.comboBoxWithLabel("Direction:").setSelection(portDirection.toString());
		selectIDL(editorBot, idlModule, idlInterface, true);
	}

	public enum PortDirection {
		IN("in <provides>"),
		OUT("out <uses>"),
		BI_DIRECTIONAL("bi-dir <uses/provides");

		private String labelText;

		private PortDirection(String labelText) {
			this.labelText = labelText;
		}

		@Override
		public String toString() {
			return this.labelText;
		}
	}

	private static void selectIDL(SWTBot bot, String module, String intf, boolean showAll) {
		bot.button("Browse...").click();
		SWTBotShell dialogShell = bot.shell("Select an interface");
		SWTBot dialogBot = dialogShell.bot();
		if (showAll) {
			dialogBot.checkBox("Show all interfaces").select();
		}
		try {
			dialogBot.waitUntil(new SelectIDL(module, intf));
		} catch (TimeoutException ex) {
			dialogBot.button("Cancel").click();
			bot.waitUntil(Conditions.shellCloses(dialogShell));
			throw ex;
		}
		dialogBot.button("OK").click();
		bot.waitUntil(Conditions.shellCloses(dialogShell));
	}

}
