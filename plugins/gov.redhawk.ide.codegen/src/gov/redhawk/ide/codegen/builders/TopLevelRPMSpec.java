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
package gov.redhawk.ide.codegen.builders;

import gov.redhawk.ide.codegen.jet.TopLevelDcdRpmSpecTemplate;
import gov.redhawk.ide.codegen.jet.TopLevelSadRpmSpecTemplate;
import gov.redhawk.ide.natures.ScaNodeProjectNature;
import gov.redhawk.ide.natures.ScaWaveformProjectNature;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.ByteArrayInputStream;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.URI;

/**
 * An incremental builder that adds a top-level RPM spec file to components / devices.
 */
public class TopLevelRPMSpec extends IncrementalProjectBuilder {

	/**
	 * The ID of the top level build script builder.
	 */
	public static final String ID = "gov.redhawk.ide.codegen.builders.TopLevelRPMSpec";

	/**
	 * A flag used internally to determine if the top-level RPM spec file should be regenerated on auto/incremental
	 * builds. 
	 */
	private boolean generateFlag;

	public TopLevelRPMSpec() {
	}

	@Override
	protected IProject[] build(final int kind, @SuppressWarnings("rawtypes") final Map args, final IProgressMonitor monitor) throws CoreException {
		try {
			final int SCAN_DELTA_WORK = 20;
			final int GENERATE_SPEC_FILE_WORK = 80;
			final SubMonitor progress = SubMonitor.convert(monitor, "Create top-level RPM spec file", SCAN_DELTA_WORK + GENERATE_SPEC_FILE_WORK);

			final IProject project = getProject();
			final boolean isWaveform = project.hasNature(ScaWaveformProjectNature.ID);
			final boolean isNode = project.hasNature(ScaNodeProjectNature.ID);
			if (!isWaveform && !isNode) {
				return null;
			}

			// Determine if we need to generate
			this.generateFlag = false;
			if (kind == IncrementalProjectBuilder.FULL_BUILD || kind == IncrementalProjectBuilder.CLEAN_BUILD) {
				// We always generate on a full build / clean build
				this.generateFlag = true;
				progress.setWorkRemaining(GENERATE_SPEC_FILE_WORK);
			} else if (this.getDelta(project) != null) {
				// Explore the delta to see if we need to generate
				final IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					@Override
					public boolean visit(final IResourceDelta delta) throws CoreException {
						// We can ignore anything that's in a sub-directory
						if (delta.getResource() instanceof IContainer && delta.getProjectRelativePath().segmentCount() == 1) {
							return false;
						}

						final String resourceName = delta.getResource().getName();
						if (isNode && resourceName.endsWith(DcdPackage.FILE_EXTENSION)) {
							// If this is a node and the DCD file was changed, generate
							TopLevelRPMSpec.this.generateFlag = true;
							return false;
						} else if (isWaveform && resourceName.endsWith(SadPackage.FILE_EXTENSION)) {
							// If this is a waveform and the SAD file was changed, generate
							TopLevelRPMSpec.this.generateFlag = true;
							return false;
						}

						return true;
					}
				};
				getDelta(project).accept(visitor);
				progress.worked(SCAN_DELTA_WORK);
			}

			if (this.generateFlag) {
				if (isNode) {
					generateDcdRpmSpecFile(progress.newChild(GENERATE_SPEC_FILE_WORK));
				} else if (isWaveform) {
					generateSadRpmSpecFile(progress.newChild(GENERATE_SPEC_FILE_WORK));
				}
			}
			return null;
		} finally {
			if (monitor != null) {
				monitor.done();
			}
		}
	}

	/**
	 * Generates a top-level RPM spec file based on the device manager/devices in the DCD file.
	 * 
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @throws CoreException An error occurs while generating the RPM spec file
	 */
	private void generateDcdRpmSpecFile(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level RPM spec file", 1);
		final IProject project = getProject();
		for (final IResource resource : project.members()) {
			if (resource.getName().endsWith(".dcd.xml")) {
				// Load the DCD
				final URI dcdURI = URI.createPlatformResourceURI(resource.getFullPath().toString(), false);
				final DeviceConfiguration devCfg = ModelUtil.loadDeviceConfiguration(dcdURI);

				// Generate content for the RPM spec file
				final TopLevelDcdRpmSpecTemplate template = new TopLevelDcdRpmSpecTemplate();
				final byte[] rpmSpecFileContent = template.generate(devCfg).getBytes();

				// Write the file to disk
				final IFile rpmSpecFile = getProject().getFile(devCfg.getName() + ".spec");
				if (rpmSpecFile.exists()) {
					rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress.newChild(1));
				} else {
					rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress.newChild(1));
				}

				return;
			}
		}
	}

	/**
	 * Generates a top-level RPM spec file based on the components and devices referenced in the SAD file.
	 * 
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @throws CoreException An error occurs while generating the RPM spec file
	 */
	private void generateSadRpmSpecFile(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level RPM spec file", 1);
		final IProject project = getProject();
		for (final IResource resource : project.members()) {
			if (resource.getName().endsWith(SadPackage.FILE_EXTENSION)) {
				// Load the SAD
				final URI sadURI = URI.createPlatformResourceURI(resource.getFullPath().toString(), false);
				final SoftwareAssembly sad = ModelUtil.loadSoftwareAssembly(sadURI);
				if (sad == null) {
					return;
				}

				// Generate content for the RPM spec file
				final TopLevelSadRpmSpecTemplate template = new TopLevelSadRpmSpecTemplate();
				final String rpmSpecFileString = template.generate(sad);
				final byte[] rpmSpecFileContent;
				if (rpmSpecFileString == null) {
					rpmSpecFileContent = null;
				} else {
					rpmSpecFileContent = rpmSpecFileString.getBytes();
				}
				
				// Write the file to disk
				final IFile rpmSpecFile = getProject().getFile(sad.getName() + ".spec");
				if (rpmSpecFileContent == null) {
					if (rpmSpecFile.exists()) {
						rpmSpecFile.delete(true, progress.newChild(1));
					}
				} else if (rpmSpecFile.exists()) {
					rpmSpecFile.setContents(new ByteArrayInputStream(rpmSpecFileContent), true, false, progress.newChild(1));
				} else {
					rpmSpecFile.create(new ByteArrayInputStream(rpmSpecFileContent), true, progress.newChild(1));
				}

				return;
			}
		}
	}
}
