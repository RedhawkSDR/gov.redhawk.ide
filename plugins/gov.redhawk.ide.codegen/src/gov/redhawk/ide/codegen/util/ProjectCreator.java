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
package gov.redhawk.ide.codegen.util;

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IOperatingSystem;
import gov.redhawk.ide.codegen.IProcessor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.natures.ScaProjectNature;
import gov.redhawk.sca.util.SubMonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.Processor;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A utility class for creating REDHAWK projects.
 * 
 * @since 9.0
 */
public abstract class ProjectCreator {

	protected ProjectCreator() {
	}

	/**
	 * Creates a new REDHAWK project without any files. Should be invoked in the context of a
	 * {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * <p />
	 * This code is based on an IBM Developer Works article by Benoit Marchal.
	 * 
	 * @param projectName The project name
	 * @param projectLocation the location on disk to create the project
	 * @param additionalNatureIDs The nature ID(s) (if any) to add to the project besides the {@link ScaProjectNature}
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * reported and that the operation cannot be canceled.
	 * @return The newly created project
	 * @throws CoreException A problem occurs while creating the project
	 */
	protected static IProject createEmptyProject(final String projectName, final java.net.URI projectLocation, final String[] additionalNatureIDs,
		final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating REDHAWK Project", 3);

		Set<String> natureIds = new HashSet<String>();
		natureIds.addAll(Arrays.asList(additionalNatureIDs));
		natureIds.add(ScaProjectNature.ID);

		// Get the root workspace
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

		// This creates the new project
		final IProject project = root.getProject(projectName);

		// Create an empty project description
		final IProjectDescription description = ResourcesPlugin.getWorkspace().newProjectDescription(project.getName());

		if (projectLocation != null) {
			description.setLocationURI(projectLocation);
		}

		description.setNatureIds(natureIds.toArray(new String[natureIds.size()]));

		// Don't use the create with description because it does not cause
		// natures to be configured
		project.create(description, progress.newChild(1));

		// Open the project
		project.open(progress.newChild(1));

		return project;
	}

	/**
	 * This method finds a file relative to the location of the spd file
	 * 
	 * @param spdFile path to the SPD file
	 * @param localfilename name of the file to find
	 * @param filters filter for file types matching the desired file
	 * @return Path for the file found, specified in a dialog or null if no file
	 * was found
	 * @since 10.0
	 */
	public static IPath findLocalFile(final IPath spdFile, final IPath localfilename, final String[] filters) {
		IPath localFile = spdFile.removeLastSegments(1).append(localfilename);
		if (localFile.toFile().exists()) {
			return localFile;
		}

		// First just see if it's easy to locate
		localFile = spdFile.removeLastSegments(1).append(localfilename.lastSegment());
		if (localFile.toFile().exists()) {
			return localFile;
		}

		// Disable UI code
		// // Now ask the user to locate it for us
		// final FileDialog dialog = new FileDialog(this.getShell(), SWT.OPEN);
		// dialog.setFilterExtensions(filters);
		// dialog.setFileName(localfilename.lastSegment());
		// dialog.setText("Locate missing file " + localfilename.lastSegment());
		// final String selection = dialog.open();
		//
		// if (selection != null) {
		// return new Path(selection);
		// }

		return null;
	}

	/**
	 * Copies the existing REDHAWK file into an empty REDHAWK project. Should be invoked in the
	 * context of a {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param project The project to generate files in
	 * @param existingFilePath The existing file's path
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be
	 * reported and that the operation cannot be canceled.
	 * @return The newly created REDHAWK file
	 * @throws CoreException An error occurs while generating files
	 */
	public static IFile importFile(final IProject project, IFile destination, final IPath existingFilePath, final IProgressMonitor monitor)
		throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Importing REDHAWK file", 2);

