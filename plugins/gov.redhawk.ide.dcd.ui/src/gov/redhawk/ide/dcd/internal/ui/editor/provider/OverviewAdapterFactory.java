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

import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;

/**
 * 
 */
public class OverviewAdapterFactory extends SpdItemProviderAdapterFactory {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createSoftPkgAdapter() {
		if (this.softPkgItemProvider == null) {
			this.softPkgItemProvider = new OverviewSectionSoftPkgItemProvider(this);
		}

		return this.softPkgItemProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createAuthorAdapter() {
		if (this.authorItemProvider == null) {
			this.authorItemProvider = new OverviewSectionAuthorItemProvider(this);
		}

		return this.authorItemProvider;
	}
}
