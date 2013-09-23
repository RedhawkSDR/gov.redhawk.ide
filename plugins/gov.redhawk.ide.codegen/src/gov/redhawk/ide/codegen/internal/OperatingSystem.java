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
package gov.redhawk.ide.codegen.internal;

import gov.redhawk.ide.codegen.IOperatingSystem;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * @since 2.0
 */
public class OperatingSystem implements IOperatingSystem {
	private static final String ATTR_OPERATING_SYSTEM = "os";

	private static final String ATTR_VERSION = "version";

	private static final String ATTR_IS_DEFAULT_OS = "default";

	private final String os;

	private final String version;

	private final boolean isDefault;

	public OperatingSystem(final IConfigurationElement element) {
		this.os = element.getAttribute(OperatingSystem.ATTR_OPERATING_SYSTEM);
		this.version = element.getAttribute(OperatingSystem.ATTR_VERSION);
		this.isDefault = Boolean.valueOf(element.getAttribute(OperatingSystem.ATTR_IS_DEFAULT_OS));
	}

	@Override
	public String getName() {
		return this.os;
	}

	@Override
	public String getVersion() {
		return this.version;
	}

	@Override
	public boolean isDefault() {
		return this.isDefault;
	}

}
