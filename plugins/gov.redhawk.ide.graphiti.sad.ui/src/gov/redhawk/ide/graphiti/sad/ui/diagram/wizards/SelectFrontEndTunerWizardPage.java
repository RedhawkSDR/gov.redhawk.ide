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

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.impl.SoftPkgImpl;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

import ExtendedCF.WKP.DEVICEKIND;
import FRONTEND.FE_TUNER_DEVICE_KIND;

public class SelectFrontEndTunerWizardPage extends WizardPage {

	private static final ImageDescriptor TITLE_IMAGE = null;
	
	private final SoftPkg[] targetSDRFrontEndDevices;
	private SoftPkg selectedDevice = null;
	private TableViewer tableViewer;
	
	public SelectFrontEndTunerWizardPage() {
		super("SelectFrontEndTunerWizardPage", "Allocate Tuner", TITLE_IMAGE);
		this.setDescription("Select a FrontEnd Tuner installed on your system.\nThis will pre-populate device model and ports in the pages ahead.");

		SoftPkg[] targetSDRDevices = SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer().getComponents().toArray(new SoftPkg[0]);
		targetSDRFrontEndDevices = getFrontEndDevices(targetSDRDevices).toArray(new SoftPkg[0]);
		
	}

	/**
	 * Return all FrontEnd Devices from the provided list
	 * @param devices
	 * @return
	 */
	public List<SoftPkg> getFrontEndDevices(SoftPkg[] devices) {
		List<SoftPkg> frontEndDevices = new ArrayList<SoftPkg>();
		
		//add Generic
		SoftPkg genericEntry = SpdFactory.eINSTANCE.createSoftPkg();
		genericEntry.setName("Generic FrontEnd Device");
		frontEndDevices.add(genericEntry);
		
		for (SoftPkg d: devices) {
			//null checks
			if (d.getPropertyFile() != null
					&& d.getPropertyFile().getProperties() != null) {
				AbstractProperty property = d.getPropertyFile().getProperties().getProperty(DEVICEKIND.value);
				if (property != null && property.toAny() != null
						&& property.toAny().toString().equals(FE_TUNER_DEVICE_KIND.value)) {
					frontEndDevices.add(d);
				}
			}
		}
		return frontEndDevices;
		
	}

	
	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		Label usesDeviceIdLabel = new Label(composite, SWT.NONE);
		usesDeviceIdLabel.setText("Select which FrontEnd device you would like to use?");

		createFrontEndDeviceTable(composite);
		
		setControl(composite);
		setPageComplete(true);

	}
	
	private void createFrontEndDeviceTable(final Composite parent) {
		GridDataFactory dataFactory = GridDataFactory.fillDefaults();
		
		tableViewer = new TableViewer(new Table(parent, SWT.BORDER));
		tableViewer.getControl().setLayoutData(dataFactory.span(1, 1).grab(true, true).create());

		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());

		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(factory), 
			PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()) {

			@Override
			public String getText(final Object element) {
				if (element instanceof SoftPkgImpl) {
					final SoftPkgImpl softPkg = (SoftPkgImpl) element;
					if (softPkg.getId() != null) {
						final URI uri = softPkg.eResource().getURI();
						return softPkg.getName() + " (" + uri.path().replace(uri.lastSegment(), "") + ")";
					} else {
						return softPkg.getName();
					}
				}
	
				return "";
			}
		});
		
		//set selection
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				List<Object> selection = Arrays.asList(((IStructuredSelection) event.getSelection()).toArray());
				for (Object obj : selection) {
					selectedDevice = (SoftPkg) obj;
				}
			}
		});
		tableViewer.setInput(targetSDRFrontEndDevices);
		
	}

	public SoftPkg getSelectedDevice() {
		return selectedDevice;
	}
}