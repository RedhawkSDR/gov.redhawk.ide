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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage.CORBANameModel;

public abstract class AbstractFindByWizardPage extends WizardPage {

	private DataBindingContext dbc;
	private Button usesPortAddBtn, usesPortDeleteBtn, providesPortAddBtn, providesPortDeleteBtn;
	private Text usesPortNameText, providesPortNameText;
	private List usesPortList, providesPortList;
	private Composite dialogComposite;

	protected AbstractFindByWizardPage(String pageName, String title) {
		super(pageName, title, null);
		dbc = new DataBindingContext();
	}

	protected abstract void createNameSection();

	protected abstract Object getModel();

	protected String validateAll() {
		String err;
		if (usesPortNameText != null) {
			err = validText("Port", usesPortNameText);
			if (err != null) {
				return err;
			}
		}
		if (providesPortNameText != null) {
			err = validText("Port", providesPortNameText);
			if (err != null) {
				return err;
			}
		}
		return null;
	}

	@Override
	public void createControl(Composite parent) {
		// Create main page composite
		WizardPageSupport.create(this, getDbc());
		this.dialogComposite = new Composite(parent, SWT.NONE);
		dialogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dialogComposite.setLayout(new GridLayout(1, false));

		createNameSection();
		createPortsSection();

		setControl(dialogComposite);
		getDbc().updateModels();
	}

	private void createPortsSection() {
		// port group
		final Group portOptions = new Group(dialogComposite, SWT.NONE);
		portOptions.setLayout(new GridLayout(2, true));
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port(s) to use for connections");

		// provides port composite
		final Composite providesPortComposite = createPortComposite(portOptions);
		// add provides port name text
		providesPortNameText = addPortNameText(providesPortComposite);
		providesPortNameText.setToolTipText("The specified provides port on the component will be located to make connections");
		// add provides port "Add" button
		providesPortAddBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortAddBtn.setText("Add Provides Port");
		// add provides port list
		providesPortList = addPortList(providesPortComposite, CORBANameModel.PROVIDES_PORT_NAMES);
		// add provides port "Delete" button
		providesPortDeleteBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortDeleteBtn.setText("Delete");
		providesPortDeleteBtn.setEnabled(false);
		// add provides port listeners
		providesPortList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (providesPortList.getSelectionCount() > 0) {
					providesPortDeleteBtn.setEnabled(true);
				} else {
					providesPortDeleteBtn.setEnabled(false);
				}
			}
		});
		providesPortAddBtn.addSelectionListener(getPortAddListener(providesPortList, providesPortNameText, providesPortDeleteBtn));
		providesPortDeleteBtn.addSelectionListener(getPortDeleteListener(providesPortList, providesPortDeleteBtn));

		// uses port composite
		final Composite usesPortComposite = createPortComposite(portOptions);
		// add uses port name text
		usesPortNameText = addPortNameText(usesPortComposite);
		usesPortNameText.setToolTipText("The specified uses port on the component will be located to make connections");
		// add uses port "Add" button
		usesPortAddBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortAddBtn.setText("Add Uses Port");
		// add uses port list
		usesPortList = addPortList(usesPortComposite, CORBANameModel.USES_PORT_NAMES);
		// add uses port "Delete" button
		usesPortDeleteBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortDeleteBtn.setText("Delete");
		usesPortDeleteBtn.setEnabled(false);
		// add uses port listeners
		usesPortList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (usesPortList.getSelectionCount() > 0) {
					usesPortDeleteBtn.setEnabled(true);
				} else {
					usesPortDeleteBtn.setEnabled(false);
				}
			}
		});
		usesPortAddBtn.addSelectionListener(getPortAddListener(usesPortList, usesPortNameText, usesPortDeleteBtn));
		usesPortDeleteBtn.addSelectionListener(getPortDeleteListener(usesPortList, usesPortDeleteBtn));
	}

	private Composite createPortComposite(Composite portOptions) {
		final Composite portComposite = new Composite(portOptions, SWT.None);
		portComposite.setLayout(new GridLayout(2, false));
		portComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return portComposite;
	}

	private Text addPortNameText(Composite portComposite) {
		final Text portNameText = new Text(portComposite, SWT.BORDER);
		GridData layoutData = new GridData(SWT.FILL, SWT.DEFAULT, true, true, 1, 1);
		layoutData.minimumWidth = 200;
		portNameText.setLayoutData(layoutData);

		portNameText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String err = validText("Port", portNameText);
				if (err != null) {
					setErrorMessage(err);
				} else {
					setErrorMessage(validateAll());
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

		Object model = getModel();
		getDbc().bindList(WidgetProperties.items().observe(portList), BeanProperties.list(model.getClass(), propertyName).observe(model));
		return portList;
	}

	private SelectionListener getPortAddListener(final List portList, final Text portNameText, final Button deleteBtn) {
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portName = portNameText.getText();
				if (portName.contains(" ")) {
					return;
				}
				if (portName != null && !portName.isEmpty() && !("").equals(portName)) {
					portList.add(portName);
					portNameText.setText("");
					getDbc().updateModels();
				}
			}
		};
		return listener;
	}

	private SelectionListener getPortDeleteListener(final List portList, final Button deleteBtn) {
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String[] selections = portList.getSelection();
				if (selections != null) {
					for (String selection : selections) {
						portList.remove(selection);
					}
					getDbc().updateModels();
				}
				if (portList.getItemCount() <= 0) {
					deleteBtn.setEnabled(false);
				}
			}
		};
		return listener;
	}

	/**
	 * If returns null, that means the value is valid/has no spaces.
	 * @param valueType
	 * @param value
	 * @return
	 */
	public String validText(String valueType, Text valueText) {
		if (valueText != null && valueText.getText().contains(" ")) {
			return valueType + " must not include spaces";
		}
		return null;
	}

	public String validText(String valueType, String value) {
		if (value.contains(" ")) {
			return valueType + " must not include spaces";
		}
		return null;
	}

	// Getters
	public Composite getDialogComposite() {
		return dialogComposite;
	}

	public DataBindingContext getDbc() {
		return dbc;
	}

}
