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
package gov.redhawk.ide.sad.graphiti.ui.wizard;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.SADDiagramTypeProvider;
import gov.redhawk.ide.sad.graphiti.internal.ui.editor.GraphitiSadMultiPageEditor;
import gov.redhawk.ide.sad.ui.wizard.ScaWaveformProjectAssemblyControllerWizardPage;
import gov.redhawk.ide.sad.ui.wizard.ScaWaveformProjectPropertiesWizardPage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.graphiti.examples.common.FileService;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.jface.viewers.IStructuredSelection;
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

/**
 * Wizard for creating new SCA waveform projects.
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.waveformPropertiesPage = new ScaWaveformProjectPropertiesWizardPage("");
		addPage(this.waveformPropertiesPage);

		this.waveformACpage = new ScaWaveformProjectAssemblyControllerWizardPage("");
		this.waveformACpage.setDescription("Add an existing Assembly Controller to your Waveform");
		addPage(this.waveformACpage);

		final WorkspaceJob job = new WorkspaceJob("Load SdrRoot") {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				final SdrRoot sdrRoot = SdrUiPlugin.getDefault().getTargetSdrRoot();
				sdrRoot.load(monitor);
				NewScaWaveformProjectWizard.this.waveformACpage.setComponents(sdrRoot.getComponentsContainer().getComponents());
				return Status.OK_STATUS;
			}
		};

		job.setUser(true);
		job.schedule();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canFinish() {
		if (this.waveformPropertiesPage.getContentsGroup().isCreateNewResource()) {
			return super.canFinish();
		} else {
			return this.waveformPropertiesPage.isPageComplete();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench arg0, final IStructuredSelection arg1) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().getCurrentPage().getControl().setEnabled(false);
			// Find the working sets and where the new project should be located on disk
			final IWorkingSet[] workingSets = this.waveformPropertiesPage.getSelectedWorkingSets();
			final java.net.URI locationURI;
			if (this.waveformPropertiesPage.useDefaults()) {
				locationURI = null;
			} else {
				locationURI = this.waveformPropertiesPage.getLocationURI();
			}
			final String id = this.waveformPropertiesPage.getID();
			final boolean isCreateNewResource = this.waveformPropertiesPage.getContentsGroup().isCreateNewResource();
			final IPath existingSadPath = this.waveformPropertiesPage.getContentsGroup().getExistingResourcePath();
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
							// Create the SCA XML files
							NewScaWaveformProjectWizard.this.openEditorOn = WaveformProjectCreator.createWaveformFiles(project,
							        id,
							        assemblyController,
							        progress.newChild(1));
						} else {
							openEditorOn = project.getFile(project.getName() + SadPackage.FILE_EXTENSION);
							ProjectCreator.importFile(project, openEditorOn, existingSadPath, progress.newChild(1));
							ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
							URI uri = URI.createPlatformResourceURI(openEditorOn.getFullPath().toString(), true).appendFragment(SoftwareAssembly.EOBJECT_PATH);
							SoftwareAssembly sad = (SoftwareAssembly) resourceSet.getEObject(uri, true);
							sad.setId(id);
							sad.setName(project.getName());
							try {
	                            sad.eResource().save(null);
                            } catch (IOException e) {
                            	throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to modify SAD File."));
                            }
							openEditorOn.refreshLocal(IResource.DEPTH_ONE, null);
						}

						// Setup automatic RPM spec file generation
						CodegenUtil.addTopLevelRPMSpecBuilder(project, progress.newChild(1));
					} finally {
						monitor.done();
					}
				}
			};
			getContainer().run(false, false, op);
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ((this.openEditorOn != null) && this.openEditorOn.exists()) {

				//opens the Graphiti editor
				//create diagram instance
				Diagram diagram = Graphiti.getPeCreateService().createDiagram(SADDiagramTypeProvider.DIAGRAM_TYPE_ID, projectName, true);
				
				//create file to contain diagram
				IFile diagramFile = this.openEditorOn.getParent().getFile(new Path(projectName + SADDiagramTypeProvider.DIAGRAM_EXT));
				
				//uri for diagram file
				URI uri = URI.createPlatformResourceURI(diagramFile.getFullPath().toString(), true);
				//create resource for diagram file
//				ResourceSet resourceSet = new ResourceSetImpl();
//				TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(resourceSet);
//				if(editingDomain == null){
//					editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain(resourceSet);
//				}
//				Resource createdResource = resourceSet.createResource(uri);
//				createdResource.getContents().add(diagram);
				
				FileService.createEmfFileForDiagram(uri, diagram);
				
//				String providerId = GraphitiUi.getExtensionManager().getDiagramTypeProviderId(diagram.getDiagramTypeId());
//				
//				DiagramEditorInput editorInput = new DiagramEditorInput(uri, providerId);
//				
//				try {
//					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(editorInput, DiagramEditor.DIAGRAM_EDITOR_ID);
//				} catch (PartInitException e) {
//					String error = "Error while opening SAD diagram editor";
//					IStatus status = new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, error, e);
//					ErrorDialog.openError(getShell(), "An error occured", null, status);
//					return false;
//				}
				
				//opens the GEF editor
				final GraphitiSadMultiPageEditor sadPart = (GraphitiSadMultiPageEditor) IDE.openEditor(activePage, this.openEditorOn, GraphitiSadMultiPageEditor.ID, true);
				
				//TODO: bwhoff2 we probably need to handle this
				//setCustomPreferences(sadPart);
				
				
				
			}

			BasicNewProjectResourceWizard.updatePerspective(this.fConfig);
		} catch (final InvocationTargetException x) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, x.getCause().getMessage(), x.getCause()),
			        StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (final InterruptedException x) {
			return false;
		} catch (final PartInitException e) {
			// If the editor cannot be opened, still close the wizard
			return true;
		} finally {
			getContainer().getCurrentPage().getControl().setEnabled(true);
		}
		return true;
	}

//	/**
//	 * Set custom viewing properties for the Sad Editor so that we can tell the difference between this editor and the Sad Explorer
//	 * 
//	 * @param sadPart The Sad Editor instance that we shall change the initial diagram style for
//	 */
//	private void setCustomPreferences(final SadEditor sadPart) {
//		if (sadPart == null) {
//			return;
//		}
//		final DiagramGraphicalViewer viewer = (DiagramGraphicalViewer) sadPart.getDiagramGraphicalViewer();
//		final IPreferenceStore store = viewer.getWorkspaceViewerPreferenceStore();
//
//		store.setValue(WorkspaceViewerProperties.VIEWRULERS, true);
//		store.setValue(WorkspaceViewerProperties.VIEWGRID, true);
//		store.setValue(WorkspaceViewerProperties.GRIDSPACING, .5); // SUPPRESS CHECKSTYLE MagicNumber
//		store.setValue(WorkspaceViewerProperties.GRIDORDER, false);
//		store.setValue(WorkspaceViewerProperties.GRIDLINESTYLE, SWT.LINE_SOLID);
//		store.setValue(WorkspaceViewerProperties.GRIDLINECOLOR, SWT.COLOR_BLACK);
//	}


	/**
	 * {@inheritDoc}
	 * 
	 * @since 1.1
	 */
	@Override
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.fConfig = config;
	}
}
