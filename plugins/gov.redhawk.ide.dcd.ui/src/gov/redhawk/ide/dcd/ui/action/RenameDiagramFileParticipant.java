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

import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.dcd.diagram.DcdDiagramUtilHelper;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdPackage;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
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

				@Override
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

									@Override
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
		return "Closing associated Diagram file.";
	}

	@Override
	public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	@Override
	public Change createPreChange(final IProgressMonitor pm) throws CoreException {
		return null;
	}

	@Override
	public Change createChange(final IProgressMonitor pm) throws CoreException {
		return null;
	}

}
