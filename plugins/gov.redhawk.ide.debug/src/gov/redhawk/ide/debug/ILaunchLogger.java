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
package gov.redhawk.ide.debug;

import org.eclipse.debug.core.ILaunch;

/**
 * A service capable of logging a message to the console associated with an {@link ILaunch}.
 * @since 8.2
 */
public interface ILaunchLogger {

	/**
	 * Write a message to the console of an {@link ILaunch}. The message can contain newline characters if it is multi-
	 * line.
	 * @param launch
	 * @param message
	 * @param color The color to use
	 * @throws IllegalStateException A console is not available
	 */
	public void writeToConsole(ILaunch launch, String message, ConsoleColor color) throws IllegalStateException;

}
