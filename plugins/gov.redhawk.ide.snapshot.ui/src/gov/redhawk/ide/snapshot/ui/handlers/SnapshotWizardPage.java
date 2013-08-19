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
package gov.redhawk.ide.snapshot.ui.handlers;

import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;

import java.io.File;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin; //added by Ryan on 6-18
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo; //added by Ryan on 6-18
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class SnapshotWizardPage extends WizardPage {

	private final SnapshotSettings settings = new SnapshotSettings();
	private DataBindingContext context;
	private WizardPageSupport support;
	private boolean dataProvided = false;

	public SnapshotWizardPage(String pageName, ImageDescriptor titleImage) {
		super(pageName, "Snapshot", titleImage);
		setDescription("Write a stream of samples from the port to the given file.");
	}

	public SnapshotWizardPage(String pageName, ImageDescriptor titleImage, String[] supportedTypes, String[] processMethods) {
		this(pageName, titleImage);
		this.settings.setSupportedTypes(supportedTypes);
		this.settings.setCaptureTypes(processMethods);
	}

	public SnapshotWizardPage(String pageName, ImageDescriptor titleImage, String[] supportedTypes, String[] processMethods, long samples) {
		this(pageName, titleImage, supportedTypes, processMethods);
		this.settings.setSamples((double) samples);
		this.settings.setCaptureType(CaptureMethod.NUMBER.toString());
		this.dataProvided = true;
	}

	@Override
	public void createControl(Composite main) {
		final Composite parent = new Composite(main, SWT.None);
		parent.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		context = new DataBindingContext();

		//Add Combo Box and text field to input how to capture samples
		Label label;
		final Combo captureCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		captureCombo.setItems(settings.getCaptureTypes());
		context.bindValue(WidgetProperties.text().observeDelayed(500, captureCombo), PojoObservables.observeValue(settings, "captureType"));

		final Text samplesTxt = new Text(parent, SWT.BORDER);
		samplesTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());
		//ensure that invalid number of samples are caught and displayed
		UpdateValueStrategy validateSamples = new UpdateValueStrategy();
		validateSamples.setBeforeSetValidator(new IValidator() {
			@Override
			public IStatus validate(Object value) {
				if (value instanceof Double) {
					CaptureMethod method = CaptureMethod.stringToValue(settings.getCaptureType());
					if (method == CaptureMethod.INDEFINITELY) {
						return ValidationStatus.ok();
					}
					if (((Double) value).doubleValue() <= 0) {
						return ValidationStatus.error(settings.getCaptureType() + " must be greater than 0");
					}
					if (method == CaptureMethod.NUMBER) {
						double val = ((Double) value).doubleValue();
						if (val > Long.MAX_VALUE) {
							return ValidationStatus.error(settings.getCaptureType() + " must less than or equal to " + Long.MAX_VALUE);
						}
						if ((val - (long) val) > 0) {
							return ValidationStatus.error(settings.getCaptureType() + " must be a whole number");
						}
						return ValidationStatus.ok();
					} else {
						return ValidationStatus.ok();
					}
				} else {
					return ValidationStatus.error("The Number of Samples must be a positive number");
				}
			}
		});
		final Binding samplesBinding = context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, samplesTxt),
			PojoObservables.observeValue(settings, "samples"), validateSamples, null);

		if (this.dataProvided) {
			samplesTxt.setEnabled(false);
			captureCombo.setEnabled(false);
		}
		final Label unitsLabel = new Label(parent, SWT.None);
		unitsLabel.setText("");
		GridData unitsLayout = new GridData();
		unitsLayout.widthHint = 20;
		unitsLabel.setLayoutData(unitsLayout);
		//update validator, set text field enable, and units as needed
		captureCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				settings.setCaptureType(captureCombo.getText());
				CaptureMethod method = CaptureMethod.stringToValue(captureCombo.getText());
				if (method == CaptureMethod.INDEFINITELY) {
					samplesTxt.setText("1");
					unitsLabel.setText("");
					samplesBinding.updateTargetToModel();
					samplesTxt.setEnabled(false);
					return;
				} else if (method == CaptureMethod.CLOCK_TIME || method == CaptureMethod.SAMPLE_TIME) {
					unitsLabel.setText("(s)");
					samplesTxt.setEnabled(true);
					samplesBinding.updateTargetToModel();
				} else {
					unitsLabel.setText("");
					samplesTxt.setEnabled(true);
					samplesBinding.updateTargetToModel();
				}
			}
		});

		//Add Label and combo box to select file type
		label = new Label(parent, SWT.None);
		label.setText("File Type:");
		Combo fileTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.SIMPLE);
		fileTypeCombo.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		fileTypeCombo.setItems(settings.getSupportedTypes());
		context.bindValue(WidgetProperties.text().observeDelayed(500, fileTypeCombo), PojoObservables.observeValue(settings, "fileType"));

		//add check box to see if the user wants to save to their workspace
		final Button workspaceCheck = new Button(parent, SWT.CHECK);
		workspaceCheck.setText("Save to Workspace");
		context.bindValue(WidgetProperties.selection().observeDelayed(500, workspaceCheck), PojoObservables.observeValue(settings, "saveToWorkspace"));

		// add check box to see if user wants to confirm overwrite of existing file(s)
		final Button confirmOverwrite = new Button(parent, SWT.CHECK);
		confirmOverwrite.setText("Confirm overwrite");
		context.bindValue(WidgetProperties.selection().observeDelayed(500, confirmOverwrite), PojoObservables.observeValue(settings, "confirmOverwrite"));
		
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
		setPageComplete(false);
		support = WizardPageSupport.create(this, context);

		setControl(parent);
	}

	private Composite makeFileSystemSave(Composite parent) {
		Composite searchFileSystem = new Composite(parent, SWT.None);
		searchFileSystem.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		//the label and text field for files from the file system
		Label fileNameLbl = new Label(searchFileSystem, SWT.None);
		fileNameLbl.setText("File Name:");
		final Text fileNameTxt = new Text(searchFileSystem, SWT.BORDER);
		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(1, 1).create());

		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, fileNameTxt), PojoObservables.observeValue(settings, "fileName"));
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

		context.bindValue(WidgetProperties.text(SWT.Modify).observeDelayed(500, fileNameTxt), PojoObservables.observeValue(settings, "fileName"));
		fileNameTxt.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());

		//create tree with which to navigate the workbench file system
		final TreeViewer workbenchTree = new TreeViewer(searchWorkbench, SWT.BORDER | SWT.V_SCROLL);
		workbenchTree.setContentProvider(new BaseWorkbenchContentProvider());
		final WorkbenchLabelProvider labels = new WorkbenchLabelProvider();
		workbenchTree.setLabelProvider(labels);
		workbenchTree.setInput(ResourcesPlugin.getWorkspace().getRoot());
		workbenchTree.getTree().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileNameTxt.setText(proccessSelectionString(workbenchTree.getSelection().toString()));
			}
		});
		workbenchTree.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(3, 1).hint(SWT.DEFAULT, 150).create());

		//the menu for the tree items
		//getSite().set
		Menu treeMenu = new Menu(workbenchTree.getControl().getShell(), SWT.POP_UP);
		MenuItem newFolderItem = new MenuItem(treeMenu, SWT.PUSH);
		newFolderItem.setText("New Folder");
		MenuItem deleteFileItem = new MenuItem(treeMenu, SWT.PUSH);
		deleteFileItem.setText("delete");

		workbenchTree.getTree().setMenu(treeMenu);

		//allow the user to right click and make a folder
		newFolderItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (settings.getFilePath() == "" || settings.getFilePath() == null) {
					fileNameTxt.setText(proccessSelectionString(workbenchTree.getSelection().toString()));
				}
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(settings.getFilePath());
				//get the absolute file path to the file to be deleted
				File filePathAbs = resource.getLocation().toFile();
				InputDialog newFolder = new InputDialog(workbenchTree.getTree().getShell(), "New Folder",
					"Please enter the name of the folder to be placed in\n" + filePathAbs.getAbsolutePath(), "", null);
				int result = newFolder.open();
				if (result == 0) {
					filePathAbs = new File(filePathAbs, newFolder.getValue());
					if (filePathAbs.isDirectory()) {
						MessageDialog.openError(workbenchTree.getTree().getShell(), "Error", "The folder already exists");
					} else {
						IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();
						IResource parentProj = (resource.getProject());
						ISchedulingRule rule = factory.createRule(parentProj);
						boolean worked;
						try {
							Job.getJobManager().beginRule(rule, null);
							worked = filePathAbs.mkdir();
						} finally {
							Job.getJobManager().endRule(rule);
						}
						if (!worked) {
							MessageDialog.openError(workbenchTree.getTree().getShell(), "Error", "The folder could not be made");
						} else {
							try {
								parentProj.refreshLocal(IResource.DEPTH_INFINITE, null);
								workbenchTree.refresh(true);
							} catch (CoreException ce) {
								//PASS
							}
						}
					}
				}
			}
		});

		//allow the user to right click and delete an item
		deleteFileItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MessageBox warning = new MessageBox(workbenchTree.getControl().getShell(), SWT.YES | SWT.NO);
				if (settings.getFilePath() == null || settings.getFilePath() == "") {
					fileNameTxt.setText(proccessSelectionString(workbenchTree.getSelection().toString()));
				}
				//get the absolute file path to the file to be deleted
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(settings.getFilePath());
				String temp = workbenchTree.getSelection().toString();
				temp = temp.substring(1, temp.length() - 1);
				if ((settings.getFileName() != null || settings.getFileName() != "") && temp.endsWith(settings.getFileName())) {
					if (resource instanceof IContainer) {
						resource = ((IContainer) resource).findMember(settings.getFileName());
					}
				}

				//double check
				warning.setMessage("Are you certian you want to delete\n" + resource.getLocation().toString());
				int result = warning.open();
				if (result != SWT.YES) {
					return;
				}

				//TODO separate model and view here

				//make the rule for the overall project or the root
				IResourceRuleFactory factory = ResourcesPlugin.getWorkspace().getRuleFactory();
				ISchedulingRule rule = factory.createRule(resource.getParent());
				//attempt to delete file and notify user if failed
				boolean worked = true;
				try {
					Job.getJobManager().beginRule(rule, null);
					resource.delete(IResource.FORCE | IResource.KEEP_HISTORY | IResource.ALWAYS_DELETE_PROJECT_CONTENT, null);
					resource.getParent().refreshLocal(IResource.DEPTH_INFINITE, null);
				} catch (CoreException cE) {
					worked = false;
				} finally {
					Job.getJobManager().endRule(rule);
				}
				if (!worked) {
					MessageBox failedMessage = new MessageBox(workbenchTree.getControl().getShell(), SWT.ERROR | SWT.OK);
					failedMessage.setMessage("Could not delete file or directory");
					failedMessage.open();
				}

				workbenchTree.refresh(true);
			}
		});

		return searchWorkbench;
	}

	private String proccessSelectionString(String selection) {
		//get the selected path and split it into its separate nodes
		String path = selection;
		String output = "";
		path = path.substring(0, path.length() - 1);
		String[] pathArray = path.split("/");

		//if the selection was not a valid tree node, do nothing
		if (pathArray.length < 2) {
			return "";
		}

		//compile the file path relative to the workspace
		File filePath = new File(pathArray[1]);
		for (int i = 2; i < pathArray.length; i++) {
			filePath = new File(filePath, pathArray[i]);
		}

		//check if the selected node is a file or directory and update the text field
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(filePath.getPath());
		if (resource.getType() == IResource.FILE) {
			output = resource.getName();
			path = filePath.getParentFile().getPath();
		} else {
			path = filePath.getPath();
		}
		settings.setFilePath(path);
		return output;
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
