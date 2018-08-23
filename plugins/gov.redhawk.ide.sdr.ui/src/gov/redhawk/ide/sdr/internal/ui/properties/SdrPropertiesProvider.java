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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.TargetSdrRoot;
import gov.redhawk.ide.sdr.provider.SdrItemProviderAdapterFactory;
import gov.redhawk.sca.properties.Category;
import gov.redhawk.sca.properties.IPropertiesProvider;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

public class SdrPropertiesProvider implements IPropertiesProvider {

	public SdrPropertiesProvider() {
		SdrRoot targetSdr = TargetSdrRoot.getSdrRoot();
		targetSdr.load(null);
	}

	@Override
	public String getName() {
		return "Target SDR";
	}

	@Override
	public String getDescription() {
		return "Properties from software installed in the SDRROOT";
	}

	@Override
	public String getIconPluginId() {
		return "gov.redhawk.ide.sdr.edit";
	}

	@Override
	public String getIconPath() {
		return "icons/full/obj16/SdrRoot.gif";
	}

	@Override
	public List<Category> getCategories() {
		SdrRoot targetSdr = TargetSdrRoot.getSdrRoot();
		ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(Arrays.asList(new SdrItemProviderAdapterFactory(), new SpdItemProviderAdapterFactory()));
		AdapterFactoryLabelProvider labelProvider = new AdapterFactoryLabelProvider(adapterFactory);

		final List<Category> myList = new ArrayList<Category>();
		myList.add(new ComponentCategory(targetSdr.getComponentsContainer(), ComponentType.RESOURCE, labelProvider));
		myList.add(new ComponentCategory(targetSdr.getDevicesContainer(), ComponentType.DEVICE, labelProvider));
		myList.add(new ComponentCategory(targetSdr.getServicesContainer(), ComponentType.SERVICE, labelProvider));
		return myList;
	}
}
