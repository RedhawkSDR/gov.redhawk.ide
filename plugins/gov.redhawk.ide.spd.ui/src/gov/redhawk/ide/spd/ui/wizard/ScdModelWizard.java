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
package gov.redhawk.ide.spd.ui.wizard;

import gov.redhawk.ide.spd.generator.newcomponent.ScdFileTemplate;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ui.parts.WizardNewFileCreationPage;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;

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
public class ScdModelWizard extends Wizard implements INewWizard {

	/**
	 * This caches an instance of the model package.
	 */
	protected ScdPackage scdPackage = ScdPackage.eINSTANCE;

	/**
	 * This caches an instance of the model factory.
	 */
	protected ScdFactory scdFactory = this.scdPackage.getScdFactory();

	/**
	 * This is the file creation page.
	 */
	protected ScdModelWizardNewFileCreationPage newFileCreationPage;

	/**
	 * Remember the selection during initialization for populating the default
	 * container.
	 */
	protected IStructuredSelection selection;

	/**
	 * Remember the workbench during initialization.
	 */
	protected IWorkbench workbench;

	/**
	 * Caches the names of the features representing global elements.
	 */
	protected List<String> initialObjectNames;

	private IFile modelFile;

	private final IProject project;

	public ScdModelWizard(final IProject project) {
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
		return this.scdFactory.createSoftwareComponent();
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

						final String scd = new ScdFileTemplate().generate(null);

						final IFile scdFile = ScdModelWizard.this.modelFile;
						if (scdFile.exists()) {
							throw new CoreException(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "File "
							        + scdFile.getName() + " already exists.", null));
						}

						try {
							scdFile.create(new ByteArrayInputStream(scd.getBytes("UTF-8")), true, progressMonitor);
						} catch (final UnsupportedEncodingException e) {
							throw new CoreException(new Status(IStatus.ERROR, ComponentUiPlugin.getPluginId(),
							        "Internal Error", e));
						}
					} catch (final Exception exception) {
						ComponentUiPlugin.logException(exception);
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
			ComponentUiPlugin.logException(exception);
			return false;
		}
	}

	/**
	 * This is the one page of the wizard. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * 
	 */
	public class ScdModelWizardNewFileCreationPage extends WizardNewFileCreationPage {
		/**
		 * Pass in the selection. <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * 
		 */
		public ScdModelWizardNewFileCreationPage(final String pageId, final IStructuredSelection selection) {
			super(pageId, selection, ScdModelWizard.this.project);
		}

		/**
		 * The framework calls this to see if the file is correct.
		 */
		@Override
		protected boolean validatePage() {
			if (super.validatePage()) {
				if (!getFileName().endsWith(ScdPackage.FILE_EXTENSION)) {
					setErrorMessage("Must end with \"" + ScdPackage.FILE_EXTENSION + "\"");
					return false;
				}
				return true;
			}
			return false;
		}

		/**
		 * <!-- begin-user-doc --> <!-- end-user-doc -->
		 * 
		 * 
		 */
		public IFile getModelFile() {
			return ResourcesPlugin.getWorkspace().getRoot().getFile(getContainerFullPath().append(getFileName()));
		}
	}

	/**
	 * The framework calls this to create the contents of the wizard. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * 
	 */
	@Override
	public void addPages() {
		// Create a page, set the title, and the initial model file name.
		//
		this.newFileCreationPage = new ScdModelWizardNewFileCreationPage("Whatever", this.selection);
		this.newFileCreationPage.setTitle("Scd File");
		this.newFileCreationPage.setDescription("Create a new Scd file");
		this.newFileCreationPage.setFileName("My.scd.xml");
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
					final String defaultModelFilenameExtension = ScdPackage.FILE_EXTENSION;
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
