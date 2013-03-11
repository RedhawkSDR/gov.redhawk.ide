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

import java.util.ArrayList;
import java.util.Collection;

import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * The Class SoftPkgItemProvider.
 */
public class OverviewSectionSoftPkgItemProvider extends mil.jpeojtrs.sca.spd.provider.SoftPkgItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public OverviewSectionSoftPkgItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection< ? extends EStructuralFeature> getChildrenFeatures(final Object object) {
		if (this.childrenFeatures == null) {
			this.childrenFeatures = new ArrayList<EStructuralFeature>();
			this.childrenFeatures.add(SpdPackage.Literals.SOFT_PKG__AUTHOR);
		}
		return this.childrenFeatures;
	}

}
