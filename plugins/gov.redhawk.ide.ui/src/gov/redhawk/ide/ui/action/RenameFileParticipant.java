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
package gov.redhawk.ide.ui.action;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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

public class RenameFileParticipant extends RenameParticipant {

	/** String builder for creating paths */
	private StringBuilder builder;

	/** Keep track to see if editor has been saved */
	private boolean saveConfirmed = false;

	/** Keep a list of active editors so that we later reference it */
	private IEditorReference[] activeEditors;

	/** The project that is being refactored */
	private IProject currentProject;

	/** The main spd file in the project that is currently being refactored */
	private IFile currentSpdFile;

	/** The current project's editor input...used to find out if a particular editor matches up with the spd we have on file */
	private FileEditorInput input;

	/** An array of all extension types that are potential candidates for refactoring */
	private static final String[] FILE_NAME_PATTERN = {
	        "*" + ScdPackage.FILE_EXTENSION,
	        "*" + PrfPackage.FILE_EXTENSION,
	        "*" + SpdPackage.FILE_EXTENSION,
	        "build.sh",
	        "startJava.sh",
	        "*.spec",
	        "*.wavedev",
	        "*.py",
	        "*.cpp",
	        "*.h",
	        "*.java",
	        "*project",
	        "Makefile.am",
	        "Makefile.am.ide",
	        "configure.ac",
	        ".classpath"
	};

	/** List of files that will be changed */
	private final List<IFile> affectedFiles = new ArrayList<IFile>();

	/** List of folders that will be changed */
	private final List<IFolder> affectedFolders = new ArrayList<IFolder>();

	/** List of REDHAWK XML file extensions */
	private static final List<String> SUPPORTED_FILES = Arrays.asList(new String[] {
	        PrfPackage.FILE_EXTENSION, SpdPackage.FILE_EXTENSION, ScdPackage.FILE_EXTENSION
	});

	public RenameFileParticipant() {

	}

	@Override
	protected boolean initialize(final Object element) {
		/*
		 *  Verify object for refactoring is a project with the REDHAWK component nature.
		 *  
		 *  Grab it's spd file.
		 *  
		 *  Get all three main resource files and prompt to save, then close the associated Editor in the UI thread...otherwise
		 *  conflicts will arise when we start making changes to resources.
		 *  
		 *  Finally, refresh the local resource to make Eclipse full aware of our changes.
		 */
		if (element instanceof IProject) {
			final IProject project = (IProject) element;

			try {
				if (project.hasNature(ScaComponentProjectNature.ID) && !project.hasNature(ScaNodeProjectNature.ID)) {
					this.currentProject = project;
					final List<IResource> resources = new ArrayList<IResource>();

					for (final IResource resource : this.currentProject.members()) {
						for (final String s : SUPPORTED_FILES) {
							if (resource.getName().endsWith(s)) {
								if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
									this.currentSpdFile = (IFile) resource;
								}
								resources.add(resource);
							}
						}
					}

					if (!resources.isEmpty()) {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								RenameFileParticipant.this.saveConfirmed = IDE.saveAllEditors(resources.toArray(new IResource[0]), true);
								RenameFileParticipant.this.activeEditors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								        .getEditorReferences();
							}
						});

						if (this.saveConfirmed) {
							for (final IEditorReference editor : this.activeEditors) {
								final IEditorPart editorPart = editor.getEditor(false);

								if (editorPart instanceof SCAFormEditor) {
									final SCAFormEditor formEditor = (SCAFormEditor) editorPart;
									if (formEditor.getEditorInput() instanceof FileEditorInput) {
										this.input = (FileEditorInput) formEditor.getEditorInput();

										if (this.input.getFile().equals(this.currentSpdFile)) {
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
					}

					this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
					return true;
				}
			} catch (final CoreException e) {
				// PASS
			}
		}
		return false;
	}

	@Override
	public String getName() {
		return "Refactoring associated resources.";
	}

	@Override
	public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context) {
		return new RefactoringStatus();
	}

