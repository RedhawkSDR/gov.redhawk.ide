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

import gov.redhawk.frontend.util.TunerProperties;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.UsesDeviceFrontEndTunerWizardPage;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

import java.util.Iterator;

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
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import ExtendedCF.WKP.DEVICEKIND;
import ExtendedCF.WKP.DEVICEMODEL;
import FRONTEND.FE_TUNER_DEVICE_KIND;

public class UsesDeviceFrontEndTunerPattern extends AbstractUsesDevicePattern implements IPattern {

	public static final String NAME = "FrontEnd Tuner";

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
		if (mainBusinessObject instanceof UsesDevice) {
			UsesDevice usesDevice = (UsesDevice) mainBusinessObject;
			if (usesDevice != null) {
				return true;
			}
		}
		return false;
	}

	// DIAGRAM FEATURES
	@Override
	public Object[] create(ICreateContext context) {
		// prompt user for CORBA Name
		final UsesDeviceFrontEndTunerWizardPage page = openWizard();
		if (page == null) {
			return null;
		}
		
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		final UsesDevice[] usesDevices = new UsesDevice[1];

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
				usesDevices[0] = SpdFactory.eINSTANCE.createUsesDevice();
				
				usesDevices[0].setType("newName");

				PropertyRef deviceKindPropertyRef = SpdFactory.eINSTANCE.createPropertyRef();
				deviceKindPropertyRef.setRefId(DEVICEKIND.value);
				deviceKindPropertyRef.setValue(FE_TUNER_DEVICE_KIND.value);
				usesDevices[0].getPropertyRef().add(deviceKindPropertyRef);
				
				PropertyRef deviceModelPropertyRef = SpdFactory.eINSTANCE.createPropertyRef();
				deviceModelPropertyRef.setRefId(DEVICEMODEL.value);
				deviceModelPropertyRef.setValue("USB");  //TODO: this can change
				usesDevices[0].getPropertyRef().add(deviceModelPropertyRef);
				
				StructRef tunerAllocationStructRef = PrfFactory.eINSTANCE.createStructRef();
				tunerAllocationStructRef.setProperty(TunerProperties.TunerAllocationProperty.INSTANCE.createStruct());
				usesDevices[0].getStructRef().add(tunerAllocationStructRef);
				
				//set values from wizard
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.TUNER_TYPE.getId(), page.getModel().getTunerType());
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.ALLOCATION_ID.getId(), page.getModel().getAllocationId());
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.CENTER_FREQUENCY.getId(), String.valueOf(page.getModel().getCenterFrequency()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.BANDWIDTH.getId(), String.valueOf(page.getModel().getBandwidth()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.BANDWIDTH_TOLERANCE.getId(), String.valueOf(page.getModel().getBandwidthTolerance()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.SAMPLE_RATE.getId(), String.valueOf(page.getModel().getSampleRate()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.SAMPLE_RATE_TOLERANCE.getId(), String.valueOf(page.getModel().getSampleRateTolerance()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.DEVICE_CONTROL.getId(), String.valueOf(page.getModel().getDeviceControl()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.GROUP_ID.getId(), String.valueOf(page.getModel().getGroupId()));
				setFEUsesDeviceTunerAllocationProp(usesDevices[0], TunerProperties.TunerAllocationProperties.RF_FLOW_ID.getId(), String.valueOf(page.getModel().getRfFlowId()));
				
				// add to diagram resource file
				getDiagram().eResource().getContents().add(usesDevices[0]);

			}
		});

		addGraphicalRepresentation(context, usesDevices[0]);

		return new Object[] { usesDevices[0] };
	}


	@Override
	public String getInnerTitle(UsesDevice usesDevice) {
		return usesDevice.getType();
	}
	
	@Override
	public void setInnerTitle(UsesDevice usesDevice, String value) {
		usesDevice.setType(value);
	}

	protected static UsesDeviceFrontEndTunerWizardPage openWizard() {
		return openWizard(null, new Wizard() {
			public boolean performFinish() {
				return true;
			}
		});
	}

	public static UsesDeviceFrontEndTunerWizardPage openWizard(UsesDevice existingUsesDevice, Wizard wizard) {
		UsesDeviceFrontEndTunerWizardPage page = new UsesDeviceFrontEndTunerWizardPage();
		wizard.addPage(page);
		if (existingUsesDevice != null) {
			fillWizardFieldsWithExistingProperties(page, existingUsesDevice);
		}
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		if (dialog.open() == WizardDialog.CANCEL) {
			return null;
		}
		return page;
	}

	/**
	 * Sets FrontEnd Uses Device Tuner Allocation Property
	 * if propValue null, remove property
	 * @param usesDevice
	 * @param propRefId
	 * @param propValue
	 */
	private static void setFEUsesDeviceTunerAllocationProp(UsesDevice usesDevice, String propRefId, String propValue) {
		
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
	private static String getFEUsesDeviceTunerAllocationProp(UsesDevice usesDevice, String propRefId) {
		EList<SimpleRef> props = usesDevice.getStructRef().get(0).getSimpleRef();
		for (SimpleRef p: props) {
			if (propRefId.equals(p.getRefID())) {
				return p.getValue();
			}
		}
		return null;
	}
	
	private static void fillWizardFieldsWithExistingProperties(UsesDeviceFrontEndTunerWizardPage page, UsesDevice usesDevice) {
		// Grab existing properties from usesDevice
		String tunerType = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.TUNER_TYPE.getId());
		String allocationId = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.ALLOCATION_ID.getId());
		String centerFrequency = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.CENTER_FREQUENCY.getId());
		String bandwidth = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.BANDWIDTH.getId());
		String bandwidthTolerance = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.BANDWIDTH_TOLERANCE.getId());
		String sampleRate = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.SAMPLE_RATE.getId());
		String sampleRateTolerance = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.SAMPLE_RATE_TOLERANCE.getId());
		String deviceControl = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.DEVICE_CONTROL.getId());
		String groupId = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.GROUP_ID.getId());
		String rfFlowId = getFEUsesDeviceTunerAllocationProp(usesDevice, TunerProperties.TunerAllocationProperties.RF_FLOW_ID.getId());

		// Fill wizard fields with existing properties
		page.getModel().setTunerType(tunerType);
		page.getModel().setAllocationId(allocationId);
		page.getModel().setCenterFrequency(Double.valueOf(centerFrequency));
		page.getModel().setBandwidth(Double.valueOf(bandwidth));
		page.getModel().setBandwidthTolerance(Double.valueOf(bandwidthTolerance));
		page.getModel().setSampleRate(Double.valueOf(sampleRate));
		page.getModel().setSampleRateTolerance(Double.valueOf(sampleRateTolerance));
		page.getModel().setDeviceControl(Boolean.valueOf(deviceControl));
		page.getModel().setGroupId(groupId);
		page.getModel().setRfFlowId(rfFlowId);
		
	}
	
	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape containerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(containerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof UsesDevice && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}
