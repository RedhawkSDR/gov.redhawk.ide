/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.codegen.ui.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;

/**
 * Utility class that allows accessing the files associated with project documentation.
 * @since 9.2
 */
public class DocumentationUtils {

	private DocumentationUtils() {
	}

	/**
	 * Opens an editor for the header file for a given project. The file is created if it does not exist.
	 * @param project
	 * @param page the active workbench page
	 */
	public static void openHeader(IProject project, IWorkbenchPage page) {
		IFile header = project.getFile("HEADER"); //$NON-NLS-1$
		createAndOpenFile(header, Messages.DocumentationUtils_HeaderContents, page, Messages.DocumentationUtils_HeaderCreateError);
	}

	private static void createAndOpenFile(IFile file, String fileContents, IWorkbenchPage page, String creationError) {
		// Create the file if it doesn't exist
		if (!file.exists()) {
			Shell shell = page.getWorkbenchWindow().getShell();
			IRunnableWithProgress op = (monitor) -> {
				try (InputStream inputStream = new ByteArrayInputStream(fileContents.getBytes())) {
					file.create(inputStream, false, monitor);
				} catch (IOException ex) {
					throw new InvocationTargetException(ex);
				} catch (CoreException ex) {
					throw new InvocationTargetException(ex);
				}
			};

			try {
				new ProgressMonitorDialog(shell).run(true, true, op);
			} catch (InvocationTargetException ex) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, creationError, ex.getCause()),
					StatusManager.SHOW | StatusManager.LOG);
				return;
			} catch (InterruptedException ex) {
				return;
			}
		}

		// Open an editor for the file
		try {
			IDE.openEditor(page, file);
		} catch (PartInitException ex) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, Messages.DocumentationUtils_OpenEditorError, ex),
				StatusManager.SHOW | StatusManager.LOG);
		}
	}
}
