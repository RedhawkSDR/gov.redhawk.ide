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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

/** run with SWTBotJunit4ClassRunner to capture screenshots on test failures. */
@RunWith(SWTBotJunit4ClassRunner.class)
public abstract class UITest {

	private static final String PYDEV_BUNDLE = "org.python.pydev";
	private static final String PYDEV_CHECK_SETTINGS = "CHECK_PREFERRED_PYDEV_SETTINGS";
	private static final String PYDEV_FUNDING_SHOWN = "PYDEV_FUNDING_SHOWN_2014";

	protected SWTWorkbenchBot bot; // SUPPRESS CHECKSTYLE VisibilityModifier
	private List<IPath> sdrDomCleanupPaths = null;
	private List<IPath> sdrDevCleanupPaths = null;

	@BeforeClass
	public static void beforeClass() throws Exception {
		// Stop PyDev from generating pop-ups
		ScopedPreferenceStore pydevPrefs = new ScopedPreferenceStore(InstanceScope.INSTANCE, PYDEV_BUNDLE);
		pydevPrefs.setValue(PYDEV_CHECK_SETTINGS, false);
		pydevPrefs.setValue(PYDEV_FUNDING_SHOWN, true);

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
		cleanup(ScaFileSystemConstants.SCHEME_TARGET_SDR_DOM, sdrDomCleanupPaths);
		cleanup(ScaFileSystemConstants.SCHEME_TARGET_SDR_DEV, sdrDevCleanupPaths);
		StandardTestActions.assertNoOpenDialogs();
		bot = null;
	}

	@AfterClass
	public static void afterClass() throws Exception {
		// final cleanup of any open dialogs/editors/etc
		StandardTestActions.cleanup(new SWTWorkbenchBot());
	}

	/**
	 * Ensure a path, relative to SDRROOT/dom, is cleaned up {@link After} a test run
	 * @param path
	 */
	public void addSdrDomCleanupPath(IPath path) {
		if (sdrDomCleanupPaths == null) {
			sdrDomCleanupPaths = new ArrayList<IPath>();
		}
		sdrDomCleanupPaths.add(path);
	}

	/**
	 * Ensure a path, relative to SDRROOT/dev, is cleaned up {@link After} a test run
	 * @param path
	 */
	public void addSdrDevCleanupPath(IPath path) {
		if (sdrDevCleanupPaths == null) {
			sdrDevCleanupPaths = new ArrayList<IPath>();
		}
		sdrDevCleanupPaths.add(path);
	}

	private void cleanup(String fileSystemScheme, List<IPath> paths) throws CoreException {
		if (paths == null) {
			return;
		}
		IFileSystem fileSystem = EFS.getFileSystem(fileSystemScheme);
		for (IPath path : paths) {
			IFileStore fileStore = fileSystem.getStore(path);
			if (fileStore.fetchInfo().exists()) {
				fileStore.delete(EFS.NONE, null);
			}
		}
	}
}
