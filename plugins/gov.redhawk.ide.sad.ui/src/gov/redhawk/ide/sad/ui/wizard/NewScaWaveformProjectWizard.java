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
package gov.redhawk.ide.sad.ui.wizard;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Wizard for creating new REDHAWK waveform projects.
 */
@SuppressWarnings("restriction")
public class NewScaWaveformProjectWizard extends Wizard implements INewWizard, IExecutableExtension {

	/** The configuration. */
	private IConfigurationElement fConfig;

	/** The assembly controller page. */
	private ScaWaveformProjectAssemblyControllerWizardPage waveformACpage;

	/** The component properties page. */
	private ScaWaveformProjectPropertiesWizardPage waveformPropertiesPage;

	private IFile openEditorOn;

	public NewScaWaveformProjectWizard() {
		this.setWindowTitle("New Waveform Project");
		this.setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		this.waveformPropertiesPage = new ScaWaveformProjectPropertiesWizardPage("");
		addPage(this.waveformPropertiesPage);
		this.waveformACpage = new ScaWaveformProjectAssemblyControllerWizardPage("");
		this.waveformACpage.setDescription("Add an existing Assembly Controller to your Waveform");
		addPage(this.waveformACpage);
	}

	@Override
	public boolean canFinish() {
		if (this.waveformPropertiesPage.isCreateNewResource()) {
			return super.canFinish();
		} else {
			return this.waveformPropertiesPage.isPageComplete();
		}
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		if (this.waveformPropertiesPage.isCreateNewResource()) {
			return super.getNextPage(page);
		}

		// Only 1 page
		return null;
	}

	@Override
	public void init(final IWorkbench arg0, final IStructuredSelection arg1) {
	}

	@Override
	public boolean performFinish() {
		if (!canFinish()) {
			return false;
		}

		try {
			// Find the working sets and where the new project should be located on disk
			final IWorkingSet[] workingSets = this.waveformPropertiesPage.getSelectedWorkingSets();
			final java.net.URI locationURI;
			if (this.waveformPropertiesPage.useDefaults()) {
				locationURI = null;
			} else {
				locationURI = this.waveformPropertiesPage.getLocationURI();
			}
			final String id = this.waveformPropertiesPage.getID();
			final boolean isCreateNewResource = this.waveformPropertiesPage.isCreateNewResource();
			final IPath existingSadPath = this.waveformPropertiesPage.getExistingResourcePath();
			final String projectName = this.waveformPropertiesPage.getProjectName();
			final SoftPkg assemblyController = this.waveformACpage.getAssemblyController();

			final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException {
					try {
						final SubMonitor progress = SubMonitor.convert(monitor, 3);

						// Create an empty project
						final IProject project = WaveformProjectCreator.createEmptyProject(projectName, locationURI, progress.newChild(1));
						if (workingSets.length > 0) {
							PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
						}

						// If we're creating a new waveform (vs importing one)
						if (isCreateNewResource) {
							// Create the XML files
							NewScaWaveformProjectWizard.this.openEditorOn = WaveformProjectCreator.createWaveformFiles(project, id, assemblyController,
								progress.newChild(1));
						} else {
							openEditorOn = project.getFile(project.getName() + SadPackage.FILE_EXTENSION);
							ProjectCreator.importFile(project, openEditorOn, existingSadPath, progress.newChild(1));
							ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
							URI uri = URI.createPlatformResourceURI(openEditorOn.getFullPath().toString(), true).appendFragment(SoftwareAssembly.EOBJECT_PATH);
							SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(resourceSet.getResource(uri, true));
							sad.setId(id);
							sad.setName(project.getName());
							try {
								sad.eResource().save(null);
							} catch (IOException e) {
								throw new CoreException(new Status(IStatus.ERROR, SadUiActivator.PLUGIN_ID, "Failed to modify SAD File."));
							}
							openEditorOn.refreshLocal(IResource.DEPTH_ONE, null);
						}
					} finally {
						monitor.done();
					}
				}
			};
			getContainer().run(false, false, op);
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ((this.openEditorOn != null) && this.openEditorOn.exists()) {
				IDE.openEditor(activePage, this.openEditorOn, true);
			}

			BasicNewProjectResourceWizard.updatePerspective(this.fConfig);
		} catch (final InvocationTargetException x) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.PLUGIN_ID, x.getCause().getMessage(), x.getCause()),
				StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (final InterruptedException x) {
			return false;
		} catch (final PartInitException e) {
			// If the editor cannot be opened, still close the wizard
			return true;
		}
		return true;
	}

	/**
	 * @since 1.1
	 */
	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.fConfig = config;
	}
}
