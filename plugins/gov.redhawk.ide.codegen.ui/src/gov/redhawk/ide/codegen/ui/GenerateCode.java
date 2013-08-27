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
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.internal.GenerateFilesDialog;
import gov.redhawk.ide.codegen.ui.internal.GeneratorConsole;
import gov.redhawk.ide.codegen.util.PropertyUtil;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.NamedThreadFactory;

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
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @since 7.0
 */
public final class GenerateCode {

	private GenerateCode() {

	}

	private static final ExecutorService EXECUTOR_POOL = Executors.newSingleThreadExecutor(new NamedThreadFactory(GenerateCode.class.getName()));

	/**
	 * @since 8.0
	 */
	public static void generate(Shell parent, Object objectToGenerate) {
		if (objectToGenerate instanceof IFile) {
			generate(parent, (IFile) objectToGenerate);
		} else if (objectToGenerate instanceof SoftPkg) {
			generate(parent, ((SoftPkg) objectToGenerate).getImplementation());
		} else if (objectToGenerate instanceof Implementation) {
			generate(parent, (Implementation) objectToGenerate);
		} else if (objectToGenerate instanceof List< ? >) {
			generate(parent, (List<Implementation>) objectToGenerate);
		} else {
			throw new IllegalArgumentException("Unknown object to generate" + objectToGenerate);
		}

	}

	/**
	 * @since 8.0
	 */
	public static void generate(Shell parent, IFile spd) {
		final IFile file = (IFile) spd;
		final URI spdURI = URI.createPlatformResourceURI(file.getFullPath().toString(), false);
		final SoftPkg softpkg = ModelUtil.loadSoftPkg(spdURI);
		generate(parent, softpkg.getImplementation());
	}

	/**
	 * @since 8.0
	 */
	public static void generate(Shell parent, final Implementation impl) {
		generate(parent, Collections.singletonList(impl));
	}

