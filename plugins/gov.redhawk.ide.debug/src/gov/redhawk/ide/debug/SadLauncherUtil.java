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

import java.util.Collections;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * @since 3.0
 */
public final class SadLauncherUtil {
	private SadLauncherUtil() {

	}

	@SuppressWarnings("unchecked")
	public static Map<String, String> getImplementationMap(final ILaunchConfiguration config) throws CoreException {
		if (config == null) {
			return Collections.emptyMap();
		}
		return config.getAttribute(ScaDebugLaunchConstants.ATT_LW_IMPLS, Collections.emptyMap());
	}
}