	/*
	 * Loosely based on DevDaily's MyRenameTypeParticipant example.
	 * 
	 * Start by searching the current project for all instances of the oldname, including folders and files.
	 * 
	 * Create changes for replacing instances of oldname with the newname in files, then folders as order is important.
	 * 
	 * Return the result.
	 */
	@Override
	public Change createPreChange(final IProgressMonitor pm) throws CoreException {
		final HashMap<IFile, TextFileChange> changes = new HashMap<IFile, TextFileChange>(); // SUPPRESS CHECKSTYLE HideField
		final String newName = getArguments().getNewName().split("[.]")[0];
		final String oldName = this.currentProject.getName();

		final IResource[] roots = { this.currentProject };

		final FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, RenameFileParticipant.FILE_NAME_PATTERN, true);

		this.builder = new StringBuilder();
		this.builder.append(newName);

		final Pattern pattern = Pattern.compile(this.currentProject.getName());

		final TextSearchRequestor collector = new TextSearchRequestor() {
			@Override
			public boolean acceptPatternMatch(final TextSearchMatchAccess matchAccess) throws CoreException {
				final IFile matchedFile = (IFile) matchAccess.getFile();

				if (!RenameFileParticipant.this.affectedFiles.contains(matchedFile)) {
					RenameFileParticipant.this.affectedFiles.add(matchedFile);
				}

				TextFileChange change = changes.get(matchedFile);

				if (change == null) {
					final TextChange textChange = getTextChange(matchedFile);
					if (textChange != null) {
						return false;
					}

					change = new TextFileChange(newName, matchedFile);
					change.setEdit(new MultiTextEdit());
					changes.put(matchedFile, change);
				}

				final ReplaceEdit edit = new ReplaceEdit(matchAccess.getMatchOffset(),
				        matchAccess.getMatchLength(),
				        RenameFileParticipant.this.builder.toString());
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

		if (hasAllResourceFiles(this.affectedFiles)) {
			String fileName = "";
			RenameResourceChange change;

			for (final IFile file : this.affectedFiles) {
				if (file.getFullPath().lastSegment().contains(oldName)) {
					fileName = file.getFullPath().lastSegment().replace(oldName, newName);

					change = new RenameResourceChange(file.getFullPath(), fileName);
					result.add(change);

					if (fileName.endsWith(".spec") || fileName.endsWith(".java")) {
						if (file.getParent() instanceof IFolder) {
							final IFolder parentFolder = (IFolder) file.getParent();

							if (!this.affectedFolders.contains(parentFolder)) {
								this.affectedFolders.add((IFolder) file.getParent());
							}
						}
					}
				}
			}

			for (final IFolder folder : this.affectedFolders) {
				fileName = folder.getFullPath().lastSegment().replace(oldName, newName);

				change = new RenameResourceChange(folder.getFullPath(), fileName);
				result.add(change);
			}
		}

		return result;
	}

	/*
	 * Method that finds the spd file in the list and uses the ModelUtil helper to get both the SCD and PRF as well.
	 * Once all of the files are found, adds it to the list.  This ensures that when the SPD files is refactored, so are
	 * the PRF and SCD files are changed as well...otherwise explosions may happen.
	 * 
	 * @param files a list of affected files that was gathered while searching for potential candidates for refactoring.
	 * @return boolean whether or not the list of files has references for SPD, SCD and PRF files. 
	 */
	private boolean hasAllResourceFiles(final List<IFile> files) {
		IFile spdFile = null;
		IFile scdFile = null;
		IFile prfFile = null;

		for (final IFile file : files) {
			if (file.getFileExtension() != null) {
				if ("xml".equals(file.getFileExtension())) {
					final String scaType = file.getFullPath().toString().split("[.]")[1];

					if ("spd".equals(scaType)) {
						spdFile = file;
						break;
					}
				}
			}
		}
		
		if (spdFile == null) {
			return false;
		}

		final URI spdURI = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), false);
		final SoftPkg softPkg = ModelUtil.loadSoftPkg(spdURI);

		scdFile = (IFile) ModelUtil.getScdFile(softPkg.getDescriptor());
		prfFile = (IFile) ModelUtil.getPrfFile(softPkg.getPropertyFile());

		if (scdFile.exists() && prfFile.exists()) {
			if (!files.contains(scdFile)) {
				files.add(scdFile);
			}

			if (!files.contains(prfFile)) {
				files.add(prfFile);
			}

			IDE.saveAllEditors(new IResource[] {
			        scdFile, prfFile, spdFile
			}, true);

			return true;
		}

		return false;
	}

	@Override
	public Change createChange(final IProgressMonitor pm) throws CoreException {
		// Refresh resource after changes have been made
		this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, pm);

		return null;
	}
}
