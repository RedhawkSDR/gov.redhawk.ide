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
package gov.redhawk.ide.sdr.internal.util;

import gov.redhawk.ide.sdr.util.AbstractEnvMap;

import java.io.File;
import java.util.Map;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.runtime.CoreException;

public class OssieHomeEnvMap extends AbstractEnvMap {

	@Override
	public boolean handles(Implementation impl) {
		return true;
	}

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("OSSIEHOME", "${OssieHome}");
		envMap.put("SDRROOT", "${SdrRoot}");
		envMap.put("PATH", "${OssieHome}/bin" + File.pathSeparator + "${env_var:PATH}");
	}

}
