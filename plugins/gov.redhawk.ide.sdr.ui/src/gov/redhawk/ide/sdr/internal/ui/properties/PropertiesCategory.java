/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.sdr.internal.ui.properties;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import gov.redhawk.sca.properties.Category;
import mil.jpeojtrs.sca.prf.Properties;

public class PropertiesCategory implements Category {

	private String name;
	private String description;
	private String iconPluginId;
	private String iconPath;
	private Properties props;

	public PropertiesCategory(String name, String description, String iconPluginId, String iconPath, Properties props) {
		this.name = name;
		this.description = description;
		this.iconPluginId = iconPluginId;
		this.iconPath = iconPath;
		this.props = props;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public String getIconPluginId() {
		return iconPluginId;
	}

	public String getIconPath() {
		return iconPath;
	}

	@Override
	public List<Category> getCategories() {
		return Collections.emptyList();
	}

	@Override
	public List<Properties> getProperties() {
		return Collections.singletonList(props);
	}

	@Override
	public boolean containsProperty(EObject obj) {
		return EcoreUtil.isAncestor(props, obj);
	}
}
