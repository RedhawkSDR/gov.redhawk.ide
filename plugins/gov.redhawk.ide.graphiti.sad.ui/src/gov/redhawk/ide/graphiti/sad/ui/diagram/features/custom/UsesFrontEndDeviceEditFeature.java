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
package gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.UsesDeviceFrontEndTunerWizard;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaStructProperty;

import java.util.List;

import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

import ExtendedCF.WKP.DEVICEMODEL;

public class UsesFrontEndDeviceEditFeature extends AbstractCustomFeature {

	private final IFeatureProvider featureProvider;
	
	/*
	 * Constructor
	 */
	public UsesFrontEndDeviceEditFeature(IFeatureProvider fp) {
		super(fp);
		this.featureProvider = fp;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Edit Uses FrontEnd Device";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "&Edit Uses FrontEnd Device";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {

		// We only want the edit context to show up for certain objects
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			Object obj = DUtil.getBusinessObject(pes[0]);
			if (obj instanceof UsesDeviceStub) {
				if (UsesDeviceFrontEndTunerPattern.isFrontEndDevice(((UsesDeviceStub) obj).getUsesDevice())) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		RHContainerShapeImpl usesDeviceShape = (RHContainerShapeImpl) context.getPictogramElements()[0];
		final UsesDeviceStub usesDevice = (UsesDeviceStub) DUtil.getBusinessObject(usesDeviceShape);
		editUsesDevice(usesDevice, usesDeviceShape);
		updatePictogramElement(usesDeviceShape);
		layoutPictogramElement(usesDeviceShape);
	}


	/**
	 * Open Wizard allowing edit of FrontEnd Tuner Allocation
	 * Persist selections in UsesDevice
	 * @param usesDevice
	 * @param usesDeviceShape
	 */
	private void editUsesDevice(final UsesDeviceStub usesDeviceStub, final RHContainerShapeImpl usesDeviceShape) {
		
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		
		// prompt user for 
		final UsesDeviceFrontEndTunerWizard wizard = (UsesDeviceFrontEndTunerWizard) UsesDeviceFrontEndTunerPattern.openWizard(
			new UsesDeviceFrontEndTunerWizard(sad, usesDeviceStub));
		if (wizard == null) {
			return;
		}
		
		//extract values from wizard
		final String usesDeviceId = wizard.getNamePage().getModel().getUsesDeviceId();
		final String deviceModel = wizard.getNamePage().getModel().getDeviceModel();
		ScaStructProperty allocationStruct = wizard.getAllocationPage().getAllocationStruct();
		final StructRef allocationStructRef = allocationStruct.createPropertyRef();
		final List<String> usesPortNames = wizard.getPortsWizardPage().getModel().getUsesPortNames();
		final List<String> providesPortNames = wizard.getPortsWizardPage().getModel().getProvidesPortNames();
		
		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				
				UsesDevice usesDevice = usesDeviceStub.getUsesDevice();
				
				//uses device id
				usesDevice.setId(usesDeviceId);
				
				//device model
				PropertyRef deviceModelPropertyRef = null;
				for (PropertyRef propRef: usesDevice.getPropertyRef()) {
					if (DEVICEMODEL.value.equals(propRef.getRefId())) {
						deviceModelPropertyRef = propRef;
					}
				}
				if (deviceModel == null || deviceModel.isEmpty()) {
					if (deviceModelPropertyRef != null) {
						//delete PropertyRef containing deviceModel
						EcoreUtil.delete(deviceModelPropertyRef);
					}
				} else if (deviceModelPropertyRef == null) {
					deviceModelPropertyRef = SpdFactory.eINSTANCE.createPropertyRef();
					usesDevice.getPropertyRef().add(deviceModelPropertyRef);
					deviceModelPropertyRef.setRefId(DEVICEMODEL.value);
					deviceModelPropertyRef.setValue(deviceModel);
				} else {
					deviceModelPropertyRef.setValue(deviceModel);
				}
				
				//replace existing structs
				usesDevice.getStructRef().clear();
				usesDevice.getStructRef().add(allocationStructRef);
				
				//update ports
				AbstractUsesDevicePattern.updatePorts(featureProvider, usesDeviceStub, usesDeviceShape, usesPortNames, providesPortNames);
				
			}
		});
	}
	
}
