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
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.ui.IdlFilter;
import gov.redhawk.eclipsecorba.library.ui.IdlInterfaceSelectionDialog;

public class FindByServiceWizardPage extends AbstractFindByWizardPage {

	// inner class model used to store user selections
	public static class ServiceModel {

		public static final String ENABLE_SERVICE_NAME = "enableServiceName";
		public static final String ENABLE_SERVICE_TYPE = "enableServiceType";
		public static final String SERVICE_NAME = "serviceName";
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

		public ServiceModel() {
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
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.ENABLE_SERVICE_NAME, oldValue, enableServiceName));
		}

		public boolean setEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceType(boolean enableServiceType) {
			final boolean oldValue = this.enableServiceType;
			this.enableServiceType = enableServiceType;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.ENABLE_SERVICE_TYPE, oldValue, enableServiceType));
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String usesPortName) {
			final String oldValue = this.serviceName;
			this.serviceName = usesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.SERVICE_NAME, oldValue, usesPortName));
		}

		public String getServiceType() {
			return serviceType;
		}

		public void setServiceType(String providesPortName) {
			final String oldValue = this.serviceType;
			this.serviceType = providesPortName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.SERVICE_TYPE, oldValue, providesPortName));
		}

		public List<String> getUsesPortNames() {
			return usesPortNames;
		}

		public void setUsesPortNames(List<String> usesPortNames) {
			final List<String> oldValue = this.usesPortNames;
			this.usesPortNames = usesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.USES_PORT_NAMES, oldValue, usesPortNames));
		}

		public List<String> getProvidesPortNames() {
			return providesPortNames;
		}

		public void setProvidesPortNames(List<String> providesPortNames) {
			final List<String> oldValue = this.providesPortNames;
			this.providesPortNames = providesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, ServiceModel.PROVIDES_PORT_NAMES, oldValue, providesPortNames));
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

	private ServiceModel model;
	private Button serviceNameBtn, serviceTypeBtn;
	private Text serviceNameText, serviceTypeText;

	public FindByServiceWizardPage() {
		super("findByService", "Find By Service");
		this.setDescription("Enter the details of a service you want to make connections to");

		model = new ServiceModel();
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

	@Override
	protected void createNameSection() {
		Composite dialogComposite = getDialogComposite();

		// ### Section for manually entering the service name ###//
		serviceNameBtn = new Button(dialogComposite, SWT.RADIO);
		serviceNameBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameBtn.setText("Service Name");
		serviceNameBtn.setSelection(model.getEnableServiceName());
		getDbc().bindValue(WidgetProperties.selection().observe(serviceNameBtn),
			BeanProperties.value(model.getClass(), ServiceModel.ENABLE_SERVICE_NAME).observe(model));

		final Label serviceNameLabel = new Label(dialogComposite, SWT.NONE);
		serviceNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameLabel.setText("Service Name:");

		serviceNameText = new Text(dialogComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		serviceNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceNameText.setToolTipText("The name of a service in the domain");
		serviceNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getDbc().updateModels();
			}
		});
		getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(serviceNameText),
			BeanProperties.value(model.getClass(), ServiceModel.SERVICE_NAME).observe(model), new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					String err = validService("Service Name", (String) value, serviceNameBtn);
					if (err != null) {
						return ValidationStatus.error(err);
					}
					err = validateAll();
					if (err != null) {
						return ValidationStatus.error(err);
					}
					return ValidationStatus.ok();
				}
			}), null);
		serviceNameBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceNameText.setEnabled(serviceNameBtn.getSelection());
				getDbc().updateModels();
			}
		});

		// ### Section for browsing for the service type ###//
		serviceTypeBtn = new Button(dialogComposite, SWT.RADIO);
		serviceTypeBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBtn.setText("Service Type");
		serviceTypeBtn.setSelection(model.getEnableServiceType());
		getDbc().bindValue(WidgetProperties.selection().observe(serviceTypeBtn),
			BeanProperties.value(model.getClass(), ServiceModel.ENABLE_SERVICE_TYPE).observe(model));

		final Label serviceTypeLabel = new Label(dialogComposite, SWT.NONE);
		serviceTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeLabel.setText("Service Type:");

		Composite serviceTypeComposite = new Composite(dialogComposite, SWT.NONE);
		serviceTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		serviceTypeComposite.setLayout(new GridLayout(2, false));

		serviceTypeText = new Text(serviceTypeComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		serviceTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceTypeText.setToolTipText("The interface (repid) of a service in the domain");
		serviceTypeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getDbc().updateModels();
			}
		});
		getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(serviceTypeText),
			BeanProperties.value(model.getClass(), ServiceModel.SERVICE_TYPE).observe(model), new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					String err = validService("Service Type", (String) value, serviceTypeBtn);
					if (err != null) {
						return ValidationStatus.error(err);
					}
					err = validateAll();
					if (err != null) {
						return ValidationStatus.error(err);
					}
					return ValidationStatus.ok();
				}
			}), null);

		final Button serviceTypeBrowseBtn = new Button(serviceTypeComposite, SWT.BUTTON1);
		serviceTypeBrowseBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBrowseBtn.setText("Browse");
		serviceTypeBrowseBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IdlInterfaceDcl result = IdlInterfaceSelectionDialog.open(getShell(), IdlFilter.ALL_WITH_MODULE);
				if (result != null) {
					serviceTypeText.setText(result.getRepId());
				}
			}
		});

		serviceTypeBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceTypeBrowseBtn.setEnabled(serviceTypeBtn.getSelection());
				serviceTypeText.setEnabled(serviceTypeBtn.getSelection());
				getDbc().updateModels();
			}
		});

		// disable text boxes when service name/type not enabled
		serviceNameText.setEnabled(model.getEnableServiceName());
		serviceTypeText.setEnabled(model.getEnableServiceType());
		serviceTypeBrowseBtn.setEnabled(model.getEnableServiceType());
	}

	public ServiceModel getModel() {
		return model;
	}

	protected String validateAll() {
		String err = validService("Service Name", serviceNameText, serviceNameBtn);
		if (err != null) {
			return err;
		}
		err = validService("Service Type", serviceTypeText, serviceTypeBtn);
		if (err != null) {
			return err;
		}

		return super.validateAll();
	}

	// Validate service name fields
	private String validService(String valueType, Text valueText, Button btn) {
		if (btn != null && btn.getSelection()) {
			if (valueText == null || valueText.getText().length() < 1) {
				return valueType + " must not be empty";
			}
			return validText(valueType, valueText);
		}
		return null;
	}

	private String validService(String valueType, String value, Button btn) {
		if (btn != null && btn.getSelection()) {
			if (value == null || value.length() < 1) {
				return valueType + " must not be empty";
			}
			return validText(valueType, value);
		}
		return null;
	}

}
