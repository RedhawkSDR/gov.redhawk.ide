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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
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
	 *            to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 *            that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportWaveform(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaWaveformProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
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
	 *            to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 *            that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportComponent(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaComponentProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
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
	 *            to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 *            that the operation cannot be canceled.
	 * @since 2.0
	 */
	public static void exportNode(final IProject proj, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
		if (!proj.hasNature(ScaNodeProjectNature.ID)) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required nature"));
		}

		if (!ExportUtils.checkProject(proj)) {
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
	 * Shared by {@link #exportDevice(IProject, IScaExporter, IProgressMonitor)},
	 * {@link #exportComponent(IProject, IScaExporter, IProgressMonitor)}, and
	 * {@link #exportNode(IProject, IScaExporter, IProgressMonitor)} to perform common export functions.
	 * 
	 * @param proj The project to search for software packages
	 * @param exporter The IScaExporter to use
	 * @param includeCode If code should be copied with the component
	 * @param monitor The progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *            to call done() on the given monitor. Accepts null, indicating that no progress should be reported and
	 *            that the operation cannot be canceled.
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
				 if (!checkProjectImplsForExport(softPkg, spdRootPath)) {
					 return;
				 }
			}
			
			
			outputFolder = outputFolder.append(proj.getName());
			exporter.mkdir(outputFolder, progress.newChild(MKDIR_WORK));
			
			// If we were told to include code make sure at least one implementation has generated code.
			if (includeCode) {
				 if (!checkProjectImplsForExport(softPkg, spdRootPath)) {
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
				int implWork = softPkg.getImplementation().size() * (PRF_WORK + ((includeCode) ? IMPL_WORK : 0)); // SUPPRESS CHECKSTYLE INLINE
				progress.setWorkRemaining(implWork);
				for (final Implementation impl : softPkg.getImplementation()) {
					if (includeCode) {
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
						if ((srcPath != null) && srcPath.exists()) {
							outputPath = outputFolder.append(codeLocalFile);
							exporter.write(srcPath, outputPath, progress.newChild(IMPL_WORK));
						}
					}
					
					if (impl.getPropertyFile() != null) {
						final IPath prfPath = new Path(impl.getPropertyFile().getLocalFile().getName());
						if (prfPath.isAbsolute()) {
							throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Cannot export absolute path localfile paths"));
						}
						final IResource srcPrfPath = ExportUtils.getWorkspaceResource(spdRootPath.append(prfPath));
						if (srcPrfPath == null) {
							throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Expected file " + prfPath + " does not exist"));
						}
						outputPath = outputFolder.append(prfPath);
						exporter.write(srcPrfPath, outputPath, progress.newChild(PRF_WORK));
					}
					implWork -= (PRF_WORK + ((includeCode) ? IMPL_WORK : 0)); // SUPPRESS CHECKSTYLE INLINE
					progress.setWorkRemaining(implWork);
				}
			}
		}
	}

	/**
	 * Checks the implementations within the soft pkg provided to see if any implementations have not been generated.
	 * If an implementation has not been generated, or no implementations exist at all, the user is asked whether or not 
	 * to continue.  True is returned if the user wishes to continue or all impelmenations were found, false is returned
	 * if the user has chosen to cancel the export.  
	 *  
	 * @param softPkg The Soft Pkg for this project
	 * @param spdRootPath The full path to the SPD resource.  May be found by: spdResource.getFullPath().removeLastSegments(1)
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
							shouldExport[0] = MessageDialog.openQuestion(null, "File does not exist", "The file '" 
						+ localFileName + "' " + inProjStr + "does not exist, export implementation anyway?");
						}
					});
					if (!shouldExport[0]) {
						SdrUiPlugin.getDefault().logError(
						        "Expected file '" + codeLocalFile + "' " + inProjStr + "does not exist, not exporting implementation");
						return false;
					}
				} 
			}
		} else {
			final boolean[] shouldExport = { false };
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					shouldExport[0] = MessageDialog.openQuestion(null, "No Implementations Found", 
							"No Implementations were found in " + projectName + ", export anyway?");
				}
			});
			if (!shouldExport[0]) {
				SdrUiPlugin.getDefault().logError(
				        "No implementations found in " + projectName + ", not exporting project");
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Exports IDL projects from the specified project using the provided exporter.
	 * 
	 * @param project The project to build and export interfaces from
	 * @param exporter The IScaExporter to use of type FileStoreExporter
	 * @param monitor The user supplied progress monitor
	 * @throws CoreException
	 * @throws IOException
	 */
	//  This has been commented out because IDL projects need to be "installed" into $OSSIEHOME.  They aren't exported into SDR.
	//  REDHAWK would need to change conventions on where IDL files are installed for this exporter to make sense
	// 
	//	private static void exportIdl(final IProject project, final IScaExporter exporter, final IProgressMonitor monitor) throws CoreException, IOException {
	//		if (!project.hasNature(IdlLibraryProjectNature.ID)) {
	//			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing required IDL project nature"));
	//		}
	//
	//		if (!(exporter instanceof FileStoreExporter)) {
	//			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "IDL Project can't be exported with the current exporter"));
	//		}
	//
	//		// Add custom filters to ignore .cpp, .lai and .o resources
	//		((FileStoreExporter) exporter).addExcludePattern(Pattern.compile(".*\\.cpp"));
	//		((FileStoreExporter) exporter).addExcludePattern(Pattern.compile(".*\\.lai"));
	//		((FileStoreExporter) exporter).addExcludePattern(Pattern.compile(".*\\.o"));
	//
	//		// Get the interface module name from the builder properties
	//		final String moduleName = IdlLibraryProjectNature.getBuilderProperty(project, IdlProjectBuilder.MODULE_NAME_ARG);
	//
	//		if (moduleName == "") {
	//			throw new CoreException(new Status(IStatus.ERROR, RedhawkIdeActivator.PLUGIN_ID, "Project missing interface name"));
	//		}
	//
	//		// Preconfigure our output paths
	//		final String redhawkSegment = IPath.SEPARATOR + "redhawk" + IPath.SEPARATOR;
	//		final SubMonitor progress = SubMonitor.convert(monitor, "Exporting Interface", project.members().length + 1);
	//
	//		final IPath exportLocation = exporter.getExportLocation();
	//		final IPath libPath = exportLocation.append(IPath.SEPARATOR + "lib" + IPath.SEPARATOR);
	//		final IPath pkgPath = libPath.append("pkgconfig" + IPath.SEPARATOR);
	//		final IPath includePath = exportLocation.append(IPath.SEPARATOR + "include" + IPath.SEPARATOR);
	//		final IPath idlPath = exportLocation.append(IPath.SEPARATOR + "share" + IPath.SEPARATOR + "idl" + redhawkSegment + moduleName.toUpperCase()
	//		        + IPath.SEPARATOR);
	//
	//		// Loop through all of our children and export them as needed
	//		int loopWorkRemaining = project.members().length;
	//		for (final IResource child : project.members()) {
	//			// Handle cases where our files have certain expected file extensions
	//			if (child.getFileExtension() != null) {
	//				if (child.getFileExtension().equals("idl")) {
	//					exporter.write(child, idlPath.append(child.getName()), progress.newChild(loopWorkRemaining));
	//				} else if (child.getFileExtension().equals("pc")) {
	//					exporter.write(child, pkgPath.append(child.getName()), progress.newChild(loopWorkRemaining));
	//				} else if (child.getFileExtension().equals("jar")) {
	//					exporter.write(child, libPath.append(child.getName()), progress.newChild(loopWorkRemaining));
	//				}
	//			}
	//
	//			// Handle exporting the source code folder (python and c++)
	//			if (child.getName().equals("src")) {
	//				final IFolder folder = (IFolder) child;
	//				final IFolder pythonFolder = folder.getFolder("python");
	//
	//				// If the python folder exists, export it all together since it's already formatted appropriately
	//				if (pythonFolder.exists()) {
	//					exporter.write(pythonFolder, libPath.append(pythonFolder.getName()), progress.newChild(loopWorkRemaining));
	//				}
	//
	//				final IFolder cppFolder = folder.getFolder("cpp");
	//
	//				// If the cpp folder exists, go ahead and copy over the folder since our ignore list will exclude the cpp files
	//				if (cppFolder.exists()) {
	//					for (final IResource cppChild : cppFolder.members()) {
	//						exporter.write(cppChild, includePath.append(cppChild.getName()), progress.newChild(loopWorkRemaining));
	//					}
	//				}
	//			}
	//
	//			// Handle the exporting of the library files
	//			if (child.getName().equals(".libs")) {
	//				final IFolder folder = (IFolder) child;
	//				String target = "";
	//				String source = "";
	//				String source0 = "";
	//
	//				for (final IResource libChild : folder.members()) {
	//					// Check to see if the file ends in a number
	//					if (libChild.getFileExtension().matches("[0-9]")) {
	//						// Keep track of the name of the library that follows the pattern: libexampleInterface.so.0.0.0
	//						if (libChild.getName().split("\\.").length > 4) {
	//							target = libChild.getName();
	//						} else {
	//							// Otherwise it's going to be the case: libexampleInterface.so.0
	//							source0 = libPath.append(libChild.getName()).toString();
	//							continue;
	//						}
	//					} else if (libChild.getFileExtension().equals("so")) {
	//						source = libPath.append(libChild.getName()).toString();
	//						continue;
	//					}
	//					// If the file is our target or it's doesn't meet the other criteria, go ahead and export it
	//					exporter.write(libChild, libPath.append(libChild.getName()), progress.newChild(loopWorkRemaining));
	//				}
	//				// Manually create symlinks for the two source libraries
	//				((FileStoreExporter) exporter).makeSymLink(target, source);
	//				((FileStoreExporter) exporter).makeSymLink(target, source0);
	//			}
	//			--loopWorkRemaining;
	//		}
	//	}

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
