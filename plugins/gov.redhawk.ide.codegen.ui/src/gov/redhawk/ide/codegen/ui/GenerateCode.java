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
package gov.redhawk.ide.codegen.ui;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.internal.GenerateFilesDialog;
import gov.redhawk.ide.codegen.ui.internal.GeneratorConsole;
import gov.redhawk.ide.codegen.util.PropertyUtil;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.UIJob;

/**
 * @since 7.0
 */
public class GenerateCode {

	private String[] filesToBeGenerated;
	private boolean generationConfirmed;
	private TransactionalEditingDomain domain;

	/**
	 * Accepts an {@link EList}<{@link Implementation}>, an {@link Implementation}, or an {@link IFile} for an SPD. Performs
	 * the appropriate code generation actions for the associated implementation(s). Code generation is performed as a
	 * job.
	 * 
	 * @param selectedObj The object to perform code generation actions for.
	 */
	@SuppressWarnings("unchecked")
	public void generate(final Object selectedObj) {
		if (selectedObj instanceof EList) {
			processImpls((List<Implementation>) selectedObj);
		} else if (selectedObj instanceof Implementation) {
			final List<Implementation> impls = new ArrayList<Implementation>();
			impls.add((Implementation) selectedObj);
			processImpls(impls);
		} else if (selectedObj instanceof IFile) {
			// The selected object should be an IFile for the SPD
			final IFile file = (IFile) selectedObj;
			final URI spdURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
			final SoftPkg softpkg = ModelUtil.loadSoftPkg(spdURI);
			processImpls(softpkg.getImplementation());
		}
	}

	/**
	 * This generates a specific implementation with an optional progress monitor.
	 * 
	 * @param impl the implementation to generate
	 * @param monitor the optional {@link IProgressMonitor} for progress
	 * @throws CoreException for any problems generating code
	 */
	public IStatus generateImpl(final Implementation impl, final IProgressMonitor monitor) throws CoreException {
		try {
			return processImpls(Collections.singletonList(impl), monitor, false);
		} finally {
			monitor.done();
		}
	}

