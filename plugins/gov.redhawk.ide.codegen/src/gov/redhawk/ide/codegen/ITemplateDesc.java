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

import org.eclipse.core.runtime.CoreException;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITemplateDesc extends Comparable<ITemplateDesc> {
	String getName();

	String getDescription();
	
	/**
	 * @since 9.2
	 */
	boolean isDeprecated();
	
	/**
	 * @since 9.2
	 */
	ICodegenTemplateMigrator getMigrationTool();
	
	/**
	 * @since 9.2
	 */
	String getNewTemplateID();
	
	/**
	 * @since 9.2
	 */
	ITemplateDesc getNewTemplate();

	String getContributingBundleID();

	/**
	 * @since 7.0
	 */
	String getId();

	/**
	 * @since 7.0
	 */
	boolean hasSettings();

	/**
	 * @since 7.0
	 */
	boolean notDefaultableGenerator();

	/**
	 * @since 7.0
	 */
	IPropertyDescriptor[] getPropertyDescriptors();

	/**
	 * @since 7.0
	 */
	public IScaComponentCodegenTemplate getTemplate() throws CoreException;

	/**
	 * @since 7.0
	 */
	public String getCodegenId();
	
	/**
	 * @since 9.2
	 */
	public ICodeGeneratorDescriptor getCodegen();

	/**
	 * @since 7.0
	 */
	public String getPortGenId();

	/**
     * @since 7.0
     */
	boolean delegatePortGeneration();
	
	/**
	 * @since 8.0
	 */
	boolean isSelectable();

	/**
	 * @since 9.0
	 */
	boolean supportsComponentType(String componentType);

}
