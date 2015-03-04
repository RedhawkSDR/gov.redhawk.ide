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
package gov.redhawk.ide.graphiti.sad.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.WaitForEditorCondition;
import gov.redhawk.ide.swtbot.diagram.RHTestBot;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

/**
 * Before: Starts a domain, dev mgr, launches a waveform, and opens the waveform's Graphiti runtime editor.
 * After: Releases the waveform if it's still running and ensures it shuts down.
 */
public abstract class AbstractGraphitiDomainWaveformRuntimeTest extends UIRuntimeTest {

	public static final String[] DOMAIN_WAVEFORM_PARENT_PATH = { "REDHAWK_DEV CONNECTED", "Waveforms" };
	public static final String DOMAIN_WAVEFORM = "ExampleWaveform06";
	public static final String DOMAIN = "REDHAWK_DEV";
	public static final String DOMAIN_MANAGER_PROCESS = "Domain Manager";
	public static final String DEVICE_MANAGER_PROCESS = "Device Manager";
	public static final String DEVICE_MANAGER = "DevMgr";
	protected SWTGefBot gefBot = new RHTestBot(); // SUPPRESS CHECKSTYLE VisibilityModifier
	private String waveFormFullName; // full name of waveform that is launched

	@Before
	public void beforeTest() throws Exception {
		// Launch domain
		ScaExplorerTestUtils.launchDomain(bot, DOMAIN, DEVICE_MANAGER);

		// Wait until domain launched and connected
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(bot, DOMAIN);

		// Launch waveform in domain
		ScaExplorerTestUtils.launchWaveformFromDomain(bot, DOMAIN, DOMAIN_WAVEFORM);

		// Wait until the editor opens and the waveform appears in the SCA Explorer view
		bot.waitUntil(new WaitForEditorCondition());
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

		// Record the waveform's full name
		waveFormFullName = ScaExplorerTestUtils.getFullNameFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
	}

	@After
	public void afterTest() {
		// does waveform exist
		SWTBotTreeItem waveformEntry = ScaExplorerTestUtils.getTreeItemFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

		// release waveform, make sure it disappears
		if (waveformEntry != null) {
			ScaExplorerTestUtils.releaseFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

			// wait until waveform no longer exists!!!!
			ScaExplorerTestUtils.waitUntilScaExplorerWaveformDisappears(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
		}

		// delete domain instance from sca explorer
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);

		// Stop domain manager and device manager
		// TODO: Noticed that the first time this runs everything works great. After the second test however the
		// console's drop down menu
		// for switching between processes throws exceptions. I think this may actually be an Eclipse bug.
		ConsoleUtils.terminateProcess(bot, DEVICE_MANAGER_PROCESS);
		ConsoleUtils.terminateProcess(bot, DOMAIN_MANAGER_PROCESS);
	}

	public String getWaveFormFullName() {
		return waveFormFullName;
	}

	public void setWaveFormFullName(String waveFormFullName) {
		this.waveFormFullName = waveFormFullName;
	}

}
