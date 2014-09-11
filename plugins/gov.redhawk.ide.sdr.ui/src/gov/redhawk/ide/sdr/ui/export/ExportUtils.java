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

import gov.redhawk.ide.RedhawkIdeActivator;
import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @since 3.1
 */
public class ExportUtils {

	/**
	 * @since 3.1
	 */
	public static final int PREFIX_SEGMENT_LENGTH = 2;

	/**
	 * @since 3.2
	 */
	public static final Set<String> WAVEFORM_EXPORT_EXTENSIONS = ExportUtils.getWaveformExtensions();
	/**
	 * @since 3.2
	 */
	public static final Set<String> NODE_EXPORT_EXTENSIONS = ExportUtils.getNodeExtensions();

	private ExportUtils() {
	}

	private static Set<String> getWaveformExtensions() {
		final Set<String> retVal = new HashSet<String>();
		retVal.add(".sad_diagramV2");
		retVal.add(SadPackage.FILE_EXTENSION);
		return retVal;
	}

	private static Set<String> getNodeExtensions() {
		final Set<String> retVal = new HashSet<String>();
		retVal.add(".dcd_diagramV2");
		retVal.add(DcdPackage.FILE_EXTENSION);
		return retVal;
	}

	private static void exportFiles(IProject proj, IPath outputFolder, IScaExporter exporter, SubMonitor progress, Collection<String> validExtensions)
		throws CoreException, IOException {
		int loopWorkRemaining = proj.members().length;
		for (final IResource child : proj.members()) {
			String fileName = child.getName();
			for (String validExtension : validExtensions) {
				if (fileName.endsWith(validExtension)) {
					final IPath outputFile = outputFolder.append(fileName);
					exporter.write(child, outputFile, progress.newChild(1));
					break;
				}
			}
			progress.setWorkRemaining(--loopWorkRemaining);
		}
	}

	/**
	 * Exports waveforms from the specified project using the provided exporter.
	 * 
	 * @param proj The project to search for waveforms
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportWaveform(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaWaveformProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
			return;
		}
		
		if (isBuildSH(proj)) {
			buildSH(monitor, proj);
			return;
		}

		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting waveforms", 1 + proj.members().length);

		final IPath outputFolder = new Path("dom/waveforms").append(proj.getName());
		exporter.mkdir(outputFolder, progress.newChild(1));
		ExportUtils.exportFiles(proj, outputFolder, exporter, progress, ExportUtils.WAVEFORM_EXPORT_EXTENSIONS);
	}

	/**
	 * Exports components from the specified project using the provided exporter.
	 * 
	 * @param proj The project to search for components
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportComponent(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaComponentProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
			return;
		}

		if (isBuildSH(proj)) {
			buildSH(monitor, proj);
			return;
		}

		ExportUtils.basicExportComponent(proj, exporter, true, monitor);
	}

	/**
	 * Exports nodes from the specified project using the provided exporter.
	 * 
	 * @param proj The project to search for nodes
	 * @param exporter The IScaExporter to use
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportNode(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaNodeProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
			return;
		}
		
		if (isBuildSH(proj)) {
			buildSH(monitor, proj);
			return;
		}

		final int EXPORT_NODE_WORK = 10;
		final int EXPORT_COMPONENT_WORK = 90;
		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting nodes", EXPORT_NODE_WORK + EXPORT_COMPONENT_WORK);
		final SubMonitor nodeProgress = progress.newChild(EXPORT_NODE_WORK).setWorkRemaining(1 + proj.members().length);

		final IPath outputFolder = new Path("dev/nodes").append(proj.getName());
		exporter.mkdir(outputFolder, nodeProgress.newChild(1));
		ExportUtils.exportFiles(proj, outputFolder, exporter, nodeProgress, ExportUtils.NODE_EXPORT_EXTENSIONS);
	}

	/**
	 * @return System environment overridden with preference values
	 * @since 3.4
	 */
	public static Map<String, String> getEnv() {
		Map<String, String> env = new HashMap<String, String>(System.getenv());

		IPath targetSDRPath = SdrUiPlugin.getDefault().getTargetSdrPath();
		env.put("SDRROOT", targetSDRPath.toOSString());
		IPath ossiehome = RedhawkIdeActivator.getDefault().getRuntimePath();
		env.put("OSSIEHOME", ossiehome.toOSString());

		if (env.containsKey("CLASSPATH")) {
			env.put("CLASSPATH", ossiehome.toOSString() + "/lib/*:" + env.get("CLASSPATH"));
		} else {
			env.put("CLASSPATH", ossiehome.toOSString() + "/lib/*");
		}

		if (env.containsKey("PYTHONPATH")) {
			if (Platform.ARCH_X86_64.equals(Platform.getOSArch())) {
				env.put("PYTHONPATH", ossiehome.toOSString() + "/lib64/python:" + ossiehome.toOSString() + "/lib/python:" + env.get("PYTHONPATH"));
			} else {
				env.put("PYTHONPATH", ossiehome.toOSString() + "/lib/python:" + env.get("PYTHONPATH"));
			}
		} else {
			if (Platform.ARCH_X86_64.equals(Platform.getOSArch())) {
				env.put("PYTHONPATH", ossiehome.toOSString() + "/lib64/python:" + ossiehome.toOSString() + "/lib/python");
			} else {
				env.put("PYTHONPATH", ossiehome.toOSString() + "/lib/python");
			}
		}

		if (env.containsKey("LD_LIBRARY_PATH")) {
			if (Platform.ARCH_X86_64.equals(Platform.getOSArch())) {
				env.put("LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib64:" + ossiehome.toOSString() + "/lib:" + env.get("LD_LIBRARY_PATH"));
			} else {
				env.put("LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib:" + env.get("LD_LIBRARY_PATH"));
			}
		} else {
			if (Platform.ARCH_X86_64.equals(Platform.getOSArch())) {
				env.put("LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib64:" + ossiehome.toOSString() + "/lib");
			} else {
				env.put("LD_LIBRARY_PATH", ossiehome.toOSString() + "/lib");
			}
		}

		if (env.containsKey("PATH")) {
			env.put("PATH", ossiehome.toOSString() + "/bin/:" + env.get("PATH"));
		} else {
			env.put("PATH", ossiehome.toOSString() + "/bin/");
		}
		return env;
	}