	/**
	 * @since 8.0
	 */
	public static void generate(final Shell shell, final List<Implementation> impls) {
		if (impls.isEmpty()) {
			return;
		}
		Job getFilesJob = new Job("Getting files to generate...") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				// Map of Implementation ->  ( map of FileName relative to output location -> true will regenerate, false wants to regenerate but contents different
				final Map<Implementation, Map<String, Boolean>> implMap = new HashMap<Implementation, Map<String, Boolean>>();
				for (Implementation impl : impls) {
					try {
						Map<String, Boolean> resultSet = getFilesToGenerateMap(monitor, impl);
						implMap.put(impl, resultSet);
					} catch (CoreException e) {
						return e.getStatus();
					}
				}
				WorkbenchJob checkFilesJob = new WorkbenchJob("Check files") {

					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						Map<String, Boolean> aggregate = new HashMap<String, Boolean>();
						for (Map<String, Boolean> v : implMap.values()) {
							aggregate.putAll(v);
						}
						List<String> filesToGenerate = new ArrayList<String>();
						// TODO
//						for (Map.Entry<String, Boolean> entry : aggregate.entrySet()) {
//							if (entry.getValue() != null && entry.getValue()) {
//								filesToGenerate.add(entry.getKey());
//							}
//						}
//						aggregate.keySet().removeAll(filesToGenerate);

						if (!aggregate.isEmpty()) {
							GenerateFilesDialog dialog = new GenerateFilesDialog(shell, aggregate);
							dialog.setBlockOnOpen(true);
							if (dialog.open() == Window.OK) {
								String[] result = dialog.getFilesToGenerate();
								if (result == null) {
									filesToGenerate = null;
								} else {
									filesToGenerate.addAll(Arrays.asList(result));
								}
							} else {
								return Status.CANCEL_STATUS;
							}
						} else {
							// No questions to ask so generate all by default
							filesToGenerate = null;
						}

						if (filesToGenerate != null && filesToGenerate.isEmpty()) {
							return Status.CANCEL_STATUS;
						}

						final Map<Implementation, String[]> implFileMap = new HashMap<Implementation, String[]>();
						for (Map.Entry<Implementation, Map<String, Boolean>> entry : implMap.entrySet()) {
							if (filesToGenerate == null) {
								implFileMap.put(entry.getKey(), null);
								continue;
							} else {
								List<String> subsetFilesToGenerate = new ArrayList<String>(entry.getValue().keySet());
								List<String> filesToRemove = new ArrayList<String>(subsetFilesToGenerate);
								filesToRemove.removeAll(filesToGenerate);
								subsetFilesToGenerate.removeAll(filesToRemove);

								implFileMap.put(entry.getKey(), subsetFilesToGenerate.toArray(new String[subsetFilesToGenerate.size()]));
							}
						}

						WorkspaceJob processJob = new WorkspaceJob("Generating...") {

							@Override
							public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
								return processImpls(implFileMap, monitor);
							}
						};
						processJob.setUser(true);
						processJob.schedule();

						return Status.OK_STATUS;
					}
				};
				checkFilesJob.setUser(true);
				checkFilesJob.schedule();

				return Status.OK_STATUS;
			}
		};
		getFilesJob.setUser(true);
		getFilesJob.schedule();
	}

	private static IStatus processImpls(Map<Implementation, String[]> implMap, IProgressMonitor monitor) throws CoreException {
		try {
			SubMonitor progress = SubMonitor.convert(monitor, "Generating...", implMap.size() + 2);
			final SoftPkg softPkg = (SoftPkg) implMap.entrySet().iterator().next().getKey().eContainer();
			TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(softPkg);
			final IProject project = ModelUtil.getProject(softPkg);
			final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings(softPkg);

			// Refresh project before generating code
			project.refreshLocal(IResource.DEPTH_INFINITE, null);

			final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Problems while generating code", null);
			for (Map.Entry<Implementation, String[]> entry : implMap.entrySet()) {
				if (progress.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				SubMonitor implGenerateWork = progress.newChild(1);
				implGenerateWork.beginTask("Generating " + entry.getKey().getId(), 1);

				final Implementation impl = entry.getKey();
				IStatus status = validate(project, softPkg, impl, waveDev);
				if (!status.isOK()) {
					retStatus.add(status);
					if (retStatus.getSeverity() == IStatus.ERROR) {
						return retStatus;
					}
				}

				// Generate code for each implementation
				final EMap<String, ImplementationSettings> implSet = waveDev.getImplSettings();
				// Generate code for implementation
				final ImplementationSettings settings = implSet.get(impl.getId());
				final ArrayList<FileToCRCMap> mapping = new ArrayList<FileToCRCMap>();

				String[] filesToGenerate = entry.getValue();
				status = generateImplementation(filesToGenerate, impl, settings, implGenerateWork.newChild(1), softPkg, mapping);
				if (!status.isOK()) {
					retStatus.add(status);
					if (status.getSeverity() == IStatus.ERROR) {
						return retStatus;
					}
				}

				// Update CRCs for implementation
				try {
					updateCRCs(domain, settings, mapping);
				} catch (final IOException e) {
					retStatus.add(new Status(IStatus.WARNING, RedhawkCodegenActivator.PLUGIN_ID, "Problem while generating CRCs for implementations", e));
				}
			}

			// Save the implementation settings if there were any implementations we generated for (saves the CRC modifications)
			progress.setTaskName("Save wavedev settings");
			try {
				saveResource(domain, waveDev);
			} catch (final CoreException ex) {
				retStatus.add(new Status(ex.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save CRCs", ex));
			}

			// Add general builders
			progress.setTaskName("Adding builders");
			CodegenUtil.addTopLevelBuildScriptBuilder(project, progress.newChild(1));

			// Refresh project after generating code
			progress.setTaskName("Refreshing project");
			project.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(1));

			if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
				// Schedule a new job which will run a full build; this should ensure all resource change
				// notifications are dispatched before beginning the build
				final WorkspaceJob buildJob = new WorkspaceJob("Building Project " + project.getName()) {
					@Override
					public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
						project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
						return new Status(IStatus.OK, RedhawkCodegenUiActivator.PLUGIN_ID, "");
					}
				};
				buildJob.setPriority(Job.LONG);
				buildJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
				buildJob.schedule();
			}

			return retStatus;
		} finally {
			monitor.done();
		}

	}

	private static IStatus generateImplementation(final String[] files, final Implementation impl, final ImplementationSettings settings,
		final IProgressMonitor monitor, final SoftPkg softpkg, final List<FileToCRCMap> crcMap) {
		if (settings == null) {
			return new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find settings (wavedev) for " + impl.getId()
				+ ", skipping generation");
		}

		final String implId = impl.getId();
		final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Problems while generating implementation " + implId,
			null);
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

				final IFile mainFile = generator.getDefaultFile(impl, settings);
				boolean openEditor = (mainFile == null || !mainFile.exists());

				status = generator.generate(settings, impl, genConsole.getOutStream(), genConsole.getErrStream(), progress.newChild(1), files,
					generator.shouldGenerate(), crcMap);
				if (!status.isOK()) {
					retStatus.add(status);
					if (status.getSeverity() == IStatus.ERROR) {
						return retStatus;
					}
				}

				// Update last generated date
				final WaveDevSettings wavedev = CodegenUtil.loadWaveDevSettings(softpkg);
				PropertyUtil.setLastGenerated(wavedev, settings, new Date(System.currentTimeMillis()));

				if (openEditor && (mainFile != null) && mainFile.exists()) {
					progress.subTask("Opening editor for main file");

					// Open the selected editor
					final WorkbenchJob openJob = new WorkbenchJob("Open editor") {
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
					openJob.schedule();
				}

				if (retStatus.isOK()) {
					return new Status(IStatus.OK, RedhawkCodegenActivator.PLUGIN_ID, "Succeeded generating code for implementation");
				}

			} catch (final CoreException e) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenActivator.PLUGIN_ID, "Unexpected error", e));
			}
		} else {
			retStatus.add(new Status(
				IStatus.WARNING,
				RedhawkCodegenActivator.PLUGIN_ID,
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
	 * @since 8.0
	 */
	private static Map<String, Boolean> getFilesToGenerateMap(IProgressMonitor monitor, Implementation impl) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Calculating files to generate...", IProgressMonitor.UNKNOWN);
		try {
			final ImplementationSettings implSettings = getImplSettings(impl);
			final IScaComponentCodegen generator = getGenerator(implSettings);

			final SoftPkg softpkg = (SoftPkg) impl.eContainer();

			if (generator.shouldGenerate()) {
				Future<Map<String, Boolean>> future = EXECUTOR_POOL.submit(new Callable<Map<String, Boolean>>() {

					@Override
					public Map<String, Boolean> call() throws Exception {
						return generator.getGeneratedFiles(implSettings, softpkg);
					}
				});
				Map<String, Boolean> retVal;
				while (true) {
					try {
						retVal = future.get(1, TimeUnit.SECONDS);
						break;
					} catch (InterruptedException e) {
						throw new CoreException(Status.CANCEL_STATUS);
					} catch (ExecutionException e) {
						if (e.getCause() instanceof CoreException) {
							throw ((CoreException) e.getCause());
						} else {
							throw new CoreException(new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID,
								"Failed in calling generator get generated files.", e));
						}
					} catch (TimeoutException e) {
						if (subMonitor.isCanceled()) {
							future.cancel(true);
							throw new OperationCanceledException();
						}
					}
				}
				return retVal;
			} else {
				return Collections.emptyMap();
			}
		} finally {
			subMonitor.done();
		}
	}

	private static ImplementationSettings getImplSettings(Implementation impl) {
		final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings((SoftPkg) impl.eContainer());
		// Generate code for each implementation
		final EMap<String, ImplementationSettings> implSet = waveDev.getImplSettings();
		// Generate code for implementation
		final ImplementationSettings settings = implSet.get(impl.getId());

		return settings;
	}

	private static IScaComponentCodegen getGenerator(ImplementationSettings settings) throws CoreException {
		final String codegenId = settings.getGeneratorId();
		final ICodeGeneratorDescriptor codeGenDesc = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegen(codegenId);
		// Get the specific code generator
		final IScaComponentCodegen generator = codeGenDesc.getGenerator();
		return generator;
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
	private static IStatus validate(final IProject project, final SoftPkg softPkg, Implementation impl, final WaveDevSettings waveDev) {
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
			ImplementationSettings implSettings = waveDev.getImplSettings().get(impl.getId());
			if (implSettings == null) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find settings in wavedev file for implementation "
					+ impl.getId()));
			} else {
				String templateId = implSettings.getTemplate();
				ITemplateDesc template = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(templateId);
				if (template == null) {
					retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find code generator template" + templateId));
				}
			}
		}

		return retStatus;
	}

	private static void updateCRCs(EditingDomain domain, final ImplementationSettings implSettings, final List<FileToCRCMap> crcMap) throws IOException {
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
		if (domain != null) {
			final CompoundCommand updateCommand = new CompoundCommand();
			for (final FileToCRCMap crc : crcMap) {
				final AddCommand cmd = new AddCommand(domain, implSettings.getGeneratedFileCRCs(), crc);
				updateCommand.append(cmd);
			}

			for (final Entry<FileToCRCMap, FileToCRCMap> crcEntry : foundCRCs.entrySet()) {
				final SetCommand cmd = new SetCommand(domain, crcEntry.getKey(), CodegenPackage.Literals.FILE_TO_CRC_MAP__CRC, crcEntry.getValue().getCrc());
				updateCommand.append(cmd);
			}
			if (!updateCommand.isEmpty()) {
				domain.getCommandStack().execute(updateCommand);
			}
		} else {
			for (final FileToCRCMap crc : crcMap) {
				implSettings.getGeneratedFileCRCs().add(crc);
			}

			for (final Entry<FileToCRCMap, FileToCRCMap> crcEntry : foundCRCs.entrySet()) {
				crcEntry.getKey().setCrc(crcEntry.getValue().getCrc());
			}
		}
	}

	/**
	 * Saves the {@link WaveDevSettings} to disk.
	 * 
	 * @param waveDevSettings The settings to save
	 * @throws CoreException A problem occurs while writing the settings to disk
	 */
	private static void saveResource(EditingDomain domain, final WaveDevSettings waveDevSettings) throws CoreException {
		try {
			waveDevSettings.eResource().save(null);

			if (domain != null) {
				((BasicCommandStack) domain.getCommandStack()).saveIsDone();
				domain.getCommandStack().flush();
			}

		} catch (final IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save the updated implementation settings", e));
		}
	}
}
