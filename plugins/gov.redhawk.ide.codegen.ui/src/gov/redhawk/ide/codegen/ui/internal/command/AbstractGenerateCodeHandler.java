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
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ui.RedhawkUiActivator;

/**
 * This handler is the main entry point to code generation in the UI.
 * It performs several checks, possibly upgrading project file(s) or settings, and then invokes the code generator.
 */
public abstract class AbstractGenerateCodeHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		// If the user used a context menu, generate code on the selection(s)
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection != null && !selection.isEmpty()) {
			handleMenuSelection(event, selection);
			return null;
		}

		// If the user clicked the generate code button in an editor, generate code on the editor input
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor != null) {
			handleEditorSelection(event, editor);
			return null;
		}

		// If we get here, somehow the generate code handler was triggered from somewhere it shouldn't be - log this
		RedhawkCodegenUiActivator.logError("Generate node handler was triggered without a valid selection", null);
		return null;
	}

	protected abstract void handleEditorSelection(ExecutionEvent event, IEditorPart editor);

	protected abstract void handleMenuSelection(ExecutionEvent event, ISelection selection);

	/**
	 * Tries to save the resources which are in the same project as the editorFile provided. The user is prompted to
	 * save if any related unsaved resources are present.
	 * @param event Handler event
	 * @param editorFile File who's project we are using to find related editor pages.
	 * @return True if everything saved correctly. False otherwise.
	 * @throws CoreException
	 */
	protected boolean saveRelatedResources(final Shell shell, final IProject parentProject) throws CoreException {
		final Set<ISaveablePart> dirtyPartsSet = getRelatedDirtyParts(parentProject);
		if (dirtyPartsSet.isEmpty()) {
			// Nothing needs to be saved
			return true;
		}

		// Prompt the user that they MUST save before generation
		if (!MessageDialog.openQuestion(shell, "File Changed", "Resources in the project '" + parentProject.getName()
			+ "' have unsaved changes.  Changes must be saved prior to code generation.\n\nDo you want to save these changes now?")) {
			// User said no to saving
			return false;
		}

		ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
		try {
			dialog.run(false, true, monitor -> {
				SubMonitor progress = SubMonitor.convert(monitor, "Saving editors...", dirtyPartsSet.size());
				for (ISaveablePart dirtyPart : dirtyPartsSet) {
					dirtyPart.doSave(progress.split(1));
				}
			});
			return true;
		} catch (InvocationTargetException e) {
			throw new CoreException(new Status(Status.ERROR, RedhawkUiActivator.PLUGIN_ID, "Error while attempting to save editors", e.getCause()));
		} catch (InterruptedException e) {
			// The user canceled the save while it was running
			return false;
		}
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
		return dirtyPartsSet;
	}
}
