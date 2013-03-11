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
package gov.redhawk.ide.dcd.ui.action;

import gov.redhawk.ide.dcd.ui.DcdUiActivator;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.dcd.diagram.DcdDiagramUtilHelper;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextChange;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.text.edits.MultiTextEdit;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEditGroup;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

@SuppressWarnings("restriction")
public class RenameDiagramFileParticipant extends RenameParticipant {

	private File dcdFile;
	private StringBuilder builder;
	private Project project;
	private boolean refactoringProject = true;
	private boolean saveConfirmed;
	private IEditorReference[] activeEditors;
	private final List<File> affectedFiles = new ArrayList<File>();
	private final String[] fileNamePattern = {
	        "*.spec", "*" + DcdDiagramUtilHelper.DIAGRAM_FILE_EXTENSION
	};

	public RenameDiagramFileParticipant() {
	}

	@Override
	protected boolean initialize(final Object element) {
		if (element instanceof Project) {
			this.project = (Project) element;

			try {
				if (this.project.hasNature(ScaNodeProjectNature.ID)) {
					this.dcdFile = (File) ModelUtil.getFile(this.project, DcdPackage.FILE_EXTENSION);
				}
			} catch (final CoreException e) {
				// PASS
			}

		} else if (element instanceof File) {
			final File file = (File) element;

			if (file.getFullPath().lastSegment().endsWith(DcdPackage.FILE_EXTENSION)) {
				this.dcdFile = file;
				this.project = (Project) file.getParent();
				this.refactoringProject = false;
			}
		}

		if (this.dcdFile != null && this.dcdFile.exists()) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				public void run() {
					RenameDiagramFileParticipant.this.saveConfirmed = IDE.saveAllEditors(new IResource[] {
						RenameDiagramFileParticipant.this.dcdFile
					}, true);
					RenameDiagramFileParticipant.this.activeEditors = PlatformUI.getWorkbench()
					        .getActiveWorkbenchWindow()
					        .getActivePage()
					        .getEditorReferences();
				}
			});

			if (this.saveConfirmed) {
				for (final IEditorReference editor : this.activeEditors) {
					final IEditorPart editorPart = editor.getEditor(false);

					if (editorPart instanceof SCAFormEditor) {
						final SCAFormEditor formEditor = (SCAFormEditor) editorPart;
						if (formEditor.getEditorInput() instanceof FileEditorInput) {
							final FileEditorInput input = (FileEditorInput) formEditor.getEditorInput();

							if (input.getFile().equals(this.dcdFile)) {
								PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

									public void run() {
										formEditor.close(true);
									}
								});
							}
						}
					}
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public String getName() {
		return "Refactoring associated Diagram file.";
	}

	@Override
	public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	@Override
	public Change createPreChange(final IProgressMonitor pm) throws CoreException {
		/**
		 *  Based upon DevDaily's MyRenameTypeParticipant example
		 */
		final HashMap<IFile, TextFileChange> changes = new HashMap<IFile, TextFileChange>();
		final String dcdExtension = ".dcd";

		this.builder = new StringBuilder();
		this.builder.append(getArguments().getNewName().split("[.]")[0]);
		this.builder.append(dcdExtension);

		final String newName = this.builder.toString();

		final IResource[] roots = { this.dcdFile.getParent() };

		final FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, this.fileNamePattern, false);

		this.builder = new StringBuilder();
		this.builder.append(this.dcdFile.getName().split("[.]")[0]);
		this.builder.append(dcdExtension);

		final Pattern pattern = Pattern.compile(this.builder.toString());

		final TextSearchRequestor collector = new TextSearchRequestor() {
			@Override
			public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
				final File matchedFile = (File) matchAccess.getFile();

				if (!RenameDiagramFileParticipant.this.affectedFiles.contains(matchedFile)) {
					RenameDiagramFileParticipant.this.affectedFiles.add(matchedFile);
				}

				TextFileChange change = changes.get(matchedFile);

				if (change == null) {
					final TextChange textChange = getTextChange(matchedFile);
					if (textChange != null) {
						return false;
					}

					change = new TextFileChange(matchedFile.getName(), matchedFile);
					change.setEdit(new MultiTextEdit());
					changes.put(matchedFile, change);
				}
				final ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(), matchAccess.getMatchLength(), newName);
				change.addEdit(edit);
				change.addTextEditGroup(new TextEditGroup("Update type reference", edit));
				return true;
			}
		};
		TextSearchEngine.create().search(scope, collector, pattern, pm);

		if (changes.isEmpty()) {
			return null;
		}

		final CompositeChange result = new CompositeChange("");
		for (final Iterator<TextFileChange> iter = changes.values().iterator(); iter.hasNext();) {
			result.add(iter.next());
		}

		RenameDiagramFileParticipant.this.dcdFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		final URI fileURI = URI.createFileURI(this.dcdFile.getLocation().toString());
		final DeviceConfiguration dcd = ModelUtil.loadDeviceConfiguration(fileURI);

		dcd.setName(getArguments().getNewName().split("[.]")[0]);

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				try {
					dcd.eResource().save(null);
				} catch (final IOException e) {
					DcdUiActivator.logError("Unable to update dcd file with new name:", e);
				}
			}
		});

		final String fileName = getArguments().getNewName().split("[.]")[0];
		RenameResourceChange change = null;

		for (final File file : this.affectedFiles) {

			if (file.getFileExtension().contains(DcdDiagramUtilHelper.DIAGRAM_FILE_EXTENSION.split("[.]")[0])) {
				if (!file.getFullPath().lastSegment().contains("DeviceManager")) {
					change = new RenameResourceChange(file.getFullPath(), file.getFullPath().lastSegment().replace(this.project.getName(), fileName));
				} else {
					change = new RenameResourceChange(file.getFullPath(), file.getFullPath().lastSegment().replace("DeviceManager", fileName));
				}
			} else {
				change = new RenameResourceChange(file.getFullPath(), file.getFullPath().lastSegment().replace(this.project.getName(), fileName));
			}
			result.add(change);
		}

		if (this.refactoringProject) {
			if (this.dcdFile.getFullPath().lastSegment().contains("DeviceManager")) {
				change = new RenameResourceChange(this.dcdFile.getFullPath(), this.dcdFile.getFullPath().lastSegment().replace("DeviceManager", fileName));
			} else {
				change = new RenameResourceChange(this.dcdFile.getFullPath(), this.dcdFile.getFullPath().lastSegment()
				        .replace(this.project.getName(), fileName));
			}
			result.add(change);
		}

		return result;
	}

	@Override
	public Change createChange(final IProgressMonitor pm) throws CoreException {
		RenameDiagramFileParticipant.this.dcdFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		return null;
	}

}
