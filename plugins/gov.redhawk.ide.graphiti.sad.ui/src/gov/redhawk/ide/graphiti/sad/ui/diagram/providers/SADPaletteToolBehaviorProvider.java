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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import gov.redhawk.ide.graphiti.sad.ui.diagram.features.create.ComponentCreateFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractPaletteToolBehaviorProvider;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class SADPaletteToolBehaviorProvider extends AbstractPaletteToolBehaviorProvider {

	private PaletteCompartmentEntry componentCompartment;

	public SADPaletteToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette Components with Target SDR Components
		addTargetSdrRefreshJob(getComponentsContainer());
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		componentCompartment = new PaletteCompartmentEntry("Components", null);
		componentCompartment.setInitiallyOpen(true);
		compartments.add(componentCompartment);
	}

	@Override
	protected void refreshPalette() {
		refreshCompartmentEntry(componentCompartment, getComponentsContainer(), WaveformImageProvider.IMG_COMPONENT_PLACEMENT);
	}

	@Override
	protected ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId) {
		return new ComponentCreateFeature(getFeatureProvider(), spd, implId);
	}

	private ComponentsContainer getComponentsContainer() {
		return SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
	}
}
