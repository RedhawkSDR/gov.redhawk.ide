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

import gov.redhawk.frontend.ui.FrontEndUIActivator.AllocationMode;
import gov.redhawk.frontend.util.TunerProperties;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperties;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.UsesDeviceFrontEndTunerWizard;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaStructProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
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
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		// prompt user for 
		final UsesDeviceFrontEndTunerWizard wizard = (UsesDeviceFrontEndTunerWizard) UsesDeviceFrontEndTunerPattern.openWizard(
			new UsesDeviceFrontEndTunerWizard(sad, usesDeviceStub));
		if (wizard == null) {
			return;
		}
		
		//extract values from wizard
		final String usesDeviceId = wizard.getNamePage().getModel().getUsesDeviceId();
		final String deviceModel = wizard.getNamePage().getModel().getDeviceModel();
		final AllocationMode allocationMode = wizard.getAllocationPage().getAllocationMode();
		final ScaStructProperty tunerAllocationStruct = wizard.getAllocationPage().getTunerAllocationStruct();
		final ScaStructProperty listenerAllocationStruct = wizard.getAllocationPage().getListenerAllocationStruct();
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
				
				//clear existing structs
				usesDevice.getStructRef().clear();
				//create new struct
				StructRef allocationStructRef = PrfFactory.eINSTANCE.createStructRef();
				usesDevice.getStructRef().add(allocationStructRef);
				//populate usesDevice from wizard values
				if (allocationMode == AllocationMode.TUNER) {
					//set tuner allocation struct
					
					allocationStructRef.setProperty(TunerProperties.TunerAllocationProperty.INSTANCE.createStruct());
					for (TunerAllocationProperties prop : TunerAllocationProperties.values()) {
						String propertyValue = String.valueOf(tunerAllocationStruct.getSimple(prop.getId()).getValue());
						UsesDeviceFrontEndTunerPattern.setFEUsesDeviceTunerAllocationProp(usesDevice, prop.getId(), propertyValue); 
					}
				} else if (allocationMode == AllocationMode.LISTENER) {
					//set listener allocation struct
					
					allocationStructRef.setProperty(TunerProperties.ListenerAllocationProperty.INSTANCE.createStruct());
					for (ListenerAllocationProperties prop : ListenerAllocationProperties.values()) {
						String propertyValue = String.valueOf(listenerAllocationStruct.getSimple(prop.getId()).getValue());
						UsesDeviceFrontEndTunerPattern.setFEUsesDeviceTunerAllocationProp(usesDevice, prop.getId(), propertyValue); 
					}
				}
				
				//update ports
				updatePorts(usesDeviceStub, usesDeviceShape, usesPortNames, providesPortNames);
				
			}
		});
	}
	
	/**
	 * Remove all old UsesPortStubs ports and add new port names
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	private void updateUsesPortStubs(final Diagram diagram, final UsesDeviceStub usesDeviceStub, 
		final RHContainerShapeImpl usesDeviceShape, final List<String> usesPortNames) {
		
		// Mark the ports to delete
		List<UsesPortStub> portsToDelete = new ArrayList<UsesPortStub>();
		for (UsesPortStub uses : usesDeviceStub.getUsesPortStubs()) {
			portsToDelete.add(uses);
		}

		// Capture the existing connection information and delete the connection
		HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
		for (UsesPortStub portStub : portsToDelete) {
			Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
			EList<Connection> connections = portStubPe.getOutgoingConnections();
			if (!connections.isEmpty()) {
				for (Connection connection : connections) {
					oldConnectionMap.put(connection, portStub.getName());
				}
			}
		}

		// Add new ports to the element
		EList<UsesPortStub> usesPortStubs = new BasicEList<UsesPortStub>();
		for (String usesPortName : usesPortNames) {
			// Add the new port to the Domain model
			UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
			usesPortStub.setName(usesPortName);
			usesPortStubs.add(usesPortStub);
		}

		// Add the new ports to the Diagram model
		usesDeviceShape.addNewUsesPorts(usesPortStubs, featureProvider, null);
		usesDeviceStub.getUsesPortStubs().addAll(usesPortStubs);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor sourceAnchor = DUtil.getUsesAnchor(diagram, usesPortStubs, cursor.getValue());
			if (sourceAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(sourceAnchor);
				createContext.setTargetAnchor(cursor.getKey().getEnd());

				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
			}
		}
		// Delete all ports and rebuild from the list provided by the wizard
		usesDeviceStub.getUsesPortStubs().removeAll(portsToDelete);
	}
	
	/**
	 * Remove all old UsesPortStubs ports and add new port names
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	private void updateProvidesPortStubs(final Diagram diagram, final UsesDeviceStub usesDeviceStub, 
		final RHContainerShapeImpl usesDeviceShape, final List<String> providesPortNames) {
		// Mark the ports to delete
		List<ProvidesPortStub> portsToDelete = new ArrayList<ProvidesPortStub>();
		for (ProvidesPortStub provides : usesDeviceStub.getProvidesPortStubs()) {
			portsToDelete.add(provides);
		}

		// Capture the existing connection information and delete the connection
		HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
		for (ProvidesPortStub portStub : portsToDelete) {
			Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
			EList<Connection> connections = portStubPe.getIncomingConnections();
			if (!connections.isEmpty()) {
				for (Connection connection : connections) {
					oldConnectionMap.put(connection, portStub.getName());
				}
			}
		}

		// Add new ports to the element
		EList<ProvidesPortStub> providesPortStubs = new BasicEList<ProvidesPortStub>();
		for (String providesPortName : providesPortNames) {
			// Add the new port to the Domain model
			ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
			providesPortStub.setName(providesPortName);
			providesPortStubs.add(providesPortStub);
		}

		// Add the new ports to the Diagram model
		usesDeviceShape.addNewProvidesPorts(providesPortStubs, featureProvider, null);
		usesDeviceStub.getProvidesPortStubs().addAll(providesPortStubs);

		// Build the new connections using the reconnect feature
		for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
			// First check if port still even exists
			Anchor targetAnchor = DUtil.getProvidesAnchor(diagram, providesPortStubs, cursor.getValue());
			if (targetAnchor != null) {
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(cursor.getKey().getStart());
				createContext.setTargetAnchor(targetAnchor);

				ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
				for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
					if (createConnectionFeature.canCreate(createContext)) {
						createConnectionFeature.create(createContext);
					}
				}
			}

			// Delete the old connection
			for (int i = 0; i < oldConnectionMap.size(); i++) {
				DeleteContext deleteContext = new DeleteContext(cursor.getKey());
				featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
			}
		}
		// Delete all ports and rebuild from the list provided by the wizard
		usesDeviceStub.getProvidesPortStubs().removeAll(portsToDelete);
	}
	
	/**
	 * Remove all old ports, add new names
	 * Perform layout
	 * @param diagram
	 * @param usesDeviceStub
	 * @param usesDeviceShape
	 * @param usesPortNames
	 */
	private void updatePorts(final UsesDeviceStub usesDeviceStub, final RHContainerShapeImpl usesDeviceShape, final List<String> usesPortNames,
		final List<String> providesPortNames) {
		
		Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();

		updateUsesPortStubs(diagram, usesDeviceStub, usesDeviceShape, usesPortNames);
		
		updateProvidesPortStubs(diagram, usesDeviceStub, usesDeviceShape, providesPortNames);
		
		// Update the shape layout to account for any changes
		usesDeviceShape.layout();
	}

}
