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
package gov.redhawk.ide.sad.graphiti.ui.runtime.local.tests;

import gov.redhawk.ide.swtbot.UIRuntimeTest;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils;
import gov.redhawk.ide.swtbot.scaExplorer.ScaExplorerTestUtils.DiagramType;

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiLocalWaveformRuntimeTest extends UIRuntimeTest {

	public static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	public static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE
	private String waveFormFullName; //full name of waveform that is launched

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
		
		//Launch Local Waveform From Target SDR
		ScaExplorerTestUtils.launchWaveformFromTargetSDR(gefBot, LOCAL_WAVEFORM);

		//wait until local waveform appears in ScaExplorer Sandbox
		ScaExplorerTestUtils.waitUntilWaveformAppearsInScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);

		// Open Local Waveform Diagram
		ScaExplorerTestUtils.openDiagramFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM, DiagramType.GRAPHITI_CHALKBOARD);
		waveFormFullName = ScaExplorerTestUtils.getWaveformFullNameFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
	}
	
	@After
	public void afterTest() {
		
		//does waveform exist
		SWTBotTreeItem waveformEntry = ScaExplorerTestUtils.getWaveformTreeItemFromScaExplorer(bot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		//release waveform, make sure it disappears
		if (waveformEntry != null) {
			ScaExplorerTestUtils.releaseWaveformFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
			
			//wait until waveform no longer exists!!!!
			ScaExplorerTestUtils.waitUntilScaExplorerWaveformDisappears(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		}
	}


	public String getWaveFormFullName() {
		return waveFormFullName;
	}

	public void setWaveFormFullName(String waveFormFullName) {
		this.waveFormFullName = waveFormFullName;
	}
	
	

}
