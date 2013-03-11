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

import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.provider.PropertyItemProvider;

import org.eclipse.emf.common.notify.AdapterFactory;

/**
 * 
 */
public class CustomPropertyItemProvider extends PropertyItemProvider {

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 */
	public CustomPropertyItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getColumnText(final Object object, final int columnIndex) {
		switch (columnIndex) {
		case 0:
			return ((Property) object).getId();
		case 1:
			return ((Property) object).getValue();
		default:
			return "";
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getColumnImage(final Object object, final int columnIndex) {
		if (columnIndex == 0) {
			return getImage(object);
		}
		return null;
	}
}
