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
package gov.redhawk.ide.snapshot.ui;

import gov.redhawk.ide.snapshot.writer.IDataWriterDesc;
import gov.redhawk.ui.util.EmptyStringToNullConverter;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.CreateFolderAction;
import org.eclipse.ui.actions.DeleteResourceAction;
import org.eclipse.ui.actions.NewProjectAction;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.actions.RenameResourceAction;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class SnapshotWizardPage extends WizardPage {

	private static final int UPDATE_DELAY_MS = 100;

	// === BEGIN: dialog page settings storage keys ===
	private static final String SS_FILE_TYPE_ID = "outFileType_id";
	private static final String SS_SAVE_TO_WORKSPACE = "saveToWorkspace";
	private static final String SS_CONFIRM_OVERWRITE = "confirmOverwrite";
	private static final String SS_FILESYSTEM_FILENAME = "filename";
	private static final String SS_WORKSPACE_FILENAME = "workspaceFilename";
	// === END: dialog page settings storage keys ===

	private final SnapshotSettings settings = new SnapshotSettings();
	private DataBindingContext context;
	private WizardPageSupport support;
	private IDialogSettings pageSettings;

	private StackLayout fileFinderLayout;

	private Composite searchWorkbench;

	private Composite searchFileSystem;

	private Group fileFinder;

	private Button workspaceCheck;

	public SnapshotWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		context = new DataBindingContext();
	}

	public DataBindingContext getContext() {
		return context;
	}

	@Override
	public void createControl(Composite main) {
		setupDialogSettingsStorage(); // for saving wizard page settings

		final Composite parent = new Composite(main, SWT.None);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		createOutputControls(parent);

		setPageComplete(false);
		setPageSupport(WizardPageSupport.create(this, context));
		setControl(parent);
	}

	protected void setPageSupport(WizardPageSupport support) {
		this.support = support;
	}

	public WizardPageSupport getPageSupport() {
		return support;
	}

	protected void createOutputControls(final Composite parent) {
		Label label;
		// Add Label and combo box to select file type
		label = new Label(parent, SWT.None);
		label.setText("File Type:");
		ComboViewer fileTypeCombo = new ComboViewer(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		fileTypeCombo.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		fileTypeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IDataWriterDesc) element).getName();
			}
		});
		fileTypeCombo.setContentProvider(ArrayContentProvider.getInstance()); // ArrayContentProvider does not store any state, therefore can re-use instances
		IDataWriterDesc[] input = SnapshotActivator.getDataReceiverRegistry().getRecieverDescs();
		fileTypeCombo.setInput(input);
		fileTypeCombo.setSorter(new ViewerSorter()); // sort combo items alphabetically (this selects last item?)
		context.bindValue(ViewerProperties.singleSelection().observe(fileTypeCombo), BeansObservables.observeValue(settings, "dataWriter"));
		if (input.length > 0) {
			fileTypeCombo.setSelection(new StructuredSelection(fileTypeCombo.getElementAt(0))); // select first sorted element
		}

		// add check box to see if the user wants to save to their workspace
		workspaceCheck = new Button(parent, SWT.CHECK);
		workspaceCheck.setText("Save to Workspace");

		// add check box to see if user wants to confirm overwrite of existing file(s)
		final Button confirmOverwrite = new Button(parent, SWT.CHECK);
		confirmOverwrite.setText("Confirm overwrite");
		context.bindValue(WidgetProperties.selection().observe(confirmOverwrite), BeansObservables.observeValue(settings, "confirmOverwrite"));

		// region to hold the different pages for saving to the workspace or the file system
		fileFinder = new Group(parent, SWT.SHADOW_ETCHED_IN);
		fileFinder.setText("Save to");
		fileFinder.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());
		fileFinderLayout = new StackLayout();
		fileFinderLayout.marginHeight = 5;
		fileFinderLayout.marginWidth = 5;
		fileFinder.setLayout(fileFinderLayout);

		// the different pages: search file system, search workspace
		searchFileSystem = makeFileSystemSave(fileFinder);
		searchFileSystem.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());
		searchWorkbench = makeWorkbenchTree(fileFinder);
		searchWorkbench.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());

		// This binding must be defined after all controls have been configured, because its update strategy
		// implementation calls setSaveLocation(), which depends on the controls being already configured
		context.bindValue(WidgetProperties.selection().observe(workspaceCheck), BeansObservables.observeValue(settings, "saveToWorkspace"),
			createWsCheckUpdateStrategy(), createWsCheckUpdateStrategy());

		restoreWidgetValues(settings);
		
		// determining which page starts on top
		setSaveLocationComposite(workspaceCheck.getSelection(), true);

		// switching pages
		workspaceCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSaveLocationComposite(workspaceCheck.getSelection(), true);
			}
		});

	}

	private void setSaveLocationComposite(final boolean workspace, final boolean updateModel) {
		if (workspace) {
			fileFinderLayout.topControl = searchWorkbench;
			fileFinder.layout();
		} else {
			fileFinderLayout.topControl = searchFileSystem;
			fileFinder.layout();
		}
		if (updateModel) {
			context.updateModels(); // <-- this will update filename validators
		}
	}

	private UpdateValueStrategy createFilenameT2MUpdateStrategy(final String fieldName, final boolean onWorkspace) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setConverter(new EmptyStringToNullConverter());
		updateValueStrategy.setAfterConvertValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (onWorkspace == settings.isSaveToWorkspace()) {
					if (value instanceof String) {
						return ValidationStatus.ok();
					}
					return ValidationStatus.error(fieldName + " must be specified.");
				}
				return ValidationStatus.ok();
			}
		});

		return updateValueStrategy;
	}

	private UpdateValueStrategy createWsCheckUpdateStrategy() {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy() {
			@Override
			public Object convert(Object value) {
				if (value instanceof Boolean) {
					setSaveLocationComposite((Boolean) value, false);
				}
				return super.convert(value);
			}
		};
		return updateValueStrategy;
	}

	private UpdateValueStrategy createWorkspaceTreeT2MUpdateStrategy(final TreeViewer viewer) {
		UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
		updateValueStrategy.setAfterConvertValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				// Only do this validation if the workspaceCheck button returns true
				if (workspaceCheck.getSelection()) {
					if (((WorkbenchContentProvider) viewer.getContentProvider()).getElements(ResourcesPlugin.getWorkspace().getRoot()).length == 0) {
						return ValidationStatus.error("A workspace project must be created.");
					}
					if (((IStructuredSelection) viewer.getSelection()).isEmpty()) {
						return ValidationStatus.error("A workspace project must be selected.");
					}
				}
				return ValidationStatus.ok();
			}
		});

		return updateValueStrategy;
	}

	private Composite makeFileSystemSave(Composite parent) {
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		// the label and text field for files from the file system
		Label fileNameLbl = new Label(comp, SWT.None);
		fileNameLbl.setText("File Name:");
		final Text fileNameTxt = new Text(comp, SWT.BORDER);
		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());

		UpdateValueStrategy fnameTargetToModelValidator = createFilenameT2MUpdateStrategy("File Name", false);
		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(UPDATE_DELAY_MS, fileNameTxt), BeansObservables.observeValue(settings, "fileName"),
			fnameTargetToModelValidator, null);

		// the browse button
		Button button = new Button(comp, SWT.PUSH);
		button.setText("Browse");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				String fileName = dialog.open();
				if (fileName != null) {
					fileNameTxt.setText(fileName);
				}
			}
		});
		return comp;
	}

	private Composite makeWorkbenchTree(Composite parent) {
		Composite comp = new Composite(parent, SWT.None);
		comp.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		// create label and text field for inputing the file name
		Label fileNameLbl = new Label(comp, SWT.None);
		fileNameLbl.setText("Workspace File Name:");
		final Text fileNameTxt = new Text(comp, SWT.BORDER);

		UpdateValueStrategy wkspFnameTargetToModelValidator = createFilenameT2MUpdateStrategy("Workspace File Name", true);
		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(UPDATE_DELAY_MS, fileNameTxt), BeansObservables.observeValue(settings, "path"),
			wkspFnameTargetToModelValidator, null);

		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		// create tree with which to navigate the workbench file system
		final TreeViewer workbenchTree = new TreeViewer(comp, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		WorkbenchContentProvider contentProvider = new WorkbenchContentProvider();
		workbenchTree.setContentProvider(contentProvider);
		final WorkbenchLabelProvider labels = new WorkbenchLabelProvider();
		workbenchTree.setLabelProvider(labels);
		workbenchTree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		workbenchTree.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 150).create());

		UpdateValueStrategy wkspTreeTargetToModelValidator = createWorkspaceTreeT2MUpdateStrategy(workbenchTree);
		context.bindValue(ViewerProperties.singleSelection().observe(workbenchTree), BeansObservables.observeValue(settings, "resource"),
			wkspTreeTargetToModelValidator, null);
		Object[] elements = contentProvider.getElements(ResourcesPlugin.getWorkspace().getRoot());
		if (elements.length > 0) {
			workbenchTree.setSelection(new StructuredSelection(elements[0]));
		}

		workbenchTree.addFilter(new ViewerFilter() {

			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof IResource) {
					IResource resource = (IResource) element;
					if (resource.getName().isEmpty() || resource.getName().charAt(0) == '.') {
						return false;
					}
				}
				return true;
			}
		});

		IShellProvider shellProvider = new IShellProvider() {

			@Override
			public Shell getShell() {
				return SnapshotWizardPage.this.getShell();
			}
		};
		final CreateFolderAction folderAction = new CreateFolderAction(shellProvider);
		final DeleteResourceAction deleteAction = new DeleteResourceAction(shellProvider);
		final RefreshAction refreshAction = new RefreshAction(shellProvider);
		final RenameResourceAction renamAction = new RenameResourceAction(shellProvider);
		final NewProjectAction projectAction = new NewProjectAction();
		workbenchTree.addSelectionChangedListener(folderAction);
		workbenchTree.addSelectionChangedListener(deleteAction);
		workbenchTree.addSelectionChangedListener(refreshAction);
		workbenchTree.addSelectionChangedListener(renamAction);

		// the menu for the tree items
		MenuManager contextMenuManager = new MenuManager();
		contextMenuManager.setRemoveAllWhenShown(true);
		contextMenuManager.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				manager.add(projectAction);
				manager.add(folderAction);
				manager.add(renamAction);
				manager.add(refreshAction);
				manager.add(deleteAction);
			}
		});
		Menu menu = contextMenuManager.createContextMenu(workbenchTree.getControl());
		workbenchTree.getControl().setMenu(menu);

		return comp;
	}

	@Override
	public void dispose() {
		if (support != null) {
			support.dispose();
			support = null;
		}
		if (context != null) {
			context.dispose();
			context = null;
		}
		super.dispose();
	}

	public SnapshotSettings getSettings() {
		return settings;
	}

	public void setupDialogSettingsStorage() {
		pageSettings = getDialogSettings().getSection(getName());
		if (pageSettings == null) {
			pageSettings = getDialogSettings().addNewSection(getName());
		}
	}

	protected IDialogSettings getPageSettingsSection() {
		return pageSettings;
	}

	protected void saveWidgetValues(SnapshotSettings ss) {
		pageSettings.put(SS_FILE_TYPE_ID, ss.getDataWriter().getID());
		pageSettings.put(SS_SAVE_TO_WORKSPACE, ss.isSaveToWorkspace());
		pageSettings.put(SS_CONFIRM_OVERWRITE, ss.isConfirmOverwrite());
		pageSettings.put(SS_FILESYSTEM_FILENAME, ss.getFileName());
		pageSettings.put(SS_WORKSPACE_FILENAME, ss.getPath());
	}

	private void restoreWidgetValues(SnapshotSettings sss) {
		String tmp;
		tmp = pageSettings.get(SS_FILE_TYPE_ID);
		if (tmp != null) {
			IDataWriterDesc dwd = SnapshotActivator.getDataReceiverRegistry().getRecieverDesc(tmp);
			if (dwd != null) {
				sss.setDataWriter(dwd);
			}
		}
		tmp = pageSettings.get(SS_SAVE_TO_WORKSPACE);
		if (tmp != null) {
			sss.setSaveToWorkspace(Boolean.valueOf(tmp));
		}
		tmp = pageSettings.get(SS_CONFIRM_OVERWRITE);
		if (tmp != null) {
			sss.setConfirmOverwrite(Boolean.valueOf(tmp));
		}
		sss.setFileName(pageSettings.get(SS_FILESYSTEM_FILENAME));
		sss.setPath(pageSettings.get(SS_WORKSPACE_FILENAME));
	}
}
