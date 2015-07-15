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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class ImageProvider extends AbstractImageProvider implements IImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "gov.redhawk.ide.graphiti.ui.diagram.providers.imageProvider.";

	public static final String IMG_START = PREFIX + "start";
	public static final String IMG_STOP = PREFIX + "stop";
	public static final String IMG_FIND_BY_CORBA_NAME = PREFIX + "findByCORBAName";
	public static final String IMG_FIND_BY_SERVICE = PREFIX + "findByService";
	public static final String IMG_FIND_BY_DOMAIN_MANAGER = PREFIX + "findByDomainManager";
	public static final String IMG_FIND_BY_FILE_MANAGER = PREFIX + "findByFileManager";
	public static final String IMG_FIND_BY_DOMAIN = PREFIX + "findByDomain";
	public static final String IMG_FIND_BY = PREFIX + "findBy";
	public static final String IMG_CONSOLE_VIEW = PREFIX + "consoleView";
	public static final String IMG_TERMINATE = PREFIX + "terminate";
	public static final String IMG_USES_DEVICE_FRONTEND_TUNER = PREFIX + "usesDeviceFrontEndTuner";
	public static final String IMG_USES_DEVICE = PREFIX + "usesDevice";
	public static final String IMG_EXPAND = PREFIX + "expand";
	public static final String IMG_COLLAPSE = PREFIX + "collapse";
	
	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_START, "icons/full/obj16/start.gif");
		addImageFilePath(IMG_STOP, "icons/full/obj16/stop.gif");
		addImageFilePath(IMG_FIND_BY_CORBA_NAME, "icons/full/obj16/NamingService.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_FILE_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_SERVICE, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY, "icons/full/obj16/FindBy.gif");
		addImageFilePath(IMG_CONSOLE_VIEW, "icons/full/obj16/ConsoleView.gif");
		addImageFilePath(IMG_TERMINATE, "icons/full/obj16/Terminate.gif");
		addImageFilePath(IMG_USES_DEVICE_FRONTEND_TUNER, "icons/full/obj16/ScaDevice.gif");
		addImageFilePath(IMG_USES_DEVICE, "icons/full/obj16/ScaDevice.gif");
		addImageFilePath(IMG_EXPAND, "icons/full/obj16/expand.png");
		addImageFilePath(IMG_COLLAPSE, "icons/full/obj16/collapse.png");
	}

}
