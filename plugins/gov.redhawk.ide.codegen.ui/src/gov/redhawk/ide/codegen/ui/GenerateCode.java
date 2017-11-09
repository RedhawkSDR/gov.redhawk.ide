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

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;

import gov.redhawk.ide.codegen.ui.internal.job.GenerateFilesJob;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * This class is the primary entry point to code generation.
 * @since 7.0
 */
public final class GenerateCode {

	private GenerateCode() {
	}

	/**
	 * Performs the code generation process for the specified implementation(s). The process may prompt the user for
	 * input. The process occurs in a job and is thus asynchronous.
	 * <p/>
	 * This entry point does not perform any deprecation checks, upgrades, etc. For that, see
	 * {@link gov.redhawk.ide.codegen.ui.internal.command.GenerateCodeHandler}.
	 * @since 8.0
	 */
	public static void generate(final Shell shell, final List<Implementation> impls) {
		if (impls.isEmpty()) {
			return;
		}

		Job getFilesJob = new GenerateFilesJob("Calculating files to generate...", shell, impls);
		getFilesJob.setUser(true);
		getFilesJob.schedule();
	}
}
