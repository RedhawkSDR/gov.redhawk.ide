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
package gov.redhawk.ide.debug.ui.diagram;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @since 1.1
 */
public class LocalScaDiagramPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.debug.ui.diagram"; //$NON-NLS-1$

	public static final String IMG_DEBUG = "icons/debug_exc.gif";

	// The shared instance
	private static LocalScaDiagramPlugin plugin;

	/**
	 * The constructor
	 */
	public LocalScaDiagramPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		LocalScaDiagramPlugin.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		LocalScaDiagramPlugin.plugin = null;
		super.stop(context);
	}

	public static Image getImage(final String path) {
		Image retVal = LocalScaDiagramPlugin.getDefault().getImageRegistry().get(path);
		if (retVal == null) {
			LocalScaDiagramPlugin.getDefault().getImageRegistry().put(path, AbstractUIPlugin.imageDescriptorFromPlugin(LocalScaDiagramPlugin.PLUGIN_ID, path));
			retVal = LocalScaDiagramPlugin.getDefault().getImageRegistry().get(path);
		}
		return retVal;

	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static LocalScaDiagramPlugin getDefault() {
		return LocalScaDiagramPlugin.plugin;
	}

}
