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

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.OpenAssociatedPerspectiveJob;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
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
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * The Class NewScaResourceProjectWizard.
 * 
 * @since 4.0
 */
public class NewScaResourceProjectWizard extends NewScaResourceWizard implements IImportWizard {

	/** The component properties page. */
	private ScaResourceProjectPropertiesWizardPage resourcePropertiesPage;
	private ImplementationWizardPage implPage;

	/**
	 * 
	 */
	public NewScaResourceProjectWizard() {
		super(ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
		this.setWindowTitle("New Component Project");
		this.setNeedsProgressMonitor(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		this.updateEntryPoints();
		final boolean isCreateNewResource = this.resourcePropertiesPage.getContentsGroup().isCreateNewResource();
		final IWorkingSet[] workingSets = this.resourcePropertiesPage.getSelectedWorkingSets();
		final String projectName = NewScaResourceProjectWizard.this.resourcePropertiesPage.getProjectName();
		final java.net.URI locationURI;
		if (this.resourcePropertiesPage.useDefaults()) {
			locationURI = null;
		} else {
			locationURI = this.resourcePropertiesPage.getLocationURI();
		}
		this.resourcePropertiesPage.getProjectHandle();
		final IPath existingResourceLocation = this.resourcePropertiesPage.getContentsGroup().getExistingResourcePath();

		// Create the SCA component project
		final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				try {
					final SubMonitor progress = SubMonitor.convert(monitor, "Creating project...", 4 + NewScaResourceProjectWizard.this.getImplList().size()); // SUPPRESS CHECKSTYLE MagicNumber 

					// Create an empty project
					final IProject project = ComponentProjectCreator.createEmptyProject(projectName, locationURI, progress.newChild(1));
					try {
						if (workingSets.length > 0) {
							PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
						}
						BasicNewProjectResourceWizard.updatePerspective(NewScaResourceProjectWizard.this.getfConfig());

						// If we're creating a new component (vs importing one)
						if (isCreateNewResource) {
							// Create the SCA XML files
							// Refs #175.  Passing in the spd file since spd name is now different than project name.
							NewScaResourceProjectWizard.this.setOpenEditorOn(ComponentProjectCreator.createComponentFiles(project,
							        NewScaResourceProjectWizard.this.getSoftPkg().getName(),
							        NewScaResourceProjectWizard.this.getSoftPkg().getId(),
							        null, progress.newChild(1)));

							// Create the implementation
							final ImplementationWizardPage page = (ImplementationWizardPage) NewScaResourceProjectWizard.this.getWizPages().get(1);
							final Implementation impl = page.getImplementation();
							final ImplementationSettings settings = page.getImplSettings();
							ProjectCreator.addImplementation(project, NewScaResourceProjectWizard.this.getSoftPkg().getName(), impl, settings, progress.newChild(1));
						} else {
							NewScaResourceProjectWizard.this.setOpenEditorOn(ProjectCreator.importFiles(project, existingResourceLocation,
							        NewScaResourceProjectWizard.this.getImplList(), NewScaResourceProjectWizard.this.getImportedSettingsMap(),
							        progress.newChild(2), NewScaResourceProjectWizard.this.getSoftPkg().getId()));
						}

						// Setup the IDL Path
						ResourceUtils.createIdlLibraryResource(project, progress.newChild(1));

						// Generate initial code
						// Disable auto-generate at least for now until we have a better consensus on what state
						// we want a project to be in immediately after it is created
						//						if (isCreateNewResource) {
						//							final GenerateCode gc = new GenerateCode();
						//							for (final ImplementationAndSettings pair : NewScaResourceProjectWizard.this.getImplList()) {
						//								final IStatus status = gc.generateImpl(pair.getImplementation(), progress.newChild(1));
						//								if (!status.isOK()) {
						//									throw new CoreException(status);
						//								}
						//							}
						//						}
					} catch (final Exception e) {
						if (project != null) {
							project.delete(true, progress.newChild(1));
						}
						throw e;
					}
				} catch (final CoreException e) {
					throw e;
				} catch (final Exception e) {
					throw new CoreException(new Status(IStatus.ERROR, ComponentUiPlugin.PLUGIN_ID, "Error creating project", e));
				} finally {

					if (monitor != null) {
						monitor.done();
					}
				}
			}

		};

		try {
			this.getContainer().run(true, false, operation);

			// Open the default editor for the new SCA component; also invoke code generator for manual templates
			final IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			final IFile spdFile = this.getOpenEditorOn();
			if ((spdFile != null) && spdFile.exists()) {
				try {
					IDE.openEditor(activePage, spdFile, true);
				} catch (final PartInitException e) {
					// PASS
				}
			}

			// Only update perspective on new component projects (not imports)
//			if (isCreateNewResource) {
//				final ImplementationSettings settings = this.getImplList().get(0).getImplementationSettings();
//				final ICodeGeneratorDescriptor descriptor = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());
//				new OpenAssociatedPerspectiveJob(descriptor).schedule();
//			}

			return true;
		} catch (final InvocationTargetException e1) {
			if (e1.getCause() instanceof CoreException) {
				ComponentUiPlugin.logException(e1);
				//StatusManager.getManager().handle(((CoreException) e1.getCause()).getStatus(), StatusManager.SHOW);
			}
			return false;
		} catch (final InterruptedException e1) {
			return true;
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		this.resourcePropertiesPage = new ScaResourceProjectPropertiesWizardPage("", "Component");
		this.addPage(this.resourcePropertiesPage);
		this.implPage = new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
		this.implPage.setDescription("Choose the initial settings for the new implementation.");
		this.addPage(this.implPage);

		this.getImplList().add(new ImplementationAndSettings(this.implPage.getImplementation(), this.implPage.getImplSettings()));

		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			this.setWizPages((List<IWizardPage>) field.get(this));
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

	/**
	 * @since 4.0
	 */
	public void importSelected(final String spdFile) {
		if (!this.getLastSpdFile().equals(spdFile)) {
			this.setLastSpdFile(spdFile);

			// Clear out the last implementations pages
			for (int i = (this.getWizPages().size() - 1); i > 0; --i) {
				this.getWizPages().remove(i).dispose();
			}
			// Clear out the old implementations and map
			this.getImplList().clear();

			// If spdFile is blank, then we're making a new implementation
			if ("".equals(spdFile)) {
				final ImplementationWizardPage page = new ImplementationWizardPage("", ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
				this.addPage(page);
				page.setName(this.resourcePropertiesPage.getProjectName());
				this.getImplList().add(new ImplementationAndSettings(page.getImplementation(), page.getImplSettings()));
				// Create a softpkg
				final SoftPkg softPkg = SpdFactory.eINSTANCE.createSoftPkg();
				softPkg.setName(this.resourcePropertiesPage.getProjectName());

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
			} else {
				final URI fileURI = URI.createFileURI(spdFile);

				// Load the soft package
				this.setSoftPkg(ModelUtil.loadSoftPkg(fileURI));
				this.getSoftPkg().setName(this.resourcePropertiesPage.getProjectName());
				
				// Figure out the ID we'll use 
				final boolean generateId = this.resourcePropertiesPage.getIdGroup().isGenerateId();
				final String providedId = this.resourcePropertiesPage.getIdGroup().getProvidedId();
				final String id;
				if (generateId) {
					id = DceUuidUtil.createDceUUID();
				} else {
					id = providedId;
				}
				getSoftPkg().setId(id);

				WaveDevSettings waveSettings = null;
				try {
					waveSettings = CodegenUtil.getWaveDevSettings(CodegenUtil.getWaveDevSettingsURI(fileURI));
				} catch (final Exception e) {
					RedhawkCodegenActivator.logInfo("Unable to find the wavedev settings file, using defaults.");
					waveSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
				}

				// Make sure there are default settings for all implementations
				CodegenUtil.recreateImplSettings(this.getSoftPkg(), waveSettings);

				this.setImportedSettingsMap(CodegenFileHelper.settingsHasSourceCode(waveSettings, fileURI));

				this.setInitializing(true);

				try {
					// Loop through all the implementations
					for (final Implementation impl : this.getSoftPkg().getImplementation()) {
						final ImplementationSettings oldImplSettings = waveSettings.getImplSettings().get(impl.getId());

						// Create and add the page for the implementation
						final ImplementationWizardPage page = new ImplementationWizardPage("", this.getSoftPkg());
						this.addPage(page);

						// Import the implementation
						page.importImplementation(impl, oldImplSettings);

						final ImplementationSettings settings = page.getImplSettings();

						final Boolean found = this.getImportedSettingsMap().get(impl.getId());
						if ((found != null) && found.booleanValue()) {
							page.enableImportCode(true);
						}

						// Configure the settings page if there is one for this
						// implementation
						final ICodeGeneratorDescriptor codeGen = page.getCodeGenerator();
						final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGen.getId(),
						        ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
						if (templates.length > 0) {
							// findPageByGeneratorId is always guaranteed to return
							// at least one page. Add this page to the wizard
							final ICodegenWizardPage codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(
							        settings.getGeneratorId())[0];
							this.addPage(codeGenPage);

							// Enable the canFlip if this isn't the last page in the list
							codeGenPage.setCanFlipToNextPage((this.getImplList().size() + 1) != this.getSoftPkg().getImplementation().size());

							// Configure the wizard page with the current settings
							codeGenPage.configure(this.getSoftPkg(), this.implPage.getImplementation(), codeGen, settings,
							        ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE);
						}

						// Save the settings
						this.getImplList().add(new ImplementationAndSettings(impl, settings));
					}

				} finally {
					this.setInitializing(false);
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

	/**
	 * @since 7.0
	 */
	public String getName() {
		return NewScaResourceProjectWizard.this.resourcePropertiesPage.getProjectName();
	}

}
