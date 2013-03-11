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
package gov.redhawk.eclipsecorba.library.internal;

import gov.redhawk.ide.util.ResourceUtils;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * 
 */
public class ResetLibraryFileHandler extends AbstractHandler implements IHandler {

	/**
	 * {@inheritDoc}
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection instanceof IStructuredSelection) {
			final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			for (final Object obj : structuredSelection.toList()) {
				final IProject project = getAdapter(IProject.class, obj);
				if (project != null) {
					final IFile libraryFile = project.getFile(".library");
					final WorkspaceJob job = new WorkspaceJob("Reset IDL File " + libraryFile) {

						@Override
						public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
							ResourceUtils.createIdlLibraryResource(libraryFile, monitor);
							return Status.OK_STATUS;
						}
					};
					if (libraryFile.exists()) {
						job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().modifyRule(libraryFile));
					} else {
						job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().createRule(libraryFile));
					}
					job.setSystem(true);
					job.schedule();
				}
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private < T > T getAdapter(final Class<T> type, final Object obj) {
		if (type.isInstance(obj)) {
			return (T) obj;
		}
		if (obj instanceof IAdaptable) {
			return (T) ((IAdaptable) obj).getAdapter(type);
		}
		return (T) Platform.getAdapterManager().getAdapter(obj, type);
	}
}
