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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

public class GraphitiSADToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry componentCompartmentEntry;
	private PaletteCompartmentEntry workspaceCompartmentEntry;
	
	public GraphitiSADToolBehaviorProvider(final IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette Components with Target SDR Components
		addComponentContainerRefreshJob(diagramTypeProvider);

	}

	@Override
	public boolean isShowFlyoutPalette() {
		if (DUtil.isDiagramExplorer(getFeatureProvider().getDiagramTypeProvider().getDiagram())) {
			return false;
		}
		return super.isShowFlyoutPalette();
	}
	
	/**
	 * Returns true if the business objects are equal. Overriding this because default implementation
	 * doesn't check the objects container and in some cases when attempting to automatically create connections
	 * in the diagram they are drawn to the wrong ports.
	 */
	@Override
	public boolean equalsBusinessObjects(Object o1, Object o2) {

		if (o1 instanceof ProvidesPortStub && o2 instanceof ProvidesPortStub) {
			ProvidesPortStub ps1 = (ProvidesPortStub) o1;
			ProvidesPortStub ps2 = (ProvidesPortStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(ps1, ps2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(ps1.eContainer(), ps2.eContainer());
			}
		} else if (o1 instanceof UsesPortStub && o2 instanceof UsesPortStub) {
			UsesPortStub ps1 = (UsesPortStub) o1;
			UsesPortStub ps2 = (UsesPortStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(ps1, ps2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(ps1.eContainer(), ps2.eContainer());
			}
		} else if (o1 instanceof ComponentSupportedInterfaceStub && o2 instanceof ComponentSupportedInterfaceStub) {
			ComponentSupportedInterfaceStub obj1 = (ComponentSupportedInterfaceStub) o1;
			ComponentSupportedInterfaceStub obj2 = (ComponentSupportedInterfaceStub) o2;
			boolean ecoreEqual = EcoreUtil.equals(obj1, obj2);
			if (ecoreEqual) {
				// ecore says they are equal, but lets verify their containers are the same
				return this.equalsBusinessObjects(obj1.eContainer(), obj2.eContainer());
			}
		}

		if (o1 instanceof EObject && o2 instanceof EObject) {
			return EcoreUtil.equals((EObject) o1, (EObject) o2);
		}
		// Both BOs have to be EMF objects. Otherwise the IndependenceSolver does the job.
		return false;
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
		final ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		getComponentCompartmentEntry(container);

		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			getWorkspaceCompartmentEntry();
		}
	}

	@Override
	protected void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments) {
		componentCompartmentEntry = new PaletteCompartmentEntry("Components", null);
		componentCompartmentEntry.setInitiallyOpen(true);
		compartments.add(componentCompartmentEntry);

		super.addPaletteCompartments(compartments);

		if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
			workspaceCompartmentEntry = new PaletteCompartmentEntry("Workspace", null);
			workspaceCompartmentEntry.setInitiallyOpen(true);
			compartments.add(workspaceCompartmentEntry);

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
	 * Returns a populated CompartmentEntry containing all components in Target SDR
	 * @param container
	 */
	private PaletteCompartmentEntry getComponentCompartmentEntry(final ComponentsContainer container) {
		componentCompartmentEntry.getToolEntries().clear();

		// loop through all components in Target SDR and if present then remove it from the entriesToRemove list
		final EList<SoftPkg> components = container.getComponents();
		final SoftPkg[] componentsArray = components.toArray(new SoftPkg[components.size()]);
		spdLoop: for (final SoftPkg spd : componentsArray) {
			for (Implementation impl : spd.getImplementation()) {
				Code code = impl.getCode();
				if (code == null) {
					continue spdLoop;
				}
				CodeFileType type = code.getType();
				if (type == null) {
					continue spdLoop;
				}
				switch (type) {
				case EXECUTABLE:
					break;
				default:
					continue spdLoop;
				}
			}
			addToolToCompartment(componentCompartmentEntry, spd, WaveformImageProvider.IMG_COMPONENT_PLACEMENT);
		}

		// Sort the children and return the new list of components
		sort(componentCompartmentEntry.getToolEntries());
		return componentCompartmentEntry;
	}

	/**
	 * Returns a populated CompartmentEntry containing all components in Workspace
	 * @param container
	 */
	private PaletteCompartmentEntry getWorkspaceCompartmentEntry() {
		workspaceCompartmentEntry.getToolEntries().clear();

		final String componentType = mil.jpeojtrs.sca.scd.ComponentType.RESOURCE.getLiteral();
		for (ResourceDesc desc : ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry().getResourceDescriptors()) {
			if (!"Workspace".equals(desc.getCategory())) {
				continue;
			}
			if (desc instanceof ComponentDesc) {
				ComponentDesc compDesc = (ComponentDesc) desc;
				// Filter out devices and services, and apply name filter
				if ((compDesc.getComponentType() != null) && compDesc.getComponentType().equals(componentType)) {
					addToolToCompartment(workspaceCompartmentEntry, compDesc.getSoftPkg(), WaveformImageProvider.IMG_COMPONENT_PLACEMENT);
				}
			}
		}

		sort(workspaceCompartmentEntry.getToolEntries());
		return workspaceCompartmentEntry;
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
