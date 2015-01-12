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

import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.ide.graphiti.sad.ui.images.SadWizardImages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class PortsWizardPage extends WizardPage {

	// inner class model used to store user selections
	public static class Model {
		
		public static final String USES_PORT_NAMES = "usesPortNames";
		public static final String PROVIDES_PORT_NAMES = "providesPortNames";

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

		private List<String> usesPortNames = new ArrayList<String>();
		private List<String> providesPortNames = new ArrayList<String>();


		public Model() {
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
			return true;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;

	private Model model;
	private DataBindingContext dbc;
	private Button usesPortAddBtn, usesPortDeleteBtn, providesPortAddBtn, providesPortDeleteBtn;
	private Text usesPortNameText, providesPortNameText;
	private Image addImage;
	private Image removeImage;
	private org.eclipse.swt.widgets.List providesPortList;
	private org.eclipse.swt.widgets.List usesPortList;

	public PortsWizardPage() {
		super("portWizardPage", "Identify Ports", TITLE_IMAGE);
		this.setDescription("Enter uses and provides ports");

		model = new Model();
		dbc = new DataBindingContext();
		addImage = SadWizardImages.ADD.createImage();
		removeImage = SadWizardImages.REMOVE.createImage();
	}
	
	public PortsWizardPage(List<String> providesPortNames, List<String> usesPortNames) {
		this();
		getModel().setUsesPortNames(usesPortNames);
		getModel().setProvidesPortNames(providesPortNames);
	}

	@Override
	public void createControl(Composite parent) {
		WizardPageSupport.create(this, dbc);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new GridLayout(1, false));

		// port group
		final Group portOptions = new Group(composite, SWT.NONE);
		portOptions.setLayout(new GridLayout(2, true));
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port(s) to use for connections");

		// provides port composite
		final Composite providesPortComposite = createPortComposite(portOptions);
		Label providesPortsLabel = new Label(providesPortComposite, SWT.NONE);
		providesPortsLabel.setText("Provides Ports");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(providesPortsLabel);
		// add provides port name text
		providesPortNameText = addPortNameText(providesPortComposite, providesPortList);
		providesPortNameText.setToolTipText("The specified provides port on the component will be located to make connections");
		// add provides port "Add" button
		providesPortAddBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortAddBtn.setImage(addImage);
		//providesPortAddBtn.setText("Add Provides Port");
		// add provides port list
		providesPortList = addPortList(providesPortComposite, Model.PROVIDES_PORT_NAMES);
		// add provides port "Delete" button
		providesPortDeleteBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortDeleteBtn.setImage(removeImage);
		//providesPortDeleteBtn.setText("Delete");
		if (providesPortList.getItemCount() <= 0) {
			providesPortDeleteBtn.setEnabled(false);
		}
		// add provides port listeners
		providesPortAddBtn.addSelectionListener(getPortAddListener(providesPortList, providesPortNameText, providesPortDeleteBtn));
		providesPortDeleteBtn.addSelectionListener(getPortDeleteListener(providesPortList, providesPortDeleteBtn));

		// uses port composite
		final Composite usesPortComposite = createPortComposite(portOptions);
		Label usesPortsLabel = new Label(usesPortComposite, SWT.NONE);
		usesPortsLabel.setText("Uses Ports");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(usesPortsLabel);
		// add uses port name text
		usesPortNameText = addPortNameText(usesPortComposite, usesPortList);
		usesPortNameText.setToolTipText("The specified uses port on the component will be located to make connections");
		// add uses port "Add" button
		usesPortAddBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortAddBtn.setImage(addImage);
		//usesPortAddBtn.setText("Add Uses Port");
		// add uses port list
		usesPortList = addPortList(usesPortComposite, Model.USES_PORT_NAMES);
		// add uses port "Delete" button
		usesPortDeleteBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortDeleteBtn.setImage(removeImage);
		//usesPortDeleteBtn.setText("Delete");
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

	private Text addPortNameText(Composite portComposite, final org.eclipse.swt.widgets.List portList) {
		final Text portNameText = new Text(portComposite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, true, 1, 1);
		layoutData.minimumWidth = 200;
		portNameText.setLayoutData(layoutData);

		portNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				setErrorMessage(validateAll());
			}
		});
		return portNameText;
	}

	private String validateAll() {
		String err = null;
		if (usesPortNameText != null) {
			err = validPortNameText("Uses port", usesPortList, usesPortNameText);
			if (err != null) {
				return err;
			}
		}
		if (providesPortNameText != null) {
			err = validPortNameText("Provides port", providesPortList, providesPortNameText);
			if (err != null) {
				return err;
			}
		}
		return null;
	}

	/**
	 * If returns null, that means the value is valid/has no spaces.
	 * Verifies string is unique
	 * @param valueType
	 * @param value
	 * @return
	 */
	public static String validPortNameText(String valueType, final org.eclipse.swt.widgets.List portList, Text valueText) {
		if (valueText != null && valueText.getText().contains(" ")) {
			return valueType + " must not include spaces";
		}
		
		//verify unique
		if (portList != null) {
			for (String portName: portList.getItems()) {
				if (portName.equals(valueText.getText())) {
					return valueType + " must be unique within the port list";
				}
			}
		}
		
		return null;
	}
	
	public static String validPortName(String valueType, String value) {
		if (value.contains(" ")) {
			return valueType + " must not include spaces";
		}
		return null;
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
				String err = validPortNameText("Port", portList, providesPortNameText);
				if (err != null) {
					return;
				}
				String portName = portNameText.getText();
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
					dbc.updateModels();
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


	public Model getModel() {
		return model;
	}
	
	@Override
	public void dispose() {
		removeImage.dispose();
		addImage.dispose();
		super.dispose();
	}
}
