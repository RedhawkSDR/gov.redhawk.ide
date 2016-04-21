/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package gov.redhawk.ide.debug.internal;

import org.eclipse.debug.core.ILaunch;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import gov.redhawk.ide.debug.ConsoleColor;
import gov.redhawk.ide.debug.ILaunchLogger;
import gov.redhawk.ide.debug.ScaDebugPlugin;

public enum LaunchLogger implements ServiceTrackerCustomizer {

	INSTANCE;

	private ILaunchLogger logger = null;

	private LaunchLogger() {
	}

	/**
	 * Write a message to the console of an {@link ILaunch}. The message can contain newline characters if it is multi-
	 * line. If the console can't be located, the message will be logged to the plugin's log instead.
	 * @param launch
	 * @param message
	 * @param color The color to use
	 */
	public void writeToConsole(ILaunch launch, String message, ConsoleColor color) {
		if (logger != null) {
			try {
				logger.writeToConsole(launch, message, color);
			} catch (IllegalStateException e) {
				writeToLog(message);
			}
		} else {
			writeToLog(message);
		}
	}

	private void writeToLog(String message) {
		ScaDebugPlugin.logWarning(message, null);
	}

	public Object addingService(ServiceReference reference) {
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		logger = (ILaunchLogger) context.getService(reference);
		return logger;
	}

	public void modifiedService(ServiceReference reference, Object service) {
		// PASS - Don't care
	}

	public void removedService(ServiceReference reference, Object service) {
		logger = null;
		BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
		context.ungetService(reference);
	}

}
