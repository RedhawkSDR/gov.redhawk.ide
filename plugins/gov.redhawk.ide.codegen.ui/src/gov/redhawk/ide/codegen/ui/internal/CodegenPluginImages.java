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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * The Class ScaPluginImages.
 */
public final class CodegenPluginImages {

	/** The PLUGIN registry. */
	// CHECKSTYLE:OFF
	private static ImageRegistry PLUGIN_REGISTRY;

	/** The Constant ICONS_PATH. */
	public static final String ICONS_PATH = "icons/"; //$NON-NLS-1$

	/** CodeGenerator icon. */
	public static final ImageDescriptor GENERATE_CODE = CodegenPluginImages.create(CodegenPluginImages.ICONS_PATH, "generate_code.gif"); //$NON-NLS-1$

	// CHECKSTYLE:ON
	/**
	 * Instantiates a new codegen plugin images.
	 */
	private CodegenPluginImages() {

	}

	/**
	 * Creates the.
	 * 
	 * @param prefix the prefix
	 * @param name the name
	 * 
	 * @return the image descriptor
	 */
	private static ImageDescriptor create(final String prefix, final String name) {
		return ImageDescriptor.createFromURL(CodegenPluginImages.makeImageURL(prefix, name));
	}

	/**
	 * Gets the.
	 * 
	 * @param key the key
	 * 
	 * @return the image
	 */
	public static Image get(final String key) {
		if (CodegenPluginImages.PLUGIN_REGISTRY == null) {
			CodegenPluginImages.initialize();
		}
		return CodegenPluginImages.PLUGIN_REGISTRY.get(key);
	}

	/* package */
	/**
	 * Initialize.
	 */
	private static void initialize() {
		CodegenPluginImages.PLUGIN_REGISTRY = new ImageRegistry();
		CodegenPluginImages.manage("generate_code.gif", CodegenPluginImages.GENERATE_CODE);
	}

	/**
	 * Make image url.
	 * 
	 * @param prefix the prefix
	 * @param name the name
	 * 
	 * @return the uRL
	 */
	private static URL makeImageURL(final String prefix, final String name) {
		final String path = "$nl$/" + prefix + name; //$NON-NLS-1$
		return FileLocator.find(RedhawkCodegenUiActivator.getDefault().getBundle(), new Path(path), null);
	}

	/**
	 * Manage the image descriptors.
	 * 
	 * @param key the key for the image
	 * @param desc the image to store
	 * 
	 * @return the image
	 */
	public static Image manage(final String key, final ImageDescriptor desc) {
		final Image image = desc.createImage();
		CodegenPluginImages.PLUGIN_REGISTRY.put(key, image);
		return image;
	}
}
