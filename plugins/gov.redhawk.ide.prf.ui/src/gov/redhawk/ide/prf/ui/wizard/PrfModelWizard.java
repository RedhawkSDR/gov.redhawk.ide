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
import gov.redhawk.ide.prf.ui.IdePrfUiPlugin;
import gov.redhawk.ui.parts.WizardNewFileCreationPage;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.part.ISetSelectionTarget;

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

	private final IProject project;

	public PrfModelWizard(final IProject project) {
		this.project = project;
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
		try {
			// Remember the file.
			//
			this.modelFile = this.newFileCreationPage.getModelFile();

			// Do the work within an operation.
			//
			final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
				@Override
				protected void execute(final IProgressMonitor progressMonitor) {
					try {
						final String prf = new PrfFileTemplate().generate(null);

						final IFile prfFile = PrfModelWizard.this.modelFile;
						if (prfFile.exists()) {
							throw new CoreException(new Status(IStatus.ERROR, IdePrfUiPlugin.PLUGIN_ID, "File " + prfFile.getName() + " already exists.", null));
						}

						try {
							prfFile.create(new ByteArrayInputStream(prf.getBytes("UTF-8")), true, progressMonitor);
						} catch (final UnsupportedEncodingException e) {
							throw new CoreException(new Status(IStatus.ERROR, IdePrfUiPlugin.getPluginId(), "Internal Error", e));
						}

					} catch (final Exception exception) {
						IdePrfUiPlugin.logException(exception);
					} finally {
						progressMonitor.done();
					}
				}
			};

			getContainer().run(false, false, operation);

			// Select the new file resource in the current view.
			//
			final IWorkbenchWindow workbenchWindow = this.workbench.getActiveWorkbenchWindow();
			final IWorkbenchPage page = workbenchWindow.getActivePage();
			final IWorkbenchPart activePart = page.getActivePart();
			if (activePart instanceof ISetSelectionTarget) {
				final ISelection targetSelection = new StructuredSelection(this.modelFile);
				getShell().getDisplay().asyncExec(new Runnable() {
					public void run() {
						((ISetSelectionTarget) activePart).selectReveal(targetSelection);
					}
				});
			}

			return true;
		} catch (final Exception exception) {
			IdePrfUiPlugin.logException(exception);
			return false;
		}
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
			super(pageId, selection, PrfModelWizard.this.project);
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
		this.newFileCreationPage.setFileName("My.prf.xml");
		addPage(this.newFileCreationPage);

		// Try and get the resource selection to determine a current directory
		// for the file dialog.
		//
		if (this.selection != null && !this.selection.isEmpty()) {
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
					String modelFilename = defaultModelBaseFilename + "." + defaultModelFilenameExtension;
					for (int i = 1; ((IContainer) selectedResource).findMember(modelFilename) != null; ++i) {
						modelFilename = defaultModelBaseFilename + i + "." + defaultModelFilenameExtension;
					}
					this.newFileCreationPage.setFileName(modelFilename);
				}
			}
		}
	}

	/**
	 * @return the modelFile
	 */
	public IFile getModelFile() {
		return this.modelFile;
	}

}
