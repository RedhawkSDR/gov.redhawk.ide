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

package gov.redhawk.ide.prf.ui.wizard;

import gov.redhawk.ide.prf.generator.PrfFileTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * This is a simple wizard for creating a new model file.
 */
public class PrfModelWizard extends Wizard implements INewWizard {

	/**
	 * This is the file creation page.
	 */
	private PrfModelWizardNewFileCreationPage newFileCreationPage;

	/**
	 * Remember the selection during initialization for populating the default
	 * container.
	 */
	private IStructuredSelection selection;

	/**
	 * Remember the workbench during initialization.
	 */
	private IWorkbench workbench;

	private IFile modelFile;

	private String initialValue;

	public PrfModelWizard(final IProject project) {
		
	}

	/**
	 * This just records the information.
	 */
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.workbench = workbench;
		this.selection = selection;
		setWindowTitle("New");
		// TODO Add Wizard banner
		// setDefaultPageImageDescriptor(PropertyUiPlugin.getDefault().getImageRegistry().getDescriptor(
		// PropertyUiPlugin.IMG_PRF_NEW_WIZBAN));
	}

	/**
	 * Create a new model.
	 */
	protected EObject createInitialModel() {
		return PrfFactory.eINSTANCE.createProperties();
	}

	/**
	 * Do the work after everything is specified.
	 */
	@Override
	public boolean performFinish() {
		modelFile = newFileCreationPage.createNewFile();
		if (modelFile == null) {
			return false;
		}

		BasicNewResourceWizard.selectAndReveal(modelFile, workbench.getActiveWorkbenchWindow());

		return true;
	}

	/**
	 * This is the one page of the wizard.
	 */
	public class PrfModelWizardNewFileCreationPage extends WizardNewFileCreationPage {
		/**
		 * Pass in the selection. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * @generated
		 */
		public PrfModelWizardNewFileCreationPage(final String pageId, final IStructuredSelection selection) {
			super(pageId, selection);
		}

		/**
		 * The framework calls this to see if the file is correct.
		 */
		@Override
		protected boolean validatePage() {
			if (super.validatePage()) {
				if (!getFileName().endsWith(PrfPackage.FILE_EXTENSION)) {
					setErrorMessage("Must end with \"" + PrfPackage.FILE_EXTENSION + "\"");
					return false;
				}
				return true;
			}
			return false;
		}

		@Override
		protected InputStream getInitialContents() {
			final String prf = new PrfFileTemplate().generate(null);
			return new ByteArrayInputStream(prf.getBytes());
		}
		
		public IFile getModelFile() {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
		}
	}

	/**
	 * The framework calls this to create the contents of the wizard.
	 */
	@Override
	public void addPages() {
		// Create a page, set the title, and the initial model file name.
		//
		this.newFileCreationPage = new PrfModelWizardNewFileCreationPage(PrfModelWizardNewFileCreationPage.class.getCanonicalName(), this.selection);
		this.newFileCreationPage.setTitle("PRF File");
		this.newFileCreationPage.setDescription("Create a new PRF file");
		addPage(this.newFileCreationPage);

		// Try and get the resource selection to determine a current directory
		// for the file dialog.
		//
		String modelFilename = "My.prf.xml";
		if (this.initialValue != null && this.initialValue.length() > 0) {
			modelFilename = this.initialValue;
		} else if (this.selection != null && !this.selection.isEmpty()) {
			// Get the resource...
			//
			final Object selectedElement = this.selection.iterator().next();
			if (selectedElement instanceof IResource) {
				// Get the resource parent, if its a file.
				//
				IResource selectedResource = (IResource) selectedElement;
				if (selectedResource.getType() == IResource.FILE) {
					selectedResource = selectedResource.getParent();
				}

				// This gives us a directory...
				//
				if (selectedResource instanceof IFolder || selectedResource instanceof IProject) {
					// Set this for the container.
					//
					this.newFileCreationPage.setContainerFullPath(selectedResource.getFullPath());

					// Make up a unique new name here.
					//
					final String defaultModelBaseFilename = selectedResource.getProject().getName();
					final String defaultModelFilenameExtension = PrfPackage.FILE_EXTENSION;
					modelFilename = defaultModelBaseFilename + defaultModelFilenameExtension;
					for (int i = 1; ((IContainer) selectedResource).findMember(modelFilename) != null; ++i) {
						modelFilename = defaultModelBaseFilename + i + defaultModelFilenameExtension;
					}
				}
			}
		}
		this.newFileCreationPage.setFileName(modelFilename);
	}

	/**
	 * @return the modelFile
	 */
	public IFile getModelFile() {
		return this.modelFile;
	}

	/**
	 * @since 3.0
	 * @param initialValue
	 */
	public void setInitialValue(String initialValue) {
		this.initialValue = initialValue;
		if (newFileCreationPage != null) {
			newFileCreationPage.setFileName(initialValue);
		}
	}

}
