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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCanvas;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

@SuppressWarnings("restriction")
public class ViewUtils {

	private static final String AUDIO_VIEW_ID = "gov.redhawk.ui.port.playaudio.view";
	private static final String CORBA_NAME_BROWSER_VIEW_ID = "gov.redhawk.ui.views.namebrowserview";
	private static final String CONSOLE_VIEW_ID = "org.eclipse.ui.console.ConsoleView";
	private static final String DATA_LIST_VIEW_ID = "gov.redhawk.datalist.ui.views.DataListView";
	private static final String PLOT_VIEW_ID = "gov.redhawk.ui.port.nxmplot.PlotView2";
	private static final String PORT_MONITOR_VIEW_ID = "gov.redhawk.ui.views.monitor.ports.PortMonitorView";
	private static final String PROBLEMS_VIEW_ID = "org.eclipse.ui.views.ProblemView";
	private static final String PROJECT_VIEW_ID = "org.eclipse.ui.navigator.ProjectExplorer";
	public static final String PROPERTIES_VIEW_ID = "org.eclipse.ui.views.PropertySheet";
	private static final String SRI_VIEW_ID = "gov.redhawk.bulkio.ui.sridata.view";

	/** private to prevent instantiation since all functions are static. */
	private ViewUtils() {
	}

	public static SWTBotView getAudioView(SWTWorkbenchBot bot) {
		return bot.viewById(AUDIO_VIEW_ID);
	}

	public static SWTBotView getCorbaNameBrowserView(SWTWorkbenchBot bot) {
		return bot.viewById(CORBA_NAME_BROWSER_VIEW_ID);
	}

	public static SWTBotView getConsoleView(SWTWorkbenchBot bot) {
		return bot.viewById(CONSOLE_VIEW_ID);
	}

	public static SWTBotView getDataListView(SWTWorkbenchBot bot) {
		return bot.viewById(DATA_LIST_VIEW_ID);
	}

	public static SWTBotView getExplorerView(SWTWorkbenchBot bot) {
		return bot.viewById(ScaExplorerTestUtils.SCA_EXPLORER_VIEW_ID);
	}

	public static SWTBotView getPlotView(SWTWorkbenchBot bot) {
		return bot.viewById(PLOT_VIEW_ID);
	}

	public static SWTBotView getPortMonitorView(SWTWorkbenchBot bot) {
		return bot.viewById(PORT_MONITOR_VIEW_ID);
	}

	public static SWTBotView getProblemsView(SWTWorkbenchBot bot) {
		return bot.viewById(PROBLEMS_VIEW_ID);
	}

	public static SWTBotView getProjectView(SWTWorkbenchBot bot) {
		return bot.viewById(PROJECT_VIEW_ID);
	}

	public static SWTBotView getSRIView(SWTWorkbenchBot bot) {
		return bot.viewById(SRI_VIEW_ID);
	}

	/**
	 * Presses the 'Start Acquire button on the Data List View
	 */
	public static void startAquireOnDataListView(SWTWorkbenchBot bot) {
		final SWTBotView dataListView = ViewUtils.getDataListView(bot);
		SWTBotButton startButton = dataListView.bot().buttonWithTooltip("Start Acquire");
		startButton.click();
	}

	/**
	 * Waits until SRI Plot View displays and is populated
	 * @param bot
	 */
	public static void waitUntilSRIViewPopulates(final SWTWorkbenchBot bot) {
		final SWTBotView sriView = getSRIView((SWTWorkbenchBot) bot);
		bot.waitWhile(Conditions.treeHasRows(sriView.bot().tree(), 0));
	}

	/**
	 * Waits until Audio View displays and is populated
	 * @param bot
	 */
	public static void waitUntilAudioViewPopulates(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Audio View property rows did not populate";
			}

			@Override
			public boolean test() throws Exception {
				final SWTBotView audioView = getAudioView((SWTWorkbenchBot) bot);
				return audioView.bot().list().getItems().length > 0;
			}
		});
	}

	/**
	 * Waits until Data List View displays
	 * @param bot
	 */
	public static SWTBotView waitUntilDataListViewDisplays(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "DataList View isn't displayed";
			}

			@Override
			public boolean test() throws Exception {
				return getDataListView((SWTWorkbenchBot) bot) != null;
			}
		});
		return getDataListView((SWTWorkbenchBot) bot);
	}

	/**
	 * Waits until Data List View populates
	 * @param bot
	 */
	public static void waitUntilDataListViewPopulates(final SWTWorkbenchBot bot) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "DataList view didn't populate";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotView dataListView = getDataListView((SWTWorkbenchBot) bot);
				return dataListView.bot().table().rowCount() > 10;
			}
		});
	}

	/**
	 * Waits until PortMonitor View populates
	 * @param bot
	 * @param componentInstanceName The component's instance name (e.g. foo_1)
	 */
	public static void waitUntilPortMonitorViewPopulates(final SWTWorkbenchBot bot, final String componentInstanceName) {
		bot.waitUntil(new DefaultCondition() {
			@Override
			public String getFailureMessage() {
				return "Port Monitor View isn't populated";
			}

			@Override
			public boolean test() throws Exception {
				SWTBotView monitorView = ViewUtils.getPortMonitorView((SWTWorkbenchBot) bot);
				for (SWTBotTreeItem item : monitorView.bot().tree().getAllItems()) {
					if (item.getText().matches(componentInstanceName + ".*")) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public static SWTBotTree selectPropertiesTab(SWTWorkbenchBot bot, final String label) {
		// Matcher for a tab in the property tab list
		Matcher<TabbedPropertyList.ListElement> matcher = new BaseMatcher<TabbedPropertyList.ListElement>() {
			@Override
			public boolean matches(Object item) {
				if (!(item instanceof TabbedPropertyList.ListElement)) {
					return false;
				}
				TabbedPropertyList.ListElement listElement = (TabbedPropertyList.ListElement) item;
				return label.equals(listElement.getTabItem().getText());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("Of type TabbedPropertyList.ListElement with text " + label);
			}
		};

		// Open the properties view, find the tab
		SWTBotView view = bot.viewById(PROPERTIES_VIEW_ID);
		view.show();
		final TabbedPropertyList.ListElement listElement = (TabbedPropertyList.ListElement) view.bot().widget(matcher);

		// Convert to Canvas (parent type), then click with SWTBot
		new SWTBotCanvas(listElement).click();

		return view.bot().tree();
	}
}
