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
 * 
 */
public interface ITemplateDesc extends Comparable<ITemplateDesc> {
	String getName();

	String getDescription();

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
