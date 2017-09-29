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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.overview;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.ui.editor.ScaFormPage;
import gov.redhawk.ui.editor.ScaSection;
import mil.jpeojtrs.sca.partitioning.Requires;
import mil.jpeojtrs.sca.sad.Option;
import mil.jpeojtrs.sca.sad.Options;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

public class WaveformOptionsSection extends ScaSection {

	private Resource sadResource;
	private Composite client;
	private WaveformOptionsComposite tableComposite;
	private Button addButton;
	private Button removeButton;

	private SelectionAdapter addOptionSelectionAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Option newOption = SadFactory.eINSTANCE.createOption();
			newOption.setName("Option Name");
			newOption.setValue("Option Value");

			EditingDomain editingDomain = getPage().getEditingDomain();

			Command command = null;
			if (getSoftwareAssembly().getOptions() == null) {
				Options options = SadFactory.eINSTANCE.createOptions();
				options.getOption().add(newOption);
				command = SetCommand.create(editingDomain, getSoftwareAssembly(), SadPackage.Literals.SOFTWARE_ASSEMBLY__OPTIONS, options);
			} else {
				command = AddCommand.create(editingDomain, getSoftwareAssembly().getOptions(), SadPackage.Literals.OPTIONS__OPTION, newOption);
			}

			editingDomain.getCommandStack().execute(command);
			getTableViewer().refresh();

			// Automatically select the most recently added item
			int size = getTable().getItemCount();
			if (size > 0) {
				getTable().setSelection(getTable().getItem(size - 1));
			}

			updateControls();
		}
	};

	private SelectionAdapter removeOptionSelectionAdapter = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {

			EditingDomain editingDomain = getPage().getEditingDomain();
			Option option = (Option) ((StructuredSelection) getTableViewer().getSelection()).getFirstElement();
			Command command = null;
			if (getSoftwareAssembly().getOptions() != null && getSoftwareAssembly().getOptions().getOption().size() == 1) {
				command = SetCommand.create(editingDomain, getSoftwareAssembly(), SadPackage.Literals.SOFTWARE_ASSEMBLY__OPTIONS, null);
			} else {
				command = RemoveCommand.create(editingDomain, getSoftwareAssembly().getOptions(), SadPackage.Literals.OPTIONS__OPTION, option);
			}

			// Table only allows a single selection, so safe to assume the item we want is at index[0]
			int selIndex = getTable().indexOf(getTable().getSelection()[0]);

			editingDomain.getCommandStack().execute(command);
			getTableViewer().refresh();

			// Update the selection to be the preceding item if it exists, otherwise select the top item
			if (selIndex > 0) {
				TableItem item = getTable().getItem(selIndex - 1);
				getTable().setSelection(item);
			} else if (getTable().getItems().length > 0) {
				TableItem item = getTable().getItem(0);
				getTable().setSelection(item);
			}

			updateControls();
		};
	};

	public WaveformOptionsSection(ScaFormPage page, Composite parent) {
		super(page, parent, ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		createClient(getSection(), page.getEditor().getToolkit());
	}

	@Override
	protected void createClient(Section section, FormToolkit toolkit) {
		section.setText("Waveform Options");
		section.setLayout(FormLayoutFactory.createClearTableWrapLayout(false, 1));
		final TableWrapData data = new TableWrapData(TableWrapData.FILL_GRAB);
		section.setLayoutData(data);

		this.client = toolkit.createComposite(section);
		client.setLayout(new GridLayout(2, false));
		client.setLayoutData(GridDataFactory.fillDefaults().create());

		tableComposite = createTableComposite(client, toolkit);
		createButtonComposite(client);

		section.setClient(this.client);

		toolkit.adapt(this.client);
		toolkit.paintBordersFor(this.client);
	}

	/**
	 * Create the composite that will contain the table displaying the {@link Option} elements
	 */
	private WaveformOptionsComposite createTableComposite(Composite parent, FormToolkit toolkit) {
		WaveformOptionsComposite composite = new WaveformOptionsComposite(parent, SWT.None, toolkit);
		composite.getTableViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateControls();
			}
		});
		return composite;
	}

	/**
	 * Create the composite that will contain buttons for adding/removing {@link Requires} elements
	 */
	private void createButtonComposite(Composite parent) {
		Composite actionComposite = new Composite(parent, SWT.NONE);
		actionComposite.setLayout(new GridLayout());
		actionComposite.setLayoutData(GridDataFactory.swtDefaults().align(SWT.CENTER, SWT.BEGINNING).create());
		actionComposite.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

		addButton = new Button(actionComposite, SWT.PUSH);
		addButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		addButton.setText("Add");
		addButton.setToolTipText("Add Option");

		removeButton = new Button(actionComposite, SWT.PUSH);
		removeButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		removeButton.setText("Remove");
		removeButton.setToolTipText("Remove Option");
		removeButton.setEnabled(false);

		// Add button selection listeners
		addButton.addSelectionListener(addOptionSelectionAdapter);
		removeButton.addSelectionListener(removeOptionSelectionAdapter);
	}

	/**
	 * Currently just controls whether or not the remove button is enabled.
	 * Can be expanded in the future as necessary.
	 */
	private void updateControls() {
		int numItems = getTable().getItems().length;
		if (numItems == 0 || getTableViewer().getSelection().isEmpty()) {
			removeButton.setEnabled(false);
		} else if (!removeButton.isEnabled()) {
			removeButton.setEnabled(true);
		}
	}

	@Override
	public void refresh(Resource resource) {
		this.sadResource = resource;
		SoftwareAssembly sad = getSoftwareAssembly();

		// Make sure input is set when the overview tab first opens and when edits are made in the XML editor.
		if (sad != null && sad.getOptions() != null) {
			getTableViewer().setInput(sad.getOptions());
		}

		sad.eAdapters().add(optionsListener);
	}

	private TableViewer getTableViewer() {
		return tableComposite.getTableViewer();
	}

	// Need a somewhat complicated listener to account for the fact that a new SoftwareAssembly object is created
	// every time the resource is reloaded
	private EContentAdapter optionsListener = new EContentAdapter() {
		@Override
		public void notifyChanged(Notification notification) {
			super.notifyChanged(notification);

			if (notification.getNotifier() instanceof SoftwareAssembly) {
				switch (notification.getFeatureID(SoftwareAssembly.class)) {
				case SadPackage.SOFTWARE_ASSEMBLY__OPTIONS:
					getTableViewer().setInput(notification.getNewValue());
					break;
				default:
					break;
				}
			}
		}
	};

	@Override
	public void dispose() {
		if (getSoftwareAssembly() != null) {
			getSoftwareAssembly().eAdapters().remove(optionsListener);
		}
		super.dispose();
	}

	private Table getTable() {
		return getTableViewer().getTable();
	}

	private SoftwareAssembly getSoftwareAssembly() {
		return SoftwareAssembly.Util.getSoftwareAssembly(this.sadResource);
	}
}
