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
 * The Interface IScaPortCodegenTemplate.
 * 
 * @since 7.0
 */
public interface IScaPortCodegenTemplate {

	/**
	 * Name of the interface
	 */
	public static final String NAME = "IScaPortCodegenTemplate";

	/**
	 * This returns a list of all files that need execute permission.
	 * 
	 * @param implSettings the implementation settings for this generator
	 * @param softPkg the SoftPkg for the project
	 * @param language the programming language to generate for
	 * @return a List&lt;String&gt; of filenames that need execute permission
	 */
	List<String> getExecutableFileNames(final ImplementationSettings implSettings, final SoftPkg softPkg, final String language);

	/**
	 * This returns a list of all files that the template can generate.
	 * 
	 * @param implSettings the implementation settings for this generator
	 * @param softPkg the SoftPkg for the project
	 * @param language the programming language to generate for
	 * @return a List&lt;String&gt; of filenames for the template that can be
	 *         generated
	 * @throws CoreException An error prevents determining the file names required for code generation 
	 */
	List<String> getAllGeneratedFileNames(final ImplementationSettings implSettings, final SoftPkg softPkg, final String language) throws CoreException;

	/**
	 * This method will generate the specified file, optionally using the
	 * helperObject.
	 * 
	 * @param fileName the name of the file to generate
	 * @param providesPort flag if this should be a provides port
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @param language the programming language to generate for
	 * @return a String representing the contents of the generated file
	 * @throws CoreException An error prevents generation of the file
	 */
	String generateFile(final String fileName, final boolean providesPort, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject, final String language) throws CoreException;

	/**
	 * This method will generate the class definition of the port, optionally 
	 * using the helperObject. This would be used in the .h for a
	 * C++ file, the .py for a Python file and .java for a Java file.
	 * 
	 * @param repId the name of the file to generate
	 * @param providesPort flag if this should be a provides port
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @param language the programming language to generate for
	 * @return a String representing the contents of the generated file
	 * @throws CoreException An error prevents generation of the file
	 */
	String generateClassDefinition(final String repId, final boolean providesPort, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject, final String language) throws CoreException;

	/**
	 * This method will generate the class implementation of the port,  
	 * optionally using the helperObject. This would typically be used in the 
	 * .cpp for a C++ file. This would be unused for Java.
	 * 
	 * @param repId the name of the file to generate
	 * @param providesPort flag if this should be a provides port
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @param language the programming language to generate for
	 * @return a String representing the contents of the generated file
	 * @throws CoreException An error prevents generation of the file
	 */
	String generateClassImplementation(final String repId, final boolean providesPort, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject, final String language) throws CoreException;

	/**
	 * Used to get includes/imports or any other outside of class definition
	 * support code.
	 * 
	 * @param repId the name of the file to generate
	 * @param providesPort flag if this should be a provides port
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @param language the programming language to generate for
	 * @return a String representing the code required to include/import the 
	 * specified repId
	 * @throws CoreException An error prevents generation of the file
	 */
	String generateClassSupport(final String repId, final boolean providesPort, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject, final String language) throws CoreException;

	/**
	 * This method is optional. It will generate the class instantiation of the  
	 * port, optionally using the helperObject. This would typically be used in
	 * the .cpp for a C++ file.
	 * 
	 * @param repId the name of the file to generate
	 * @param providesPort flag if this should be a provides port
	 * @param softPkg the SoftPkg for the project
	 * @param implSettings the implementation settings
	 * @param helperObject an optional object used to store details to aid in
	 *            generation
	 * @param language the programming language to generate for
	 * @return a String representing the contents of the generated file
	 * @throws CoreException An error prevents generation of the file
	 */
	String generateClassInstantiator(final String repId, final boolean providesPort, final SoftPkg softPkg, final ImplementationSettings implSettings,
	        final Object helperObject, final String language) throws CoreException;

	/**
	 * Boolean flag if generation should be performed or not.
	 * 
	 * @param language the programming language to generate for
	 * @return true if the generator produces files that should get generated
	 */
	boolean shouldGenerate(final String language);

	/**
	 * This sets the interfaces that the template should generate. This should
	 * not be called by any users.
	 * 
	 * @param interfaces the interfaces this template supports
	 */
	void setInterfaces(String[] interfaces);
}
