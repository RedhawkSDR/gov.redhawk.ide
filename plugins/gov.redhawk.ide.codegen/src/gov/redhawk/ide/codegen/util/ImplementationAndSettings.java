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
package gov.redhawk.ide.codegen.util;

import gov.redhawk.ide.codegen.ImplementationSettings;
import mil.jpeojtrs.sca.spd.Implementation;

/**
 * This is a simple container class that pairs an {@link Implementation} and its
 * {@link ImplementationSettings}.
 * 
 * @since 7.0
 */
public class ImplementationAndSettings {
	private final Implementation implementation;
	private final ImplementationSettings implementationSettings;

	public ImplementationAndSettings(final Implementation i, final ImplementationSettings is) {
		this.implementation = i;
		this.implementationSettings = is;
	}

	public Implementation getImplementation() {
		return this.implementation;
	}

	public ImplementationSettings getImplementationSettings() {
		return this.implementationSettings;
	}
}
