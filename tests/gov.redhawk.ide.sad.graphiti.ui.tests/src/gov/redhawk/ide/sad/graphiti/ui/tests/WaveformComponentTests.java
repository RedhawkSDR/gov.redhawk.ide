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

import gov.redhawk.ide.swtbot.tests.waveform.CreateNewWaveform;

import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.BeforeClass;
import org.junit.Test;

public class WaveformComponentTests {

	private static SWTBot bot;
	private static SWTBotGefEditor editor;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTBot();
	}

	/**
	 * IDE-726
	 * Create the pictogram shape in the waveform diagram that represents the component business object.
	 * This includes the ContainerShape for the component, labels for Usage Name and ID, port shapes and labels,
	 * start order icon, and component supported interface.
	 */
	@Test
	public void checkComponentPictogram() {
		CreateNewWaveform.createNewWaveform(bot, "IDE-726-Test");
//		CreateNewWaveform.createNewWaveformWithAssemblyController(bot, "IDE-726-Test", null);
		// TODO call static method to open SAD editor
		// TODO call static method to add component from palette
		// TODO select all of the expected components for make sure they were created
			/* containerShape for the component 
			 * label for Usage Name
			 * label for ID
			 * port shapes
			 * port labels,
			 * start order icon
			 * component supported interface (lollipop)
			 */
	}

}
