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
package gov.redhawk.ide.sdr.internal.ui.properties;

import gov.redhawk.sca.properties.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.EObject;

/**
 * @since 5.0
 */
public class ComponentCategory implements Category {

	private final List<SoftPkg> components;
	private final String name;
	private ComponentType type;

	/**
	 * @since 6.0
	 */
	public ComponentCategory(final List<SoftPkg> components, final String name, ComponentType type) {
		this.components = components;
		this.name = name;
		this.type = type;
	}

	public ComponentType getType() {
		return type;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getDescription() {
		switch (type) {
		case DEVICE:
			return "Devices installed in SDRROOT";
		case SERVICE:
			return "Services installed in SDRROOT";
		case RESOURCE:
			return "Components installed in SDRROOT";
		default:
			return "";
		}
	}

	@Override
	public String getIconPluginId() {
		return "gov.redhawk.ide.sdr.edit";
	}

	@Override
	public String getIconPath() {
		switch (type) {
		case DEVICE:
			return "icons/full/obj16/DevicesContainer.gif";
		case SERVICE:
			return "icons/full/obj16/ServicesContainer.gif";
		case RESOURCE:
		default:
			return "icons/full/obj16/ComponentsContainer.gif";
		}
	}

	@Override
	public List<Category> getCategories() {
		final List<Category> myList = new ArrayList<Category>();
		for (final SoftPkg softPkg : this.components) {
			myList.add(new SpdCategory(softPkg));
		}
		return myList;
	}

	@Override
	public List<Properties> getProperties() {
		return Collections.emptyList();
	}

	@Override
	public boolean containsProperty(final EObject obj) {
		for (final Category category : getCategories()) {
			if (category.containsProperty(obj)) {
				return true;
			}
		}
		return false;
	}
}
