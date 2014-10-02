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

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.utils.SWTUtils;
import org.eclipse.ui.PlatformUI;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * 
 */
public class RedhawkApplicationRunListener extends RunListener {
	/** The logger. */
	private static Logger log = Logger.getLogger(RedhawkApplicationRunListener.class);

	/** Counts the screenshots to determine if maximum number is reached. */
	private static int screenshotCounter = 0;

	public void testFailure(Failure failure) throws Exception {
		captureScreenshot(failure);
		super.testFailure(failure);
	}

	@Override
	public void testFinished(Description description) throws Exception {
		cleanup();
		super.testFinished(description);
	}

	private void cleanup() throws CoreException {
		SWTWorkbenchBot bot = new SWTWorkbenchBot();

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

		bot.closeAllShells();

		bot.closeAllEditors();

		StandardTestActions.clearWorkspace();
	}

	private void captureScreenshot(Failure failure) {
		try {
			int maximumScreenshots = SWTBotPreferences.MAX_ERROR_SCREENSHOT_COUNT;
			String fileName = SWTBotPreferences.SCREENSHOTS_DIR + "/" + failure.getTestHeader() + "." + SWTBotPreferences.SCREENSHOT_FORMAT.toLowerCase(); //$NON-NLS-1$
			if (++screenshotCounter <= maximumScreenshots) {
				captureScreenshot(fileName);
			} else {
				log.info("No screenshot captured for '" + failure.getTestHeader() + "' because maximum number of screenshots reached: " //$NON-NLS-1$ 
					+ maximumScreenshots);
			}
		} catch (Exception e) {
			log.warn("Could not capture screenshot", e); //$NON-NLS-1$
		}
	}

	private boolean captureScreenshot(String fileName) {
		return SWTUtils.captureScreenshot(fileName);
	}

	public int hashCode() {
		return 31;
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return true;
	}
}
