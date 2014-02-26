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
package gov.redhawk.ide.sdr.util;

import gov.redhawk.ide.sdr.IdeSdrActivator;

import java.util.HashMap;
import java.util.Map;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.runtime.CoreException;

/**
 * @since 3.2
 * 
 */
public final class ScaEnvironmentUtil {

	private ScaEnvironmentUtil() {
		
	}
	
	/**
	 * @deprecated Use {@link #getLauncherEnvMap(Implementation)} instead.
	 * @return
	 */
	@Deprecated
	public static Map<String, String> getLauncherEnvMap() {
		try {
			return getLauncherEnvMap(null);
		} catch (CoreException e) {
			// This should never happen since the impl is null
			throw new IllegalStateException("Core Exception when loading envirornment map.", e);
		}
	}
	
	/**
	 * 
	 * @param impl
	 * @return
	 * @throws CoreException 
	 * @since 8.1
	 */
	public static Map<String, String> getLauncherEnvMap(Implementation impl) throws CoreException {
		final Map<String, String> retVal = new HashMap<String, String>();
		for (IEnvMap map : IdeSdrActivator.getDefault().getEnvMapServices()) {
			if (map.handles(impl)) {
				map.initEnv(impl, retVal);
			}
		}
		
		return retVal;
	}

}
