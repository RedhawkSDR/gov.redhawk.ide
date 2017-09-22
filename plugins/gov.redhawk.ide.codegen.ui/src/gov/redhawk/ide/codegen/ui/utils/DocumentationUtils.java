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

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
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
 * @since 10.0
 */
public class DocumentationUtils {

	private static final String HEADER_FILE_NAME = "HEADER"; //$NON-NLS-1$

	private DocumentationUtils() {
	}

	/**
	 * Adds/updates/removes an XML comment header.
	 * @param documentRootMixed the {@link FeatureMap} from calling <code>getMixed()</code> on the document root.
	 * @return True if changes were made to the document
	 */
	public static boolean setXMLCommentHeader(EditingDomain editingDomain, FeatureMap documentRootMixed, String commentText) {
		// Pad the comment text with newlines so it's not on the same line as the comment markers
		if (commentText != null) {
			commentText = '\n' + commentText + '\n';
		}

		// Create an EMF command to perform modifications
		Command command = null;
		if (documentRootMixed.size() > 0 && FeatureMapUtil.isComment(documentRootMixed.get(0))) {
			FeatureMap.Entry existingComment = documentRootMixed.get(0);
			if (commentText == null) {
				command = new RemoveCommand(editingDomain, documentRootMixed, existingComment);
			} else {
				if (!commentText.equals(existingComment.getValue())) {
					CompoundCommand compoundCommand = new CompoundCommand();
					compoundCommand.append(new RemoveCommand(editingDomain, documentRootMixed, existingComment));
					FeatureMap.Entry newComment = FeatureMapUtil.createCommentEntry(commentText);
					compoundCommand.append(new AddCommand(editingDomain, documentRootMixed, newComment, 0));
					command = compoundCommand;
				}
			}
		} else {
			if (commentText != null) {
				FeatureMap.Entry comment = FeatureMapUtil.createCommentEntry(commentText);
				command = new AddCommand(editingDomain, documentRootMixed, comment, 0);
			}
		}

		// If a command was created, run it
		if (command == null) {
			return false;
		}
		if (editingDomain != null) {
			editingDomain.getCommandStack().execute(command);
		} else {
			command.execute();
		}
		return true;
	}

	/**
	 * Opens an editor for the header file for a given project. The file is created if it does not exist.
	 * @param project
	 * @param page the active workbench page
	 */
	public static void openHeader(IProject project, IWorkbenchPage page) {
		IFile header = project.getFile(HEADER_FILE_NAME); // $NON-NLS-1$
		createAndOpenFile(header, Messages.DocumentationUtils_HeaderContents, page, Messages.DocumentationUtils_HeaderCreateError);
	}

	/**
	 * Gets the contents of the header file.
	 * @param project
	 * @return The header content, or null if the file does not exist
	 * @throws CoreException
	 */
	public static String getHeaderContents(IProject project) throws CoreException {
		IFile header = project.getFile(HEADER_FILE_NAME); // $NON-NLS-1$
		if (!header.exists()) {
			return null;
		}
		try (InputStream is = header.getContents(true)) {
			return IOUtils.toString(is, "UTF-8"); // $NON-NLS-1$
		} catch (IOException e) {
			IStatus status = new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, Messages.DocumentationUtils_HeaderReadError, e);
			throw new CoreException(status);
		}
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
