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
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.ide.util.ResourceUtils;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.util.SubMonitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * @since 7.0
 */
public abstract class NewScaResourceWizard extends Wizard implements INewWizard, ScaImplementationWizard {

	private IFile openEditorOn;

	private final List<ImplementationAndSettings> implList = new ArrayList<ImplementationAndSettings>();
	private SoftPkg softPkg;
	private HashMap<String, Boolean> importedSettingsMap = new HashMap<String, Boolean>();
	private String lastSpdFile = "";
	private List<IWizardPage> wizPages;
	private boolean initializing = false;
	private IConfigurationElement fConfig;
	private int firstImplPage;
	private final String componentType;
	/** The component properties page. */
	private ScaProjectPropertiesWizardPage resourcePropertiesPage;
	private ImplementationWizardPage implPage;

	public NewScaResourceWizard(final String componentType) {
		super();
		this.componentType = componentType;
	}

	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.firstImplPage = 1;
	}
	
	/**
     * @since 8.0
     */
	protected void setResourcePropertiesPage(ScaProjectPropertiesWizardPage resourcePropertiesPage) {
	    this.resourcePropertiesPage = resourcePropertiesPage;
    }
	
	/**
     * @since 8.0
     */
	protected ScaProjectPropertiesWizardPage getResourcePropertiesPage() {
	    return resourcePropertiesPage;
    }
	
	/**
     * @since 8.0
     */
	protected void setImplPage(ImplementationWizardPage implPage) {
	    this.implPage = implPage;
    }
	
	/**
     * @since 8.0
     */
	public ImplementationWizardPage getImplPage() {
	    return implPage;
    }

	@Override
	public void createPageControls(final Composite pageContainer) {
		super.createPageControls(pageContainer);
	}

	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		if (this.initializing) {
			return;
		}

		// If the code generator has settings, update the page as needed
		if (codeGeneratorDescriptor != null) {
			int implIndex = -1;
			for (int i = 0; i < this.implList.size(); i++) {
				if (this.implList.get(i).getImplementation() == impl) {
					implIndex = i;
					break;
				}
			}
			int codegenIndex = this.wizPages.size();
			IWizardPage oldGenPage = null;
			ImplementationSettings settings = null;

			// Figure out where the codegen page to replace is, based on the
			// current implementation
			for (int i = this.firstImplPage; i < this.wizPages.size(); ++i) {
				final IWizardPage page = this.wizPages.get(i);
				// If this is an implementation page, check to see if its index
				// matches the one for the current implementation
				if (page instanceof ImplementationWizardPage) {
					// If it does, check to see where to put the codegen page
					if (implIndex == 0) {
						settings = ((ImplementationWizardPage) page).getImplSettings();
						// The generator page is going after this one
						codegenIndex = i + 1;

						// Three scenarios:
						// - Implementation is the last in the list and it
						// doesn't have a settings page previously
						// - The implementation is in the middle of the list
						// without a settings page
						// - There is a settings page after this implementation
						// page, replace it
						// Store the generator page if it's next, otherwise
						// we'll be inserting one
						if ((i != (this.wizPages.size() - 1)) && !(this.wizPages.get(i + 1) instanceof ImplementationWizardPage)) {
							oldGenPage = this.wizPages.get(i + 1);
						}
						break;
					}
					// Otherwise decrement and try again
					implIndex--;
				}
			}

			// Allow the wizards to exit now if the generators are the same
			if (this.checkGenerator(settings, codeGeneratorDescriptor)) {
				this.getContainer().updateButtons();
				return;
			}
			ICodegenWizardPage codeGenPage = null;
			boolean createControl = true;
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGeneratorDescriptor.getId(),
			        this.componentType);

			// Add the new page first
			if (settings != null && templates.length > 0) {
				// findPageByGeneratorId is always guaranteed to return at least
				// one page.
				codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId())[0];

				if (oldGenPage != null) {
					// If the generator page is different, add the new one
					if (codeGenPage.getClass() != oldGenPage.getClass()) {
						this.wizPages.add(codegenIndex, codeGenPage);
						// Enable the canFlip if this isn't the last page in the
						// list
						codeGenPage.setCanFlipToNextPage(oldGenPage.canFlipToNextPage());

						// Otherwise get rid of the one we just created and
						// reuse it
					} else {
						codeGenPage.dispose();
						codeGenPage = (ICodegenWizardPage) oldGenPage;
						oldGenPage = null;
						codeGenPage.setCanFinish(false);
						createControl = false;
					}
				} else if (codegenIndex == this.wizPages.size()) {
					this.wizPages.add(codeGenPage);
					// Disable canFlip since this is the last page in the list
					codeGenPage.setCanFlipToNextPage(false);
				} else {
					this.wizPages.add(codegenIndex, codeGenPage);
					// Enable canFlip since this isn't the last page in the list
					codeGenPage.setCanFlipToNextPage(true);
				}
			}

			if (oldGenPage != null) {
				this.wizPages.remove(oldGenPage);
				oldGenPage.dispose();
			}

			// Initialize the settings page
			if (codeGenPage != null && settings != null && templates.length > 0) {
				if (createControl) {
					codeGenPage.setWizard(this);
				}

				settings.setOutputDir(null); // let the page pick the outputdir

				// Configure the wizard page with the current settings
				codeGenPage.configure(this.getSoftPkg(), impl, codeGeneratorDescriptor, settings, this.componentType);
			}
		}

		// Force an update on the buttons, changing the generator may have
		// enabled or disabled the display of one of the buttons
		this.getContainer().updateButtons();
	}

	public boolean hasMoreImplementations(final Implementation curImpl) {
		// Return true if there are implementations and the current
		// implementation isn't the last one in the list
		return ((this.implList.size() > 0) && (curImpl != this.implList.get(this.implList.size() - 1).getImplementation()));
	}

	@Override
	public IWizardPage getNextPage(final IWizardPage page) {
		if (page instanceof ImplementationWizardPage) {
			final ImplementationWizardPage nextImplPage = (ImplementationWizardPage) page;
			this.importedSettingsMap.put(nextImplPage.getImplementation().getId(), nextImplPage.shouldImportCode());
		}
		return super.getNextPage(page);
	}

	protected void updateEntryPoints() {
		for (final ImplementationAndSettings pair : this.implList) {
			final Implementation impl = pair.getImplementation();
			final ImplementationSettings settings = pair.getImplementationSettings();

			if (impl.getCode() != null) {
				if (!impl.getCode().getLocalFile().toString().equals(settings.getOutputDir())) {
					final ICodeGeneratorDescriptor desc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());

					if (desc != null) {
						try {
							final IScaComponentCodegen generator = desc.getGenerator();
							Assert.isNotNull(impl.getSoftPkg());
							final Code code = generator.getInitialCodeSettings(impl.getSoftPkg(), settings, impl);

							impl.setCode(code);
						} catch (final CoreException e) {
							//PASS
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data) throws CoreException {
		this.fConfig = config;
	}

	public IFile getOpenEditorOn() {
		return this.openEditorOn;
	}

	public void setOpenEditorOn(final IFile openEditorOn) {
		this.openEditorOn = openEditorOn;
	}

	public SoftPkg getSoftPkg() {
		return this.softPkg;
	}

	public void setSoftPkg(final SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	public HashMap<String, Boolean> getImportedSettingsMap() {
		return this.importedSettingsMap;
	}

	public void setImportedSettingsMap(final HashMap<String, Boolean> importedSettingsMap) {
		this.importedSettingsMap = importedSettingsMap;
	}

	public String getLastSpdFile() {
		return this.lastSpdFile;
	}

	public void setLastSpdFile(final String lastSpdFile) {
		this.lastSpdFile = lastSpdFile;
	}

	public List<IWizardPage> getWizPages() {
		return this.wizPages;
	}

	public void setWizPages(final List<IWizardPage> wizPages) {
		this.wizPages = wizPages;
	}

	public boolean isInitializing() {
		return this.initializing;
	}

	public void setInitializing(final boolean initializing) {
		this.initializing = initializing;
	}

	public IConfigurationElement getfConfig() {
		return this.fConfig;
	}

	public void setfConfig(final IConfigurationElement fConfig) {
		this.fConfig = fConfig;
	}

	public int getFirstImplPage() {
		return this.firstImplPage;
	}

	public void setFirstImplPage(final int firstImplPage) {
		this.firstImplPage = firstImplPage;
	}

	public List<ImplementationAndSettings> getImplList() {
		return this.implList;
	}

	/**
	 * 
	 * @param settings
	 * @param codeGeneratorDescriptor
	 * @return
	 */
	public boolean checkGenerator(final ImplementationSettings settings, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		return false;
	}

	/**
	 * @since 8.0
	 */
	public void importSelected(final String spdFile) {
		if (!getLastSpdFile().equals(spdFile)) {
			setLastSpdFile(spdFile);

			// Clear out the last implementations pages
			for (int i = (getWizPages().size() - 1); i > 0; --i) {
				getWizPages().remove(i).dispose();
			}
			// Clear out the old implementations and map
			getImplList().clear();

			final String name = getProjectName();
			final String id = getID();

			// If spdFile is blank, then we're making a new implementation
			if ("".equals(spdFile)) {
				final ImplementationWizardPage page = new ImplementationWizardPage("", getType());
				addPage(page);
				page.setName(name);
				getImplList().add(new ImplementationAndSettings(page.getImplementation(), page.getImplSettings()));
				// Create a softpkg
				final SoftPkg newSoftPkg = SpdFactory.eINSTANCE.createSoftPkg();
				newSoftPkg.setName(name);
				newSoftPkg.setId(id);
				setSoftPkg(newSoftPkg);
			} else {
				final URI fileURI = URI.createFileURI(spdFile);

				// Load the soft package
				setSoftPkg(ModelUtil.loadSoftPkg(fileURI));
				getSoftPkg().setName(name);
				getSoftPkg().setId(id);

				WaveDevSettings waveSettings = null;
				try {
					waveSettings = CodegenUtil.getWaveDevSettings(CodegenUtil.getWaveDevSettingsURI(fileURI));
				} catch (final Exception e) {
					RedhawkCodegenActivator.logInfo("Unable to find the wavedev settings file, using defaults.");
					waveSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
				}

				// Make sure there are default settings for all implementations
				CodegenUtil.recreateImplSettings(getSoftPkg(), waveSettings);

				setImportedSettingsMap(CodegenFileHelper.settingsHasSourceCode(waveSettings, fileURI));

				setInitializing(true);

				try {
					// Loop through all the implementations
					for (final Implementation impl : getSoftPkg().getImplementation()) {
						final ImplementationSettings oldImplSettings = waveSettings.getImplSettings().get(impl.getId());

						// Create and add the page for the implementation
						final ImplementationWizardPage page = new ImplementationWizardPage("", getSoftPkg());
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
						final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGen.getId(), getType());
						if (templates.length > 0) {
							// findPageByGeneratorId is always guaranteed to return
							// at least one page. Add this page to the wizard
							final ICodegenWizardPage codeGenPage = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId())[0];
							addPage(codeGenPage);

							// Enable the canFlip if this isn't the last page in the list
							codeGenPage.setCanFlipToNextPage((getImplList().size() + 1) != getSoftPkg().getImplementation().size());

							// Configure the wizard page with the current settings
							codeGenPage.configure(getSoftPkg(), getImplementation(), codeGen, settings, getType());
						}

						// Save the settings
						getImplList().add(new ImplementationAndSettings(impl, settings));
					}

				} finally {
					setInitializing(false);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		this.updateEntryPoints();
		final boolean isCreateNewResource = this.resourcePropertiesPage.getContentsGroup().isCreateNewResource();
		final IWorkingSet[] workingSets = this.resourcePropertiesPage.getSelectedWorkingSets();
		final String projectName = this.resourcePropertiesPage.getProjectName();
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
					final SubMonitor progress = SubMonitor.convert(monitor, "Creating project...", 4 + getImplList().size()); // SUPPRESS CHECKSTYLE MagicNumber 

					// Create an empty project
					final IProject project = createEmptyProject(projectName, locationURI, progress.newChild(1));
					try {
						if (workingSets.length > 0) {
							PlatformUI.getWorkbench().getWorkingSetManager().addToWorkingSets(project, workingSets);
						}
						BasicNewProjectResourceWizard.updatePerspective(getfConfig());

						// If we're creating a new component (vs importing one)
						if (isCreateNewResource) {
							// Create the SCA XML files
							setOpenEditorOn(createComponentFiles(project, getSoftPkg().getId(), null, progress.newChild(1)));

							// Create the implementation
							final ImplementationWizardPage page = (ImplementationWizardPage) getWizPages().get(1);
							final Implementation impl = page.getImplementation();
							final ImplementationSettings settings = page.getImplSettings();
							ProjectCreator.addImplementation(project, impl, settings, progress.newChild(1));
						} else {
							setOpenEditorOn(ProjectCreator.importFiles(project, existingResourceLocation, getImplList(), getImportedSettingsMap(), progress.newChild(2), getSoftPkg().getId()));
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
     * @since 8.0
     */
	protected abstract IFile createComponentFiles(IProject project, String id, String author, IProgressMonitor monitor) throws CoreException;

	/**
     * @since 8.0
     */
	protected abstract IProject createEmptyProject(String projectName, java.net.URI locationURI, IProgressMonitor monitor) throws CoreException;

	/**
	 * @since 8.0
	 */
	public void switchingResourcePage() {
		final ImplementationWizardPage page = (ImplementationWizardPage) this.getWizPages().get(1);
		page.setName(this.resourcePropertiesPage.getProjectName());

		// Create a softpkg
		final SoftPkg newSoftPkg = SpdFactory.eINSTANCE.createSoftPkg();
		newSoftPkg.setName(this.resourcePropertiesPage.getProjectName());
		newSoftPkg.setId(getID());
		this.setSoftPkg(newSoftPkg);
	}

	/**
     * @since 8.0
     */
	protected Implementation getImplementation() {
		return implPage.getImplementation();
	}

	/**
     * @since 8.0
     */
	protected String getID() {
		// Figure out the ID we'll use 
		return this.resourcePropertiesPage.getIdGroup().getId();
	}

	/**
     * @since 8.0
     */
	protected String getProjectName() {
		return resourcePropertiesPage.getProjectName();
	}

	/**
     * @since 8.0
     */
	protected String getType() {
		return ICodeGeneratorDescriptor.COMPONENT_TYPE_RESOURCE;
	}

}
