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
package gov.redhawk.ide.tests.ui;

import gov.redhawk.ide.swtbot.UITest;

import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.Test;

public class DomainLaunchDialogTest extends UITest {

	/**
	 * User should be able to open the domain launching dialog immediately after starting the IDE
	 */
	@Test
	public void test_IDE_828() {
		SWTBotView scaExplorerView = bot.viewByTitle("SCA Explorer");
		SWTBotTree scaTree = scaExplorerView.bot().tree();
		SWTBotTreeItem targetSDR = scaTree.getTreeItem("Target SDR");
		
		targetSDR.pressShortcut(Keystrokes.ESC);
		targetSDR.select();
		new SWTBotMenu(ContextMenuHelper.contextMenu(scaTree, "Launch Domain ...")).click();
		
		// Will timeout if error causes launch window not to display
		bot.waitUntil(Conditions.shellIsActive("Launch Domain Manager"), 30000);
		
		try {
			bot.shell("Launch Domain Manager").close();
		} catch (Exception e) {
			// PASS
		}
	}
}
