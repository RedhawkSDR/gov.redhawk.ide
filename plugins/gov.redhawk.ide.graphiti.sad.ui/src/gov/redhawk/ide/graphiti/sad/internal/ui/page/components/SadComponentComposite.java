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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.components;

import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.IScaComposite;

public class SadComponentComposite extends Composite implements IScaComposite {

	private final FormToolkit toolkit;
	private FormEntry nameEntry;

	public SadComponentComposite(final Composite parent, final int style, final FormToolkit toolkit, final IEditingDomainProvider editingDomainProvider) {
		super(parent, style);

		this.toolkit = toolkit;
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, 2));

		createNameEntry();

		toolkit.paintBordersFor(this);
	}

	/**
	 * Text field for component usage name (and naming service name; edits here change both)
	 */
	private void createNameEntry() {
		this.nameEntry = new FormEntry(this, this.toolkit, Messages.SadComponentComposite_UsageName, SWT.SINGLE);
		this.nameEntry.getText().setToolTipText(Messages.SadComponentComposite_UsageNameTooltip);
	}

	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	@Override
	public void setEditable(final boolean canEdit) {
		this.nameEntry.setEditable(canEdit);
	}
}
