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
 * @since 7.0
 * 
 */
public interface IPortTemplateDesc extends Comparable<IPortTemplateDesc> {
	String getName();

	String getDescription();

	String getContributingBundleID();

	String getId();

	boolean pullInputData();

	IPropertyDescriptor[] getPropertyDescriptors();

	String[] getInterfaces();

	String[] getLanguages();

	public IScaPortCodegenTemplate getTemplate() throws CoreException;

	public String getCodegenId();

	public String getPriority();
}
