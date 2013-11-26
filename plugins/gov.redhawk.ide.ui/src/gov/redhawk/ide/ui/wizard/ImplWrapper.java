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
package gov.redhawk.ide.ui.wizard;

import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * @since 9.1
 */
public class ImplWrapper {

	private ProjectRecord project;
	private Implementation impl;
	private String template;

	public ImplWrapper(ProjectRecord project, Implementation impl) {
		this.setImpl(impl);
		this.setProject(project);
		setTemplate();
	}

	private void setTemplate() {
		if ("python".equals(impl.getId())) {
			template = "redhawk.codegen.jinja.python.component.pull";
			project.pythonImplTemplate = template;
		} else if ("cpp".equals(impl.getId())) {
			template = "redhawk.codegen.jinja.cpp.component.pull";
			project.cppImplTemplate = template;
		} else if ("java".equals(impl.getId())) {
			template = "redhawk.codegen.jinja.java.component.pull";
			project.javaImplTemplate = template;
		}
	}

	public ProjectRecord getProject() {
		return project;
	}

	public void setProject(ProjectRecord project) {
		this.project = project;
	}

	public Implementation getImpl() {
		return impl;
	}

	public void setImpl(Implementation impl) {
		this.impl = impl;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

}
