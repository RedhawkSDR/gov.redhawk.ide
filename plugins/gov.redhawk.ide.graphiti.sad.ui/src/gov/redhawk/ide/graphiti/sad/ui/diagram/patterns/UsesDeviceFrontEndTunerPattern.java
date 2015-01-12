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
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import gov.redhawk.frontend.ui.FrontEndUIActivator.AllocationMode;
import gov.redhawk.frontend.util.TunerProperties;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperties;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.UsesDeviceFrontEndTunerWizard;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaStructProperty;

import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.UsesDeviceDependencies;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import ExtendedCF.WKP.DEVICEKIND;
import ExtendedCF.WKP.DEVICEMODEL;
import FRONTEND.FE_TUNER_DEVICE_KIND;

public class UsesDeviceFrontEndTunerPattern extends AbstractUsesDevicePattern implements IPattern {

	public static final String NAME = "Uses FrontEnd Tuner Device";

	public UsesDeviceFrontEndTunerPattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "";
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_USES_DEVICE_FRONTEND_TUNER;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof UsesDeviceStub) {
			UsesDeviceStub usesDeviceStub = (UsesDeviceStub) mainBusinessObject;
			if (usesDeviceStub != null && AbstractUsesDevicePattern.isFrontEndDevice(usesDeviceStub.getUsesDevice())) {
				return true;
			}
		}
		return false;
	}
