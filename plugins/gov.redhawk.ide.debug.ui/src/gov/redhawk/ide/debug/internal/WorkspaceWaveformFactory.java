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
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.SpdLauncherUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.AnyUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import CF.DataType;
import CF.Resource;
import CF.ResourceFactoryPackage.CreateResourceFailure;

/**
 * 
 */
public class WorkspaceWaveformFactory extends AbstractResourceFactory {

	private final SoftwareAssembly sad;
	private final IFile profile;

	public WorkspaceWaveformFactory(final IFile profile) throws IOException {
		final ResourceSet resourceSet = new ResourceSetImpl();
		final org.eclipse.emf.ecore.resource.Resource resource = resourceSet.getResource(URI.createPlatformResourceURI(profile.getFullPath().toString(), true),
		        true);
		resource.load(null);
		this.sad = SoftwareAssembly.Util.getSoftwareAssembly(resource);
		this.profile = profile;
	}

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.sad.getId();
	}

	@Override
	protected Resource createInstance(final String resourceId, final DataType[] qualifiers, final String mode) throws CreateResourceFailure {
		throw new UnsupportedOperationException();
	}

	private String createParams(final DataType[] qualifiers) {
		final Map<String, Object> execParams = new HashMap<String, Object>();
		for (final DataType t : qualifiers) {
			execParams.put(t.id, AnyUtils.convertAny(t.value));
		}
		return SpdLauncherUtil.createExecParamString(execParams);
	}

}
