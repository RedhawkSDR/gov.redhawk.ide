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

import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;

public abstract class XViewerCellEditor extends Composite {

	private ICellEditorValidator validator = null;
	private String errorMessage = null;

	public XViewerCellEditor(Composite parent, int style) {
		super(parent, style);

		validator = null;
	}

	public void setValue(Object value) {
		doSetValue(value);
	}

	protected abstract void doSetValue(Object value);

	public Object getValue() {
		return doGetValue();
	}

	protected abstract Object doGetValue();

	protected abstract Control getMainControl();

	@Override
	public void addListener(int eventType, Listener listener) {
		getMainControl().addListener(eventType, listener);
	}

	@Override
	public void removeListener(int eventType, Listener listener) {
		getMainControl().removeListener(eventType, listener);
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

	protected boolean isCorrect(Object value) {
		errorMessage = null;
		if (validator == null) {
			return true;
		}

		errorMessage = validator.isValid(value);
		return (errorMessage == null || errorMessage.isEmpty());
	}
}
