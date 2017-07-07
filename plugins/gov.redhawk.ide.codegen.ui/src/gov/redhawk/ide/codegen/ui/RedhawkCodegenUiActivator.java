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

package gov.redhawk.ide.codegen.ui;

import gov.redhawk.ide.codegen.ui.internal.CodeGeneratorPageRegistry;
import gov.redhawk.ide.codegen.ui.internal.CodeGeneratorTemplatePageRegistry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @since 2.0
 */
public class RedhawkCodegenUiActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "gov.redhawk.ide.codegen.ui";

	/**
	 * Create the Redhawk Codegen & Codegen Template Wizard Page Registry
	 */
	private static ICodeGeneratorPageRegistry codeGeneratorPageRegistry;
	private static ICodeGeneratorPageRegistry codeGeneratorTemplatePageRegistry;
	private static ICodeGeneratorLanguagePageRegistry codeGeneratorLanguagePageRegistry;

	// The shared instance
	private static RedhawkCodegenUiActivator plugin;


	private ServiceTracker<IComponentProjectUpgrader, IComponentProjectUpgrader> upgradeProjectServiceTracker;

	/**
	 * The constructor
	 */
	public RedhawkCodegenUiActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		RedhawkCodegenUiActivator.plugin = this;
		upgradeProjectServiceTracker = new ServiceTracker<IComponentProjectUpgrader, IComponentProjectUpgrader>(context, IComponentProjectUpgrader.class, null);
		upgradeProjectServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		RedhawkCodegenUiActivator.plugin = null;
		upgradeProjectServiceTracker.close();
		upgradeProjectServiceTracker = null;
		super.stop(context);
	}
	
	/**
	 * @since 8.0
	 */
	public IComponentProjectUpgrader getComponentProjectUpgraderService() {
		if (upgradeProjectServiceTracker != null) {
			return upgradeProjectServiceTracker.getService();
		}
		return null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RedhawkCodegenUiActivator getDefault() {
		return RedhawkCodegenUiActivator.plugin;
	}

	/**
	 * Returns the RedhawkCodegenActivator
	 * 
	 * @return the RedhawkCodegenActivator
	 */
	public static ICodeGeneratorPageRegistry getCodeGeneratorsRegistry() {
		if (RedhawkCodegenUiActivator.codeGeneratorPageRegistry == null) {
			RedhawkCodegenUiActivator.codeGeneratorPageRegistry = new CodeGeneratorPageRegistry();
		}

		return RedhawkCodegenUiActivator.codeGeneratorPageRegistry;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(RedhawkCodegenUiActivator.PLUGIN_ID, path);
	}

	public static String getPluginId() {
		return RedhawkCodegenUiActivator.getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Logging functionality
	 * 
	 * @param msg
	 * @param e
	 */
	public static final void logError(final String msg, final Throwable e) {
		RedhawkCodegenUiActivator.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, msg, e));
	}

	/**
	 * @since 8.1
	 */
	public static ICodeGeneratorPageRegistry getCodeGeneratorsTemplateRegistry() {
		if (RedhawkCodegenUiActivator.codeGeneratorTemplatePageRegistry == null) {
			RedhawkCodegenUiActivator.codeGeneratorTemplatePageRegistry = new CodeGeneratorTemplatePageRegistry();
		}

		return RedhawkCodegenUiActivator.codeGeneratorTemplatePageRegistry;
	}

	/**
	 * @since 9.2
	 */
	public static ICodeGeneratorLanguagePageRegistry getCodeGeneratorsLanguageRegistry() {
		if (RedhawkCodegenUiActivator.codeGeneratorLanguagePageRegistry == null) {
			RedhawkCodegenUiActivator.codeGeneratorLanguagePageRegistry = new CodeGeneratorPageRegistry();
		}

		return RedhawkCodegenUiActivator.codeGeneratorLanguagePageRegistry;
	}

}
