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
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DCDConnectInterfacePattern;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.LogLevelFeature;
import gov.redhawk.ide.graphiti.ui.diagram.palette.SpdToolEntry;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.impl.CustomContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.custom.ICustomFeature;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.eclipse.graphiti.tb.ContextButtonEntry;
import org.eclipse.graphiti.tb.ContextMenuEntry;
import org.eclipse.graphiti.tb.IContextButtonPadData;
import org.eclipse.graphiti.tb.IContextMenuEntry;
import org.eclipse.ui.progress.WorkbenchJob;

public class GraphitiDCDToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	private PaletteCompartmentEntry deviceCompartment;
	private PaletteCompartmentEntry serviceCompartment;
	private PaletteCompartmentEntry advancedCompartment;
	
	public GraphitiDCDToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette with Target SDR contents
		addTargetSdrRefreshJob(diagramTypeProvider);
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

	/** This is where we define the different compartments in the palette, and populate the palette entries */
	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		List<IPaletteCompartmentEntry> compartments = new ArrayList<IPaletteCompartmentEntry>();

		// DEVICES compartment
		final DevicesContainer devicesContainer = SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer();
		PaletteCompartmentEntry devicesCompartmentEntry = getCompartmentEntry(devicesContainer);
		compartments.add(devicesCompartmentEntry);

		// SERVICES compartment
		final ServicesContainer servicesContainer = SdrUiPlugin.getDefault().getTargetSdrRoot().getServicesContainer();
		PaletteCompartmentEntry servicesCompartmentEntry = getCompartmentEntry(servicesContainer);
		compartments.add(servicesCompartmentEntry);

		// FINDBY Compartment - get from super
		for (IPaletteCompartmentEntry compartment : super.getPalette()) {
			compartments.add(compartment);
		}

		// BASE TYPES Compartment
		PaletteCompartmentEntry baseTypesCompartmentEntry = getAdvancedCompartmentEntry();
		compartments.add(baseTypesCompartmentEntry);

		return compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);
	}

	private PaletteCompartmentEntry getCompartmentEntry(SoftPkgRegistry container) {

		final PaletteCompartmentEntry compartmentEntry;
		if (container instanceof DevicesContainer) {
			compartmentEntry = new PaletteCompartmentEntry("Devices", null);
		} else if (container instanceof ServicesContainer) {
			compartmentEntry = new PaletteCompartmentEntry("Services", null);
		} else {
			compartmentEntry = null;
		}

		// add all palette entries into a entriesToRemove list.
		final List<IToolEntry> entriesToRemove = new ArrayList<IToolEntry>();
		for (final Object obj : compartmentEntry.getToolEntries()) {
			if (obj instanceof SpdToolEntry || obj instanceof StackEntry) {
				entriesToRemove.add((IToolEntry) obj);
			}
		}

		// Loop through all devices in the Target SDR. When one is found, remove it from the entriesToRemove list.
		final EList<SoftPkg> components = container.getComponents();
		final SoftPkg[] devicesArray = components.toArray(new SoftPkg[components.size()]);
		spdLoop: for (final SoftPkg spd : devicesArray) {
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
			boolean foundTool = false;
			for (int i = 0; i < entriesToRemove.size(); i++) {
				final IToolEntry entry = entriesToRemove.get(i);
				if (entry.getLabel().equals(spd.getId())) {
					foundTool = true;
					if (passesFilter(entry.getLabel())) {
						entriesToRemove.remove(i);
					}
					break;
				}
			}
			if (!foundTool && passesFilter(spd.getName())) {
				// special way of instantiating create feature, allows us to know which palette tool was used
				String iconId = NodeImageProvider.IMG_COMPONENT_PLACEMENT;
				if ("Devices".equals(compartmentEntry.getLabel())) {
					iconId = NodeImageProvider.IMG_SCA_DEVICE;
				} else if ("Services".equals(compartmentEntry.getLabel())) {
					iconId = NodeImageProvider.IMG_SCA_SERVICE;
				}
				this.addToolToCompartment(compartmentEntry, spd, iconId);

//				if (spd.getImplementation().size() > 1) {
//					StackEntry stackEntry = new StackEntry(spd.getName() + spd.getImplementation().get(0).getId(), spd.getDescription(),
//						iconId);
//					compartmentEntry.addToolEntry(stackEntry);
//					List<IToolEntry> stackEntries = new ArrayList<IToolEntry>();
//					for (Implementation impl : spd.getImplementation()) {
//						ICreateFeature createComponentFeature = null;
//						if (container instanceof DevicesContainer) {
//							createComponentFeature = new DeviceCreateFeature(getFeatureProvider(), spd, impl.getId());
//						} else if (container instanceof ServicesContainer) {
//							createComponentFeature = new ServiceCreateFeature(getFeatureProvider(), spd, impl.getId());
//						}
//						SpdToolEntry entry = new SpdToolEntry(spd.getName() + " (" + impl.getId() + ")", spd.getDescription(), EcoreUtil.getURI(spd),
//							spd.getId(), null, iconId, createComponentFeature);
//						stackEntries.add(entry);
//					}
//					sort(stackEntries);
//					for (IToolEntry entry : stackEntries) {
//						stackEntry.addCreationToolEntry((SpdToolEntry) entry);
//					}
//				} else {
//					ICreateFeature createComponentFeature = null;
//					if (container instanceof DevicesContainer) {
//						createComponentFeature = new DeviceCreateFeature(getFeatureProvider(), spd, spd.getImplementation().get(0).getId());
//					} else if (container instanceof ServicesContainer) {
//						createComponentFeature = new ServiceCreateFeature(getFeatureProvider(), spd, spd.getImplementation().get(0).getId());
//					}
//					final SpdToolEntry entry = new SpdToolEntry(spd, createComponentFeature, iconId);
//					compartmentEntry.addToolEntry(entry);
//				}
			}
		}
		for (final IToolEntry entry : entriesToRemove) {
			compartmentEntry.getToolEntries().remove(entry);
		}

		// Sort the children and return the new list of components
		final ArrayList<IToolEntry> entries = new ArrayList<IToolEntry>();
		for (final IToolEntry entry : compartmentEntry.getToolEntries()) {
			if (entry instanceof IToolEntry) {
				entries.add((IToolEntry) entry);
			}
		}
		sort(entries);
		compartmentEntry.getToolEntries().clear();
		compartmentEntry.getToolEntries().addAll(entries);

		return compartmentEntry;
	}

	/**
	 * Returns a populated CompartmentEntry containing all the Base Types
	 */
	private PaletteCompartmentEntry getAdvancedCompartmentEntry() {

		advancedCompartment = initializeCompartment(advancedCompartment, "Advanced");
		final PaletteCompartmentEntry compartmentEntry = advancedCompartment;

		IFeatureProvider featureProvider = getFeatureProvider();
		// connection
		ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
		if (createConnectionFeatures.length > 0) {
			for (ICreateConnectionFeature ccf : createConnectionFeatures) {
				if (DCDConnectInterfacePattern.NAME.equals(ccf.getCreateName())) {
					ConnectionCreationToolEntry ccTool = new ConnectionCreationToolEntry(ccf.getCreateName(), ccf.getCreateDescription(),
						ccf.getCreateImageId(), ccf.getCreateLargeImageId());
					ccTool.addCreateConnectionFeature(ccf);
					compartmentEntry.addToolEntry(ccTool);
				}
			}

		}

		return compartmentEntry;
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

	/**
	 * IDE-1021: Adds start/stop/etc. buttons to hover context button pad of component as applicable.
	 */
	@Override
	public IContextButtonPadData getContextButtonPad(IPictogramElementContext context) {
		PictogramElement pe = context.getPictogramElement();
		// IDE-1061 allow button pad to appear when cursor is anywhere 
		// inside the ComponentShape
		if (pe instanceof Shape) {
			while (!(pe instanceof RHContainerShapeImpl || pe == null)) {
				pe = (PictogramElement) pe.eContainer();
			}
			if (pe == null) {
				return null;
			}
		}
		context = new LayoutContext(pe);
		IContextButtonPadData pad = super.getContextButtonPad(context);
		CustomContext cc = new CustomContext(new PictogramElement[] {pe});
		ICustomFeature[] cf = getFeatureProvider().getCustomFeatures(cc);
		for (ICustomFeature feature: cf) {
			if (feature.getImageId() != null && feature.canExecute(cc)) {
				pad.getDomainSpecificContextButtons().add(new ContextButtonEntry(feature, cc));
			}
		}
		return pad;
	}
	// TODO: reimplement the filter. May be able to refactor this backwards out of the WaveformToolBehaviorProvider?
	
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
