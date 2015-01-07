/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.sad.graphiti.ui.runtime.local.tests;

import gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl;
import gov.redhawk.ide.swtbot.diagram.DiagramTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.junit.Assert;
import org.junit.Test;

public class LocalWaveformRuntimeStartOrderTest extends AbstractGraphitiLocalWaveformRuntimeTest {

	private SWTBotGefEditor editor;
	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	private static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	private static final String SIGGEN = "SigGen";

	/**
	 * IDE-326
	 * Test to make sure the Start Order ellipse doesn't not get drawn in the sandbox
	 */
	@Test
	public void removeStartOrderIconTest() {
		
		editor = gefBot.gefEditor(getWaveFormFullName());
		editor.setFocus();

		//drag SigGen to diagram and verify loaded in sca explorer
		DiagramTestUtils.dragFromPaletteToDiagram(editor, SIGGEN, 0, 0);
		ScaExplorerTestUtils.waitUntilComponentDisplaysInScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, SIGGEN + "_1");
		

		SWTBotGefEditPart sigGenEditPart = editor.getEditPart(SIGGEN);
		ComponentShapeImpl componentShape = (ComponentShapeImpl) sigGenEditPart.part().getModel();
		Assert.assertNull("Start Order ellipse should not be created during runtime", componentShape.getStartOrderEllipseShape());
	}
}
