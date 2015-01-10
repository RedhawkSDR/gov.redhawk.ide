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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
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

public class UsesDeviceWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class Model {

		public static final String USES_DEVICE_ID = "usesDeviceId";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private String usesDeviceId;
		

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

	private Model model;
	private DataBindingContext dbc;
	
	public UsesDeviceWizardPage() {
		super("UsesDeviceWizardPage", "Allocate Device", TITLE_IMAGE);
		this.setDescription("Provide an ID for the device");

		model = new Model();
		dbc = new DataBindingContext();
	
	}
	
	public UsesDeviceWizardPage(String usesDeviceId) {
		this();
		model.setUsesDeviceId(usesDeviceId);
	
	}
	
	public UsesDeviceWizardPage(String usesDeviceId, String deviceModel) {
		this(usesDeviceId);
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
			BeansObservables.observeValue(model, Model.USES_DEVICE_ID));
		
		setControl(composite);

		dbc.updateModels();

	}


	public Model getModel() {
		return model;
	}
	
	

}