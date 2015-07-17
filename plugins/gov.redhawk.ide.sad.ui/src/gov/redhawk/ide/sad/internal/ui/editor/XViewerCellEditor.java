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
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public abstract class XViewerCellEditor extends Composite {

	private ICellEditorValidator validator = null;
	private String errorMessage = null;
	private boolean valid = false;
	private ControlDecoration decoration = null;

	private Listener forwardingListener = new Listener() {

		@Override
		public void handleEvent(Event event) {
			notifyListeners(event.type, event);
		}

	};

	public XViewerCellEditor(Composite parent) {
		super(parent, SWT.NONE);

		validator = null;
	}

	@Override
	public void dispose() {
		if (decoration != null) {
			decoration.dispose();
		}
		super.dispose();
	}

	public void setValue(Object value) {
		doSetValue(value);
	}

	protected ControlDecoration getControlDecoration() {
		if (decoration == null) {
			decoration = new ControlDecoration(this, SWT.TOP | SWT.LEFT);
			decoration.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEC_FIELD_ERROR));
		}
		return decoration;
	}

	protected void showControlDecoration(String message) {
		getControlDecoration().setDescriptionText(message);
		getControlDecoration().show();
	}

	protected void hideControlDecoration() {
		if (decoration != null) {
			decoration.hide();
		}
	}

	protected abstract void doSetValue(Object value);

	public Object getValue() {
		if (!valid) {
			return null;
		}

		return doGetValue();
	}

	protected abstract Object doGetValue();

	protected void deactivate() {
		Event event = new Event();
		event.type = SWT.FocusOut;
		event.widget = this;
		event.display = getDisplay();
		notifyListeners(SWT.FocusOut, event);
	}

	public void setValidator(ICellEditorValidator validator) {
		this.validator = validator;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isValueValid() {
		return valid;
	}

	protected void setValueValid(boolean valid) {
		this.valid = valid;
	}

	protected boolean isCorrect(Object value) {
		errorMessage = null;
		if (validator == null) {
			return true;
		}

		errorMessage = validator.isValid(value);
		return (errorMessage == null || errorMessage.isEmpty());
	}

	protected void forwardEvents(int eventType, Control control, boolean enable) {
		if (enable) {
			control.addListener(eventType, forwardingListener);
		} else {
			control.removeListener(eventType, forwardingListener);
		}
	}
}
