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

import gov.redhawk.ide.debug.ILauncherVariableResolver;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;

import java.util.List;

import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.common.util.URI;

/**
 * Provides the profile name (path to the spd.xml file rooted in the domain/device manager file system).
 */
public class ProfileNameVariableResolver extends AbstractLauncherResolver implements ILauncherVariableResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {
		SoftwareComponent scd = null;
		if (spd.getDescriptor() != null) {
			scd = spd.getDescriptor().getComponent();
		}
		ComponentType type = SoftwareComponent.Util.getWellKnownComponentType(scd);
		String root;
		switch (type) {
		case SERVICE:
			root = "services";
			break;
		case DEVICE:
			root = "devices";
			break;
		case RESOURCE:
			root = "components";
			break;
		default:
			root = "components";
			break;
		}
		URI uri = spd.eResource().getURI();
		List<String> list = uri.segmentsList();
		if (uri.toString().contains(root)) {
			List<String> subList = list.subList(list.indexOf(root), list.size()); 
			return "/" + StringUtils.join(subList, "/");
		} else if (list.size() >= 2) {
			return "/" + root + "/" + list.get(list.size() - 2) + "/" + list.get(list.size() - 1);
		} else {
			return "/" + root + "/" + list.get(list.size() - 1);
		}
	}
}
