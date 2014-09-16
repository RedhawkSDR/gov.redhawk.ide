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

import gov.redhawk.ide.swtbot.WaveformUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class GmfGraphitiTests extends AbstractGraphitiTest {

	private String waveformName;

	@Test
	public void openDiagramGmfAndGraphiti() {

		waveformName = "Diagram_Type";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		SWTBotGefEditor editor = gefBot.gefEditor(waveformName);
		editor.close();

		// Focus on the project explorer view
		SWTBotView projectView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectView.setFocus();
		SWTBotTree projectTree = projectView.bot().tree();
		projectTree.expandNode(waveformName);
		
		// Open Graphiti editor
		projectTree.getTreeItem(waveformName).getNode(waveformName + ".sad.xml").select().contextMenu("Open With").menu("Graphiti Waveform Editor").click();
		
		editor = gefBot.gefEditor(waveformName);
		Assert.assertNotNull(editor);
		editor.close();
		
		projectView.setFocus();

		// Open GMF editor
		SWTBotMenu openWith = projectTree.getTreeItem(waveformName).getNode(waveformName + ".sad.xml").select().contextMenu("Open With");
		SWTBotMenu menuItem = openWith.menu("Waveform Editor");
		menuItem.click();

		editor = gefBot.gefEditor(waveformName);
		Assert.assertNotNull(editor);
		editor.close();
		
		projectView.setFocus();

		// Open Graphiti editor
		projectTree.getTreeItem(waveformName).getNode(waveformName + ".sad.xml").select().contextMenu("Open With").menu("Graphiti Waveform Editor").click();
	}
}
