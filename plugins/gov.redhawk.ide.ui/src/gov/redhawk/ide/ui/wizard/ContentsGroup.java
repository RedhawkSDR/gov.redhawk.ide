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

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @since 8.0
 * 
 */
public class ContentsGroup {

	private final Button createNewResourceButton;
	private final Button importFileButton;
	private final Text fileText;
	private final Button browseButton;
	private IValidator validator;
	private final Group group;
	private final IValidatableWizardPage page;

	public ContentsGroup(final Composite parent, final int style, final String resourceType, final String resourceExtension, final IValidatableWizardPage page) {
		final int COLS = 3;
		this.page = page;
		this.group = new Group(parent, style);
		this.group.setText("Contents");
		this.group.setLayout(new GridLayout(COLS, false));

		this.createNewResourceButton = new Button(this.group, SWT.RADIO);
		GridDataFactory.generate(this.createNewResourceButton, COLS, 1);
		this.createNewResourceButton.setText("Create a new " + resourceType.toLowerCase());
		this.createNewResourceButton.setLayoutData(GridDataFactory.fillDefaults().span(COLS, 1).create());
		this.createNewResourceButton.setSelection(true);
		this.createNewResourceButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				page.validate();
				ContentsGroup.this.fileText.setEnabled(ContentsGroup.this.importFileButton.getSelection());
				ContentsGroup.this.browseButton.setEnabled(ContentsGroup.this.importFileButton.getSelection());
			}
		});

		this.importFileButton = new Button(this.group, SWT.RADIO);
		this.importFileButton.setText("Use existing " + resourceType.toLowerCase() + " as a template");
		this.importFileButton.setLayoutData(GridDataFactory.fillDefaults().span(COLS, 1).create());

		new Label(this.group, SWT.NONE).setText(resourceExtension + " File:");
		this.fileText = new Text(this.group, SWT.BORDER);
		this.fileText.setEnabled(false);
		this.fileText.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				page.validate();
			}
		});
		GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.CENTER).applyTo(this.fileText);
		this.browseButton = new Button(this.group, SWT.NONE);
		this.browseButton.setText("Browse...");
		this.browseButton.setEnabled(false);
		this.browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleBrowseForTemplate(resourceExtension);
			}
		});
	}

	protected void handleBrowseForTemplate(final String resourceExtension) {
		final FileDialog dialog = new FileDialog(this.browseButton.getShell(), SWT.OPEN);
		dialog.setFilterExtensions(new String[] { "*." + resourceExtension.toLowerCase() + ".xml", "*" });
		dialog.setText("Open " + resourceExtension.toUpperCase());
		final String result = dialog.open();
		if (result != null) {
			this.fileText.setText(result);
		}
	}

	public void setLayoutData(final Object layoutData) {
		this.group.setLayoutData(layoutData);
	}

	public void setValidator(final IValidator validator) {
		this.validator = validator;
		this.page.validate();
	}

	public IStatus validateGroup() {
		if (this.createNewResourceButton.getSelection()) {
			return Status.OK_STATUS;
		} else if (this.validator != null) {
			return this.validator.validate(this.fileText.getText());
		} else {
			return Status.OK_STATUS;
		}
	}

	public boolean isCreateNewResource() {
		return this.createNewResourceButton.getSelection();
	}

	public IPath getExistingResourcePath() {
		return new Path(this.fileText.getText().trim());
	}
	
	public Button getCreateNewResourceButton() {
	    return createNewResourceButton;
    }
	
	public Button getImportFileButton() {
	    return importFileButton;
    }

}
