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
package gov.redhawk.ide.codegen.ui;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.debug.ui.AbstractLaunchWorkspaceComponentShortcut;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * @since 7.0
 * 
 */
public abstract class AbstractLaunchCodegenShortcut extends AbstractLaunchWorkspaceComponentShortcut {
	@Override
	protected Collection<Implementation> getSupportedImplementations(final SoftPkg spd, final String mode) {
		final List<Implementation> impls = new ArrayList<Implementation>();
		for (final Implementation impl : spd.getImplementation()) {
			final ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
			if (settings.getGeneratorId().equals(getGeneratorID())) {
				impls.add(impl);
			}
		}
		return impls;
	}

	protected abstract String getGeneratorID();
}
