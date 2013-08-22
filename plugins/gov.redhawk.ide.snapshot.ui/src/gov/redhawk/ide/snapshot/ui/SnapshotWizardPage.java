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

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin; //added by Ryan on 6-18
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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

//added by Ryan on 6-18

public class SnapshotWizardPage extends WizardPage {

	private final SnapshotSettings settings = new SnapshotSettings();
	private DataBindingContext context;
	private WizardPageSupport support;

	public SnapshotWizardPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
		context = new DataBindingContext();
	}

	public DataBindingContext getContext() {
		return context;
	}

	@Override
	public void createControl(Composite main) {
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
		//Add Label and combo box to select file type
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
		fileTypeCombo.setContentProvider(new ArrayContentProvider());
		IDataWriterDesc[] input = SnapshotActivator.getDataReceiverRegistry().getRecieverDescs();
		fileTypeCombo.setInput(input);
		context.bindValue(ViewerProperties.singleSelection().observe(fileTypeCombo), BeansObservables.observeValue(settings, "dataWriter"));
		if (input.length > 0) {
			fileTypeCombo.setSelection(new StructuredSelection(input[0]));
		}

		//add check box to see if the user wants to save to their workspace
		final Button workspaceCheck = new Button(parent, SWT.CHECK);
		workspaceCheck.setText("Save to Workspace");
		context.bindValue(WidgetProperties.selection().observe(workspaceCheck), BeansObservables.observeValue(settings, "saveToWorkspace"));

		// add check box to see if user wants to confirm overwrite of existing file(s)
		final Button confirmOverwrite = new Button(parent, SWT.CHECK);
		confirmOverwrite.setText("Confirm overwrite");
		context.bindValue(WidgetProperties.selection().observe(confirmOverwrite), BeansObservables.observeValue(settings, "confirmOverwrite"));

		//region to hold the different pages for saving to the workspace or the file system
		final Group fileFinder = new Group(parent, SWT.SHADOW_ETCHED_IN);
		fileFinder.setText("Save to");
		fileFinder.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());
		final StackLayout fileFinderLayout = new StackLayout();
		fileFinderLayout.marginHeight = 5;
		fileFinderLayout.marginWidth = 5;
		fileFinder.setLayout(fileFinderLayout);

		// the different pages: search file system, search workspace
		final Composite searchFileSystem = makeFileSystemSave(fileFinder);
		searchFileSystem.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());
		final Composite searchWorkbench = makeWorkbenchTree(fileFinder);
		searchWorkbench.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 2).create());

		//determining which page starts on top
		if (workspaceCheck.getSelection()) {
			fileFinderLayout.topControl = searchWorkbench;
			fileFinder.layout();
		} else {
			fileFinderLayout.topControl = searchFileSystem;
			fileFinder.layout();
		}

		//switching pages
		workspaceCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (workspaceCheck.getSelection()) {
					fileFinderLayout.topControl = searchWorkbench;
					fileFinder.layout();
				} else {
					fileFinderLayout.topControl = searchFileSystem;
					fileFinder.layout();
				}
			}
		});
	}

	private Composite makeFileSystemSave(Composite parent) {
		Composite searchFileSystem = new Composite(parent, SWT.None);
		searchFileSystem.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		//the label and text field for files from the file system
		Label fileNameLbl = new Label(searchFileSystem, SWT.None);
		fileNameLbl.setText("File Name:");
		final Text fileNameTxt = new Text(searchFileSystem, SWT.BORDER);
		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());

		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, fileNameTxt), BeansObservables.observeValue(settings, "fileName"));
		// the browse button
		Button button = new Button(searchFileSystem, SWT.PUSH);
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
		return searchFileSystem;
	}

	private Composite makeWorkbenchTree(Composite parent) {
		Composite searchWorkbench = new Composite(parent, SWT.None);
		searchWorkbench.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		//create label and text field for inputing the file name
		Label fileNameLbl = new Label(searchWorkbench, SWT.None);
		fileNameLbl.setText("File Name:");
		final Text fileNameTxt = new Text(searchWorkbench, SWT.BORDER);

		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, fileNameTxt), BeansObservables.observeValue(settings, "path"));
		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		//create tree with which to navigate the workbench file system
		final TreeViewer workbenchTree = new TreeViewer(searchWorkbench, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION);
		WorkbenchContentProvider contentProvider = new WorkbenchContentProvider();
		workbenchTree.setContentProvider(contentProvider);
		final WorkbenchLabelProvider labels = new WorkbenchLabelProvider();
		workbenchTree.setLabelProvider(labels);
		workbenchTree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		workbenchTree.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 150).create());

		context.bindValue(ViewerProperties.singleSelection().observe(workbenchTree), BeansObservables.observeValue(settings, "resource"));
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

		//the menu for the tree items
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

		return searchWorkbench;
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

}
