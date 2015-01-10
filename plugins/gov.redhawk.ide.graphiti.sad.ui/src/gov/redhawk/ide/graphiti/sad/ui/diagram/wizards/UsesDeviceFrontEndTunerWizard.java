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

import gov.redhawk.frontend.ui.wizard.TunerAllocationWizardPage;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperty;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperty;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaStructProperty;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.PropertyValueType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.UsesDeviceDependencies;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.jface.wizard.Wizard;

import ExtendedCF.WKP.DEVICEMODEL;


public class UsesDeviceFrontEndTunerWizard extends Wizard {

	private SoftwareAssembly sad;
	private UsesDeviceStub existingUsesDeviceStub;
	private UsesDeviceFrontEndTunerWizardPage namePage;
	private TunerAllocationWizardPage allocationPage;
	private PortsWizardPage portsWizardPage;

	public UsesDeviceFrontEndTunerWizard(SoftwareAssembly sad) {
		this.sad = sad;
	}
	
	public UsesDeviceFrontEndTunerWizard(SoftwareAssembly sad, UsesDeviceStub existingUsesDevice) {
		this(sad);
		this.existingUsesDeviceStub = existingUsesDevice;
	}
	


	@Override
	public void addPages() {
		
		ScaStructProperty tunerAllocationStruct = null;
		ScaStructProperty listenerAllocationStruct = null;
		
		if (existingUsesDeviceStub == null) {

			String deviceId = getUniqueUsesDeviceId(sad, "FrontEndTuner_");
			
			namePage = new UsesDeviceFrontEndTunerWizardPage(deviceId);
			allocationPage = new TunerAllocationWizardPage();
			portsWizardPage = new PortsWizardPage();
		} else {
			//get device model
			String deviceModel = null;
			for (PropertyRef propRef: existingUsesDeviceStub.getUsesDevice().getPropertyRef()) {
				if (DEVICEMODEL.value.equals(propRef.getRefId())) {
					deviceModel = propRef.getValue();
				}
			}
			
			//create name page for wizard
			namePage = new UsesDeviceFrontEndTunerWizardPage(existingUsesDeviceStub.getUsesDevice().getId(), deviceModel);
			
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
			
			//determine which struct was set in Device, tuner_allocation or listener_allocation
			String structRefId = getFrontEndTunerStructRefId(existingUsesDeviceStub);
			
			if (TunerAllocationProperty.INSTANCE.getId().equals(structRefId)) {
			
				// populate StructRef with tuner_allocation properties
				tunerAllocationStruct = ScaFactory.eINSTANCE.createScaStructProperty();
				tunerAllocationStruct.setId(structRefId);
				for (TunerAllocationProperties allocProp : TunerAllocationProperties.values()) {
					ScaSimpleProperty simple = ScaFactory.eINSTANCE.createScaSimpleProperty();
					Simple definition = (Simple) PrfFactory.eINSTANCE.create(PrfPackage.Literals.SIMPLE);
					definition.setType(allocProp.getType());
					definition.setId(allocProp.getType().getLiteral());
					definition.setName(allocProp.getType().getName());
					simple.setDefinition(definition);
					simple.setId(allocProp.getId());
					switch (allocProp.getType().getValue()) {
						case PropertyValueType.STRING_VALUE :
							simple.setValue(UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(), allocProp.getId()));
							break;
						case PropertyValueType.BOOLEAN_VALUE :
							simple.setValue(Boolean.valueOf(UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(), allocProp.getId())));
							break;
						case PropertyValueType.DOUBLE_VALUE :
							simple.setValue(Double.valueOf(UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(), allocProp.getId())));
							break;
						default :
								break;
					}
					tunerAllocationStruct.getSimples().add(simple);
					allocationPage = new TunerAllocationWizardPage(tunerAllocationStruct);
				}
			} else if (ListenerAllocationProperty.INSTANCE.getId().equals(structRefId)) { 
				
				// populate StructRef with listener_allocation properties
				listenerAllocationStruct = ScaFactory.eINSTANCE.createScaStructProperty();
				listenerAllocationStruct.setId(structRefId);
				for (ListenerAllocationProperties allocProp : ListenerAllocationProperties.values()) {
					ScaSimpleProperty simple = ScaFactory.eINSTANCE.createScaSimpleProperty();
					Simple definition = (Simple) PrfFactory.eINSTANCE.create(PrfPackage.Literals.SIMPLE);
					definition.setType(allocProp.getType());
					definition.setId(allocProp.getType().getLiteral());
					definition.setName(allocProp.getType().getName());
					simple.setDefinition(definition);
					simple.setId(allocProp.getId());
					simple.setValue(UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(), allocProp.getId()));
					listenerAllocationStruct.getSimples().add(simple);
					allocationPage = new TunerAllocationWizardPage(listenerAllocationStruct);
				}
			}
		}
		
		
		addPage(namePage);
		addPage(allocationPage);
		addPage(portsWizardPage);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

	private static String getFrontEndTunerStructRefId(UsesDeviceStub usesDeviceStub) {
		StructRef structRef = usesDeviceStub.getUsesDevice().getStructRef().get(0);
		return structRef.getRefID();
	}
	
	/**
	 * Create unique device id from provided prefix
	 * @param sad
	 * @param idPrefix
	 * @return
	 */
	private static String getUniqueUsesDeviceId(SoftwareAssembly sad, String idPrefix) {
		int suffixNum = 1;
		String proposedDeviceId = idPrefix + String.valueOf(suffixNum);
		UsesDeviceDependencies usesDeviceDependencies = sad.getUsesDeviceDependencies();
		if (usesDeviceDependencies == null || usesDeviceDependencies.getUsesdevice().size() < 1) {
			return proposedDeviceId;
		}
		
		
		while (true) {
			boolean found = false;
			for (UsesDevice usesDevice: usesDeviceDependencies.getUsesdevice()) {
				if (usesDevice.getId().equals(proposedDeviceId)) {
					found = true;
					break;
				}
			}
			if (found) {
				suffixNum++;
				proposedDeviceId = idPrefix + String.valueOf(suffixNum);
			} else {
				break;
			}
		}
		
		return proposedDeviceId;
	}


	public UsesDeviceFrontEndTunerWizardPage getNamePage() {
		return namePage;
	}

	public TunerAllocationWizardPage getAllocationPage() {
		return allocationPage;
	}

	public PortsWizardPage getPortsWizardPage() {
		return portsWizardPage;
	}
}
