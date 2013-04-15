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
package gov.redhawk.eclipsecorba.library.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class LibraryUIPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.eclipsecorba.library.ui";

	// The shared instance
	private static LibraryUIPlugin plugin;

	/**
	 * The constructor
	 */
	public LibraryUIPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		LibraryUIPlugin.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		LibraryUIPlugin.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LibraryUIPlugin getDefault() {
		return LibraryUIPlugin.plugin;
	}

	public static void logError(final String msg, final Throwable e) {
		LibraryUIPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, LibraryUIPlugin.PLUGIN_ID, msg, e));
	}

	/**
     * @since 1.1
     */
	public ImageDescriptor getImageDescriptor(String string) {
		ImageDescriptor retVal = getImageRegistry().getDescriptor(string);
		if (retVal == null) {
			retVal = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, string);
			if (retVal != null) {
				getImageRegistry().put(string, retVal);
			}
		}
	    return retVal;
    }

}
