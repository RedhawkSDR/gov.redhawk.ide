/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.dcd.ui.diagram.providers;

import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.DeviceCreateFeature;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.features.create.ServiceCreateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

public class GraphitiDCDToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry deviceCompartment;
	private PaletteCompartmentEntry serviceCompartment;
	
	public GraphitiDCDToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette with Target SDR contents
		addTargetSdrRefreshJob(getDevicesContainer());
		addTargetSdrRefreshJob(getServicesContainer());
	}

	@Override
	protected void refreshPalette() {
		refreshCompartmentEntry(deviceCompartment, getDevicesContainer(), NodeImageProvider.IMG_SCA_DEVICE);
		refreshCompartmentEntry(serviceCompartment, getServicesContainer(), NodeImageProvider.IMG_SCA_SERVICE);
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		// DEVICES compartment
		deviceCompartment = new PaletteCompartmentEntry("Devices", null);
		compartments.add(deviceCompartment);

		// SERVICES compartment
		serviceCompartment = new PaletteCompartmentEntry("Services", null);
		compartments.add(serviceCompartment);

		// Get FindBy from superclass
		super.addPaletteCompartments(compartments);
	}

	@Override
	protected ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId) {
		if (iconId == NodeImageProvider.IMG_SCA_DEVICE) {
			return new DeviceCreateFeature(getFeatureProvider(), spd, implId);
		}
		if (iconId == NodeImageProvider.IMG_SCA_SERVICE) {
			return new ServiceCreateFeature(getFeatureProvider(), spd, implId);
		}
		return null;
	}

	private DevicesContainer getDevicesContainer() {
		return SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer();
	}

	private ServicesContainer getServicesContainer() {
		return SdrUiPlugin.getDefault().getTargetSdrRoot().getServicesContainer();
	}
}
