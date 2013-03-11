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

import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;

/**
 * The Interface IScaComponentCodegenTemplate.
 * 
 * @since 7.0
 */
public interface IScaComponentCodegenTemplate {

	/**
	 * Name of the interface
	 */
	public static final String NAME = "IScaComponentCodegenTemplate";

	/**
	 * This returns a list of all files that need execute permission.
	 * 
	 * @param implSettings the implementation settings for this generator
	 * @param softPkg the SoftPkg for the project
	 * @return a List&lt;String&gt; of filenames that need execute permission
	 */
	List<String> getExecutableFileNames(final ImplementationSettings implSettings, final SoftPkg softPkg);

	/**
	 * This returns a list of all files that the template can generate.
	 * 
	 * @param implSettings the implementation settings for this generator
	 * @param softPkg the SoftPkg for the project
	 * @return a List&lt;String&gt; of filenames for the template that can be
	 *         generated
	 */
	List<String> getAllGeneratedFileNames(final ImplementationSettings implSettings, final SoftPkg softPkg);

	/**
	 * This method will generate the specified file, optionally using the
	 * helperObject.
	 * 
	 * @param fileName the name of the file to generate
	 * @param componentName the name of the component
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @return a String representing the contents of the generated file
	 * @throws CoreException An error prevents generation of the file
	 * @since 9.0
	 */
	String generateFile(final String fileName, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject) throws CoreException;

	/**
	 * Boolean flag if generation should be performed or not.
	 * 
	 * @return true if the generator produces files that should get generated
	 */
	boolean shouldGenerate();
	
	/**
	 * This returns the name of the default user editable file. 
	 * 
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings for the template
	 * @param srcDir default srcDir for the generator
	 * @return the default filename to edit
	 */
	String getDefaultFilename(final SoftPkg softPkg, final ImplementationSettings implSettings, final String srcDir);
}
