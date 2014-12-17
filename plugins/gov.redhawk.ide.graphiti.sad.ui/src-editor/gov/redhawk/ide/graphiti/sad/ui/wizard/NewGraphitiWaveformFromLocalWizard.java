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
package gov.redhawk.ide.graphiti.sad.ui.wizard;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.graphiti.sad.internal.ui.editor.GraphitiWaveformMultiPageEditor;
import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.sad.ui.wizard.ScaWaveformProjectPropertiesWizardPage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.ComponentFile;
import mil.jpeojtrs.sca.partitioning.ComponentFileRef;
import mil.jpeojtrs.sca.partitioning.ComponentPlacement;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

@SuppressWarnings("restriction")
public class NewGraphitiWaveformFromLocalWizard extends Wizard implements IExecutableExtension {

	/** The configuration. */
	private IConfigurationElement fConfig;

	/** The component properties page. */
	private ScaWaveformProjectPropertiesWizardPage waveformPropertiesPage;

	private IFile openEditorOn;

	private final SoftwareAssembly sad;

	public NewGraphitiWaveformFromLocalWizard(final SoftwareAssembly sad) {
		this.setWindowTitle("Save Chalkboard");
		this.setNeedsProgressMonitor(true);
		this.sad = sad;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.waveformPropertiesPage = new ScaWaveformProjectPropertiesWizardPage("");
		this.waveformPropertiesPage.setShowContentsGroup(false);
		this.waveformPropertiesPage.setTitle("Save Chalkboard");
		this.waveformPropertiesPage.setDescription("Save the Chalkboard as a new SCA Waveform Project.");
		addPage(this.waveformPropertiesPage);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canFinish() {
		return this.waveformPropertiesPage.isPageComplete();
	}

	public void init(final IWorkbench arg0, final IStructuredSelection arg1) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
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
			final String projectName = this.waveformPropertiesPage.getProjectName();
			final SoftwareAssembly newSad = EcoreUtil.copy(this.sad);
			updateComponentFiles(newSad);
			newSad.setId(id);
			newSad.setName(projectName);

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
						NewGraphitiWaveformFromLocalWizard.this.openEditorOn = WaveformProjectCreator.createWaveformFiles(project, newSad.getId(), null, null);
						final URI uri = URI.createPlatformResourceURI(NewGraphitiWaveformFromLocalWizard.this.openEditorOn.getFullPath().toPortableString(),
							true);
						final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
						final Resource resource = resourceSet.getResource(uri, true);
						resource.getContents().clear();
						resource.getContents().add(newSad);
						final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						try {
							resource.save(buffer, null);
						} catch (final IOException e) {
							// PASS
						}
						NewGraphitiWaveformFromLocalWizard.this.openEditorOn.setContents(new ByteArrayInputStream(buffer.toByteArray()), true, false,
							progress.newChild(1));

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
				IDE.openEditor(activePage, this.openEditorOn, GraphitiWaveformMultiPageEditor.ID, true);
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
		}
		return true;
	}

	private void updateComponentFiles(final SoftwareAssembly newSad) {
		Map<String, ComponentFile> fileMap = new HashMap<String, ComponentFile>();
		EList<SadComponentInstantiation> sadInstantiations = newSad.getAllComponentInstantiations();
		for (SadComponentInstantiation inst : sadInstantiations) {
			ComponentPlacement< ? > placement = inst.getPlacement();
			ComponentFileRef fileRef = placement.getComponentFileRef();
			ComponentFile oldFile = null;
			for (ComponentFile f : newSad.getComponentFiles().getComponentFile()) {
				if (f.getId().equals(fileRef.getRefid())) {
					oldFile = f;
					break;
				}
			}
			if (oldFile == null) {
				continue;
			}
			String name;
			final URI uri = URI.createURI(oldFile.getLocalFile().getName());
			if (uri.scheme() != null) {
				final String fileName = uri.lastSegment();
				final int index = fileName.indexOf('.');
				final String folderName = fileName.substring(0, index);
				name = "/components/" + folderName + "/" + fileName;
			} else {
				name = oldFile.getLocalFile().getName();
			}
			ComponentFile file = fileMap.get(name);
			if (file == null) {
				file = PartitioningFactory.eINSTANCE.createDomComponentFile();
				file.setId(DceUuidUtil.createDceUUID());
				file.setType("SPD");
				file.setLocalFile(PartitioningFactory.eINSTANCE.createLocalFile());
				file.getLocalFile().setName(name);
				fileMap.put(name, file);
			}
			placement.getComponentFileRef().setFile(file);
		}

		newSad.getComponentFiles().getComponentFile().clear();
		newSad.getComponentFiles().getComponentFile().addAll(fileMap.values());
	}

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
