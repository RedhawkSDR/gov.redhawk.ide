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
public class NonEclipseImportWizard extends Wizard implements IImportWizard {

	private NonEclipseImportWizardPage importPage = null;

	public NonEclipseImportWizard() {
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
		importPage  = new NonEclipseImportWizardPage("Import Non-Eclipse Based REDHAWK Project");
		this.addPage(importPage);
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
