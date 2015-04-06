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
package gov.redhawk.ide.graphiti.sad.ui.runtime.chalkboard.tests;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.diagram.RHTestBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiChalkboardTest extends UIRuntimeTest {

	static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	static final String CHALKBOARD = "Chalkboard";
	static final String CHALKBOARD_TAB_TITLE = "Chalkboard";

	// Common Test Component Names
	static final String HARD_LIMIT = "HardLimit";
	static final String SIGGEN = "SigGen";

	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE VisibilityModifier

	@Before
	public void beforeTest() throws Exception {
		gefBot = new RHTestBot();
	}
	
	@After
	public void afterTest() {
		ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		
		//wait until waveform empty
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD);
		
		//close editors
		gefBot.closeAllEditors();
		gefBot = null;
	}

	/** Helper method to open Sandbox "Waveform" Chalkboard Graphiti Diagram */
	static SWTBotGefEditor openChalkboardDiagram(SWTGefBot gefBot) {
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, CHALKBOARD_PARENT_PATH, CHALKBOARD, DiagramType.GRAPHITI_CHALKBOARD);
		SWTBotGefEditor editor = gefBot.gefEditor(CHALKBOARD_TAB_TITLE);
		editor.setFocus();
		return editor;
	}

}
