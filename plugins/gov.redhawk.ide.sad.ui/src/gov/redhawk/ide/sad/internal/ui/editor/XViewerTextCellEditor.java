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
package gov.redhawk.ide.sad.internal.ui.editor;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class XViewerTextCellEditor extends XViewerCellEditor {

	private Text text;
	private ControlDecoration decoration;

	public XViewerTextCellEditor(Composite parent, int style) {
		super(parent, style);

		text = new Text(this, style);
		decoration = new ControlDecoration(text, SWT.TOP | SWT.LEFT);
		decoration.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		decoration.hide();
		decoration.setShowOnlyOnFocus(true);
		decoration.setShowHover(true);

		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				editOccurred(e);
			}
		});

		setLayout(new FillLayout());
	}

	@Override
	public void dispose() {
		decoration.dispose();
		super.dispose();
	}

	@Override
	protected void doSetValue(Object value) {
		text.setText((String) value);
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

	@Override
	protected Control getMainControl() {
		return text;
	}

	protected void editOccurred(ModifyEvent e) {
		String value = text.getText();
		if (value == null) {
			value = "";//$NON-NLS-1$
		}
		if (!isCorrect(value)) {
			decoration.show();
			decoration.setDescriptionText(getErrorMessage());
		} else {
			decoration.hide();
		}
	}
}
