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
package gov.redhawk.ide.debug;

import java.io.IOException;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.DataType;
import CF.Resource;
import CF.ResourceFactoryPackage.CreateResourceFailure;

/**
 * @since 4.0
 * 
 */
public class WorkspaceWaveformFactory extends AbstractResourceFactory {

	private final SoftwareAssembly sad;

	public WorkspaceWaveformFactory(final IFile profile) throws IOException {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		try {
			final org.eclipse.emf.ecore.resource.Resource resource =  resourceSet.getResource(URI.createPlatformResourceURI(profile.getFullPath().toString(), true), true);
			resource.load(null);
			this.sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		} catch (WrappedException we) {
			throw new IOException("Failed to load Waveform: " + profile.getFullPath(), we);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String identifier() {
		return this.sad.getId();
	}

	@Override
	protected Resource createInstance(final String resourceId, final DataType[] qualifiers, final String mode) throws CreateResourceFailure {
		throw new UnsupportedOperationException();
	}

}
