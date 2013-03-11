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
import gov.redhawk.ide.codegen.IScaComponentCodegenTemplate;
import gov.redhawk.ide.codegen.ITemplateDesc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 2.0
 * 
 */
public class TemplateDescriptor implements ITemplateDesc {

	private static final String ATTR_CLASS = "class";
	private static final String ATTR_CODEGEN_ID = "codegenId";
	private static final String ATTR_HAS_SETTINGS = "hasSettings";
	private static final String ATTR_ID = "id";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_NOT_DEFAULTABLE = "notDefaultable";
	private static final String ATTR_PORT_CODEGEN_ID = "portCodegenId";
	private static final String ATTR_SELECTABLE = "selectable";
	private static final String ATTR_USES_PORT_TEMPLATES = "usesPortTemplates";
	private static final String ELM_PROPERTY = "property";
	private static final String ELM_COMPONENT_TYPE = "componenttype";
	private static final String ELM_COMPONENT_TYPE_ATTR_TYPE = "type";

	private String description;
	private final String name;
	private final String bundleId;
	private final String codegenId;
	private final String portCodegenId;
	private final String id;
	private final IPropertyDescriptor[] propertyDescs;
	private final String hasSettings;
	private final String notDefaultable;
	private final String selectable;
	private final IConfigurationElement element;
	private final String delegatePortGeneration;
	private final String[] componentTypes;

	public TemplateDescriptor(final IConfigurationElement element) {
		this.element = element;
		this.hasSettings = element.getAttribute(TemplateDescriptor.ATTR_HAS_SETTINGS);
		this.id = element.getAttribute(TemplateDescriptor.ATTR_ID);
		this.name = element.getAttribute(TemplateDescriptor.ATTR_NAME);
		this.notDefaultable = element.getAttribute(TemplateDescriptor.ATTR_NOT_DEFAULTABLE);
		this.selectable = element.getAttribute(TemplateDescriptor.ATTR_SELECTABLE);
		this.codegenId = element.getAttribute(TemplateDescriptor.ATTR_CODEGEN_ID);
		this.portCodegenId = element.getAttribute(TemplateDescriptor.ATTR_PORT_CODEGEN_ID);
		this.bundleId = element.getContributor().getName();
		this.delegatePortGeneration = element.getAttribute(TemplateDescriptor.ATTR_USES_PORT_TEMPLATES);
		final IConfigurationElement[] children = element.getChildren(CodeGeneratorDescriptor.ELM_DESCRIPTION);
		if (children.length == 1) {
			this.description = children[0].getValue();
		}

		final List<IPropertyDescriptor> tempDesc = new ArrayList<IPropertyDescriptor>();
		for (final IConfigurationElement elem : element.getChildren(TemplateDescriptor.ELM_PROPERTY)) {
			tempDesc.add(new PropertyDescriptor(elem));
		}
		this.propertyDescs = tempDesc.toArray(new IPropertyDescriptor[tempDesc.size()]);
		
		final List<String> tempCt = new ArrayList<String>();
		for (final IConfigurationElement elem : element.getChildren(TemplateDescriptor.ELM_COMPONENT_TYPE)) {
			tempCt.add(elem.getAttribute(ELM_COMPONENT_TYPE_ATTR_TYPE));
		}
		this.componentTypes = tempCt.toArray(new String[tempCt.size()]);
		Arrays.sort(this.componentTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getContributingBundleID() {
		return this.bundleId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 7.0
	 */
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return this.propertyDescs;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 7.0
	 */
	public boolean hasSettings() {
		return Boolean.parseBoolean(this.hasSettings);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 7.0
	 */
	public boolean notDefaultableGenerator() {
		return Boolean.parseBoolean(this.notDefaultable);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 7.0
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @since 7.0
	 */
	public String getCodegenId() {
		return this.codegenId;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @since 7.0
	 */
	public IScaComponentCodegenTemplate getTemplate() throws CoreException {
		return (IScaComponentCodegenTemplate) this.element.createExecutableExtension(TemplateDescriptor.ATTR_CLASS);
	}

	/**
	 * @since 7.0
	 */
	public String getPortGenId() {
		return this.portCodegenId;
	}

	/**
     * @since 7.0
     */
	public boolean delegatePortGeneration() {
	    return Boolean.parseBoolean(this.delegatePortGeneration);
    }

	/**
	 * @since 7.0
	 */
	public int compareTo(final ITemplateDesc o) {
		if (o == null) {
			return -1;
		}
		if (!this.codegenId.equals(o.getCodegenId())) {
			return this.codegenId.compareTo(o.getCodegenId());
		}

		return this.name.compareTo(o.getName());
	}

	/**
     * @since 8.0
     */
	public boolean isSelectable() {
	    return Boolean.parseBoolean(this.selectable);
    }
	
	/**
	 * @since 9.0
	 */
	public boolean supportsComponentType(String componentType) {
		final int i = Arrays.binarySearch(this.componentTypes, componentType);
		return (i >= 0);
	}

}
