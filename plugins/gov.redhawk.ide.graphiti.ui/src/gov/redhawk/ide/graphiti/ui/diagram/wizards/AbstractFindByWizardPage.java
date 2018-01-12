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
package gov.redhawk.ide.graphiti.ui.diagram.wizards;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Text;

public abstract class AbstractFindByWizardPage extends WizardPage {

	private static final String PROVIDES_ICON = "icons/full/obj16/Provides.gif"; //$NON-NLS-1$
	private static final String USES_ICON = "icons/full/obj16/Uses.gif"; //$NON-NLS-1$

	private DataBindingContext dbc;
	private Button usesPortAddBtn, usesPortDeleteBtn, providesPortAddBtn, providesPortDeleteBtn;
	private Text usesPortNameText, providesPortNameText;
	private TableViewer usesPortList, providesPortList;
	private Composite dialogComposite;

	/**
	 * Used as the model for UI input.
	 */
	protected abstract class FindByModel {

		public static final String USES_PORT_NAMES = "usesPortNames"; //$NON-NLS-1$
		public static final String PROVIDES_PORT_NAMES = "providesPortNames"; //$NON-NLS-1$

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		private List<String> usesPortNames = new ArrayList<>();
		private List<String> providesPortNames = new ArrayList<>();

		public FindByModel() {
		}

		protected PropertyChangeSupport getPropChangeSupport() {
			return pcs;
		}

		public List<String> getUsesPortNames() {
			return usesPortNames;
		}

		public void setUsesPortNames(List<String> usesPortNames) {
			final List<String> oldValue = this.usesPortNames;
			this.usesPortNames = usesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, USES_PORT_NAMES, oldValue, usesPortNames));
		}

		public List<String> getProvidesPortNames() {
			return providesPortNames;
		}

		public void setProvidesPortNames(List<String> providesPortNames) {
			final List<String> oldValue = this.providesPortNames;
			this.providesPortNames = providesPortNames;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, PROVIDES_PORT_NAMES, oldValue, providesPortNames));
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}
	}

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
		providesPortList = addPortList(providesPortComposite, FindByModel.PROVIDES_PORT_NAMES, PROVIDES_ICON);
		// add provides port "Delete" button
		providesPortDeleteBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortDeleteBtn.setText("Delete");
		providesPortDeleteBtn.setEnabled(false);
		// add provides port listeners
		providesPortList.addSelectionChangedListener(event -> {
			providesPortDeleteBtn.setEnabled(!providesPortList.getStructuredSelection().isEmpty());
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
		usesPortList = addPortList(usesPortComposite, FindByModel.USES_PORT_NAMES, USES_ICON);
		// add uses port "Delete" button
		usesPortDeleteBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortDeleteBtn.setText("Delete");
		usesPortDeleteBtn.setEnabled(false);
		// add uses port listeners
		usesPortList.addSelectionChangedListener(event -> {
			usesPortDeleteBtn.setEnabled(!usesPortList.getStructuredSelection().isEmpty());
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

	private TableViewer addPortList(Composite portComposite, String propertyName, String scdEditIconPath) {
		TableViewer portList = new TableViewer(portComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		GridData listLayout = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		listLayout.heightHint = 80;
		portList.getControl().setLayoutData(listLayout);

		TableViewerColumn column = new TableViewerColumn(portList, SWT.NONE);
		column.getColumn().setWidth(100);
		column.setLabelProvider(new FindByPortLabelProvider(scdEditIconPath));

		portList.setContentProvider(new ObservableListContentProvider());
		@SuppressWarnings("unchecked")
		IObservableList< ? > input = BeanProperties.list(getModel().getClass(), propertyName).observe(getModel());
		portList.setInput(input);

		return portList;
	}

	private SelectionListener getPortAddListener(final TableViewer portList, final Text portNameText, final Button deleteBtn) {
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String portName = portNameText.getText();
				if (portName == null || portName.isEmpty() || portName.contains(" ")) { //$NON-NLS-1$
					return;
				}
				@SuppressWarnings("unchecked")
				IObservableList<String> input = (IObservableList<String>) portList.getInput();
				if (input.contains(portName)) {
					return;
				}

				input.add(portName);
				portNameText.setText(""); //$NON-NLS-1$
			}
		};
		return listener;
	}

	private SelectionListener getPortDeleteListener(final TableViewer portList, final Button deleteBtn) {
		SelectionListener listener = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				@SuppressWarnings("unchecked")
				IObservableList<String> input = (IObservableList<String>) portList.getInput();
				input.removeAll(portList.getStructuredSelection().toList());
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
		if (valueText != null && valueText.getText().contains(" ")) { //$NON-NLS-1$
			return valueType + " must not include spaces";
		}
		return null;
	}

	public String validText(String valueType, String value) {
		if (value.contains(" ")) { //$NON-NLS-1$
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
