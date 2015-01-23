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

import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.AbstractUsesDevicePattern;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class UsesDeviceFrontEndTunerWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class Model {

		public static final String USES_DEVICE_ID = "usesDeviceId";
		public static final String DEVICE_MODEL = "deviceModel";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private String usesDeviceId;
		private String deviceModel;
		private List<EObject> properties = new ArrayList<EObject>();
		

		public Model() {
		}

		public String getUsesDeviceId() {
			return usesDeviceId;
		}

		public void setUsesDeviceId(String usesDeviceId) {
			final String oldValue = this.usesDeviceId;
			this.usesDeviceId = usesDeviceId;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.USES_DEVICE_ID, oldValue, usesDeviceId));
		}

		public String getDeviceModel() {
			return deviceModel;
		}

		public void setDeviceModel(String deviceModel) {
			final String oldValue = this.deviceModel;
			this.deviceModel = deviceModel;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.DEVICE_MODEL, oldValue, deviceModel));
		}
		
		public List<EObject> getProperties() {
			return properties;
		}

		public void setProperties(List<EObject> properties) {
			this.properties = properties;
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		public boolean isComplete() {
			return true;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;
	
	private Text usesDeviceIdText;
	private Text deviceModelText;
	
	private SoftwareAssembly sad;
	private Model model;
	private DataBindingContext dbc;
	
	public UsesDeviceFrontEndTunerWizardPage(SoftwareAssembly sad) {
		super("UsesDeviceFrontEndTunerWizardPage", "Allocate Tuner", TITLE_IMAGE);
		this.setDescription("Provide the ID for the device you want to allocate and use.\nYou may optionally provide the device model.");

		model = new Model();
		dbc = new DataBindingContext();
	
		this.sad = sad;
	}
	
	public UsesDeviceFrontEndTunerWizardPage(SoftwareAssembly sad, String usesDeviceId) {
		this(sad);
		model.setUsesDeviceId(usesDeviceId);
	
	}
	
	public UsesDeviceFrontEndTunerWizardPage(SoftwareAssembly sad, String usesDeviceId, String deviceModel) {
		this(sad, usesDeviceId);
		model.setDeviceModel(deviceModel);
	}

	
	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(2, false));

		Label usesDeviceIdLabel = new Label(composite, SWT.NONE);
		usesDeviceIdLabel.setText("Uses Device Id");
		usesDeviceIdText = new Text(composite, SWT.BORDER);
		usesDeviceIdText.setToolTipText("Unique id for Device");
		usesDeviceIdText.setEnabled(true);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(usesDeviceIdText);
		dbc.bindValue(SWTObservables.observeText(usesDeviceIdText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.USES_DEVICE_ID),  
			new UpdateValueStrategy().setAfterGetValidator(new AbstractUsesDevicePattern.UsesDeviceIdValidator(sad, model.getUsesDeviceId())),
			null);
	
		Label deviceModelLabel = new Label(composite, SWT.NONE);
		deviceModelLabel.setText("Device Model (optional)");
		deviceModelText = new Text(composite, SWT.BORDER);
		deviceModelText.setToolTipText("The device's model (optional)");
		deviceModelText.setEnabled(true);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(deviceModelText);
		dbc.bindValue(SWTObservables.observeText(deviceModelText, SWT.Modify), 
			BeansObservables.observeValue(model, Model.DEVICE_MODEL));

		setControl(composite);

		dbc.updateModels();

	}
	
	public Model getModel() {
		return model;
	}
}