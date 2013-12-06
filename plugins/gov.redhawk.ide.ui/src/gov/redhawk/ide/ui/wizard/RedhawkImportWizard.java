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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @since 9.1
 */
public class RedhawkImportWizard extends Wizard implements IImportWizard {

	private RedhawkImportWizardPage1 importPage = null;
	private RedhawkImportWizardPage2 detailsPage = null;

	public RedhawkImportWizard() {
		this.setWindowTitle("Import");
		this.setHelpAvailable(false);
	}

	@Override
	public boolean needsProgressMonitor() {
		return true;
	}

	@Override
	public void addPages() {
		super.addPages();
		importPage = new RedhawkImportWizardPage1("Import Non-Eclipse Based REDHAWK Project");
		detailsPage = new RedhawkImportWizardPage2("Import Non-Eclipse Based REDHAWK Project");
		this.addPage(importPage);
		this.addPage(detailsPage);
	}

	@Override
	public boolean performFinish() {
		return importPage.createProjects();
	}

	@Override
	public boolean performCancel() {
		importPage.performCancel();
		return true;
	}

	@Override
	public boolean canFinish() {
		return super.canFinish();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}
}
