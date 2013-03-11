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

import gov.redhawk.ide.debug.ScaLauncherUtil;

import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.AnyUtils;
import CF.DataType;
import CF.Resource;
import CF.ResourceFactoryPackage.CreateResourceFailure;

/**
 * 
 */
public class SdrWaveformFactory extends AbstractResourceFactory {

	private final String refID;
	private final SoftwareAssembly sad;

	public SdrWaveformFactory(final SoftwareAssembly sad) {
		this.refID = sad.getId();
		this.sad = sad;
	}

	/**
	 * {@inheritDoc}
	 */
	public String identifier() {
		return this.refID;
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
		return ScaLauncherUtil.createExecParamString(execParams);
	}

	private Implementation chooseImplementation(final SoftPkg spd) {
		return spd.getImplementation().get(0);
	}

}
