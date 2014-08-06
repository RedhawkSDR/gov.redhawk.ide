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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.swt.finder.finders.ContextMenuHelper;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class DomainLaunchDialogTest {
	private static SWTGefBot gefBot;

	@BeforeClass
	public static void beforeClass() throws Exception {
		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

			@Override
			public void run() {
				final IIntroManager introManager = PlatformUI.getWorkbench().getIntroManager();
				if (introManager != null) {
					IIntroPart part = introManager.getIntro();
					if (part != null) {
						introManager.closeIntro(part);
					}
				}
			}
		});

		SWTWorkbenchBot tmpBot = new SWTWorkbenchBot();
		SWTBotPerspective perspective = tmpBot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		tmpBot.resetActivePerspective();
	}

	@Before
	public void beforeTest() {
		gefBot = new SWTGefBot();
	}

	/**
	 * User should be able to open the domain launching dialog immediately after starting the IDE
	 */
	@Test
	public void test_IDE_828() {
		SWTBotView scaExplorerView = gefBot.viewByTitle("SCA Explorer");
		SWTBotTree scaTree = scaExplorerView.bot().tree();
		SWTBotTreeItem targetSDR = scaTree.getTreeItem("Target SDR");
		
		targetSDR.pressShortcut(Keystrokes.ESC);
		targetSDR.select();
		new SWTBotMenu(ContextMenuHelper.contextMenu(scaTree, "Launch...")).click();
		
		// Will timeout if error causes launch window not to display
		gefBot.waitUntil(Conditions.shellIsActive("Launch Domain Manager"));
	}
}
