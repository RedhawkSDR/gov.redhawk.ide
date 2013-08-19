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

/**
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ICodeGeneratorsRegistry {
	
	/**
	 * Find the specified codegen.
	 * 
	 * @param the id of the code generator
	 * @return a particular codegen after finding it via it's reference id
	 */
	ICodeGeneratorDescriptor findCodegen(String id);

	/**
	 * Find codegen by language
	 * 
	 * @param language the programming language
	 * @return a codegen by looking it by it's language
	 */
	ICodeGeneratorDescriptor[] findCodegenByLanguage(String language);
	
	/**
	 * Find codegen by language
	 * 
	 * @param language the programming language
	 * @return a codegen by looking it by it's language
	 * @since 9.0
	 */
	ICodeGeneratorDescriptor[] findCodegenByLanguage(String language, String componenttype);

	/**
	 * Get all codegens.
	 */
	ICodeGeneratorDescriptor[] getCodegens();

	/**
	 * Get all of the language type currently registered
	 */
	String[] getLanguages();

}
