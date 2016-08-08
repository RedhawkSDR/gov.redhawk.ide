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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenamePackageChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;
import org.eclipse.ltk.core.refactoring.resource.RenameResourceChange;
import org.eclipse.search.ui.text.FileTextSearchScope;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

@SuppressWarnings("restriction")
public class RenameFileParticipant extends RenameParticipant {

	/** Keep track to see if editor has been saved */
	private boolean saveConfirmed = false;

	/** Keep a list of active editors so that we later reference it */
	private IEditorReference[] activeEditors;

	/** The project that is being refactored */
	private IProject currentProject;

	/** The main spd file in the project that is currently being refactored */
	private IFile currentSpdFile;

	/**
	 * The current project's editor input
	 * Used to find out if a particular editor matches up with the spd we have on file
	 */
	private FileEditorInput input;

	/** List of REDHAWK XML file extensions */
	private static final List<String> SUPPORTED_FILES = Arrays.asList(
		new String[] { PrfPackage.FILE_EXTENSION, SpdPackage.FILE_EXTENSION, ScdPackage.FILE_EXTENSION });

	@Override
	protected boolean initialize(final Object element) {
		/*  Verify object for refactoring is a project with the REDHAWK component nature.
		 *  Grab it's spd file.
		 *  Get all three main resource files and prompt to save, then close the associated Editor in the UI thread...otherwise
		 *  conflicts will arise when we start making changes to resources.
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
								RenameFileParticipant.this.activeEditors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
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

	/*
	 * Loosely based on DevDaily's MyRenameTypeParticipant example.
	 * Start by searching the current project for all instances of the oldname, including folders and files.
	 * Create changes for replacing instances of oldname with the newname in files, then folders as order is important.
	 * Return the result.
	 */
	@Override
	public Change createPreChange(final IProgressMonitor pm) throws CoreException {
		final RenameFileSearchRequestor searchRequestor = new RenameFileSearchRequestor(this);

		// Update any dot delimited changes
		final String oldName = this.currentProject.getName();
		final String newName = getArguments().getNewName();
		final String changeId = newName;
		Map<FileTextSearchScope, Pattern> namePatternMap = createNamePatternMap();
		for (Entry<FileTextSearchScope, Pattern> namePattern : namePatternMap.entrySet()) {
			searchRequestor.createChange(namePattern.getKey(), changeId, namePattern.getValue(), newName, oldName, pm);
		}

		// Update any path changes
		final String oldPath = oldName.replace(".", "/");
		final String newPath = newName.replace(".", "/");
		Map<FileTextSearchScope, Pattern> pathPatternMap = createPathPatternMap();
		for (Entry<FileTextSearchScope, Pattern> pathPattern : pathPatternMap.entrySet()) {
			searchRequestor.createChange(pathPattern.getKey(), changeId, pathPattern.getValue(), newPath, oldPath, pm);
		}

		// If no changes were recorded, exit the function
		if (searchRequestor.getChanges().isEmpty()) {
			return null;
		}

		// Collect a composite of all TextFileChanges created by the SearchRequestor
		final CompositeChange result = new CompositeChange("");
		for (final Iterator<TextFileChange> iter = searchRequestor.getChanges().values().iterator(); iter.hasNext();) {
			result.add(iter.next());
		}

		if (hasAllResourceFiles(searchRequestor.getAffectedFiles())) {
			// Update file name for *.spec
			for (final IFile file : searchRequestor.getAffectedFiles()) {
				if (file.getFullPath().lastSegment().endsWith(".spec")) {
					String fileName = file.getFullPath().lastSegment().replace(oldName, newName);
					RenameResourceChange change = new RenameResourceChange(file.getFullPath(), fileName);
					result.add(change);
				}
			}

			// Handle updating Java packages, if necessary
			if (this.currentProject.isNatureEnabled(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(this.currentProject);
				IPackageFragment[] packages = javaProject.getPackageFragments();
				for (IPackageFragment pkg : packages) {
					if (pkg.getKind() == IPackageFragmentRoot.K_SOURCE) {
						if (pkg.getElementName().equals(oldName + ".java")) {
							RenamePackageChange packageChange = new RenamePackageChange(pkg, newName + ".java", true);
							result.add(packageChange);
						}
					}
				}
			}
		}

		return result;
	}

	/**
	 * A map used to determine files where we need to update dot(.) delimited patterns (e.g. replace Foo.Bar with
	 * Foo.Baz.Bar).
	 */
	private Map<FileTextSearchScope, Pattern> createNamePatternMap() {
		Map<FileTextSearchScope, Pattern> namePatternMap = new HashMap<FileTextSearchScope, Pattern>();
		final IResource[] roots = { this.currentProject };
		final String oldProjectName = this.currentProject.getName();

		/* A Map where:
		 * - Key is the name of the files we want to look in
		 * - Value is the pattern we are using to find the text we want to replace
		 */
		Map<String[], String> filePatterns = new HashMap<String[], String>();
		filePatterns.put(new String[] { "build.sh" }, "(?:-e.*)" + oldProjectName + "|(?:tmpdir.*)");
		filePatterns.put(new String[] { "*.spec" }, "(?:Name:\\s*)" + oldProjectName);
		filePatterns.put(new String[] { "*" + SpdPackage.FILE_EXTENSION }, "(?:name.*)" + oldProjectName + "(?!\\.)");
		filePatterns.put(new String[] { "Makefile.am" }, "(?:ossieName.*)" + oldProjectName);
		filePatterns.put(new String[] { "*.wavedev" }, "(?:properties.*)" + oldProjectName);
		filePatterns.put(new String[] { "*.java" }, "(?:package.*)" + oldProjectName);
		filePatterns.put(new String[] { "configure.ac" }, "(?:AC_INIT.*)" + oldProjectName);
		filePatterns.put(new String[] { "startJava.sh" }, "(?<!\\.)" + oldProjectName + "(?!\\.jar)");

		for (Entry<String[], String> filePattern : filePatterns.entrySet()) {
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, filePattern.getKey(), true);
			Pattern pattern = Pattern.compile(filePattern.getValue());
			namePatternMap.put(scope, pattern);
		}

		return namePatternMap;
	}

	/**
	 * A map used to determine files where we need to update forward-slash(/) delimited patterns (e.g. replace Foo/Bar
	 * with Foo/Baz/Bar)
	 */
	private Map<FileTextSearchScope, Pattern> createPathPatternMap() {
		Map<FileTextSearchScope, Pattern> pathPatternMap = new HashMap<FileTextSearchScope, Pattern>();
		final IResource[] roots = { this.currentProject };
		final String oldProjectPath = this.currentProject.getName().replace(".", "/");

		/* A Map where:
		 * - Key is the name of the files we want to look in
		 * - Value is the pattern we are using to find the text we want to replace
		 */
		Map<String[], String> filePatterns = new HashMap<String[], String>();
		filePatterns.put(new String[] { "*.spec" }, "(?:/)" + oldProjectPath + "(?!\\.)");
		filePatterns.put(new String[] { "Makefile.am" }, "(?:\\$\\(prefix\\).*)" + oldProjectPath);

		for (Entry<String[], String> filePattern : filePatterns.entrySet()) {
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, filePattern.getKey(), true);
			Pattern pattern = Pattern.compile(filePattern.getValue());
			pathPatternMap.put(scope, pattern);
		}

		return pathPatternMap;
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

					// Assuming pattern of *.spd.xml, where the extension we care about is the second to last element
					String[] nameSegments = file.getFullPath().toString().split("[.]");
					final String scaType = nameSegments[nameSegments.length - 2];

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

			IDE.saveAllEditors(new IResource[] { scdFile, prfFile, spdFile }, true);

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

	@Override
	public String getName() {
		return "Refactoring associated resources.";
	}

	@Override
	public RefactoringStatus checkConditions(final IProgressMonitor pm, final CheckConditionsContext context) {
		// Get project names
		String oldProjName = this.currentProject.toString().substring(2); // Trim 'P/' from beginning of project name
		String newProjName = this.getArguments().getNewName();

		// Get the base names. Add +1 so we don't include the '.', and so we start from index=0 if no '.' is present
		String newBaseName = newProjName.substring(newProjName.lastIndexOf('.') + 1);
		String oldBaseName = oldProjName.substring(oldProjName.lastIndexOf('.') + 1);

		RefactoringStatus status = new RefactoringStatus();
		if (!oldBaseName.equals(newBaseName)) {
			status.addError("REDHAWK only supports changing project namespaces.  Changing the project basename is not supported. "
				+ "If you choose to continue please note all references to the original project name must be manually updated");
		}
		return status;
	}
}
