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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import gov.redhawk.ide.codegen.FileStatus;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.GeneratorUtil;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.sca.util.SubMonitor;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Asks the code generator to provide the list of files it will act on, along with information about those files.
 */
public class CodegenFileStatusJob extends Job {

	private List<Implementation> impls;
	private Map<Implementation, Set<FileStatus>> implMap = new HashMap<>();

	public CodegenFileStatusJob(List<Implementation> impls) {
		super("Calculating files to generate...");
		setUser(true);
		this.impls = impls;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// Create a map of implementation -> set of files codegen wants to act on
		SubMonitor progress = SubMonitor.convert(monitor, "Calculating files to generate", impls.size());
		for (Implementation impl : impls) {
			try {
				Set<FileStatus> resultSet = getFilesToGenerate(progress.newChild(1), impl);
				implMap.put(impl, resultSet);
			} catch (CoreException e) {
				return new Status(e.getStatus().getSeverity(), RedhawkCodegenUiActivator.PLUGIN_ID, "Failed to calculate files to generate", e);
			}
		}

		return Status.OK_STATUS;
	}

	/**
	 * Get the file list for a single implementation
	 * @param monitor
	 * @param impl the {@link Implementation} code generation will be performed for 
	 * @throws CoreException A problem occurs while determining which files to generate
	 */
	private Set<FileStatus> getFilesToGenerate(IProgressMonitor monitor, Implementation impl) throws CoreException {
		SubMonitor.convert(monitor, "Calculating files for implementation " + impl.getId(), IProgressMonitor.UNKNOWN);
		SoftPkg softpkg = (SoftPkg) impl.eContainer();
		final ImplementationSettings implSettings = WaveDevUtil.getImplSettings(impl);
		final IScaComponentCodegen generator = GeneratorUtil.getGenerator(implSettings);

		if (!generator.shouldGenerate()) {
			return Collections.emptySet();
		} else {
			return generator.getGeneratedFilesStatus(implSettings, softpkg);
		}
	}

	/**
	 * @return The results of the job (which files the code generator will act on for each implementation)
	 */
	public Map<Implementation, Set<FileStatus>> getFilesForImplementation() {
		return implMap;
	}
}
