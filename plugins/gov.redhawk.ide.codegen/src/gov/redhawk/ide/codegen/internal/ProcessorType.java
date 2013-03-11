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
package gov.redhawk.ide.codegen.internal;

import gov.redhawk.ide.codegen.IProcessor;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 2.0
 */
public class ProcessorType implements IProcessor {

	private static final String ATTR_PROCESSOR_TYPE = "proc_type";

	private static final String ATTR_DEFAULT_PROCESSOR = "default";

	private final String procType;

	private final boolean isDefault;

	public ProcessorType(final IConfigurationElement element) {
		this.procType = element.getAttribute(ProcessorType.ATTR_PROCESSOR_TYPE);
		this.isDefault = Boolean.valueOf(element.getAttribute(ProcessorType.ATTR_DEFAULT_PROCESSOR));
	}

	public String getProcessorType() {
		return this.procType;
	}

	public boolean isDefault() {
		return this.isDefault;
	}

}
