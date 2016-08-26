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
	protected static final String PREFIX = "gov.redhawk.ide.graphiti.sad.ui.diagram.providers.imageProvider.";

	public static final String IMG_HOST_COLLOCATION = "hostCollocation";

	public WaveformImageProvider() {
	}

	// register the path for each image identifier
	@Override
	protected void addAvailableImages() {
		addImageFilePath(IMG_HOST_COLLOCATION, "icons/full/obj16/HostCollocation.gif");
	}

}
