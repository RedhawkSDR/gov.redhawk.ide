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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

public class WaveformUtils { // SUPPRESS CHECKSTYLE INLINE

	/**
	 * Creates a new waveform using File > New > Other... > SCA Waveform Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewWaveform(SWTBot bot, String waveformName) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		bot.shell("New").activate();
		bot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		bot.button("Next >").click();

		// Enter the name for the new waveform
		bot.textWithLabel("Project name:").setText(waveformName);

		// Close wizard
		SWTBotButton finishButton = bot.button("Finish");
		finishButton.click();
	}

	/**
	 * Creates a new waveform with an assembly controller using File > New > SCA Waveform Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewWaveformWithAssemblyController(SWTBot bot, String waveformName, String assemblyControllerType) {
		// Open the new waveform project wizard
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu otherMenu = newMenu.menu("Other...");
		otherMenu.click();
		bot.shell("New").activate();
		bot.tree().getTreeItem("SCA").expand().getNode("SCA Waveform Project").select();
		bot.button("Next >").click();

		// Enter the name for the new waveform
		bot.textWithLabel("Project name:").setText(waveformName);

		// Click next
		SWTBotButton nextButton = bot.button("Next >");
		nextButton.click();

		// Wait as the assembly controller table populates
		bot.sleep(1000);

		// Select AC for new waveform
		SWTBotTable acTable = bot.table();
		for (int row = 0; row < acTable.rowCount(); row++) {
			if (acTable.getTableItem(row).getText().contains(assemblyControllerType)) {
				acTable.select(row);
				break;
			}
		}

		// Click finish
		SWTBotButton finishButton = bot.button("Finish");
		finishButton.click();
	}

}
