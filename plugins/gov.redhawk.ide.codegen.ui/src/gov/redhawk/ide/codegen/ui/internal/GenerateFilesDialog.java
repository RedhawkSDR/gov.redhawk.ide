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

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.preferences.CodegenPreferenceConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeColumnViewerLabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
	private final Set<FileStatus> fileStatus;
	/** The Checkbox Tree that handles the input */
	private CheckboxTreeViewer treeViewer;
	private boolean askedUserFile = false;
	private boolean showUserFiles = false;
	private boolean askedSystemFileGenerate;
	private boolean askedSystemFileNotGenerate;

	private static class FileStatusContentProvider implements ITreeContentProvider {

		@Override
		public void dispose() {

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		}

		@Override
		public Object[] getElements(Object inputElement) {
			return ((Set< ? >) inputElement).toArray();
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public boolean hasChildren(Object element) {
			return false;
		}

	}

	public GenerateFilesDialog(final Shell parentShell, final Set<FileStatus> generateFiles) {
		super(parentShell);
		this.fileStatus = generateFiles;
	}

	@Override
	protected void configureShell(final Shell newShell) {
		newShell.setText("Regenerate files");

		super.configureShell(newShell);
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout(2, false);
		container.setLayout(gridLayout);
		
		boolean warning = false;
		for (FileStatus s : this.fileStatus) {
			if (!s.getDoItDefault() && s.getType() != FileStatus.Type.USER) {
				warning = true;
				break;
			}
		}
		if (warning) {
			final Image image = parent.getDisplay().getSystemImage(SWT.ICON_WARNING);
			final Label warningLabel = new Label(container, SWT.NONE);
			warningLabel.setImage(image);
			warningLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	
			final Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			label.setText("The following generated files have had their contents modified since they were generated.\n"
				+ "Selected files will be generated and their current contents will be lost.");
		} else {
			final Image image = parent.getDisplay().getSystemImage(SWT.ICON_INFORMATION);
			final Label warningLabel = new Label(container, SWT.NONE);
			warningLabel.setImage(image);
			warningLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
	
			final Label label = new Label(container, SWT.NONE);
			label.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
			label.setText("Select files to be generated:");
		}

		Composite treeContainer = new Composite(container, SWT.None);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		data.widthHint = 600;
		treeContainer.setLayoutData(data);
		TreeColumnLayout layout = new TreeColumnLayout();
		treeContainer.setLayout(layout);
		this.treeViewer = new CheckboxTreeViewer(treeContainer, SWT.BORDER | SWT.SINGLE | SWT.FULL_SELECTION | SWT.H_SCROLL);
		this.treeViewer.setContentProvider(new FileStatusContentProvider());
		this.treeViewer.getTree().setLinesVisible(true);
		this.treeViewer.getTree().setHeaderVisible(true);

		TreeViewerColumn column = new TreeViewerColumn(this.treeViewer, SWT.None);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setText("File Name");
		column.setLabelProvider(new TreeColumnViewerLabelProvider(new LabelProvider()));
		layout.setColumnData(column.getColumn(), new ColumnWeightData(80, 80, true));

		column = new TreeViewerColumn(this.treeViewer, SWT.None);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setText("Action");
		column.setLabelProvider(new TreeColumnViewerLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((FileStatus) element).getDesiredAction().toString();
			}
		}));
		layout.setColumnData(column.getColumn(), new ColumnWeightData(10, 80, true));

		column = new TreeViewerColumn(this.treeViewer, SWT.None);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(false);
		column.getColumn().setText("Type");
		column.setLabelProvider(new TreeColumnViewerLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((FileStatus) element).getType().toString();
			}
		}));
		layout.setColumnData(column.getColumn(), new ColumnWeightData(10, 80, true));

		this.treeViewer.setCheckStateProvider(new ICheckStateProvider() {

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}

			@Override
			public boolean isChecked(Object element) {
				if (element instanceof FileStatus) {
					return ((FileStatus) element).isDoIt();
				}
				return false;
			}
		});
		this.treeViewer.addCheckStateListener(new ICheckStateListener() {

			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				FileStatus s = (FileStatus) event.getElement();
				if (checkFile(event.getChecked(), s)) {
					s.setDoIt(event.getChecked());
				}
				treeViewer.refresh();
			}
		});
		this.treeViewer.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (((FileStatus) element).getType() == FileStatus.Type.USER) {
					return showUserFiles;
				}
				return true;
			}
		});
		this.treeViewer.setComparator(new ViewerComparator());

		this.treeViewer.setInput(this.fileStatus);

		Composite panel = new Composite(container, SWT.NO_FOCUS);
		panel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		panel.setLayout(new RowLayout());

		final Button restore = new Button(panel, SWT.PUSH);
		restore.setText("Defaults");
		restore.setToolTipText("Clicking this will restore the selection to the default set of files");
		restore.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				selectDefaults();
			}
		});

		final Button selectAll = new Button(panel, SWT.PUSH);
		selectAll.setText("Select All");
		selectAll.setToolTipText("Clicking this will select all files");
		selectAll.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				selectAllFiles();
			}
		});

		final Button clear = new Button(panel, SWT.PUSH);
		clear.setText("Clear");
		clear.setToolTipText("Clicking this will deselect all files");
		clear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(final Event event) {
				GenerateFilesDialog.this.clear();
			}
		});

		panel = new Composite(container, SWT.NO_FOCUS);
		panel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 2, 1));
		panel.setLayout(new GridLayout(1, false));
		Button showUserButton = new Button(panel, SWT.CHECK);
		showUserButton.setText("Show User Files");
		showUserButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				showUserFiles = !showUserFiles;
				treeViewer.refresh();
			}
		});
		final Button alwaysButton = new Button(panel, SWT.CHECK);
		alwaysButton.setText("Always Generate Default Files Without Asking");
		final IPreferenceStore store = RedhawkCodegenUiActivator.getDefault().getPreferenceStore();
		alwaysButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				alwaysButton.getSelection();
				store.setValue(CodegenPreferenceConstants.P_ALWAYS_GENERATE_DEFAULTS, alwaysButton.getSelection());
			}
		});
		alwaysButton.setSelection(store.getBoolean(CodegenPreferenceConstants.P_ALWAYS_GENERATE_DEFAULTS));

		return container;
	}

	private void selectDefaults() {
		for (FileStatus s : this.fileStatus) {
			s.setToDefault();
		}
		this.treeViewer.refresh();
	}

	public void setShowUserFiles(boolean showUserFiles) {
		this.showUserFiles = showUserFiles;
	}

	public boolean isShowUserFiles() {
		return showUserFiles;
	}
	
	protected boolean checkUserFile(boolean newValue, FileStatus element) {
		if (!askedUserFile) {
			if (newValue && !element.getDoItDefault()) {
				MessageDialog dialog = new MessageDialog(getShell(), "WARNING", null,
					"You have indicated you wish to generate a file that is marked as a 'USER' file.  "
						+ "This file may contain code that was written by the user.  " + "\n\nCONTINUING WILL OVERWRITE THIS CODE.\n\n"
						+ "Are you sure you want to do this?", MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
				if (dialog.open() == 0) {
					askedUserFile = true;
					return true;
				} else {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}
	
	protected boolean checkSystemFile(boolean newValue, FileStatus element) {
		if (!askedSystemFileGenerate && newValue) {
			if (!element.getDoItDefault()) {
				MessageDialog dialog = new MessageDialog(getShell(), "WARNING", null,
					"You have indicated you wish to generate a 'SYSTEM' file that has been modified.  "
						+ "This file may contain code that was written by the user.  " + "\n\nCONTINUING WILL OVERWRITE THIS CODE.\n\n"
						+ "Are you sure you want to do this?", MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
				if (dialog.open() == 0) {
					askedSystemFileGenerate = true;
					return true;
				} else {
					return false;
				}
			}
		} else if (!askedSystemFileNotGenerate && !newValue) {
			if (element.getDoItDefault()) {
				MessageDialog dialog = new MessageDialog(getShell(), "WARNING", null,
					"You have indicated you wish to NOT generate a 'SYSTEM' file.\n"
						+ "Not generating this file may have undesired side effects.\n\n"
						+ "Are you sure you want to do this?", MessageDialog.WARNING, new String[] { "Yes", "No" }, 1);
				if (dialog.open() == 0) {
					askedSystemFileNotGenerate = true;
					return true;
				} else {
					return false;
				}
			}
		} 
		
		return true;
	}

	protected boolean checkFile(boolean newValue, FileStatus element) {
		switch(element.getType()) {
		case USER:
			return checkUserFile(newValue, element);
		case SYSTEM:
			return checkSystemFile(newValue, element);
		default:
			break;
		}
		return true;
	}
	
	@Override
	protected void okPressed() {
		// TODO Auto-generated method stub
		super.okPressed();
	}

	/**
	 * This method will set the checkboxes for the default files.
	 */
	private void selectAllFiles() {
		for (FileStatus s : this.fileStatus) {
			if (showUserFiles) {
				if (checkFile(true, s)) {
					s.setDoIt(true);
				}
			} else {
				if (s.getType() == FileStatus.Type.USER) {
					s.setToDefault();
				} else {
					s.setDoIt(true);
				}
			}
		}
		this.treeViewer.refresh();
	}

	private void clear() {
		for (FileStatus s : this.fileStatus) {
			if (!checkFile(false, s)) {
				return;
			}
		}
		for (FileStatus s : this.fileStatus) {
			s.setDoIt(false);
		}
		this.treeViewer.refresh();
	}

	/**
	 * This returns an array of strings for the currently checked files.
	 * 
	 * @return list of filenames to be generated
	 */
	public String[] getFilesToGenerate() {
		List<String> retVal = new ArrayList<String>();
		for (FileStatus s : this.fileStatus) {
			if (s.isDoIt()) {
				retVal.add(s.getFilename());
			}
		}
		return retVal.toArray(new String[retVal.size()]);
	}
}
