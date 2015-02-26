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
package gov.redhawk.ide.swtbot;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class UITest {
	
	protected SWTWorkbenchBot bot; // SUPPRESS CHECKSTYLE VisibilityModifier
	
	@BeforeClass
	public static void beforeClass() throws Exception {
		while (PlatformUI.getWorkbench().isStarting()) {
			Thread.sleep(1000);
		}
		StandardTestActions.closeIntro();
	}

	@Before
	public void before() throws Exception {
		bot = new SWTWorkbenchBot();
		StandardTestActions.cleanup(bot);
		StandardTestActions.switchToScaPerspective(bot);
	}
	
	@After
	public void after() throws Exception {
		StandardTestActions.assertNoOpenDialogs();
		bot = null;
	}

}
