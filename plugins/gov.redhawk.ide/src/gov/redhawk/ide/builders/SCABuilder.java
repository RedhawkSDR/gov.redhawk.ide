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
package gov.redhawk.ide.builders;

import gov.redhawk.ide.natures.ScaProjectNature;

import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class SCABuilder extends IncrementalProjectBuilder {

	/** The ID of this project builder. */
	public static final String ID = "gov.redhawk.ide.builders.scaproject";

	private class DeltaVisitor implements IResourceDeltaVisitor {

		private boolean shouldVisit = false;

		public boolean visit(final IResourceDelta delta) throws CoreException {
			if (this.shouldVisit) {
				return false;
			}
			final IResource resource = delta.getResource();
			if (resource instanceof IProject) {
				return isInterestingProject((IProject) resource);
			}

			if (resource instanceof IFolder) {
				return true;
			}

			if (resource instanceof IFile) {
				// see if this is it
				final IFile candidate = (IFile) resource;
				if (isScaResource(candidate)) {
					this.shouldVisit = true;
				}
			}
			return false;
		}

	}

	private class SCABuildVisitor implements IResourceVisitor {

		public boolean visit(final IResource resource) throws CoreException {
			if (resource instanceof IProject) {
				return isInterestingProject((IProject) resource);
			}

			if (resource instanceof IFolder) {
				return true;
			}

			if (resource instanceof IFile) {
				// see if this is it
				final IFile candidate = (IFile) resource;
				if (isScaResource(candidate)) {
					final URI uri = URI.createPlatformResourceURI(resource.getFullPath().toString(), false);
					if (uri != null) {
						final ResourceSet set = new ResourceSetImpl();
						final Resource eResource = set.getResource(uri, true);
						final Diagnostic diagnostic = SCAMarkerUtil.INSTANCE.getDiagnostician().validate(eResource.getEObject("/"));
						SCAMarkerUtil.INSTANCE.createMarkers(eResource, diagnostic);
					}
				}
			}
			return false;
		}

	}

	public SCABuilder() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
		IResourceDelta delta = null;
		if (kind != IncrementalProjectBuilder.FULL_BUILD) {
			delta = getDelta(getProject());
		}

		if (delta == null || kind == IncrementalProjectBuilder.FULL_BUILD) {
			if (isInterestingProject(getProject())) {
				fullBuild(monitor);
			}
		} else {
			incrementalBuild(delta, monitor);
		}
		return new IProject[0];
	}

	private void incrementalBuild(final IResourceDelta delta, final IProgressMonitor monitor) throws CoreException {
		final DeltaVisitor visitor = new DeltaVisitor();
		delta.accept(visitor);
		if (visitor.shouldVisit) {
			fullBuild(monitor);
		}
	}

	private void fullBuild(final IProgressMonitor monitor) throws CoreException {
		getProject().accept(new SCABuildVisitor());
	}

	/**
	 * @since 6.0
	 */
	protected boolean isInterestingProject(final IProject project) throws CoreException {
		return project.hasNature(ScaProjectNature.ID);
	}

	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		final SubMonitor localmonitor = SubMonitor.convert(monitor, "Clean sca project " + getProject().getName(), 1);
		try {
			// clean existing markers on schema files
			cleanScaIn(getProject(), localmonitor);
			localmonitor.worked(1);
		} finally {
			localmonitor.done();
		}
	}

	private void cleanSca(final IFile file) throws CoreException {
		if (isScaResource(file)) {
			file.deleteMarkers(SCAMarkerUtil.VALIDATION_MARKER_TYPE, true, IResource.DEPTH_ZERO);
		}
	}

	private void cleanScaIn(final IContainer container, final IProgressMonitor monitor) throws CoreException {
		if (monitor.isCanceled()) {
			throw new OperationCanceledException();
		}
		final IResource[] members = container.members();
		for (int i = 0; i < members.length; i++) {
			final IResource member = members[i];
			if (member instanceof IContainer) {
				cleanScaIn((IContainer) member, monitor);
			} else if (member instanceof IFile) {
				cleanSca((IFile) member);
			}
		}
	}

	/**
	 * @since 6.0
	 */
	protected boolean isScaResource(final IFile resource) {
		final String name = resource.getName();
		return name.endsWith(SpdPackage.FILE_EXTENSION) || name.endsWith(PrfPackage.FILE_EXTENSION) || name.endsWith(ScdPackage.FILE_EXTENSION)
		        || name.endsWith(SadPackage.FILE_EXTENSION) || name.endsWith(DcdPackage.FILE_EXTENSION);
	}
}
