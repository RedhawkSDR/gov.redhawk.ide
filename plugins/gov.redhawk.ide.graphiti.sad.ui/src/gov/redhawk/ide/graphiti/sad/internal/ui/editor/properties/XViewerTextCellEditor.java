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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class XViewerTextCellEditor extends XViewerCellEditor {

	private Text text;
	private ModifyListener modifyListener;

	public XViewerTextCellEditor(Composite parent, int style) {
		super(parent);

		text = new Text(this, style);

		modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				editOccurred(e);
			}
		};

		text.addModifyListener(modifyListener);

		forwardEvents(SWT.FocusOut, text, true);
		forwardEvents(SWT.Traverse, text, true);
		forwardEvents(SWT.Verify, text, true);

		setLayout(new FillLayout());
	}

	@Override
	protected void doSetValue(Object value) {
		if (value != null) {
			text.removeModifyListener(modifyListener);
			text.setText((String) value);
			text.addModifyListener(modifyListener);
		}
	}

	@Override
	protected Object doGetValue() {
		return text.getText();
	}

	@Override
	public boolean setFocus() {
		text.setFocus();
		text.setSelection(0, text.getText().length());
		return true;
	}

	protected void editOccurred(ModifyEvent e) {
		String value = text.getText();
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		if (!isCorrect(value)) {
			showControlDecoration(getErrorMessage());
			setValueValid(false);
		} else {
			hideControlDecoration();
			setValueValid(true);
		}
	}
}
