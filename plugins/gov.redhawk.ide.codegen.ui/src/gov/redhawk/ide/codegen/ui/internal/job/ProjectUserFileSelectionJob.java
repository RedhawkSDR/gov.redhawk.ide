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
package gov.redhawk.ide.codegen.ui.internal.job;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.ui.internal.GenerateFilesDialog;

/**
 * Similar to {@link UserFileSelectionJob}, but not specific to implementations in an SPD, just an {@link IProject}.
 */
public class ProjectUserFileSelectionJob extends UIJob {

	private Shell shell;
	private Set<FileStatus> fileStatuses;
	private List<String> filesToGenerate = null;

	public ProjectUserFileSelectionJob(Shell shell, Set<FileStatus> fileStatuses) {
		super(shell.getDisplay(), "Select files to generate");
		this.shell = shell;
		this.fileStatuses = fileStatuses;

		setUser(true);
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		GenerateFilesDialog dialog = new GenerateFilesDialog(shell, fileStatuses);
		dialog.setShowUserFiles(true);
		dialog.setBlockOnOpen(true);
		if (dialog.open() == Window.OK) {
			filesToGenerate = Collections.unmodifiableList(Arrays.asList(dialog.getFilesToGenerate()));
			return Status.OK_STATUS;
		} else {
			filesToGenerate = Collections.emptyList();
			return Status.CANCEL_STATUS;
		}
	}

	public List<String> getFilesToGenerate() {
		return filesToGenerate;
	}
}
