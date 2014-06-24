package gov.redhawk.ide.swtbot.tests.menus;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;

public class ShowView {

	public static String CONSOLE = "Console", CORBA_NAME_BROWSER = "CORBA Name Browser", ERROR_LOG = "Error Log", 
			EVENT_VIEWER = "Event Viewer", OUTLINE = "Outline", PROBLEMS = "Problems", PROJECT_EXPLORER = "Project Explorer", 
			PROPERTIES = "Properties", SCA_EXPLORER = "SCA Explorer";

	/**
	 * Opens the indicated view. If already open, brings view into focus.
	 * @param bot
	 * @param view
	 */
	public static void showView(SWTBot bot, String view) {
		// Open the new waveform project wizard
		SWTBotMenu fileMenu = bot.menu("Window");
		SWTBotMenu showViewMenu = fileMenu.menu("Show View");
		SWTBotMenu viewMenu = showViewMenu.menu(view);
		viewMenu.click();
	}

}
