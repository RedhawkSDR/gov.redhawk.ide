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
package gov.redhawk.ide.spd.internal.ui.editor.provider;

import org.eclipse.emf.common.notify.AdapterFactory;

/**
 * The Class AuthorItemProvider.
 */
public class OverviewSectionAuthorItemProvider extends mil.jpeojtrs.sca.spd.provider.AuthorItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public OverviewSectionAuthorItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(final Object object) {
		return super.getText(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object object, final int columnIndex) {
		return getText(object);
	}

}
