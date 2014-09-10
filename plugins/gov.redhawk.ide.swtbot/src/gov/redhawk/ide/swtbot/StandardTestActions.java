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

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.ide.sdr.ui.preferences.SdrUiPreferenceConstants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroManager;
import org.eclipse.ui.intro.IIntroPart;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public final class StandardTestActions {

	private StandardTestActions() {
		// TODO Auto-generated constructor stub
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		while (PlatformUI.getWorkbench().isStarting()) {
			Thread.sleep(1000);
		}
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
	}

	@Before
	public static void beforeTest(SWTWorkbenchBot bot) throws Exception {
		SWTBotPerspective perspective = bot.perspectiveById("gov.redhawk.ide.ui.perspectives.sca");
		perspective.activate();
		bot.resetActivePerspective();
		bot.sleep(100);
	}

	@After
	public static void afterTest(SWTWorkbenchBot bot) throws Exception {
		final boolean[] dialogsClosed = { false };
		while (!dialogsClosed[0]) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				@Override
				public void run() {
					Shell s = Display.getCurrent().getActiveShell();
					if (s == PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()) {
						dialogsClosed[0] = true;
					} else {
						if (s != null) {
							s.dispose();
						}
					}
				}

			});
		}
		

		bot.closeAllEditors();
		bot.sleep(100);
		
		for (IProject p : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			p.delete(true, true, null);
		}
	}

	@AfterClass
	public static void afterClass() throws Exception {
		SWTWorkbenchBot tmpBot = new SWTWorkbenchBot();
		tmpBot.sleep(500);
	}

	/**
	 * <b>NOTE</b>: It is recommended you override the environment variables in the pom.xml for tests instead of changing the SDR root. 
	 * @param pluginId Plugin that contains the SDR
	 * @param path Relative path within the plugin for the SDR, usually 'sdr'
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static void setTargetSdr(String pluginId, String path) throws IOException, URISyntaxException {
		// XXX NOTE: SDR is loaded from the environment variables in the pom.xml not need to change it here.
		final URL url = FileLocator.find(Platform.getBundle(pluginId), new Path(path), null);
		final SdrRoot root = SdrUiPlugin.getDefault().getTargetSdrRoot();
		root.load(null);
		final URL fileURL = FileLocator.toFileURL(url);
		SdrUiPlugin.getDefault().getPreferenceStore().setValue(SdrUiPreferenceConstants.SCA_LOCAL_SDR_PATH_PREFERENCE,
			new File(fileURL.toURI()).getAbsolutePath());
		root.reload(null);
		Assert.assertTrue("SDR failed to load: " + root.getLoadStatus(), root.getLoadStatus().isOK());
	}
}
