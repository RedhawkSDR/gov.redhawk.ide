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

import org.eclipse.core.runtime.CoreException;

/**
 * 
 */
public interface ICodeGeneratorDescriptor extends Comparable<ICodeGeneratorDescriptor> {
	/**
	 * @since 9.0
	 */
	public static final String COMPONENT_TYPE_RESOURCE = "resource";
	
	/**
	 * @since 9.0
	 */
	public static final String COMPONENT_TYPE_DEVICE = "device";
	
	/**
	 * @since 9.0
	 */
	public static final String COMPONENT_TYPE_SERVICE = "service";
	
	String getName();

	String getId();

	String getIconPath();

	String getLanguage();

	String getLanguageVersion();

	String getCompiler();

	String getCompilerVersion();

	String getRuntime();

	String getRuntimeVersion();

	String getDescription();

	String getHumanLanguage();

	String getContributingBundleId();

	/**
	 * @since 5.0
	 */
	String getAssociatedPerspectiveId();

	boolean notDefaultableGenerator();

	/**
	 * @since 4.0
	 */
	boolean autoGenerate();

	IOperatingSystem[] getOperatingSystems();

	IProcessor[] getProcessors();

	IScaComponentCodegen getGenerator() throws CoreException;
	
	/**
	 * @since 9.0
	 */
	String getPriority();

	/**
	 * Returns true if the generator supports the specified component type.
	 * 
	 * @param componentType
	 * @return true if the generator supports the given component type
	 * @since 9.0
	 */
	boolean supportsComponentType(String componentType);
}
