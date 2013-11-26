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

import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @since 9.1
 */
public class RedhawkImportWizardPage2 extends WizardPage {
	//CHECKSTYLE:OFF
	TreeViewer viewer;
	TreeColumnLayout columnLayout;
	ProjectRecord[] projects = null;

	public RedhawkImportWizardPage2() {
		this("redhawkImportWizard"); 
	}

	protected RedhawkImportWizardPage2(String pageName) {
		super(pageName);
		// Users are not required to make edits using this page
		setTitle("Import Projects");
		setDescription("Some projects are missing files and may require additional information");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		Composite workArea = new Composite(parent, SWT.NULL);
		workArea.setLayout(new GridLayout());
		workArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		Label projectLabel = new Label(workArea, SWT.NONE);
		projectLabel.setText("Project Details: ");
		createProjectsTable(workArea);

		setControl(workArea);
	}

	private void createProjectsTable(Composite workArea) {
		viewer = new TreeViewer(workArea);
		Tree projectTree = viewer.getTree();
		projectTree.setHeaderVisible(true);
		projectTree.setLinesVisible(true);
		
		viewer.setContentProvider(new RedhawkImportContentProvider());
		viewer.setLabelProvider(new RedhawkImportLabelProvider());
		viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		createColumns(workArea, viewer);
	}

	private void createColumns(Composite workArea, TreeViewer viewer) {
		TreeColumn nameColumn = new TreeColumn(viewer.getTree(), SWT.NONE);
		nameColumn.setWidth(300);
		nameColumn.setText("Project: ");
		TreeColumn templateColumn = new TreeColumn(viewer.getTree(), SWT.NONE);
		templateColumn.setWidth(300);
		templateColumn.setText("Template: ");
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		
		// Get selected list of projects from the previous page
		RedhawkImportWizardPage1 previousPage = (RedhawkImportWizardPage1) getPreviousPage();
		projects = previousPage.getProjectRecords();
		viewer.setInput(projects);
	}

}
