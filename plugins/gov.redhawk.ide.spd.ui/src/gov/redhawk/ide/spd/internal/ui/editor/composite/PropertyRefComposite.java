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
import gov.redhawk.ide.spd.internal.ui.parts.PropertyElementSelectorDialog;
import gov.redhawk.ui.editor.IScaComposite;
import mil.jpeojtrs.sca.prf.Simple;

import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class PropertyRefComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private FormEntry valueEntry;
	private FormEntry idEntry;
	private final FormToolkit toolkit;

	/**
	 * @param parent
	 * @param style
	 */
	public PropertyRefComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, PropertyRefComposite.NUM_COLUMNS));

		createRefIDEntry();

		createValueEntry();
		this.toolkit.paintBordersFor(this);
	}

	/**
	 * Creates the value entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createValueEntry() {
		this.valueEntry = new FormEntry(this, this.toolkit, "Value:", SWT.SINGLE);
		this.valueEntry.getText().setToolTipText(
		        "A property value attribute used by the domain Management function to perform the dependency check");
	}

	/**
	 * @return the valueEntry
	 */
	public FormEntry getValueEntry() {
		return this.valueEntry;
	}

	/**
	 * @return the idEntry
	 */
	public FormEntry getIdEntry() {
		return this.idEntry;
	}

	/**
	 * Creates the id entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createRefIDEntry() {
		this.idEntry = new FormEntry(this, this.toolkit, "Ref ID:", "Browse", false);
		this.idEntry.getText().setToolTipText("The DCE UUID of the property being referenced.");
		this.idEntry.getButton().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(final SelectionEvent e) {

				final PropertyElementSelectorDialog dialog = new PropertyElementSelectorDialog(getShell());
				final int result = dialog.open();
				if (result == Window.OK) {
					final Simple sel = (Simple) dialog.getFirstResult();
					if (sel != null) {
						PropertyRefComposite.this.idEntry.getText().setText(sel.getId());
					}
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEditable(boolean canEdit) {
	   this.idEntry.setEditable(canEdit);
	   this.valueEntry.setEditable(canEdit);
    }

}
