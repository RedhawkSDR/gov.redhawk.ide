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
package gov.redhawk.ide.sad.ui.action;

import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.sad.diagram.SadDiagramUtilHelper;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

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

/**
 * @since 4.0
 */
@SuppressWarnings("restriction")
public class RenameDiagramFileParticipant extends RenameParticipant {

	private Project project;
	private File sadFile;
	private IFile sadDiagramFile;
	private StringBuilder builder;
	private boolean saveConfirmed;
	private boolean refactoringProject = true;
	private IEditorReference[] activeEditors;

	public RenameDiagramFileParticipant() {
	}

	@Override
	protected boolean initialize(final Object element) {
		if (element instanceof Project) {
			this.project = (Project) element;

			try {
				if (this.project.hasNature(ScaWaveformProjectNature.ID)) {
					this.sadFile = (File) ModelUtil.getFile(this.project, SadPackage.FILE_EXTENSION);
				}
			} catch (final CoreException e) {
				// PASS
			}

		} else if (element instanceof File) {
			final File file = (File) element;

			if (file.getFullPath().lastSegment().endsWith(SadPackage.FILE_EXTENSION)) {
				this.sadFile = file;
				this.project = (Project) file.getParent();
				this.refactoringProject = false;
			}
		}

		if (this.sadFile != null && this.sadFile.exists()) {
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

				public void run() {
					RenameDiagramFileParticipant.this.saveConfirmed = IDE.saveAllEditors(new IResource[] {
						RenameDiagramFileParticipant.this.sadFile
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

							if (input.getFile().equals(this.sadFile)) {
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
	public Change createChange(final IProgressMonitor pm) throws CoreException {
		RenameDiagramFileParticipant.this.sadFile.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		return null;
	}

	@Override
	public Change createPreChange(final IProgressMonitor pm) throws CoreException {
		/**
		 *  Based upon DevDaily's MyRenameTypeParticipant example
		 */
		final HashMap<IFile, TextFileChange> changes = new HashMap<IFile, TextFileChange>();
		final String sadExtension = ".sad";

		this.builder = new StringBuilder();
		this.builder.append(getArguments().getNewName().split("[.]")[0]);
		this.builder.append(sadExtension);

		final String newName = this.builder.toString();

		final IResource[] roots = {
			this.sadFile.getParent()
		};
		final String[] fileNamePattern = {
			"*" + SadDiagramUtilHelper.SAD_DIAGRAM_FILE_EXTENSION
		};
		final FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePattern, false);

		this.builder = new StringBuilder();
		this.builder.append(this.sadFile.getName().split("[.]")[0]);
		this.builder.append(sadExtension);

		final Pattern pattern = Pattern.compile(this.builder.toString());

		final TextSearchRequestor collector = new TextSearchRequestor() {
			@Override
			public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
				RenameDiagramFileParticipant.this.sadDiagramFile = matchAccess.getFile();
				TextFileChange change = changes.get(RenameDiagramFileParticipant.this.sadDiagramFile);

				if (change == null) {
					final TextChange textChange = getTextChange(RenameDiagramFileParticipant.this.sadDiagramFile);
					if (textChange != null) {
						return false;
					}

					change = new TextFileChange(RenameDiagramFileParticipant.this.sadDiagramFile.getName(), RenameDiagramFileParticipant.this.sadDiagramFile);
					change.setEdit(new MultiTextEdit());
					changes.put(RenameDiagramFileParticipant.this.sadDiagramFile, change);
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

		final URI fileURI = URI.createFileURI(this.sadFile.getLocation().toString());
		final SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(fileURI);

		if (sad.getName().equals(RenameDiagramFileParticipant.this.sadFile.getName().split("[.]")[0])) {
			sad.setName(getArguments().getNewName().split("[.]")[0]);
			try {
				sad.eResource().save(null);
			} catch (final IOException e) {
				SadUiActivator.logError("Unable to update sad file with new name:", e);
			}
		}

		final String fileName = getArguments().getNewName().split("[.]")[0];

		if (this.refactoringProject) {
			final RenameResourceChange change = new RenameResourceChange(this.sadFile.getFullPath(), this.sadFile.getFullPath()
			        .lastSegment()
			        .replace(this.project.getName(), fileName));
			result.add(change);
		}

		final RenameResourceChange change = new RenameResourceChange(this.sadDiagramFile.getFullPath(), this.sadDiagramFile.getFullPath()
		        .lastSegment()
		        .replace(this.project.getName(), fileName));
		result.add(change);

		return result;
	}
}
