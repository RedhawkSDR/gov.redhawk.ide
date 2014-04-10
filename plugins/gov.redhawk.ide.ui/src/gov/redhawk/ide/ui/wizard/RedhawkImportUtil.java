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

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @since 9.1
 */
public abstract class RedhawkImportUtil {
	private static String sadExtension = ".+\\.sad.xml";
	private static String spdExtension = ".+\\.spd.xml";
	private static String dcdExtension = ".+\\.dcd.xml";

	protected abstract int findMissingFiles();

	protected abstract IProject createDotProjectFile(String projectType);

	protected abstract WaveDevSettings createWaveDevFile() throws CoreException;

	public abstract SoftPkg getSoftPkg(String path);

	public static String getSadExtension() {
		return sadExtension;
	}

	public static String getSpdExtension() {
		return spdExtension;
	}

	public static String getDcdExtension() {
		return dcdExtension;
	}

	public void generateFiles(IProgressMonitor progressMonitor, String projectLocation, IProject importProject, WaveDevSettings importWaveDev)
		throws CoreException {
		final SubMonitor monitor = SubMonitor.convert(progressMonitor, 2);
		final IProject project = importProject;
		final SoftPkg softPkg = getSoftPkg(project);
		if (softPkg == null) {
			throw new IllegalStateException("Could not load spd.xml for project");
		}

		// Create or open the existing settings
		final WaveDevSettings waveDev = importWaveDev;
		if (waveDev == null) {
			throw new IllegalStateException("Could not load wavedev settings for project");
		}

		final EMap<String, ImplementationSettings> implSet = waveDev.getImplSettings();

		// Try generate each implementation, or just the specified language
		for (final Implementation impl : softPkg.getImplementation()) {
			final String currLang = impl.getProgrammingLanguage().getName();

			// Prepare for generation
			final ImplementationSettings settings = implSet.get(impl.getId());
			final ArrayList<FileToCRCMap> crcMap = new ArrayList<FileToCRCMap>();

			// Validate the settings name
			final String implName = impl.getId();
			if (!implName.equals(CodegenUtil.getValidName(implName))) {
				System.err.println("Invalid characters in implementation name for " + implName); // SUPPRESS CHECKSTYLE INLINE
				continue;
			} else if (settings.getGeneratorId() != null) {
				// Find the desired code generator
				String codegenId = settings.getGeneratorId();
				final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(codegenId);
				if (codeGenDesc == null) {
					System.err.println("The code generator(" + codegenId + ") for this implementation could not be found."); // SUPPRESS CHECKSTYLE INLINE
					continue;
				}
				// Get the actual code generator
				final IScaComponentCodegen generator = codeGenDesc.getGenerator();
				// Get files to generate
				final Set<FileStatus> fileStatusSet = generator.getGeneratedFilesStatus(settings, softPkg);
				final Set<String> fileList = new HashSet<String>();
				for (FileStatus s : fileStatusSet) {
					fileList.add(s.getFilename());
				}

				// Create the files, shouldGenerate parameter should be false
				final IStatus status = generator.generate(settings, impl, System.out, System.err, monitor.newChild(1), fileList.toArray(new String[0]), false,
					crcMap);
				// Save the workspace
				final WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

					@Override
					protected void execute(final IProgressMonitor monitor) throws CoreException, InvocationTargetException, InterruptedException {
						final IStatus saveStatus = ResourcesPlugin.getWorkspace().save(true, monitor);
						// Check the save results, hopefully this worked
						if (!saveStatus.isOK()) {
							System.err.println("Generated files, but there was a problem saving the workspace: " + saveStatus.getMessage()); //SUPPRESS CHECKSTYLE INLINE
						}
					}
				};
				try {
					operation.run(monitor.newChild(1));
				} catch (final InvocationTargetException e) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Error saving resources", e));
				} catch (final InterruptedException e) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Error saving resources", e));
				}

				// Check the results
				if (!status.isOK()) {
					throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "Errors occurred generating " + currLang + " code: " + status.getMessage()));
				} else {
					System.out.println("Done generating " + currLang + " code"); // SUPPRESS CHECKSTYLE INLINE
				}
			} else {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkIDEUiPlugin.PLUGIN_ID, "No generator specified for implementation: " + implName + ". No code generated."));
			}
		}
		project.build(IncrementalProjectBuilder.FULL_BUILD, monitor.newChild(1));
	}

	private SoftPkg getSoftPkg(final IProject project) {
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final IFile softPkgFile = project.getFile(project.getName() + ".spd.xml");
		final Resource resource = set.getResource(URI.createFileURI(softPkgFile.getLocation().toString()), true);
		return SoftPkg.Util.getSoftPkg(resource);
	}
}
