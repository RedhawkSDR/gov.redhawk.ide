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
package gov.redhawk.ide.sad.graphiti.ui.runtime.domain.tests;

import gov.redhawk.ide.swtbot.ConsoleUtils;
import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiDomainWaveformRuntimeTest extends UIRuntimeTest {

	public static final String[] DOMAIN_WAVEFORM_PARENT_PATH = {"REDHAWK_DEV CONNECTED", "Waveforms"};
	public static final String DOMAIN_WAVEFORM = "ExampleWaveform06";
	public static final String DOMAIN = "REDHAWK_DEV";
	public static final String DOMAIN_MANAGER_PROCESS = "Domain Manager";
	public static final String DEVICE_MANAGER_PROCESS = "Device Manager";
	public static final String DEVICE_MANAGER = "DevMgr";
	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE
	private String waveFormFullName; //full name of waveform that is launched

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
		
		//Launch Domain
		ScaExplorerTestUtils.launchDomain(gefBot, DOMAIN, DEVICE_MANAGER);
		
		//wait until Domain launched and connected
		ScaExplorerTestUtils.waitUntilScaExplorerDomainConnects(gefBot, DOMAIN);
		
		//Launch Domain Waveform From Domain
		ScaExplorerTestUtils.launchWaveformFromDomain(gefBot, DOMAIN, DOMAIN_WAVEFORM);

		//wait until domain waveform appears in ScaExplorer Domain
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);

		// Open Domain Waveform Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
		waveFormFullName = ScaExplorerTestUtils.getWaveformFullNameFromScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
				
	}
	
	@After
	public void afterTest() {
		
		//does waveform exist
		SWTBotTreeItem waveformEntry = ScaExplorerTestUtils.getWaveformTreeItemFromScaExplorer(bot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
		
		//release waveform, make sure it disappears
		if (waveformEntry != null) {
			ScaExplorerTestUtils.releaseWaveformFromScaExplorer(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
			
			//wait until waveform no longer exists!!!!
			ScaExplorerTestUtils.waitUntilScaExplorerWaveformDisappears(gefBot, DOMAIN_WAVEFORM_PARENT_PATH, DOMAIN_WAVEFORM);
		}
		
		//delete domain instance from sca explorer
		ScaExplorerTestUtils.deleteDomainInstance(bot, DOMAIN);
		
		//Stop domain manager and device manager
		//TODO: Noticed that the first time this runs everything works great.  After the second test however the console's drop down menu
		//for switching between processes throws exceptions.  I think this may actually be an Eclipse bug.
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
