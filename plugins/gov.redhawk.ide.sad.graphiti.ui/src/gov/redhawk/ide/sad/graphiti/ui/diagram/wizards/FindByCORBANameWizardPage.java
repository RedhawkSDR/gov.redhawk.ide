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

import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.FindByCORBANamePattern;

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
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class FindByCORBANameWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class CORBANameModel {

		public static final String CORBA_NAME = "corbaName";
		public static final String USES_PORT_NAMES = "usesPortNames";
		public static final String PROVIDES_PORT_NAMES = "providesPortNames";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private String corbaName;
		private List<String> usesPortNames = new ArrayList<String>();
		private List<String> providesPortNames = new ArrayList<String>();

		public CORBANameModel() {
		}

		public String getCorbaName() {
			return corbaName;
		}

		public void setCorbaName(String corbaName) {
			final String oldValue = this.corbaName;
			this.corbaName = corbaName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.CORBA_NAME, oldValue, corbaName));
		}

		public List<String> getUsesPortNames() {
			return usesPortNames;
		}

		public void setUsesPortNames(List<String> usesPortNames) {
			final List<String> oldValue = this.usesPortNames;
			this.usesPortNames = usesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.USES_PORT_NAMES, oldValue, usesPortNames));
		}

		public List<String> getProvidesPortNames() {
			return providesPortNames;
		}

		public void setProvidesPortNames(List<String> providesPortNames) {
			final List<String> oldValue = this.providesPortNames;
			this.providesPortNames = providesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, CORBANameModel.PROVIDES_PORT_NAMES, oldValue, providesPortNames));
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		public boolean isComplete() {
			if (this.corbaName.length() == 0) {
				return false;
			}
			return true;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;

	private CORBANameModel model;
	private DataBindingContext dbc;

	private Button usesPortAddBtn, usesPortDeleteBtn, providesPortAddBtn, providesPortDeleteBtn;
	private Text usesPortNameText, providesPortNameText, corbaNameText;

	public FindByCORBANameWizardPage() {
		super("findByCorbaName", "Find By CORBA Name", TITLE_IMAGE);
		this.setDescription("Enter CORBA Name and port information");

		model = new CORBANameModel();
		dbc = new DataBindingContext();
	}

	@Override
	public void createControl(Composite parent) {

		WizardPageSupport.create(this, dbc);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		// CORBA Name
		Label corbaNameLabel = new Label(composite, SWT.NONE);
		corbaNameLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		corbaNameLabel.setText("CORBA Name:");

		corbaNameText = new Text(composite, SWT.SINGLE | SWT.LEAD | SWT.BORDER);
		corbaNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		corbaNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				dbc.updateModels();
			}
		});
		dbc.bindValue(SWTObservables.observeText(corbaNameText, SWT.Modify), 
			BeansObservables.observeValue(model, CORBANameModel.CORBA_NAME),
			new UpdateValueStrategy().setAfterGetValidator(new IValidator() {
				@Override
				public IStatus validate(Object value) {
					String err = FindByCORBANamePattern.validate("CORBA", (String) value);
					if (err != null) {
						return ValidationStatus.error(err);
					} else if (validateText() != null) {
						return ValidationStatus.error(validateText());
					}
					return ValidationStatus.ok();
				}
			}), null);

		// port group
		final Group portOptions = new Group(composite, SWT.NONE);
		portOptions.setLayout(new GridLayout(2, true));
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port Options");

		// provides port composite
		final Composite providesPortComposite = createPortComposite(portOptions);
		// add provides port name text
		providesPortNameText = addPortNameText(providesPortComposite);
		// add provides port "Add" button
		providesPortAddBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortAddBtn.setText("Add Provides Port");
		// add provides port list
		final org.eclipse.swt.widgets.List providesPortList = addPortList(providesPortComposite, CORBANameModel.PROVIDES_PORT_NAMES);
		// add provides port "Delete" button
		providesPortDeleteBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortDeleteBtn.setText("Delete");
		if (providesPortList.getItemCount() <= 0) {
			providesPortDeleteBtn.setEnabled(false);
		}
		// add provides port listeners
		providesPortAddBtn.addSelectionListener(getPortAddListener(providesPortList, providesPortNameText, providesPortDeleteBtn));
		providesPortDeleteBtn.addSelectionListener(getPortDeleteListener(providesPortList, providesPortDeleteBtn));

		// uses port composite
		final Composite usesPortComposite = createPortComposite(portOptions);
		// add uses port name text
		usesPortNameText = addPortNameText(usesPortComposite);
		// add uses port "Add" button
		usesPortAddBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortAddBtn.setText("Add Uses Port");
		// add uses port list
		final org.eclipse.swt.widgets.List usesPortList = addPortList(usesPortComposite, CORBANameModel.USES_PORT_NAMES);
		// add uses port "Delete" button
		usesPortDeleteBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortDeleteBtn.setText("Delete");
		if (usesPortList.getItemCount() <= 0) {
			usesPortDeleteBtn.setEnabled(false);
		}
		// add uses port listeners
		usesPortAddBtn.addSelectionListener(getPortAddListener(usesPortList, usesPortNameText, usesPortDeleteBtn));
		usesPortDeleteBtn.addSelectionListener(getPortDeleteListener(usesPortList, usesPortDeleteBtn));

		setControl(composite);

		dbc.updateModels();

	}

	private Composite createPortComposite(Composite portOptions) {
		final Composite composite = new Composite(portOptions, SWT.None);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return composite;
	}

	private Text addPortNameText(Composite portComposite) {
		final Text portNameText = new Text(portComposite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, true, 1, 1);
		layoutData.minimumWidth = 200;
		portNameText.setLayoutData(layoutData);

		portNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (portNameText.getText().contains(" ")) {
					setErrorMessage("Port name must not include spaces");
				} else {
					setErrorMessage(validateText());
				}
			}
		});
		return portNameText;
	}

	private org.eclipse.swt.widgets.List addPortList(Composite portComposite, String propertyName) {
		org.eclipse.swt.widgets.List portList = new org.eclipse.swt.widgets.List(portComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData listLayout = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		listLayout.heightHint = 80;
		portList.setLayoutData(listLayout);
		dbc.bindList(SWTObservables.observeItems(portList), BeansObservables.observeList(model, propertyName));
		return portList;
	}

	private SelectionListener getPortAddListener(final org.eclipse.swt.widgets.List portList, final Text portNameText, final Button deleteBtn) {
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portName = portNameText.getText();
				if (portName.contains(" ")) {
					return;
				}
				if (portName != null && !portName.isEmpty() && !("").equals(portName)) {
					portList.add(portName);
					portNameText.setText("");
					deleteBtn.setEnabled(true);
					dbc.updateModels();
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
		return listener;
	}

	private SelectionListener getPortDeleteListener(final org.eclipse.swt.widgets.List portList, final Button deleteBtn) {
		SelectionListener listener = new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selections = portList.getSelection();
				if (selections != null) {
					for (String selection : selections) {
						portList.remove(selection);
					}
				}
				if (portList.getItemCount() <= 0) {
					deleteBtn.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};
		return listener;
	}
	
	private String validateText() {
		String errCORBA = FindByCORBANamePattern.validate("CORBA", corbaNameText.getText());
		if (errCORBA != null) {
			return errCORBA;
		} else if (usesPortNameText.getText().contains(" ") 
				|| providesPortNameText.getText().contains(" ")) {
			return "Port name must not include spaces";
		}
		return null;
	}

	public CORBANameModel getModel() {
		return model;
	}

}
