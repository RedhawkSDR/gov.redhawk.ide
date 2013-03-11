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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;

/**
 * 
 */
public class WaveDevItemProviderAdapterFactoryAdapter extends CodegenItemProviderAdapterFactory {

	private Adapter implementationSettingsAdapter;
	private Adapter propertyAdapter;
	private Adapter waveDevSettingsAdapter;

	/**
	 * @return the implementationSettingsAdapter
	 */
	public Adapter getImplementationSettingsAdapter() {
		return this.implementationSettingsAdapter;
	}

	/**
	 * @param implementationSettingsAdapter the implementationSettingsAdapter to
	 *            set
	 */
	public void setImplementationSettingsAdapter(final Adapter implementationSettingsAdapter) {
		this.implementationSettingsAdapter = implementationSettingsAdapter;
	}

	/**
	 * @return the propertyAdapter
	 */
	public Adapter getPropertyAdapter() {
		return this.propertyAdapter;
	}

	/**
	 * @param propertyAdapter the propertyAdapter to set
	 */
	public void setPropertyAdapter(final Adapter propertyAdapter) {
		this.propertyAdapter = propertyAdapter;
	}

	/**
	 * @return the waveDevSettingsAdapter
	 */
	public Adapter getWaveDevSettingsAdapter() {
		return this.waveDevSettingsAdapter;
	}

	/**
	 * @param waveDevSettingsAdapter the waveDevSettingsAdapter to set
	 */
	public void setWaveDevSettingsAdapter(final Adapter waveDevSettingsAdapter) {
		this.waveDevSettingsAdapter = waveDevSettingsAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createImplementationSettingsAdapter() {
		if (this.implementationSettingsAdapter != null) {
			return this.implementationSettingsAdapter;
		}
		return super.createImplementationSettingsAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createPropertyAdapter() {
		if (this.propertyAdapter != null) {
			return this.propertyAdapter;
		}
		return super.createPropertyAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createWaveDevSettingsAdapter() {
		if (this.waveDevSettingsAdapter != null) {
			return this.waveDevSettingsAdapter;
		}
		return super.createWaveDevSettingsAdapter();
	}

}
