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
public class DependencyComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private FormEntry typeEntry;
	private final FormToolkit toolkit;

	/**
	 * @param parent
	 * @param style
	 */
	public DependencyComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, DependencyComposite.NUM_COLUMNS));
		this.toolkit = toolkit;

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
	 * @return the typeEntry
	 */
	public FormEntry getTypeEntry() {
		return this.typeEntry;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEditable(boolean canEdit) {
	    // TODO Auto-generated method stub
	    
    }
}
