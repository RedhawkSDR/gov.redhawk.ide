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

import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.provider.OsItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;

/**
 * The Class ImplementationDetailsSectionOsItemProvider.
 */
public class ImplementationDetailsSectionOsItemProvider extends OsItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public ImplementationDetailsSectionOsItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTextGen(final Object object) {
		final String label = ((Os) object).getName();
		if (label == null || label.length() == 0) {
			return getString("_UI_Os_type");
		} else {
			return label;
		}
	}

}
