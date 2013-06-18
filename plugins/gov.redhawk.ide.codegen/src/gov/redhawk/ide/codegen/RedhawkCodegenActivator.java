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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.codegen;

import gov.redhawk.ide.codegen.internal.CodeGeneratorPortTemplatesRegistry;
import gov.redhawk.ide.codegen.internal.CodeGeneratorTemplatesRegistry;
import gov.redhawk.ide.codegen.internal.CodeGeneratorsRegistry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle.
 */
public class RedhawkCodegenActivator extends Plugin {

	// The plug-in ID
	/** The Constant PLUGIN_ID. */
	public static final String PLUGIN_ID = "gov.redhawk.ide.codegen";

	// The shared instance
	/** The plugin. */
	private static RedhawkCodegenActivator plugin;

	/**
	 * Create the REDHAWK Codegen Registry
	 */
	private static ICodeGeneratorsRegistry codeGeneratorsRegistry;

	/**
	 * Create the REDHAWK Codegen Port Template Registry
	 */
	private static ICodeGeneratorPortTemplatesRegistry codeGeneratorPortTemplatesRegistry;

	/**
	 * Create the REDHAWK Codegen Template Registry
	 */
	private static ICodeGeneratorTemplatesRegistry codeGeneratorTemplatesRegistry;

	/**
	 * Language constants
	 */
	public static final String ENGLISH = "EN";

	/**
	 * Supported languages
	 */
	public static final String[] SUPPORTED_LANGUAGES = { RedhawkCodegenActivator.ENGLISH };

	/**
	 * Property file constants
	 */
	public static final String SAMPLE_PROPERTY_FILE = "samplePropertyFile.dtd";
	public static final String SAMPLE_PROPERTY_FILE_EXTENSION = "DTD";

	/**
	 * The constructor.
	 */
	public RedhawkCodegenActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		RedhawkCodegenActivator.plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		RedhawkCodegenActivator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 * 
	 * @return the shared instance
	 */
	public static RedhawkCodegenActivator getDefault() {
		return RedhawkCodegenActivator.plugin;
	}

	/**
	 * Returns the registry of Code Generators.
	 * 
	 * @return the registry of code generators
	 */
	public static ICodeGeneratorsRegistry getCodeGeneratorsRegistry() {
		if (RedhawkCodegenActivator.codeGeneratorsRegistry == null) {
			RedhawkCodegenActivator.codeGeneratorsRegistry = new CodeGeneratorsRegistry();
		}

		return RedhawkCodegenActivator.codeGeneratorsRegistry;
	}

	/**
	 * Returns the registry of Code Generator Port Templates.
	 * 
	 * @return the registry of code generator port templates.
	 * @since 7.0
	 */
	public static ICodeGeneratorPortTemplatesRegistry getCodeGeneratorPortTemplatesRegistry() {
		if (RedhawkCodegenActivator.codeGeneratorPortTemplatesRegistry == null) {
			RedhawkCodegenActivator.codeGeneratorPortTemplatesRegistry = new CodeGeneratorPortTemplatesRegistry();
		}

		return RedhawkCodegenActivator.codeGeneratorPortTemplatesRegistry;
	}

	/**
	 * Returns the registry of Code Generator Templates.
	 * 
	 * @return the registry of code generator templates.
	 * @since 7.0
	 */
	public static ICodeGeneratorTemplatesRegistry getCodeGeneratorTemplatesRegistry() {
		if (RedhawkCodegenActivator.codeGeneratorTemplatesRegistry == null) {
			RedhawkCodegenActivator.codeGeneratorTemplatesRegistry = new CodeGeneratorTemplatesRegistry();
		}

		return RedhawkCodegenActivator.codeGeneratorTemplatesRegistry;
	}

	/**
	 * Helper method to log an error, optionally with an exception.
	 * 
	 * @param msg the message to log
	 * @param e the exception that caused this error, if any
	 */
	public static final void logError(final String msg, final Throwable e) {
		RedhawkCodegenActivator.getDefault().getLog().log(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, msg, e));
	}

	/**
	 * Helper method to log an warning, optionally with an exception.
	 *
	 * @param msg the message to log
	 * @param e the exception that caused this warning, if any
	 * @since 9.2
	 */
	public static final void logWarning(final String msg, final Throwable e) {
		RedhawkCodegenActivator.getDefault().getLog().log(new Status(IStatus.WARNING, RedhawkCodegenActivator.PLUGIN_ID, msg, e));
	}

	/**
	 * Helper method to log an info message
	 * 
	 * @param msg the message to log
	 * @since 9.0
	 */
	public static final void logInfo(final String msg) {
		RedhawkCodegenActivator.getDefault().getLog().log(new Status(IStatus.INFO, RedhawkCodegenActivator.PLUGIN_ID, msg));
	}
}
