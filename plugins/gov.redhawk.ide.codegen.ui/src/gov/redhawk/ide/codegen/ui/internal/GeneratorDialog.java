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
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.IPortTemplateDesc;
import gov.redhawk.ide.codegen.PortRepToGeneratorMap;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;

import java.util.Collections;
import java.util.HashSet;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
public class GeneratorDialog extends Dialog {
	private static final int NUM_COLUMNS = 3;
	/**
	 * The title of the dialog.
	 */
	private final String title;

	/**
	 * The input value; the empty string by default.
	 */
	private PortRepToGeneratorMap value;

	/**
	 * Ok button widget.
	 */
	private Button okButton;

	/**
	 * Viewer for selecting the RepID.
	 */
	private ComboViewer repIdViewer;

	/**
	 * Viewer for selecting the code generator.
	 */
	private ComboViewer generatorViewer;

	/**
	 * Error message label widget.
	 */
	private Text errorMessageText;

	/**
	 * Error message string.
	 */
	private String errorMessage;

	private Text descriptionText;

	private final HashSet<String> repIds;

	private final String language;

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
	 * @param curValue the initial input value, or <code>null</code> if none
	 * @param ports the Ports object of the SCD containing all the projects
	 *            ports
	 * @param language the programming language of the base code generator
	 */
	public GeneratorDialog(final Shell parentShell, final String dialogTitle, final PortRepToGeneratorMap curValue, final HashSet<String> repIds,
	        final String language) {
		super(parentShell);
		this.title = dialogTitle;
		this.language = language;
		this.repIds = repIds;
		this.value = curValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			final String selectedRep = (String) ((IStructuredSelection) this.repIdViewer.getSelection()).getFirstElement();
			final IPortTemplateDesc selectedGen = (IPortTemplateDesc) ((IStructuredSelection) this.generatorViewer.getSelection()).getFirstElement();
			final PortRepToGeneratorMap ref = CodegenFactory.eINSTANCE.createPortRepToGeneratorMap();
			ref.setRepId(selectedRep);
			ref.setGenerator(selectedGen.getId());
			this.value = ref;
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
		// do this here because setting the text will set enablement on the ok
		// button
		this.repIdViewer.getControl().setFocus();
		this.repIdViewer.setInput(this.repIds);
		if (this.value != null) {
			for (final String rep : this.repIds) {
				if (this.value.getRepId().equals(rep)) {
					this.repIdViewer.setSelection(new StructuredSelection(Collections.singletonList(rep)));
					break;
				}
			}
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
		layout.numColumns = GeneratorDialog.NUM_COLUMNS;

		Label label = new Label(composite, SWT.WRAP);
		label.setText("Port RepID:");
		label.setFont(parent.getFont());
		this.repIdViewer = new ComboViewer(composite, getInputTextStyle() | SWT.READ_ONLY);
		this.repIdViewer.getCombo().setEnabled(this.value == null);
		this.repIdViewer.getCombo().addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(final ModifyEvent e) {
				final String key = GeneratorDialog.this.repIdViewer.getCombo().getText();
				IPortTemplateDesc gen = null;
				// Get all the generators for this rep
				final IPortTemplateDesc[] gens = RedhawkCodegenActivator.getCodeGeneratorPortTemplatesRegistry().findTemplatesByRepId(key,
				        GeneratorDialog.this.language);
				GeneratorDialog.this.generatorViewer.setInput(gens);

				// If we have a saved value, try to reselect it
				if (GeneratorDialog.this.value != null) {
					gen = CodegenUtil.getPortTemplate(GeneratorDialog.this.value.getGenerator(), null);
				}

				// If we couldn't find the value, or none is selected, choose the first
				if ((gen == null) && (gens.length > 0)) {
					gen = gens[0];
				}

				// If we have something to select, select it
				if (gen != null) {
					GeneratorDialog.this.generatorViewer.setSelection(new StructuredSelection(Collections.singletonList(gen)));
					GeneratorDialog.this.descriptionText.setText((gen.getDescription() == null) ? "" : gen.getDescription().trim()); // SUPPRESS CHECKSTYLE AvoidInline
				}

				// Validate the dialog
				validate();
			}

		});

		this.repIdViewer.setContentProvider(new ArrayContentProvider());
		this.repIdViewer.getControl().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		this.descriptionText = new Text(composite, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.BORDER);
		this.descriptionText.setEditable(false);
		this.descriptionText.setEnabled(false);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true, 1, GeneratorDialog.NUM_COLUMNS);
		data.widthHint = 200; // SUPPRESS CHECKSTYLE MagicNumber
		data.heightHint = (int) (data.widthHint * 0.75); // SUPPRESS CHECKSTYLE MagicNumber
		this.descriptionText.setLayoutData(data);

		label = new Label(composite, SWT.WRAP);
		label.setText("Generator:");
		label.setFont(parent.getFont());
		this.generatorViewer = new ComboViewer(composite, getInputTextStyle() | SWT.READ_ONLY);
		this.generatorViewer.getCombo().addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IPortTemplateDesc gen = (IPortTemplateDesc) ((IStructuredSelection) GeneratorDialog.this.generatorViewer.getSelection())
				        .getFirstElement();
				GeneratorDialog.this.descriptionText.setText((gen.getDescription() == null) ? "" : gen.getDescription().trim()); // SUPPRESS CHECKSTYLE AvoidInline
				validate();
			}

		});
		this.generatorViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return ((IPortTemplateDesc) element).getName();
			}
		});
		this.generatorViewer.setContentProvider(new ArrayContentProvider());
		this.generatorViewer.getControl().setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));

		this.errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
		data = new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = GeneratorDialog.NUM_COLUMNS;
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
		if ("".equals(this.repIdViewer.getCombo().getText().trim())) {
			setErrorMessage("Must select a RepID.");
		} else if ("".equals(this.generatorViewer.getCombo().getText().trim())) {
			setErrorMessage("Must select a generator.");
		} else {
			setErrorMessage(null);
		}
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
	 * @return the property value string
	 */
	public EObject getValue() {
		return this.value;
	}

	/**
	 * Sets or clears the error message. If not <code>null</code>, the OK button
	 * is disabled.
	 * 
	 * @param errorMessage the error message, or <code>null</code> to clear
	 */
	public void setErrorMessage(final String errorMessage) {
		this.errorMessage = errorMessage;
		if (this.errorMessageText != null && !this.errorMessageText.isDisposed()) {
			this.errorMessageText.setText((errorMessage == null) ? " \n " : errorMessage); // SUPPRESS CHECKSTYLE AvoidInline //$NON-NLS-1$
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
				button.setEnabled(errorMessage == null);
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
