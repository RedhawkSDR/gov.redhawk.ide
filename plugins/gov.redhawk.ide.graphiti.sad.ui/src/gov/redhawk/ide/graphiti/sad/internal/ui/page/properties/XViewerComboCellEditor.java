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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class XViewerComboCellEditor extends XViewerCellEditor {

	private CCombo combo;
	private ModifyListener modifyListener;

	public XViewerComboCellEditor(Composite parent, int style) {
		this(parent, new String[0], style);
	}

	public XViewerComboCellEditor(Composite parent, String[] items, int style) {
		super(parent);

		combo = new CCombo(this, style);
		combo.setItems(items);

		modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				editOccurred(e);
			}
		};

		combo.addModifyListener(modifyListener);

		forwardEvents(SWT.FocusOut, combo, true);
		forwardEvents(SWT.Traverse, combo, true);
		forwardEvents(SWT.Verify, combo, true);

		setLayout(new FillLayout());
	}

	@Override
	protected void doSetValue(Object value) {
		if (value != null) {
			combo.removeModifyListener(modifyListener);
			combo.setText((String) value);
			combo.addModifyListener(modifyListener);
			doValidate((String) value);
		}
	}

	@Override
	protected Object doGetValue() {
		return combo.getText();
	}

	private void editOccurred(ModifyEvent e) {
		String value = combo.getText();
		doValidate(value);
	}

	private void doValidate(String value) {
		if (!isCorrect(value)) {
			showControlDecoration(getErrorMessage());
			setValueValid(false);
		} else {
			hideControlDecoration();
			setValueValid(true);
		}
	}
}
