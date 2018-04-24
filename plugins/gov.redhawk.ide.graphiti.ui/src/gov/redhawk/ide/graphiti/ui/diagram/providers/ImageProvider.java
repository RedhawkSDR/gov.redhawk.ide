/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class ImageProvider extends AbstractImageProvider implements IImageProvider {

	// Prefixes

	protected static final String PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";

	// Custom feature / pattern icons

	public static final String IMG_CONSOLE_VIEW = PREFIX + "consoleView";

	/**
	 * @since 2.0
	 */
	public static final String IMG_TERMINATE = PREFIX + "terminate";

	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_CONSOLE_VIEW, "icons/full/obj16/ConsoleView.gif");
		addImageFilePath(IMG_TERMINATE, "icons/full/obj16/Terminate.gif");
	}
}
