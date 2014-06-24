package gov.redhawk.ide.swtbot.tests.waveform;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

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
	
}
