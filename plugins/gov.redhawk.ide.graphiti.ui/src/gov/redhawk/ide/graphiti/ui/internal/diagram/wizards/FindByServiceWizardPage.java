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
package gov.redhawk.ide.graphiti.ui.internal.diagram.wizards;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.swt.SWT;
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
import gov.redhawk.ide.graphiti.ui.diagram.wizards.AbstractPortWizardPage;

public class FindByServiceWizardPage extends AbstractPortWizardPage {

	/**
	 * Used as the model for UI input.
	 */
	public class ServiceModel extends AbstractPortWizardPage.AbstractPortModel {

		public static final String ENABLE_SERVICE_NAME = "enableServiceName"; //$NON-NLS-1$
		public static final String ENABLE_SERVICE_TYPE = "enableServiceType"; //$NON-NLS-1$
		public static final String SERVICE_NAME = "serviceName"; //$NON-NLS-1$
		public static final String SERVICE_TYPE = "serviceType"; //$NON-NLS-1$

		private boolean enableServiceName = true;
		private boolean enableServiceType;
		private String serviceName;
		private String serviceType;

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
			getPropChangeSupport().firePropertyChange(new PropertyChangeEvent(this, ServiceModel.ENABLE_SERVICE_NAME, oldValue, enableServiceName));
		}

		public boolean setEnableServiceType() {
			return enableServiceType;
		}

		public void setEnableServiceType(boolean enableServiceType) {
			final boolean oldValue = this.enableServiceType;
			this.enableServiceType = enableServiceType;
			getPropChangeSupport().firePropertyChange(new PropertyChangeEvent(this, ServiceModel.ENABLE_SERVICE_TYPE, oldValue, enableServiceType));
		}

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String usesPortName) {
			final String oldValue = this.serviceName;
			this.serviceName = usesPortName;
			getPropChangeSupport().firePropertyChange(new PropertyChangeEvent(this, ServiceModel.SERVICE_NAME, oldValue, usesPortName));
		}

		public String getServiceType() {
			return serviceType;
		}

		public void setServiceType(String providesPortName) {
			final String oldValue = this.serviceType;
			this.serviceType = providesPortName;
			getPropChangeSupport().firePropertyChange(new PropertyChangeEvent(this, ServiceModel.SERVICE_TYPE, oldValue, providesPortName));
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

	@SuppressWarnings("unchecked")
	@Override
	protected void createTopSection(Composite parent) {
		// ### Section for manually entering the service name ###//
		serviceNameBtn = new Button(parent, SWT.RADIO);
		serviceNameBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameBtn.setText("Service Name");
		serviceNameBtn.setSelection(model.getEnableServiceName());
		Binding binding = getDbc().bindValue(WidgetProperties.selection().observe(serviceNameBtn),
			BeanProperties.value(model.getClass(), ServiceModel.ENABLE_SERVICE_NAME).observe(model));
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);

		final Label serviceNameLabel = new Label(parent, SWT.NONE);
		serviceNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceNameLabel.setText("Service Name:");

		serviceNameText = new Text(parent, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		serviceNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceNameText.setToolTipText("The name of a service in the domain");
		binding = getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(serviceNameText),
			BeanProperties.value(model.getClass(), ServiceModel.SERVICE_NAME).observe(model), new UpdateValueStrategy().setAfterGetValidator(value -> {
				String err = validService("Service Name", (String) value, serviceNameBtn);
				if (err != null) {
					return ValidationStatus.error(err);
				}
				return ValidationStatus.ok();
			}), null);
		ControlDecorationSupport.create(binding, SWT.TOP | SWT.LEFT);
		serviceNameBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				serviceNameText.setEnabled(serviceNameBtn.getSelection());
			}
		});

		// ### Section for browsing for the service type ###//
		serviceTypeBtn = new Button(parent, SWT.RADIO);
		serviceTypeBtn.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeBtn.setText("Service Type");
		serviceTypeBtn.setSelection(model.getEnableServiceType());
		getDbc().bindValue(WidgetProperties.selection().observe(serviceTypeBtn),
			BeanProperties.value(model.getClass(), ServiceModel.ENABLE_SERVICE_TYPE).observe(model));

		final Label serviceTypeLabel = new Label(parent, SWT.NONE);
		serviceTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		serviceTypeLabel.setText("Service Type:");

		Composite serviceTypeComposite = new Composite(parent, SWT.NONE);
		serviceTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		serviceTypeComposite.setLayout(new GridLayout(2, false));

		serviceTypeText = new Text(serviceTypeComposite, SWT.SINGLE | SWT.LEAD | SWT.BORDER | SWT.READ_ONLY);
		serviceTypeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		serviceTypeText.setToolTipText("The interface (repid) of a service in the domain");
		getDbc().bindValue(WidgetProperties.text(SWT.Modify).observe(serviceTypeText),
			BeanProperties.value(model.getClass(), ServiceModel.SERVICE_TYPE).observe(model), new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					String err = validService("Service Type", (String) value, serviceTypeBtn);
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
