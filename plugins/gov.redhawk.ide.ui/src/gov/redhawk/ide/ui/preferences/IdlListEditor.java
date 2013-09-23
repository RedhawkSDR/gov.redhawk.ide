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
package gov.redhawk.ide.ui.preferences;

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceConstants;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;

/**
 * @since 4.1
 */
public class IdlListEditor extends FieldEditor {
	/**
	 * The list widget; <code>null</code> if none (before creation or after
	 * disposal).
	 */
	private ListViewer list;

	/**
	 * The button box containing the Add, Remove buttons; <code>null</code> if
	 * none (before creation or after disposal).
	 */
	private Composite buttonBox;

	private static final int NUM_BUTTONS = 5;

	private final List<String> idlLocations = new ArrayList<String>();

	private Button removeButton;

	private String startingDirectory;

	private IPreferenceStore prefs = null;

	private Button upButton;

	private Button downButton;

	/**
	 * Creates a new list field editor
	 */
	public IdlListEditor() {
		setPreferenceName(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
	}

	/**
	 * Creates a list field editor.
	 * 
	 * @param labelText the label text of the field editor
	 * @param parent the parent of the field editor's control
	 * @param prefs the preference store for the list
	 */
	public IdlListEditor(final String labelText, final Composite parent, final IPreferenceStore prefs) {
		init("", labelText);
		createControl(parent);
		this.prefs = prefs;
		setPreferenceName(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void adjustForNumColumns(final int numColumns) {
		final Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) this.list.getList().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box the box for the buttons
	 */
	private void createButtons(final Composite box) {
		final Button newButton = createPushButton(box, "New...");
		newButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final DirectoryDialog fileDialog = new DirectoryDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN | SWT.SHEET);
				if (IdlListEditor.this.startingDirectory != null) {
					fileDialog.setFilterPath(IdlListEditor.this.startingDirectory);
				} else {
					fileDialog.setFilterPath(RedhawkIdeActivator.getDefault().getRuntimePath().toOSString());
				}
				String dir = fileDialog.open();
				if (dir != null) {
					dir = dir.trim();
					if (dir.length() > 0) {
						IdlListEditor.this.startingDirectory = dir;
						IdlListEditor.this.idlLocations.add(dir);
						IdlListEditor.this.list.refresh();
					}
				}

			}

		});
		this.removeButton = createPushButton(box, "Remove");
		this.removeButton.setEnabled(false);
		this.removeButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final boolean confirmedDelete = MessageDialog.openConfirm(box.getShell(), "Delete IDL path entry?",
				"Are you sure you want to remove the entry from the IDL locations?");
				if (confirmedDelete) {
					for (final Object obj : ((StructuredSelection) IdlListEditor.this.list.getSelection()).toArray()) {
						IdlListEditor.this.idlLocations.remove(obj);
					}

					IdlListEditor.this.list.refresh();
				}
			}

		});
		this.upButton = createPushButton(box, "Up");
		this.upButton.setEnabled(false);
		this.upButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IStructuredSelection sel = (IStructuredSelection) IdlListEditor.this.list.getSelection();

				if (!sel.isEmpty()) {
					final String path = (String) sel.getFirstElement();
					final int idx = IdlListEditor.this.idlLocations.indexOf(path);
					if (idx > 0) {
						IdlListEditor.this.idlLocations.remove(idx);
						IdlListEditor.this.idlLocations.add(idx - 1, path);
						IdlListEditor.this.list.refresh();
						IdlListEditor.this.enableButtons();
					}
				}
			}

		});
		this.downButton = createPushButton(box, "Down");
		this.downButton.setEnabled(false);
		this.downButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final IStructuredSelection sel = (IStructuredSelection) IdlListEditor.this.list.getSelection();

				if (!sel.isEmpty()) {
					final String path = (String) sel.getFirstElement();
					final int idx = IdlListEditor.this.idlLocations.indexOf(path);
					if (idx < (IdlListEditor.this.idlLocations.size() - 1)) {
						IdlListEditor.this.idlLocations.remove(idx);
						IdlListEditor.this.idlLocations.add(idx + 1, path);
						IdlListEditor.this.list.refresh();
						IdlListEditor.this.enableButtons();
					}
				}
			}

		});
	}

	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent the parent control
	 * @param key the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(final Composite parent, final String text) {
		final Button button = new Button(parent, SWT.PUSH);
		button.setText(text);
		button.setFont(parent.getFont());
		final GridData data = new GridData(GridData.FILL_HORIZONTAL);
		final int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return button;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doFillIntoGrid(final Composite parent, final int numColumns) {
		final Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);

		this.list = new ListViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		this.list.setContentProvider(new ArrayContentProvider());
		this.list.setLabelProvider(new LabelProvider());
		this.list.setInput(this.idlLocations);
		this.list.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				IdlListEditor.this.removeButton.setEnabled(!event.getSelection().isEmpty());
				IdlListEditor.this.enableButtons();
			}
		});

		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		final int heightHint = convertVerticalDLUsToPixels(this.list.getList(), IDialogConstants.BUTTON_HEIGHT * IdlListEditor.NUM_BUTTONS);
		gd.heightHint = Math.max(heightHint, this.list.getList().computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		this.list.getList().setLayoutData(gd);

		this.buttonBox = getButtonBoxControl(parent);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		this.buttonBox.setLayoutData(gd);
	}

	protected void enableButtons() {
		if (!this.list.getSelection().isEmpty()) {
			final int idx = IdlListEditor.this.idlLocations.indexOf(((IStructuredSelection) this.list.getSelection()).getFirstElement());
			IdlListEditor.this.upButton.setEnabled(idx > 0);
			IdlListEditor.this.downButton.setEnabled(idx < (IdlListEditor.this.idlLocations.size() - 1));
		} else {
			IdlListEditor.this.upButton.setEnabled(false);
			IdlListEditor.this.downButton.setEnabled(false);
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doLoad() {
		this.idlLocations.clear();

		final String paths = this.prefs.getString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
		final String delim = this.prefs.getString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER);
		for (final String path : paths.split(delim)) {
			if ((path != null) && (path.trim().length() != 0)) {
				this.idlLocations.add(path);
			}
		}

		if (this.list != null) {
			this.list.refresh();
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doLoadDefault() {
		this.idlLocations.clear();

		final String paths = this.prefs.getDefaultString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE);
		final String delim = this.prefs.getDefaultString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER);
		for (final String path : paths.split(delim)) {
			this.idlLocations.add(path);
		}

		if (this.list != null) {
			this.list.refresh();
		}
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	protected void doStore() {
		final StringBuilder builder = new StringBuilder();
		final String delim = this.prefs.getString(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE_DELIMITER);

		for (int i = 0; i < this.idlLocations.size(); ++i) {
			builder.append(this.idlLocations.get(i));
			if (i != (this.idlLocations.size() - 1)) {
				builder.append(delim);
			}
		}

		this.prefs.putValue(RedhawkIdePreferenceConstants.RH_IDE_IDL_INCLUDE_PATH_PREFERENCE, builder.toString());
	}

	/**
	 * Returns this field editor's button box containing the Add, Remove, Up,
	 * and Down button.
	 * 
	 * @param parent the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(final Composite parent) {
		if (this.buttonBox == null) {
			this.buttonBox = new Composite(parent, SWT.NULL);
			final GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			this.buttonBox.setLayout(layout);
			createButtons(this.buttonBox);
			this.buttonBox.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(final DisposeEvent event) {
					IdlListEditor.this.buttonBox = null;
				}
			});

		} else {
			checkParent(this.buttonBox, parent);
		}

		return this.buttonBox;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	public int getNumberOfControls() {
		return 2;
	}

	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	@Override
	public void setFocus() {
		if (this.list != null) {
			this.list.getList().setFocus();
		}
	}

	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	@Override
	public void setEnabled(final boolean enabled, final Composite parent) {
		super.setEnabled(enabled, parent);
		this.list.getList().setEnabled(enabled);
	}

}
