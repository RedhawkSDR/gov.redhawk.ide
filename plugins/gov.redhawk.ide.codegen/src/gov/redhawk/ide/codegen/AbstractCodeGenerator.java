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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.CRC32;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

/**
 * @since 7.0
 */
public abstract class AbstractCodeGenerator implements IScaComponentCodegen {

	@Override
	public abstract IStatus cleanupSourceFolders(IProject project, IProgressMonitor monitor);

	@Override
	public abstract IStatus generate(ImplementationSettings implSettings, Implementation impl, PrintStream out, PrintStream err, IProgressMonitor monitor, // SUPPRESS CHECKSTYLE NumParameters
	        String[] generateFiles, boolean shouldGenerate, List<FileToCRCMap> crcMap);

	/**
	 * @since 9.0
	 */
	@Override
	public abstract Code getInitialCodeSettings(SoftPkg softPkg, ImplementationSettings settings, Implementation impl);

	@Override
	public abstract boolean shouldGenerate();

	/**
	 * This method checks to see if a file has changed or not.
	 * 
	 * @param implSettings the Implementation settings for this generation
	 * @param project The project containing the implementation
	 * @param fileName the name of the file to check
	 * @return true if the file has changed
	 * @throws  
	 * @throws CoreException 
	 * @since 10.0
	 */
	protected boolean checkFile(final ImplementationSettings implSettings, final IProject project, final String fileName) throws CoreException {
		if (project == null || !project.exists()) {
			return false;
		}
		final String outputDir = implSettings.getOutputDir();
		final IFile file = project.getFile(outputDir + "/" + fileName);

		if (file.exists()) {
			CRC32 check = new CRC32();
			check.reset();
			check.update(getFileBytes(file));
			final long crc = check.getValue();

			if (implSettings.getGeneratedFileCRCs() != null) {
				for (final FileToCRCMap map : implSettings.getGeneratedFileCRCs()) {
					if (map.getFile().equals(fileName)) {
						if (crc == map.getCrc()) {
							return false;
						} else {
							return true;
						}
					}
				}
			}
		} else {
			return false;
		}
		return false;
	}

	/**
	 * @since 5.0
	 */
	protected void updateCRC(final String fileName, final byte[] bs, final List<FileToCRCMap> crcMap) {
		if (crcMap == null) {
			return;
		}
		final FileToCRCMap map = CodegenFactory.eINSTANCE.createFileToCRCMap();
		CRC32 check = new CRC32();
		check.reset();
		check.update(bs);

		map.setCrc(check.getValue());
		map.setFile(fileName);
		crcMap.add(map);
	}

	/**
	 * Gets the contents of a file as an array of bytes. Line-terminators are removed from the data (BUG ALERT!)
	 * @throws CoreException 
	 * @throws IOException 
	 * 
	 * @since 3.0
	 */
	protected byte[] getFileBytes(final IFile file) throws CoreException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		InputStream input = null;
		try {
			input = file.getContents();
			IOUtils.copy(input, bos);
		} catch (IOException e) {
			throw new CoreException(new Status(Status.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Failed to read file " + file, e));
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// PASS
				}
			}
		}

		return bos.toByteArray();
	}

	/**
	 * @since 5.0
	 */
	public IFile getDefaultFile(final Implementation impl, final ImplementationSettings implSettings, final String defaultFilename) {
		final IResource resource = ModelUtil.getResource(implSettings);

		final IProject project = resource.getProject();
		return project.getFile(new Path(defaultFilename));
	}

	@Override
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

	/**
	 * 
	 * @param impl
	 * @param implSettings
	 * @return
	 * @since 10.0
	 */
	protected String getSourceDir(Implementation impl, final ImplementationSettings implSettings) {
		return implSettings.getOutputDir() + File.separator;
	}
	
	/**
	 * @since 10.0
	 */
	public Map<String, Boolean> getGeneratedFiles(final ImplementationSettings implSettings, final SoftPkg softPkg) throws CoreException {
		final IProject project = ModelUtil.getProject(softPkg);
		final HashMap<String, Boolean> fileMap = new HashMap<String, Boolean>();
		final ITemplateDesc template = CodegenUtil.getTemplate(implSettings.getTemplate(), implSettings.getGeneratorId());
		if (template == null) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID,
				"Unable to find code generation template. Please check your template selection under the 'Code"
					+ " Generation Details' section of the Implementation tab of your component."));
		}

		final List<String> templateFileList = template.getTemplate().getAllGeneratedFileNames(implSettings, softPkg);
		if (templateFileList != null) {
			for (final String fileName : templateFileList) {
				if (project != null) {
					fileMap.put(fileName, checkFile(implSettings, project, fileName));
				} else {
					fileMap.put(fileName, true);
				}
			}
		}

		return fileMap;
	}
	
	/**
	 * @since 10.0
	 */
	public List<String> getUnchangedFiles(final ImplementationSettings implSettings, final SoftPkg softPkg) throws CoreException {
		final IProject project = ModelUtil.getProject(softPkg);
		final List<String> fileList = new ArrayList<String>();
		final ITemplateDesc template = CodegenUtil.getTemplate(implSettings.getTemplate(), implSettings.getGeneratorId());
		if (template == null) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID,
				"Unable to find code generation template. Please check your template selection under the 'Code"
					+ " Generation Details' section of the Implementation tab of your component."));
		}

		final List<String> templateFileList = template.getTemplate().getAllGeneratedFileNames(implSettings, softPkg);
		if (templateFileList != null) {
			for (final String fileName : templateFileList) {
				if (!checkFile(implSettings, project, fileName)) {
					fileList.add(fileName);
				}
			}
		}

		return fileList;
	}

	/**
	 * @since 10.0
	 */
	@Override
	public Set<FileStatus> getGeneratedFilesStatus(ImplementationSettings implSettings, SoftPkg softpkg) throws CoreException {
		Map<String, Boolean> result = getGeneratedFiles(implSettings, softpkg);
		Set<FileStatus> retVal = new HashSet<FileStatus>();
		for (Map.Entry<String, Boolean> entry : result.entrySet()) {
			String filename = entry.getKey();
			Boolean modified = entry.getValue();
			if (modified != null) {
				if (modified) {
					retVal.add(new FileStatus(filename, FileStatus.Action.REGEN, FileStatus.State.MODIFIED, FileStatus.Type.SYSTEM));
				} else {
					retVal.add(new FileStatus(filename, FileStatus.Action.REGEN, FileStatus.State.MATCHES, FileStatus.Type.SYSTEM));
				}
			} else {
				retVal.add(new FileStatus(filename, FileStatus.Action.REGEN, FileStatus.State.MATCHES, FileStatus.Type.SYSTEM));
			}
		}
		return retVal;
	}
	
	/**
	 * @since 11.0
	 */
	protected String getSpdBaseName(SoftPkg spd) {
		String name = spd.getName();
		int lastDot = name.lastIndexOf('.');
		if (lastDot > -1) {
			return name.substring(lastDot + 1);
		}
		return name;
	}
}
