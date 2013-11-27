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
package gov.redhawk.ide.sdr.ui.internal.handlers;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

/**
 * 
 */
public class LaunchDomainManagerDialogLabelProvider extends AdapterFactoryLabelProvider {

	public LaunchDomainManagerDialogLabelProvider() {
		super(new DcdItemProviderAdapterFactory());
	}

	@Override
	public String getText(final Object object) {
		if (object instanceof DeviceConfiguration) {
			final DeviceConfiguration dcd = (DeviceConfiguration) object;
			final URI uri = dcd.eResource().getURI();
			return dcd.getName() + " (" + uri.path().replace(uri.lastSegment(), "") + ")";
		}
		return super.getText(object);
	}
}
