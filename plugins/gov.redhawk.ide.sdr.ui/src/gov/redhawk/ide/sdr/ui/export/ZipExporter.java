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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @since 3.1
 */
public class ZipExporter implements IScaExporter {
	private static final int DEFAULT_UNIX_READONLY_MODE = 0444;
	private static final int DEFAULT_UNIX_EXECUTABLE_MODE = 0755;
	private static final int BUFFER_SIZE = 4096;
	private final ZipOutputStream zos;
	private final IPath zipfilePath;

	public ZipExporter(final IPath zipFilePath) throws IOException {
		super();
		this.zos = new ZipOutputStream(new FileOutputStream(zipFilePath.toFile()));
		this.zipfilePath = zipFilePath;
	}

	public void finished() throws IOException {
		this.zos.close();
	}

	public void write(final IResource resource, final IPath destinationPath, final IProgressMonitor monitor) throws IOException, CoreException {

		if (resource.getLocation().toFile().isDirectory()) {
			addDirectory(resource, destinationPath, monitor);
		} else {
			addFile(resource, destinationPath, monitor);
		}
	}

	public void mkdir(final IPath destinationPath, final IProgressMonitor monitor) throws IOException, CoreException {
		// pass
	}

	private boolean isExecutable(final IPath path) {
		final ResourceAttributes attributes = ResourcesPlugin.getWorkspace().getRoot().getFile(path).getResourceAttributes();
		if (attributes != null) {
			return attributes.isExecutable();
		} else {
			return false;
		}
	}

	private boolean isReadOnly(final IPath path) {
		final ResourceAttributes attributes = ResourcesPlugin.getWorkspace().getRoot().getFile(path).getResourceAttributes();
		if (attributes != null) {
			return attributes.isReadOnly();
		} else {
			return false;
		}
	}

	private void addDirectory(final IResource resource, final IPath destinationPath, final IProgressMonitor monitor) throws IOException {
		for (final File f : resource.getLocation().toFile().listFiles()) {
			final IPath newPath = destinationPath.append(f.getName());
			if (f.isDirectory()) {
				addDirectory(ResourcesPlugin.getWorkspace().getRoot().getFolder(newPath), newPath, monitor);
			} else {
				addFile(ResourcesPlugin.getWorkspace().getRoot().getFile(newPath.removeFirstSegments(ExportUtils.PREFIX_SEGMENT_LENGTH)), newPath, monitor);
			}
		}
	}

	private void addFile(final IResource resource, final IPath destinationPath, final IProgressMonitor monitor) throws IOException {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(resource.getLocation().toFile());
			final ZipEntry anEntry = new ZipEntry(destinationPath.toString());

			this.setAttributes(resource, anEntry);
			this.zos.putNextEntry(anEntry);

			final byte[] readBuffer = new byte[BUFFER_SIZE];
			int bytesIn = 0;

			while ((bytesIn = fis.read(readBuffer)) != -1) {
				this.zos.write(readBuffer, 0, bytesIn);
			}
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	private void setAttributes(final IResource resource, final ZipEntry anEntry) {
		if (resource.getLocalTimeStamp() != IResource.NULL_STAMP) {
			anEntry.setTime(resource.getLocalTimeStamp());
		}
		if (this.isExecutable(resource.getFullPath())) {
			anEntry.setUnixMode(DEFAULT_UNIX_EXECUTABLE_MODE);
		}
		if (this.isReadOnly(resource.getFullPath())) {
			anEntry.setUnixMode(DEFAULT_UNIX_READONLY_MODE);
		}

	}

	public IPath getExportLocation() {
		return this.zipfilePath;
	}
}
