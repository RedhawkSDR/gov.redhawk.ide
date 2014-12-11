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
import gov.redhawk.ide.graphiti.ui.diagram.palette.SpdToolEntry;
import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;

public class GraphitiDCDToolBehaviorProvider extends AbstractGraphitiToolBehaviorProvider {

	public GraphitiDCDToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);

		// sync palette with Target SDR contents
		addTargetSdrRefreshJob(diagramTypeProvider);
	}

	// TODO: Do we need to overrider the equals method similar to what is done in the WaveformToolBehaviorProvider

	// TODO: update getSelection to avoid selecting certain elements. See WaveformToolBehaviorProvider

	// TODO: Make this job
	private void addTargetSdrRefreshJob(IDiagramTypeProvider diagramTypeProvider) {

	}

	// TODO: override get palette so that we can define our own compartments
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
		PaletteCompartmentEntry baseTypesCompartmentEntry = getBaseTypesCompartmentEntry();
		compartments.add(baseTypesCompartmentEntry);

		return compartments.toArray(new IPaletteCompartmentEntry[compartments.size()]);
	}

	private PaletteCompartmentEntry getCompartmentEntry(SoftPkgRegistry container) {

		// TODO: in practice, will there ever be a device with multiple implementations?
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
			// TODO: Confirm that devices are of type SpdToolEntry, which would then need to be refactored out of the
			// graphiti.sad package
			if (obj instanceof SpdToolEntry || obj instanceof StackEntry) {
				entriesToRemove.add((IToolEntry) obj);
			}
		}

		// Loop through all devices in the Target SDR. When one is found, remove it from the entriesToRemove list.
		// TODO: If we don't need to support multiple implementations for devices, remove the relevant logic
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
			// TODO: implement palette filter
			for (int i = 0; i < entriesToRemove.size(); i++) {
				final IToolEntry entry = entriesToRemove.get(i);
				if (entry.getLabel().equals(spd.getId())) {
					foundTool = true;
					/*if (passesFilter(entry.getLabel())) {
						entriesToRemove.remove(i);
					}*/
					break;
				}
			}
			if (!foundTool /*&& passesFilter(spd.getName())*/) {
				// special way of instantiating create feature, allows us to know which palette tool was used

				if (spd.getImplementation().size() > 1) {
					StackEntry stackEntry = new StackEntry(spd.getName() + spd.getImplementation().get(0).getId(), spd.getDescription(),
						NodeImageProvider.IMG_COMPONENT_PLACEMENT);
					compartmentEntry.addToolEntry(stackEntry);
					List<IToolEntry> stackEntries = new ArrayList<IToolEntry>();
					for (Implementation impl : spd.getImplementation()) {
						ICreateFeature createComponentFeature = null;
						if (container instanceof DevicesContainer) {
							createComponentFeature = new DeviceCreateFeature(getFeatureProvider(), spd, impl.getId());
						} else if (container instanceof ServicesContainer) {
							createComponentFeature = new ServiceCreateFeature(getFeatureProvider(), spd, impl.getId());
						}
						SpdToolEntry entry = new SpdToolEntry(spd.getName() + " (" + impl.getId() + ")", spd.getDescription(), EcoreUtil.getURI(spd),
							spd.getId(), null, NodeImageProvider.IMG_COMPONENT_PLACEMENT, createComponentFeature);
						stackEntries.add(entry);
					}
					sort(stackEntries);
					for (IToolEntry entry : stackEntries) {
						stackEntry.addCreationToolEntry((SpdToolEntry) entry);
					}
				} else {
					ICreateFeature createComponentFeature = null;
					if (container instanceof DevicesContainer) {
						createComponentFeature = new DeviceCreateFeature(getFeatureProvider(), spd, spd.getImplementation().get(0).getId());
					} else if (container instanceof ServicesContainer) {
						createComponentFeature = new ServiceCreateFeature(getFeatureProvider(), spd, spd.getImplementation().get(0).getId());
					}
					final SpdToolEntry entry = new SpdToolEntry(spd, createComponentFeature, NodeImageProvider.IMG_COMPONENT_PLACEMENT);
					compartmentEntry.addToolEntry(entry);
				}
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
	private PaletteCompartmentEntry getBaseTypesCompartmentEntry() {

		final PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Base Types", null);

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

	// TODO: add compartment for connections. Should this be "Base Types" like in waveforms? Do we need host
	// collocations too?

	// TODO: This may be another good method for refactoring up
	private void sort(List<IToolEntry> entries) {
		Collections.sort(entries, new Comparator<IToolEntry>() {

			@Override
			public int compare(final IToolEntry o1, final IToolEntry o2) {
				final String str1 = o1.getLabel();
				final String str2 = o2.getLabel();
				if (str1 == null) {
					if (str2 == null) {
						return 0;
					} else {
						return 1;
					}
				} else if (str2 == null) {
					return -1;
				} else {
					return str1.compareToIgnoreCase(str2);
				}
			}

		});
	}

	// TODO: reimplement the filter. May be able to refactor this backwards out of the WaveformToolBehaviorProvider?

	// TODO: dispose to clean up any listeners we add for refresh jobs
}
