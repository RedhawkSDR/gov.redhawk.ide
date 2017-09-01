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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.devices;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.core.graphiti.ui.adapters.ComponentInstantiationPropertyViewerAdapter;
import gov.redhawk.sca.ui.ScaComponentFactory;
import gov.redhawk.sca.ui.properties.ScaPropertiesAdapterFactory;
import gov.redhawk.ui.editor.IScaComposite;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;

public class DcdComponentComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private FormEntry nameEntry;
	private ComboViewer parentViewer;
	private Button unsetParentButton;

	private ScaPropertiesAdapterFactory adapterFactory = new ScaPropertiesAdapterFactory();
	private final IEditingDomainProvider editingDomainProvider;

	private final ComponentInstantiationPropertyViewerAdapter adapter;

	private TreeViewer propertiesViewer;

	public DcdComponentComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider) {
		super(parent, style);

		this.toolkit = toolkit;
		this.editingDomainProvider = editingDomainProvider;
		this.adapter = new ComponentInstantiationPropertyViewerAdapter(this.editingDomainProvider);

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, DcdComponentComposite.NUM_COLUMNS));

		createNameEntry();

		createParentEntry();

		createPropertiesArea();

		toolkit.paintBordersFor(this);
	}

	private void createNameEntry() {
		this.nameEntry = new FormEntry(this, this.toolkit, "Name:", SWT.SINGLE);
		this.nameEntry.getText().setToolTipText("Human readable name for the device instantiation");
	}

	@Override
	public void dispose() {
		this.adapter.dispose();
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
		super.dispose();
	}

	private void createParentEntry() {
		final Label label = this.toolkit.createLabel(this, "Parent:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		this.parentViewer = new ComboViewer(this, SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
		this.parentViewer.getCombo().addListener(SWT.MouseVerticalWheel, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// Disable Mouse Wheel Combo Box Control
				event.doit = false;
			}

		});

		this.parentViewer.setContentProvider(new ArrayContentProvider());
		this.parentViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final DcdComponentPlacement desc = (DcdComponentPlacement) element;
				return (desc != null) ? desc.getComponentInstantiation().get(0).getUsageName() : "";
			}
		});
		this.parentViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(true, false).create());
		this.unsetParentButton = this.toolkit.createButton(this, "Unset", SWT.PUSH);
		this.unsetParentButton.setLayoutData(GridDataFactory.fillDefaults().span(1, 1).grab(false, false).create());
		this.unsetParentButton.setEnabled(false);
	}

	private void createPropertiesArea() {
		final Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setText("Properties");
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 300).create());
		group.setLayout(new FillLayout());
		this.propertiesViewer = ScaComponentFactory.createPropertyTable(group, SWT.SINGLE | SWT.BORDER, this.adapterFactory);
		this.adapter.setViewer(this.propertiesViewer);
	}

	public Button getUnsetParentButton() {
		return this.unsetParentButton;
	}

	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	public ComboViewer getParentViewer() {
		return this.parentViewer;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		this.nameEntry.setEditable(canEdit);
		this.parentViewer.getCombo().setEnabled(canEdit);
		this.unsetParentButton.setEnabled(canEdit);
		// Don't set enabled on properties viewer since this will disable scrolling
//		this.propertiesViewer.getTree().setEnabled(canEdit);
	}

	public void setInput(final DcdComponentInstantiation instantiation) {
		this.adapter.setInput(instantiation);
	}
}
