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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.Property;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * The Class PropertyDialog.
 */
public class PropertyDialog extends Dialog {
	/**
	 * The title of the dialog.
	 */
	private final String title;

	/**
	 * The input value; the empty string by default.
	 */
	private Property value;

	/**
	 * Ok button widget.
	 */
	private Button okButton;

	/**
	 * Input text widget.
	 */
	private ComboViewer idText;

	/**
	 * Error message label widget.
	 */
	private Text errorMessageText;

	/**
	 * Error message string.
	 */
	private String errorMessage;

	private Text valueText;

	private final ITemplateDesc desc;

	private Text descriptionText;

	/**
	 * Creates an input dialog with OK and Cancel buttons. Note that the dialog
	 * will have no visual representation (no widgets) until it is told to open.
	 * <p>
	 * Note that the <code>open</code> method blocks for input dialogs.
	 * </p>
	 * 
	 * @param parentShell the parent shell, or <code>null</code> to create a
	 *            top-level shell
	 * @param dialogTitle the dialog title, or <code>null</code> if none
	 * @param initialValue the initial input value, or <code>null</code> if none
	 *            (equivalent to the empty string)
	 */
	public PropertyDialog(final Shell parentShell, final String dialogTitle, final Property initialValue, final ITemplateDesc desc) {
		super(parentShell);
		this.title = dialogTitle;
		this.desc = desc;
		if (initialValue == null) {
			this.value = CodegenFactory.eINSTANCE.createProperty();
		} else {
			this.value = initialValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			this.value.setId(this.idText.getCombo().getText());
			this.value.setValue(this.valueText.getText());
		} else {
			this.value = null;
		}
		super.buttonPressed(buttonId);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		if (this.title != null) {
			shell.setText(this.title);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		// create OK and Cancel buttons by default
		this.okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		this.okButton.setEnabled(false);
		// do this here because setting the text will set enablement on the ok button
		this.idText.getControl().setFocus();
		this.idText.setInput(this.desc.getPropertyDescriptors());
		if (this.value != null) {
			String id = this.value.getId();
			if (id == null) {
				id = "";
			}
			this.idText.getCombo().setText(id);
			String valueStr = this.value.getValue();
			if (valueStr == null) {
				valueStr = "";
			}
			this.valueText.setText(valueStr);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(final Composite parent) {

		// create composite
		final Composite composite = (Composite) super.createDialogArea(parent);
		final GridLayout layout = (GridLayout) composite.getLayout();
		layout.numColumns = 3; // SUPPRESS CHECKSTYLE MagicNumber

		Label label = new Label(composite, SWT.WRAP);
		label.setText("ID:");
		label.setFont(parent.getFont());
		this.idText = new ComboViewer(composite, getInputTextStyle() | SWT.READ_ONLY);
		this.idText.getCombo().addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				final String key = PropertyDialog.this.idText.getCombo().getText();
				String v = null;
				for (final IPropertyDescriptor pDesc : PropertyDialog.this.desc.getPropertyDescriptors()) {
					if (pDesc.getKey().equals(key)) {
						v = pDesc.getDescription();
						if (PropertyDialog.this.valueText.getText().length() == 0) {
							PropertyDialog.this.valueText.setText(pDesc.getDefaultValue());
						}
						break;
					}
				}
				if (v == null) {
					v = "";
				}
				PropertyDialog.this.descriptionText.setText(v.trim());
				validate();
			}

		});
		this.idText.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				final IPropertyDescriptor prop = (IPropertyDescriptor) element;
				return prop.getKey();
			}
		});
		this.idText.setContentProvider(new ArrayContentProvider());
		this.idText.getControl().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		this.descriptionText = new Text(composite, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER);
		this.descriptionText.setEditable(false);
		this.descriptionText.setEnabled(false);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2);
		data.widthHint = 200; // SUPPRESS CHECKSTYLE MagicNumber
		data.heightHint = (int) (data.widthHint * 0.75); // SUPPRESS CHECKSTYLE MagicNumber
		this.descriptionText.setLayoutData(data);

		label = new Label(composite, SWT.WRAP);
		label.setText("Value:");
		label.setFont(parent.getFont());
		label.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.TOP).grab(false, true).create());
		this.valueText = new Text(composite, getInputTextStyle());
		this.valueText.setLayoutData(GridDataFactory.fillDefaults().align(SWT.FILL, SWT.TOP).grab(true, false).create());
		this.valueText.addModifyListener(new ModifyListener() {

			public void modifyText(final ModifyEvent e) {
				validate();
			}

		});

		this.errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = layout.numColumns;
		this.errorMessageText.setLayoutData(data);
		this.errorMessageText.setBackground(this.errorMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		// Set the error message text
		// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=66292
		setErrorMessage(this.errorMessage);

		Dialog.applyDialogFont(composite);
		return composite;
	}

	/**
	 * 
	 */
	protected void validate() {
		boolean enabledOkay = false;
		if ("".equals(this.idText.getCombo().getText().trim())) {
			setErrorMessage("Must enter a property id.");
		} else if ("".equals(this.valueText.getText().trim())) {
			setErrorMessage("Must enter a property value.");
		} else {
			setErrorMessage(null);
			enabledOkay = true;
		}
		this.okButton.setEnabled(enabledOkay);
	}

	/**
	 * Returns the ok button.
	 * 
	 * @return the ok button
	 */
	protected Button getOkButton() {
		return this.okButton;
	}

	/**
	 * Returns the string typed into this input dialog.
	 * 
	 * @return the input string
	 */
	public Property getValue() {
		return this.value;
	}

	/**
	 * Sets or clears the error message. If not <code>null</code>, the OK button
	 * is disabled.
	 * 
	 * @param errorMessage the error message, or <code>null</code> to clear
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			if (errorMessage == null) {
				errorMessage = " \n "; //$NON-NLS-1$
			}
			this.errorMessageText.setText(errorMessage);
			// Disable the error message text control if there is no error, or
			// no error text (empty or whitespace only). Hide it also to avoid
			// color change.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=130281
			final boolean hasError = errorMessage != null && (StringConverter.removeWhiteSpaces(errorMessage)).length() > 0;
			this.errorMessageText.setEnabled(hasError);
			this.errorMessageText.setVisible(hasError);
			this.errorMessageText.getParent().update();
			// Access the ok button by id, in case clients have overridden
			// button creation.
			// See https://bugs.eclipse.org/bugs/show_bug.cgi?id=113643
			final Control button = getButton(IDialogConstants.OK_ID);
			if (button != null) {
				button.setEnabled(!hasError);
			}
		}
	}

	/**
	 * Returns the style bits that should be used for the input text field.
	 * Defaults to a single line entry. Subclasses may override.
	 * 
	 * @return the integer style bits that should be used when creating the
	 *         input text
	 */
	protected int getInputTextStyle() {
		return SWT.SINGLE | SWT.BORDER;
	}
}
