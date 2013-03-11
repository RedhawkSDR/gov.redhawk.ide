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
package gov.redhawk.ide.codegen;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * The Interface IScaComponentCodegen.
 */

public interface IScaComponentCodegen {

	/**
	 * Name of the interface
	 */
	public static final String NAME = "IScaComponentCodegen";

	/**
	 * This function is called when the component implementation should be
	 * regenerated. If necessary, the generator can add extra project
	 * natures/builders to the project.
	 * <p>
	 * This function is called outside of the UI thread
	 * 
	 * @param implSettings holds all of the code generation settings
	 * @param impl the implementation to generate
	 * @param out the output stream for messages
	 * @param err the error stream for messages
	 * @param monitor The progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts null, indicating that no progress
	 *            should be reported and that the operation cannot be canceled.
	 * @param generateFiles an array of filenames to generate
	 * @param shouldGenerate
	 * @param crcMap map of filenames to CRC's
	 * @return The status of the operation; {@link IStatus#ERROR} indicates failure
	 * @since 7.0
	 */
	IStatus generate(ImplementationSettings implSettings, Implementation impl, PrintStream out, PrintStream err, IProgressMonitor monitor, String[] generateFiles,
	        boolean shouldGenerate, List<FileToCRCMap> crcMap);

	/**
	 * Get the initial values based on the given settings
	 * 
	 * @param softPkg the softpkg
	 * @param settings the implementation settings to initialize
	 * @param impl the implementation
	 * @return the SCA Code struct representing the code generator settings
	 * @since 9.0
	 */
	Code getInitialCodeSettings(SoftPkg softPkg, ImplementationSettings settings, Implementation impl);

	/**
	 * Ask the generator to clean up source folders and remove project nature if
	 * no implementations exist.
	 * 
	 * @param project the project that houses this generator
	 * @param monitor The progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts null, indicating that no progress
	 *            should be reported and that the operation cannot be canceled.
	 * @return The status of the operation; {@link IStatus#ERROR} indicates failure
	 * @since 7.0
	 */
	IStatus cleanupSourceFolders(final IProject project, IProgressMonitor monitor);

	/**
	 * Get a {@link HashMap} of all files the generator will generate.
	 * 
	 * @param implSettings The {@link ImplementationSettings} for the
	 *            {@link Implementation}
	 * @param softpkg the {@link SoftPkg} for the {@link Implementation}
	 * @return a {@link HashMap} of filenames and booleans on whether or not the
	 *         file will be generated
	 * @throws CoreException The generator is unable to determine which files
	 *             will be generated due to an error
	 * @since 3.0
	 */
	HashMap<String, Boolean> getGeneratedFiles(final ImplementationSettings implSettings, final SoftPkg softpkg) throws CoreException;

	/**
	 * Boolean flag that indicates if code generation should be performed
	 * 
	 * @return true if the generator produces files that should get generated
	 */
	boolean shouldGenerate();

	/**
	 * This returns the default user-editable file for an implementation
	 * 
	 * @param impl The implementation
	 * @param implSettings The implementation settings
	 * @return the default file to edit, or null if a problem
	 * @since 7.0
	 */
	IFile getDefaultFile(Implementation impl, ImplementationSettings implSettings);

	/**
	 * Requests that the code generator perform validation checks to ensure that it can produce code. This should
	 * reduce the likelihood of a failure to generate code when
	 * {@link #generate(ImplementationSettings, Implementation, PrintStream, PrintStream, IProgressMonitor, String[], boolean, List) generate}
	 * is called.
	 * 
	 * @return An {@link IStatus} indicating any issues found; problems of severity {@link IStatus#ERROR} indicate code
	 * generation will fail if attempted
	 * @since 7.0
	 */
	IStatus validate();
}
