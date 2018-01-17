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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.palette.impl.ObjectCreationToolEntry;
import org.eclipse.graphiti.palette.impl.PaletteCompartmentEntry;
import org.eclipse.graphiti.palette.impl.StackEntry;
import org.eclipse.graphiti.pattern.CreateFeatureForPattern;
import org.eclipse.ui.progress.WorkbenchJob;

import gov.redhawk.core.graphiti.ui.diagram.providers.AbstractToolBehaviorProvider;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.PaletteTreeEntry;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * Provides tool behavior for Graphiti diagrams that have a palette (only the XML editors, sandbox diagrams)
 */
public abstract class AbstractPaletteToolBehaviorProvider extends AbstractToolBehaviorProvider {

	private final WorkbenchJob refreshPaletteJob = new WorkbenchJob("Refresh Palette") {
		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			// refresh palette which will call IToolBehaviorProvider.getPalette()
			getDiagramTypeProvider().getDiagramBehavior().refreshPalette();
			return Status.OK_STATUS;
		}
	};

	private final Adapter sdrListener = new AdapterImpl() {
		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getFeatureID(SoftPkgRegistry.class) == SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS) {
				refreshPaletteAsync();
			}
		}
	};

	private List<IPaletteCompartmentEntry> paletteCompartments = null;
	private List<SoftPkgRegistry> registries = new ArrayList<SoftPkgRegistry>();

	public AbstractPaletteToolBehaviorProvider(IDiagramTypeProvider diagramTypeProvider) {
		super(diagramTypeProvider);
	}

	@Override
	public void dispose() {
		for (SoftPkgRegistry registry : registries) {
			registry.eAdapters().remove(sdrListener);
		}
		super.dispose();
	}

	@Override
	public boolean isShowFlyoutPalette() {
		return true;
	}

	@Override
	public IPaletteCompartmentEntry[] getPalette() {
		// Allow child classes to contribute palette compartments the first time we get the palette
		if (paletteCompartments == null) {
			paletteCompartments = new ArrayList<IPaletteCompartmentEntry>();
			addPaletteCompartments(paletteCompartments);
		}

		// Allow subclasses to refresh their palette entries
		refreshPalette();

		return paletteCompartments.toArray(new IPaletteCompartmentEntry[paletteCompartments.size()]);
	}

	/**
	 * The tool behavior provider should contribute {@link IPaletteCompartmentEntry}. This is only invoked once.
	 */
	protected abstract void addPaletteCompartments(List<IPaletteCompartmentEntry> compartments);

	/**
	 * The tool behavior provider should refresh its {@link IToolEntry}s in each {@link PaletteCompartmentEntry}.
	 */
	protected abstract void refreshPalette();

	/**
	 * Starts an asynchronous refresh of the palette in the UI thread.
	 */
	protected void refreshPaletteAsync() {
		refreshPaletteJob.schedule(1000);
	}

	/**
	 * Creates a {@link PaletteCompartmentEntry} for findby's, and adds all findby patterns found in the feature
	 * provider.
	 * @return
	 */
	protected PaletteCompartmentEntry createFindByCompartmentEntry() {
		PaletteCompartmentEntry compartmentEntry = new PaletteCompartmentEntry("Find By", null);

		for (ICreateFeature cf : getFeatureProvider().getCreateFeatures()) {
			// Search for create features that use a pattern derived from the abstract FindBy patten
			if ((cf instanceof CreateFeatureForPattern && ((CreateFeatureForPattern) cf).getPattern() instanceof AbstractFindByPattern)) {
				ObjectCreationToolEntry objectCreationToolEntry = new ObjectCreationToolEntry(cf.getCreateName(), cf.getCreateDescription(),
					cf.getCreateImageId(), cf.getCreateLargeImageId(), cf);

				compartmentEntry.addToolEntry(objectCreationToolEntry);
			}
		}

		return compartmentEntry;
	}

	/**
	 * Subclasses must provide a {@link ICreateFeature} for the given {@link SoftPkg} implementation.
	 * @param spd
	 * @param implId
	 * @param iconId
	 * @return
	 */
	protected abstract ICreateFeature getCreateFeature(SoftPkg spd, String implId, String iconId);

	/**
	 * Refreshes the contents of a {@link PaletteCompartmentEntry} with the contents of a {@link SoftPkgRegistry}.
	 * @param compartmentEntry
	 * @param container
	 * @param iconId
	 */
	protected void refreshCompartmentEntry(PaletteCompartmentEntry compartmentEntry, final SoftPkgRegistry container, String iconId) {
		compartmentEntry.getToolEntries().clear();

		// Collect SPDs in the tree under the container
		List<SoftPkg> spds = ScaModelCommand.runExclusive(container, () -> {
			return container.getAllComponents();
		});

		for (SoftPkg spd : spds) {
			for (Implementation impl : spd.getImplementation()) {
				if (impl.isExecutable()) {
					addToolToCompartment(compartmentEntry, spd, iconId);
					break;
				}
			}
		}

		sort(compartmentEntry.getToolEntries());
	}

	/**
	 * Creates and adds {@link IToolEntry} to the specified {@link PaletteCompartmentEntry} for the specified
	 * {@link SoftPkg}.
	 * @param compartment
	 * @param spd
	 * @param iconId
	 */
	protected void addToolToCompartment(PaletteCompartmentEntry compartment, SoftPkg spd, String iconId) {
		Assert.isNotNull(compartment, "Cannot add tool to non-existent compartment");
		String[] segments = getNameSegments(spd);
		PaletteCompartmentEntry folder = compartment;
		for (int index = 0; index < segments.length - 1; ++index) {
			folder = getSegmentEntry(folder, segments[index]);
		}
		folder.addToolEntry(makeTool(spd, iconId));
	}

	private String[] getNameSegments(SoftPkg spd) {
		String fullName = spd.getName();
		if (fullName == null) {
			return new String[] { "" };
		}
		if (!fullName.contains(".")) {
			return new String[] { fullName };
		}
		return fullName.split("\\.");
	}

	private PaletteTreeEntry getSegmentEntry(PaletteCompartmentEntry parent, String label) {
		if (label == null) {
			return null;
		}
		if (parent == null) {
			return new PaletteTreeEntry(label);
		}
		for (IToolEntry entry : parent.getToolEntries()) {
			if (entry instanceof PaletteTreeEntry && label.equals(entry.getLabel())) {
				return (PaletteTreeEntry) entry;
			}
		}
		return new PaletteTreeEntry(label, parent);
	}

	private IToolEntry makeTool(SoftPkg spd, String iconId) {
		List<IToolEntry> newEntries = createPaletteEntries(spd, iconId);
		if (newEntries != null && newEntries.size() > 1) {
			sort(newEntries);
			IToolEntry firstEntry = newEntries.get(0);
			StackEntry stackEntry = new StackEntry(firstEntry.getLabel(), ((ObjectCreationToolEntry) firstEntry).getDescription(), firstEntry.getIconId());
			for (IToolEntry entry : newEntries) {
				stackEntry.addCreationToolEntry((ObjectCreationToolEntry) entry);
			}
			return stackEntry;
		}
		return newEntries.get(0);
	}

	/**
	 * Creates a new IToolEntry for the {@link SoftPkg}, or in the case of a runtime diagram, an {@link IToolEntry} for
	 * each of the {@link SoftPkg}'s implementations. Also assigns the createComponentFeature to the palette entry so
	 * that the diagram knows which shape to create.
	 */
	private List<IToolEntry> createPaletteEntries(SoftPkg spd, String iconId) {
		String label = getLastSegment(getNameSegments(spd));
		List<IToolEntry> entries = new ArrayList<IToolEntry>(spd.getImplementation().size());
		if (spd.getImplementation().size() == 1 || DUtil.isDiagramWorkpace(this.getDiagramTypeProvider().getDiagram())) {
			entries.add(createSpdToolEntry(label, spd, null, iconId));
		} else {
			for (Implementation impl : spd.getImplementation()) {
				entries.add(createSpdToolEntry(label, spd, impl.getId(), iconId));
			}
		}
		return entries;
	}

	private String getLastSegment(String[] segments) {
		if (segments == null || segments.length < 1) {
			return "";
		}
		return segments[segments.length - 1];
	}

	private ObjectCreationToolEntry createSpdToolEntry(String label, SoftPkg spd, String implId, String iconId) {
		if (implId == null) {
			// Use default implementation
			implId = spd.getImplementation().get(0).getId();
		} else {
			// Implementation given, include it in the label
			label = label + " (" + implId + ")";
		}
		String description = spd.getDescription();
		if (description == null) {
			description = "Create a new instance of \"" + label + "\"";
		}
		ICreateFeature createComponentFeature = getCreateFeature(spd, implId, iconId);
		return new ObjectCreationToolEntry(label, description, iconId, null, createComponentFeature);
	}

	/**
	 * Sorts a list of {@link IToolEntry} according to:
	 * <ol>
	 * <li>Folders ({@link PaletteTreeEntry}) before other objects</li>
	 * <li>Case-insensitive label comparison</li>
	 * </ol>
	 * @param entries
	 */
	protected void sort(List<IToolEntry> entries) {
		Collections.sort(entries, new Comparator<IToolEntry>() {

			@Override
			public int compare(final IToolEntry o1, final IToolEntry o2) {
				// Put the namespace folders together at the top
				if (o1 instanceof PaletteTreeEntry && !(o2 instanceof PaletteTreeEntry)) {
					return -1;
				}
				if (o2 instanceof PaletteTreeEntry && !(o1 instanceof PaletteTreeEntry)) {
					return 1;
				}
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
		for (IToolEntry entry : entries) {
			if (entry instanceof PaletteTreeEntry) {
				sort(((PaletteTreeEntry) entry).getToolEntries());
			}
		}
	}

	/**
	 * Add a listener on a {@link SoftPkgRegistry} (i.e. part of the SDRROOT) that will refresh the palette when there
	 * are changes.
	 */
	protected void addTargetSdrRefreshJob(SoftPkgRegistry container) {
		container.eAdapters().add(sdrListener);
		registries.add(container);
	}
}
