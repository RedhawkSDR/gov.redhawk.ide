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
package gov.redhawk.ide.sad.graphiti.ui.runtime.tests;

import gov.redhawk.ide.swtbot.UIRuntimeTest;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.After;
import org.junit.Before;

/**
 * 
 */
public abstract class AbstractGraphitiRuntimeTest extends UIRuntimeTest {

	protected SWTGefBot gefBot; // SUPPRESS CHECKSTYLE INLINE

	@Before
	public void beforeTest() throws Exception {
		gefBot = new SWTGefBot();
		super.before();
	}

	@After
	public void afterTest() {
		// Clear out the Sandbox
		SWTBotView scaExplorerView = gefBot.viewByTitle("SCA Explorer");
		SWTBotTree scaTree = scaExplorerView.bot().tree();
		SWTBotTreeItem chalkboard = scaTree.expandNode("Sandbox", "Chalkboard");
		chalkboard.select();
		SWTBotMenu release = chalkboard.contextMenu("Release");
		release.click();
	}

}