		final File existingFile = existingFilePath.toFile();
		FileInputStream input = null;
		try {
			input = new FileInputStream(existingFile);
			destination.create(input, true, progress.newChild(1));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "REDHAWK file (" + existingFilePath.toString() + ") does not exist"));
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					// PASS
				}
			}
		}
		monitor.worked(1);

		return destination;
	}

	/**
	 * 
	 * Copies existing SPD, PRF and SCD files into an empty REDHAWK resource project. Should be invoked in the
	 * context of a {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param project The project that the files will be imported to
	 * @param existingSpdFile Path to the existing SPD file
	 * @param implList List of implementations
	 * @param importedSettingsMap Mapping of imported implementations to their settings
	 * @param monitor The progress monitor
	 * @param id
	 * @return spdFile Returns a copy of the locally imported SPD file
	 * @throws CoreException
	 */
	public static IFile importFiles(final IProject project, final IPath existingSpdFile, final List<ImplementationAndSettings> implList,
		final HashMap<String, Boolean> importedSettingsMap, final IProgressMonitor monitor) throws CoreException {
		return importFiles(project, existingSpdFile, implList, importedSettingsMap, monitor, null);
	}

	/**
	 * 
	 * Copies existing SPD, PRF and SCD files into an empty REDHAWK resource project. Should be invoked in the
	 * context of a {@link org.eclipse.ui.actions.WorkspaceModifyOperation WorkspaceModifyOperation}.
	 * 
	 * @param project The project that the files will be imported to
	 * @param existingSpdFile Path to the existing SPD file
	 * @param implList List of implementations
	 * @param importedSettingsMap Mapping of imported implementations to their settings
	 * @param monitor The progress monitor
	 * @param id ID for new SPD
	 * @return spdFile Returns a copy of the locally imported SPD file
	 * @throws CoreException
	 * @since 9.1
	 */
	public static IFile importFiles(final IProject project, final IPath existingSpdFile, final List<ImplementationAndSettings> implList,
		final HashMap<String, Boolean> importedSettingsMap, final IProgressMonitor monitor, String id) throws CoreException {
		final SubMonitor subMonitor = SubMonitor.convert(monitor, "Importing REDHAWK XML", 7); // SUPPRESS CHECKSTYLE
																							// MagicNumber

		// Copy the SPD file into the project
		final IFile spdFile = project.getFile(project.getName() + SpdPackage.FILE_EXTENSION);
		try {
			spdFile.create(new FileInputStream(existingSpdFile.toFile()), true, subMonitor.newChild(1));
		} catch (final FileNotFoundException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "SPD File does not exist"));
		}
		// Get the URI of the model file.
		final URI fileURI = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), true).appendFragment(SoftPkg.EOBJECT_PATH);
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final SoftPkg spd = (SoftPkg) set.getEObject(fileURI, true);

		if (spd == null) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Invalid SPD File"));
		}

		spd.setName(project.getName());
		if (id == null) {
			spd.setId(DceUuidUtil.createDceUUID());
		} else {
			spd.setId(id);
		}
		if (spd.getPropertyFile() != null) {
			final IPath propertyFilePath = new Path(spd.getPropertyFile().getLocalFile().getName());
			final IPath propertyFile = ProjectCreator.findLocalFile(existingSpdFile, propertyFilePath, new String[] { "*" + PrfPackage.FILE_EXTENSION });

			if (propertyFile == null) {
				spd.setPropertyFile(null);
				subMonitor.worked(1);
			} else {
				spd.getPropertyFile().getLocalFile().setName(project.getName() + PrfPackage.FILE_EXTENSION);
				final IFile prfFile = project.getFile(spd.getPropertyFile().getLocalFile().getName());

				try {
					prfFile.create(new FileInputStream(propertyFile.toFile()), true, subMonitor.newChild(1));
				} catch (final FileNotFoundException e) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Referenced PRF File " + propertyFilePath
						+ " does not exist"));
				}
			}
		} else {
			subMonitor.notWorked(1);
		}

		if (spd.getDescriptor() != null) {
			final IPath descriptorFilePath = new Path(spd.getDescriptor().getLocalfile().getName());
			final IPath descriptorFile = ProjectCreator.findLocalFile(existingSpdFile, descriptorFilePath, new String[] { "*" + ScdPackage.FILE_EXTENSION });

			if (descriptorFile == null) {
				spd.setDescriptor(null);
				subMonitor.worked(1);
			} else {
				spd.getDescriptor().getLocalfile().setName(project.getName() + ScdPackage.FILE_EXTENSION);
				final IFile scdFile = project.getFile(spd.getDescriptor().getLocalfile().getName());

				try {
					scdFile.create(new FileInputStream(descriptorFile.toFile()), true, subMonitor.newChild(1));
				} catch (final FileNotFoundException e) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Referenced SCD File " + descriptorFilePath
						+ " does not exist"));
				}
			}
		} else {
			subMonitor.notWorked(1);
		}

		final IFile waveDevFile = project.getFile(CodegenUtil.getWaveDevSettingsURI(fileURI).lastSegment());
		if (waveDevFile.exists()) {
			waveDevFile.delete(true, subMonitor.newChild(1));
		} else {
			subMonitor.notWorked(1);
		}

		final Resource waveResource = set.createResource(URI.createPlatformResourceURI(waveDevFile.getFullPath().toString(), true));
		final WaveDevSettings wavedevSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
		waveResource.getContents().add(wavedevSettings);

		// Remove the old implementations
		spd.getImplementation().clear();

		// Add the new implementations
		for (final ImplementationAndSettings pair : implList) {
			final Implementation impl = pair.getImplementation();
			spd.getImplementation().add(impl);
			wavedevSettings.getImplSettings().put(impl.getId(), pair.getImplementationSettings());
		}

		// Save settings
		try {
			final SubMonitor loopProgress = subMonitor.newChild(1).setWorkRemaining(set.getResources().size());
			for (final Resource resource : set.getResources()) {
				// Don't save if the URI isn't a platform one; this is probably just a reference to
				// something in the SDRROOT
				if (!resource.getURI().isPlatform()) {
					loopProgress.notWorked(1);
					continue;
				}

				loopProgress.setTaskName("Saving " + resource.getURI().lastSegment());
				resource.save(null);
				loopProgress.worked(1);
			}
		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Internal Error", e));
		}

		// Check to see if we have any imported settings to copy over
		final SubMonitor loopProgress = subMonitor.newChild(1).setWorkRemaining(importedSettingsMap.size());
		for (final String settingsId : importedSettingsMap.keySet()) {
			// See if our settings wants to be copied over
			if (importedSettingsMap.get(settingsId)) {
				// Find its matching implementation
				Implementation tempImpl = null;
				for (final Implementation impl : spd.getImplementation()) {
					if (impl.getId().equals(settingsId)) {
						tempImpl = impl;
						break;
					}
				}

				final SubMonitor loopProgress2 = loopProgress.newChild(1).setWorkRemaining(3);

				// Add the appropriate nature to the project via the generate
				// command
				CodegenFileHelper.addProjectNature(project, wavedevSettings.getImplSettings().get(settingsId), tempImpl, loopProgress2.newChild(1));

				// Copy the source files
				CodegenFileHelper.copySourceFiles(existingSpdFile, project, settingsId, loopProgress2.newChild(1));

				// Refresh the output folder
				project.getFolder(wavedevSettings.getImplSettings().get(settingsId).getOutputDir()).refreshLocal(IResource.DEPTH_INFINITE,
					loopProgress2.newChild(1));
			} else {
				loopProgress.notWorked(1);
			}
		}

		// Refresh the project and all subfolders
		project.refreshLocal(IResource.DEPTH_INFINITE, subMonitor.newChild(1));

		return spdFile;
	}

	/**
	 * IDE-1111: Make usable file/class names out of project names
	 * @param project The project for which to generate file names
	 * @return The base name to be used for files and classes in the project
	 * @since 11.0
	 */
	protected static String getBaseFileName(IProject project) {
		return getBaseFileName(project.getName());
	}

	/**
	 * IDE-1111: Make usable file/class names out of project names
	 * @param name The original project name
	 * @return The base name to be used for files and classes in the project
	 * @since 11.0
	 */
	public static String getBaseFileName(String name) {
		if (name == null) {
			return null;
		}
		String[] tokens = name.split("\\.");
		return tokens[tokens.length - 1];
	}

	/**
	 * Makes sure that a project has been cleaned out.
	 * @param project
	 * @throws CoreException
	 */
	public static void resetProject(final IProject project, final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, 1);

		// IDE-1111 Make sure we are looking for correct file names
		final IFile spdFile = project.getFile(getBaseFileName(project) + SpdPackage.FILE_EXTENSION);
		final URI spdUri = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), true).appendFragment(SoftPkg.EOBJECT_PATH);
		final IFile waveDevFile = project.getFile(CodegenUtil.getWaveDevSettingsURI(spdUri).lastSegment());
		if (waveDevFile.exists()) {
			waveDevFile.delete(true, progress.newChild(1));
		} else {
			progress.setWorkRemaining(1);
		}

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource waveResource = resourceSet.createResource(URI.createPlatformResourceURI(waveDevFile.getFullPath().toString(), true));
		final WaveDevSettings wavedevSettings = CodegenFactory.eINSTANCE.createWaveDevSettings();
		waveResource.getContents().add(wavedevSettings);

		// Save EMF resources
		try {
			final SubMonitor loopProgress = progress.newChild(1).setWorkRemaining(resourceSet.getResources().size() * 2);
			for (final Resource resource : resourceSet.getResources()) {
				loopProgress.setTaskName("Saving " + resource.getURI().lastSegment());
				// Save the resource to disk
				resource.save(null);
				loopProgress.worked(1);

				// Generate an Eclipse resource change notification
				String platformURI = resource.getURI().toPlatformString(true);
				ResourcesPlugin.getWorkspace().getRoot().findMember(platformURI).refreshLocal(IResource.DEPTH_ZERO, progress.newChild(1));
			}
		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Internal Error", e));
		}
	}

	/**
	 * Establishes initial implementation parameters based on the selected code generator.
	 * 
	 * @param impl The implementation to be added
	 * @param settings The implementation's settings
	 * @throws CoreException An error occurs while working with the SPD or WaveDev files
	 */
	public static void setupImplementation(final SoftPkg softPkg, final Implementation impl, final ImplementationSettings settings) {
		final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());

		if (codeGenDesc != null) {
			IScaComponentCodegen generator;
			try {
				generator = codeGenDesc.getGenerator();
				final Code codeSettings = generator.getInitialCodeSettings(softPkg, settings, impl);
				impl.setCode(codeSettings);
			} catch (CoreException e) {
				RedhawkCodegenActivator.getDefault().getLog().log(
					new Status(Status.WARNING, RedhawkCodegenActivator.PLUGIN_ID, "Errors wihle initializing code settings.", e));
			}

			if (impl.getOs().size() == 0) {
				for (final IOperatingSystem tempOs : codeGenDesc.getOperatingSystems()) {
					if (tempOs.isDefault()) {
						final Os os = SpdFactory.eINSTANCE.createOs();
						os.setName(tempOs.getName());

						if (tempOs.getVersion() != "") {
							os.setVersion(tempOs.getVersion());
						}

						impl.getOs().add(os);
					}
				}
			}
			if (impl.getProcessor().size() == 0) {
				for (final IProcessor tempProc : codeGenDesc.getProcessors()) {
					if (tempProc.isDefault()) {
						final Processor proc = SpdFactory.eINSTANCE.createProcessor();
						proc.setName(tempProc.getProcessorType());
						impl.getProcessor().add(proc);
					}
				}
			}
		}
	}

	/**
	 * Directly adds an implementation to a {@link SoftPkg}.
	 * 
	 * The {@link Implementation} will have its {@link Code} entry filled in
	 * based on the code generator template specified in {@link ImplementationSettings}.
	 * <p />
	 * This should be invoked in the context of a {@link org.eclipse.ui.actions.WorkspaceModifyOperation
	 * WorkspaceModifyOperation}.
	 * @param project
	 * @param impl
	 * @param settings
	 * @param monitor
	 * @deprecated This method will be removed in the future. Use the updated method which takes in the String spdName.
	 */
	@Deprecated
	public static void addImplementation(final IProject project, final Implementation impl, final ImplementationSettings settings,
		final IProgressMonitor monitor) throws CoreException {
		addImplementation(project, null, impl, settings, monitor);
	}

	/**
	 * Directly adds an implementation to a {@link SoftPkg}.
	 * 
	 * The {@link Implementation} will have its {@link Code} entry filled in
	 * based on the code generator template specified in {@link ImplementationSettings}.
	 * <p />
	 * This should be invoked in the context of a {@link org.eclipse.ui.actions.WorkspaceModifyOperation
	 * WorkspaceModifyOperation}.
	 * 
	 * @param project
	 * @param impl
	 * @param command null if the implementation isn't being added within an editor context
	 * @since 10.0
	 */
	public static void addImplementation(final IProject project, final String spdName, final Implementation impl, final ImplementationSettings settings,
		final IProgressMonitor monitor) throws CoreException {

		// IDE-1111: Make sure correct file names are being used
		String spdFileBaseName = (spdName == null) ? getBaseFileName(project) : getBaseFileName(spdName);
		String spdFileName = spdFileBaseName + SpdPackage.FILE_EXTENSION; // SUPPRESS

		final SubMonitor progress = SubMonitor.convert(monitor, 2);

		final IFile spdFile = project.getFile(spdFileName);
		final URI spdUri = URI.createPlatformResourceURI(spdFile.getFullPath().toString(), true).appendFragment(SoftPkg.EOBJECT_PATH);
		final IFile waveDevFile = project.getFile(CodegenUtil.getWaveDevSettingsURI(spdUri).lastSegment());
		final URI waveDevUri = URI.createPlatformResourceURI(waveDevFile.getFullPath().toString(), true);

		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();

		Assert.isTrue(waveDevFile.exists());
		Assert.isTrue(spdFile.exists());
		// Fill in the implementation's code settings (i.e. localfile, entrypoint, etc) using the code generator
		// specified in the implementation settings
		final SoftPkg eSpd = (SoftPkg) resourceSet.getEObject(spdUri, true);
		final WaveDevSettings wavedevSettings = (WaveDevSettings) resourceSet.getResource(waveDevUri, true).getEObject("/");
		Assert.isNotNull(eSpd);
		Assert.isNotNull(wavedevSettings);

		// Add the implementation to the SPD
		if (!eSpd.getImplementation().contains(impl)) {
			eSpd.getImplementation().add(impl);
		}
		Assert.isTrue(impl.eContainer().equals(eSpd));

		// Create WaveDevSettings and add them to the WaveDev file
		wavedevSettings.getImplSettings().put(impl.getId(), settings);

		ProjectCreator.setupImplementation(eSpd, impl, settings);

		final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(settings.getGeneratorId());
		final IScaComponentCodegen generator = codeGenDesc.getGenerator();
		final Code codeSettings = generator.getInitialCodeSettings(eSpd, settings, impl);
		impl.setCode(codeSettings);

		if (CodegenUtil.canPrimary(impl.getProgrammingLanguage().getName())) {
			final EMap<String, ImplementationSettings> settingsMap = wavedevSettings.getImplSettings();
			boolean primary = true;
			for (final Implementation tmpImpl : eSpd.getImplementation()) {
				if (tmpImpl.getProgrammingLanguage().getName().equals(impl.getProgrammingLanguage().getName())) {
					final ImplementationSettings set = settingsMap.get(tmpImpl.getId());
					if (set.isPrimary()) {
						primary = false; // Don't change the primary if another implementation of the same language is
											// primary
					}
				}
			}
			settings.setPrimary(primary);
		}

		// Save EMF resources
		try {
			final SubMonitor loopProgress = progress.newChild(1).setWorkRemaining(resourceSet.getResources().size() * 2);
			for (final Resource resource : resourceSet.getResources()) {
				loopProgress.setTaskName("Saving " + resource.getURI().lastSegment());
				// Save the resource to disk
				resource.save(null);
				loopProgress.worked(1);

				// Generate an Eclipse resource change notification
				String platformURI = resource.getURI().toPlatformString(true);
				ResourcesPlugin.getWorkspace().getRoot().findMember(platformURI).refreshLocal(IResource.DEPTH_ZERO, loopProgress.newChild(1));
			}
		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Internal Error", e));
		}
	}

	/**
	 * @since 11.0
	 */
	public static String createDirectoryBlock(String subDirectory) {
		final String WAVEFORMS = "waveforms", NODES = "nodes";
		StringBuilder builder = new StringBuilder();

		// Preserve the existing prefix, as this is specific to the project type
		String pathPrefix;
		if (subDirectory.contains(WAVEFORMS)) {
			pathPrefix = subDirectory.substring(0, subDirectory.indexOf(WAVEFORMS) + WAVEFORMS.length());
		} else if (subDirectory.contains(NODES)) {
			pathPrefix = subDirectory.substring(0, subDirectory.indexOf(NODES) + NODES.length());
		} else {
			return subDirectory;
		}

		// Making the assumption that the prefix includes the first three elements of the array
		String[] textArray = subDirectory.split("/");
		textArray = Arrays.copyOfRange(textArray, 3, textArray.length);

		// Add lines to the directory block for each folder in the path
		String pathSuffix = "";
		for (int i = 0; i < textArray.length; i++) {
			pathSuffix = pathSuffix + "/" + textArray[i];
			if (i == textArray.length - 1) {
				builder.append(pathPrefix + pathSuffix);
			} else {
				builder.append(pathPrefix + pathSuffix + "\n");
			}
		}

		// Return the formated block
		return builder.toString();
	}
}
