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

import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class StartOrderTests {

	private static SWTBot bot;
	private static SWTGefBot gefBot;
	private static SWTBotGefEditor editor;

	@BeforeClass
	public static void beforeClass() {
		bot = new SWTBot();
		gefBot = new SWTGefBot();
	}

	/**
	 * IDE-721
	 * Start order should be treated as an optional field, and should not cause
	 * errors when null
	 */
	@Test
	public void nullStartOrderTest() {

	}

	@AfterClass
	public static void cleanUp() {
		gefBot.sleep(2000);
	}

}
