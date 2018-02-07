/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.codegen.ui.internal.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.osgi.framework.Version;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.FileToCRCMap;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.GeneratorConsole;
import gov.redhawk.ide.codegen.ui.internal.GeneratorUtil;
import gov.redhawk.ide.codegen.ui.internal.SaveXmlUtils;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.ide.codegen.ui.utils.DocumentationUtils;
import gov.redhawk.ide.codegen.util.PropertyUtil;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.prf.PrfDocumentRoot;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.scd.ScdDocumentRoot;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdDocumentRoot;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class CodegenJob extends WorkspaceJob {

	private Map<Implementation, String[]> implMap;

	/**
	 * A set of all files that we want to open an editor for after codegen completes
	 */
	private Set<IFile> mainFileSet = new HashSet<>();

	/**
	 * Must call {@link #setImplementationsAndFiles(Map)} before scheduling the job.
	 */
	public CodegenJob() {
		super("Generating...");
		setUser(true);
	}

	/**
	 * Provides the set of files the code generator will be asked to generate for each implementation.
	 * @param filesForImplementation
	 */
	public void setImplementationsAndFiles(Map<Implementation, String[]> filesForImplementation) {
		this.implMap = filesForImplementation;
	}

	@Override
	public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
		SubMonitor progress = SubMonitor.convert(monitor, "Generating...", implMap.size() + 2);
		final SoftPkg softPkg = (SoftPkg) implMap.entrySet().iterator().next().getKey().eContainer();
		final TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(softPkg);
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
				retStatus.add(new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID, "Problem while generating CRCs for implementations", e));
			}

			// Gather some info to update the XML
			ImplementationSettings implSettings = WaveDevUtil.getImplSettings(impl);
			final IScaComponentCodegen generator = GeneratorUtil.getGenerator(implSettings);
			final Version codeGenVersion = generator.getCodegenVersion();
			String headerContents = DocumentationUtils.getHeaderContents(project);
			SpdDocumentRoot spdRoot = ScaEcoreUtils.getEContainerOfType(softPkg, SpdDocumentRoot.class);
			Properties prf = (softPkg.getPropertyFile() == null) ? null : softPkg.getPropertyFile().getProperties();
			PrfDocumentRoot prfRoot = ScaEcoreUtils.getEContainerOfType(prf, PrfDocumentRoot.class);
			SoftwareComponent scd = (softPkg.getDescriptor() == null) ? null : softPkg.getDescriptor().getComponent();
			ScdDocumentRoot scdRoot = ScaEcoreUtils.getEContainerOfType(scd, ScdDocumentRoot.class);

			RunnableWithResult<Void> runnable = new RunnableWithResult.Impl<Void>() {
				public void run() {
					boolean changes = false;

					String codegenVersion = generator.getCodegenVersion().toString();
					if (new Version(1, 10, 0).compareTo(codeGenVersion) <= 0 && !codegenVersion.equals(softPkg.getType())) {
						// Set the SPD's type field to the code generator's version
						ScaModelCommand.execute(softPkg, new ScaModelCommand() {
							@Override
							public void execute() {
								softPkg.setType(codegenVersion);
							}
						});
						changes = true;
					}

					// Apply header to XML files
					if (spdRoot != null) {
						changes |= DocumentationUtils.setXMLCommentHeader(domain, spdRoot.getMixed(), headerContents);
					}
					if (prfRoot != null) {
						changes |= DocumentationUtils.setXMLCommentHeader(domain, prfRoot.getMixed(), headerContents);
					}
					if (scdRoot != null) {
						changes |= DocumentationUtils.setXMLCommentHeader(domain, scdRoot.getMixed(), headerContents);
					}

					// Save XML files (SPD, PRF, SCD) and wavedev
					// This should include codegen version, header updates, file CRCs
					if (changes) {
						try {
							SaveXmlUtils.save(softPkg, prf, scd);
						} catch (CoreException e) {
							setStatus(new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to save changes to XML", e));
							return;
						}
					}

					setStatus(Status.OK_STATUS);
				};
			};
			PlatformUI.getWorkbench().getDisplay().syncExec(runnable);

			if (!runnable.getStatus().isOK()) {
				return runnable.getStatus();
			}
		}

		// Remove top-level build.sh script builder if it exists, as this was only needed in support of 1.8 projects
		progress.setTaskName("Updating builders");
		CodegenUtil.removeDeprecatedBuilders(project, progress.newChild(1));

		// Refresh project after generating code
		progress.setTaskName("Refreshing project");
		project.refreshLocal(IResource.DEPTH_INFINITE, progress.newChild(1));

		if (ResourcesPlugin.getWorkspace().getDescription().isAutoBuilding()) {
			// Schedule a new job which will run a full build; this should ensure all resource change
			// notifications are dispatched before beginning the build
			final WorkspaceJob buildJob = new WorkspaceJob("Building Project " + project.getName()) {
				@Override
				public IStatus runInWorkspace(final IProgressMonitor monitor) throws CoreException {
					final int CLEAN_WORK = 15;
					final int BUILD_WORK = 85;
					SubMonitor progress = SubMonitor.convert(monitor, CLEAN_WORK + BUILD_WORK);

					project.build(IncrementalProjectBuilder.CLEAN_BUILD, progress.newChild(CLEAN_WORK));
					if (monitor.isCanceled()) {
						return Status.CANCEL_STATUS;
					}

					project.build(IncrementalProjectBuilder.FULL_BUILD, progress.newChild(BUILD_WORK));
					return Status.OK_STATUS;
				}
			};
			buildJob.setPriority(Job.LONG);
			buildJob.setRule(ResourcesPlugin.getWorkspace().getRuleFactory().buildRule());
			buildJob.schedule();
		}

		return retStatus;
	}

	private IStatus generateImplementation(final String[] files, final Implementation impl, final ImplementationSettings settings,
		final IProgressMonitor monitor, final SoftPkg softpkg, final List<FileToCRCMap> crcMap) {
		if (settings == null) {
			return new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID,
				"Unable to find settings (wavedev) for " + impl.getId() + ", skipping generation");
		}

		final String implId = impl.getId();
		final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Problems while generating implementation " + implId,
			null);
		final SubMonitor progress = SubMonitor.convert(monitor, 1);
		progress.setTaskName("Generating implementation " + implId);

		if (settings.getGeneratorId() == null) {
			retStatus.add(new Status(IStatus.WARNING, RedhawkCodegenUiActivator.PLUGIN_ID,
				"No code generator is specified in the settings (wavedev). Code generation was skipped for the implementation. Check your generator selection for the implementation."));
			return retStatus;
		}

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

			if (openEditor && mainFile != null && mainFile.exists()) {
				mainFileSet.add(mainFile);
			}

			if (retStatus.isOK()) {
				return new Status(IStatus.OK, RedhawkCodegenUiActivator.PLUGIN_ID, "Succeeded generating code for implementation");
			}
		} catch (final CoreException e) {
			retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unexpected error", e));
		}

		return retStatus;
	}

	/**
	 * Performs several validations to detect problems prior to code generation.
	 * 
	 * @param project The project to validate
	 * @param softPkg The {@link SoftPkg} to validate
	 * @param impls The {@link Implementation}s to be generated
	 * @param waveDev The wavedev to validate
	 * @return An {@link IStatus} indicating any issues found; problems should be of severity {@link IStatus#ERROR} to
	 * prevent code generation
	 */
	private IStatus validate(final IProject project, final SoftPkg softPkg, Implementation impl, final WaveDevSettings waveDev) {
		final MultiStatus retStatus = new MultiStatus(RedhawkCodegenUiActivator.PLUGIN_ID, IStatus.OK, "Validation problems prior to generating code", null);

		if (project == null) {
			return new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Project does not exist");
		}

		// Check XML files
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

			// Don't validate SCD or PRF if project is a Shared Library
			if (!impl.isSharedLibrary()) {
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
			}
		} catch (final CoreException e) {
			retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "A problem occurred while verifying the XML files", e));
		}

		// Wavedev checks
		if (waveDev == null) {
			retStatus.add(
				new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Unable to find project settings (wavedev) file. Cannot generate code."));
		} else {
			ImplementationSettings implSettings = waveDev.getImplSettings().get(impl.getId());
			if (implSettings == null) {
				retStatus.add(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID,
					"Unable to find settings in wavedev file for implementation " + impl.getId()));
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

	private void updateCRCs(EditingDomain domain, final ImplementationSettings implSettings, final List<FileToCRCMap> crcMap) throws IOException {
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
	 * @return The set of files that should be opened in their respective editors
	 */
	public Set<IFile> getFilesToOpen() {
		return mainFileSet;
	}
}
