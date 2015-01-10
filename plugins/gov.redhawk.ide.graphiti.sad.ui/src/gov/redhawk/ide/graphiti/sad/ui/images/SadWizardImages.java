/** 
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.sad.ui.images;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 
 */
public final class SadWizardImages {
	private SadWizardImages() {

	}

	public static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

	private static final String PATH_OBJ = SadWizardImages.ICONS_PATH + "obj16/"; //$NON-NLS-1$

	public static final ImageDescriptor ADD = SadWizardImages.create(SadWizardImages.PATH_OBJ, "add.gif"); //$NON-NLS-1$
	public static final ImageDescriptor REMOVE = SadWizardImages.create(SadWizardImages.PATH_OBJ, "remove.gif"); //$NON-NLS-1$

	private static ImageDescriptor create(final String prefix, final String name) {
		return ImageDescriptor.createFromURL(SadWizardImages.makeImageURL(prefix, name));
	}

	private static URL makeImageURL(final String prefix, final String name) {
		final String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(SADUIGraphitiPlugin.getDefault().getBundle(), new Path(path), null);
	}
}
