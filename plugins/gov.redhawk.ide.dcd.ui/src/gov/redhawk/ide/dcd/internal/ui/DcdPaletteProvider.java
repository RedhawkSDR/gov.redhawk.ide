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
package gov.redhawk.ide.dcd.internal.ui;

import gov.redhawk.diagram.edit.helpers.ComponentPlacementEditHelperAdvice;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.diagram.providers.DcdElementTypes;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.gef.Request;
import org.eclipse.gef.Tool;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gmf.runtime.diagram.ui.internal.services.palette.PaletteToolEntry;
import org.eclipse.gmf.runtime.diagram.ui.providers.DefaultPaletteProvider;
import org.eclipse.gmf.runtime.diagram.ui.tools.CreationTool;
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * 
 */
public class DcdPaletteProvider extends DefaultPaletteProvider {
	private static final String TOOL_PREFIX = "deviceTool.";

	@Override
	public void contributeToPalette(final IEditorPart editor, final Object content, final PaletteRoot root, final Map predefinedEntries) {
		final PaletteDrawer ddrawer = new PaletteDrawer("Devices");
		root.add(ddrawer);
		ddrawer.add(new PaletteSeparator("devices"));
		final DevicesContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getDevicesContainer();
		container.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					refreshPallette(container, ddrawer);
					return Status.OK_STATUS;
				}

			};

			@Override
			public void notifyChanged(final Notification msg) {
				super.notifyChanged(msg);
				switch (msg.getFeatureID(ComponentsContainer.class)) {
				case SdrPackage.DEVICES_CONTAINER__COMPONENTS:
					this.refreshPalletteJob.schedule(1000); // SUPPRESS CHECKSTYLE MagicNumber
					break;
				default:
					break;
				}
			}
		});
		refreshPallette(container, ddrawer);

		final PaletteDrawer sdrawer = new PaletteDrawer("Services");
		root.add(sdrawer);
		sdrawer.add(new PaletteSeparator("services"));
		final ServicesContainer servContainer = SdrUiPlugin.getDefault().getTargetSdrRoot().getServicesContainer();
		servContainer.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					refreshPallette(servContainer, sdrawer);
					return Status.OK_STATUS;
				}

			};

			@Override
			public void notifyChanged(final Notification msg) {
				super.notifyChanged(msg);
				switch (msg.getFeatureID(ComponentsContainer.class)) {
				case SdrPackage.SERVICES_CONTAINER__COMPONENTS:
					this.refreshPalletteJob.schedule(1000); // SUPPRESS CHECKSTYLE MagicNumber
					break;
				default:
					break;
				}
			}
		});
		refreshPallette(servContainer, sdrawer);
	}

	@Override
	public void setContributions(final IConfigurationElement configElement) {

	}

	@SuppressWarnings("unchecked")
	private void refreshPallette(final EObject container, final PaletteDrawer drawer) {

		final List<PaletteEntry> entriesToRemove = new ArrayList<PaletteEntry>();
		for (final Object obj : drawer.getChildren()) {
			if (obj instanceof PaletteEntry) {
				final PaletteEntry entry = (PaletteEntry) obj;
				if (entry.getId().startsWith(DcdPaletteProvider.TOOL_PREFIX)) {
					entriesToRemove.add(entry);
				}
			}
		}
		final EList<SoftPkg> components = getComponents(container);
		final SoftPkg[] componentsArray = components.toArray(new SoftPkg[components.size()]);
		for (final SoftPkg spd : componentsArray) {
			boolean foundTool = false;
			for (int i = 0; i < entriesToRemove.size(); i++) {
				final PaletteEntry entry = entriesToRemove.get(i);
				if (entry.getId().equals(DcdPaletteProvider.TOOL_PREFIX + spd.getId())) {
					foundTool = true;
					entriesToRemove.remove(i);
					break;
				}
			}
			if (!foundTool) {
				String description = spd.getDescription();
				if (description == null) {
					description = MessageFormat.format("Create a new instance of the component \"{0}\".", spd.getName());
				}
				final NodeToolEntry entry = new NodeToolEntry(spd.getName(), description, DcdElementTypes.DcdComponentPlacement_3001, EcoreUtil.getURI(spd));
				entry.setSmallIcon(DcdElementTypes.getImageDescriptor(DcdElementTypes.DcdComponentPlacement_3001));
				entry.setLargeIcon(entry.getSmallIcon());
				entry.setId(DcdPaletteProvider.TOOL_PREFIX + spd.getId());
				drawer.add(entry);
			}
		}
		for (final PaletteEntry entry : entriesToRemove) {
			drawer.remove(entry);
		}

		// Sort the children
		final ArrayList<PaletteEntry> top = new ArrayList<PaletteEntry>();
		final ArrayList<PaletteEntry> childrenToSort = new ArrayList<PaletteEntry>();
		top.addAll(drawer.getChildren());
		for (final PaletteEntry entry : top) {
			if (entry.getId().startsWith(DcdPaletteProvider.TOOL_PREFIX)) {
				childrenToSort.add(entry);
			}
		}
		top.removeAll(childrenToSort);
		Collections.sort(childrenToSort, new Comparator<PaletteEntry>() {

			public int compare(final PaletteEntry o1, final PaletteEntry o2) {
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
		top.addAll(childrenToSort);
		drawer.setChildren(top);
	}

	private static class NodeToolEntry extends PaletteToolEntry {

		private final IElementType elementType;
		private final URI spdUri;

		private NodeToolEntry(final String title, final String description, final IElementType elementType, final URI spdUri) {
			super(null, title, null);
			Assert.isNotNull(spdUri);
			this.setDescription(description);
			this.elementType = elementType;
			this.spdUri = spdUri;
		}

		@Override
		public Tool createTool() {
			final Tool tool = new CreationTool(this.elementType) {
				@SuppressWarnings("unchecked")
				@Override
				protected Request createTargetRequest() {
					final Request retVal = super.createTargetRequest();
					final HashMap<Object, Object> map = new HashMap<Object, Object>();
					map.putAll(retVal.getExtendedData());
					map.put(ComponentPlacementEditHelperAdvice.CONFIGURE_OPTIONS_SPD_URI, NodeToolEntry.this.spdUri);
					retVal.setExtendedData(map);
					return retVal;
				}
			};
			tool.setProperties(getToolProperties());
			return tool;
		}
	}

	private EList<SoftPkg> getComponents(final EObject container) {
		if (container instanceof DevicesContainer) {
			return ((DevicesContainer) container).getComponents();
		} else {
			return ((ServicesContainer) container).getComponents();
		}
	}

}
