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

import gov.redhawk.ide.codegen.IPropertyDescriptor;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 2.0
 * 
 */
public class PropertyDescriptor implements IPropertyDescriptor {

	private static final String ATTR_KEY = "key";

	private static final String ATTR_REQUIRED = "required";
	
	private static final String ATTR_DEPRECATED = "deprecated";

	private static final String ATTR_DEFAULTVALUE = "default_value";

	private static final String ATTR_NAME = "name";

	private final String key;

	private String description;

	private final boolean required;
	
	// deprecated properties are not shown in the UI for selection
	private final boolean deprecated;

	private final String defaultValue;

	private final String name;

	public PropertyDescriptor(final IConfigurationElement element) {
		this.key = element.getAttribute(PropertyDescriptor.ATTR_KEY);
		this.defaultValue = element.getAttribute(PropertyDescriptor.ATTR_DEFAULTVALUE);
		this.required = Boolean.valueOf(element.getAttribute(PropertyDescriptor.ATTR_REQUIRED));
		this.deprecated = Boolean.valueOf(element.getAttribute(PropertyDescriptor.ATTR_DEPRECATED));
		this.name = element.getAttribute(PropertyDescriptor.ATTR_NAME);

		final IConfigurationElement[] descChild = element.getChildren(CodeGeneratorDescriptor.ELM_DESCRIPTION);
		if (descChild.length == 1) {
			this.description = descChild[0].getValue();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return this.key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequired() {
		return this.required;
	}
	
	/**
	 * {@inheritDoc}
	 * @since 9.0
	 */
	@Override
	public boolean isDeprecated() {
		return this.deprecated;
	}

	@Override
	public String getDefaultValue() {
		return this.defaultValue;
	}

	/**
	 * @since 6.0
	 */
	@Override
	public String getName() {
		return this.name;
	}
}
