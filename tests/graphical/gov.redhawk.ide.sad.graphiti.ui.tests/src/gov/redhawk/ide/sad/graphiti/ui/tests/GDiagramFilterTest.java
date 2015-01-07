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

import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.WaveformUtils;
import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.junit.Assert;
import org.junit.Test;

public class GDiagramFilterTest extends AbstractGraphitiTest {
	private String waveformName;

	/**
	 * IDE-910
	 * The *.sad_GDiagram file should be filtered from the Project Explorer view by default
	 */
	@Test
	public void filterGDiagramResourceTest() {
		waveformName = "Filter_Resource";

		// Create a new empty waveform
		WaveformUtils.createNewWaveform(gefBot, waveformName);

		// Check if GDiagram is visible
		SWTBotView projectExplorerView = gefBot.viewById("org.eclipse.ui.navigator.ProjectExplorer");
		projectExplorerView.setFocus();
		gefBot.tree().expandNode(waveformName);
		boolean xmlExists = ViewUtils.checkIfTreeItemExistsEntry(gefBot.tree(), waveformName, waveformName + ".sad.xml");
		Assert.assertTrue("XML is not visible", xmlExists);
		boolean resourceExists = ViewUtils.checkIfTreeItemExistsEntry(gefBot.tree(), waveformName, waveformName + ".sad_GDiagram");
		Assert.assertFalse("GDiagram is not filtered", resourceExists);
	}
}
