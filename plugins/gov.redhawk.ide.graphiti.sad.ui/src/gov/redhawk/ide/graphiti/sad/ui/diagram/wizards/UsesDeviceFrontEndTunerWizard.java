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
import gov.redhawk.frontend.ui.wizard.FEIStructDefaults;
import gov.redhawk.frontend.ui.wizard.ScannerAllocationWizardPage;
import gov.redhawk.frontend.ui.wizard.TunerAllocationWizardPage;
import gov.redhawk.frontend.util.TunerProperties.ListenerAllocationProperty;
import gov.redhawk.frontend.util.TunerProperties.ScannerAllocationProperty;
import gov.redhawk.frontend.util.TunerProperties.TunerAllocationProperty;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;
import gov.redhawk.model.sca.ScaAbstractProperty;
import gov.redhawk.model.sca.ScaStructProperty;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AbstractPropertyRef;
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
import mil.jpeojtrs.sca.util.collections.FeatureMapList;

public class UsesDeviceFrontEndTunerWizard extends Wizard {

	private SoftwareAssembly sad;
	private UsesDeviceStub existingUsesDeviceStub;

	private SelectFrontEndTunerWizardPage selectFrontEndTunerWizardPage;
	private UsesDeviceFrontEndTunerWizardPage namePage;
	private TunerAllocationWizardPage allocationPage;
	private ScannerAllocationWizardPage scannerPage;
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
		namePage = new UsesDeviceFrontEndTunerWizardPage(sad);
		portsWizardPage = new PortsWizardPage();
		if (existingUsesDeviceStub == null) {
			addPagesNoExistingXML();
		} else {
			addPagesExistingXML();
		}
		addPage(namePage);
		addPage(allocationPage);
		addPage(scannerPage);
		addPage(portsWizardPage);
	}

	private void addPagesNoExistingXML() {
		selectFrontEndTunerWizardPage = new SelectFrontEndTunerWizardPage();
		addPage(selectFrontEndTunerWizardPage);

		String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, DEFAULT_DEVICE_ID_PREFIX);
		namePage.getModel().setUsesDeviceId(deviceId);

		allocationPage = new TunerAllocationWizardPage();
		scannerPage = new ScannerAllocationWizardPage();
	}

	private void addPagesExistingXML() {
		// Device ID / model
		namePage.getModel().setUsesDeviceId(existingUsesDeviceStub.getUsesDevice().getId());
		for (PropertyRef propRef : existingUsesDeviceStub.getUsesDevice().getPropertyRef()) {
			if (DEVICEMODEL.value.equals(propRef.getRefId())) {
				namePage.getModel().setDeviceModel(propRef.getValue());
				break;
			}
		}

		// Ports
		for (ProvidesPortStub p : existingUsesDeviceStub.getProvidesPortStubs()) {
			portsWizardPage.getModel().getProvidesPortNames().add(p.getName());
		}
		for (UsesPortStub p : existingUsesDeviceStub.getUsesPortStubs()) {
			portsWizardPage.getModel().getUsesPortNames().add(p.getName());
		}

		// Allocation structs
		ScaStructProperty tunerAllocationStruct = null;
		ScaStructProperty listenerAllocationStruct = null;
		ScaStructProperty scannerAllocationStruct = null;
		for (StructRef structRef : existingUsesDeviceStub.getUsesDevice().getStructRef()) {
			String structRefID = structRef.getRefID();
			if (TunerAllocationProperty.INSTANCE.getId().equals(structRefID)) {
				tunerAllocationStruct = FEIStructDefaults.defaultTunerAllocationStruct(""); //$NON-NLS-1$
				setValuesFromRefs(tunerAllocationStruct, structRef);
			} else if (ListenerAllocationProperty.INSTANCE.getId().equals(structRefID)) {
				listenerAllocationStruct = FEIStructDefaults.defaultListenerAllocationStruct();
				setValuesFromRefs(listenerAllocationStruct, structRef);
			} else if (ScannerAllocationProperty.INSTANCE.getId().equals(structRefID)) {
				scannerAllocationStruct = FEIStructDefaults.defaultScannerAllocationStruct();
				setValuesFromRefs(scannerAllocationStruct, structRef);
			}
		}
		if (tunerAllocationStruct != null) {
			allocationPage = new TunerAllocationWizardPage(tunerAllocationStruct);
		} else if (listenerAllocationStruct != null) {
			allocationPage = new TunerAllocationWizardPage(listenerAllocationStruct);
		} else {
			allocationPage = new TunerAllocationWizardPage();
		}
		if (scannerAllocationStruct != null) {
			scannerPage = new ScannerAllocationWizardPage(scannerAllocationStruct);
		} else {
			scannerPage = new ScannerAllocationWizardPage();
		}
	}

	private void setValuesFromRefs(ScaStructProperty struct, StructRef structRef) {
		for (AbstractPropertyRef< ? > existingField : new FeatureMapList<>(structRef.getRefs(), AbstractPropertyRef.class)) {
			String refId = existingField.getRefID();
			ScaAbstractProperty< ? > newField = struct.getField(refId);
			if (newField != null) {
				newField.setValueFromRef(existingField);
			}
		}
	}

	private boolean isScannerAllocation() {
		return allocationPage.isScannerAllocation();
	}

	@Override
	public boolean canFinish() {
		IWizardPage[] pages;
		if (isScannerAllocation()) {
			pages = new IWizardPage[] { namePage, allocationPage, scannerPage, portsWizardPage };
		} else {
			pages = new IWizardPage[] { namePage, allocationPage, portsWizardPage };
		}
		for (IWizardPage page : pages) {
			if (!page.isPageComplete()) {
				return false;
			}
		}
		return true;
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
				String baseName;
				if (spd.getName().indexOf('.') == -1) {
					baseName = spd.getName();
				} else {
					baseName = spd.getName().substring(spd.getName().lastIndexOf('.') + 1);
				}
				String deviceId = AbstractUsesDevicePattern.getUniqueUsesDeviceId(sad, baseName + "_"); //$NON-NLS-1$
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
		} else if (page == allocationPage && !isScannerAllocation()) {
			return portsWizardPage;
		}

		return super.getNextPage(page);
	}

	@Override
	public int getPageCount() {
		int count = super.getPageCount();

		// Report 1 fewer page if using the allocate wizard pages, and scanner isn't chosen
		return (!isScannerAllocation()) ? count - 1 : count;
	}

	@Override
	public boolean performFinish() {
		return true;
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

	public ScannerAllocationWizardPage getScannerPage() {
		return scannerPage;
	}

	public PortsWizardPage getPortsWizardPage() {
		return portsWizardPage;
	}

}
