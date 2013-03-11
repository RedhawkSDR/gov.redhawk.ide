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

/**
 * @since 7.0
 */
public interface ICodeGeneratorPortTemplatesRegistry {

	/**
	 * This returns the Port generator for the given ID
	 * 
	 * @param id id of the generator to use
	 * @return the generator
	 */
	public IPortTemplateDesc findTemplate(final String id);

	/**
	 * This returns all port templates.
	 * 
	 * @return all port templates registered
	 */
	public IPortTemplateDesc[] getTemplates();

	/**
	 * This returns all templates that can be used for a specific repId and
	 * language
	 * 
	 * @param repId the repId of the port
	 * @param language the language for generation
	 * @return the templates matching the id and language
	 */
	public IPortTemplateDesc[] findTemplatesByRepId(final String repId, final String language);

}
