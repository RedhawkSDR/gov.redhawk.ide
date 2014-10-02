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
package gov.redhawk.ide.tests.ui;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;

public class WaveformNamingRaceConditionTest extends UITest {

	@Test
	public void test_IDE_826() throws Exception {
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		SWTBotShell wizardShell = bot.shell("New");
		wizardShell.activate();
		wizardShell.setFocus();
		bot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		bot.button("Next >").click();

		SWTBotText projectNameField = bot.textWithLabel("Project name:");
		projectNameField.setText("test_IDE_826");

		SWTBotButton finishButton = bot.button("Finish");
		finishButton.click();

		projectNameField.setText("test_IDE_826_bad");

		bot.waitUntil(Conditions.shellCloses(wizardShell), 10000);

		SWTBotView navigatorView = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		navigatorView.show();
		navigatorView.setFocus();
		
		navigatorView.bot().tree().select("test_IDE_826");
	}
}
