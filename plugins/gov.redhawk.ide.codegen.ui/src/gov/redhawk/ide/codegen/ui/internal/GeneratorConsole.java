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

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

import java.io.IOException;
import java.io.PrintStream;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 */
public class GeneratorConsole extends IOConsole {

	private final PrintStream outStream;
	private final PrintStream errStream;

	/**
	 * @param name
	 * @param consoleType
	 * @param imageDescriptor
	 * @param encoding
	 * @param autoLifecycle
	 */
	public GeneratorConsole(final ICodeGeneratorDescriptor desc) {
		super(desc.getName(), desc.getId(), GeneratorConsole.getConsoleImageDesc(desc));
		final IOConsoleOutputStream outIOStream = this.newOutputStream();
		outIOStream.setActivateOnWrite(false);
		this.outStream = new PrintStream(outIOStream);

		final IOConsoleOutputStream errIOStream = this.newOutputStream();
		errIOStream.setActivateOnWrite(true);
		errIOStream.setColor(JFaceResources.getColorRegistry().get(JFacePreferences.ERROR_COLOR));

		this.errStream = new PrintStream(errIOStream);

		try {
			this.getInputStream().close();
		} catch (final IOException e) {
			RedhawkCodegenUiActivator.getDefault().getLog().log(
			        new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Error in generator console.", e));
		}
	}

	/**
	 * @return the outStream
	 */
	public PrintStream getOutStream() {
		return this.outStream;
	}

	/**
	 * @return the errStream
	 */
	public PrintStream getErrStream() {
		return this.errStream;
	}

	/**
	 * @param desc
	 * @return
	 */
	private static ImageDescriptor getConsoleImageDesc(final ICodeGeneratorDescriptor desc) {
		final String path = desc.getIconPath();
		final String bundleId = desc.getContributingBundleId();
		if (path == null) {
			return null;
		}
		return AbstractUIPlugin.imageDescriptorFromPlugin(bundleId, path);
	}

}
