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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.junit.Test;

public class WaveformNamingRaceConditionTest extends UITest {

	/**
	 * Tests a race condition - it was possible to enter an illegal character in the waveform name while completing
	 * the new SCA waveform wizard.
	 *  
	 * @throws Exception
	 */
	@Test
	public void test_IDE_826() throws Exception {
		bot.menu("File").menu("New").menu("Other...").click();
		
		SWTBotShell wizardShell = bot.shell("New");
		wizardShell.activate();
		bot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		bot.button("Next >").click();

		SWTBotText projectNameField = bot.textWithLabel("Project name:");
		projectNameField.setText("test_IDE_826");

		bot.button("Finish").click();
		
		projectNameField.setText("test_IDE_826_bad");

		bot.waitUntil(Conditions.shellCloses(wizardShell));

		SWTBotView navigatorView = bot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		navigatorView.show();
		navigatorView.setFocus();
		
		navigatorView.bot().tree().select("test_IDE_826");
	}
}
