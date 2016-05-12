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

import gov.redhawk.model.sca.ScaComponent;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import CF.DataType;
import CF.ErrorNumberType;
import CF.Resource;
import CF.ResourceFactoryOperations;
import CF.LifeCyclePackage.ReleaseError;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.InvalidResourceId;
import CF.ResourceFactoryPackage.ShutdownFailure;
import ExtendedCF.Sandbox;

/**
 * @since 4.0
 */
public abstract class AbstractResourceFactory implements ResourceFactoryOperations {

	@Override
	public Resource createResource(String resourceId, final DataType[] inputQualifiers) throws CreateResourceFailure {
		LocalSca localSca;
		try {
			localSca = ScaDebugPlugin.getInstance().getLocalSca(null);
		} catch (CoreException e) {
			throw new CreateResourceFailure(ErrorNumberType.CF_ENODEV, "Failed to find local sandbox to launch resource in.");
		}
		for (ScaComponent component : localSca.getSandboxWaveform().getComponents()) {
			if (component.getIdentifier().equals(resourceId)) {
				return component.getObj();
			}
		}
		String mode = null;

		// Strip off and Launch Type qualifiers
		List<DataType> qualifiers = new ArrayList<DataType>();
		for (final DataType t : inputQualifiers) {
			if (Sandbox.LAUNCH_TYPE.equals(t.id)) {
				final String value = t.value.extract_string();
				mode = value;
			} else {
				qualifiers.add(t);
			}
		}

		if (mode == null) {
			mode = "run";
		}

		// TODO Add support for other run modes
		if (!"run".equals(mode)) {
			throw new CreateResourceFailure(ErrorNumberType.CF_EINVAL, "Only 'run' mode is currently supported from sandbox.");
		}

		return createInstance(resourceId, qualifiers.toArray(new DataType[qualifiers.size()]), mode);
	}

	protected abstract Resource createInstance(String resourceId, DataType[] qualifiers, String launchMode) throws CreateResourceFailure;

	@Override
	public void releaseResource(final String resourceId) throws InvalidResourceId {
		LocalSca localSca;
		try {
			localSca = ScaDebugPlugin.getInstance().getLocalSca(null);
		} catch (CoreException e) {
			throw new InvalidResourceId("Failed to find local sandbox to launch resource in.");
		}
		for (ScaComponent component : localSca.getSandboxWaveform().getComponents()) {
			if (component.getIdentifier().equals(resourceId)) {
				try {
					component.releaseObject();
				} catch (ReleaseError e) {
					// PASS
				}
			}
		}
	}

	@Override
	public void shutdown() throws ShutdownFailure {
		// Do nothing
	}

}
