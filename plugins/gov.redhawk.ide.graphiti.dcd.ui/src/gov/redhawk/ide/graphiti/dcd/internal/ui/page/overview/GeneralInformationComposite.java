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
package gov.redhawk.ide.graphiti.dcd.internal.ui.page.overview;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.IScaComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;

/**
 * 
 */
public class GeneralInformationComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private FormEntry descriptionEntry;
	private FormEntry idEntry;
	private FormEntry nameEntry;

	/**
	 * @param parent
	 * @param style
	 * @param toolkit
	 * @param actionBars
	 */
	public GeneralInformationComposite(final Composite parent, final int style, final FormToolkit toolkit, final IActionBars actionBars) {
		super(parent, style);
		this.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, GeneralInformationComposite.NUM_COLUMNS));

		createIDEntry(this, toolkit, actionBars);

		createNameEntry(this, toolkit, actionBars);

		createDescriptionEntry(this, toolkit, actionBars);
	}

	/**
	 * Creates the description area.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createDescriptionEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.descriptionEntry = new FormEntry(client, toolkit, "Description:", SWT.MULTI | SWT.WRAP);
		final Object data = this.descriptionEntry.getText().getLayoutData();
		if (data instanceof GridData) {
			((GridData) this.descriptionEntry.getLabel().getLayoutData()).verticalAlignment = SWT.TOP;
			final GridData gData = (GridData) data;
			gData.verticalAlignment = SWT.FILL;
			gData.grabExcessVerticalSpace = true;
			gData.heightHint = 75; // SUPPRESS CHECKSTYLE MagicNumber
		} else if (data instanceof TableWrapData) {
			((TableWrapData) this.descriptionEntry.getLabel().getLayoutData()).valign = SWT.TOP;
			final TableWrapData tData = (TableWrapData) data;
			tData.valign = SWT.FILL;
			tData.grabVertical = true;
			tData.heightHint = 75; // SUPPRESS CHECKSTYLE MagicNumber
		}
	}

	/**
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createIDEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.idEntry = new FormEntry(client, toolkit, "ID:", "Generate", false);

	}

	/**
	 * Creates the name entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createNameEntry(final Composite client, final FormToolkit toolkit, final IActionBars actionBars) {
		this.nameEntry = new FormEntry(client, toolkit, "Name:", null, false);
	}

	/**
	 * @return the descriptionEntry
	 */
	public FormEntry getDescriptionEntry() {
		return this.descriptionEntry;
	}

	/**
	 * @return the idEntry
	 */
	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	/**
	 * @return the nameEntry
	 */
	public FormEntry getNameEntry() {
		return this.nameEntry;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(final boolean editable) {
		this.descriptionEntry.setEditable(editable);
		this.idEntry.setEditable(editable);
		this.nameEntry.setEditable(editable);
	}

}
