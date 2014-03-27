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

import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;
import gov.redhawk.ide.spd.ui.wizard.ScaResourceProjectPropertiesWizardPage;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * The Class ScaDeviceProjectPropertiesWizardPage.
 */
public class ScaDeviceProjectPropertiesWizardPage extends ScaResourceProjectPropertiesWizardPage {

	private static class DeviceProjectSettings {
		String deviceType =  RedhawkIdePreferenceConstants.DEVICE;
		boolean aggregate = false;

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public boolean isAggregate() {
			return aggregate;
		}

		public void setAggregate(boolean aggregate) {
			this.aggregate = aggregate;
		}
	}

	private DeviceProjectSettings deviceProjSettings = new DeviceProjectSettings();
	private Combo deviceTypeCombo;
	private Button aggregateButton;
	private Group deviceGroup;
	private DataBindingContext context;

	/**
	 * Instantiates a new sca resource project properties wizard page.
	 * 
	 * @param pageName the page name
	 */
	protected ScaDeviceProjectPropertiesWizardPage(final String pageName, final String type) {
		super(pageName, type);
		this.setDescription("Choose to either create a new Device or import an existing one.");
		context = new DataBindingContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void customCreateControl(final Composite parent) {
		// Device Group
		deviceGroup = new Group(parent, SWT.NONE);
		deviceGroup.setText(getResourceType());
		deviceGroup.setLayout(new GridLayout(2, false));
		GridDataFactory.generate(deviceGroup, 2, 1);

		deviceTypeCombo = new Combo(deviceGroup, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);
		deviceTypeCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		deviceTypeCombo.setItems(RedhawkIdePreferenceConstants.DEVICE_TYPES);
		deviceTypeCombo.select(0);

		aggregateButton = new Button(deviceGroup, SWT.CHECK);
		aggregateButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		aggregateButton.setText(RedhawkIdePreferenceConstants.AGGREGATE_DEVICE + " device");

		context.bindValue(WidgetProperties.text().observe(deviceTypeCombo), PojoProperties.value("deviceType").observe(this.deviceProjSettings));
		context.bindValue(WidgetProperties.selection().observe(aggregateButton), PojoProperties.value("aggregate").observe(this.deviceProjSettings));
		deviceTypeCombo.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(DisposeEvent e) {
				if (context != null) {
					context.dispose();
				}
			}
		});

	}

	@Override
	public void dispose() {
		super.dispose();
		if (context != null) {
			context.dispose();
			context = null;
		}
	}

	@Override
	protected void createContentsGroup(Composite parent) {
		super.createContentsGroup(parent);
		getContentsGroup().getImportFileButton().addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				if (getContentsGroup().getImportFileButton().getSelection()) {
					deviceGroup.setEnabled(false);
					deviceTypeCombo.setEnabled(false);
					aggregateButton.setSelection(false);
					aggregateButton.setEnabled(false);
				} else {
					deviceGroup.setEnabled(true);
					deviceTypeCombo.setEnabled(true);
					deviceTypeCombo.select(0);
					aggregateButton.setEnabled(true);
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @return
	 */
	public String getDeviceType() {
		return deviceProjSettings.getDeviceType();
	}

	/**
	 * @return
	 */
	public boolean getAggregateDeviceType() {
		return deviceProjSettings.isAggregate();
	}

}
