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
package gov.redhawk.ide.spd.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class InternalErrorDialog extends ErrorDialog {

	public static int openError(Shell parent, String dialogTitle, String message, IStatus status) {
		return openError(parent, dialogTitle, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
	}

	public static int openError(Shell parentShell, String title, String message, IStatus status, int displayMask) {
		InternalErrorDialog dialog = new InternalErrorDialog(parentShell, title, message, status, displayMask);
		return dialog.open();
	}
	
	public InternalErrorDialog(Shell parentShell, String dialogTitle, String message,
		IStatus status, int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
		this.message = message;
	}

	public InternalErrorDialog(Shell parentShell, String dialogTitle, String message, IStatus status) {
		this(parentShell, dialogTitle, message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		createDetailsButton(parent);
	}

}
