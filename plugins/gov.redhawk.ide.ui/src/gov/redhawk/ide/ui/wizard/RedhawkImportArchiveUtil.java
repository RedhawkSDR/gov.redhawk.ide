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

package gov.redhawk.ide.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.wizards.datatransfer.ILeveledImportStructureProvider;
import org.eclipse.ui.internal.wizards.datatransfer.TarEntry;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.dcd.generator.newnode.NodeProjectCreator;
import gov.redhawk.ide.sad.generator.newwaveform.WaveformProjectCreator;
import gov.redhawk.ide.spd.generator.newcomponent.ComponentProjectCreator;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * @since 9.1
 */
@SuppressWarnings("restriction")
public class RedhawkImportArchiveUtil extends RedhawkImportUtil {

	private ProjectRecord record;
	private IProgressMonitor monitor;
	private ILeveledImportStructureProvider structureProvider;
	private RedhawkImportWizardPage1 parent;
	private String projectName;
	private int result;
	private IProject project; // Holds the generated .project file

	private boolean dotProjectMissing = true;
	private boolean wavedevMissing = true;

	public void createMissingFiles(ProjectRecord record, IProgressMonitor monitor, RedhawkImportWizardPage1 parent,
		ILeveledImportStructureProvider structureProvider) {
		this.record = record;
		this.monitor = monitor;
		this.projectName = record.projectName;
		this.parent = parent;
		this.structureProvider = structureProvider;
		String type = structureProvider.getLabel(record.projectArchiveFile);

		if (findMissingFiles() == SWT.CANCEL) {
			// User canceled import because of missing source directories
			// Don't create files
			return;
		}

		try {
			WaveDevSettings waveDev = null;
			if (type.matches(getSadExtension()) && dotProjectMissing) {
				project = createDotProjectFile("SAD");
			}
			if (type.matches(getDcdExtension()) && dotProjectMissing) {
				project = createDotProjectFile("DCD");
			}
			if (type.matches(getSpdExtension())) {
				if (dotProjectMissing) {
					project = createDotProjectFile("SPD");
				}
				if (wavedevMissing) {
					waveDev = createWaveDevFile();
				}
			
				if (dotProjectMissing || wavedevMissing) {
					generateFiles(monitor, null, project, waveDev);
				}
			}
			
		} catch (CoreException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
	}

	protected int findMissingFiles() {
		boolean hasSource = true;

		// Find the parent directory to search for missing files in
		Object root = structureProvider.getRoot();
		Object parentDirectory = findParentInArchive(root);

		// Search for missing files, assume standard folder structure
		List< ? > children = structureProvider.getChildren(parentDirectory);
		Iterator< ? > childrenEnum = children.iterator();
		while (childrenEnum.hasNext()) {
			Object child = childrenEnum.next();
			String name = structureProvider.getLabel(child);

			// TODO: Evaluate if source is present and set hasSource

			// Check for .project and .wavedev files
			if (name.matches(".+\\.project")) {
				dotProjectMissing = false;
				continue;
			}
			if (name.matches(".+\\.wavedev")) {
				wavedevMissing = false;
				continue;
			}
		}

		if (!hasSource) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					MessageBox dialog = new MessageBox(parent.getShell(), SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
					dialog.setText("Error - No Source Directory");
					dialog.setMessage("Warning: \nNo source files found in the specified location.  " + "\nImported projects will not function correctly");
					result = dialog.open();
				}
			});
		}

		result = SWT.OK;
		return result;
	}

	private Object findParentInArchive(Object root) {
		String path = null;
		Object parentDirectory = new Object();
		if (record.projectArchiveFile instanceof TarEntry) {
			path = ((TarEntry) record.projectArchiveFile).getName();
		} else if (record.projectArchiveFile instanceof ZipEntry) {
			path = ((ZipEntry) record.projectArchiveFile).getName();
		}
		String parentDirString = path.substring(0, path.lastIndexOf("/") + 1);

		List< ? > children = structureProvider.getChildren(root);
		Iterator< ? > childrenEnum = children.iterator();
		while (childrenEnum.hasNext()) {
			Object child = childrenEnum.next();
			if (structureProvider.isFolder(child)) {
				// If this object is the parent directory, assign it and break
				if (structureProvider.getFullPath(child).equals(parentDirString)) {
					parentDirectory = child;
					break;
				}

				// Otherwise, recurse down
				findParentInArchive(child);
			}
		}
		return parentDirectory;
	}

	protected IProject createDotProjectFile(String projectType) {
		try {
			IProject dotProject = null;

			// Build new .project files of the appropriate type
			if ("SAD".equals(projectType)) {
				dotProject = WaveformProjectCreator.createEmptyProject(projectName, null, monitor);
			}
			if ("DCD".equals(projectType)) {
				dotProject = NodeProjectCreator.createEmptyProject(projectName, null, monitor);
			}
			if ("SPD".equals(projectType)) {
				dotProject = ComponentProjectCreator.createEmptyProject(projectName, null, monitor);
			}

			// Import files into workspace
			List< ? > fileSystemObjects = structureProvider.getChildren(record.parent);
			structureProvider.setStrip(record.level);
			ImportOperation operation = new ImportOperation(dotProject.getFullPath(), structureProvider.getRoot(), structureProvider, parent, fileSystemObjects);
			operation.setContext(parent.getShell());
			operation.run(monitor);

			return dotProject;
		} catch (final CoreException e) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					IDEWorkbenchPlugin.log(e.getMessage());
					MessageBox errorDialog = new MessageBox(parent.getShell(), SWT.ERROR);
					errorDialog.setText("Import Failed");
					errorDialog.setMessage("Import Failed for the following reason: \n" + e.getMessage());
					errorDialog.open();
				}
			});
		} catch (InvocationTargetException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		} catch (InterruptedException e) {
			IDEWorkbenchPlugin.log(e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected WaveDevSettings createWaveDevFile() throws CoreException {
		// Locate the SCA file that was just imported into workspace via the createDotProjectFile() method
		String relativePath = structureProvider.getFullPath(record.projectArchiveFile);
		IFile file = project.getFile(relativePath);

		// Build SoftPkg object
		SoftPkg softPkg = null;
		softPkg = getSoftPkg(file.getLocation().toString());
		
		return createWaveDevFile(record, projectName, softPkg);
	}

	@Override
	public SoftPkg getSoftPkg(String path) {
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		URI resourceURI = URI.createFileURI(path);
		Resource resource = set.getResource(resourceURI, true);
		return SoftPkg.Util.getSoftPkg(resource);
	}
}
