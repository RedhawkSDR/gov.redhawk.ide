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
package gov.redhawk.ide.sad.graphiti.ui.diagram.wizards;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.ui.IdlInterfaceSelectionDialog;
import gov.redhawk.ide.sad.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage.CORBANameModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FindByServiceWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class Model {

		public static final String ENABLE_SERVICE_NAME = "enableServiceName";
		public static final String SERVICE_NAME = "serviceName";
		public static final String ENABLE_SERVICE_TYPE = "enableServiceType";
		public static final String SERVICE_TYPE = "serviceType";
		public static final String USES_PORT_NAMES = "usesPortNames";
		public static final String PROVIDES_PORT_NAMES = "providesPortNames";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private boolean enableServiceName = true;
		private boolean enableServiceType;
		private String serviceName;
		private String serviceType;
		private List<String> usesPortNames = new ArrayList<String>();
		private List<String> providesPortNames = new ArrayList<String>();


		private boolean serviceTypeSupportsPorts = false;

		public Model() {
		}

		public boolean isServiceTypeSupportsPorts() {
			return serviceTypeSupportsPorts;
		}

		public void setServiceTypeSupportsPorts(boolean serviceTypeSupportsPorts) {
			this.serviceTypeSupportsPorts = serviceTypeSupportsPorts;
		}

		public boolean getEnableServiceName() {
			return enableServiceName;
		}

		public boolean getEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceName(boolean enableServiceName) {
			final boolean oldValue = this.enableServiceName;
			this.enableServiceName = enableServiceName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.ENABLE_SERVICE_NAME, oldValue, enableServiceName));
		}

		public boolean setEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceType(boolean enableServiceType) {
			final boolean oldValue = this.enableServiceType;
			this.enableServiceType = enableServiceType;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.ENABLE_SERVICE_TYPE, oldValue, enableServiceType));
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String usesPortName) {
			final String oldValue = this.serviceName;
			this.serviceName = usesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SERVICE_NAME, oldValue, usesPortName));
		}

		public String getServiceType() {
			return serviceType;
		}

		public void setServiceType(String providesPortName) {
			final String oldValue = this.serviceType;
			this.serviceType = providesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.SERVICE_TYPE, oldValue, providesPortName));
		}

		public List<String> getUsesPortNames() {
			return usesPortNames;
		}
		
		public void setUsesPortNames(List<String> usesPortNames) {
			final List<String> oldValue = this.usesPortNames;
			this.usesPortNames = usesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.USES_PORT_NAMES, oldValue, usesPortNames));
		}
		
		public List<String> getProvidesPortNames() {
			return providesPortNames;
		}
		
		public void setProvidesPortNames(List<String> providesPortNames) {
			final List<String> oldValue = this.providesPortNames;
			this.providesPortNames = providesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, Model.PROVIDES_PORT_NAMES, oldValue, providesPortNames));
		}
		
		
		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		public boolean isComplete() {
			if (this.enableServiceType && this.serviceType.length() == 0) {
				return false;
			}
			if (this.enableServiceName && this.serviceName.length() == 0) {
				return false;
			}
			return true;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;

	private Model model;
	private DataBindingContext dbc;
	private Button serviceNameBtn, serviceTypeBtn, usesPortBtn, providesPortBtn;
	private Text serviceNameText, serviceTypeText, usesPortNameText, providesPortNameText;

	public FindByServiceWizardPage() {
		super("findByService", "Find By Service", TITLE_IMAGE);
		this.setDescription("Enter Service Identification Information");

		model = new Model();
		dbc = new DataBindingContext();
	}

	@Override 
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		// service name checkbox
		serviceNameBtn = new Button(composite, SWT.RADIO);
		serviceNameBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameBtn.setText("Service Name");
		serviceNameBtn.setSelection(model.getEnableServiceName());
		dbc.bindValue(WidgetProperties.selection().observe(serviceNameBtn), BeansObservables.observeValue(model, Model.ENABLE_SERVICE_NAME));

		// service name
		final Label serviceNameLabel = new Label(composite, SWT.NONE);
		serviceNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameLabel.setText("Service Name:");

		serviceNameText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		serviceNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(serviceNameText, SWT.Modify), BeansObservables.observeValue(model, Model.SERVICE_NAME),
			new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					if (value instanceof String && serviceNameBtn.getSelection() && ((String) value).length() < 1) {
						return ValidationStatus.error("Service Name must not be empty");
					}
					if (value instanceof String && serviceNameBtn.getSelection() && ((String) value).contains(" ")) {
						return ValidationStatus.error("Service Name must not have spaces in the name");
					}
					return ValidationStatus.ok();
				}
			}), null);
		serviceNameBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceNameText.setEnabled(serviceNameBtn.getSelection());
				updateEnablePortsFields();
				dbc.updateModels();
			}
		});

		// service type checkbox
		serviceTypeBtn = new Button(composite, SWT.RADIO);
		serviceTypeBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBtn.setText("Service Type");
		serviceTypeBtn.setSelection(model.getEnableServiceType());
		dbc.bindValue(WidgetProperties.selection().observe(serviceTypeBtn), BeansObservables.observeValue(model, Model.ENABLE_SERVICE_TYPE));

		// service type
		final Label serviceTypeLabel = new Label(composite, SWT.NONE);
		serviceTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeLabel.setText("Service Type:");

		Composite serviceTypeComposite = new Composite(composite, SWT.NONE);
		serviceTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		serviceTypeComposite.setLayout(new GridLayout(2, false));

		serviceTypeText = new Text(serviceTypeComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		serviceTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceTypeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(serviceTypeText, SWT.Modify), BeansObservables.observeValue(model, Model.SERVICE_TYPE),
			new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					if (value instanceof String && serviceTypeBtn.getSelection() && ((String) value).length() < 1) {
						return ValidationStatus.error("Service Type must not be empty");
					}
					if (value instanceof String && serviceTypeBtn.getSelection() && ((String) value).contains(" ")) {
						return ValidationStatus.error("Service Type must not have spaces in the name");
					}
					return ValidationStatus.ok();
				}
			}), null);
		serviceTypeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceTypeText.setEnabled(serviceTypeBtn.getSelection());
				updateEnablePortsFields();
				dbc.updateModels();
			}
		});
		Button serviceTypeBrowseBtn = new Button(serviceTypeComposite, SWT.BUTTON1);
		serviceTypeBrowseBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBrowseBtn.setText("Browse");
		serviceTypeBrowseBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!serviceTypeBtn.getSelection()) {
					// Only enable if Service Type radio button is selected
					return;
				}
				
				IdlInterfaceDcl result = IdlInterfaceSelectionDialog.create(getShell());
				if (result != null) {
					serviceTypeText.setText(result.getRepId());
					// if the interface selected inherits from PortSupplier than allow user to
					// specify port information
					if (extendsPortSupplier(result)) {
						model.setServiceTypeSupportsPorts(true);
						updateEnablePortsFields();
					}
				}
			}
		});

		// disable text boxes when service name/type not enabled
		serviceNameText.setEnabled(model.getEnableServiceName());
		serviceTypeText.setEnabled(model.getEnableServiceType());
		
		// port group
		final Group portOptions = new Group(composite, SWT.NONE);
		portOptions.setLayout(new GridLayout(2, true));
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port Options");

		// uses port composite
		final Composite usesPortComposite = new Composite(portOptions, SWT.None);
		usesPortComposite.setLayout(new GridLayout(2, false));
		usesPortComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// add uses port name text
		usesPortNameText = new Text(usesPortComposite, SWT.BORDER);
		usesPortNameText.setLayoutData(new GridData(200, SWT.DEFAULT));
		usesPortNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String value = usesPortNameText.getText();
				if (value.contains(" ")) {
					setErrorMessage("Uses Port Name must not have spaces in the name");
				} else {
					setErrorMessage(null);
				}
			}
		}); 
		
		// add uses port button
		usesPortBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortBtn.setText("Add Uses Port");
		
		// add uses port list
		final org.eclipse.swt.widgets.List usesPortListNew = new org.eclipse.swt.widgets.List(usesPortComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		usesPortListNew.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		dbc.bindList(SWTObservables.observeItems(usesPortListNew), BeansObservables.observeList(model, Model.USES_PORT_NAMES));
		usesPortBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portName = usesPortNameText.getText();
				if (portName != null && !portName.isEmpty() && !("").equals(portName)) {
					usesPortListNew.add(portName);
					dbc.updateModels();
					usesPortNameText.setText("");
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		// provides port composite
		final Composite providesPortComposite = new Composite(portOptions, SWT.None);
		providesPortComposite.setLayout(new GridLayout(2, false));
		providesPortComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		// add provides port name text
		providesPortNameText = new Text(providesPortComposite, SWT.BORDER);
		providesPortNameText.setLayoutData(new GridData(200, SWT.DEFAULT));
		providesPortNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String value = providesPortNameText.getText();
				if (value.contains(" ")) {
					setErrorMessage("Uses Port Name must not have spaces in the name");
				} else {
					setErrorMessage(null);
				}
			}
		}); 
		
		// add provides port button
		providesPortBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortBtn.setText("Add Provides Port");
		
		// add provides port list
		final org.eclipse.swt.widgets.List providesPortListNew = new org.eclipse.swt.widgets.List(providesPortComposite, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		providesPortListNew.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		dbc.bindList(SWTObservables.observeItems(providesPortListNew), BeansObservables.observeList(model, Model.PROVIDES_PORT_NAMES));
		providesPortBtn.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portName = providesPortNameText.getText();
				if (portName != null && !portName.isEmpty() && !("").equals(portName)) {
					providesPortListNew.add(portName);
					dbc.updateModels();
					providesPortNameText.setText("");
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		
		setControl(composite);

		dbc.updateModels();
	}

	/**
	 * Return true if interface extends PortSupplier interface
	 * @param idlInterfaceDcl
	 * @return
	 */
	public boolean extendsPortSupplier(IdlInterfaceDcl idlInterfaceDcl) {
		if (idlInterfaceDcl.getInheritedInterfaces() != null) {
			for (IdlInterfaceDcl inheritedInterface : idlInterfaceDcl.getInheritedInterfaces()) {
				if (inheritedInterface.getRepId().startsWith("IDL:CF/PortSupplier") || extendsPortSupplier(inheritedInterface)) {
					return true;
				}
			}
		}
		return false;
	}

	// enable/disable port fields
	public void updateEnablePortsFields() {
		// TODO: We want to limit ports to service types that extend portSupplier
		// TODO: From Devin - What does this do?
		// if(model.getEnableServiceName() || model.getEnableServiceType() && model.isServiceTypeSupportsPorts()){
		if (model.getEnableServiceName() || model.getEnableServiceType()) {
			usesPortNameText.setEnabled(true);
			usesPortBtn.setEnabled(true);
			providesPortNameText.setEnabled(true);
			providesPortBtn.setEnabled(true);
		} else {
			usesPortNameText.setEnabled(false);
			usesPortBtn.setEnabled(false);
			providesPortNameText.setEnabled(false);
			providesPortBtn.setEnabled(false);
		}

	}

	public Model getModel() {
		return model;
	}

}
