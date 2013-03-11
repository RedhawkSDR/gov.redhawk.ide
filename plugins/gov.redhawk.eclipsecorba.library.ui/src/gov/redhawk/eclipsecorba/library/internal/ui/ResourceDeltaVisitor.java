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
package gov.redhawk.eclipsecorba.library.internal.ui;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;

/**
 * This class visits a resource delta looking for modified ".library" files. Upon completion, the collection of
 * {@link IResource}s are available via {@link #getChangedResources()}.
 */
class ResourceDeltaVisitor implements IResourceDeltaVisitor {
	/** The changed resources. */
	private final Collection<IResource> changedResources = new ArrayList<IResource>();

	/**
	 * Instantiates a new resource delta visitor.
	 */
	public ResourceDeltaVisitor() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean visit(final IResourceDelta delta) {
		if (delta.getResource().getType() == IResource.FILE) {
			if (delta.getKind() == IResourceDelta.REMOVED || delta.getKind() == IResourceDelta.CHANGED
			        || delta.getKind() == IResourceDelta.ADDED
					&& delta.getFlags() != IResourceDelta.MARKERS) {
				if (delta.getResource() instanceof IFile && !delta.getResource().getName().equals(".library")) {
					return false;
				}
				this.changedResources.add(delta.getResource());
			}
		}

		return true;
	}

	/**
	 * Gets the changed resources.
	 * 
	 * @return the changed resources
	 */
	public Collection<IResource> getChangedResources() {
		return this.changedResources;
	}
}
