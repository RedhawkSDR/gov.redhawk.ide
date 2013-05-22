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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.codegen;

import gov.redhawk.model.sca.util.ModelUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

/**
 * @since 7.0
 */
public abstract class AbstractCodeGenerator implements IScaComponentCodegen {

	private final CRC32 check = new CRC32();

	public abstract IStatus cleanupSourceFolders(IProject project, IProgressMonitor monitor);

	public abstract IStatus generate(ImplementationSettings implSettings, Implementation impl, PrintStream out, PrintStream err, IProgressMonitor monitor, // SUPPRESS CHECKSTYLE NumParameters
	        String[] generateFiles, boolean shouldGenerate, List<FileToCRCMap> crcMap);

	public abstract HashMap<String, Boolean> getGeneratedFiles(ImplementationSettings implSettings, SoftPkg softpkg) throws CoreException;

	/**
	 * @since 9.0
	 */
	public abstract Code getInitialCodeSettings(SoftPkg softPkg, ImplementationSettings settings, Implementation impl);

	public abstract boolean shouldGenerate();

	/**
	 * This method checks to see if a file has changed or not.
	 * 
	 * @param implSettings the Implementation settings for this generation
	 * @param project The project containing the implementation
	 * @param fileMap a map of FileName and changed status, only populated if it
	 *            has changed
	 * @param unchangedList a List to add the filename to if it hasn't changed
	 * @param fileName the name of the file to check
	 * @return true if the file has changed or the project doesn't exist
	 * @since 5.0
	 */
	protected boolean checkFile(final ImplementationSettings implSettings, final IProject project, final HashMap<String, Boolean> fileMap,
	        final List<String> unchangedList, final String fileName) {
		if (project == null) {
			fileMap.put(fileName, false);
			return true;
		}
		
		final String outputDir = implSettings.getOutputDir();
		final IFile file = project.getFile(outputDir + "/" + fileName);

		if (file.exists()) {
			this.check.reset();
			this.check.update(getFileBytes(file));
			final long crc = this.check.getValue();

			if (implSettings.getGeneratedFileCRCs() != null) {
				for (final FileToCRCMap map : implSettings.getGeneratedFileCRCs()) {
					if (map.getFile().equals(fileName)) {
						if (crc == map.getCrc()) {
							if (unchangedList != null) {
								unchangedList.add(fileName);
							}
							return false;
						} else {
							break;
						}
					}
				}
			}
		}

		if (fileMap != null) {
			fileMap.put(fileName, !file.exists());
		}
		return true;
	}

	/**
	 * @since 5.0
	 */
	protected void updateCRC(final String fileName, final byte[] bs, final List<FileToCRCMap> crcMap) {
		if (crcMap == null) {
			return;
		}
		final FileToCRCMap map = CodegenFactory.eINSTANCE.createFileToCRCMap();
		this.check.reset();
		this.check.update(bs);

		map.setCrc(this.check.getValue());
		map.setFile(fileName);
		crcMap.add(map);
	}

	/**
	 * Gets the contents of a file as an array of bytes. Line-terminators are removed from the data (BUG ALERT!)
	 * 
	 * @since 3.0
	 */
	protected byte[] getFileBytes(final IFile file) {
		byte[] ret = new byte[0];

		try {
			final BufferedReader r = new BufferedReader(new InputStreamReader(file.getContents()));
			final StringBuilder s = new StringBuilder();
			while (r.ready()) {
				s.append(r.readLine());
			}
			ret = s.toString().getBytes();
		} catch (final CoreException e) {
			// PASS
		} catch (final IOException e) {
			// PASS
		}

		return ret;
	}

	/**
	 * Strips newlines from an array of bytes. This is used to modify byte arrays to be backwards compatible with
	 * those returned by {@link #getFileBytes(IFile)}, which reads a file in without newlines.
	 * 
	 * @param input The input to be processed
	 * @return A new byte array without newlines
	 * @since 8.0
	 */
	protected byte[] stripNewlines(final byte[] input) {
		byte[] ret = new byte[0];

		try {
			final BufferedReader r = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input)));
			final StringBuilder s = new StringBuilder();
			while (r.ready()) {
				s.append(r.readLine());
			}
			ret = s.toString().getBytes();
		} catch (final IOException e) {
			// PASS
		}

		return ret;
	}

	/**
	 * @since 5.0
	 */
	public IFile getDefaultFile(final Implementation impl, final ImplementationSettings implSettings, final String defaultFilename) {
		final IResource resource = ModelUtil.getResource(implSettings);

		final IProject project = resource.getProject();
		return project.getFile(new Path(defaultFilename));
	}

	public IFile getDefaultFile(final Implementation impl, final ImplementationSettings implSettings) {
		final ITemplateDesc template = CodegenUtil.getTemplate(implSettings.getTemplate(), implSettings.getGeneratorId());
		IFile file = null;

		try {
			final IScaComponentCodegenTemplate temp = template.getTemplate();
			final String srcDir = getSourceDir(impl, implSettings);
			file = getDefaultFile(impl, implSettings, temp.getDefaultFilename((SoftPkg) impl.eContainer(), implSettings, srcDir));
		} catch (final CoreException c) {
			RedhawkCodegenActivator.logWarning("Unable to query code generator template '" + template.getId() + "' for default file", c);
		}

		return file;
	}

	protected String getSourceDir(Implementation impl, final ImplementationSettings implSettings) {
		return implSettings.getOutputDir() + File.separator;
	}
}
