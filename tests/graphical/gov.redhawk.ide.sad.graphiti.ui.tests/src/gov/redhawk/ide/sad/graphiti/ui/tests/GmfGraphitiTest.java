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
package gov.redhawk.ide.sad.graphiti.ui.tests;

import gov.redhawk.ide.swtbot.SWTBotRadioMenu;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class GmfGraphitiTest extends AbstractGraphitiTest {

	private String waveformName;

	@Test
	public void openDiagramGmfAndGraphiti() {

		waveformName = "Diagram_Type";

		// Create a new empty waveform, close editor
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		gefBot.waitUntil(new WaitForEditorCondition());
		SWTBotGefEditor editor = gefBot.gefEditor(waveformName);
		editor.close();

		// Focus on the project explorer view
		SWTBotView projectView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectView.setFocus();
		SWTBotTree projectTree = projectView.bot().tree();
		SWTBotTreeItem sadFileTreeItem = projectTree.expandNode(waveformName).getNode(waveformName + ".sad.xml");

		// Open Graphiti editor
		SWTBotMenu menu = sadFileTreeItem.select().contextMenu("Open With").menu("Waveform Editor");
		new SWTBotRadioMenu(menu).click();

		// Find the editor by name to ensure it opened, then close it
		gefBot.waitUntil(new WaitForEditorCondition());
		editor = gefBot.gefEditor(waveformName);
		editor.close();

		// Open GMF editor
		menu = sadFileTreeItem.select().contextMenu("Open With").menu("Legacy Waveform Editor");
		new SWTBotRadioMenu(menu).click();

		// Find the editor by name to ensure it opened, then close it
		gefBot.waitUntil(new WaitForEditorCondition());
		editor = gefBot.gefEditor(waveformName);
		editor.close();
	}
}
