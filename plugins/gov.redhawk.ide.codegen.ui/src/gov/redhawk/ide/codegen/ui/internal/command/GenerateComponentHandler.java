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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.ManualGeneratorUtil;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.ide.codegen.ui.internal.upgrade.DeprecatedCodegenUtil;
import gov.redhawk.ide.codegen.ui.internal.upgrade.PropertyKindUtil;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.RedhawkUiActivator;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

/**
 * This handler is the main entry point to code generation for Components, Devices, Services, and Shared Libraries in
 * the UI.
 * 
 */
public class GenerateComponentHandler extends AbstractGenerateCodeHandler {

	@Override
	protected void handleEditorSelection(ExecutionEvent event, IEditorPart editor) {
		if (editor instanceof SCAFormEditor) {
			SCAFormEditor scaEditor = (SCAFormEditor) editor;

			// Get SoftPkg from the editor
			SoftPkg spd = SoftPkg.Util.getSoftPkg(scaEditor.getMainResource());
			if (spd == null) {
				RedhawkCodegenUiActivator.logError("Couldn't get SPD from editor in generate code handler", null);
				return;
			}

			IProject project = ModelUtil.getProject(spd);
			try {
				handleImplementations(spd.getImplementation(), project, HandlerUtil.getActiveShell(event));
			} catch (CoreException e) {
				StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
					StatusManager.SHOW | StatusManager.LOG);
			}
		} else {
			final IEditorInput editorInput = editor.getEditorInput();
			if (!(editorInput instanceof IFileEditorInput)) {
				RedhawkCodegenUiActivator.logError("Couldn't determine input from editor in generate code handler", null);
				return;
			}

			final IFile file = ((IFileEditorInput) editorInput).getFile();
			if (file == null || !file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				RedhawkCodegenUiActivator.logError("Editor input is not an SPD file in generate code handler", null);
				return;
			}

			try {
				final URI spdURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
				final SoftPkg softpkg = ModelUtil.loadSoftPkg(spdURI);
				handleImplementations(softpkg.getImplementation(), file.getProject(), HandlerUtil.getActiveShell(event));
			} catch (CoreException e) {
				StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
					StatusManager.SHOW | StatusManager.LOG);
			}
		}
	}

	@Override
	protected void handleMenuSelection(ExecutionEvent event, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			RedhawkCodegenUiActivator.logError("Generate code handler was triggered with an invalid selection", null);
			return;
		}

		final IStructuredSelection ss = (IStructuredSelection) selection;
		for (Object obj : ss.toList()) {
			if (obj instanceof IFile && ((IFile) obj).getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				IFile spdFile = (IFile) obj;
				try {
					final URI spdURI = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), false);
					final SoftPkg softpkg = ModelUtil.loadSoftPkg(spdURI);
					handleImplementations(softpkg.getImplementation(), spdFile.getProject(), HandlerUtil.getActiveShell(event));
				} catch (CoreException e) {
					StatusManager.getManager().handle(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
						StatusManager.SHOW | StatusManager.LOG);
				}
				return;
			} else if (obj instanceof Implementation) {
				Implementation impl = (Implementation) obj;
				String platformURI = impl.eResource().getURI().toPlatformString(true);
				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(platformURI);

				if (resource instanceof IFile && ((IFile) resource).getName().endsWith(SpdPackage.FILE_EXTENSION)) {
					try {
						handleImplementations(Collections.singletonList(impl), ((IFile) resource).getProject(), HandlerUtil.getActiveShell(event));
					} catch (CoreException e) {
						StatusManager.getManager().handle(
							new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, e.getLocalizedMessage(), e),
							StatusManager.SHOW | StatusManager.LOG);
						return;
					}
				}
			}
		}
	}

	/**
	 * First, saves resources in the project with unsaved changes. Performs several upgrade checks, and finally invokes
	 * code generation.
	 * @param impls The implementation(s) for which to generate code
	 * @param parentProject The IProject which contains all the implementation(s)
	 * @param shell The current shell for user interaction
	 * @throws CoreException
	 */
	public void handleImplementations(List<Implementation> impls, IProject parentProject, Shell shell) throws CoreException {
		if (impls == null || impls.size() == 0) {
			return;
		}

		// Prompt to save (if necessary) before continuing
		if (!saveRelatedResources(shell, parentProject)) {
			return;
		}

		// Ensure there's a .wavedev file
		final SoftPkg spd = (SoftPkg) impls.get(0).eContainer();
		WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(spd);
		if (waveDev == null) {
			waveDev = WaveDevUtil.generateWaveDev(spd);
		}
		if (waveDev == null) {
			String message = "Failed to find implementation settings in " + spd.getName() + ".wavedev file";
			throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID, message));
		}

		boolean shouldGenerate = true;
		try {
			DeprecatedCodegenUtil.checkDeprecated(shell, impls);
			PropertyKindUtil.checkProperties(shell, parentProject, impls);
			shouldGenerate = ManualGeneratorUtil.checkManualGenerator(shell, impls);
		} catch (OperationCanceledException e) {
			return;
		}
		// GenerateCode Class simply takes an object to generate from.
		// It should be either a list of implementations, an implementation or IFile
		if (shouldGenerate) {
			GenerateCode.generate(shell, impls);
		}
	}

	/**
	 * Tries to save the resources which are in the same project as the editorFile provided. The user is prompted to
	 * save if any related unsaved resources are present.
	 * @param event Handler event
	 * @param editorFile File who's project we are using to find related editor pages.
	 * @return True if everything saved correctly. False otherwise.
	 * @throws CoreException
	 */
	private boolean saveRelatedResources(final Shell shell, final IProject parentProject) throws CoreException {

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

}
