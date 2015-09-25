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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDoubleClickContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.create.ComponentCreateFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.UsesDeviceEditFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.features.custom.UsesFrontEndDeviceEditFeature;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.UsesDeviceFrontEndTunerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class GraphitiSADToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry componentCompartment;
	private PaletteCompartmentEntry workspaceCompartment;
	
	public GraphitiSADToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette Components with Target SDR Components
		addComponentContainerRefreshJob(diagramTypeProvider);
	}

	/**
	 * Add a refresh listener job that will refresh the palette of the provided diagramTypeProvider
	 * every time Target SDR refreshes
	 * @param diagramTypeProvider
	 */
	private void addComponentContainerRefreshJob(final IDiagramTypeProvider diagramTypeProvider) {

		final ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		container.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					// refresh palette which will call GraphitiSADToolBehaviorProvider.getPalette()
					diagramTypeProvider.getDiagramBehavior().refreshPalette();
					return Status.OK_STATUS;
				}

			};

			@Override
			public void notifyChanged(final Notification msg) {
				super.notifyChanged(msg);
				switch (msg.getFeatureID(ComponentsContainer.class)) {
				case SdrPackage.COMPONENTS_CONTAINER__COMPONENTS:
					this.refreshPalletteJob.schedule(1000); // SUPPRESS CHECKSTYLE MagicNumber
					break;
				default:
					break;
				}
			}
		});
	}

	@Override
	protected void refreshPalette() {
		ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		refreshCompartmentEntry(componentCompartment, container, WaveformImageProvider.IMG_COMPONENT_PLACEMENT);

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
			if (IResourceFactoryRegistry.PROP_RESOURCES.equals(evt.getPropertyName())) {
				WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor) {
						// refresh palette which will call RHToolBehaviorProvider.getPalette()
						getDiagramTypeProvider().getDiagramBehavior().refreshPalette();
						return Status.OK_STATUS;
					}

				};
				refreshPalletteJob.schedule(1000);
			}
		}
	};

	@Override
	public void dispose() {
		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.removeListener(listener);
		super.dispose();
	}
	
	@Override
	public IContextMenuEntry[] getContextMenu(ICustomContext context) {
		List<IContextMenuEntry> contextMenuItems = new ArrayList<IContextMenuEntry>();

		ICustomFeature[] customFeatures = getFeatureProvider().getCustomFeatures(context);
		for (ICustomFeature customFeature : customFeatures) {
			ContextMenuEntry entry = new ContextMenuEntry(customFeature, context);
			if (customFeature instanceof LogLevelFeature) {
				// Create a sub-menu for logging
				ContextMenuEntry loggingSubMenu = new ContextMenuEntry(null, context);
				loggingSubMenu.setText("Logging");
				loggingSubMenu.setDescription("Logging");
				loggingSubMenu.setSubmenu(true);
				contextMenuItems.add(0, loggingSubMenu);

				// Make the log level feature a sub-entry of this menu
				loggingSubMenu.add(entry);
			} else {
				contextMenuItems.add(entry);
			}
		}

        return contextMenuItems.toArray(new IContextMenuEntry[contextMenuItems.size()]);
	}

	@Override
	public ICustomFeature getDoubleClickFeature(IDoubleClickContext context) {
		//UsesFrontEndDeviceEditFeature
		ICustomFeature usesFrontEndDeviceEditFeature = new UsesFrontEndDeviceEditFeature(getFeatureProvider());
		if (usesFrontEndDeviceEditFeature.canExecute(context)) {
			return usesFrontEndDeviceEditFeature;
		}
		
		//UsesDeviceEditFeature
		ICustomFeature usesDeviceEditFeature = new UsesDeviceEditFeature(getFeatureProvider());
		if (usesDeviceEditFeature.canExecute(context)) {
			return usesDeviceEditFeature;
		}

		return super.getDoubleClickFeature(context);
	}

	@Override
	protected ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId) {
		return new ComponentCreateFeature(getFeatureProvider(), spd, implId);
	}
	
}
