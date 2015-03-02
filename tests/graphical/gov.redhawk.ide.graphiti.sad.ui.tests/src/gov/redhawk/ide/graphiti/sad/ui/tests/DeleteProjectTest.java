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
package gov.redhawk.ide.graphiti.sad.ui.tests;

import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.junit.Assert;
import org.junit.Test;

public class DeleteProjectTest extends AbstractGraphitiTest {
	
	private SWTBotGefEditor editor;
	private String waveformName;
	
	/**
	 * IDE-880
	 * Diagram editor should close if the respective project is deleted
	 */
	@Test
	public void confirmEditorClosesOnDelete() {
		waveformName = "Delete_and_Close";
		final String SIGGEN = "SigGen";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);
		editor = gefBot.gefEditor(waveformName);
		List< ? extends SWTBotEditor> editors = gefBot.editors();
		Assert.assertEquals("Editor not found", 1, editors.size());

		// Add component to the diagram
		DiagramTestUtils.addFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		
		// Delete project from the project explorer
		SWTBotView projectExplorerView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectExplorerView.setFocus();
		gefBot.tree().select(waveformName).contextMenu("Delete").click();
		
		gefBot.shell("Delete Resources").setFocus();
		gefBot.checkBox(0).click();
		gefBot.button("OK").click();
		
		// Make sure the editor closed
		bot.waitUntil(new ICondition() {

			@Override
			public boolean test() throws Exception {
				return gefBot.editors().size() == 0;
			}

			@Override
			public void init(SWTBot bot) {
				
			}

			@Override
			public String getFailureMessage() {
				return "Editor did not close";
			}
			
		}, 30000, 1000);
		editors = gefBot.editors();
		Assert.assertEquals("Editor did not close", 0, editors.size());
	}
}
