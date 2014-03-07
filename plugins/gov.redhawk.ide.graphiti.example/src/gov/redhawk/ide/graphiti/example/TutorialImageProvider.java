/*******************************************************************************
 * <copyright>
 *
 * Copyright (c) 2005, 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    SAP AG - initial API, implementation and documentation
 *
 * </copyright>
 *
 *******************************************************************************/
package gov.redhawk.ide.graphiti.example;

import org.eclipse.graphiti.ui.platform.AbstractImageProvider;

public class TutorialImageProvider extends AbstractImageProvider {

	// The prefix for all identifiers of this image provider
	protected static final String PREFIX = "gov.redhawk.ide.graphiti.example."; //$NON-NLS-1$

	// The image identifier for an EReference.
	public static final String IMG_EREFERENCE = PREFIX + "ereference"; //$NON-NLS-1$

	@Override
	protected void addAvailableImages() {
		// register the path for each image identifier
		addImageFilePath(IMG_EREFERENCE, "icons/ereference.gif"); //$NON-NLS-1$
	}
}
