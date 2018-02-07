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
package gov.redhawk.ide.codegen.ui.internal.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.GenerateFilesDialog;
import gov.redhawk.ide.codegen.ui.preferences.CodegenPreferenceConstants;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * Prompts the user to select which files will be generated, if applicable. 
 */
public class UserFileSelectionJob extends UIJob {

	private Shell shell;
	private Map<Implementation, Set<FileStatus>> implMap;
	private Map<Implementation, String[]> userImplMap;

	/**
	 * Must call {@link #setImplementationsAndFiles(Map)} before scheduling the job
	 * @param shell
	 */
	public UserFileSelectionJob(Shell shell) {
		super(shell.getDisplay(), "Check files");
		setUser(true);
		this.shell = shell;
	}

	/**
	 * Provides the set of file information the code generator provided for each implementation
	 * @param implMap
	 */
	public void setImplementationsAndFiles(Map<Implementation, Set<FileStatus>> implMap) {
		this.implMap = implMap;
	}

	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		SubMonitor progress = SubMonitor.convert(monitor);
		if (implMap == null) {
			return new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "No implementations or files specified to generate");
		}

		// Determine based on the user's preferences, and possibly the provide information about the files, whether we
		// need to prompt the user about which files the code generator should act on
		Set<FileStatus> aggregate = new HashSet<FileStatus>();
		for (Set<FileStatus> v : implMap.values()) {
			aggregate.addAll(v);
		}
		final IPreferenceStore store = RedhawkCodegenUiActivator.getDefault().getPreferenceStore();
		List<String> filesToGenerate = new ArrayList<String>();
		boolean showDialog = false;
		boolean generateDefault = store.getBoolean(CodegenPreferenceConstants.P_ALWAYS_GENERATE_DEFAULTS);
		if (generateDefault) {
			for (FileStatus s : aggregate) {
				if (!s.isDoIt() && s.getType() != FileStatus.Type.USER) {
					showDialog = true;
					break;
				}
			}
		} else {
			showDialog = true;
		}
		progress.worked(1);

		// Collect a list of filenames to act on for each implementation. This may be automatic, or based on the user's
		// dialog choices.
		if (showDialog) {
			GenerateFilesDialog dialog = new GenerateFilesDialog(shell, aggregate);
			dialog.setBlockOnOpen(true);
			if (dialog.open() == Window.OK) {
				String[] result = dialog.getFilesToGenerate();
				if (result != null) {
					filesToGenerate.addAll(Arrays.asList(result));
				}
			} else {
				return Status.CANCEL_STATUS;
			}
		} else {
			for (FileStatus s : aggregate) {
				if (s.isDoIt()) {
					filesToGenerate.add(s.getFilename());
				}
			}
		}
		progress.worked(1);

		userImplMap = new HashMap<Implementation, String[]>();
		for (Map.Entry<Implementation, Set<FileStatus>> entry : implMap.entrySet()) {
			Set<String> subsetFilesToGenerate = new HashSet<String>();
			for (FileStatus s : entry.getValue()) {
				subsetFilesToGenerate.add(s.getFilename());
			}
			Set<String> filesToRemove = new HashSet<String>(subsetFilesToGenerate);
			filesToRemove.removeAll(filesToGenerate);
			subsetFilesToGenerate.removeAll(filesToRemove);

			userImplMap.put(entry.getKey(), subsetFilesToGenerate.toArray(new String[subsetFilesToGenerate.size()]));
		}

		return Status.OK_STATUS;
	}

	/**
	 * @return The results of the job (which files the user has selected for code generation)
	 */
	public Map<Implementation, String[]> getFilesForImplementation() {
		return userImplMap;
	}
}
