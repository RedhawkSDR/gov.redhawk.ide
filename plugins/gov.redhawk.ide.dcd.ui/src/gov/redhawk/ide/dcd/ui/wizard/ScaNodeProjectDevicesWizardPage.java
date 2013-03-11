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
package gov.redhawk.ide.dcd.ui.wizard;

import java.util.Collections;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.impl.SoftPkgImpl;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.PlatformUI;

public class ScaNodeProjectDevicesWizardPage extends WizardPage {

	private final SoftPkg[] devices;
	private CheckboxTableViewer tableViewer;

	public ScaNodeProjectDevicesWizardPage(final String pageName, final SoftPkg[] devices) {
		super(pageName);
		setTitle("Select Devices for Node");
		this.devices = devices;
		this.setPageComplete(true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void createControl(final Composite parent) {
		// The top-level composite for this page
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		// Top Heading
		final Label directionsLabel = new Label(composite, SWT.NONE);
		directionsLabel.setText("Check the boxes next to the devices to include in this node:");
		GridDataFactory.generate(directionsLabel, 2, 1);

		this.tableViewer = new CheckboxTableViewer(new Table(composite, SWT.CHECK | SWT.BORDER));
		this.tableViewer.getControl().setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 2, 1));

		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());

		this.tableViewer.setContentProvider(new ArrayContentProvider());
		this.tableViewer.setLabelProvider(new DecoratingLabelProvider(new AdapterFactoryLabelProvider(factory), PlatformUI.getWorkbench().getDecoratorManager()
		        .getLabelDecorator()) {

			@Override
			public String getText(final Object element) {
				if (element instanceof SoftPkgImpl) {
					final SoftPkgImpl softPkg = (SoftPkgImpl) element;
					final URI uri = softPkg.eResource().getURI();
					return softPkg.getName() + " (" + uri.path().replace(uri.lastSegment(), "") + ")";
				}

				return "";
			}

		});
		this.tableViewer.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(final DoubleClickEvent event) {
				final StructuredSelection ss = (StructuredSelection) event.getSelection();
				final SoftPkg selected = (SoftPkg) ss.getFirstElement();
				ScaNodeProjectDevicesWizardPage.this.tableViewer.setChecked(selected, !ScaNodeProjectDevicesWizardPage.this.tableViewer.getChecked(selected));
			}
		});
		this.tableViewer.setInput(this.devices);
		this.tableViewer.setCheckedElements(Collections.EMPTY_LIST.toArray());

		setControl(composite);
	}

	@Override
	public void setVisible(final boolean visible) {
		this.setPageComplete(true);
		super.setVisible(visible);
	}

	public SoftPkg[] getNodeDevices() {
		final Object[] elements = this.tableViewer.getCheckedElements();
		final SoftPkg[] retVal = new SoftPkg[elements.length];
		System.arraycopy(elements, 0, retVal, 0, elements.length);
		return retVal;
	}

}
