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
package gov.redhawk.ide.sad.internal.ui.provider;

import gov.redhawk.ide.sad.ui.providers.SpdToolEntry;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gmf.runtime.diagram.ui.providers.DefaultPaletteProvider;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.WorkbenchJob;

/**
 * @since 4.0
 * 
 */
public class SadPaletteProvider extends DefaultPaletteProvider {

	/**
	 * 
	 */
	public SadPaletteProvider() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void contributeToPalette(final IEditorPart editor, final Object content, final PaletteRoot root, final Map predefinedEntries) {
		final PaletteDrawer drawer = new PaletteDrawer("Components");
		root.add(drawer);
		drawer.add(new PaletteSeparator("components"));
		final ComponentsContainer container = SdrUiPlugin.getDefault().getTargetSdrRoot().getComponentsContainer();
		container.eAdapters().add(new AdapterImpl() {
			private final WorkbenchJob refreshPalletteJob = new WorkbenchJob("Refresh Pallette") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					refreshPallette(container, drawer);
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
		refreshPallette(container, drawer);
	}

	@Override
	public void setContributions(final IConfigurationElement configElement) {

	}

	@SuppressWarnings("unchecked")
	private void refreshPallette(final ComponentsContainer container, final PaletteDrawer drawer) {
		final List<PaletteEntry> entriesToRemove = new ArrayList<PaletteEntry>();
		for (final Object obj : drawer.getChildren()) {
			if (obj instanceof PaletteEntry) {
				final PaletteEntry entry = (PaletteEntry) obj;
				if (entry.getId().startsWith(SpdToolEntry.TOOL_PREFIX)) {
					entriesToRemove.add(entry);
				}
			}
		}
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
				switch(type) {
				case EXECUTABLE:
					break;
				default:
					continue spdLoop;
				}
			}
			boolean foundTool = false;
			for (int i = 0; i < entriesToRemove.size(); i++) {
				final PaletteEntry entry = entriesToRemove.get(i);
				if (entry.getId().equals(SpdToolEntry.TOOL_PREFIX + spd.getId())) {
					foundTool = true;
					entriesToRemove.remove(i);
					break;
				}
			}
			if (!foundTool) {
				final SpdToolEntry entry = new SpdToolEntry(spd);
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
			if (entry.getId().startsWith(SpdToolEntry.TOOL_PREFIX)) {
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

}
