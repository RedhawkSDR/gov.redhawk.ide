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

import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.jface.wizard.Wizard;


public class UsesDeviceWizard extends Wizard {

	private SoftwareAssembly sad;
	private UsesDeviceStub existingUsesDeviceStub;
	private UsesDeviceWizardPage namePage;
	private PortsWizardPage portsWizardPage;

	public UsesDeviceWizard(SoftwareAssembly sad) {
		this.sad = sad;
	}
	
	public UsesDeviceWizard(SoftwareAssembly sad, UsesDeviceStub existingUsesDevice) {
		this(sad);
		this.existingUsesDeviceStub = existingUsesDevice;
	}
	


	@Override
	public void addPages() {
		
		if (existingUsesDeviceStub == null) {

			String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, "Device_");
			
			namePage = new UsesDeviceWizardPage(deviceId);
			portsWizardPage = new PortsWizardPage();
		} else {
			
			//create name page for wizard
			namePage = new UsesDeviceWizardPage(existingUsesDeviceStub.getUsesDevice().getId());
			
			//ports
			List<String> providesPortNames = new ArrayList<String>();
			for (ProvidesPortStub p: existingUsesDeviceStub.getProvidesPortStubs()) {
				providesPortNames.add(p.getName());
			}
			List<String> usesPortNames = new ArrayList<String>();
			for (UsesPortStub p: existingUsesDeviceStub.getUsesPortStubs()) {
				usesPortNames.add(p.getName());
			}
			portsWizardPage = new PortsWizardPage(providesPortNames, usesPortNames);
			
			
		}
		
		addPage(namePage);
		addPage(portsWizardPage);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

	public UsesDeviceWizardPage getNamePage() {
		return namePage;
	}


	public PortsWizardPage getPortsWizardPage() {
		return portsWizardPage;
	}
}
