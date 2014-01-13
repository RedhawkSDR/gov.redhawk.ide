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
package gov.redhawk.ide.spd.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * @since 7.0
 * 
 */
public final class ComponentImages {
	public static final String RUN_EXEC = "icons/obj16/run_exc.gif";
	public static final String DEBUG_EXEC = "icons/obj16/debug_exc.gif";
	
	private ComponentImages() {

	}

	public static ImageDescriptor getImageDesc(final String key) {
		ImageDescriptor retVal = ComponentUiPlugin.getDefault().getImageRegistry().getDescriptor(key);
		if (retVal == null) {
			retVal = AbstractUIPlugin.imageDescriptorFromPlugin(ComponentUiPlugin.PLUGIN_ID, key);
			ComponentUiPlugin.getDefault().getImageRegistry().put(key, retVal);
		}
		return retVal;
	}

	public static Image getImage(final String key) {
		Image retVal = ComponentUiPlugin.getDefault().getImageRegistry().get(key);
		if (retVal == null) {
			final ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(ComponentUiPlugin.PLUGIN_ID, key);
			ComponentUiPlugin.getDefault().getImageRegistry().put(key, desc);
			retVal = ComponentUiPlugin.getDefault().getImageRegistry().get(key);
		}
		return retVal;
	}
}
