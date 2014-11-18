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

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiLocalWaveformRuntimeTest extends UIRuntimeTest {

	private static final String[] LOCAL_WAVEFORM_PARENT_PATH = {"Sandbox"};
	private static final String LOCAL_WAVEFORM = "ExampleWaveform01";
	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
	}
	
	@After
	public void afterTest() {
		ScaExplorerTestUtils.releaseWaveformFromScaExplorer(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
		//wait until waveform empty
		ScaExplorerTestUtils.waitUntilScaExplorerWaveformEmpty(gefBot, LOCAL_WAVEFORM_PARENT_PATH, LOCAL_WAVEFORM);
		
	}

}
