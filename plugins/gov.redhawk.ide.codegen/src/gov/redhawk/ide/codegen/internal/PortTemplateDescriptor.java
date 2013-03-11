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

import gov.redhawk.ide.codegen.IPortTemplateDesc;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.IScaPortCodegenTemplate;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 7.0
 * 
 */
public class PortTemplateDescriptor implements IPortTemplateDesc {

	private static final String ATTR_CLASS = "class";
	private static final String ATTR_CODEGEN_ID = "codegenId";
	private static final String ATT_ID = "id";
	private static final String ATT_IDLINTERFACE = "idlInterface";
	private static final String ATT_NAME = "name";
	private static final String ATT_NOT_DEFAULTABLE = "notDefaultable";
	private static final String ATT_PRIORITY = "priority";
	private static final String ELM_INTERFACES = "interfaces";
	private static final String ELM_LANGUAGE = "language";
	private static final String ELM_PORT_INTERFACE = "portInterface";
	private static final String ELM_PROPERTY = "property";

	private String description;
	private final String name;
	private final String bundleId;
	private final String codegenId;
	private final String id;
	private final String[] interfaces;
	private final String[] languages;
	private final IPropertyDescriptor[] propertyDescs;
	private final boolean hasSettings;
	private final String notDefaultable;
	private final IConfigurationElement element;
	private final String priority;

	public PortTemplateDescriptor(final IConfigurationElement element) {
		this.element = element;
		this.id = element.getAttribute(PortTemplateDescriptor.ATT_ID);
		this.name = element.getAttribute(PortTemplateDescriptor.ATT_NAME);
		this.notDefaultable = element.getAttribute(PortTemplateDescriptor.ATT_NOT_DEFAULTABLE);
		this.codegenId = element.getAttribute(PortTemplateDescriptor.ATTR_CODEGEN_ID);
		this.bundleId = element.getContributor().getName();
		this.priority = element.getAttribute(PortTemplateDescriptor.ATT_PRIORITY);
		final IConfigurationElement[] children = element.getChildren(CodeGeneratorDescriptor.ELM_DESCRIPTION);
		if (children.length == 1) {
			this.description = children[0].getValue();
		}

		final List<String> ifaceDesc = new ArrayList<String>();
		final IConfigurationElement[] childrens = element.getChildren(PortTemplateDescriptor.ELM_INTERFACES);
		if (children.length == 1) {
			for (final IConfigurationElement elem : childrens[0].getChildren(PortTemplateDescriptor.ELM_PORT_INTERFACE)) {
				ifaceDesc.add(elem.getAttribute(PortTemplateDescriptor.ATT_IDLINTERFACE));
			}
		}
		this.interfaces = ifaceDesc.toArray(new String[ifaceDesc.size()]);

		final List<String> langDesc = new ArrayList<String>();
		for (final IConfigurationElement elem : element.getChildren(PortTemplateDescriptor.ELM_LANGUAGE)) {
			langDesc.add(elem.getAttribute(PortTemplateDescriptor.ATT_NAME));
		}
		this.languages = langDesc.toArray(new String[langDesc.size()]);

		final List<IPropertyDescriptor> tempDesc = new ArrayList<IPropertyDescriptor>();
		for (final IConfigurationElement elem : element.getChildren(PortTemplateDescriptor.ELM_PROPERTY)) {
			tempDesc.add(new PropertyDescriptor(elem));
		}
		this.propertyDescs = tempDesc.toArray(new IPropertyDescriptor[tempDesc.size()]);
		this.hasSettings = this.propertyDescs.length != 0;
	}

	public String getDescription() {
		return this.description;
	}

	public String getName() {
		return this.name;
	}

	public String getContributingBundleID() {
		return this.bundleId;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return this.propertyDescs;
	}

	public boolean hasSettings() {
		return this.hasSettings;
	}

	public boolean notDefaultableGenerator() {
		return Boolean.parseBoolean(this.notDefaultable);
	}

	public String getId() {
		return this.id;
	}

	public String getCodegenId() {
		return this.codegenId;
	}

	/**
	 * {@inheritDoc}
	 */
	public IScaPortCodegenTemplate getTemplate() throws CoreException {
		IScaPortCodegenTemplate templ = (IScaPortCodegenTemplate) this.element.createExecutableExtension(PortTemplateDescriptor.ATTR_CLASS);
		templ.setInterfaces(this.interfaces);
		return templ;
	}

	public int compareTo(final IPortTemplateDesc o) {
		if (o == null) {
			return -1;
		}
		if (!this.codegenId.equals(o.getCodegenId())) {
			return this.codegenId.compareTo(o.getCodegenId());
		}

		return this.name.compareTo(o.getName());
	}

	public String[] getInterfaces() {
		return this.interfaces;
	}

	public String[] getLanguages() {
		return this.languages;
	}

	public boolean pullInputData() {
		// TODO Auto-generated method stub
		return false;
	}

	public String getPriority() {
		return this.priority;
	}

}