	/**
	 * Shared by {@link #exportDevice(IProject, IScaExporter, IProgressMonitor)},
	 * {@link #exportComponent(IProject, IScaExporter, IProgressMonitor)}, and
	 * {@link #exportNode(IProject, IScaExporter, IProgressMonitor)} to perform common export functions.
	 * 
	 * @param proj The project to search for software packages
	 * @param exporter The IScaExporter to use
	 * @param includeCode If code should be copied with the component
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 * to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 * that the operation cannot be canceled.
	 */
	private static void basicExportComponent(final IProject proj, final IScaExporter exporter, final boolean includeCode, final IProgressMonitor monitor)
		throws CoreException, IOException {
		if (!proj.hasNature(ScaComponentProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		// Find .spd.xml files
		final List<IResource> spds = new ArrayList<IResource>();
		for (final IResource child : proj.members()) {
			if (child.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				spds.add(child);
			}
		}

		final SubMonitor allComponentsProgress = SubMonitor.convert(monitor, "Exporting SPD resources...", spds.size());
		final int SPD_WORK = 1;
		final int PRF_WORK = 1;
		final int SCD_WORK = 1;
		final int MKDIR_WORK = 1;
		final int IMPL_WORK = 96;

		for (final IResource spdResource : spds) {
			final SubMonitor progress = allComponentsProgress.newChild(1);
			progress.beginTask("Exporting resources of " + spdResource.getName(), SPD_WORK + PRF_WORK + SCD_WORK + IMPL_WORK + MKDIR_WORK);

			// Now load the SPD file and copy the prf and scd files, if any
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final Resource resource = resourceSet.getResource(URI.createURI(spdResource.getLocationURI().toString()), true);
			final SoftPkg softPkg = ModelUtil.getSoftPkg(resource);

			IPath outputFolder = null;
			ComponentType type = ComponentType.OTHER;
			if ((softPkg.getDescriptor() != null) && (softPkg.getDescriptor().getComponent() != null)) {
				final SoftwareComponent component = softPkg.getDescriptor().getComponent();
				type = SoftwareComponent.Util.getWellKnownComponentType(component);
			}

			switch (type) {
			case DEVICE:
				outputFolder = new Path("dev/devices");
				break;
			case DEVICE_MANAGER:
				outputFolder = new Path("dev/nodes");
				break;
			case DOMAIN_MANAGER:
				outputFolder = new Path("dom/mgr");
				break;
			case EVENT_SERVICE:
			case SERVICE:
				outputFolder = new Path("dev/services");
				break;
			case FILE_MANAGER:
			case FILE_SYSTEM:
			case LOG:
			case NAMING_SERVICE:
			case RESOURCE:
			case OTHER:
			case RESOURCE_FACTORY:
				outputFolder = new Path("dom/components");
				break;
			default:
				throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Unknown component type: " + type));
			}

			final IPath spdRootPath = spdResource.getFullPath().removeLastSegments(1);

			// If we were told to include code make sure at least one implementation has generated code.
			if (includeCode) {
				if (!ExportUtils.checkProjectImplsForExport(softPkg, spdRootPath)) {
					return;
				}
			}

			outputFolder = outputFolder.append(proj.getName());
			exporter.mkdir(outputFolder, progress.newChild(MKDIR_WORK));

			// Copy the SPD File
			IPath outputPath = outputFolder.append(spdResource.getName());
			exporter.write(spdResource, outputPath, progress.newChild(SPD_WORK));

			if (softPkg.getPropertyFile() != null) {
				final IPath prfPath = new Path(softPkg.getPropertyFile().getLocalFile().getName());
				if (prfPath.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}
				final IResource srcPath = ExportUtils.getWorkspaceResource(spdRootPath.append(prfPath));
				if (srcPath == null) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + prfPath + " does not exist"));
				}
				outputPath = outputFolder.append(prfPath);
				exporter.write(srcPath, outputPath, progress.newChild(PRF_WORK));
			}
			progress.setWorkRemaining(SCD_WORK + IMPL_WORK);

			if (softPkg.getDescriptor() != null) {
				final IPath scdPath = new Path(softPkg.getDescriptor().getLocalfile().getName());
				if (scdPath.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}
				final IResource srcPath = ExportUtils.getWorkspaceResource(spdRootPath.append(scdPath));
				if (srcPath == null) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + scdPath + " does not exist"));
				}
				outputPath = outputFolder.append(scdPath);
				exporter.write(srcPath, outputPath, progress.newChild(SCD_WORK));
			}
			progress.setWorkRemaining(IMPL_WORK);

			if (softPkg.getImplementation() != null) {
				int implWork = softPkg.getImplementation().size() * (PRF_WORK + ((includeCode) ? IMPL_WORK : 0)); // SUPPRESS
																													// CHECKSTYLE
																													// INLINE
				progress.setWorkRemaining(implWork);
				for (final Implementation impl : softPkg.getImplementation()) {
					ExportUtils.exportImpl(exporter, includeCode, progress.newChild(1), outputFolder, spdRootPath, impl);
					implWork -= (PRF_WORK + ((includeCode) ? IMPL_WORK : 0)); // SUPPRESS CHECKSTYLE INLINE
					progress.setWorkRemaining(implWork);
				}
			}
		}
	}

	private static void exportImpl(final IScaExporter exporter, final boolean includeCode, final SubMonitor progress, IPath outputFolder,
		final IPath spdRootPath, final Implementation impl) throws CoreException, IOException {
		progress.beginTask("Exporting Implementation " + impl.getId(), 1);
		final String localFileName = impl.getCode().getLocalFile().getName();
		final IPath codeLocalFile;
		if (localFileName != null) {
			codeLocalFile = new Path(localFileName);
		} else {
			codeLocalFile = null;
		}
		if (codeLocalFile != null && codeLocalFile.isAbsolute()) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute 'localfile' paths"));
		}
		final IResource srcPath = ExportUtils.getWorkspaceResource(spdRootPath.append(codeLocalFile));

		IPath outputPath;
		if (includeCode) {

			// Check if the path exists, there may be multiple implementations in this, only one needs to be built
			if ((srcPath != null) && srcPath.exists()) {
				outputPath = outputFolder.append(codeLocalFile);
				exporter.write(srcPath, outputPath, progress.newChild(1));
			}
		}

		if (impl.getPropertyFile() != null) {
			final IPath prfPath = new Path(impl.getPropertyFile().getLocalFile().getName());
			if (prfPath.isAbsolute()) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path property paths"));
			}
			final IResource srcPrfPath = ExportUtils.getWorkspaceResource(spdRootPath.append(prfPath));
			if (srcPrfPath == null) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + prfPath + " does not exist"));
			}
			outputPath = outputFolder.append(prfPath);
			exporter.write(srcPrfPath, outputPath, progress.newChild(1));
		}
	}

	private static boolean isBuildSH(IProject project) {
//		return project.getFile(new Path("build.sh")).exists();
		// TODO Disable the behavior until build.sh is fixed
		return false;
	}

	private static void buildSH(final IProgressMonitor progress, IProject project) throws CoreException, IOException, DebugException {
		String configTypeId = IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE;
		final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		final String launchConfigName = launchManager.generateLaunchConfigurationName("Build Install " + project.getName());
		final ILaunchConfigurationType configType = launchManager.getLaunchConfigurationType(configTypeId);
		final ILaunchConfigurationWorkingCopy retVal = configType.newInstance(null, launchConfigName);

		retVal.setAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true);
		retVal.setAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, ExportUtils.getEnv());
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		retVal.setAttribute(IExternalToolConstants.ATTR_BUILDER_SCOPE, "${none}");
		retVal.setAttribute(IExternalToolConstants.ATTR_SHOW_CONSOLE, true);

		URL fileUrl = FileLocator.toFileURL(FileLocator.find(SdrUiPlugin.getDefault().getBundle(), new Path("install.sh"), null));
		try {
			File file = new File(fileUrl.toURI());
			if (!file.canExecute()) {
				file.setExecutable(true);
			}
			retVal.setAttribute(IExternalToolConstants.ATTR_LOCATION, file.getAbsolutePath());
		} catch (URISyntaxException e1) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Failed to find install script.", e1));
		}
		retVal.setAttribute(IExternalToolConstants.ATTR_WORKING_DIRECTORY, project.getLocation().toOSString());

		ILaunch launch = retVal.launch("run", progress, false);
		while (!launch.isTerminated()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// PASS
			}
			if (progress.isCanceled()) {
				launch.terminate();
				break;
			}
		}
		if (launch.getProcesses()[0].getExitValue() != 0) {
			throw new CoreException(new Status(IStatus.ERROR, SdrUiPlugin.PLUGIN_ID, "Install script returned with error code "
				+ launch.getProcesses()[0].getExitValue() + "\n\nSee console output for details.", null));
		}
	}

	/**
	 * Checks the implementations within the soft pkg provided to see if any implementations have not been generated.
	 * If an implementation has not been generated, or no implementations exist at all, the user is asked whether or not
	 * to continue. True is returned if the user wishes to continue or all impelmenations were found, false is returned
	 * if the user has chosen to cancel the export.
	 * 
	 * @param softPkg The Soft Pkg for this project
	 * @param spdRootPath The full path to the SPD resource. May be found by:
	 * spdResource.getFullPath().removeLastSegments(1)
	 * @return False if the user has chosen to stop the export, True otherwise.
	 * @throws CoreException
	 */
	private static boolean checkProjectImplsForExport(SoftPkg softPkg, IPath spdRootPath) throws CoreException {

		final String projectName = ModelUtil.getProject(softPkg).getProject().getName();
		if (softPkg.getImplementation() != null && softPkg.getImplementation().size() != 0) {
			for (final Implementation impl : softPkg.getImplementation()) {
				final String localFileName = impl.getCode().getLocalFile().getName();
				final IPath codeLocalFile;
				if (localFileName != null) {
					codeLocalFile = new Path(localFileName);
				} else {
					codeLocalFile = null;
				}
				if (codeLocalFile != null && codeLocalFile.isAbsolute()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
				}

				final IResource srcPath = ExportUtils.getWorkspaceResource(spdRootPath.append(codeLocalFile));
				// Check if the path exists, there may be multiple implementations in this, only one needs to be built
				if ((srcPath == null) || !srcPath.exists()) {
					final String inProjStr = "in project '" + projectName + "' ";

					final boolean[] shouldExport = { false };
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							shouldExport[0] = MessageDialog.openQuestion(null, "File does not exist", "The file '" + localFileName + "' " + inProjStr
								+ "does not exist, export implementation anyway?");
						}
					});
					if (!shouldExport[0]) {
						SdrUiPlugin.getDefault().logError("Expected file '" + codeLocalFile + "' " + inProjStr + "does not exist, not exporting implementation");
						return false;
					}
				}
			}
		} else {
			final boolean[] shouldExport = { false };
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					shouldExport[0] = MessageDialog.openQuestion(null, "No Implementations Found", "No Implementations were found in " + projectName
						+ ", export anyway?");
				}
			});
			if (!shouldExport[0]) {
				SdrUiPlugin.getDefault().logError("No implementations found in " + projectName + ", not exporting project");
				return false;
			}
		}

		return true;
	}


	private static IResource getWorkspaceResource(final IPath path) {
		return ResourcesPlugin.getWorkspace().getRoot().findMember(path);
	}

	/**
	 * This checks a project for errors and prompts the user to continue if any are found.
	 * 
	 * @param proj the project to check
	 * @return true if there are no errors or the user still wants to export
	 * @throws CoreException
	 */
	private static boolean checkProject(final IProject proj) throws CoreException {
		final int maxSeverity = proj.findMaxProblemSeverity(IMarker.PROBLEM, true, 2);
		boolean retVal = true;

		if (maxSeverity == IMarker.SEVERITY_ERROR) {
			final boolean[] continueExport = { true };
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					continueExport[0] = MessageDialog.openQuestion(null, "Project has errors", "Project '" + proj.getName() + "' has errors. Export anyway?");
				}
			});
			if (!continueExport[0]) {
				retVal = false;
			}
		}

		return retVal;
	}

}
