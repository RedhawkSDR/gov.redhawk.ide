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

import gov.redhawk.ide.codegen.jet.TopLevelBuildShTemplate;
import gov.redhawk.ide.util.ResourceUtils;
import gov.redhawk.model.sca.util.ModelUtil;

import java.io.ByteArrayInputStream;
import java.util.Map;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

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
 * An incremental builder that adds a top-level build script to components / devices.
 * 
 * @since 4.0
 * @deprecated This functionality is used by 1.8 projects, and is no longer necessary in 1.9. If you remove this,
 * remove the template that goes with it as well. 
 */
public class TopLevelBuildScript extends IncrementalProjectBuilder {
	
	/**
	 * The ID of the top level build script builder.
	 */
	public static final String ID = "gov.redhawk.ide.codegen.builders.TopLevelBuildScript";
	
	/**
	 * A flag used internally to determine if the top-level build script should be regenerated on auto/incremental
	 * builds. 
	 */
	private boolean generateFlag;

	public TopLevelBuildScript() {
	}

	@Override
	protected IProject[] build(int kind, @SuppressWarnings("rawtypes") Map args, IProgressMonitor monitor) throws CoreException {
		try {
			if (kind == IncrementalProjectBuilder.FULL_BUILD || kind == IncrementalProjectBuilder.CLEAN_BUILD) {
				generateBuildScript(monitor);
			} else if (this.getDelta(getProject()) != null) {
				generateFlag = false;
				IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
					public boolean visit(IResourceDelta delta) throws CoreException {
						// We can ignore anything that's in a sub-directory
						if (delta.getResource() instanceof IContainer && delta.getProjectRelativePath().segmentCount() == 1) {
							return false;
						}
						
						// If an SPD file was changed, we definitely need to generate the build script
						if (delta.getResource().getName().endsWith(SpdPackage.FILE_EXTENSION)) {
							generateFlag = true;
							return false;
						}
						
						return true;
					}
				};
				getDelta(getProject()).accept(visitor);
				if (generateFlag) {
					generateBuildScript(monitor);
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
	 * Generates a top-level project build script based on the implementations in the SPD file.
	 * 
	 * @param monitor the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be
	 *  reported and that the operation cannot be canceled.
	 * @throws CoreException An error occurs while generating the build script file
	 */
	private void generateBuildScript(IProgressMonitor monitor) throws CoreException {
		final SubMonitor progress = SubMonitor.convert(monitor, "Creating top-level build script", 1);
	    final IProject project = getProject();
	    for (IResource resource : project.members()) {
	    	if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
	    		// Generate content for the build script
	    		byte[] buildScriptContent = generateBuildScriptContent(resource);
	    		
	    		// Write the file to disk
				final IFile buildShFile = project.getFile("build.sh");
				if (buildScriptContent == null) {
					if (buildShFile.exists()) {
						buildShFile.delete(true, progress.newChild(1));
					}
				} else {
					if (buildShFile.exists()) {
						buildShFile.setContents(new ByteArrayInputStream(buildScriptContent), true, false, progress.newChild(1));
					} else {
						buildShFile.create(new ByteArrayInputStream(buildScriptContent), true, progress.newChild(1));
					}
					ResourceUtils.runSystemCommand("chmod +x " + buildShFile.getLocation().toOSString());
				}
				
				return;
	    	}
	    }
    }
	
	/**
	 * Creates the contents of the top-level build script.
	 * 
	 * @param resource The {@link IResource} for the SPD
	 * @return The contents of the top-level build script file, or null if one should not be created
	 * @throws CoreException A problem occurs while generating the file contents
	 */
	private byte[] generateBuildScriptContent(IResource resource) throws CoreException {
		// Load the SPD
		final URI spdURI = URI.createPlatformResourceURI(resource.getFullPath().toString(), false);
		final SoftPkg softPkg = ModelUtil.loadSoftPkg(spdURI);
		
		// If there are no implementations, signal no build script content
		if (softPkg.getImplementation().size() == 0) {
			return null;
		}
		
		// Generate content
		TopLevelBuildShTemplate template = new TopLevelBuildShTemplate();
		return template.generate(softPkg).getBytes();
	}
}
