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

	/**
	 * Maintained for SAD/waveform diagram icons for backwards compatibility purposes.
	 * @deprecated Use {@link #PREFIX}
	 */
	@Deprecated
	private static final String OLD_PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";

	protected static final String PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";

	// Diagram icons

	public static final String IMG_FIND_BY = OLD_PREFIX + "findBy";

	// Custom feature / pattern icons

	public static final String IMG_FIND_BY_CORBA_NAME = PREFIX + "findByCORBAName";
	public static final String IMG_FIND_BY_SERVICE = PREFIX + "findByService";
	public static final String IMG_FIND_BY_DOMAIN_MANAGER = PREFIX + "findByDomainManager";
	public static final String IMG_FIND_BY_FILE_MANAGER = PREFIX + "findByFileManager";
	public static final String IMG_CONSOLE_VIEW = PREFIX + "consoleView";
	public static final String IMG_TERMINATE = PREFIX + "terminate";
	public static final String IMG_USES_DEVICE = PREFIX + "usesDevice";

	// Both diagram and custom feature / pattern

	public static final String IMG_USES_DEVICE_FRONTEND_TUNER = OLD_PREFIX + "usesDeviceFrontEndTuner";

	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_FIND_BY_CORBA_NAME, "icons/full/obj16/NamingService.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_FILE_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_SERVICE, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY, "icons/full/obj16/FindBy.gif");
		addImageFilePath(IMG_CONSOLE_VIEW, "icons/full/obj16/ConsoleView.gif");
		addImageFilePath(IMG_TERMINATE, "icons/full/obj16/Terminate.gif");
		addImageFilePath(IMG_USES_DEVICE_FRONTEND_TUNER, "icons/full/obj16/ScaDevice.gif");
		addImageFilePath(IMG_USES_DEVICE, "icons/full/obj16/ScaDevice.gif");
	}
}
