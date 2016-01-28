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

import java.util.ArrayList;
import java.util.List;

import CF.DataType;
import CF.ErrorNumberType;
import CF.Resource;
import CF.ResourceFactoryOperations;
import CF.ResourceFactoryPackage.CreateResourceFailure;
import CF.ResourceFactoryPackage.ShutdownFailure;
import ExtendedCF.Sandbox;

/**
 * @since 4.0
 */
public abstract class AbstractResourceFactory implements ResourceFactoryOperations {

	@Override
	public Resource createResource(String resourceId, final DataType[] inputQualifiers) throws CreateResourceFailure {
		// Strip off and launch type, implementation ID if provided
		String mode = null;
		String implementation = null;
		List<DataType> qualifiers = new ArrayList<DataType>();
		for (final DataType t : inputQualifiers) {
			if (Sandbox.LAUNCH_TYPE.equals(t.id)) {
				final String value = t.value.extract_string();
				mode = value;
			} else if ("__implementationID".equals(t.id)) {
				final String value = t.value.extract_string();
				implementation = value;
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

		return createInstance(resourceId, qualifiers.toArray(new DataType[qualifiers.size()]), mode, implementation);
	}

	/**
	 * @since 9.0
	 */
	protected abstract Resource createInstance(String resourceId, DataType[] qualifiers, String launchMode, String implementation) throws CreateResourceFailure;

	@Override
	public void shutdown() throws ShutdownFailure {
		// TODO: SCA 2.2.2, 3.1.3.1.7.5.3.3 - factory should be unavailable to more CORBA calls
	}

}
