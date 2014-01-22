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
import gov.redhawk.ide.codegen.ui.ICodeGeneratorPageRegistry;
import gov.redhawk.ide.codegen.ui.ICodeGeneratorPageRegistry2;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory2;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import gov.redhawk.ide.codegen.util.ImplementationAndSettings;
import gov.redhawk.ide.codegen.util.ProjectCreator;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.ui.wizard.ScaProjectPropertiesWizardPage;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.util.SubMonitor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Compiler;
import mil.jpeojtrs.sca.spd.HumanLanguage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.ProgrammingLanguage;
import mil.jpeojtrs.sca.spd.Runtime;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
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
public abstract class NewScaResourceWizard extends Wizard implements INewWizard, ScaImplementationWizard2 {

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
	
	private Implementation impl = SpdFactory.eINSTANCE.createImplementation();
	private final ProgrammingLanguage progLang = SpdFactory.eINSTANCE.createProgrammingLanguage();
	private final HumanLanguage humanLang = SpdFactory.eINSTANCE.createHumanLanguage();
	private final Compiler compiler = SpdFactory.eINSTANCE.createCompiler();
	private final Runtime runtime = SpdFactory.eINSTANCE.createRuntime();
	
	public NewScaResourceWizard(final String componentType) {
		super();
		this.componentType = componentType;
		initImpl(this.impl);
	}

	@Override
	public void init(final IWorkbench workbench, final IStructuredSelection selection) {
		this.firstImplPage = 1;
	}
	
	/**
	 * Creates a default implementation object.  Override with specific implementation if needed.
	 * @param implementation
	 * @since 8.1
	 */
	protected void initImpl(Implementation implementation) {
		implementation.setDescription("The implementation contains descriptive information about the template for a software component.");
		implementation.setId("");
		this.progLang.setName("");
		implementation.setProgrammingLanguage(this.progLang);
		
		this.humanLang.setName(RedhawkCodegenActivator.ENGLISH);
		implementation.setHumanLanguage(this.humanLang);
		
		implementation.setCompiler(this.compiler);
		implementation.setRuntime(this.runtime);
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

	@Override
	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		generatorChanged(impl, codeGeneratorDescriptor, null);
	}
	
