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
package gov.redhawk.ide.spd.internal.ui.parts;

import gov.redhawk.ui.parts.TablePart;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class AuthorsPart extends TablePart {

	/**
	 * @param toolkit
	 * @param buttonLabels
	 */
	public AuthorsPart(final Composite parent, final FormToolkit toolkit) {
		super(new String[] { "Add", "Edit", "Remove" });
		createControl(parent, SWT.None, 2, toolkit);
	}

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// protected void createMainLabel(final Composite parent, final int span,
	// final FormToolkit toolkit) {
	// final Label label = toolkit.createLabel(parent, "Authors:");
	// label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
	// }

	public Button getAddButton() {
		return getButton(0);
	}

	public Button getEditButton() {
		return getButton(1);
	}

	public Button getRemoveButton() {
		return getButton(2);
	}

}
