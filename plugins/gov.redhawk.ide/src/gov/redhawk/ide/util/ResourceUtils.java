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
package gov.redhawk.ide.util;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.eclipsecorba.library.util.LibraryResourceImpl;
import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.preferences.RedhawkIdePreferenceInitializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;

public final class ResourceUtils {
	private ResourceUtils() {

	}

	/**
	 * Creates a resource (file/folder/project). Missing parent directories (including project) are created, if
	 * necessary. Files are created without content (zero length).
	 * <p />
	 * Based on work by J A from Eclipse Corner.
	 * 
	 * @param resource The file, folder or project to create
	 * @param monitor 
	 * @throws CoreException A problem occurs creating a resource
	 */
	public static void create(final IResource resource, final IProgressMonitor monitor) throws CoreException {
		if (resource == null || resource.exists()) {
			return;
		}

		if (!resource.getParent().exists()) {
			ResourceUtils.create(resource.getParent(), monitor);
		}

		switch (resource.getType()) {
		case IResource.FILE:
			((IFile) resource).create(new ByteArrayInputStream(new byte[0]), true, monitor);
			break;

		case IResource.FOLDER:
			((IFolder) resource).create(IResource.NONE, true, monitor);
			break;

		case IResource.PROJECT:
			((IProject) resource).create(monitor);
			((IProject) resource).open(monitor);
			break;
		default:
			break;
		}
	}

	/**
	 * Creates a file with the given contents. If the file exists, its contents are replaced. Missing parent
	 * directories (including project) are created, if necessary.
	 * 
	 * @param file The file to create
	 * @param contents The contents of the file
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *   to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *   reported and that the operation cannot be canceled.
	 * @throws CoreException A problem occurs creating a resource
	 * @since 4.1
	 */
	public static void createFile(final IFile file, final byte[] contents, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, 2);
		ResourceUtils.create(file, progress.newChild(1));
		file.setContents(new ByteArrayInputStream(contents), IResource.FORCE, progress.newChild(1));
	}

	public static boolean deleteDir(final File dir) {
		if (dir.isDirectory()) {
			final String[] files = dir.list();
			for (int i = 0; i < files.length; i++) {
				final boolean success = ResourceUtils.deleteDir(new File(dir, files[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

	/**
	 * Uses a {@link WorkspaceJob} to execute a command as a new process on the system. Errors executing the command,
	 * and errors received via stderr will be logged rather than displayed to the user.
	 * 
	 * @param command The command (and possibly arguments) to execute
	 */
	public static void runSystemCommand(final String command) {
		final WorkspaceJob job = new WorkspaceJob("Running system command") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				if (monitor != null) {
					monitor.beginTask("Running " + command, IProgressMonitor.UNKNOWN);
				}

				try {
					String s = null;
					Process p = null;

					try {
						p = Runtime.getRuntime().exec(command);
					} catch (final IOException e) {
						RedhawkIdeActivator.logError("Error while running system command.", e);
						return Status.OK_STATUS;
					}

					final BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
					try {
						while ((s = stdError.readLine()) != null) {
							RedhawkIdeActivator.logError(s, null);
						}
					} catch (final IOException e) {
						RedhawkIdeActivator.logError("Error while reading input from system env.", e);
						return Status.OK_STATUS;
					}

					return Status.OK_STATUS;
				} finally {
					if (monitor != null) {
						monitor.done();
					}
				}
			}

		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * @since 3.0
	 */
	public static void createIdlLibraryResource(final IFile libraryFile, final IProgressMonitor m) throws CoreException {
		final IdlLibrary library = LibraryFactory.eINSTANCE.createIdlLibrary();
		RedhawkIdePreferenceInitializer.initializeIdlLibraryToDefaults(library);
		final LibraryResourceImpl tmpResource = new LibraryResourceImpl(null);
		tmpResource.getContents().add(library);
		final ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			tmpResource.save(stream, null);
		} catch (final IOException e) {
			// PASS With not happen
		}

		if (libraryFile.exists()) {
			libraryFile.setContents(new ByteArrayInputStream(stream.toByteArray()), IResource.FORCE, m);
		} else {
			libraryFile.create(new ByteArrayInputStream(stream.toByteArray()), true, m);
		}
	}

	/**
	 * @since 3.0
	 */
	public static void createIdlLibraryResource(final IProject project, final IProgressMonitor m) throws CoreException {
		ResourceUtils.createIdlLibraryResource(project.getFile(".library"), m);
	}
}
