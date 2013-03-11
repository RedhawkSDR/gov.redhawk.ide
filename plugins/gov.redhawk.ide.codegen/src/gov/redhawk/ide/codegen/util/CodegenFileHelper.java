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

package gov.redhawk.ide.codegen.util;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.sca.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;

/**
 * @since 5.0
 */
public class CodegenFileHelper {

	private static IFileStore sourceDir;
	private static IFileStore destDir;

	private CodegenFileHelper() {

	}

	/**
	 * @since 7.0
	 */
	public static HashMap<String, Boolean> settingsHasSourceCode(final WaveDevSettings waveSettings, final URI fileURI) {
		final String workingPath = fileURI.path().substring(0, fileURI.path().lastIndexOf("/"));
		final HashMap<String, Boolean> settingsMap = new HashMap<String, Boolean>();

		if (waveSettings != null) {
			for (final ImplementationSettings implSettings : waveSettings.getImplSettings().values()) {
				settingsMap.put(implSettings.getId(), false);

				final IFileStore outputDir = EFS.getLocalFileSystem().getStore(
				        java.net.URI.create((workingPath + IPath.SEPARATOR + implSettings.getOutputDir())));

				try {
					if (Arrays.asList(outputDir.childInfos(0, new NullProgressMonitor())).size() > 0) {
						settingsMap.put(implSettings.getId(), true);
					}
				} catch (final CoreException e) {
					RedhawkCodegenActivator.logError("Unable to retrieve info on folder from file system.", null);
				}
			}
		}

		return settingsMap;
	}

	public static boolean copySourceFiles(final IPath sourceSpdPath, final IProject project, final String settingsId, final IProgressMonitor monitor) {
		final String workingPath = sourceSpdPath.toString().substring(0, sourceSpdPath.toString().lastIndexOf("/"));
		final WaveDevSettings waveSettings = CodegenUtil.getWaveDevSettings(CodegenUtil.getWaveDevSettingsURI(URI.createFileURI(sourceSpdPath.toString())));
		final ImplementationSettings implSettings = waveSettings.getImplSettings().get(settingsId);
		final IFolder outputDir = project.getFolder(implSettings.getOutputDir());

		try {
			outputDir.create(true, true, monitor);
		} catch (final CoreException e) {
			RedhawkCodegenActivator.logError("Unable to create new destination directory.", null);
		}

		CodegenFileHelper.sourceDir = EFS.getLocalFileSystem().getStore(java.net.URI.create((workingPath + IPath.SEPARATOR + implSettings.getOutputDir())));
		CodegenFileHelper.destDir = EFS.getLocalFileSystem().getStore(java.net.URI.create(outputDir.getLocation().toString()));

		try {
			CodegenFileHelper.sourceDir.copy(CodegenFileHelper.destDir, EFS.OVERWRITE, monitor);
		} catch (final CoreException e) {
			RedhawkCodegenActivator.logError("Unable to copy contents of source directory.", null);
		}

		return true;
	}

	public static void addProjectNature(final IProject project, final ImplementationSettings implSettings, final Implementation impl,
	        final IProgressMonitor monitor) throws CoreException {
		final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(implSettings.getGeneratorId());

		if (codeGenDesc != null) {
			IScaComponentCodegen generator = null;
			generator = codeGenDesc.getGenerator();

			if (codeGenDesc.getLanguage().contains(CodegenUtil.CPP) || codeGenDesc.getLanguage().contains(CodegenUtil.PYTHON)
			        || codeGenDesc.getLanguage().contains(CodegenUtil.JAVA)) {
				// We throw an exception on error, but suppress otherwise because we have no GUI facilities available here
				final IStatus status = generator.generate(implSettings, impl, System.out, System.err, monitor, new String[] { "Adding Project Nature" }, true,
				        new ArrayList<FileToCRCMap>());
				if (status.getSeverity() == IStatus.ERROR) {
					throw new CoreException(status);
				}

				final IFolder folder = project.getFolder(implSettings.getOutputDir());
				folder.delete(true, monitor);
			}
		}
	}

	/**
	 * Get's a human readable implementation name in a safe manner.
	 * 
	 * This code is specifically designed to retain compatibility with R.1.7.X series generators.
	 * @since 9.0
	 */
	@SuppressWarnings("deprecation")
    public static String safeGetImplementationName(Implementation impl, ImplementationSettings settings) {
		String result = null;
		if (DceUuidUtil.isValid(impl.getId())) {
			result = settings.getName();
		} else {
			result = impl.getId();
		}
		
		// Cleanup the name
		if (result != null) {
			result = result.replace(' ', '_');
		}
		return result;
	}
	
	/**
	 * Gets the preferred file prefix a generator should use.
	 * 
	 * This code is specifically designed to retain compatibility with R.1.7.X series generators.
	 * 
	 * @since 9.0
	 */
	@SuppressWarnings("deprecation")
    public static String getPreferredFilePrefix(SoftPkg softPkg, ImplementationSettings settings) {
		Assert.isNotNull(softPkg);
		Assert.isNotNull(settings);
		String result = null;
		if (DceUuidUtil.isValid(settings.getId()) && (settings.getName() != null)) {
			result = settings.getName();
		} else {
			result = softPkg.getName();
		}
		int index = result.indexOf('.');
		if (index > 0) {
			result = result.substring(index);
		}
		
		// Cleanup the name
		if (result != null) {
			result = result.replace(' ', '_');
		}
		return result;
	}
	
	/**
	 * Gets the {@link SoftPkg} name formatted appropriate for a file system name.
	 *  
	 * @param softPkg
	 * @return
	 * @see #getPreferredFilePrefix(SoftPkg, ImplementationSettings)
	 * @since 9.0
	 */
	public static String getProjectFileName(SoftPkg softPkg) {
		final String softPkgName = softPkg.getName();
		final StringBuilder builder = new StringBuilder(softPkgName.length());
		for (int i = 0; i < softPkgName.length(); i++) {
			final char c = softPkgName.charAt(i);
			if (Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '-') {
				builder.append(c);
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
	
	/**
	 * @since 9.0
	 */
	public static String createDefaultOutputDir(final SoftPkg softPkg, final ICodeGeneratorDescriptor codeGen) {
		// Make up a unique new name here.
		final StringBuilder outputDir = new StringBuilder();
		String baseName = null;
		if ((codeGen == null) || (codeGen.getLanguage() == null)) {
			baseName = "src";
		} else {
			baseName = codeGen.getLanguage().toLowerCase();
		}
		
		baseName = baseName.replace('+', 'p');
		outputDir.append(baseName);
		
		final List<String> dirs = new ArrayList<String>();
		if (softPkg != null) {
			final WaveDevSettings waveDevSettings = CodegenUtil.loadWaveDevSettings(softPkg);
			if (waveDevSettings != null) {
				for (final Implementation anImpl : softPkg.getImplementation()) {
					final ImplementationSettings settings = waveDevSettings.getImplSettings().get(anImpl.getId());
					if (settings != null) {
						dirs.add(settings.getOutputDir());
					}
				}
			}
		}
		return StringUtil.defaultCreateUniqueString(outputDir.toString(), dirs, StringUtil.getDefaultUpdateStrategy(baseName, 1));
	}
}
