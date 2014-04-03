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
package gov.redhawk.ide.codegen.ui.internal.command;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.codegen.ui.IComponentProjectUpgrader;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.preferences.CodegenPreferenceConstants;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.ide.ui.wizard.IRedhawkImportProjectWizardAssist;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.RedhawkUiActivator;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;

public class GenerateCodeHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		// GenerateCode Class simply takes an object to generate from. It should be either a list of implementations, an
		// implementation or IFile

		// If the user used a context menu, generate code on the selection(s)
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);

		// for each of the elements in the selection
		// if its empty or null then its the editor (IFile)
		// if its not empty / null then it should be either an Implementation or an IFile.
		// We need to get the project so we can save related resources.
		// Fom the IFile its easy, from the implementation we need to get the EMF Resource and get the IFile from there

		// If the menu selection is empty then Generate button was pressed within the editor
		if (selection == null || selection.isEmpty()) {
			final IEditorPart editor = HandlerUtil.getActiveEditor(event);
			if (editor != null) {
				if (editor instanceof SCAFormEditor) {
					SCAFormEditor scaEditor = (SCAFormEditor) editor;
					SoftPkg spd = SoftPkg.Util.getSoftPkg(scaEditor.getMainResource());
					if (spd != null) {
						IProject project = ModelUtil.getProject(spd);
						try {
							saveAndGenerate(spd, project, HandlerUtil.getActiveShell(event));
						} catch (CoreException e) {
							StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
							return null;
						}
						return null;
					}
				} else {
					final IEditorInput editorInput = editor.getEditorInput();
					if (editorInput instanceof IFileEditorInput) {
						final IFile f = ((IFileEditorInput) editorInput).getFile();
						if (isSpdFile(f)) {
							try {
								saveAndGenerate(f, f.getProject(), HandlerUtil.getActiveShell(event));
							} catch (CoreException e) {
								StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
								return null;
							}
							return null;
						}
					}
				}
			}
		} else { // The selection was made either via the right click menu from the Project Explorer, or in the
					// Implementations tab.
			if (selection instanceof IStructuredSelection) {
				final IStructuredSelection ss = (IStructuredSelection) selection;

				for (Object obj : ss.toList()) {
					if (obj instanceof IFile && isSpdFile((IFile) obj)) {
						try {
							saveAndGenerate(obj, ((IFile) obj).getProject(), HandlerUtil.getActiveShell(event));
						} catch (CoreException e) {
							StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
							return null;
						}
					} else if (obj instanceof Implementation) {
						Implementation impl = (Implementation) obj;
						String platformURI = impl.eResource().getURI().toPlatformString(true);
						IResource spdFile = ResourcesPlugin.getWorkspace().getRoot().findMember(platformURI);

						if (spdFile instanceof IFile && isSpdFile((IFile) spdFile)) {
							try {
								saveAndGenerate(impl, ((IFile) spdFile).getProject(), HandlerUtil.getActiveShell(event));
							} catch (CoreException e) {
								StatusManager.getManager().handle(e.getStatus(), StatusManager.SHOW | StatusManager.LOG);
								return null;
							}
						}
					}
				}
				return null;
			}
		}

		// If we get here, somehow the generate code handler was triggered from somewhere it shouldn't be - log this
		RedhawkCodegenUiActivator.logError("Generate code handler was triggered without a valid selection", null);
		return null;
	}

	/**
	 * Attempts to save the project associated with the object to generate if there are unsaved changes. The object to
	 * generate
	 * is then passed into the GenerateCode class for code generation.
	 * @param objectToGenerate The object which will be passed into the GenerateCode's generate method.
	 * @param parentProject The IProject which contains the objectToGenerate resource
	 * @param shell A shell used for dialog generation
	 * @return True if code generation has been attempted.
	 * @throws CoreException
	 */
	private boolean saveAndGenerate(Object objectToGenerate, IProject parentProject, Shell shell) throws CoreException {
		if (relatedResourcesSaved(shell, parentProject)) {
			try {
				checkDeprecated(objectToGenerate, shell);
			} catch (OperationCanceledException e) {
				return false;
			}
			GenerateCode.generate(shell, objectToGenerate);
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private void checkDeprecated(Object selectedObj, Shell parent) throws CoreException {
		boolean enableDeprecated = RedhawkCodegenUiActivator.getDefault().getPreferenceStore().getBoolean(
			CodegenPreferenceConstants.P_ENABLE_DEPRECATED_CODE_GENERATORS);
		// Skip check since the user has asked to enable deprecated generators
		if (enableDeprecated) {
			return;
		}
		if (selectedObj instanceof SoftPkg) {
			checkDeprecatedImpls(parent, ((SoftPkg) selectedObj).getImplementation());
		} else if (selectedObj instanceof EList) {
			checkDeprecatedImpls(parent, (List<Implementation>) selectedObj);
		} else if (selectedObj instanceof Implementation) {
			final List<Implementation> impls = new ArrayList<Implementation>();
			impls.add((Implementation) selectedObj);
			checkDeprecatedImpls(parent, impls);
		} else if (selectedObj instanceof IFile) {
			// The selected object should be an IFile for the SPD
			final IFile file = (IFile) selectedObj;
			final URI spdURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
			final SoftPkg softpkg = ModelUtil.loadSoftPkg(spdURI);
			checkDeprecatedImpls(parent, softpkg.getImplementation());
		}
	}

	private void checkDeprecatedImpls(Shell parent, List<Implementation> impls) throws CoreException {
		if (impls == null || impls.isEmpty()) {
			throw new OperationCanceledException();
		}

		final SoftPkg softPkg = (SoftPkg) impls.get(0).eContainer();
		final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);
		boolean hasDeprecated = false;
		for (final Implementation impl : impls) {
			hasDeprecated = isDeprecated(impl, waveDev);
			if (hasDeprecated) {
				break;
			}
		}
		if (hasDeprecated && shouldUpgrade(parent, softPkg.getName())) {
			upgrade(parent, softPkg, waveDev);
		}
	}

	private void upgrade(Shell parent, final SoftPkg spd, final WaveDevSettings implSettings) throws CoreException {
		ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(parent);
		try {
			progressDialog.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						IComponentProjectUpgrader service = RedhawkCodegenUiActivator.getDefault().getComponentProjectUpgraderService();
						if (service != null) {
							service.upgrade(monitor, spd, implSettings);
						} else {
							throw new CoreException(new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to find project upgrade service.",
								null));
						}
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e1) {
			if (e1.getCause() instanceof CoreException) {
				CoreException core = (CoreException) e1.getCause();
				throw core;
			} else if (e1.getCause() instanceof OperationCanceledException) {
				throw new OperationCanceledException();
			} else {
				Status status = new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to update code generator.", e1.getCause());
				throw new CoreException(status);
			}
		} catch (InterruptedException e1) {
			throw new OperationCanceledException();
		}
	}

	private boolean isDeprecated(Implementation impl, WaveDevSettings waveDev) throws CoreException {
		if (waveDev == null) {
			waveDev = generateWaveDev(impl.getSoftPkg());
		}
		if (waveDev == null) {
			throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID, "GENERATE FAILED: Failed to find implementation settings in "
				+ impl.getSoftPkg().getName() + ".wavedev file", null));
		}
		final ImplementationSettings implSettings = waveDev.getImplSettings().get(impl.getId());
		if (implSettings != null) {
			ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(implSettings.getGeneratorId());
			if (generator != null) {
				return generator.isDeprecated();
			} else {
				// Can't find generator assume then deprecated
				return true;
			}
		} else {
			// try to auto-generate implementation settings
			ImplementationSettings generatedImplSettings = generateWaveDev(impl.getSoftPkg()).getImplSettings().get(impl.getId());
			if (generatedImplSettings != null) {
				ICodeGeneratorDescriptor generator = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(generatedImplSettings.getGeneratorId());
				if (generator != null) {
					return generator.isDeprecated();
				} else {
					// Can't find generator assume then deprecated
					return true;
				}
			} else {
				throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID,
					"GENERATE FAILED: Failed to find implementation settings for implementation: " + impl.getId(), null));
			}
		}
	}

	private WaveDevSettings generateWaveDev(SoftPkg softPkg) throws CoreException {

		WaveDevSettings waveDev = CodegenFactory.eINSTANCE.createWaveDevSettings();

		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			final ImplementationSettings settings = CodegenFactory.eINSTANCE.createImplementationSettings();
			final String lang = impl.getProgrammingLanguage().getName();
			// Find the code generator if specified, otherwise pick the first
			// one returned by the registry
			ICodeGeneratorDescriptor codeGenDesc = null;
			final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
			if (codeGens.length > 0) {
				codeGenDesc = codeGens[0];
			}

			if (codeGenDesc != null) {
				final IScaComponentCodegen generator = codeGenDesc.getGenerator();

				// Assume that there is <name>[/].+<other> format for the entry point
				// Pick out <name> for both the output directory and settings name
				final String lf = impl.getCode().getEntryPoint();

				// Set the generator, settings name and output directory
				settings.setGeneratorId(generator.getClass().getCanonicalName());
				settings.setOutputDir(lf.substring(0, lf.lastIndexOf('/')));

				// pick the first selectable and defaultable template returned by the registry
				ITemplateDesc templateDesc = null;
				final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(settings.getGeneratorId());
				for (final ITemplateDesc itd : templates) {
					if (itd.isSelectable() && !itd.notDefaultableGenerator()) {
						templateDesc = itd;
						break;
					}
				}
				// If we found the template, use it
				if (templateDesc != null) {
					// Set the properties to their default values
					for (final IPropertyDescriptor prop : templateDesc.getPropertyDescriptors()) {
						final Property p = CodegenFactory.eINSTANCE.createProperty();
						p.setId(prop.getKey());
						p.setValue(prop.getDefaultValue());
						settings.getProperties().add(p);
					}
					// Set the template
					settings.setTemplate(templateDesc.getId());
					for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
						if (assistant.handlesLanguage(lang)) {
							settings.setTemplate(assistant.getDefaultTemplate());
							break;
						}
					}
				}
			}

			for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
				if (assistant.handlesLanguage(lang)) {
					assistant.setupWaveDev(softPkg.getName(), settings);
					break;
				}
			}
			waveDev.getImplSettings().put(impl.getId(), settings);
		}

		// Create the URI to the .wavedev file
		final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createPlatformResourceURI(softPkg.getName() + "/." + softPkg.getName()
			+ ".wavedev", false);
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final Resource res = set.createResource(uri);

		// Add the WaveDevSettings to the resource and save to disk to persist the newly created WaveDevSettings
		res.getContents().add(waveDev);
		try {
			res.save(null);
		} catch (final IOException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}

		return waveDev;
	}

	private boolean shouldUpgrade(Shell parent, String name) throws CoreException {
		String message = name + " uses deprecated code generators.\n\n" + "Would you like to upgrade this project?";
		MessageDialog dialog = new MessageDialog(parent, "Deprecated Generator", null, message, MessageDialog.WARNING, new String[] { "Upgrade", "Cancel" }, 1);
		switch (dialog.open()) {
		case 0: // Upgrade
			return true;
		case 1:// Cancel
		default:
			throw new OperationCanceledException();
		}
	}

	/**
	 * Tries to save the resources which are in the same project as the editorFile provided. The user is prompted to
	 * save
	 * if any related unsaved resources are present.
	 * @param event Handler event
	 * @param editorFile File who's project we are using to find related editor pages.
	 * @return True if everything saved correctly. False otherwise.
	 * @throws CoreException
	 */
	private boolean relatedResourcesSaved(final Shell shell, final IProject parentProject) throws CoreException {

		final Set<ISaveablePart> dirtyPartsSet = getRelatedDirtyParts(parentProject);

		// If there were unsaved parts in this project.
		if (!dirtyPartsSet.isEmpty()) {

			// Prompt the user that they MUST save before generation
			if (MessageDialog.openQuestion(shell, "File Changed", "Resources in the project '" + parentProject.getName()
				+ "' have unsaved changes.  Changes must be saved prior to code generation.\n\nDo you want to save these changes now?")) {

				ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
				try {
					dialog.run(false, true, new IRunnableWithProgress() {
						@Override
						public void run(IProgressMonitor monitor) {
							monitor.beginTask("Saving Resources ...", dirtyPartsSet.size());

							// Go through and save each of the parts that were previously identified.
							for (ISaveablePart dirtyPart : dirtyPartsSet) {
								dirtyPart.doSave(monitor);
								monitor.worked(1);
								if (monitor.isCanceled()) {
									break;
								}
							}
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID, "Error while attempting to save editors", e.getCause()));
				} catch (InterruptedException e) {
					return false; // The user canceled this save dialog.
				}

				if (dialog.getProgressMonitor().isCanceled()) {
					return false; // The user has canceled another dialog which was passed our monitor.
				}

				return true; // User saved all unsaved parts with no errors, generate code.
			}

			// User canceled the save dialog do not generate
			return false;
		}

		// Resources don't need to be saved
		return true;
	}

	/**
	 * Returns any ISavableParts which are part of the same project as the given editor file.
	 * @param editorFile The editor file who's project you want to find the other dirty parts of
	 * @return A set of dirty ISavableParts from the same project as the editorFile.
	 */
	private Set<ISaveablePart> getRelatedDirtyParts(final IProject project) {
		final Set<ISaveablePart> dirtyPartsSet = new HashSet<ISaveablePart>();

		// Go through each of the workbench windows pages
		for (IWorkbenchPage page : PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages()) {
			// If the page contains at least one dirty editor
			if (page.getDirtyEditors().length != 0) {
				// Go through each of the dirty editor parts and see if they belong to the referenced project.
				for (IWorkbenchPart dirtyPart : page.getDirtyEditors()) {
					if (dirtyPart instanceof IEditorPart && ((IEditorPart) dirtyPart).getEditorInput() instanceof IFileEditorInput) {
						IFileEditorInput input = (IFileEditorInput) ((IEditorPart) dirtyPart).getEditorInput();
						if (input.getFile().getProject().equals(project)) {
							if (dirtyPart instanceof ISaveablePart) {
								dirtyPartsSet.add((ISaveablePart) dirtyPart);
							}
						}
					}
				}
			}
		}
		return dirtyPartsSet;
	}

	/**
	 * Checks the file extension to see if it ends with the SpdPackage extension of ".spd.xml". Returns false if file is
	 * null.
	 * @param file The file under test
	 * @return True if filename ends with .spd.xml false otherwise or if null.
	 */
	private boolean isSpdFile(final IFile file) {
		if (file == null) {
			return false;
		}
		return (file.getName().endsWith(SpdPackage.FILE_EXTENSION));
	}

	@Override
	public void setEnabled(final Object evaluationContext) {
		final IEvaluationContext context = (IEvaluationContext) evaluationContext;
		final IWorkbenchWindow window = (IWorkbenchWindow) context.getVariable("activeWorkbenchWindow");
		if (window == null) {
			setBaseEnabled(false);
			return;
		}

		// We check two things.
		// 1.) If the user has right clicked on an item that is appropriate
		// 2.) If not then we check the open active editor.

		// The highest priority check is the right click action. If they have right clicked on something that object is
		// the proper context
		// so we check that first.
		if (context.getVariable("activeMenuSelection") != null && context.getVariable("activeMenuSelection") instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) context.getVariable("activeMenuSelection");

			for (Object selection : ss.toList()) {
				if (selection instanceof IFile && isSpdFile((IFile) selection) && waveDevFileExists((IFile) selection)) {
					continue;
				} else if (selection instanceof Implementation) {
					Implementation impl = (Implementation) selection;
					WaveDevSettings wavDev = CodegenUtil.getWaveDevSettings(impl);

					if (wavDev != null) {
						continue;
					}
				}

				// One of the selections did not meet the tests above.
				setBaseEnabled(false);
				return;
			}

			// If we made it through the for loop then all the objects passed.
			setBaseEnabled(true);
			return;
		} else if (window.getActivePage() != null) {
			// If we've gotten this far we know they have not right clicked on anything so all we need to check is the
			// open active editor
			IWorkbenchPage activePage = window.getActivePage();
			IEditorPart activeEditor = activePage.getActiveEditor();
			if (activeEditor != null) {
				if ((activeEditor.getEditorInput() instanceof FileEditorInput)) {
					IFile spdFile = ((FileEditorInput) activeEditor.getEditorInput()).getFile();
					if (isSpdFile(spdFile)) {
						setBaseEnabled(waveDevFileExists(spdFile));
						return;
					}
				}
			}
		}

		// Set to false if none of the appropriate situations are met
		setBaseEnabled(false);
		return;
	}

	/**
	 * Helper method where we attempt to find the WavedevSettings file given either a PRF or SPD file
	 * 
	 * @param file An IFile element, either PRF or SPD
	 * @return The associated WavedevSettings file should we be able to find it
	 */
	private Boolean waveDevFileExists(final IFile file) {
		if (file != null && file.exists()) {
			String filePath = "";
			if (file.getFullPath().toString().endsWith(SpdPackage.FILE_EXTENSION)) {
				filePath = file.getFullPath().toString();
			} else if (file.getFullPath().toString().endsWith(PrfPackage.FILE_EXTENSION)) {
				filePath = file.getFullPath().toString().replace(PrfPackage.FILE_EXTENSION, SpdPackage.FILE_EXTENSION);
			} else {
				return false;
			}
			final URI spdUri = URI.createPlatformResourceURI(filePath, true).appendFragment(SoftPkg.EOBJECT_PATH);
			final URI waveUri = URI.createPlatformPluginURI(CodegenUtil.getWaveDevSettingsURI(spdUri).lastSegment(), true).appendFragment("/");
			final IFile waveDevFile = file.getProject().getFile(waveUri.lastSegment());

			return waveDevFile.isSynchronized(IResource.DEPTH_INFINITE) && waveDevFile.exists();
		}

		return false;
	}
}
