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

import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @since 6.0
 */
public class SpdCategory implements Category {
	
	private final SoftPkg softPkg;

	public SpdCategory(final SoftPkg softPkg) {
		this.softPkg = softPkg;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return this.softPkg.getName();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Category> getCategories() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Properties> getProperties() {
		return Collections.singletonList(this.softPkg.getPropertyFile().getProperties());
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsProperty(final EObject obj) {
		return EcoreUtil.isAncestor(this.softPkg.getPropertyFile().getProperties(), obj);
	}

}
