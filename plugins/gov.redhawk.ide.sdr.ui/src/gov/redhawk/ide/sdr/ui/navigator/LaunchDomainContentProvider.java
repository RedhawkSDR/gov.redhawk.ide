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
package gov.redhawk.ide.sdr.ui.navigator;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * @since 4.2
 */
public class LaunchDomainContentProvider extends SdrNavigatorContentProvider {
	@Override
	public boolean hasChildren(Object object) {
		if (object instanceof DeviceConfiguration) {
			return false;
		}
		return super.hasChildren(object);
	}
}
