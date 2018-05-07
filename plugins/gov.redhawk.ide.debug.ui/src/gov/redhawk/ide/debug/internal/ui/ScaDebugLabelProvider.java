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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.provider.ScaDebugItemProviderAdapterFactory;
import gov.redhawk.sca.ui.ScaLabelProvider;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

/**
 * Label provider for the SCA debug model. Used by the REDHAWK Explorer common navigator.
 */
public class ScaDebugLabelProvider extends ScaLabelProvider {

	public ScaDebugLabelProvider() {
		super(ScaDebugLabelProvider.createAdapterFactory());
	}

	protected static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new ScaDebugItemProviderAdapterFactory());
		factory.addAdapterFactory(ScaLabelProvider.createAdapterFactory());
		return factory;
	}

}