//	
//	@Override
//	protected boolean isPatternRoot(PictogramElement pictogramElement) {
//		return false;
//	}

	// DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {
		
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		
		// prompt user for 
		final UsesDeviceFrontEndTunerWizard wizard = (UsesDeviceFrontEndTunerWizard) openWizard(
			new UsesDeviceFrontEndTunerWizard(sad));
		if (wizard == null) {
			return null;
		}
		
		//extract values from wizard
		final String usesDeviceId = wizard.getNamePage().getModel().getUsesDeviceId();
		final String deviceModel = wizard.getNamePage().getModel().getDeviceModel();
		final AllocationMode allocationMode = wizard.getAllocationPage().getAllocationMode();
		final ScaStructProperty tunerAllocationStruct = wizard.getAllocationPage().getTunerAllocationStruct();
		final ScaStructProperty listenerAllocationStruct = wizard.getAllocationPage().getListenerAllocationStruct();
		final List<String> usesPortNames = wizard.getPortsWizardPage().getModel().getUsesPortNames();
		final List<String> providesPortNames = wizard.getPortsWizardPage().getModel().getProvidesPortNames();

		final UsesDeviceStub[] usesDeviceStubs = new UsesDeviceStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				
				//set uses device dependencies if not already set
				UsesDeviceDependencies usesDeviceDependencies = sad.getUsesDeviceDependencies();
				if (usesDeviceDependencies == null) {
					usesDeviceDependencies = SadFactory.eINSTANCE.createUsesDeviceDependencies();
					sad.setUsesDeviceDependencies(usesDeviceDependencies);
				}
				
				//create device	
				//WE ADD DEVICE TO BOTH UsesDeviceStub & UsesDeviceDependencies
				//UsesDeviceStub is contained in the Graphiti diagram file, UsesDeviceDependencies is stored in the sad file
				UsesDevice usesDevice = SpdFactory.eINSTANCE.createUsesDevice();
				usesDeviceDependencies.getUsesdevice().add(usesDevice);
				usesDevice.setId(usesDeviceId);
				//usesDevice.setType(); //not using this type on purpose, no value according to Core Framework team

				PropertyRef deviceKindPropertyRef = SpdFactory.eINSTANCE.createPropertyRef();
				deviceKindPropertyRef.setRefId(DEVICEKIND.value);
				deviceKindPropertyRef.setValue(FE_TUNER_DEVICE_KIND.value);
				usesDevice.getPropertyRef().add(deviceKindPropertyRef);
				
				if (deviceModel != null && !deviceModel.isEmpty()) {
					//add deviceModel if set
					PropertyRef deviceModelPropertyRef = SpdFactory.eINSTANCE.createPropertyRef();
					deviceModelPropertyRef.setRefId(DEVICEMODEL.value);
					deviceModelPropertyRef.setValue(deviceModel);
					usesDevice.getPropertyRef().add(deviceModelPropertyRef);
				}
				
				StructRef allocationStructRef = PrfFactory.eINSTANCE.createStructRef();
				allocationStructRef.setProperty(TunerProperties.TunerAllocationProperty.INSTANCE.createStruct());
				usesDevice.getStructRef().add(allocationStructRef);
				
				//set tuner allocation struct in device from tuner allocation struct in wizard
				if (allocationMode == AllocationMode.TUNER) {
					allocationStructRef.setProperty(TunerProperties.TunerAllocationProperty.INSTANCE.createStruct());
					for (TunerAllocationProperties prop : TunerAllocationProperties.values()) {
						setFEUsesDeviceTunerAllocationProp(usesDevice, prop.getId(), String.valueOf(tunerAllocationStruct.getSimple(prop.getId()).getValue()));
					}
				} else if (allocationMode == AllocationMode.LISTENER) {
					allocationStructRef.setProperty(TunerProperties.ListenerAllocationProperty.INSTANCE.createStruct());
					for (ListenerAllocationProperties prop : ListenerAllocationProperties.values()) {
						setFEUsesDeviceTunerAllocationProp(usesDevice, prop.getId(), String.valueOf(listenerAllocationStruct.getSimple(prop.getId()).getValue()));
					}
				}
				
				//UsesDeviceStub
				usesDeviceStubs[0] = createUsesDeviceStub(usesDevice);
				
				// if applicable add uses port stub(s)
				if (usesPortNames != null) {
					for (String usesPortName : usesPortNames) {
						UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
						usesPortStub.setName(usesPortName);
						usesDeviceStubs[0].getUsesPortStubs().add(usesPortStub);
					}
				}

				// if applicable add provides port stub(s)
				if (providesPortNames != null) {
					for (String providesPortName : providesPortNames) {
						ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
						providesPortStub.setName(providesPortName);
						usesDeviceStubs[0].getProvidesPortStubs().add(providesPortStub);
					}
				}
				
			}
		});
		
		//store UsesDeviceStub in graphiti diagram
		getDiagram().eResource().getContents().add(usesDeviceStubs[0]);

		addGraphicalRepresentation(context, usesDeviceStubs[0]);

		return new Object[] { usesDeviceStubs[0] };
	}

	

	
	
	public static Wizard openWizard(Wizard wizard) {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return wizard;
	}
	
	/**
	 * Sets FrontEnd Uses Device Tuner Allocation Property
	 * if propValue null, remove property
	 * @param usesDevice
	 * @param propRefId
	 * @param propValue
	 */
	public static void setFEUsesDeviceTunerAllocationProp(UsesDevice usesDevice, String propRefId, String propValue) {
		
		EList<SimpleRef> props = usesDevice.getStructRef().get(0).getSimpleRef();
		
		boolean found = false;
		for (Iterator<SimpleRef> iter = props.iterator(); iter.hasNext();) {
			SimpleRef p = (SimpleRef) iter.next();
			if (propRefId.equals(p.getRefID())) {
				found = true;
				if (propValue != null) {
					p.setValue(propValue);
				} else {
					iter.remove();
				}
				break;
			}
		}
		
		//if property wasn't there, add it
		if (!found) {
			SimpleRef newProp = PrfFactory.eINSTANCE.createSimpleRef();
			newProp.setRefID(propRefId);
			newProp.setValue(propValue);
			props.add(newProp);
		}
	}
	
	/**
	 * Gets FrontEnd Uses Device Tuner Allocation Property
	 * @param usesDevice
	 * @param propRefId
	 */
	public static String getFEUsesDeviceTunerAllocationProp(UsesDevice usesDevice, String propRefId) {
		EList<SimpleRef> props = usesDevice.getStructRef().get(0).getSimpleRef();
		for (SimpleRef p: props) {
			if (propRefId.equals(p.getRefID())) {
				return p.getValue();
			}
		}
		return null;
	}

	

}
