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

import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ExtendedCF.WKP.DEVICEKIND;
import FRONTEND.FE_DEVICE_KIND;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorContentProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorLabelProvider;
import gov.redhawk.sca.util.PropertyChangeSupport;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

public class SelectFrontEndTunerWizardPage extends WizardPage {

	private static final String SELECTED_DEVICE = "selectedDevice";

	private SoftPkg selectedDevice = null;
	private SoftPkg genericFeiDevice;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	private TreeViewer treeViewer;

	public SelectFrontEndTunerWizardPage() {
		super("SelectFrontEndTunerWizardPage", "Select Target Device", null);
		this.setDescription("Select a FrontEnd tuner installed on your system.\nThis will pre-populate many of the fields in the following wizard pages.");
		genericFeiDevice = SpdFactory.eINSTANCE.createSoftPkg();
		genericFeiDevice.setName("Generic FrontEnd Device");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		Label usesDeviceIdLabel = new Label(composite, SWT.NONE);
		usesDeviceIdLabel.setText("Which FrontEnd Device you would like to use?");

		createFrontEndDeviceTable(composite);

		setControl(composite);
	}

	private void createFrontEndDeviceTable(final Composite parent) {
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		treeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(true, true).minSize(1, 160).create());
		treeViewer.setContentProvider(new SdrNavigatorContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				Object[] parentElements = super.getElements(inputElement);

				// Add an entry at the beginning for a generic FEI device
				Object[] elements = Arrays.copyOf(parentElements, parentElements.length + 1);
				elements[elements.length - 1] = genericFeiDevice;
				return elements;
			}

			@Override
			public Object[] getChildren(Object object) {
				if (object instanceof SoftPkg) {
					return new Object[0];
				}
				return super.getChildren(object);
			}

			@Override
			public boolean hasChildren(Object object) {
				if (object instanceof SoftPkg) {
					return false;
				}
				return super.hasChildren(object);
			}
		});
		treeViewer.setLabelProvider(new SdrNavigatorLabelProvider());
		treeViewer.addFilter(new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				// Show folders and the generic FEI device
				if (element instanceof SoftPkgRegistry || element == genericFeiDevice) {
					return true;
				}

				// Show SPD's whose device kind shows they are an FEI device
				SoftPkg device = (SoftPkg) element;
				if (device.getPropertyFile() == null || device.getPropertyFile().getProperties() == null) {
					return false;
				}
				AbstractProperty property = device.getPropertyFile().getProperties().getProperty(DEVICEKIND.value);
				String kind = ((Simple) property).getValue();
				return property instanceof Simple && kind.contains(FE_DEVICE_KIND.value);
			}
		});
		treeViewer.setComparator(new ViewerComparator() {
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {
				// Show the generic FEI device first
				if (e1 == genericFeiDevice) {
					return -1;
				}
				if (e2 == genericFeiDevice) {
					return 1;
				}
				return super.compare(viewer, e1, e2);
			}
		});
		treeViewer.addSelectionChangedListener(event -> {
			Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (element instanceof SoftPkg) {
				selectedDevice = (SoftPkg) element;
				setPageComplete(true);
			} else {
				selectedDevice = null;
				setPageComplete(false);
			}
		});

		databind();

		treeViewer.setInput(TargetSdrRoot.getSdrRoot().getDevicesContainer());
		treeViewer.setSelection(new StructuredSelection(genericFeiDevice));
	}

	@SuppressWarnings("unchecked")
	private void databind() {
		DataBindingContext dbc = new DataBindingContext();
		dbc.bindValue(ViewersObservables.observeSingleSelection(treeViewer),
			BeanProperties.value(SelectFrontEndTunerWizardPage.class, SELECTED_DEVICE).observe(this), new UpdateValueStrategy().setBeforeSetValidator(value -> {
				if (!(value instanceof SoftPkg)) {
					return ValidationStatus.error("Must select a device");
				}
				return ValidationStatus.ok();
			}), null);

		WizardPageSupport.create(this, dbc);
	}

	public SoftPkg getSelectedDevice() {
		return selectedDevice;
	}

	public void setSelectedDevice(SoftPkg selectedDevice) {
		SoftPkg oldValue = this.selectedDevice;
		this.selectedDevice = selectedDevice;
		pcs.firePropertyChange(SELECTED_DEVICE, oldValue, this.selectedDevice);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

}