	/**
	 * @since 8.1
	 */
	@Override
	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor, final String previousImplId) {
		if (this.initializing) {
			return;
		}

		// If the code generator has settings, update the page as needed
		if (codeGeneratorDescriptor != null) {
			// Go through the list of implementations to see if we already have the selected implementation in the list.
			int implIndex = -1;
			for (int i = 0; i < this.implList.size(); i++) {
				if (this.implList.get(i).getImplementation() == impl) {
					implIndex = i;
					break;
				}
			}
			
			// This is assuming that the codegen page is the last page since it is the last statically added page.
			// TODO: Can we remove this now?  Or is it needed down the line where its not going to be properly set?
			int codegenIndex = this.wizPages.size();
			ICodegenWizardPage oldGenPages[] = null;
			ImplementationSettings settings = null;
			int numOfOldGenPages = -1;
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
						codegenIndex = getWizardPageIndex(page) + 1;

						//TODO: Remove this if the thing above worked.
//						codegenIndex = i + 1;  // Index of where new codeGenPages should be added.
						

						// Three scenarios:
						// - Implementation is the last in the list and it
						// doesn't have a settings page previously
						// - The implementation is in the middle of the list
						// without a settings page
						// - There is a settings page after this implementation
						// page, replace it
						// Store the generator page if it's next, otherwise
						// we'll be inserting one
						
						// If the wizard has an old codeGen page we need to snatch up then the wizards size will be greater or equal than codeGen index + 1.
						// If the wizard has an old codeGen page we need to snatch up then the old codeGen page will be of type ICodegenWizardPage
						if (codegenIndex + 1 <= this.wizPages.size()  && (this.wizPages.get(codegenIndex) instanceof ICodegenWizardPage)) {
							ICodegenWizardPage[] oldCodeGenPages = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(previousImplId);
							numOfOldGenPages = oldCodeGenPages.length;
							
							List<IWizardPage> tmpList = new ArrayList<IWizardPage>();
							for (int ii = 0; ii < numOfOldGenPages; ii++) {
								tmpList.add(this.wizPages.get(codegenIndex + ii));
							}
							
							oldGenPages = tmpList.toArray(new ICodegenWizardPage[tmpList.size()]);
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
			ICodegenWizardPage codeGenPages[] = null;
			boolean createControl = true;
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGeneratorDescriptor.getId(),
			        this.componentType);

			// Add the new page first
			if (settings != null && templates.length > 0) {
				// findPageByGeneratorId is always guaranteed to return at least
				// one page.
				codeGenPages = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId());

				if (oldGenPages != null) {
					// Determine if this is the same set of codegen Pages as the previous selection. 
					boolean sameSetOfPages = true;
					if (numOfOldGenPages == codeGenPages.length) {
						for (int i = 0; i < numOfOldGenPages; i++) {
							if (oldGenPages[i].getClass() != codeGenPages[i].getClass()) {
								sameSetOfPages = false;
								//TODO: This still needed?
//								codeGenPage.setCanFlipToNextPage(oldGenPage.canFlipToNextPage());
								break;
							}
						}
					} else {
						sameSetOfPages = false;
					}
					
					// If the generator pages are different, add the new ones
					if (!sameSetOfPages) {
						int tmpCodegenIndex = codegenIndex;
						for (IWizardPage newCodeGenPage : codeGenPages) {
							this.wizPages.add(tmpCodegenIndex, newCodeGenPage);
							tmpCodegenIndex++;
						}

						// Otherwise get rid of the one we just created and
						// reuse it
					} else {
						for (IWizardPage newCodeGenPage : codeGenPages) {
							newCodeGenPage.dispose();
						}
						codeGenPages = oldGenPages;
						oldGenPages = null;
						
						createControl = false;
						
					}
				} else if (codegenIndex == this.wizPages.size()) {
					for (IWizardPage codeGenPage : codeGenPages) {
						this.wizPages.add(codeGenPage);
					}
				} else {
					int tmpCodegenIndex = codegenIndex;
					for (ICodegenWizardPage codeGenPage : codeGenPages) {
						this.wizPages.add(tmpCodegenIndex, codeGenPage);
						tmpCodegenIndex++;
					}

				}
			}

			if (oldGenPages != null) {
				for (IWizardPage oldGenPage : oldGenPages) {
					this.wizPages.remove(oldGenPage);
					oldGenPage.dispose();
				}
			}

			// Initialize the settings page
			if (codeGenPages != null && settings != null && templates.length > 0) {
				
				settings.setOutputDir(null); // let the page pick the outputdir
				
					for (ICodegenWizardPage codeGenPage : codeGenPages) {
						if (createControl) {
							codeGenPage.setWizard(this);
							codeGenPage.setCanFinish(true);
							codeGenPage.setCanFlipToNextPage(true);
						}
						// Configure the wizard page with the current settings
						codeGenPage.configure(this.getSoftPkg(), impl, codeGeneratorDescriptor, settings, this.componentType);
					}
				
			}
		}

		// Force an update on the buttons, changing the generator may have
		// enabled or disabled the display of one of the buttons
		this.getContainer().updateButtons();
	}

	/**
	 * Provides the index of the given wizard page.
	 * @param page The page who's index you are looking for
	 * @return
	 */
	private int getWizardPageIndex(IWizardPage page) {
		IWizard pageWizard = page.getWizard();
		
		IWizardPage[] allPages = pageWizard.getPages();
		List<IWizardPage> arrayOfPages = Arrays.asList(allPages);
		
		return arrayOfPages.indexOf(page);
	}

	@Override
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
			final Implementation pairImpl = pair.getImplementation();
			final ImplementationSettings settings = pair.getImplementationSettings();

			if (pairImpl.getCode() != null) {
				if (!pairImpl.getCode().getLocalFile().toString().equals(settings.getOutputDir())) {
					final ICodeGeneratorDescriptor desc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());

					if (desc != null) {
						try {
							final IScaComponentCodegen generator = desc.getGenerator();
							Assert.isNotNull(pairImpl.getSoftPkg());
							final Code code = generator.getInitialCodeSettings(pairImpl.getSoftPkg(), settings, pairImpl);

							pairImpl.setCode(code);
						} catch (final CoreException e) {
							//PASS
						}
					}
				}
			}
		}
	}

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
				page.setImpl(this.impl);
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
				} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
					RedhawkCodegenActivator.logInfo("Unable to find the wavedev settings file, using defaults.");
					waveSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
				}

				// Make sure there are default settings for all implementations
				CodegenUtil.recreateImplSettings(getSoftPkg(), waveSettings);

				setImportedSettingsMap(CodegenFileHelper.settingsHasSourceCode(waveSettings, fileURI));

				setInitializing(true);

				try {
					// Loop through all the implementations
					for (final Implementation pkgImpl : getSoftPkg().getImplementation()) {
						final ImplementationSettings oldImplSettings = waveSettings.getImplSettings().get(pkgImpl.getId());

						// Create and add the page for the implementation
						final ImplementationWizardPage page = new ImplementationWizardPage("", getSoftPkg());
						page.setImpl(this.impl);
						addPage(page);

						// Import the implementation
						page.importImplementation(pkgImpl, oldImplSettings);

						final ImplementationSettings settings = page.getImplSettings();

						final Boolean found = getImportedSettingsMap().get(pkgImpl.getId());
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
							final ICodegenWizardPage[] codeGenPages = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId());
							for (ICodegenWizardPage codeGenPage : codeGenPages) {
								addPage(codeGenPage);
								// Enable the canFlip if this isn't the last page in the list
								// TODO: Determine if the next line is needed.
								//codeGenPage.setCanFlipToNextPage((getImplList().size() + 1) != getSoftPkg().getImplementation().size());
								
								// Configure the wizard page with the current settings
								codeGenPage.configure(getSoftPkg(), getImplementation(), codeGen, settings, getType());
							}

						}

						// Save the settings
						getImplList().add(new ImplementationAndSettings(pkgImpl, settings));
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
		final boolean isCreateNewResource = this.resourcePropertiesPage.isCreateNewResource();
		final IWorkingSet[] workingSets = this.resourcePropertiesPage.getSelectedWorkingSets();
		final String projectName = this.resourcePropertiesPage.getProjectName();
		final java.net.URI locationURI;
		if (this.resourcePropertiesPage.useDefaults()) {
			locationURI = null;
		} else {
			locationURI = this.resourcePropertiesPage.getLocationURI();
		}
		this.resourcePropertiesPage.getProjectHandle();
		final IPath existingResourceLocation = this.resourcePropertiesPage.getExistingResourcePath();

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
							setOpenEditorOn(createComponentFiles(project, projectName, getSoftPkg().getId(), null, progress.newChild(1)));

							// Create the implementation
							final ImplementationWizardPage page = (ImplementationWizardPage) getWizPages().get(1);
							final Implementation pageImpl = page.getImplementation();
							final ImplementationSettings settings = page.getImplSettings();
							ProjectCreator.addImplementation(project, projectName, pageImpl, settings, progress.newChild(1));
						} else {
							setOpenEditorOn(ProjectCreator.importFiles(project, existingResourceLocation, getImplList(), getImportedSettingsMap(), progress.newChild(2), getSoftPkg().getId()));
						}
						
						String spdFileName = project.getName() + SpdPackage.FILE_EXTENSION; //SUPPRESS CHECKSTYLE AvoidInLine
						final IFile spdFile = project.getFile(spdFileName);
						
						// Allows for subclasses to modify the project
						modifyResult(project, spdFile,  progress.newChild(1));
						
						// Allows for codegenerators to add to the project.
						for (ImplementationAndSettings implAndSettings : getImplList()) {
							String generatorId = implAndSettings.getImplementationSettings().getGeneratorId();
							ICodeGeneratorPageRegistry codegenRegistry = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry();
							if (codegenRegistry instanceof ICodeGeneratorPageRegistry2) {
								List<ICodegenDisplayFactory> codegenDisplayFactories = ((ICodeGeneratorPageRegistry2) codegenRegistry).findCodegenDisplayFactoriesByGeneratorId(generatorId);
								
								for (ICodegenDisplayFactory factory : codegenDisplayFactories) {
									if (factory instanceof ICodegenDisplayFactory2) {
										((ICodegenDisplayFactory2) factory).modifyProject(project, spdFile, progress.newChild(1));
									}
								}
							}
							
						}
						project.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(1));

					} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
						if (project != null) {
							project.delete(true, progress.newChild(1));
						}
						throw e;
					}
				} catch (final CoreException e) {
					throw e;
				} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
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
	 * Called before the Wizard is finished to allow classes extending this class to add additional behavior. 
	 * @since 8.1
	 */
	protected void modifyResult(IProject project, IFile spdFile, SubMonitor newChild) throws CoreException {
		// Do nothing by default
		
	}

	/**
     * @since 8.0
     */
	protected abstract IFile createComponentFiles(IProject project, String spdName, String id, String author, IProgressMonitor monitor) throws CoreException;

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
		return this.impl;
	}

	/**
     * @since 8.0
     */
	protected String getID() {
		// Figure out the ID we'll use 
		return this.resourcePropertiesPage.getID();
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
