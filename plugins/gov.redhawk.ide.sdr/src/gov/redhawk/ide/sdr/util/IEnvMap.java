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

import java.util.Map;

import mil.jpeojtrs.sca.spd.Implementation;

import org.eclipse.core.runtime.CoreException;

/**
 * @since 8.2
 * 
 */
public interface IEnvMap {

	boolean handles(Implementation impl) throws CoreException;

	void initEnv(Implementation impl, Map<String, String> retVal) throws CoreException;

}
