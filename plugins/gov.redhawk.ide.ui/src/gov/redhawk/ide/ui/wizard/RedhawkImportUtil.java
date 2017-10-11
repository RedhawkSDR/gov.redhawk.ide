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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

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

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.ide.ui.wizard.RedhawkImportWizardPage1.ProjectRecord;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

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
				final IStatus status = generator.generate(settings, impl, System.out, System.err, monitor.newChild(1), fileList.toArray(new String[0]), false, crcMap);
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

	/**
	 * @since 10.0
	 */
	protected WaveDevSettings createWaveDevFile(ProjectRecord projectRecord, String projectName, SoftPkg softPkg) throws CoreException {
		WaveDevSettings waveDev = CodegenFactory.eINSTANCE.createWaveDevSettings();
		ComponentType componentType = ComponentType.OTHER;
		if (softPkg.getDescriptor() != null) {
			componentType = SoftwareComponent.Util.getWellKnownComponentType(softPkg.getDescriptor().getComponent());
		}

		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			final String lang = impl.getProgrammingLanguage().getName();

			// Make a best guess at the code generator
			ICodeGeneratorDescriptor codeGenDesc = null;
			final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
			if (codeGens.length > 0) {
				codeGenDesc = codeGens[0];
			}
			switch (componentType) {
			case RESOURCE:
			case DEVICE:
			case SERVICE:
				if (lang != null) {
					switch (lang.toLowerCase()) {
					case "c++":
						codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
							"gov.redhawk.ide.codegen.jinja.cplusplus.CplusplusGenerator");
						break;
					case "java":
						codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen("gov.redhawk.ide.codegen.jinja.java.JavaGenerator");
						break;
					case "python":
						codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen("gov.redhawk.ide.codegen.jinja.python.PythonGenerator");
						break;
					default:
						break;
					}
				}
				break;
			case OTHER:
				if (CodeFileType.SHARED_LIBRARY.equals(impl.getCode().getType()) && lang != null) {
					switch (lang.toLowerCase()) {
					case "c++":
						codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(
							"gov.redhawk.ide.codegen.jinja.cplusplus.CplusplusSharedLibraryGenerator");
						break;
					default:
						break;
					}
				}
				break;
			default:
				break;
			}

			final ImplementationSettings settings = CodegenFactory.eINSTANCE.createImplementationSettings();
			if (codeGenDesc != null) {
				final IScaComponentCodegen generator = codeGenDesc.getGenerator();

				// Use the localfile to determine the output directory
				String outputDir = impl.getCode().getLocalFile().getName();
				if (outputDir.indexOf('/') != -1) {
					outputDir = outputDir.substring(0, outputDir.lastIndexOf('/'));
				}

				// Set the generator, settings name and output directory
				settings.setGeneratorId(generator.getClass().getCanonicalName());
				settings.setOutputDir(outputDir);

				// Find a template using the template registry and the componentType
				ITemplateDesc templateDesc = null;
				
				if (CodeFileType.SHARED_LIBRARY.equals(impl.getCode().getType()) && lang != null) {
					// Handle shared libraries as a special case
					switch (lang.toLowerCase()) {
					case "c++":
						templateDesc = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate("redhawk.codegen.jinja.cpp.library");
						break;
					default:
						break;
					}
				} else {
					// Handle all other project types
					final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(
						settings.getGeneratorId(), componentType.getLiteral());
					for (final ITemplateDesc itd : templates) {
						if (itd.isSelectable() && !itd.notDefaultableGenerator()) {
							templateDesc = itd;
							break;
						}
					}
				}
				
				// If we still don't have a template, ask the import project wizard for help
				if (templateDesc == null) {
					for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
						if (assistant.handlesLanguage(lang)) {
							String templateId = assistant.getDefaultTemplate();
							templateDesc = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(templateId);
							break;
						}
					}
				}
				
				// If we found the template, use it
				if (templateDesc != null) {
					// Set the properties to their default values
					for (final IPropertyDescriptor prop : templateDesc.getPropertyDescriptors()) {
						final Property p = CodegenFactory.eINSTANCE.createProperty();
						p.setId(prop.getKey());
						p.setValue(prop.getDefaultValue());
						settings.getProperties().add(p);
					}
					settings.setTemplate(templateDesc.getId());
				}
			}

			for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
				if (assistant.handlesLanguage(lang)) {
					assistant.setupWaveDev(projectName, settings);
					break;
				}
			}
			waveDev.getImplSettings().put(impl.getId(), settings);
		}

		// Create the URI to the .wavedev file
		final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createPlatformResourceURI(softPkg.getName() + "/." + softPkg.getName()
			+ ".wavedev", false);
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final Resource res = set.createResource(uri);

		// Add the WaveDevSettings to the resource and save to disk to persist the newly created WaveDevSettings
		res.getContents().add(waveDev);
		try {
			res.save(null);
		} catch (final IOException e) {
			RedhawkIDEUiPlugin.logError(e.getMessage(), e);
		}

		return waveDev;
	}
}
