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
package gov.redhawk.ide.graphiti.sad.ui.runtime.local.tests;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.ViewUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import java.util.List;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The purpose of this class is to test that selecting "Monitor Ports" on a waveform in the SCA Explorer correctly adds
 * the ports of all child components to the Port Monitor view. The actual implementation of the Port Monitor View is
 * tested via a Jubula test that was labeled, at the time of this writing, as 'IDE-Runtime_Port_Monitor_View'
 */
public class PortMonitorViewTest extends UIRuntimeTest {

	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = { "Sandbox" };
	private static final String WAVEFORM_NAME = "MonitorPortsWaveform";
	private String waveFormFullName; // full name of waveform that is launched

	private SWTGefBot gefBot;

	@Before
	public void beforeTest() {
		gefBot = new SWTGefBot();
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, WAVEFORM_NAME);
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
		waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
	}

	/**
	 * IDE-1063
	 * Enable "Monitor Ports" option on waveform in the SCA Explorer to monitor all ports of all components
	 */
	@Test
	public void portMonitorWaveformTest() {
		ScaExplorerTestUtils.startWaveformFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
		ScaExplorerTestUtils.montiorWaveformPortsFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);

		ViewUtils.disableConsoleView(gefBot);

		SWTBotView portMonitorView = ViewUtils.getPortMonitorView(gefBot);
		portMonitorView.show();

		SWTBotTreeItem rootItem = portMonitorView.bot().tree().getAllItems()[0];
		Assert.assertTrue("Root tree item should be the waveform", waveFormFullName.equals(rootItem.getText()));

		List<String> nodes = rootItem.getNodes();
		Assert.assertTrue("Expected nine (9) ports to be monitored, instead found: " + nodes.size(), nodes.size() == 9);

	}

	@After
	public void afterTest() {
		// does waveform exist
		SWTBotTreeItem waveformEntry = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);

		// release waveform, make sure it disappears
		if (waveformEntry != null) {
			ScaExplorerTestUtils.releaseFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);

			// wait until waveform no longer exists
			ScaExplorerTestUtils.waitUntilScaExplorerWaveformDisappears(gefBot, LOCAL_WAVEFORM_PARENT_PATH, WAVEFORM_NAME);
		}
	}

}
