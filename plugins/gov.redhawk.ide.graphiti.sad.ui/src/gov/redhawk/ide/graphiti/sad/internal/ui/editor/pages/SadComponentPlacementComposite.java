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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.pages;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.IScaComposite;

public class SadComponentPlacementComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private FormEntry idEntry;
	private FormEntry nameEntry;

	public SadComponentPlacementComposite(final Composite parent, final int style, final FormToolkit toolkit,
		final IEditingDomainProvider editingDomainProvider) {
		super(parent, style);

		this.toolkit = toolkit;
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, SadComponentPlacementComposite.NUM_COLUMNS));

		this.idEntry = new FormEntry(this, this.toolkit, "Component ID:", SWT.SINGLE);

		this.nameEntry = new FormEntry(this, this.toolkit, "Usage Name:", SWT.SINGLE);
		this.nameEntry.getText().setToolTipText("Human readable name for the component instantiation");

		toolkit.paintBordersFor(this);
	}

	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		this.idEntry.setEditable(canEdit);
		this.nameEntry.setEditable(canEdit);
	}
}
