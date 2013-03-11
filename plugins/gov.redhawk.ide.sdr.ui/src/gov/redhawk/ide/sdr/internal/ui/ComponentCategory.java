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
package gov.redhawk.ide.sdr.internal.ui;

import gov.redhawk.sca.properties.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.EObject;

/**
 * @since 5.0
 */
public class ComponentCategory implements Category {

	private final List<SoftPkg> components;
	private final String name;

	/**
	 * @since 6.0
	 */
	public ComponentCategory(final List<SoftPkg> components, final String name) {
		this.components = components;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Category> getCategories() {
		final List<Category> myList = new ArrayList<Category>();
		for (final SoftPkg softPkg : this.components) {
			myList.add(new SpdCategory(softPkg));
		}
		return myList;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Properties> getProperties() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsProperty(final EObject obj) {
		for (final Category category : getCategories()) {
			if (category.containsProperty(obj)) {
				return true;
			}
		}
		return false;
	}

}
