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

import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ILabelProvider;

public class SpdCategory implements Category {

	private SoftPkg softPkg;
	private ILabelProvider labelProvider;

	public SpdCategory(final SoftPkg softPkg, ILabelProvider labelProvider) {
		this.softPkg = softPkg;
		this.labelProvider = labelProvider;
	}

	@Override
	public String getName() {
		return labelProvider.getText(softPkg);
	}

	@Override
	public String getDescription() {
		return softPkg.getDescription();
	}

	@Override
	public String getIconPluginId() {
		return "mil.jpeojtrs.sca.spd.edit";
	}

	@Override
	public String getIconPath() {
		return "icons/full/obj16/SoftPkg.gif";
	}

	@Override
	public List<Category> getCategories() {
		return Collections.emptyList();
	}

	@Override
	public List<Properties> getProperties() {
		Properties properties = ScaEcoreUtils.getFeature(softPkg, PATH);
		if (properties == null) {
			return Collections.emptyList();
		} else {
			return Collections.singletonList(properties);
		}
	}

	private static final EStructuralFeature[] PATH = new EStructuralFeature[] { SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
		SpdPackage.Literals.PROPERTY_FILE__PROPERTIES };

	@Override
	public boolean containsProperty(final EObject obj) {
		Properties properties = ScaEcoreUtils.getFeature(softPkg, PATH);
		if (properties != null && obj != null) {
			return EcoreUtil.isAncestor(properties, obj);
		} else {
			return false;
		}
	}
}
