/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.ui.diagram.wizards;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import ExtendedCF.WKP.DEVICEMODEL;
import gov.redhawk.frontend.ui.wizard.TunerAllocationWizardPage;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperty;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperties;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperty;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.model.sca.ScaFactory;
import gov.redhawk.model.sca.ScaSimpleProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.StructRef;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.scd.ComponentFeatures;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.PropertyRef;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class UsesDeviceFrontEndTunerWizard extends Wizard {

	private SoftwareAssembly sad;
	private UsesDeviceStub existingUsesDeviceStub;
	private SelectFrontEndTunerWizardPage selectFrontEndTunerWizardPage;
	private UsesDeviceFrontEndTunerWizardPage namePage;
	private TunerAllocationWizardPage allocationPage;
	private PortsWizardPage portsWizardPage;
	private static final String DEFAULT_DEVICE_ID_PREFIX = "FrontEndTuner_"; //$NON-NLS-1$

	public UsesDeviceFrontEndTunerWizard(SoftwareAssembly sad) {
		this.setWindowTitle(Messages.UsesDeviceFrontEndTunerWizard_WindowTitle);
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
			selectFrontEndTunerWizardPage = new SelectFrontEndTunerWizardPage();
			addPage(selectFrontEndTunerWizardPage);
			String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, DEFAULT_DEVICE_ID_PREFIX);
			namePage = new UsesDeviceFrontEndTunerWizardPage(sad, deviceId);
			allocationPage = new TunerAllocationWizardPage();
			portsWizardPage = new PortsWizardPage();
		} else {
			// get device model
			String deviceModel = null;
			for (PropertyRef propRef : existingUsesDeviceStub.getUsesDevice().getPropertyRef()) {
				if (DEVICEMODEL.value.equals(propRef.getRefId())) {
					deviceModel = propRef.getValue();
				}
			}

			// create name page for wizard
			namePage = new UsesDeviceFrontEndTunerWizardPage(sad, existingUsesDeviceStub.getUsesDevice().getId(), deviceModel);

			// ports
			List<String> providesPortNames = new ArrayList<String>();
			for (ProvidesPortStub p : existingUsesDeviceStub.getProvidesPortStubs()) {
				providesPortNames.add(p.getName());
			}
			List<String> usesPortNames = new ArrayList<String>();
			for (UsesPortStub p : existingUsesDeviceStub.getUsesPortStubs()) {
				usesPortNames.add(p.getName());
			}
			portsWizardPage = new PortsWizardPage(providesPortNames, usesPortNames);

			// determine which struct was set in Device, tuner_allocation or listener_allocation
			String structRefId = getFrontEndTunerStructRefId(existingUsesDeviceStub);

			if (structRefId == null) {
				// no allocation structure found in sad
				allocationPage = new TunerAllocationWizardPage();
			} else if (TunerAllocationProperty.INSTANCE.getId().equals(structRefId)) {
				// populate StructRef with tuner_allocation properties
				tunerAllocationStruct = ScaFactory.eINSTANCE.createScaStructProperty();
				tunerAllocationStruct.setId(structRefId);
				tunerAllocationStruct.setDefinition(TunerAllocationProperty.INSTANCE.createProperty());
				for (TunerAllocationProperties propDetails : TunerAllocationProperties.values()) {
					ScaSimpleProperty simple = tunerAllocationStruct.getSimple(propDetails.getId());
					String value = UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(),
						propDetails.getId());
					switch (simple.getDefinition().getType()) {
					case STRING:
						simple.setValue(value);
						break;
					case BOOLEAN:
						simple.setValue(Boolean.valueOf(value));
						break;
					case DOUBLE:
						simple.setValue(Double.valueOf(value));
						break;
					default:
						break;
					}
				}
				allocationPage = new TunerAllocationWizardPage(tunerAllocationStruct);
			} else if (ListenerAllocationProperty.INSTANCE.getId().equals(structRefId)) {
				// populate StructRef with listener_allocation properties
				listenerAllocationStruct = ScaFactory.eINSTANCE.createScaStructProperty();
				listenerAllocationStruct.setId(structRefId);
				listenerAllocationStruct.setDefinition(ListenerAllocationProperty.INSTANCE.createProperty());
				for (ListenerAllocationProperties propDetails : ListenerAllocationProperties.values()) {
					ScaSimpleProperty simple = listenerAllocationStruct.getSimple(propDetails.getId());
					String value = UsesDeviceFrontEndTunerPattern.getFEUsesDeviceTunerAllocationProp(existingUsesDeviceStub.getUsesDevice(),
						propDetails.getId());
					simple.setValue(value);
				}
				allocationPage = new TunerAllocationWizardPage(listenerAllocationStruct);
			}
		}

		addPage(namePage);
		addPage(allocationPage);
		addPage(portsWizardPage);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof SelectFrontEndTunerWizardPage && selectFrontEndTunerWizardPage != null
			&& selectFrontEndTunerWizardPage.getSelectedDevice() != null) {

			SoftPkg spd = selectFrontEndTunerWizardPage.getSelectedDevice();

			if (spd.getId() == null) {
				// device model
				String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, DEFAULT_DEVICE_ID_PREFIX);
				namePage.getModel().setUsesDeviceId(deviceId);
				// device model
				namePage.getModel().setDeviceModel(""); //$NON-NLS-1$
				// ports
				portsWizardPage.getModel().setProvidesPortNames(new ArrayList<String>());
				portsWizardPage.getModel().setUsesPortNames(new ArrayList<String>());

			} else {
				// real frontEnd device selected
				final Descriptor desc = spd.getDescriptor();
				final SoftwareComponent scd = desc.getComponent();
				ComponentFeatures features = scd.getComponentFeatures();
				if (features == null) {
					features = ScdFactory.eINSTANCE.createComponentFeatures();
					scd.setComponentFeatures(features);
				}
				Ports ports = features.getPorts();

				// pre-populate deviceId
				String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, spd.getName() + "_"); //$NON-NLS-1$
				namePage.getModel().setUsesDeviceId(deviceId);

				// pre-populate device_model
				AbstractProperty property = spd.getPropertyFile().getProperties().getProperty(DEVICEMODEL.value);
				if (property != null && !"null".equals(property.toAny().toString())) {
					namePage.getModel().setDeviceModel(property.toAny().toString());
				}

				// pre-set ports
				List<String> providesPortNames = new ArrayList<String>();
				for (Provides p : ports.getProvides()) {
					providesPortNames.add(p.getName());
				}
				List<String> usesPortNames = new ArrayList<String>();
				for (Uses p : ports.getUses()) {
					usesPortNames.add(p.getName());
				}
				portsWizardPage.getModel().setProvidesPortNames(providesPortNames);
				portsWizardPage.getModel().setUsesPortNames(usesPortNames);
			}
		}

		return super.getNextPage(page);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	/**
	 * Return the first struct ref id
	 * @param usesDeviceStub
	 * @return
	 */
	private static String getFrontEndTunerStructRefId(UsesDeviceStub usesDeviceStub) {
		if (usesDeviceStub.getUsesDevice().getStructRef() == null || usesDeviceStub.getUsesDevice().getStructRef().size() < 1) {
			return null;
		}

		StructRef structRef = usesDeviceStub.getUsesDevice().getStructRef().get(0);
		return structRef.getRefID();
	}

	public SelectFrontEndTunerWizardPage getSelectFrontEndTunerWizardPage() {
		return selectFrontEndTunerWizardPage;
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
