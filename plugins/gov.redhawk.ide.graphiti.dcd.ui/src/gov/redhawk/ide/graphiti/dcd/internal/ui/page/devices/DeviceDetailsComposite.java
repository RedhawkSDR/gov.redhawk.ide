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
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;

public class DeviceDetailsComposite extends DcdComponentComposite {

	private ComboViewer parentViewer;
	private Button unsetParentButton;

	public DeviceDetailsComposite(Composite parent, int style, FormToolkit toolkit, IEditingDomainProvider editingDomainProvider) {
		super(parent, style, toolkit, editingDomainProvider);
	}

	@Override
	protected void createCompositeSections(boolean showProps) {
		createNameEntry();

		createParentEntry();

		createPropertiesArea();
	}

	@Override
	protected void createNameEntry() {
		super.createNameEntry();
		getNameEntry().getText().setToolTipText("Human readable name for the device instantiation");
	}

	protected void createParentEntry() {
		final Label label = this.toolkit.createLabel(this, "Parent:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		parentViewer = new ComboViewer(this, SWT.SINGLE | SWT.READ_ONLY | SWT.DROP_DOWN);
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

	public ComboViewer getParentViewer() {
		return this.parentViewer;
	}

	public Button getUnsetParentButton() {
		return this.unsetParentButton;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		super.setEditable(canEdit);
		this.parentViewer.getCombo().setEnabled(canEdit);
		this.unsetParentButton.setEnabled(canEdit);
	}
}
