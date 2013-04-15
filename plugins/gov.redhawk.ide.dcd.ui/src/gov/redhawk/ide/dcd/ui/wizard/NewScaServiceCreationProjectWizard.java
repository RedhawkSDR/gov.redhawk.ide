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
package gov.redhawk.ide.dcd.ui.wizard;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.dcd.generator.newservice.ServiceProjectCreator;
import gov.redhawk.ide.dcd.ui.DcdUiActivator;
import gov.redhawk.ide.spd.ui.wizard.ImplementationWizardPage;
import gov.redhawk.ide.spd.ui.wizard.NewScaResourceWizard;
import gov.redhawk.ide.ui.wizard.IImportWizard;
import gov.redhawk.ide.util.ResourceUtils;
import gov.redhawk.model.sca.util.ModelUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

public class NewScaServiceCreationProjectWizard extends NewScaResourceWizard implements IImportWizard {

	/** The service properties page. */
	private ScaServiceProjectPropertiesWizardPage resourcePropertiesPage;
	private ImplementationWizardPage implPage;

	public NewScaServiceCreationProjectWizard() {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);
		this.setWindowTitle("New Service Project");
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		updateEntryPoints();
		final boolean isCreateNewResource = this.resourcePropertiesPage.getContentsGroup().isCreateNewResource();
		final IWorkingSet[] workingSets = this.resourcePropertiesPage.getSelectedWorkingSets();
		final String projectName = this.resourcePropertiesPage.getProjectName();
		final java.net.URI locationURI;
		if (this.resourcePropertiesPage.useDefaults()) {
			locationURI = null;
		} else {
			locationURI = this.resourcePropertiesPage.getLocationURI();
		}
		final boolean generateId = this.resourcePropertiesPage.getIdGroup().isGenerateId();
		final String providedId = this.resourcePropertiesPage.getIdGroup().getProvidedId();
		final IPath existingSpdPath = this.resourcePropertiesPage.getContentsGroup().getExistingResourcePath();
		final String serviceRepId = this.resourcePropertiesPage.getRepId();

		try {
			final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
				@Override
				protected void execute(final IProgressMonitor monitor) throws CoreException {
					try {

						final SubMonitor progress = SubMonitor.convert(monitor, 5 + getImplList().size()); // SUPPRESS CHECKSTYLE MagicNumber

						// Create an empty project
						final IProject project = ServiceProjectCreator.createEmptyProject(projectName, locationURI, progress.newChild(1));
						if (workingSets.length > 0) {
							PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
						}
						
						// Figure out the ID we'll use 
						String id;
						if (generateId) {
							id = DceUuidUtil.createDceUUID();
						} else {
							id = providedId;
						}

						// If we're creating a new service (vs importing one)
						if (isCreateNewResource) {

							// Create the SCA XML files
							final IFile defaultFile = ServiceProjectCreator.createServiceFiles(project, 
									NewScaServiceCreationProjectWizard.this.getSoftPkg().getName(),
									NewScaServiceCreationProjectWizard.this.getSoftPkg().getId(),
									null, serviceRepId, progress.newChild(1));
							NewScaServiceCreationProjectWizard.this.setOpenEditorOn(defaultFile);

							// Create the implementation
							final ImplementationWizardPage page = (ImplementationWizardPage) getWizPages().get(1);
							final Implementation impl = page.getImplementation();
							final ImplementationSettings settings = page.getImplSettings();
							ProjectCreator.addImplementation(project, NewScaServiceCreationProjectWizard.this.getSoftPkg().getName(), impl, settings, progress.newChild(1));
						} else {
							setOpenEditorOn(ProjectCreator.importFiles(project, existingSpdPath, getImplList(), getImportedSettingsMap(), progress.newChild(2), id));
						}

						// Setup the IDL Path
						ResourceUtils.createIdlLibraryResource(project, progress.newChild(1));

						// Generate initial code
						// Disable auto-generate at least for now until we have a better consensus on what state
						// we want a project to be in immediately after it is created
//						if (isCreateNewResource) {
//							final GenerateCode gc = new GenerateCode();
//							for (final ImplementationAndSettings pair : getImplList()) {
//								try {
//									gc.generateImpl(pair.getImplementation(), progress.newChild(1));
//								} catch (final CoreException c) {
//									// PASS
//								}
//							}
//						}
					} finally {
						monitor.done();
					}
				}
			};
			getContainer().run(false, false, op);
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			if ((getOpenEditorOn() != null) && getOpenEditorOn().exists()) {
				IDE.openEditor(activePage, getOpenEditorOn(), true);
			}

