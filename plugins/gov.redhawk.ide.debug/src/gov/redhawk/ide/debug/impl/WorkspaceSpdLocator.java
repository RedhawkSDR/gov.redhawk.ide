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
package gov.redhawk.ide.debug.impl;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * 
 */
public class WorkspaceSpdLocator implements IResourceVisitor {
	private IFile result;
	private final ResourceSet resourceSet = new ResourceSetImpl();
	private final String profileId;
	private SoftPkg spd;

	public WorkspaceSpdLocator(final String profileId) {
		this.profileId = profileId;
	}

	public IFile getResult() {
		return this.result;
	}

	public boolean visit(final IResource resource) throws CoreException {
		if (resource instanceof IFile) {
			final IFile file = (IFile) resource;
			if (file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				try {
					final Resource eResource = this.resourceSet.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true);
					this.spd = SoftPkg.Util.getSoftPkg(eResource);
					if (this.spd != null && this.spd.getId() != null && this.spd.getId().equals(this.profileId)) {
						this.result = file;
					} else {
						this.spd = null;
					}
				} catch (final Exception e) {
					// PASS
				}
			}
			return false;
		} else {
			return this.result == null;
		}
	}

	public SoftPkg getSpd() {
		return this.spd;
	}

}
