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
package gov.redhawk.ide.graphiti.sad.ui.diagram.providers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.create.ComponentCreateFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class GraphitiSADToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry componentCompartment;
	private PaletteCompartmentEntry workspaceCompartment;
	
	public GraphitiSADToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette Components with Target SDR Components
		addTargetSdrRefreshJob(getComponentsContainer());
	}

	@Override
	protected void refreshPalette() {
		refreshCompartmentEntry(componentCompartment, getComponentsContainer(), WaveformImageProvider.IMG_COMPONENT_PLACEMENT);

		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			refreshWorkspaceComponents();
		}
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		componentCompartment = new PaletteCompartmentEntry("Components", null);
		componentCompartment.setInitiallyOpen(true);
		compartments.add(componentCompartment);

		super.addPaletteCompartments(compartments);

		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			workspaceCompartment = new PaletteCompartmentEntry("Workspace", null);
			workspaceCompartment.setInitiallyOpen(true);
			compartments.add(workspaceCompartment);

			// Add a listener to refresh the palette when the workspace is updated
			IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
			registry.addListener(listener);
		} else {
			compartments.add(getAdvancedCompartmentEntry());
		}
	}

	/**
	 * Returns a populated CompartmentEntry containing all the Base Types and Uses Device tools
	 */
	private PaletteCompartmentEntry getAdvancedCompartmentEntry() {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Advanced", null);
		compartmentEntry.setInitiallyOpen(false);

		for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
			if (HostCollocationPattern.NAME.equals(cf.getCreateName()) || UsesDeviceFrontEndTunerPattern.NAME.equals(cf.getCreateName())) {
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
		}

		return compartmentEntry;
	}

	/**
	 * Rebuilds the workspace component palette entries
	 */
	private void refreshWorkspaceComponents() {
		workspaceCompartment.getToolEntries().clear();

		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry(); 
		for (ResourceDesc desc : registry.getResourceDescriptors()) {
			if (!"Workspace".equals(desc.getCategory())) {
				continue;
			}
			if (desc instanceof ComponentDesc) {
				ComponentDesc compDesc = (ComponentDesc) desc;

				// Filter out devices and services, and apply name filter
				if (ComponentType.RESOURCE.getLiteral().equals(compDesc.getComponentType())) {
					addToolToCompartment(workspaceCompartment, compDesc.getSoftPkg(), WaveformImageProvider.IMG_COMPONENT_PLACEMENT);
				}
			}
		}

		sort(workspaceCompartment.getToolEntries());
	}

	/**
	 * Returns a listener that refreshes the palette
	 */
	private PropertyChangeListener listener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			refreshPaletteJob.schedule(1000);
		}
	};

	@Override
	public void dispose() {
		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.removeListener(listener);
		super.dispose();
	}
	
	@Override
	protected ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId) {
		return new ComponentCreateFeature(getFeatureProvider(), spd, implId);
	}

	private ComponentsContainer getComponentsContainer() {
		return SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
	}
}