			BasicNewProjectResourceWizard.updatePerspective(getfConfig());
		} catch (final InvocationTargetException x) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DcdUiActivator.PLUGIN_ID, x.getCause().getMessage(), x.getCause()),
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
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		this.resourcePropertiesPage = new ScaServiceProjectPropertiesWizardPage("", "Service");
		addPage(this.resourcePropertiesPage);
		this.implPage = new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);
		this.implPage.setDescription("Choose the initial settings for the new implementation.");
		addPage(this.implPage);

		getImplList().add(new ImplementationAndSettings(this.implPage.getImplementation(), this.implPage.getImplSettings()));

		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			setWizPages((List<IWizardPage>) field.get(this));
		} catch (final SecurityException e1) {
			// PASS
		} catch (final NoSuchFieldException e1) {
			// PASS
		} catch (final IllegalArgumentException e) {
			// PASS
		} catch (final IllegalAccessException e) {
			// PASS
		}
	}

	public void importSelected(final String spdFile) {
		if (!getLastSpdFile().equals(spdFile)) {
			setLastSpdFile(spdFile);

			// Clear out the last implementations pages
			for (int i = (getWizPages().size() - 1); i > 0; --i) {
				getWizPages().remove(i).dispose();
			}
			// Clear out the old implementations and map
			getImplList().clear();

			// If spdFile is blank, then we're making a new implementation
			if ("".equals(spdFile)) {
				final ImplementationWizardPage page = new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);
				addPage(page);
				page.setName(this.resourcePropertiesPage.getProjectName());
				getImplList().add(new ImplementationAndSettings(page.getImplementation(), page.getImplSettings()));
			} else {
				final URI fileURI = URI.createFileURI(spdFile);

				// Load the soft package
				setSoftPkg(ModelUtil.loadSoftPkg(fileURI));

				WaveDevSettings waveSettings = null;
				try {
					waveSettings = CodegenUtil.getWaveDevSettings(CodegenUtil.getWaveDevSettingsURI(fileURI));
				} catch (final Exception e) {
					RedhawkCodegenActivator.getDefault()
					        .getLog()
					        .log(new Status(IStatus.INFO, RedhawkCodegenActivator.PLUGIN_ID, "Unable to find the wavedev settings file."));
					waveSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
				}

				if (waveSettings != null) {

					setImportedSettingsMap(CodegenFileHelper.settingsHasSourceCode(waveSettings, fileURI));

					setInitializing(true);

					try {
						// Loop through all the implementations
						for (final Implementation impl : getSoftPkg().getImplementation()) {
							final ImplementationSettings oldImplSettings = waveSettings.getImplSettings().get(impl.getId());
							// Create and add the page for the implementation
							final ImplementationWizardPage page = new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);
							addPage(page);

							// Import the implementation
							page.importImplementation(impl, oldImplSettings);

							final ImplementationSettings settings = page.getImplSettings();

							final Boolean found = getImportedSettingsMap().get(impl.getId());
							if ((found != null) && found.booleanValue()) {
								page.enableImportCode(true);
							}

							// Configure the settings page if there is one for this
							// implementation
							final ICodeGeneratorDescriptor codeGen = page.getCodeGenerator();
							// findPageByGeneratorId is always guaranteed to return

							// at least one page. Add this page to the wizard
							final ICodegenWizardPage codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry()
							        .findPageByGeneratorId(settings.getGeneratorId())[0];
							addPage(codeGenPage);

							// Enable the canFlip if this isn't the last page in the list
							codeGenPage.setCanFlipToNextPage((getImplList().size() + 1) != getSoftPkg().getImplementation().size());

							// Configure the wizard page with the current settings
							codeGenPage.configure(getSoftPkg(),
							        this.implPage.getImplementation(),
							        codeGen,
							        settings,
							        ICodeGeneratorDescriptor.COMPONENT_TYPE_SERVICE);

							// Save the settings
							getImplList().add(new ImplementationAndSettings(impl, settings));
						}
					} finally {
						setInitializing(false);
					}
				}
			}
		}
	}

	/**
	 * @since 4.0
	 */
	public void switchingResourcePage() {
		final ImplementationWizardPage page = (ImplementationWizardPage) this.getWizPages().get(1);
		page.setName(this.resourcePropertiesPage.getProjectName());

		// Create a softpkg
		final SoftPkg softPkg = SpdFactory.eINSTANCE.createSoftPkg();
		
		String[] tokens = this.resourcePropertiesPage.getProjectName().split("\\.");
		softPkg.setName(tokens[tokens.length - 1]);

		final boolean generateId = this.resourcePropertiesPage.getIdGroup().isGenerateId();
		final String providedId = this.resourcePropertiesPage.getIdGroup().getProvidedId();
		// Figure out the ID we'll use 
		final String id;
		if (generateId) {
			id = DceUuidUtil.createDceUUID();
		} else {
			id = providedId;
		}
		softPkg.setId(id);
		this.setSoftPkg(softPkg);
	}
}
