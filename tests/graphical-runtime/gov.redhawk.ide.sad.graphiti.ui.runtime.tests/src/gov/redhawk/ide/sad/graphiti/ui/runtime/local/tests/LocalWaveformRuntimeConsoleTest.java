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
package gov.redhawk.ide.sad.graphiti.ui.runtime.local.tests;

import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 */
public class LocalWaveformRuntimeConsoleTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String SIGGEN = "SigGen";
	private static final String SIGGEN_1 = SIGGEN + "_1";
	private static final String CONSOLE_VIEW_LABEL = "Console";
	private static final String WAVEFORM_EDITOR_TITLE = "Waveform Chalkboard";
	
	/**
	 * IDE-1054 Making sure Console title is set correctly for component process
	 */
	@Test
	public void checkConsoleTitle() {
		editor = gefBot.gefEditor(WAVEFORM_EDITOR_TITLE);
		editor.setFocus();

		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		
		//wait for SIGGEN_1 to show up in Sca Explorer
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN_1);

		bot.cTabItem(CONSOLE_VIEW_LABEL).activate();
		SWTBotView consoleView = bot.activeView(); //.viewByTitle(CONSOLE_VIEW_LABEL);
		BaseMatcher<MenuItem> matcher = new BaseMatcher<MenuItem>() {

			@Override
			public boolean matches(Object item) {
				if (item instanceof MenuItem) {
					MenuItem menuItem = (MenuItem) item;
					String itemText = menuItem.getText();
					return itemText.contains(SIGGEN_1);
				}
				return false;
			}
			@Override
			public void describeTo(Description description) {
			}
		};
		consoleView.toolbarDropDownButton("Display Selected Console").menuItem(matcher).click();
		SWTBotLabel titleText = consoleView.bot().label();
		String title = titleText.getText();
		Assert.assertTrue("Console title does not start with component and waveform name", 
			title.startsWith(SIGGEN_1 + " [" + getWaveFormFullName() + "] "));
	}
	
}
