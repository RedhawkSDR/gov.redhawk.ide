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
package gov.redhawk.ide.sad.graphiti.debug.internal.ui;

import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.RHGraphitiDiagramEditor;
import gov.redhawk.ide.sad.ui.providers.SpdToolEntry;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.scd.ComponentType;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;

public class SandboxRHGraphitiDiagramEditor extends RHGraphitiDiagramEditor {

	private static final ImageDescriptor TOOL_ICON = SpdToolEntry.getDefaultIcon();

	private java.beans.PropertyChangeListener listener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (IResourceFactoryRegistry.PROP_RESOURCES.equals(evt.getPropertyName())) {
				updatePaletteJob.schedule(500);
			}
		}
	};

	private final Job updatePaletteJob = new Job("Updating Palette") {

		@Override
		public IStatus run(IProgressMonitor monitor) {
			final PaletteRoot root = getEditDomain().getPaletteViewer().getPaletteRoot();
			final List<PaletteEntry> tools = getWorkspaceComponentTools();
			
			// Add the toolbar and filter controls
			tools.add(0, (PaletteEntry) rootPalette.getChildren().get(0));
			if (root != null) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						root.setChildren(tools);
					}

				});

			}
			return Status.OK_STATUS;
		}
	};

	private PaletteRoot rootPalette;

	public SandboxRHGraphitiDiagramEditor(final TransactionalEditingDomain editingDomain) {
		super(editingDomain);

	}

	@Override
	public void dispose() {
		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.removeListener(listener);
		super.dispose();
	}
	
	//TODO: handle changing the contents of the palette
//	@Override
//	protected PaletteRoot createPaletteRoot(final PaletteRoot existingPaletteRoot) {
//		this.rootPalette = super.createPaletteRoot(existingPaletteRoot);
//		final PaletteRoot retVal = new PaletteRoot();
//		List<PaletteEntry> tools = getWorkspaceComponentTools();
//		
//		// Add the toolbar and filter controls
//		tools.add(0, (PaletteEntry) rootPalette.getChildren().get(0));
//		
//		retVal.setChildren(tools);
//
//		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
//		registry.addListener(listener);
//		return retVal;
//	}

	private synchronized List<PaletteEntry> getWorkspaceComponentTools() {
		Map<String, List<PaletteEntry>> containerMap = new HashMap<String, List<PaletteEntry>>();

		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
		registry.addListener(listener);
		for (ResourceDesc desc : registry.getResourceDescriptors()) {
			if (!shouldAdd(desc)) {
				continue;
			}
			String category = desc.getCategory();
			List<PaletteEntry> containerList = containerMap.get(category);
			if (containerList == null) {
				String label = category;
				if (label == null) {
					label = "Other";
				}
				containerList = new ArrayList<PaletteEntry>();
				containerMap.put(category, containerList);
			}
			if (desc instanceof ComponentDesc) {
				PaletteEntry newEntry = null;
				ComponentDesc compDesc = (ComponentDesc) desc;
				List<PaletteEntry> entries = createPaletteEntries(compDesc);
				sort(entries);
				if (entries.size() > 1) {
					PaletteStack stack = new PaletteStack(entries.get(0).getLabel(), entries.get(0).getDescription(), entries.get(0).getLargeIcon());
					stack.setChildren(entries);
					newEntry = stack;
				} else if (!entries.isEmpty()) {
					newEntry = entries.get(0);
				}
				if (newEntry != null) {
					containerList.add(newEntry);
				}
			}
		}

		final List<PaletteEntry> retVal = new ArrayList<PaletteEntry>();
		for (Map.Entry<String, List<PaletteEntry>> entry : containerMap.entrySet()) {
			PaletteDrawer container = new PaletteDrawer(entry.getKey());
			sort(entry.getValue());
			container.addAll(entry.getValue());
			retVal.add(container);
		}
		sort(retVal);

		return retVal;
	}

	private boolean shouldAdd(ResourceDesc desc) {
		if (desc instanceof ComponentDesc) {
			ComponentDesc compDesc = (ComponentDesc) desc;
			String type = compDesc.getComponentType();
			if (!ComponentType.RESOURCE.getLiteral().equalsIgnoreCase(type)) {
				return false;
			} else if (compDesc.getImplementationIds().isEmpty()) { // SUPPRESS CHECKSTYLE Explicit returns
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void sort(List<PaletteEntry> entries) {
		Collections.sort(entries, new Comparator<PaletteEntry>() {

			@Override
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
	}

	private List<PaletteEntry> createPaletteEntries(ComponentDesc desc) {
		List<PaletteEntry> retVal = new ArrayList<PaletteEntry>(desc.getImplementationIds().size());
		if (desc.getImplementationIds().size() == 1) {
			retVal.add(new SpdToolEntry(desc.getName(), desc.getDescription(), desc.getResourceURI(), desc.getIdentifier(), desc.getImplementationIds().get(0),
				TOOL_ICON));
		} else {
			for (String implID : desc.getImplementationIds()) {
				retVal.add(new SpdToolEntry(desc.getName() + " (" + implID + ")", desc.getDescription(), desc.getResourceURI(), desc.getIdentifier(), implID,
					TOOL_ICON));
			}
		}
		return retVal;
	}
}
