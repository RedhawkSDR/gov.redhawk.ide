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
package gov.redhawk.ide.sad.graphiti.ui.diagram.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label; 
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Implementations of this abstract class generate a dialog with text input
 * and validation of said input.
 */
public abstract class AbstractInputValidationDialog extends TitleAreaDialog {

	private Text inputText;
	private String inputString;
	private String windowTitle, defaultMessage, inputLabel;

	public AbstractInputValidationDialog(String windowTitle, String defaultMessage, String inputLabel) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.windowTitle = windowTitle;
		this.defaultMessage = defaultMessage;
		this.inputLabel = inputLabel;
		
	}

	@Override
	public void create() {
		super.create();
		setTitle(windowTitle);
		setMessage(defaultMessage);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayout layout = new GridLayout(2, false);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		container.setLayout(layout);

		createInputArea(container);

		return area;
	}

	private void createInputArea(Composite container) {
		Label hostCollocationLabel = new Label(container, SWT.NONE);
		hostCollocationLabel.setText(inputLabel);

		GridData hostCollocationData = new GridData();
		hostCollocationData.grabExcessHorizontalSpace = true;
		hostCollocationData.horizontalAlignment = GridData.FILL;

		inputText = new Text(container, SWT.BORDER);
		inputText.setLayoutData(hostCollocationData);
		inputText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String err = inputValidity(inputText.getText());
				setErrorMessage(err);
				if (err != null) {
					getButton(OK).setEnabled(false);
					return;
				}
				setMessage(defaultMessage);
				getButton(OK).setEnabled(true);
			}
		});
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
	
	public abstract String inputValidity(String value);

	// save content of the Text fields because they get disposed
	// as soon as the Dialog closes
	private void saveInput() {
		inputString = inputText.getText(); 
	}

	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}

	public String getInput() {
		create();
		if (open() != Window.OK) {
			return null;
		} 
		return inputString;
	}
}
