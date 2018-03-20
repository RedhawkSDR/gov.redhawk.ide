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
package gov.redhawk.ide.graphiti.sad.ui.diagram.wizards;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import gov.redhawk.ide.graphiti.ui.diagram.wizards.AbstractPortWizardPage;

public class PortsWizardPage extends AbstractPortWizardPage {

	/**
	 * Used as the model for UI input.
	 */
	public class Model extends AbstractPortWizardPage.AbstractPortModel {

		public Model() {
		}

	};

	private Model model;

	public PortsWizardPage() {
		super("portWizardPage", "Identify Ports");
		this.setDescription("Enter the names of any uses and provides ports from the device you want to use");

		model = new Model();
	}

	/**
	 * @deprecated Use the no-args constructor and {@link #getModel()}
	 */
	@Deprecated
	public PortsWizardPage(List<String> providesPortNames, List<String> usesPortNames) {
		this();
		getModel().setUsesPortNames(usesPortNames);
		getModel().setProvidesPortNames(providesPortNames);
	}

	@Override
	public void createTopSection(Composite parent) {
		// PASS - Nothing but ports on this page
	}

	public Model getModel() {
		return model;
	}
}
