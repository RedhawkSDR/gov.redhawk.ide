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
package gov.redhawk.ide.sdr.ui.export;

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

/**
 * This class is used to perform "export" operations - essentially mkdir's and copy's on a target EFS file system.
 * <p />
 * FUTURE WORK: Although the class currently takes an {@link IPath} to find its target root, this could be changed to
 * a URL, allowing the class to generically target any EFS that Eclipse knows about. The class uses {@link IResource}s
 * as sources for copy operations.
 * @since 3.1
 */
public class FileStoreExporter implements IScaExporter {

	private final IPath rootPath;
	private final URI rootURI;

	/**
	 * File/directory exclude patterns (see {@link #FileStoreExporter(IPath, List)}).
	 */
	private final List<Pattern> excludePatterns;

	/**
	 * Creates a default exporter that ignores files beginning with a dot (.)
	 * 
	 * @param rootPath The root path of the destination export location
	 */
	public FileStoreExporter(final IPath rootPath) {
		this(rootPath, new ArrayList<Pattern>());
		this.excludePatterns.add(Pattern.compile("^\\.")); // Ignore files beginning with a dot
	}

	/**
	 * Creates an exporter that ignores the specified file patterns.
	 * 
	 * @param rootPath The root path of the destination export location
	 * @param excludePatterns A {@link List} of regular expressions. Files matching any regular expression will
	 * <b>not</b> be exported when calling {@link #write(IResource, IPath, IProgressMonitor)}. Regular expressions
	 * are matched greedily, i.e. any subset of the file name can match (so use ^ or $ if you need them).
	 */
	public FileStoreExporter(final IPath rootPath, final List<Pattern> excludePatterns) {
		super();
		this.rootPath = rootPath;
		this.rootURI = rootPath.toFile().toURI();
		this.excludePatterns = new ArrayList<Pattern>(excludePatterns);
	}

	public void finished() throws IOException {
		// PASS - nothing needed
	}

	private static final int FETCH_WORK = 5;
	private static final int COPY_WORK = 95;

	public void write(final IResource resource, final IPath destinationPath, final IProgressMonitor monitor) throws IOException, CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, FileStoreExporter.FETCH_WORK + FileStoreExporter.FETCH_WORK + FileStoreExporter.COPY_WORK);

		// Calculate destination URI
		final URI destURI = this.rootURI.resolve(destinationPath.toString());

		// Create the root directory we'll be copying into
		if (destinationPath.removeLastSegments(1).segmentCount() > 0) {
			final IPath dirPath = destinationPath.removeLastSegments(1);
			mkdir(dirPath, progress.newChild(FileStoreExporter.FETCH_WORK));
		}
		progress.setWorkRemaining(FileStoreExporter.FETCH_WORK + FileStoreExporter.COPY_WORK);

		// Get the IFileStores and perform a recursive copy
		final IFileStore srcFileStore = EFS.getStore(resource.getLocationURI());
		final IFileInfo srcFileInfo = srcFileStore.fetchInfo(EFS.NONE, progress.newChild(FileStoreExporter.FETCH_WORK));
		final IFileStore destFileStore = EFS.getStore(destURI);
		recursiveCopy(srcFileStore, srcFileInfo, destFileStore, progress.newChild(FileStoreExporter.COPY_WORK));
	}

	/**
	 * Recursively copies files/directories from one file store to another, excluding anything matching the
	 * {@link #excludePatterns exclude patterns}.
	 * 
	 * @param srcFileStore The source file store
	 * @param srcFileInfo The source file info (from {@link IFileStore#fetchInfo(int, IProgressMonitor)})
	 * @param destFileStore The destination file store
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @throws CoreException Copying fails (see {@link IFileStore#copy(IFileStore, int, IProgressMonitor)} for reasons)
	 */
	private void recursiveCopy(final IFileStore srcFileStore, final IFileInfo srcFileInfo, final IFileStore destFileStore, final IProgressMonitor monitor)
	        throws CoreException {
		if (monitor != null && monitor.isCanceled()) {
			return;
		}

		// We do nothing if the filename matches an exclude pattern
		final String sourceName = srcFileInfo.getName();
		for (final Pattern excludePattern : this.excludePatterns) {
			if (excludePattern.matcher(sourceName).find()) {
				return;
			}
		}

		// Copy logic (directory / individual file)
		if (srcFileInfo.isDirectory()) {
			final SubMonitor progress = SubMonitor.convert(monitor, FileStoreExporter.FETCH_WORK + FileStoreExporter.COPY_WORK);

			// Fetch children
			final IFileStore[] srcChildFileStores = srcFileStore.childStores(0, progress.newChild(FileStoreExporter.FETCH_WORK));

			// Copy the directory itself - disallow recursion; we'll do that ourselves
			final SubMonitor copyProgress = progress.newChild(FileStoreExporter.COPY_WORK).setWorkRemaining(
			        FileStoreExporter.COPY_WORK + srcChildFileStores.length * (FileStoreExporter.FETCH_WORK + FileStoreExporter.COPY_WORK));
			srcFileStore.copy(destFileStore, EFS.OVERWRITE | EFS.SHALLOW, copyProgress.newChild(FileStoreExporter.COPY_WORK));
			for (final IFileStore srcChildFileStore : srcChildFileStores) {
				final IFileInfo srcChildFileInfo = srcChildFileStore.fetchInfo(EFS.NONE, copyProgress.newChild(FileStoreExporter.FETCH_WORK));
				recursiveCopy(srcChildFileStore, srcChildFileInfo, destFileStore.getChild(srcChildFileInfo.getName()),
				        copyProgress.newChild(FileStoreExporter.COPY_WORK));
			}
		} else {
			final SubMonitor progress = SubMonitor.convert(monitor, FileStoreExporter.COPY_WORK);
			srcFileStore.copy(destFileStore, EFS.OVERWRITE, progress.newChild(FileStoreExporter.COPY_WORK));
		}
	}

	public void mkdir(final IPath destinationPath, final IProgressMonitor monitor) throws IOException, CoreException {
		final URI destURI = this.rootURI.resolve(destinationPath.toString());
		EFS.getStore(destURI).mkdir(EFS.NONE, monitor);
	}

	public IPath getExportLocation() {
		return this.rootPath;
	}

	@Override
	public String toString() {
		return getClass().getName() + " [" + this.rootURI.toString() + "]";
	}

	/**
	 * All for friends to add custom exclude patterns
	 * @param pattern The pattern to include for exclusion
	 */
	public void addExcludePattern(final Pattern pattern) {
		if (pattern != null) {
			this.excludePatterns.add(pattern);
		}
	}

	/**
	 * Manually create a symlink between the target (just name) and the source (full path + name)
	 * @param target Just the name of the target file
	 * @param source The full path and name of the source file is needed
	 * @return boolean Whether or not the process completed correctly
	 * @throws IOException
	 */
	public boolean makeSymLink(final String target, final String source) throws IOException {
		int retVal = 0;
		Process process = null;

		final String[] commands = { "ln", "-s", target, source };
		process = Runtime.getRuntime().exec(commands);

		try {
			retVal = process.waitFor();
		} catch (final InterruptedException e) {
			SdrUiPlugin.getDefault().logError("Unable to create symlink", e);
		}

		return (retVal == 0);
	}
}
