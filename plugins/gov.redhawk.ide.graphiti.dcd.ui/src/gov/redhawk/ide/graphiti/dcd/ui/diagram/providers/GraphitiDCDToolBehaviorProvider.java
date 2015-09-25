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
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.ui.progress.WorkbenchJob;

public class GraphitiDCDToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry deviceCompartment;
	private PaletteCompartmentEntry serviceCompartment;
	
	public GraphitiDCDToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette with Target SDR contents
		addTargetSdrRefreshJob(diagramTypeProvider);
	}

	private void addTargetSdrRefreshJob(final IDiagramTypeProvider diagramTypeProvider) {
		final DevicesContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer();
		container.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					// refresh palette which will call GraphitiDCDToolBehaviorProvider.getPalette()
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
		DevicesContainer devicesContainer = SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer();
		refreshCompartmentEntry(deviceCompartment, devicesContainer, NodeImageProvider.IMG_SCA_DEVICE);

		ServicesContainer servicesContainer = SdrUiPlugin.getDefault().getTargetSdrRoot().getServicesContainer();
		refreshCompartmentEntry(serviceCompartment, servicesContainer, NodeImageProvider.IMG_SCA_SERVICE);
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
	protected ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId) {
		if (iconId == NodeImageProvider.IMG_SCA_DEVICE) {
			return new DeviceCreateFeature(getFeatureProvider(), spd, implId);
		}
		if (iconId == NodeImageProvider.IMG_SCA_SERVICE) {
			return new ServiceCreateFeature(getFeatureProvider(), spd, implId);
		}
		return null;
	}
}