	/**
	 * Performs code generation actions for one or more implementations from the same SPD as a background
	 * {@link WorkspaceJob}.
	 * 
	 * @param impls The implementation(s) to generate code for.
	 */
	private void processImpls(final List<Implementation> impls) {
		if (impls.size() == 0) {
			return;
		}

		final WorkspaceJob job = new WorkspaceJob("Generate Component(s)") {

			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
				return processImpls(impls, monitor, true);
			}

		};
		job.setPriority(Job.LONG);
		job.setUser(true);
		job.setSystem(false);
		job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
		job.schedule();
	}

	private IStatus processImpls(final List<Implementation> impls, IProgressMonitor monitor, final boolean openEditor) throws CoreException {
		try {
			final int GENERATE_CODE_WORK = 9;
			final int ADD_BUILDER_WORK = 1;
			final int REFRESH_WORKSPACE_WORK = 1;

			if (monitor == null) {
				monitor = new NullProgressMonitor();
			}

			final SubMonitor progress = SubMonitor.convert(monitor, "Generating component code", GENERATE_CODE_WORK + ADD_BUILDER_WORK + ADD_BUILDER_WORK
			        + REFRESH_WORKSPACE_WORK);

			final SoftPkg softPkg = (SoftPkg) impls.get(0).eContainer();
			final IProject project = ModelUtil.getProject(softPkg);
			final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);

			// Initial validation
			final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Problems while generating code", null);
			IStatus status = validate(project, softPkg, impls, waveDev);
			if (!status.isOK()) {
				retStatus.add(status);
				if (retStatus.getSeverity() == IStatus.ERROR) {
					return retStatus;
				}
			}
			if (progress.isCanceled()) {
				return retStatus;
			}

			// Refresh project before generating code
			progress.setTaskName("Refreshing project");
			project.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(REFRESH_WORKSPACE_WORK));

			// Generate code for each implementation
			final EMap<String, ImplementationSettings> implSet = waveDev.getImplSettings();
			final SubMonitor implProgress = progress.newChild(GENERATE_CODE_WORK).setWorkRemaining(impls.size());
			for (final Implementation impl : impls) {
				// Generate code for implementation
				final ImplementationSettings settings = implSet.get(impl.getId());
				final ArrayList<FileToCRCMap> mapping = new ArrayList<FileToCRCMap>();
				status = generateImplementation(impl, settings, implProgress.newChild(1), softPkg, mapping, openEditor);
				if (!status.isOK()) {
					retStatus.add(status);
					if (status.getSeverity() == IStatus.ERROR) {
						return retStatus;
					}
				}

				// Update CRCs for implementation
				implProgress.subTask("Updating CRCs");
				try {
					updateCRCs(settings, mapping);
				} catch (final IOException e) {
					retStatus.add(new Status(IStatus.WARNING, RedhawkCodegenActivator.PLUGIN_ID, "Problem while generating CRCs for implementation "
					        + settings.getName(), e));
				}

				if (progress.isCanceled()) {
					break;
				}
			}

			// Save the implementation settings if there were any implementations we generated for (saves the CRC modifications)
			progress.setTaskName("Save wavedev settings");
			if (impls.size() > 0) {
				try {
					saveResource(waveDev);
				} catch (final CoreException ex) {
					retStatus.add(new Status(ex.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save CRCs", ex));
				}
			}

			if (progress.isCanceled()) {
				return retStatus;
			}

			// Add general builders
			progress.setTaskName("Adding builders");
			CodegenUtil.addTopLevelBuildScriptBuilder(project, progress.newChild(ADD_BUILDER_WORK));

			// Refresh project after generating code
			progress.setTaskName("Refreshing project");
			project.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(REFRESH_WORKSPACE_WORK));

			if (progress.isCanceled()) {
				return retStatus;
			}

			if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
				// Schedule a new job which will run a full build; this should ensure all resource change
				// notifications are dispatched before beginning the build
				final WorkspaceJob buildJob = new WorkspaceJob("Build generated code") {
					@Override
					public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
						project.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
						return new Status(IStatus.OK, RedhawkCodegenUiActivator.PLUGIN_ID, "");
					}
				};
				buildJob.setPriority(Job.LONG);
				buildJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
				buildJob.setSystem(false);
				buildJob.setUser(true);
				buildJob.schedule();
			}

			return retStatus;
		} finally {
			monitor.done();
		}

	}

	/**
	 * Generates the code for an {@link Implementation}.
	 * 
	 * @param impl The {@link Implementation} to generate code for.
	 * @param settings The implementation settings
	 * @param monitor The progress monitor to use for reporting progress to the
	 *            user. It is the caller's responsibility to call done() on the
	 *            given monitor. Accepts null, indicating that no progress
	 *            should be reported and that the operation cannot be canceled.
	 * @param softpkg The parent SPD of the {@link Implementation}
	 * @param crcMap A {@link List} that will be populated with the CRCs of all generated files
	 * @param openEditor Indicates whether or not to open the editor after generating
	 * @return The status of any problems encountered while generating the implementation
	 */
	protected IStatus generateImplementation(final Implementation impl, final ImplementationSettings settings, final IProgressMonitor monitor,
	        final SoftPkg softpkg, final List<FileToCRCMap> crcMap, boolean openEditor) {
		if (settings == null) {
			return new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find settings (wavedev) for " + impl.getId()
			        + ", skipping generation");
		}

		final String implId = impl.getId();
		final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Problems while generating implementation "
		        + implId, null);
		final SubMonitor progress = SubMonitor.convert(monitor, 1);
		progress.setTaskName("Generating implementation " + implId);

		if (settings.getGeneratorId() != null) {
			final String codegenId = settings.getGeneratorId();
			final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(codegenId);
			if (codeGenDesc == null) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID,
				        "The code generator specified in the settings (wavedev) could not be found. Check your generator selection for the implementation"));
				return retStatus;
			}

			// Find the code generator console, or create it if necessary
			GeneratorConsole genConsole = null;
			final IConsole[] consoles = ConsolePlugin.getDefault().getConsoleManager().getConsoles();
			for (final IConsole console : consoles) {
				if (console instanceof GeneratorConsole && console.getType().equals(codeGenDesc.getId())) {
					genConsole = (GeneratorConsole) console;
					break;
				}
			}
			if (genConsole == null) {
				genConsole = new GeneratorConsole(codeGenDesc);
				ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { genConsole });
			}

			try {
				// Get the specific code generator
				final IScaComponentCodegen generator = codeGenDesc.getGenerator();

				// Validate that we can perform code generation
				IStatus status = generator.validate();
				if (!status.isOK()) {
					retStatus.add(status);
					if (status.getSeverity() == IStatus.ERROR) {
						return retStatus;
					}
				}

				// Confirm files to generate
				final String[] files = this.verifyGeneration(generator, settings, softpkg);

				if (files != null) {
					final IFile mainFile = generator.getDefaultFile(impl, settings);
					openEditor = openEditor && (mainFile == null || !mainFile.exists());

					status = generator.generate(settings, impl, genConsole.getOutStream(), genConsole.getErrStream(), progress.newChild(1), files,
					        generator.shouldGenerate(), crcMap);
					if (!status.isOK()) {
						retStatus.add(status);
						if (status.getSeverity() == IStatus.ERROR) {
							return retStatus;
						}
					}

					// Update last generated date
					PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						public void run() {
							final WaveDevSettings wavedev = CodegenUtil.loadWaveDevSettings(softpkg);
							PropertyUtil.setLastGenerated(wavedev, settings, new Date(System.currentTimeMillis()));
						}
					});

					if (openEditor && (mainFile != null) && mainFile.exists()) {
						progress.subTask("Opening editor for main file");

						// Open the selected editor
						final UIJob openJob = new UIJob("Open editor") {
							@Override
							public IStatus runInUIThread(final IProgressMonitor monitor) {
								try {
									IDE.openEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), mainFile, true);
								} catch (final PartInitException p) {
									return new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to open main file for editing.");
								}
								return new Status(IStatus.OK, RedhawkCodegenUiActivator.PLUGIN_ID, "");
							}
						};
						openJob.setPriority(Job.SHORT);
						openJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
						openJob.setSystem(false);
						openJob.setUser(true);
						openJob.schedule();
					}

					if (retStatus.isOK()) {
						return new Status(IStatus.OK, RedhawkCodegenActivator.PLUGIN_ID, "Succeeded generating code for implementation");
					}
				} else {
					retStatus.add(new Status(IStatus.CANCEL, RedhawkCodegenActivator.PLUGIN_ID, "User cancelled code generation"));
				}
				 
			} catch (final CoreException e) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unexpected error", e));
			}
		} else {
			retStatus.add(new Status(IStatus.WARNING, RedhawkCodegenActivator.PLUGIN_ID,
			                "No code generator is specified in the settings (wavedev). Code generation was skipped for the implementation. Check your generator selection for the implementation."));
		}

		return retStatus;
	}

	/**
	 * Figures out which files will be generated, possibly prompting the user to
	 * confirm generation if files have been modified since they were originally
	 * generated.
	 * 
	 * @param generator The code generator to use
	 * @param implSettings The settings for the implementation
	 * @param softpkg The SPD
	 * @return An array of the files which are to be generated
	 * @throws CoreException A problem occurs while determining which files to generate
	 */
	protected String[] verifyGeneration(final IScaComponentCodegen generator, final ImplementationSettings implSettings, final SoftPkg softpkg)
	        throws CoreException {
		this.filesToBeGenerated = new String[0];

		final IResource res = ModelUtil.getResource(softpkg);
		final IProject project = res.getProject();

		final HashMap<String, Boolean> filesMap = generator.getGeneratedFiles(implSettings, softpkg);
		this.setFileDefaults(filesMap, implSettings);

		this.generationConfirmed = (filesMap.size() == 0);

		final WaveDevSettings wavedev = CodegenUtil.loadWaveDevSettings(softpkg);
		try {
			boolean fileExists = false;
			if (PropertyUtil.getLastGenerated(wavedev, implSettings) == null) {
				for (final String file : generator.getGeneratedFiles(implSettings, softpkg).keySet()) {
					final String tempPath = implSettings.getOutputDir() + File.separator + file;
					if (project.getFile(tempPath).exists()) {
						this.generationConfirmed = false;
						fileExists = true;
						break;
					}
				}

				if (!fileExists) {
					GenerateCode.this.filesToBeGenerated = generator.getGeneratedFiles(implSettings, softpkg).keySet().toArray(new String[0]);
					this.generationConfirmed = true;
				}
			}
		} catch (final CoreException e1) {
			RedhawkCodegenUiActivator.getDefault().getLog()
			        .log(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to get the last generated date."));
		}

		if (generator.shouldGenerate()) {
			while (!this.generationConfirmed) {
				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {
						String name = "";
						if (implSettings.getName() != null && !"".equals(implSettings.getName())) {
							name = implSettings.getName();
						} else {
							name = implSettings.getId();
						}

						final GenerateFilesDialog dialog = new GenerateFilesDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), filesMap, name);

						final int result = dialog.open();
						GenerateCode.this.generationConfirmed = true;

						if (result == Window.OK) {
							GenerateCode.this.filesToBeGenerated = dialog.getFilesToGenerate();

						} else {
							GenerateCode.this.filesToBeGenerated = null;
						}
					}

				});
			}
		}

		return this.filesToBeGenerated;
	}

	/**
	 * For each file which does not exist in the implementation's output
	 * directory, the generate flag (boolean value in the map) is changed to
	 * true.
	 * 
	 * @param filesMap A mapping of file names to a boolean indicating if the
	 *            file will be generated
	 * @param implSettings The implementation settings
	 */
	private void setFileDefaults(final HashMap<String, Boolean> filesMap, final ImplementationSettings implSettings) {
		final IProject project = ModelUtil.getResource(implSettings).getProject();

		for (final String fileName : filesMap.keySet()) {
			if (!project.getFile(implSettings.getOutputDir() + File.separator + fileName).exists()) {
				filesMap.put(fileName, true);
			}
		}
	}

	/**
	 * Performs several validations to detect problems prior to code generation.
	 * 
	 * @param project The project to validate
	 * @param softPkg The {@link SoftPkg} to validate
	 * @param impls The {@link Implementation}s to be generated
	 * @param waveDev The wavedev to validate
	 * @return An {@link IStatus} indicating any issues found; problems should be of severity {@link IStatus#ERROR} to
	 *   prevent code generation
	 */
	private IStatus validate(final IProject project, final SoftPkg softPkg, final List<Implementation> impls, final WaveDevSettings waveDev) {
		final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Validation problems prior to generating code", null);

		if (project == null) {
			return new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Project does not exist");
		}

		// Check SCA XML files
		try {
			// Check SPD file
			final String spdFileName = ModelUtil.getSpdFileName(softPkg);
			if (spdFileName == null) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to determine SPD filename"));
			} else {
				final IFile file = project.getFile(spdFileName);
				if (!file.exists()) {
					retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Cannot locate SPD file"));
				} else {
					for (final IMarker mark : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
						if (mark.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) {
							retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "The SPD file contains errors"));
							break;
						}
					}
				}
			}

			// Check SCD file
			final String scdFileName = ModelUtil.getScdFileName(softPkg);
			if (scdFileName == null) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to determine SCD filename"));
			} else {
				final IFile file = project.getFile(scdFileName);
				if (!file.exists()) {
					retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Cannot locate SCD file"));
				} else {
					for (final IMarker mark : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
						if (mark.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) {
							retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "The SCD file contains errors"));
							break;
						}
					}
				}
			}

			// Check PRF file
			final String prfFileName = ModelUtil.getPrfFileName(softPkg.getPropertyFile());
			if (prfFileName != null) {
				final IFile file = project.getFile(prfFileName);
				if (!file.exists()) {
					retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Cannot locate PRF file"));
				} else {
					for (final IMarker mark : file.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE)) {
						if (mark.getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) {
							retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "The PRF file contains errors"));
							break;
						}
					}
				}
			}
		} catch (final CoreException e) {
			retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "A problem occurred while verifying the SCA XML files", e));
		}

		// Wavedev checks
		if (waveDev == null) {
			retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID,
			        "Unable to find project settings (wavedev) file. Cannot generate code."));
		} else {
			for (final Implementation impl : impls) {
				if (!waveDev.getImplSettings().containsKey(impl.getId())) {
					retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find settings in wavedev file for implementation "
					        + impl.getId()));
				}
			}
		}

		return retStatus;
	}

	private void updateCRCs(final ImplementationSettings implSettings, final List<FileToCRCMap> crcMap) throws IOException {
		this.domain = TransactionUtil.getEditingDomain(implSettings);

		if (this.domain == null) {
			this.domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
			this.domain.setID("gov.redhawk.spd.editingDomainId");
		}

		final Map<FileToCRCMap, FileToCRCMap> foundCRCs = new HashMap<FileToCRCMap, FileToCRCMap>();
		for (final FileToCRCMap entry : implSettings.getGeneratedFileCRCs()) {
			for (final FileToCRCMap currCRC : crcMap) {
				if (entry.getFile().equals(currCRC.getFile())) {
					foundCRCs.put(entry, currCRC);
					crcMap.remove(currCRC);
					break;
				}
			}
		}

		final CompoundCommand updateCommand = new CompoundCommand();
		for (final FileToCRCMap crc : crcMap) {
			final AddCommand cmd = new AddCommand(this.domain, implSettings.getGeneratedFileCRCs(), crc);
			updateCommand.append(cmd);
		}

		for (final Entry<FileToCRCMap, FileToCRCMap> crcEntry : foundCRCs.entrySet()) {
			final SetCommand cmd = new SetCommand(this.domain, crcEntry.getKey(), CodegenPackage.Literals.FILE_TO_CRC_MAP__CRC, crcEntry.getValue().getCrc());
			updateCommand.append(cmd);
		}

		PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
			public void run() {
				if (GenerateCode.this.domain != null) {
					GenerateCode.this.domain.getCommandStack().execute(updateCommand);
				}
			}
		});
	}

	/**
	 * Saves the {@link WaveDevSettings} to disk.
	 * 
	 * @param waveDevSettings The settings to save
	 * @throws CoreException A problem occurs while writing the settings to disk
	 */
	private void saveResource(final WaveDevSettings waveDevSettings) throws CoreException {
		try {
			waveDevSettings.eResource().save(null);

			PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
				public void run() {
					if (GenerateCode.this.domain != null) {
						GenerateCode.this.domain.getCommandStack().flush();
						((BasicCommandStack) GenerateCode.this.domain.getCommandStack()).saveIsDone();
					}
				}
			});

		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save the updated implementation settings", e));
		}
	}
}
