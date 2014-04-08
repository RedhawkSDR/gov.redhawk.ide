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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import gov.redhawk.eclipsecorba.idl.Identifiable;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryPackage;
import gov.redhawk.eclipsecorba.library.ui.IdlFilteredTree;
import gov.redhawk.eclipsecorba.library.ui.IdlPatternFilter;
import gov.redhawk.sca.util.PluginUtil;
import gov.redhawk.sca.util.PropertyChangeSupport;
import gov.redhawk.sca.util.StringUtil;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.PortTypeContainer;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.Uses;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * The Class PortWizardPage.
 */
public class PortWizardPage extends WizardPage {

	public static enum WizardPortType {
		PROVIDES("in <provides>"),
		USES("out <uses>"),
		BIDIR("bidir <uses/provides>");

		private String label;

		private WizardPortType(final String label) {
			this.label = label;
		}

		public String getLabel() {
			return this.label;
		}
	}

	private class LibraryLoadAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(final Notification msg) {
			super.notifyChanged(msg);
			switch (msg.getFeatureID(IdlLibrary.class)) {
			case LibraryPackage.IDL_LIBRARY__LOAD_STATUS:
				PortWizardPage.this.getShell().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						PortWizardPage.this.updateTreeSelection();
					}
				});
				break;
			default:
				break;
			}
		}

	}

	public static class PortWizardModel {
		public static final String PROP_TYPE = "type";
		public static final String PROP_NAME = "portName";
		public static final String PROP_REP_ID = "repId";
		public static final String PROP_PORT_TYPES = "portTypes";
		private static final Pattern REPID_PATTERN = Pattern.compile("IDL:(\\w*/)*(\\w*):\\d.\\d");

		private AbstractPort port = null;

		private WizardPortType type = WizardPortType.PROVIDES;

		private String portName = null;

		private String repId = null;

		private final Set<PortType> portTypes = new HashSet<PortType>();

		private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
		private String defaultName;

		private final Set<String> portNames = new HashSet<String>();
		private Ports ports;
		private String originalName;

		public PortWizardModel() {

		}

		public PortWizardModel(final AbstractPort port) {
			this();
			this.setPort(port);
		}

		public void setPorts(final Ports ports) {
			this.ports = ports;
			if (ports != null) {
				for (final FeatureMap.Entry entry : ports.getGroup()) {
					if (entry.getValue() instanceof AbstractPort) {
						this.portNames.add(((AbstractPort) entry.getValue()).getName());
					}
				}
				if (this.originalName != null) {
					this.portNames.remove(this.originalName);
				}
			} else {
				this.portNames.clear();
			}
		}

		public void addPropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.addPropertyChangeListener(listener);
		}

		public void removePropertyChangeListener(final PropertyChangeListener listener) {
			this.pcs.removePropertyChangeListener(listener);
		}

		private void setDefaultName(final String defaultName) {
			if (PluginUtil.equals(this.defaultName, this.portName) || "".equals(this.portName)) {
				setPortName(defaultName);
			}
			this.defaultName = defaultName;
		}

		public void setPort(final AbstractPort port) {
			this.port = port;
			if (port.isBiDirectional()) {
				setType(WizardPortType.BIDIR);
			} else {
				if (port instanceof Uses) {
					setType(WizardPortType.USES);
				} else {
					setType(WizardPortType.PROVIDES);
				}
			}
			setRepId((this.port.getRepID()));
			this.originalName = port.getName();
			if (this.originalName != null) {
				this.portNames.remove(this.originalName);
			}
			setPortName(this.port.getName());

			for (final PortTypeContainer ptc : this.port.getPortType()) {
				this.portTypes.add(ptc.getType());
			}
		}

		public String getPortName() {
			return this.portName;
		}

		public AbstractPort getPort() {
			return this.port;
		}

		public void setPortName(final String portName) {
			final String oldValue = this.portName;
			this.portName = portName;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortWizardModel.PROP_NAME, oldValue, portName));
		}

		public String getRepId() {
			return this.repId;
		}

		public void setRepId(final String repId) {
			final String oldValue = this.repId;
			this.repId = repId;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortWizardModel.PROP_REP_ID, oldValue, repId));

			if (repId != null) {
				final Matcher matcher = PortWizardModel.REPID_PATTERN.matcher(repId);
				if (matcher.matches()) {
					final int groupCount = matcher.groupCount();
					final String idlName = matcher.group(groupCount);
					final String newDefault = StringUtil.defaultCreateUniqueString(idlName, this.portNames);
					setDefaultName(newDefault);
				}
			}
		}

		public Set<PortType> getPortTypes() {
			return this.portTypes;
		}

		public void setPortTypes(final Set<PortType> newTypes) {
			this.portTypes.clear();
			this.portTypes.addAll(newTypes);
			if (this.portTypes.isEmpty()) {
				this.portTypes.add(PortType.CONTROL);
			}
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortWizardModel.PROP_PORT_TYPES, null, this.portTypes));
		}

		public WizardPortType getType() {
			return this.type;
		}

		public void setType(final WizardPortType type) {
			final WizardPortType oldValue = this.type;
			this.type = type;
			this.pcs.firePropertyChange(new PropertyChangeEvent(this, PortWizardModel.PROP_REP_ID, oldValue, type));
		}

		public boolean isComplete() {
			boolean complete = true;
			complete &= (this.portName != null) && (this.portName.length() != 0);
			complete &= (this.repId != null) && (this.repId.length() != 0);
			complete &= (this.type != null);
			return complete;
		}
	};

	private static final ImageDescriptor TITLE_IMAGE = null;
	private static final String NEW_DIALOG_PROMPT = "Add a port to a component by providing a name and interface";
	private static final String EDIT_DIALOG_PROMPT = "Change the port name, type, and interface";
	private final GridLayoutFactory layoutFactory = GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false);
	private final GridDataFactory dataFactory = GridDataFactory.fillDefaults();
	private ComboViewer portViewer;
	private CheckboxTableViewer typeViewer;
	private DataBindingContext context;
	private LibraryLoadAdapter libraryLoadAdapter;

	private IdlFilteredTree filteredTree;
	private TreeViewer idlTree;
	private Text repid;
	private Text name;

	private final PortWizardModel model = new PortWizardModel();

	private final IValidator nameValidator = new IValidator() {
		@Override
		public IStatus validate(final Object value) {
			final String s = ((String) value).trim();
			if ((s == null) || (s.length() == 0)) {
				return ValidationStatus.ok();
			}
			if (isReservedName(s)) {
				return ValidationStatus.error("The name '" + s + "' is a reserved port name.");
			}

			return ValidationStatus.ok();
		}
	};

	private final IValidator repIDValidator = new IValidator() {
		@Override
		public IStatus validate(final Object value) {
			final String s = (String) value;
			if ((s == null) || (s.trim().length() == 0)) {
				return ValidationStatus.ok();
			}

			return ValidationStatus.ok();
		}
	};

	private final SCAFormEditor editor;

	/**
	 * The Constructor.
	 */
	protected PortWizardPage(final Ports ports, final SCAFormEditor editor) {
		super("portPage", "Add Port", PortWizardPage.TITLE_IMAGE);
		this.setDescription(PortWizardPage.NEW_DIALOG_PROMPT);
		this.model.setPorts(ports);
		this.editor = editor;
	}

	/**
	 * The Edit Constructor.
	 */
	protected PortWizardPage(final AbstractPort port, final Ports ports, final SCAFormEditor editor) {
		super("portPage", "Edit Port", PortWizardPage.TITLE_IMAGE);
		this.setTitle("Edit Port");
		this.setDescription(PortWizardPage.EDIT_DIALOG_PROMPT);
		this.model.setPort(port);
		this.model.setPorts(ports);
		this.editor = editor;
	}

	protected boolean isReservedName(final String s) {
		for (final String reserved : Ports.RESERVED_NAMES) {
			if (s.equals(reserved)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createControl(final Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);
		client.setLayout(this.layoutFactory.create());

		final Composite portComposite = createPortEntry(client);
		portComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.setControl(client);

		this.context = new DataBindingContext();
		WizardPageSupport.create(this, this.context);

		this.bind();

		// This is necessary to get the validation error message to show up
		// immediately
		this.context.updateModels();

	}

	private void bind() {
		final IObservableValue nameObservable = new WritableValue(null, new String());
		this.context.bindValue(SWTObservables.observeText(this.name, SWT.Modify), nameObservable,
			new UpdateValueStrategy().setAfterConvertValidator(this.nameValidator), null);

		final IObservableValue repIdObservable = new WritableValue(null, new String());
		this.context.bindValue(SWTObservables.observeText(this.repid, SWT.Modify), repIdObservable,
			new UpdateValueStrategy().setAfterConvertValidator(this.repIDValidator), null);

		final IObservableValue portDirectionObservable = new WritableValue(null, WizardPortType.USES);
		this.context.bindValue(ViewersObservables.observeSingleSelection(this.portViewer), portDirectionObservable, new UpdateValueStrategy(), null);

		final MultiValidator constraintValidation = new MultiValidator() {

			@Override
			protected IStatus validate() {
				final String portName = (String) nameObservable.getValue();
				final String repId = (String) repIdObservable.getValue();
				final WizardPortType portType = (WizardPortType) portDirectionObservable.getValue();

				if ((portName == null) || (portName.trim().length() == 0)) {
					return ValidationStatus.ok();
				}

				if (PortWizardPage.this.model.ports != null) {
					for (final FeatureMap.Entry entry : PortWizardPage.this.model.ports.getGroup()) {
						final AbstractPort existingPort = (AbstractPort) entry.getValue();
						if (existingPort != PortWizardPage.this.model.getPort()
							&& (existingPort.getSibling() == null || existingPort.getSibling() != PortWizardPage.this.model.getPort())
							&& portName.equals(existingPort.getName())) {
							return ValidationStatus.error("A port with the name \"" + portName + "\" is already defined.");
						}
					}
				}

				if ((repId == null) || (repId.trim().length() == 0)) {
					return ValidationStatus.ok();
				}

				if ("IDL:ExtendedEvent/MessageEvent:1.0".equals(repId) && (portType != WizardPortType.BIDIR)) {
					return ValidationStatus.info("It is recommended that MessageEvent ports be bidirectional");
				} else if (!"IDL:ExtendedEvent/MessageEvent:1.0".equals(repId) && (portType == WizardPortType.BIDIR)) {
					return ValidationStatus.warning("Bidirectional support for this port type is not recommended.");
				}

				return ValidationStatus.ok();
			}
		};
		this.context.addValidationStatusProvider(constraintValidation);
		this.context.bindValue(constraintValidation.observeValidatedValue(repIdObservable),
			BeansObservables.observeValue(this.model, PortWizardModel.PROP_REP_ID));
		this.context.bindValue(constraintValidation.observeValidatedValue(portDirectionObservable),
			BeansObservables.observeValue(this.model, PortWizardModel.PROP_TYPE));
		this.context.bindValue(constraintValidation.observeValidatedValue(nameObservable), BeansObservables.observeValue(this.model, PortWizardModel.PROP_NAME));

		if (this.model.portTypes.isEmpty()) {
			selectDefaultChecks();
		} else {
			for (final PortType pt : this.model.portTypes) {
				this.typeViewer.setChecked(pt, true);
			}
		}

		this.typeViewer.addCheckStateListener(new ICheckStateListener() {
			private boolean ignore = false;

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (this.ignore) {
					return;
				}
				for (final PortType type : PortType.values()) {
					if (PortWizardPage.this.typeViewer.getChecked(type) && !PortWizardPage.this.typeViewer.getGrayed(type)) {
						PortWizardPage.this.model.portTypes.add(type);
					} else {
						PortWizardPage.this.model.portTypes.remove(type);
					}
				}
				this.ignore = true;
				if (PortWizardPage.this.model.portTypes.contains(PortType.CONTROL)) {
					if (PortWizardPage.this.model.portTypes.size() == 1) {
						PortWizardPage.this.model.portTypes.clear();
					}
				} else if (PortWizardPage.this.model.portTypes.size() == 1) {
					PortWizardPage.this.typeViewer.setGrayed(PortType.CONTROL, false);
					PortWizardPage.this.typeViewer.setChecked(PortType.CONTROL, false);
				}

				if (PortWizardPage.this.model.portTypes.isEmpty()) {
					selectDefaultChecks();
				}
				this.ignore = false;
			}
		});

		// deal with interfaces
		final IdlLibrary library = this.editor.getIdlLibrary();
		if (library != null && this.libraryLoadAdapter == null) {
			this.libraryLoadAdapter = new LibraryLoadAdapter();
			library.eAdapters().add(this.libraryLoadAdapter);
		}

		updateTreeSelection();

		this.idlTree.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				final Object element = selection.getFirstElement();

				if (element instanceof IdlInterfaceDcl) {
					final IdlInterfaceDcl si = (IdlInterfaceDcl) element;
					final String theRepid = si.getRepId();
					PortWizardPage.this.model.setRepId(theRepid);
				} else {
					PortWizardPage.this.model.setRepId("");
				}
			}
		});
	}

	private void selectDefaultChecks() {
		this.typeViewer.setChecked(PortType.CONTROL, true);
		this.typeViewer.setGrayed(PortType.CONTROL, true);
	}

	@Override
	public void setPageComplete(final boolean complete) {
		super.setPageComplete(complete && this.model.isComplete());
	}

	/**
	 * 
	 */
	private void updateTreeSelection() {
		final IdlLibrary library = this.getIdlLibrary();
		if (this.model.getRepId() != null && this.model.getRepId().trim().length() > 0 && library != null) {
			final String repId = this.model.getRepId();
			final Identifiable obj = library.find(repId);
			if (obj != null) {
				this.idlTree.refresh();
				final StructuredSelection selection = new StructuredSelection(obj);
				this.idlTree.reveal(obj);
				this.idlTree.setSelection(selection, true);
			} else {
				setErrorMessage("Unknown interface error; Cannot resolve REPID!");
				setPageComplete(false);
			}
		}
	}

	/**
	 * Creates the port entry.
	 * 
	 * @param parent
	 * the parent Composite
	 */
	private Composite createPortEntry(final Composite parent) {
		final Composite client = new Composite(parent, SWT.NULL);
		final GridLayout layout = new GridLayout(3, false);

		client.setLayout(layout);

		Label label;

		label = new Label(client, SWT.NULL);
		label.setText("Name:");
		this.name = new Text(client, SWT.BORDER);
		this.name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		this.name.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());

		label = new Label(client, SWT.NULL);
		label.setText("Direction:");

		this.portViewer = new ComboViewer(client);
		this.portViewer.setContentProvider(new ArrayContentProvider());
		this.portViewer.setLabelProvider(new LabelProvider() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public String getText(final Object element) {
				return ((WizardPortType) element).getLabel();
			}
		});

		this.portViewer.setInput(WizardPortType.values());
		this.portViewer.setSelection(new StructuredSelection(WizardPortType.PROVIDES));
		this.portViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(false, true).create());

		// Fill last column
		label = new Label(client, SWT.NULL);
		label.setText("");

		label = new Label(client, SWT.NULL);
		label.setText("Type:");
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(false, true).create());

		this.typeViewer = CheckboxTableViewer.newCheckList(client, SWT.BORDER);
		this.typeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(false, true).create());
		this.typeViewer.setContentProvider(new ArrayContentProvider());
		this.typeViewer.setLabelProvider(new LabelProvider());

		this.typeViewer.setInput(PortType.VALUES.subList(0, PortType.VALUES.size() - 1));

		final Composite interfaceComposite = new Composite(client, SWT.NULL);
		interfaceComposite.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, true).create());
		interfaceComposite.setLayout(this.layoutFactory.numColumns(1).create());

		label = new Label(interfaceComposite, SWT.NULL);
		label.setText("Select an IDL interface");
		label.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());

		final PatternFilter patternFilter = new IdlPatternFilter();
		patternFilter.setIncludeLeadingWildcard(false);
		this.filteredTree = new IdlFilteredTree(interfaceComposite, true, this.editor.getIdlLibrary());
		this.idlTree = this.filteredTree.getViewer();
		final GridData gd = this.dataFactory.grab(true, true).create();
		// hint to show 5 items
		gd.heightHint = this.filteredTree.getViewer().getTree().getItemHeight() * 5;
		this.idlTree.getControl().setLayoutData(gd);

		this.repid = new Text(interfaceComposite, SWT.BORDER);
		this.repid.setEnabled(false);
		this.repid.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());

		return client;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public PortWizardModel getValue() {
		return this.model;
	}

	/**
	 * Obtains the {@link IdlLibrary} from the {@link SCAFormEditor}.
	 * 
	 * @return the formEditor's idlLibrary
	 */
	public IdlLibrary getIdlLibrary() {
		return this.editor.getIdlLibrary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		final IdlLibrary library = this.getIdlLibrary();
		if (library != null && this.libraryLoadAdapter != null) {
			library.eAdapters().remove(this.libraryLoadAdapter);
		}

		this.context.dispose();
		super.dispose();
	}
}
