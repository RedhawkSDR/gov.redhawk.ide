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
package gov.redhawk.ide.codegen.ui.internal.job;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.GeneratorUtil;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.NamedThreadFactory;

/**
 * Parent job for code generation. Calls jobs for:
 * <ul>
 * <li>Calculating and displaying which files to generate
 * <li>Code generation
 * <li>Opening the editor for the default file
 * </ul>
 */
public class GenerateFilesJob extends Job {

	private static final ExecutorService EXECUTOR_POOL = Executors.newSingleThreadExecutor(new NamedThreadFactory(GenerateCode.class.getName()));

	private List<Implementation> impls;
	private Shell shell;

	/**
	 * Parent job for code generation.
	 */
	public GenerateFilesJob(String name, final Shell shell, final List<Implementation> impls) {
		super(name);
		this.shell = shell;
		this.impls = impls;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		// Map of Implementation -> map of FileName relative to output location -> true will regenerate,
		// false wants to regenerate but contents different
		SubMonitor progress = SubMonitor.convert(monitor, "Calculating files to generate", impls.size());
		final Map<Implementation, Set<FileStatus>> implMap = new HashMap<Implementation, Set<FileStatus>>();
		for (Implementation impl : impls) {
			try {
				Set<FileStatus> resultSet = getFilesToGenerate(progress.newChild(1, SubMonitor.SUPPRESS_NONE), impl);
				implMap.put(impl, resultSet);
			} catch (CoreException e) {
				return new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to calculate files to generate", e);
			}
		}
		progress.done();

		WorkbenchJob checkFilesJob = new ProcessImplsJob("Check files", shell, implMap);
		checkFilesJob.setUser(false);
		checkFilesJob.setSystem(true);
		checkFilesJob.schedule();

		return Status.OK_STATUS;
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
	private Set<FileStatus> getFilesToGenerate(IProgressMonitor monitor, Implementation impl) throws CoreException {
		SubMonitor subMonitor = SubMonitor.convert(monitor, "Calculating files for implementation " + impl.getId(), IProgressMonitor.UNKNOWN);
		try {
			final ImplementationSettings implSettings = WaveDevUtil.getImplSettings(impl);
			final IScaComponentCodegen generator = GeneratorUtil.getGenerator(implSettings);
			if (!generator.shouldGenerate()) {
				return Collections.emptySet();
			}

			final SoftPkg softpkg = (SoftPkg) impl.eContainer();
			Future<Set<FileStatus>> future = EXECUTOR_POOL.submit(new Callable<Set<FileStatus>>() {
				@Override
				public Set<FileStatus> call() throws Exception {
					return generator.getGeneratedFilesStatus(implSettings, softpkg);
				}
			});

			Set<FileStatus> retVal;
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
						throw new CoreException(
							new Status(Status.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Failed in calling generator get generated files.", e));
					}
				} catch (TimeoutException e) {
					if (subMonitor.isCanceled()) {
						future.cancel(true);
						throw new OperationCanceledException();
					}
				}
			}
			return retVal;
		} finally {
			subMonitor.done();
		}
	}

}
