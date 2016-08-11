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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;

import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import mil.jpeojtrs.sca.scd.ComponentType;

public class WaveformSandboxToolBehaviorProvider extends SADPaletteToolBehaviorProvider {

	private static final String CATEGORY_WORKSPACE = "Workspace";

	/**
	 * A listener that refreshes the palette when a workspace resource is added/changed/removed
	 */
	private PropertyChangeListener workspaceResourceListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			// Trigger a refresh
			if (IResourceFactoryRegistry.PROP_RESOURCES.equals(evt.getPropertyName())) {
				ResourceDesc resourceDesc = (ResourceDesc) evt.getOldValue();
				if (resourceDesc != null && CATEGORY_WORKSPACE.equals(resourceDesc.getCategory())) {
					refreshPaletteAsync();
					return;
				}
				resourceDesc = (ResourceDesc) evt.getNewValue();
				if (resourceDesc != null && CATEGORY_WORKSPACE.equals(resourceDesc.getCategory())) {
					refreshPaletteAsync();
					return;
				}
			}
		}
	};

	private PaletteCompartmentEntry workspaceCompartment;

	public WaveformSandboxToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public void dispose() {
		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.removeListener(workspaceResourceListener);
		super.dispose();
	}

	@Override
	protected void refreshPalette() {
		super.refreshPalette();
		refreshWorkspaceComponents();
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		super.addPaletteCompartments(compartments);

		workspaceCompartment = new PaletteCompartmentEntry("Workspace", null);
		workspaceCompartment.setInitiallyOpen(true);
		compartments.add(workspaceCompartment);

		// Add a listener to refresh the palette when the workspace is updated
		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.addListener(workspaceResourceListener);
	}

	/**
	 * Rebuilds the workspace component palette entries
	 */
	private void refreshWorkspaceComponents() {
		workspaceCompartment.getToolEntries().clear();

		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		for (ResourceDesc desc : registry.getResourceDescriptors()) {
			if (!CATEGORY_WORKSPACE.equals(desc.getCategory())) {
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
}
