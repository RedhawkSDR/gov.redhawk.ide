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
package gov.redhawk.ide.graphiti.sad.ui.diagram.wizards;

import java.util.List;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import mil.jpeojtrs.sca.spd.UsesDevice;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

/**
 * A wizard page for selecting which uses devices are collocated with a host collocation.
 */
public class UsesDeviceSelectionWizardPage extends WizardPage {

	private SpdItemProviderAdapterFactory adapterFactory;

	private TableViewer availableTable;
	private Button addButton;
	private Button removeButton;
	private TableViewer addedTable;

	private List<UsesDevice> usesDevices;
	private List<UsesDevice> collocatedUsesDevices;

	/**
	 * @param usesDevices The list of all uses devices in the SAD
	 * @param collocatedUsesDevices A subset of the uses devices which have been selected for collocation. This list
	 * will be adjusted by the wizard page.
	 */
	public UsesDeviceSelectionWizardPage(List<UsesDevice> usesDevices, List<UsesDevice> collocatedUsesDevices) {
		super("Uses Device selection", "Uses Device Selection", null);
		setDescription("Select devices that must be collocated with the host collocation");
		this.usesDevices = usesDevices;
		this.collocatedUsesDevices = collocatedUsesDevices;
	}

	@Override
	public void createControl(Composite parent) {
		setInitialStatus();

		initializeDialogUnits(parent);
		adapterFactory = new SpdItemProviderAdapterFactory();

		// 3 column composite
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite.setLayoutData(gridData);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		composite.setLayout(gridLayout);
		setControl(composite);

		Composite leftComposite = new Composite(composite, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = convertWidthInCharsToPixels(40);
		leftComposite.setLayoutData(gridData);

		Composite centerComposite = new Composite(composite, SWT.NONE);
		centerComposite.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));

		Composite rightComposite = new Composite(composite, SWT.NONE);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = convertWidthInCharsToPixels(40);
		rightComposite.setLayoutData(gridData);

		// Grid layouts for everybody
		GridLayoutFactory factory = GridLayoutFactory.fillDefaults();
		factory.generateLayout(leftComposite);
		factory.generateLayout(centerComposite);
		factory.generateLayout(rightComposite);

		// Add contents to each composite
		createAvailableTable(leftComposite);
		createButtons(centerComposite);
		createAddedTable(rightComposite);

		hookButtonsAndTables();
	}

	private void setInitialStatus() {
		if (usesDevices.isEmpty() && collocatedUsesDevices.isEmpty()) {
			setErrorMessage("There are no uses devices in the SAD file");
			setPageComplete(false);
		}
	}

	private void createAvailableTable(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Available uses devices:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		availableTable = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		GridData layout = new GridData(SWT.FILL, SWT.FILL, true, true);
		availableTable.getControl().setLayoutData(layout);
		availableTable.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		availableTable.setContentProvider(new ArrayContentProvider());
		availableTable.setFilters(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return !collocatedUsesDevices.contains(element);
			}
		});
		availableTable.setInput(usesDevices);
	}

	private void createButtons(Composite parent) {
		GridDataFactory factory = GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false);

		Label spacer = new Label(parent, SWT.NONE);
		factory.applyTo(spacer);

		addButton = new Button(parent, SWT.PUSH);
		factory.applyTo(addButton);
		addButton.setText("Add ->");

		removeButton = new Button(parent, SWT.PUSH);
		factory.applyTo(removeButton);
		removeButton.setText("<- Remove");
	}

	private void createAddedTable(Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Collocated uses devices:");
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		addedTable = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI);
		GridData layout = new GridData(SWT.FILL, SWT.FILL, true, true);
		addedTable.getControl().setLayoutData(layout);
		addedTable.setLabelProvider(new AdapterFactoryLabelProvider(adapterFactory));
		addedTable.setContentProvider(new ArrayContentProvider());
		addedTable.setInput(collocatedUsesDevices);
	}

	private void hookButtonsAndTables() {
		addButton.setEnabled(!availableTable.getStructuredSelection().isEmpty());
		removeButton.setEnabled(!addedTable.getStructuredSelection().isEmpty());

		addButton.addListener(SWT.Selection, event -> {
			for (Object selectedObj : availableTable.getStructuredSelection().toList()) {
				collocatedUsesDevices.add((UsesDevice) selectedObj);
			}
			availableTable.refresh();
			addedTable.refresh();
		});

		removeButton.addListener(SWT.Selection, event -> {
			for (Object selectedObj : addedTable.getStructuredSelection().toList()) {
				collocatedUsesDevices.remove(selectedObj);
			}
			availableTable.refresh();
			addedTable.refresh();
		});

		availableTable.addSelectionChangedListener(event -> {
			addButton.setEnabled(!availableTable.getStructuredSelection().isEmpty());
		});

		addedTable.addSelectionChangedListener(event -> {
			removeButton.setEnabled(!addedTable.getStructuredSelection().isEmpty());
		});
	}

	@Override
	public void dispose() {
		if (adapterFactory != null) {
			adapterFactory.dispose();
			adapterFactory = null;
		}

		super.dispose();
	}
}
