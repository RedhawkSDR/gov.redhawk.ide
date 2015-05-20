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

import gov.redhawk.ide.swtbot.diagram.AbstractGraphitiTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Assert;
import org.junit.Test;

public class UnknownPortTest extends AbstractGraphitiTest {

	private static final String COMP_NAME = "testIDE1095"; 
	private static final String PORT1 = "a_out";
	private static final String PORT2 = "b_in";
	private static final String PORT3 = "b_out";
	private static final String PORT4 = "a_in";
	private static final String SCA_EXPLORER_VIEW_ID = "gov.redhawk.ui.sca_explorer";
	private static final String[] CHALKBOARD_PARENT_PATH = { "Sandbox" };
	private static final String CHALKBOARD = "Chalkboard";
	
	/**
	 * IDE-1095
	 * A device/component developer wants to have dynamic ports - so that under certain circumstances the getPort will
	 * raise CF::PortSupplier::UnknownPort. However, the IDE is not catching this error. The behavior for this bug is
	 * that no ports are displayed if any of the getPort calls raise UnknownPort.
	 */
	@Test
	public void confirmPortsAreDisplayed() {
		
		// Launch device from TargetSDR
		ScaExplorerTestUtils.launchComponentFromTargetSDR(bot, COMP_NAME, "python");
		final String[] chalkboardPath = ScaExplorerTestUtils.joinPaths(CHALKBOARD_PARENT_PATH, new String[] {CHALKBOARD});
		ScaExplorerTestUtils.waitUntilNodeAppearsInScaExplorer(bot, chalkboardPath, COMP_NAME);
		SWTBotView scaExplorerView = bot.viewById(SCA_EXPLORER_VIEW_ID);
		scaExplorerView.setFocus();
		
		// Check for the existence of each of the 3 ports which should be displayed
		SWTBotTreeItem componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", COMP_NAME, PORT1);
		componentEntry.select();
		componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", COMP_NAME, PORT2);
		componentEntry.select();
		componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", COMP_NAME, PORT3);
		componentEntry.select();
		
		boolean widgetNotFound = false;

		// Make sure the fourth port is not displayed, it is what returned the Unknown Port exception
		try {
			componentEntry = scaExplorerView.bot().tree().expandNode("Sandbox", "Chalkboard", COMP_NAME, PORT4);
		} catch (WidgetNotFoundException wnfe) {
			widgetNotFound = true;
		}
		Assert.assertTrue("Port " + PORT4 + " should not be visible.", widgetNotFound);
	}
}
