/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.spd.internal.ui.editor;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.eclipsecorba.idl.Identifiable;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryPackage;
import gov.redhawk.eclipsecorba.library.ui.IdlFilteredTree;
import gov.redhawk.eclipsecorba.library.ui.IdlPatternFilter;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;
import gov.redhawk.ui.editor.ScaSection;

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.scd.AbstractPort;
import mil.jpeojtrs.sca.scd.PortType;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.databinding.edit.EMFEditObservables;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.databinding.fieldassist.ControlDecorationSupport;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

// TODO: CHECKSTYLE:OFF
public class PortDetailsSection extends ScaSection {

	private static final int NUM_COLUMNS = 2;

	private class LibraryLoadAdapter extends AdapterImpl {

		@Override
		public void notifyChanged(final Notification msg) {
			super.notifyChanged(msg);
			switch (msg.getFeatureID(IdlLibrary.class)) {
			case LibraryPackage.IDL_LIBRARY__LOAD_STATUS:
				PortDetailsSection.this.getSection().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						PortDetailsSection.this.updateTreeSelection();
					}
				});
				break;
			default:
				break;
			}
		}
	}

	private final GridLayoutFactory layoutFactory = GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(false);
	private final GridDataFactory dataFactory = GridDataFactory.fillDefaults();
	private ComboViewer portViewer;
	private CheckboxTableViewer typeViewer;
	private DataBindingContext context;
	private LibraryLoadAdapter libraryLoadAdapter;

	private ComponentEditor editor;
	private IdlFilteredTree filteredTree;
	private TreeViewer idlTree;
	private Text repIdText;
	private Text nameText;
	private StyledText descriptionText;

	private Resource scdResource;
	private final Collection<Binding> bindings = new ArrayList<Binding>();
	private Composite client; // The main section composite
	private final PortsPageModel model;

	private final IValidator nameValidator = new IValidator() {
		@Override
		public IStatus validate(final Object value) {
			final String s = ((String) value).trim();
			if ((s == null) || (s.length() == 0)) {
				return ValidationStatus.error("ERROR: Port name cannot be blank");
			}
			if (isReservedName(s)) {
				return ValidationStatus.error("The name '" + s + "' is a reserved port name.");
			}

			return ValidationStatus.ok();
		}
	};

	protected boolean isReservedName(final String s) {
		for (final String reserved : Ports.RESERVED_NAMES) {
			if (s.equals(reserved)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Instantiates the port details section
	 * 
	 * @param page
	 * @param parent
	 * @param editor
	 * @param model
	 * @param style
	 */
	public PortDetailsSection(ScaFormPage page, Composite parent, SCAFormEditor editor, PortsPageModel model) {
		super(page, parent, Section.DESCRIPTION);
		this.model = model;
		this.editor = (ComponentEditor) editor;
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Port Details");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData tableData = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(tableData);
		section.setDescription("Enter port details");

		client = toolkit.createComposite(section);
		section.setClient(client);

		createPortDetailsArea(client, toolkit);

		toolkit.paintBordersFor(client);

		// Databinding stuff
		this.context = new DataBindingContext();

		// This is necessary to get the validation error message to show up immediately
		this.context.updateModels();
	}

	/**
	 * Creates the port details area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private Composite createPortDetailsArea(final Composite client, final FormToolkit toolkit) {
		client.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, NUM_COLUMNS));
		GridData data = GridDataFactory.fillDefaults().grab(true, true).create();
		client.setLayoutData(data);

		Label label;

		label = new Label(client, SWT.NULL);
		label.setText("Name*:");
		this.nameText = new Text(client, SWT.NONE);
		this.nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));
		this.nameText.setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(true, true).create());

		// TODO: HACK to force a redo the binding with the port.
		// --- For some reason the name text field was not re-binding when direction was changed
		this.nameText.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// PASS
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (PortDetailsSection.this.model.getPort() != null) {
					bind();
				}
			}
		});

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
				return ((PortDirection) element).getLabel();
			}
		});

		this.portViewer.setInput(PortDirection.values());
		this.portViewer.setSelection(new StructuredSelection(PortDirection.PROVIDES));
		this.portViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(false, true).create());

		label = new Label(client, SWT.NULL);
		label.setText("Type:");
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(false, true).create());

		this.typeViewer = CheckboxTableViewer.newCheckList(client, SWT.NONE);
		this.typeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(false, true).create());
		this.typeViewer.setContentProvider(new ArrayContentProvider());
		this.typeViewer.setLabelProvider(new LabelProvider());
		this.typeViewer.setInput(PortType.VALUES.subList(0, PortType.VALUES.size() - 1));
		addTypeViewerListener();

		final Group interfaceGroup = new Group(client, SWT.NULL);
		interfaceGroup.setLayoutData(GridDataFactory.fillDefaults().span(3, 1).grab(true, true).create());
		interfaceGroup.setLayout(this.layoutFactory.numColumns(1).create());
		interfaceGroup.setText("Select an IDL interface");

		final PatternFilter patternFilter = new IdlPatternFilter();
		patternFilter.setIncludeLeadingWildcard(false);
		this.filteredTree = new IdlFilteredTree(interfaceGroup, true, this.editor.getIdlLibrary());
		this.idlTree = this.filteredTree.getViewer();
		final GridData gd = this.dataFactory.grab(true, true).create();
		// hint to show 5 items
		gd.heightHint = this.filteredTree.getViewer().getTree().getItemHeight() * 5;
		this.idlTree.getControl().setLayoutData(gd);

		this.repIdText = new Text(interfaceGroup, SWT.BORDER);
		this.repIdText.setEnabled(false);
		this.repIdText.setLayoutData(GridDataFactory.fillDefaults().grab(false, true).create());

		label = new Label(client, SWT.NULL);
		label.setText("Description:");
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(false, true).create());

		this.descriptionText = new StyledText(client, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		GridData descriptionLayoutData = GridDataFactory.fillDefaults().span(3, 1).grab(true, true).create();
		descriptionLayoutData.heightHint = 150;
		this.descriptionText.setMargins(5, 5, 5, 0);
		this.descriptionText.setLayoutData(descriptionLayoutData);

		return client;
	}

	public Text getNameText() {
		return this.nameText;
	}

	public ComboViewer getDirectionCombo() {
		return this.portViewer;
	}

	private void bind() {
		// Binds on refresh, so we need to clear out and re-bind everything
		for (final Binding binding : this.bindings) {
			binding.getValidationStatus().setValue(ValidationStatus.ok()); // Clears any lingering control decorators
			binding.dispose();
		}

		this.bindings.clear();

		AbstractPort truePort = null;
		for (AbstractPort port : getScd().getComponentFeatures().getPorts().getAllPorts()) {
			if (port.equals(this.model.getPort())) {
				truePort = port;
			}
		}

		if (truePort == null) {
			return;
		}

		// Bind name text field to the model port name
		Binding nameModelBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.nameText),
			EMFEditObservables.observeValue(editor.getEditingDomain(), truePort, ScdPackage.Literals.ABSTRACT_PORT__NAME),
			new UpdateValueStrategy().setBeforeSetValidator(nameValidator), null);

		if (truePort.isBiDirectional()) {
			// Handle bi-directional ports by binding siblings as well
			Binding siblingBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.nameText),
				EMFEditObservables.observeValue(editor.getEditingDomain(), truePort.getSibling(), ScdPackage.Literals.ABSTRACT_PORT__NAME),
				new UpdateValueStrategy().setBeforeSetValidator(nameValidator), null);
			this.bindings.add(siblingBinding);
		}

		ControlDecorationSupport.create(nameModelBinding, SWT.TOP | SWT.LEFT);
		this.bindings.add(nameModelBinding);

		/** Bind repId text field to the model port interfaces **/
		Binding repIdModelBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.repIdText),
			EMFEditObservables.observeValue(editor.getEditingDomain(), truePort, ScdPackage.Literals.ABSTRACT_PORT__REP_ID));
		this.bindings.add(repIdModelBinding);

		if (truePort.isBiDirectional()) {
			// Handle bi-directional ports by binding siblings as well
			Binding siblingBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.repIdText),
				EMFEditObservables.observeValue(editor.getEditingDomain(), truePort.getSibling(), ScdPackage.Literals.ABSTRACT_PORT__REP_ID));
			this.bindings.add(siblingBinding);
		}

		/** Bind portDirection combo to the type of port (provides, uses, bi-directional) **/
		Binding portDirectionModelBinding = this.context.bindValue(ViewerProperties.singleSelection().observe(portViewer),
			BeansObservables.observeValue(this.model, PortsPageModel.PROP_PORT_DIRECTION));
		this.bindings.add(portDirectionModelBinding);

		// TODO: Add a validator to the portDirectionalModelBinding target-to-model strategy to check the following:
		/**
		 * if ("IDL:ExtendedEvent/MessageEvent:1.0".equals(repId) && (portType != SectionPortType.BIDIR)) {
		 * return ValidationStatus.info("It is recommended that MessageEvent ports be bidirectional");
		 * } else if (!"IDL:ExtendedEvent/MessageEvent:1.0".equals(repId) && (portType == SectionPortType.BIDIR)) {
		 * return ValidationStatus.warning("Bidirectional support for this port type is not recommended.");
		 * }
		 */

		/** Bind description text field to the model port description field **/
		Binding descriptionModelBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.descriptionText),
			EMFEditObservables.observeValue(editor.getEditingDomain(), truePort, ScdPackage.Literals.ABSTRACT_PORT__DESCRIPTION));
		this.bindings.add(descriptionModelBinding);

		if (truePort.isBiDirectional()) {
			// Handle bi-directional ports by binding siblings as well
			Binding siblingBinding = this.context.bindValue(WidgetProperties.text(SWT.Modify).observe(this.descriptionText),
				EMFEditObservables.observeValue(editor.getEditingDomain(), truePort.getSibling(), ScdPackage.Literals.ABSTRACT_PORT__DESCRIPTION));
			this.bindings.add(siblingBinding);
		}

		// Add IdlLibrary adapter
		final IdlLibrary library = this.editor.getIdlLibrary();
		if (library != null && this.libraryLoadAdapter == null) {
			this.libraryLoadAdapter = new LibraryLoadAdapter();
			ScaModelCommand.execute(library, new ScaModelCommand() {

				@Override
				public void execute() {
					library.eAdapters().add(PortDetailsSection.this.libraryLoadAdapter);
				}
			});
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
					PortDetailsSection.this.model.setRepId(theRepid);
				} else {
					PortDetailsSection.this.model.setRepId("");
				}
			}
		});
	}

	public void addTypeViewerListener() {
		this.typeViewer.addCheckStateListener(new ICheckStateListener() {
			private boolean ignore = false;

			@Override
			public void checkStateChanged(final CheckStateChangedEvent event) {
				if (this.ignore) {
					return;
				}
				for (final PortType type : PortType.values()) {
					if (PortDetailsSection.this.typeViewer.getChecked(type) && !PortDetailsSection.this.typeViewer.getGrayed(type)) {
						PortDetailsSection.this.model.getPortTypes().add(type);
					} else {
						PortDetailsSection.this.model.getPortTypes().remove(type);
					}
				}
				this.ignore = true;
				if (PortDetailsSection.this.model.getPortTypes().contains(PortType.CONTROL)) {
					if (PortDetailsSection.this.model.getPortTypes().size() == 1) {
						PortDetailsSection.this.model.getPortTypes().clear();
					}
				} else if (PortDetailsSection.this.model.getPortTypes().size() == 1) {
					PortDetailsSection.this.typeViewer.setGrayed(PortType.CONTROL, false);
					PortDetailsSection.this.typeViewer.setChecked(PortType.CONTROL, false);
				}

				if (PortDetailsSection.this.model.getPortTypes().isEmpty()) {
					selectDefaultChecks();
				}

				PortDetailsSection.this.model.updateModelPortTypes();
				this.ignore = false;
			}
		});
	}

	private SoftwareComponent getScd() {
		return (SoftwareComponent) this.scdResource.getContents().get(0);
	}

	private void selectDefaultChecks() {
		this.typeViewer.setChecked(PortType.CONTROL, true);
		this.typeViewer.setGrayed(PortType.CONTROL, true);
	}

	private void updatePortTypes() {
		if (this.model.getPortTypes().isEmpty()) {
			selectDefaultChecks();
		} else {
			typeViewer.setAllChecked(false);
			for (final PortType pt : this.model.getPortTypes()) {
				this.typeViewer.setChecked(pt, true);
			}
		}
	}

	private void updatePortDirectionCombo() {
		// TODO: temp disabled
//		portViewer.setSelection(new StructuredSelection(this.model.getPortDirection()));
	}

	private void updateTreeSelection() {
		final IdlLibrary library = this.editor.getIdlLibrary();
		if (this.model.getRepId() != null && this.model.getRepId().trim().length() > 0 && library != null) {
			final String repId = this.model.getRepId();
			final Identifiable obj = library.find(repId);
			if (obj != null) {
				this.idlTree.refresh();
				final StructuredSelection selection = new StructuredSelection(obj);
				this.idlTree.reveal(obj);
				this.idlTree.setSelection(selection, true);
			} else {
				// PASS
				// TODO: put an error message when unknown interface??
//				setErrorMessage("Unknown interface error; Cannot resolve REPID!");
			}
		}
	}

	public void resetToDefault() {
		// TODO: temp disabled
//		this.nameText.setText("");
//		this.portViewer.getCombo().select(0);
//		this.typeViewer.setAllChecked(false);
//		this.idlTree.collapseAll();
	}

	@Override
	public void refresh(final Resource resource) {
		super.refresh();
		this.scdResource = resource;

		updatePortTypes();
		updatePortDirectionCombo();

		this.bind();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.AbstractFormPart#dispose()
	 */
	@Override
	public void dispose() {
		final IdlLibrary library = this.editor.getIdlLibrary();
		if (library != null && this.libraryLoadAdapter != null) {
			ScaModelCommand.execute(library, new ScaModelCommand() {

				@Override
				public void execute() {
					library.eAdapters().remove(PortDetailsSection.this.libraryLoadAdapter);
				}
			});
		}

		this.context.dispose();
		super.dispose();
	}
}
