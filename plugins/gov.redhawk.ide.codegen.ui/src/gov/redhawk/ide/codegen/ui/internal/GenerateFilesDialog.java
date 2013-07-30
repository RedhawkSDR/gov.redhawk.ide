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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class GenerateFilesDialog extends Dialog {
	/**
	 * The list of all files that can be generated and the default generation
	 * state
	 */
	private HashMap<String, Boolean> generateFiles = null;
	/** The initial list of selected files */
	private List<String> defaultSelection = new ArrayList<String>();
	/** The list of files to be generated */
	private ArrayList<String> filesToGenerate = null;
	/** The Checkbox Tree that handles the input */
	private CheckboxTreeViewer v = null;
	/** The implementation settings for these generated file **/
	private String name = null;
	private boolean generateAll;

	public GenerateFilesDialog(final Shell parentShell, final HashMap<String, Boolean> generateFiles, final String name) {
		super(parentShell);
		this.generateFiles = generateFiles;
		for (String fileName : this.generateFiles.keySet()) {
			if (this.generateFiles.get(fileName)) {
				defaultSelection.add(fileName);
			}
		}
		this.filesToGenerate = new ArrayList<String>();
		this.name = name;
	}

	@Override
	protected void configureShell(final Shell newShell) {
		newShell.setText("Regenerate files for: " + this.name);

		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout(2, false);
		container.setLayout(gridLayout);

		final Image image = parent.getDisplay().getSystemImage(SWT.ICON_WARNING);
		final Label warning = new Label(container, SWT.NONE);
		warning.setImage(image);
		warning.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));

		final Label label = new Label(container, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		label.setText("The following generated files have had their contents modified since they were generated.\n"
		        + "Selected files will be re-generated and their current contents will be lost.");

		final Button button = new Button(container, SWT.CHECK);
		button.setText("Generate All Files");
		button.setToolTipText("Checking this will cause all files to be regenerated.");
		button.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				// Disable the tree if the All button is selected
				GenerateFilesDialog.this.v.getControl().setEnabled(!button.getSelection());

				// If the All button is selected, save the currently selected
				// files, then enable all of them
				generateAll = button.getSelection();
				if (button.getSelection()) {
					GenerateFilesDialog.this.filesToGenerate.clear();
					for (final Object fileName : GenerateFilesDialog.this.v.getCheckedElements()) {
						GenerateFilesDialog.this.filesToGenerate.add((String) fileName);
					}

					GenerateFilesDialog.this.v.setCheckedElements(GenerateFilesDialog.this.generateFiles.keySet().toArray(new String[0]));

					// Otherwise, restore the previously selected files
				} else {
					GenerateFilesDialog.this.v.setCheckedElements(GenerateFilesDialog.this.filesToGenerate.toArray(new String[0]));
				}
			}
		});

		this.v = new CheckboxTreeViewer(container);
		this.v.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		this.v.setContentProvider(new MyContentProvider());
		this.v.setLabelProvider(new LabelProvider());
		this.v.getTree().setLinesVisible(true);
		this.v.getTree().setHeaderVisible(false);

		// Get a list of files that are currently checked
		for (final Map.Entry<String, Boolean> entry : this.generateFiles.entrySet()) {
			if (entry.getValue()) {
				this.filesToGenerate.add(entry.getKey());
			}
		}

		// Set the tree input, check the boxes later
		final List<String> vals = Arrays.asList(this.generateFiles.keySet().toArray(new String[0]));
		Collections.sort(vals);
		this.v.setInput(vals.toArray(new String[0]));

		selectAllFiles();

		final Composite panel = new Composite(container, SWT.NO_FOCUS);
		panel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		panel.setLayout(new RowLayout());

		final Button clear = new Button(panel, SWT.PUSH);
		clear.setText("Deselect All Files");
		clear.setToolTipText("Clicking this will deselect all files");
		clear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				GenerateFilesDialog.this.deselectAllFiles();
			}
		});

		final Button restore = new Button(panel, SWT.PUSH);
		restore.setText("Restore Defaults");
		restore.setToolTipText("Clicking this will restore the selection to the default set of files");
		restore.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				GenerateFilesDialog.this.selectAllFiles();
			}
		});

		// Enable the clear and restore buttons based on the setting of the "Generate All" checkbox.
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				boolean enabled = !button.getSelection();
				clear.setEnabled(enabled);
				restore.setEnabled(enabled);
			}
		});

		return container;
	}

	private class MyContentProvider implements ITreeContentProvider {
		private Object input = null;

		public Object[] getChildren(final Object parentElement) {
			return new Object[0];
		}

		public Object getParent(final Object element) {
			return null;
		}

		public boolean hasChildren(final Object element) {
			return false;
		}

		public Object[] getElements(final Object inputElement) {
			return (Object[]) this.input;
		}

		public void dispose() {
		}

		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
			this.input = newInput;
		}
	};

	/**
	 * This method will set the checkboxes for the default files.
	 */
	private void selectAllFiles() {
		this.v.setCheckedElements(this.defaultSelection.toArray(new String[this.defaultSelection.size()]));
	}

	private void deselectAllFiles() {
		this.v.setCheckedElements(new String[0]);
	}

	@Override
	protected void okPressed() {
		// Need to save the filenames here, doing it in getFilesToGenerate
		// is too late, the Tree is disposed
		this.filesToGenerate.clear();
		for (final Object fileName : this.v.getCheckedElements()) {
			this.filesToGenerate.add((String) fileName);
		}
		super.okPressed();
	}

	/**
	 * This returns an array of strings for the currently checked files.
	 * 
	 * @return list of filenames to be generated
	 */
	public String[] getFilesToGenerate() {
		if (generateAll) {
			return null;
		}
		return this.filesToGenerate.toArray(new String[this.filesToGenerate.size()]);
	}
}
