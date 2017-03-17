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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
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
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * @since 11.0
 */
@SuppressWarnings("restriction")
public class RenameFileParticipant extends RenameParticipant {

	/** The project that is being refactored */
	private IProject currentProject;

	/** Keep a list of active editors so that we later reference it */
	private IEditorReference[] activeEditors;

	/**
	 * The current project's editor input
	 * Used to find out if a particular editor matches up with the spd we have on file
	 */
	private FileEditorInput input;

	@Override
	protected boolean initialize(final Object element) {
		/*  Verify object for refactoring is a project with the REDHAWK component nature.
		 *  Prompt to save, then close associated editors in the UI thread, otherwise conflicts 
		 *  will arise when we start making changes to resources.
		 *  Finally, refresh the local resource to make Eclipse full aware of our changes.
		 */
		if (!(element instanceof IProject)) {
			return false;
		}

		this.currentProject = (IProject) element;

		try {

			if (!hasCorrectNature(currentProject)) {
				return false;
			}

			// Prompt to save any dirty editors associated with this project
			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					IDE.saveAllEditors(new IResource[] { RenameFileParticipant.this.currentProject }, true);
					RenameFileParticipant.this.activeEditors = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				}
			});

			// If the user decided not to save, we won't try and update anything other than the Project name
			for (IEditorReference editor : activeEditors) {
				if (editor.isDirty()) {
					return false;
				}
			}

			// Close associated editors in the UI thread, otherwise conflicts will arise when we start making changes to
			// resources.
			for (final IEditorReference editor : this.activeEditors) {
				final IEditorPart editorPart = editor.getEditor(false);

				if (editor.getEditorInput() instanceof FileEditorInput) {
					this.input = (FileEditorInput) editor.getEditorInput();
					if (this.currentProject.equals(this.input.getFile().getProject())) {
						PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

							@Override
							public void run() {
								editor.getPage().closeEditor(editorPart, true);
							}
						});
					}
				}
			}

			// Refresh the local resource to make Eclipse full aware of our changes.
			this.currentProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			return true;
		} catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Failed to perform project refactor", e),
				StatusManager.SHOW | StatusManager.LOG);
			return false;
		}
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

		// Update DCE ID to keep project unique
		addDceIdChange(searchRequestor, pm);

		// If no changes were recorded, exit the function
		if (searchRequestor.getChanges().isEmpty()) {
			return null;
		}

		// Collect a composite of all TextFileChanges created by the SearchRequestor
		final CompositeChange result = new CompositeChange("");
		for (final Iterator<TextFileChange> iter = searchRequestor.getChanges().values().iterator(); iter.hasNext();) {
			result.add(iter.next());
		}

		// Update file name for *.spec
		for (final IFile file : searchRequestor.getAffectedFiles()) {
			if (file.getFullPath().lastSegment().endsWith(".spec") || file.getFullPath().lastSegment().endsWith(".pc.in")) {
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

		return result;
	}

	/**
	 * A map used to determine files where we need to update dot(.) delimited patterns (e.g. replace Foo.Bar with
	 * Foo.Baz.Bar).
	 */
	protected Map<FileTextSearchScope, Pattern> createNamePatternMap() {
		Map<FileTextSearchScope, Pattern> namePatternMap = new HashMap<FileTextSearchScope, Pattern>();
		final IResource[] roots = { this.currentProject };
		final String oldProjectName = this.currentProject.getName();

		/* A Map where:
		 * - Key is the name of the files we want to look in
		 * - Value is the pattern we are using to find the text we want to replace
		 */
		Map<String[], String> filePatterns = new HashMap<String[], String>();
		filePatterns.put(new String[] { "*" + SpdPackage.FILE_EXTENSION }, oldProjectName + "(?:.*type=)");
		filePatterns.put(new String[] { "*.spec", "*.pc.in" }, "(?:Name:\\s*)" + oldProjectName);
		filePatterns.put(new String[] { "*.java" }, "(?:package.*)" + oldProjectName);
		filePatterns.put(new String[] { "*.wavedev" }, "(?:properties.*)" + oldProjectName);
		filePatterns.put(new String[] { "build.sh" }, "(?:-e.*)" + oldProjectName + "|(?:tmpdir.*)");
		filePatterns.put(new String[] { "configure.ac" }, "(?:AC_INIT.*)" + oldProjectName);
		filePatterns.put(new String[] { "configure.ac" }, "(?:RH_SOFTPKG_PREFIX.*)" + oldProjectName);
		filePatterns.put(new String[] { "configure.ac" }, "(?:AC_CONFIG_FILES.*)" + oldProjectName);
		filePatterns.put(new String[] { "Makefile.am" }, "(?:ossieName.*)" + oldProjectName);
		filePatterns.put(new String[] { "Makefile.am" }, "(?:pkgconfig_DATA.*)" + oldProjectName);
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

		filePatterns.put(new String[] { "Makefile.am" }, "(?:\\$\\(prefix\\).*)" + oldProjectPath);

		// Update all paths in the .spec file EXCEPT those describing %dir
		filePatterns.put(new String[] { "*.spec" }, "(?<!\\%dir )(?:\\%\\{_prefix\\}.*)" + oldProjectPath + "(?!\\.)");
		filePatterns.put(new String[] { "*.spec" }, "(?<!\\%dir )(?:\\%\\{_sdrroot\\}.*)" + oldProjectPath + "(?!\\.)");

		// Update the %dir declarations in .spec files. These are special cases that need to be handled
		filePatterns.put(new String[] { "*.spec" }, "(?s:\\%dir \\%\\{_prefix\\}/(dom|dev)/(components|deps|devices|nodes|services|waveforms).*?)" + oldProjectPath);
		filePatterns.put(new String[] { "*.spec" }, "(?s:\\%dir \\%\\{_sdrroot\\}/(dom|dev)/(components|deps|devices|nodes|services|waveforms).*?)" + oldProjectPath);

		for (Entry<String[], String> filePattern : filePatterns.entrySet()) {
			FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, filePattern.getKey(), true);
			Pattern pattern = Pattern.compile(filePattern.getValue());
			pathPatternMap.put(scope, pattern);
		}

		return pathPatternMap;
	}

	/**
	 * Create an additional change request to update the value of the projects DCE UUID
	 */
	private void addDceIdChange(RenameFileSearchRequestor searchRequestor, IProgressMonitor pm) throws CoreException {
		// Get project file
		IFile file = getFile(this.getCurrentProject().getName().split("\\."));

		// Get DEC ID
		ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		URI uri = URI.createURI(file.getLocationURI().toString());
		Resource resource = set.getResource(uri, true);
		String dceId = getDceId(resource);

		// Add the change to the searchRequestor
		final String changeId = getArguments().getNewName();
		final IResource[] roots = { this.currentProject };
		final String[] fileNamePattern = { "*" + getFileExtension() };
		FileTextSearchScope scope = FileTextSearchScope.newSearchScope(roots, fileNamePattern, true);
		Pattern pattern = Pattern.compile(dceId);

		searchRequestor.createChange(scope, changeId, pattern, DceUuidUtil.createDceUUID(), dceId, pm);
	}

	/**
	 * @since 10.0
	 */
	protected IFile getFile(String[] segments) throws CoreException {
		String fileName = segments[segments.length - 1] + getFileExtension();
		return this.currentProject.getFile(fileName);
	}

	/**
	 * @since 10.0
	 */
	protected String getFileExtension() throws CoreException {
		return SpdPackage.FILE_EXTENSION;
	}

	/**
	 * @since 10.0
	 */
	protected String getDceId(Resource resource) throws CoreException {
		return SoftPkg.Util.getSoftPkg(resource).getId();
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

	/**
	 * @param currentProject2
	 * @return True if the project has a nature that is acceptable for this RenameParticipant
	 * @throws CoreException
	 */
	protected boolean hasCorrectNature(IProject project) throws CoreException {
		return (project.hasNature(ScaComponentProjectNature.ID) && !project.hasNature(ScaNodeProjectNature.ID));
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

	protected IProject getCurrentProject() {
		return currentProject;
	}
}
