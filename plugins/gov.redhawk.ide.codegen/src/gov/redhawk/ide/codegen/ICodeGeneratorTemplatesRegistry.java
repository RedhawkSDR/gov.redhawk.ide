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
 * @since 7.0
 */
public interface ICodeGeneratorTemplatesRegistry {
	/**
	 * Find the specified codegen template.
	 * 
	 * @return A particular codegen template after finding it via it's reference
	 *         ID, or null if not found
	 */
	ITemplateDesc findTemplate(String id);

	/**
	 * Find codegen template by codeGenId
	 * 
	 * @return an array of codegen templates by looking it by it's language
	 */
	ITemplateDesc[] findTemplatesByCodegen(String codeGenId);
	
	/**
	 * Find codegen template by codeGenId
	 * 
	 * @return an array of codegen templates by looking it by it's language
	 * @since 9.0
	 */
	ITemplateDesc[] findTemplatesByCodegen(String codeGenId, String componentType);

	/**
	 * Get all codegens template.
	 */
	ITemplateDesc[] getTemplates();

}
