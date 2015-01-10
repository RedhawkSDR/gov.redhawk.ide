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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;
import org.eclipse.graphiti.ui.platform.IImageProvider;

public class WaveformImageProvider extends AbstractImageProvider implements IImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "gov.redhawk.ide.sad.graphiti.ui.diagram.providers.imageProvider.";

	public static final String IMG_COMPONENT_PLACEMENT = PREFIX + "componentPlacement";
	public static final String IMG_COMPONENT_INSTANCE = PREFIX + "componentInstance";
	public static final String IMG_CONNECTION = PREFIX + "connection";
	public static final String IMG_FIND_BY_CORBA_NAME = PREFIX + "findByCORBAName";
	public static final String IMG_FIND_BY_SERVICE = PREFIX + "findByService";
	public static final String IMG_FIND_BY_DOMAIN_MANAGER = PREFIX + "findByDomainManager";
	public static final String IMG_FIND_BY_FILE_MANAGER = PREFIX + "findByFileManager";
	public static final String IMG_FIND_BY_DOMAIN = PREFIX + "findByDomain";
	public static final String IMG_FIND_BY = PREFIX + "findBy";
	public static final String IMG_HOST_COLLOCATION = "hostCollocation";

	public WaveformImageProvider() {
	}

	// register the path for each image identifier
	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_COMPONENT_PLACEMENT, "icons/full/obj16/ComponentPlacement.gif");
		addImageFilePath(IMG_COMPONENT_INSTANCE, "icons/full/obj16/ComponentInstantiation.gif");
		addImageFilePath(IMG_CONNECTION, "icons/full/obj16/ConnectInterface.gif");
		addImageFilePath(IMG_FIND_BY_CORBA_NAME, "icons/full/obj16/NamingService.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_FILE_MANAGER, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_DOMAIN, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY_SERVICE, "icons/full/obj16/DomainFinder.gif");
		addImageFilePath(IMG_FIND_BY, "icons/full/obj16/FindBy.gif");
		addImageFilePath(IMG_HOST_COLLOCATION, "icons/full/obj16/HostCollocation.gif");
	}

}
