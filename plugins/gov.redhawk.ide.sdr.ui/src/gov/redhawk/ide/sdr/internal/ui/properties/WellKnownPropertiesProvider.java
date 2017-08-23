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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.sca.properties.Category;
import gov.redhawk.sca.properties.IPropertiesProvider;
import mil.jpeojtrs.sca.prf.Properties;

public class WellKnownPropertiesProvider implements IPropertiesProvider {

	private static final String EP_ID = "wellKnownProperties";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_ICON = "icon";
	private static final String ATTR_PRF = "prf";

	private ResourceSet resourceSet = new ResourceSetImpl();
	private List<Category> categories = new ArrayList<>();

	public WellKnownPropertiesProvider() {
		IConfigurationElement[] configElements = Platform.getExtensionRegistry().getConfigurationElementsFor(SdrUiPlugin.PLUGIN_ID, EP_ID);
		for (IConfigurationElement configElement : configElements) {
			String pluginId = configElement.getContributor().getName();
			String name = configElement.getAttribute(ATTR_NAME);
			IConfigurationElement[] descriptionElements = configElement.getChildren("description");
			String description = "";
			if (descriptionElements.length == 1) {
				description = descriptionElements[0].getValue();
			}
			String icon = configElement.getAttribute(ATTR_ICON);
			String prf = configElement.getAttribute(ATTR_PRF);
			Resource resource = resourceSet.getResource(URI.createPlatformPluginURI("/" + pluginId + "/" + prf, true), true);
			Properties props = Properties.Util.getProperties(resource);
			if (props != null) {
				categories.add(new PropertiesCategory(name, description, pluginId, icon, props));
			}
		}
	}

	@Override
	public String getName() {
		return "Well-known properties";
	}

	@Override
	public String getDescription() {
		return "Properties that have pre-established meaning in REDHAWK or other platforms";
	}

	@Override
	public String getIconPluginId() {
		return "org.eclipse.ui.views";
	}

	@Override
	public String getIconPath() {
		return "icons/full/eview16/prop_ps.png";
	}

	@Override
	public List<Category> getCategories() {
		return Collections.unmodifiableList(categories);
	}
}
