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

import java.io.IOException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @since 3.1
 */
public interface IScaExporter {

	/**
	 * This method should always be invoked as the last operation on the {@link IScaExporter}.
	 * 
	 * @throws IOException
	 */
	public void finished() throws IOException;

	/**
	 * Creates a directory.
	 * 
	 * @param destinationPath The destination path to create relative to the target export root
	 * @param monitor
	 * @throws IOException
	 * @throws CoreException
	 */
	public void mkdir(IPath destinationPath, IProgressMonitor monitor) throws IOException, CoreException;

	/**
	 * Exports a copy of an {@link IResource}.
	 * 
	 * @param resource The resource to be exported
	 * @param outputPath The destination path relative to the target export root
	 * @param monitor
	 * @throws IOException
	 * @throws CoreException
	 * @since 3.0
	 */
	public void write(IResource resource, IPath outputPath, IProgressMonitor monitor) throws IOException, CoreException;

	/**
	 * Deletes a target file if it exists from a previous export.
	 * @param deletePath The path to delete, if it exists
	 * @param monitor
	 * @throws IOException
	 * @throws CoreException
	 * @since 5.0
	 */
	void delete(IPath deletePath, IProgressMonitor monitor) throws IOException, CoreException;

	/**
	 * Returns the target export root location of the exporter.
	 * 
	 * @return The target export root location
	 * @since 4.0
	 */
	public IPath getExportLocation();

}
