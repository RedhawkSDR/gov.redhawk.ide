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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.ui.ICodeGeneratorPageRegistry;
import gov.redhawk.ide.codegen.ui.ICodeGeneratorPageRegistry2;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodegenDisplayFactory2;
import gov.redhawk.ide.codegen.ui.ICodegenTemplateDisplayFactory;
import gov.redhawk.ide.codegen.ui.ICodegenWizardPage;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.spd.ui.ComponentUiPlugin;
import gov.redhawk.ide.spd.ui.wizard.ImplementationWizardPage;
import gov.redhawk.ide.spd.ui.wizard.ScaImplementationWizard2;
import gov.redhawk.sca.util.SubMonitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.Compiler;
import mil.jpeojtrs.sca.spd.HumanLanguage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.ProgrammingLanguage;
import mil.jpeojtrs.sca.spd.Runtime;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.statushandlers.StatusManager;

public class ImplementationWizard extends Wizard implements ScaImplementationWizard2 {

	private final ImplementationWizardPage implPage;
	private final String projectName;
	private final SoftPkg softPkg;
	private final EditingDomain editingDomain;
	private List<IWizardPage> wizPages;

	public ImplementationWizard(final EditingDomain editingDomain, final String name, final SoftPkg softPkg) {
		this.setWindowTitle("New Implementation");
		Implementation impl = SpdFactory.eINSTANCE.createImplementation();
		ProgrammingLanguage progLang = SpdFactory.eINSTANCE.createProgrammingLanguage();
		HumanLanguage humanLang = SpdFactory.eINSTANCE.createHumanLanguage();
		Compiler compiler = SpdFactory.eINSTANCE.createCompiler();
		Runtime runtime = SpdFactory.eINSTANCE.createRuntime();
		
		impl.setDescription("The implementation contains descriptive information about the template for a software resource.");
		impl.setId("");
		progLang.setName("");
		impl.setProgrammingLanguage(progLang);
		
		humanLang.setName(RedhawkCodegenActivator.ENGLISH);
		impl.setHumanLanguage(humanLang);
		
		impl.setCompiler(compiler);
		impl.setRuntime(runtime);
		
		this.implPage = new ImplementationWizardPage(name, softPkg);
		this.implPage.setImpl(impl);
		this.setNeedsProgressMonitor(true);
		this.projectName = name;
		this.softPkg = softPkg;
		this.editingDomain = editingDomain;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addPages() {
		try {
			final Field field = Wizard.class.getDeclaredField("pages");
			field.getModifiers();
			if (!Modifier.isPublic(field.getModifiers())) {
				field.setAccessible(true);
			}
			this.wizPages = (List<IWizardPage>) field.get(this);
		} catch (final SecurityException e1) {
			// PASS
		} catch (final NoSuchFieldException e1) {
			// PASS
		} catch (final IllegalArgumentException e) {
			// PASS
		} catch (final IllegalAccessException e) {
			// PASS
		}
		
		this.addPage(this.implPage);
	}

	@Override
	public boolean needsPreviousAndNextButtons() {
		return true;
	}

	@Override
	public boolean performFinish() {
		
		// Create the REDHAWK component project
		final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {
			
			@Override
			protected void execute(final IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
				try {
					final SubMonitor progress = SubMonitor.convert(monitor);
					IFile spdFile = (IFile) ResourcesPlugin.getWorkspace().getRoot().findMember(softPkg.eResource().getURI().toPlatformString(true));
					IProject project = spdFile.getProject();
					try {
						// If we're creating a new component (vs importing one)
						
						// Allows for codegenerators to add to the project.
						String generatorId = implPage.getImplSettings().getGeneratorId();
						
						ICodeGeneratorPageRegistry codegenRegistry = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry();
						if (codegenRegistry instanceof ICodeGeneratorPageRegistry2) {
							List<ICodegenDisplayFactory> codegenDisplayFactories = ((ICodeGeneratorPageRegistry2) codegenRegistry).findCodegenDisplayFactoriesByGeneratorId(generatorId);
							
							if (codegenDisplayFactories != null) {
								for (ICodegenDisplayFactory factory : codegenDisplayFactories) {
									if (factory instanceof ICodegenDisplayFactory2) {
										((ICodegenDisplayFactory2) factory).modifyProject(project,  spdFile, progress.newChild(1));
									}
								}
							}
						}
							
						
						// Allows for codegenerator templates to add to the project.
						String templateId = implPage.getImplSettings().getTemplate();
						ICodeGeneratorPageRegistry codegenTemplateRegistry = RedhawkCodegenUiActivator.getCodeGeneratorsTemplateRegistry();
						if (codegenTemplateRegistry instanceof ICodeGeneratorPageRegistry2) {
							List<ICodegenDisplayFactory> codegenDisplayFactories = ((ICodeGeneratorPageRegistry2) codegenTemplateRegistry).findCodegenDisplayFactoriesByGeneratorId(templateId);
							
							if (codegenDisplayFactories != null) {
								for (ICodegenDisplayFactory factory : codegenDisplayFactories) {
									if (factory instanceof ICodegenTemplateDisplayFactory) {
										((ICodegenTemplateDisplayFactory) factory).modifyProject(project, spdFile, progress.newChild(1));
									}
								}
							}
						}
						
					} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
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
		} catch (final InvocationTargetException e) {
			final IStatus status = new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Failed to create implementation.", e.getCause());
			StatusManager.getManager().handle(status, StatusManager.LOG | StatusManager.SHOW);
			return false;
		} catch (final InterruptedException e) {
			// PASS
		}

		return true;
	}

	/**
	 * Gets the implementation.
	 * 
	 * @return the implementation
	 */
	public Implementation getImplementation() {
		return this.implPage.getImplementation();
	}

	/**
	 * Gets the settings.
	 * 
	 * @return the settings
	 */
	public ImplementationSettings getSettings() {
		return this.implPage.getImplSettings();
	}

	@Override
	public void generatorChanged(final Implementation impl, final ICodeGeneratorDescriptor codeGeneratorDescriptor, final String previousImplId) {
		// If the code generator has settings, update the page as needed
		if (codeGeneratorDescriptor != null) {

			// First we find and remove the old pages.
			ICodegenWizardPage[] oldCodeGenPages = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(previousImplId);
			int numOfOldGenPages = oldCodeGenPages.length;
			
			// Only used these to determine number of pages, not needed
			for (ICodegenWizardPage oldCodeGenPage : oldCodeGenPages) {
				oldCodeGenPage.dispose();
			}
			
			List<IWizardPage> tmpList = new ArrayList<IWizardPage>();
			if (wizPages.size() > 1) {
				for (int i = 0; i < numOfOldGenPages; i++) {
					// The first page is always the implementation selection here.
					tmpList.add(wizPages.get(1 + i));
				}
			}
			
			ICodegenWizardPage[] oldGenPages = tmpList.toArray(new ICodegenWizardPage[tmpList.size()]);

			ImplementationSettings settings = this.implPage.getImplSettings();
			
			ICodegenWizardPage[] codeGenPages = null;
			
			boolean createControl = true;
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(codeGeneratorDescriptor.getId(),
				this.softPkg.getDescriptor().getComponent().getComponentType());

			// Add the new page first
			if (settings != null && templates.length > 0) {
				// findPageByGeneratorId is always guaranteed to return at least
				// one page.
				codeGenPages = RedhawkCodegenUiActivator.getCodeGeneratorsRegistry().findPageByGeneratorId(settings.getGeneratorId());

				if (oldGenPages != null && oldGenPages.length > 0) {
					// Determine if this is the same set of codegen Pages as the previous selection. 
					boolean sameSetOfPages = true;
					if (numOfOldGenPages == codeGenPages.length) {
						for (int i = 0; i < numOfOldGenPages; i++) {
							if (oldGenPages[i].getClass() != codeGenPages[i].getClass()) {
								sameSetOfPages = false;
								break;
							}
						}
					} else {
						sameSetOfPages = false;
					}
					
					// If the generator pages are different, add the new ones
					if (!sameSetOfPages) {
						int tmpCodegenIndex = 1;
						for (IWizardPage newCodeGenPage : codeGenPages) {
							wizPages.add(tmpCodegenIndex, newCodeGenPage);
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
				} else {
					int tmpCodegenIndex = 1;
					for (ICodegenWizardPage cgp : codeGenPages) {
						wizPages.add(tmpCodegenIndex, cgp);
						tmpCodegenIndex++;
					}

				}
			}

			if (oldGenPages != null && oldGenPages.length > 0) {
				for (IWizardPage oldGenPage : oldGenPages) {
					wizPages.remove(oldGenPage);
					oldGenPage.dispose();
				}
			}

			// Initialize the settings page
			if (codeGenPages != null && settings != null && templates.length > 0) {
				
				settings.setOutputDir(null); // let the page pick the outputdir
				
					for (ICodegenWizardPage cgp : codeGenPages) {
						if (createControl) {
							cgp.setWizard(this);
							cgp.setCanFinish(true);
							cgp.setCanFlipToNextPage(true);
						}
						// Configure the wizard page with the current settings
						cgp.configure(this.softPkg, impl, codeGeneratorDescriptor, settings, this.softPkg.getDescriptor().getComponent().getComponentType());
					}
				
			}
		}

		// Force an update on the buttons, changing the generator may have
		// enabled or disabled the display of one of the buttons
		this.getContainer().updateButtons();
	}
	
	@Override
	public boolean hasMoreImplementations(final Implementation curImpl) {
		return false;
	}

	public String getName() {
		return this.projectName;
	}

	@Override
	public void generatorChanged(Implementation impl, ICodeGeneratorDescriptor codeGeneratorDescriptor) {
		generatorChanged(impl, codeGeneratorDescriptor, null);
	}

	@Override
	public void addTemplatePages(IWizardPage pageAddingPages, ICodegenWizardPage[] pagesToAdd) {
		int addingPageIndex = wizPages.lastIndexOf(pageAddingPages);
		
		for (ICodegenWizardPage pageToAdd : pagesToAdd) {
			addingPageIndex++;
			wizPages.add(addingPageIndex, pageToAdd);
			pageToAdd.setWizard(this);
		}
		
		if (pageAddingPages instanceof ICodegenWizardPage) {
			if (pagesToAdd.length > 0) {
				((ICodegenWizardPage) pageAddingPages).setCanFinish(false);
				((ICodegenWizardPage) pageAddingPages).setCanFlipToNextPage(true);
			}
		}
	}

	@Override
	public void removeTemplatePages(IWizardPage pageAddingPages, ICodegenWizardPage[] pageTypesToRemove) {
		// The passed in array of pages is a new instance of the pages which we'd like removed.  We use them just to make sure
				// the right pages are being removed based on class type.  It's a bit of an assumption.
				int indexOfAdder = wizPages.indexOf(pageAddingPages);
				List<ICodegenWizardPage> pagesToRemove = new ArrayList<ICodegenWizardPage>();
				
				for (ICodegenWizardPage page : pageTypesToRemove) {
					
					if (wizPages.size() > indexOfAdder + 1 && wizPages.get(indexOfAdder + 1).getClass() == page.getClass()) {
						pagesToRemove.add((ICodegenWizardPage) wizPages.get(indexOfAdder + 1));
						indexOfAdder++;
					}
					// Dispose the unused page.
					page.dispose();
				}
				
				wizPages.removeAll(pagesToRemove);

				for (ICodegenWizardPage page : pagesToRemove) {
					page.dispose();
				}
				
				if (pageAddingPages instanceof ICodegenWizardPage) {
					((ICodegenWizardPage) pageAddingPages).setCanFinish(true);
					((ICodegenWizardPage) pageAddingPages).setCanFlipToNextPage(false);
				}
				
				// May be null on dispose of Wizard if user clicks cancel
				if (this.getContainer() != null) {
					this.getContainer().updateButtons();
				}
	}

}
