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
package gov.redhawk.ide.debug.internal.ui.wizards;

import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorContentProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrNavigatorLabelProvider;
import gov.redhawk.ide.sdr.ui.navigator.SdrViewerSorter;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class SoftPkgSelectionPage extends WizardPage {

	private LaunchComponentWizard wizard;
	private TreeViewer viewer;

	private DataBindingContext dbc = new DataBindingContext();

	protected SoftPkgSelectionPage(LaunchComponentWizard wizard) {
		super("spdSelection", "Select software package", null);
		setDescription("Select the software package descriptor (SPD) to launch in the sandbox.");
		this.wizard = wizard;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		main.setLayout(new GridLayout(2, false));

		viewer = new TreeViewer(main, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL);
		viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(2, 1).minSize(1, 160).create());
		viewer.setContentProvider(new SdrNavigatorContentProvider() {
			@Override
			public boolean hasChildren(Object object) {
				if (object instanceof SoftPkg) {
					return false;
				}
				return super.hasChildren(object);
			}
		});
		viewer.setLabelProvider(new SdrNavigatorLabelProvider());
		viewer.setFilters(new ViewerFilter[] { new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) element;
					if (spd.getDescriptor() != null) {
						ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(spd.getDescriptor().getComponent());
						if (type != null) {
							switch (type) {
							case DEVICE:
							case DEVICE_MANAGER:
							case DOMAIN_MANAGER:
							case RESOURCE:
							case SERVICE:
								return true;
							default:
								return false;
							}
						}
					}
				} else if (element instanceof SoftPkgRegistry) {
					return true;
				}
				return false;
			}
		} });
		viewer.setComparator(new SdrViewerSorter());

		Group descriptionGroup = new Group(main, SWT.None);
		descriptionGroup.setText("Description");
		descriptionGroup.setLayout(new GridLayout());
		descriptionGroup.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).create());
		final Text descriptionField = new Text(descriptionGroup, SWT.READ_ONLY | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		descriptionField.setLayoutData(GridDataFactory.fillDefaults().hint(SWT.FILL, 100).grab(true, false).create());

		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Object selection = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (selection instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) selection;
					descriptionField.setText((spd.getDescription() == null) ? "" : spd.getDescription());
				} else {
					descriptionField.setText("");
				}
			}
		});

		dbc.bindValue(ViewersObservables.observeInput(viewer), BeanProperties.value(wizard.getClass(), "spdContainer").observe(wizard));
		dbc.bindValue(ViewersObservables.observeSingleSelection(viewer), BeanProperties.value(wizard.getClass(), "softPkg").observe(wizard),
			new UpdateValueStrategy().setBeforeSetValidator(new IValidator() {

				@Override
				public IStatus validate(Object value) {
					if (!(value instanceof SoftPkg)) {
						return ValidationStatus.error("Must select an spd");
					}
					return ValidationStatus.ok();
				}
			}), null);

		WizardPageSupport.create(this, dbc);

		setControl(main);
	}

	@Override
	public boolean isPageComplete() {
		if (viewer.getSelection().isEmpty()) {
			return false;
		}

		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		for (Iterator< ? > iterator = selection.iterator(); iterator.hasNext();) {
			if (!(iterator.next() instanceof SoftPkg)) {
				return false;
			}
		}

		return super.isPageComplete();
	}
}
