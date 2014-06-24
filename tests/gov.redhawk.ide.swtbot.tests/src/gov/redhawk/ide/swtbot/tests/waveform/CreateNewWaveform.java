package gov.redhawk.ide.swtbot.tests.waveform;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;

public class CreateNewWaveform {

	/**
	 * Creates a new waveform using File > New > SCA Waveform Project wizard
	 * @param bot - the executing SWTBot
	 * @param waveformName
	 */
	public static void createNewWaveform(SWTBot bot, String waveformName) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu waveformMenu = newMenu.menu("SCA Waveform Project");
		waveformMenu.click();

		// Enter the name for the new waveform
		bot.textWithLabel("Project name:").setText(waveformName);

		// 
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
		SWTBotMenu fileMenu = bot.menu("File");
		SWTBotMenu newMenu = fileMenu.menu("New");
		SWTBotMenu waveformMenu = newMenu.menu("SCA Waveform Project");
		waveformMenu.click();

		// Enter the name for the new waveform
		bot.textWithLabel("Project name:").setText(waveformName);

		// Click next
		SWTBotButton nextButton = bot.button("Next >");
		nextButton.click();

		// Wait as the assembly controller table populates
		bot.sleep(1000);
		
		// Select AC for new waveform
		SWTBotTable acTable = bot.table();
		if (assemblyControllerType != null && acTable.containsItem(assemblyControllerType)) {
			acTable.select(assemblyControllerType);
		} else {
			acTable.select(0);
		}
		
		// Click finish
		SWTBotButton finishButton = bot.button("Finish");
		finishButton.click();
	}

}
