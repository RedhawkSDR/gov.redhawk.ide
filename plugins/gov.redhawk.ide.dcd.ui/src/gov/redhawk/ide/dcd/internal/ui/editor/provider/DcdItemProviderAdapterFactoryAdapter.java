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
package gov.redhawk.ide.dcd.internal.ui.editor.provider;

import mil.jpeojtrs.sca.dcd.provider.DcdItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;

/**
 * The Class ProcessorAdapterFactoryItemProvider.
 */
public class DcdItemProviderAdapterFactoryAdapter extends DcdItemProviderAdapterFactory {
	private Adapter deviceConfigurationAdapter;
	private Adapter componentPlacementAdapter;

	public Adapter getDeviceConfigurationAdapter() {
		return this.deviceConfigurationAdapter;
	}

	public void setDeviceConfigurationAdapter(final Adapter deviceConfigurationAdapter) {
		this.deviceConfigurationAdapter = deviceConfigurationAdapter;
	}

	public Adapter getComponentPlacementAdapter() {
		return this.componentPlacementAdapter;
	}

	public void setComponentPlacementAdapter(final Adapter placementAdapter) {
		this.componentPlacementAdapter = placementAdapter;
	}

	@Override
	public Adapter createDeviceConfigurationAdapter() {
		if (this.deviceConfigurationAdapter != null) {
			return this.deviceConfigurationAdapter;
		}
		return super.createDeviceConfigurationAdapter();
	}

	@Override
	public Adapter createComponentPlacementAdapter() {
		if (this.componentPlacementAdapter != null) {
			return this.componentPlacementAdapter;
		}
		return super.createComponentPlacementAdapter();
	}

}
