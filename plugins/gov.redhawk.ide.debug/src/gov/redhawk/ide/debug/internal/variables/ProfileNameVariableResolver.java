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
package gov.redhawk.ide.debug.internal.variables;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides the profile name (path to the spd.xml file within the appropriate CF:FileSystem).
 * @see gov.redhawk.core.resourcefactory.ResourceDesc#createProfile()
 */
public class ProfileNameVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {
		// Note: We generally only expect file URIs, which are simply mapped to an absolute file path
		URI uri = spd.eResource().getURI();
		if (uri.isFile()) {
			return uri.toFileString();
		} else if (uri.isPlatformPlugin()) {
			return new Path("/bundle").append(uri.toPlatformString(true)).toString();
		} else {
			return new Path(uri.scheme()).append(uri.path()).toString();
		}
	}
}
