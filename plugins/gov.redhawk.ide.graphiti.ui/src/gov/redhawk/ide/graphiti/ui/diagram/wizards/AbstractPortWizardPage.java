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
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
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
import org.eclipse.ui.plugin.AbstractUIPlugin;

import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;

/**
 * @since 2.0
 */
public abstract class AbstractPortWizardPage extends WizardPage {

	private static final String ADD_ICON = "icons/obj16/add.gif"; //$NON-NLS-1$
	private static final String REMOVE_ICON = "icons/obj16/remove.gif"; //$NON-NLS-1$
	private static final String PROVIDES_ICON = "icons/full/obj16/Provides.gif"; //$NON-NLS-1$
	private static final String USES_ICON = "icons/full/obj16/Uses.gif"; //$NON-NLS-1$

	private DataBindingContext dbc;
	private Button usesPortAddBtn, usesPortDeleteBtn, providesPortAddBtn, providesPortDeleteBtn;
	private Text usesPortNameText, providesPortNameText;
	private TableViewer usesPortList, providesPortList;
	private Composite dialogComposite;
	private Image addImage;
	private Image removeImage;

	/**
	 * Used as the model for UI input.
	 */
	protected abstract class AbstractPortModel {

		public static final String USES_PORT_NAMES = "usesPortNames"; //$NON-NLS-1$
		public static final String PROVIDES_PORT_NAMES = "providesPortNames"; //$NON-NLS-1$

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		private List<String> usesPortNames = new ArrayList<>();
		private List<String> providesPortNames = new ArrayList<>();

		public AbstractPortModel() {
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

	protected AbstractPortWizardPage(String pageName, String title) {
		super(pageName, title, null);
	}

	protected abstract Object getModel();

	@Override
	public void createControl(Composite parent) {
		dbc = new DataBindingContext();
		WizardPageSupport.create(this, getDbc());

		this.dialogComposite = new Composite(parent, SWT.NONE);
		dialogComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		dialogComposite.setLayout(new GridLayout(1, false));

		createTopSection(dialogComposite);
		createPortsSection(dialogComposite);

		setControl(dialogComposite);
		getDbc().updateModels();
	}

	/**
	 * Create the section at the top of the page
	 */
	protected abstract void createTopSection(Composite parent);

	private void createPortsSection(Composite parent) {
		ImageDescriptor addDesc = AbstractUIPlugin.imageDescriptorFromPlugin(GraphitiUIPlugin.PLUGIN_ID, ADD_ICON);
		addImage = addDesc.createImage(parent.getDisplay());
		ImageDescriptor removeDesc = AbstractUIPlugin.imageDescriptorFromPlugin(GraphitiUIPlugin.PLUGIN_ID, REMOVE_ICON);
		removeImage = removeDesc.createImage(parent.getDisplay());

		final Group portOptions = new Group(parent, SWT.NONE);
		portOptions.setLayout(new GridLayout(2, true));
		portOptions.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		portOptions.setText("Port(s) to use for connections");

		final Composite providesPortComposite = createPortComposite(portOptions);

		Label providesPortsLabel = new Label(providesPortComposite, SWT.NONE);
		providesPortsLabel.setText("Provides Ports");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(providesPortsLabel);

		providesPortNameText = addPortNameText(providesPortComposite);
		providesPortNameText.setToolTipText("The specified provides port on the component will be located to make connections");

		providesPortAddBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortAddBtn.setImage(addImage);
		providesPortAddBtn.setToolTipText("Add provides port");

		providesPortList = addPortList(providesPortComposite, AbstractPortModel.PROVIDES_PORT_NAMES, PROVIDES_ICON);

		providesPortDeleteBtn = new Button(providesPortComposite, SWT.PUSH);
		providesPortDeleteBtn.setImage(removeImage);
		providesPortDeleteBtn.setToolTipText("Remove provides port");
		providesPortDeleteBtn.setEnabled(false);

		providesPortList.addSelectionChangedListener(event -> {
			providesPortDeleteBtn.setEnabled(!providesPortList.getStructuredSelection().isEmpty());
		});
		providesPortAddBtn.addSelectionListener(getPortAddListener(providesPortList, providesPortNameText, providesPortDeleteBtn));
		providesPortDeleteBtn.addSelectionListener(getPortDeleteListener(providesPortList, providesPortDeleteBtn));

		final Composite usesPortComposite = createPortComposite(portOptions);

		Label usesPortsLabel = new Label(usesPortComposite, SWT.NONE);
		usesPortsLabel.setText("Uses Ports");
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(usesPortsLabel);

		usesPortNameText = addPortNameText(usesPortComposite);
		usesPortNameText.setToolTipText("The specified uses port on the component will be located to make connections");

		usesPortAddBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortAddBtn.setImage(addImage);
		usesPortAddBtn.setToolTipText("Add uses port");

		usesPortList = addPortList(usesPortComposite, AbstractPortModel.USES_PORT_NAMES, USES_ICON);

		usesPortDeleteBtn = new Button(usesPortComposite, SWT.PUSH);
		usesPortDeleteBtn.setImage(removeImage);
		usesPortDeleteBtn.setToolTipText("Remove uses port");
		usesPortDeleteBtn.setEnabled(false);

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
		final ControlDecoration controlDecoration = new ControlDecoration(portNameText, SWT.TOP | SWT.LEFT);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_WARNING);
		controlDecoration.setImage(fieldDecoration.getImage());
		controlDecoration.hide();

		portNameText.addModifyListener(event -> {
			String err = validText("Port", portNameText.getText());
			if (err != null) {
				if (getErrorMessage() == null) {
					setMessage(err, WizardPage.WARNING);
				}
				controlDecoration.setDescriptionText(err);
				controlDecoration.show();
			} else {
				getDbc().updateModels();
				controlDecoration.hide();
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
		column.setLabelProvider(new PortLabelProvider(scdEditIconPath));

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

	public String validText(String valueType, String value) {
		if (value.contains(" ")) { //$NON-NLS-1$
			return valueType + " must not include spaces";
		}
		return null;
	}

	public DataBindingContext getDbc() {
		return dbc;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (addImage != null) {
			addImage.dispose();
			addImage = null;
		}
		if (removeImage != null) {
			removeImage.dispose();
			removeImage = null;
		}
	}

}
