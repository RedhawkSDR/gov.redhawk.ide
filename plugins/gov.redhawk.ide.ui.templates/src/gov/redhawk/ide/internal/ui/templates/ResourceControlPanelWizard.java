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
package gov.redhawk.ide.internal.ui.templates;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.pde.ui.templates.ITemplateSection;
import org.eclipse.pde.ui.templates.NewPluginTemplateWizard;

/**
 * 
 */
public class ResourceControlPanelWizard extends NewPluginTemplateWizard {

	private ResourceControlPanelTemplateSection resourceTemplate;

	/**
	 * 
	 */
	public ResourceControlPanelWizard() {
		setWindowTitle("New plug-in project with a component control panel");
	}

	/*
	 * @see NewExtensionTemplateWizard#createTemplateSections()
	 */
	@Override
	public ITemplateSection[] createTemplateSections() {
		resourceTemplate = new ResourceControlPanelTemplateSection();
		return new ITemplateSection[] { resourceTemplate };
	}

	/**
	 * @param spd
	 */
	public void setResource(EObject obj) {
		resourceTemplate.setResource(obj);
	}
	
	public ResourceControlPanelTemplateSection getResourceControlPanelTemplateSection() {
		return this.resourceTemplate;
	}
}
