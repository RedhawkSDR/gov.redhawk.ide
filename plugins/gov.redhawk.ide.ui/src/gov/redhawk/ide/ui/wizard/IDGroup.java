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
package gov.redhawk.ide.ui.wizard;

import gov.redhawk.validation.DceUuidConstraint;

import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @since 8.0
 * 
 */
public class IDGroup {

	private final Button generateIdButton;
	private final Button useProvidedIdButton;
	private final Text providedIdText;
	private final Group group;
	private final IValidatableWizardPage page;

	public IDGroup(final Composite parent, final int style, final String resourceType, final IValidatableWizardPage page) {
		this.page = page;
		this.group = new Group(parent, style);
		this.group.setText(Character.toString(resourceType.charAt(0)).toUpperCase() + resourceType.substring(1) + " ID");
		this.group.setLayout(new GridLayout(2, false));

		this.generateIdButton = new Button(this.group, SWT.RADIO);
		GridDataFactory.generate(this.generateIdButton, 3, 1);
		this.generateIdButton.setText("Generate an ID");
		this.generateIdButton.setSelection(true);
		this.generateIdButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				IDGroup.this.providedIdText.setEnabled(!IDGroup.this.generateIdButton.getSelection());
				page.validate();
			}
		});

		this.useProvidedIdButton = new Button(this.group, SWT.RADIO);
		this.useProvidedIdButton.setText("Provide an ID");
		GridDataFactory.generate(this.useProvidedIdButton, 3, 1);

		new Label(this.group, SWT.NONE).setText("DCE UUID:");
		this.providedIdText = new Text(this.group, SWT.BORDER);
		this.providedIdText.setEnabled(false);
		this.providedIdText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				page.validate();
			}
		});
		GridDataFactory.swtDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.providedIdText);
	}

	public IStatus validateGroup() {
		if (!IDGroup.this.providedIdText.isEnabled()) {
			return Status.OK_STATUS;
		}

		return DceUuidConstraint.validate(this.providedIdText.getText());
	}
	
	/**
     * @since 9.0
     */
	public String getId() {
		if (generateIdButton.getSelection()) {
			return DceUuidUtil.createDceUUID();
		} else {
			return providedIdText.getText();
		}
	}

	public void setLayoutData(final Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

}
