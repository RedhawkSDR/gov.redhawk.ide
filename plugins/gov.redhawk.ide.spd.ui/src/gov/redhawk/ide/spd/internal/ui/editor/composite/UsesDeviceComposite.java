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
package gov.redhawk.ide.spd.internal.ui.editor.composite;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ui.editor.IScaComposite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class UsesDeviceComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private FormEntry idEntry;
	private final FormToolkit toolkit;
	private FormEntry typeEntry;

	/**
	 * @param parent
	 * @param style
	 */
	public UsesDeviceComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, UsesDeviceComposite.NUM_COLUMNS));

		createIDEntry();
		createTypeEntry();
		this.toolkit.paintBordersFor(this);
	}

	/**
	 * Creates the type entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createTypeEntry() {
		this.typeEntry = new FormEntry(this, this.toolkit, "Type:", SWT.SINGLE);
	}

	/**
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createIDEntry() {
		this.idEntry = new FormEntry(this, this.toolkit, "ID:", SWT.SINGLE, "Generate", false);
	}

	/**
	 * @return the idEntry
	 */
	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	/**
	 * @return the typeEntry
	 */
	public FormEntry getTypeEntry() {
		return this.typeEntry;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setEditable(boolean canEdit) {
	    this.idEntry.setEditable(canEdit);
	    this.typeEntry.setEditable(canEdit);
    }

}
