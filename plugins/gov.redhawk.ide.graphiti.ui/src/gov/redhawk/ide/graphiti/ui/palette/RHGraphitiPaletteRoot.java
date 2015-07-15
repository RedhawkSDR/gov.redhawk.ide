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
package gov.redhawk.ide.graphiti.ui.palette;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.AbstractTool;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;
import org.eclipse.graphiti.features.ICreateFeature;
import org.eclipse.graphiti.palette.IConnectionCreationToolEntry;
import org.eclipse.graphiti.palette.ICreationToolEntry;
import org.eclipse.graphiti.palette.IObjectCreationToolEntry;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.palette.IPaletteSeparatorEntry;
import org.eclipse.graphiti.palette.IStackToolEntry;
import org.eclipse.graphiti.palette.IToolEntry;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.IEclipseImageDescriptor;
import org.eclipse.graphiti.ui.internal.Messages;
import org.eclipse.graphiti.ui.internal.editor.GFConnectionCreationTool;
import org.eclipse.graphiti.ui.internal.editor.GFCreationTool;
import org.eclipse.graphiti.ui.internal.util.gef.MultiCreationFactory;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 
 */
@SuppressWarnings("restriction")
public class RHGraphitiPaletteRoot extends PaletteRoot {

	private static final boolean DND_FROM_PALETTE = true;

	private IDiagramTypeProvider diagramTypeProvider;
	private PaletteContainer paletteTools = null;

	/**
	 * Creates a new GenericPaletteRoot for the given Model. It is constructed
	 * by calling createModelIndependentTools() and createCreationTools().
	 * 
	 * @param configurationProvider
	 *            the configuration provider
	 */
	public RHGraphitiPaletteRoot(IDiagramTypeProvider diagramTypeProvider) {
		this.diagramTypeProvider = diagramTypeProvider;
		updatePaletteEntries();
	}

	private PaletteDrawer getEmptyDrawer(String label) {
		for (Object obj: this.getChildren()) {
			if (obj instanceof PaletteDrawer) {
				PaletteDrawer drawer = (PaletteDrawer) obj;
				if (drawer.getLabel().equals(label)) {
					// Can't just clear(), doesn't send property change 
					// notification to update GUI
					drawer.setChildren(new ArrayList<Object>());
					return (PaletteDrawer) obj;
				}
			}
		}
		return null;
	}
	/**
	 * Creates resp. updates the PaletteEntries. All old PaletteEntries will be
	 * removed and new ones will be created by calling the corresponding
	 * create-methods.
	 */
	public void updatePaletteEntries() {
		// remove old entries
		setDefaultEntry(null);

		boolean setInitialDrawerState = false;
		// create new entries
		if (paletteTools == null) {
			paletteTools = createModelIndependentTools();
//			add(paletteTools);
			add(new PaletteEntry(null, null) {
				@Override
				public Object getType() {
					return "paletteFilter";
				}
			});
			setInitialDrawerState = true;
		}

		IToolBehaviorProvider currentToolBehaviorProvider = diagramTypeProvider.getCurrentToolBehaviorProvider();

		IPaletteCompartmentEntry[] paletteCompartments = currentToolBehaviorProvider.getPalette();

		List <String> compartmentLabels = new ArrayList<String>();
		
		for (IPaletteCompartmentEntry compartmentEntry : paletteCompartments) {
			compartmentLabels.add(compartmentEntry.getLabel());
			PaletteDrawer drawer = getEmptyDrawer(compartmentEntry.getLabel());
			if (drawer == null) {
				drawer = new PaletteDrawer(compartmentEntry.getLabel(), getImageDescriptor(compartmentEntry));
				if (setInitialDrawerState && !compartmentEntry.isInitiallyOpen()) {
					drawer.setInitialState(PaletteDrawer.INITIAL_STATE_CLOSED);
				}
				add(drawer);
			}
			List<IToolEntry> toolEntries = compartmentEntry.getToolEntries();
			fillContainer(drawer, toolEntries);
//			for (IToolEntry toolEntry : toolEntries) {
//
//				if (toolEntry instanceof ICreationToolEntry) {
//					ICreationToolEntry creationToolEntry = (ICreationToolEntry) toolEntry;
//
//					PaletteEntry createTool = createTool(creationToolEntry);
//					if (createTool != null) {
//						drawer.add(createTool);
//					}
//				} else if (toolEntry instanceof IStackToolEntry) {
//					IStackToolEntry stackToolEntry = (IStackToolEntry) toolEntry;
//					PaletteStack stack = new PaletteStack(stackToolEntry.getLabel(), stackToolEntry.getDescription(),
//							GraphitiUi.getImageService().getImageDescriptorForId(diagramTypeProvider.getProviderId(),
//									stackToolEntry.getIconId()));
//					drawer.add(stack);
//					List<ICreationToolEntry> creationToolEntries = stackToolEntry.getCreationToolEntries();
//					for (ICreationToolEntry creationToolEntry : creationToolEntries) {
//						PaletteEntry createTool = createTool(creationToolEntry);
//						if (createTool != null) {
//							stack.add(createTool);
//						}
//					}
//				} else if (toolEntry instanceof IPaletteSeparatorEntry) {
//					drawer.add(new PaletteSeparator());
//				}
//			}
		}
		
		// Hide the drawers for which no compartment entry was returned (empty)
		for (Object obj: getChildren()) {
			if (obj instanceof PaletteDrawer) {
				PaletteDrawer drawer = (PaletteDrawer) obj;
				if (!compartmentLabels.contains(drawer.getLabel())) {
					// Can't just call clear(), doesn't send property change 
					// notification to update the GUI
					drawer.setChildren(new ArrayList<Object>());
				}
			}
		}
	}

	protected PaletteEntry produceEntry(IToolEntry model) {
		if (model instanceof ICreationToolEntry) {
			return createTool((ICreationToolEntry) model);
		}
		if (model instanceof IStackToolEntry) {
			IStackToolEntry stackToolEntry = (IStackToolEntry) model;
			PaletteStack stack = new PaletteStack(stackToolEntry.getLabel(), stackToolEntry.getDescription(),
				GraphitiUi.getImageService().getImageDescriptorForId(diagramTypeProvider.getProviderId(),
						stackToolEntry.getIconId()));
			fillContainer(stack, stackToolEntry.getCreationToolEntries());
			return stack;
		}
		if (model instanceof IPaletteSeparatorEntry) {
			return new PaletteSeparator();
		}
		if (model instanceof PaletteTreeEntry) {
			PaletteTreeEntry treeEntry = (PaletteTreeEntry) model;
			PaletteNamespaceFolder folder = new PaletteNamespaceFolder(treeEntry.getLabel());
			folder.setInitialState(treeEntry.isInitiallyOpen() ? PaletteDrawer.INITIAL_STATE_OPEN : PaletteDrawer.INITIAL_STATE_CLOSED);
			fillContainer(folder, treeEntry.getToolEntries());
			return folder;
		}
		return null;
	}
	
	protected void fillContainer(PaletteContainer container, List<? extends IToolEntry> entries) {
		for (IToolEntry entry: entries) {
			container.add(produceEntry(entry));
		}
	}
	
	/**
	 * Creates and adds the model-independent tools to a new PaletteContainer.
	 * This currently includes only the component/device filter.
	 * 
	 * @return The PaletteContainer with the model-independent tools.
	 */
	protected PaletteContainer createModelIndependentTools() {
		PaletteGroup controlGroup = new PaletteGroup(Messages.GraphicsPaletteRoot_0_xmen);
		return controlGroup;
	}

	private PaletteEntry createTool(ICreationToolEntry creationToolEntry) {
		String label = creationToolEntry.getLabel();
		String description = creationToolEntry.getDescription();
		if (creationToolEntry instanceof IObjectCreationToolEntry) {
			IObjectCreationToolEntry objectCreationToolEntry = (IObjectCreationToolEntry) creationToolEntry;
			DefaultCreationFactory cf = new DefaultCreationFactory(objectCreationToolEntry.getCreateFeature(),
					ICreateFeature.class);
			Object template = (DND_FROM_PALETTE) ? cf : null;
			CombinedTemplateCreationEntry pe = new CombinedTemplateCreationEntry(label, description, template, cf,
					getImageDescriptor(creationToolEntry, true), getImageDescriptor(creationToolEntry, false));
			pe.setToolClass(GFCreationTool.class);
			return pe;
		} else if (creationToolEntry instanceof IConnectionCreationToolEntry) {
			IConnectionCreationToolEntry connectionCreationToolEntry = (IConnectionCreationToolEntry) creationToolEntry;
			MultiCreationFactory multiCreationFactory = new MultiCreationFactory(
					connectionCreationToolEntry.getCreateConnectionFeatures());
			ConnectionCreationToolEntry pe = new ConnectionCreationToolEntry(label, description, multiCreationFactory,
					getImageDescriptor(creationToolEntry, true), getImageDescriptor(creationToolEntry, false));
			pe.setToolClass(GFConnectionCreationTool.class);

			if (!diagramTypeProvider.getCurrentToolBehaviorProvider().isStayActiveAfterExecution(
					connectionCreationToolEntry)) {
				pe.setToolProperty(AbstractTool.PROPERTY_UNLOAD_WHEN_FINISHED, true);
			}
			return pe;
		}
		return null;
	}

	private class DefaultCreationFactory implements CreationFactory {

		private Object obj;

		private Object objType;

		/**
		 * 
		 */
		public DefaultCreationFactory(Object obj, Object objType) {
			super();
			this.obj = obj;
			this.objType = objType;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
		 */
		public Object getNewObject() {
			return obj;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
		 */
		public Object getObjectType() {
			return objType;
		}

	}

	private ImageDescriptor getImageDescriptor(ICreationToolEntry creationToolEntry, boolean smallImage) {
		ImageDescriptor imageDescriptor = null;
		if (creationToolEntry instanceof IEclipseImageDescriptor) {
			imageDescriptor = ((IEclipseImageDescriptor) creationToolEntry).getImageDescriptor();
		} else {
			String iconId = (smallImage) ? creationToolEntry.getIconId() : creationToolEntry.getLargeIconId();
			imageDescriptor = GraphitiUi.getImageService().getImageDescriptorForId(diagramTypeProvider.getProviderId(),
					iconId);
		}
		return imageDescriptor;
	}

	private ImageDescriptor getImageDescriptor(IPaletteCompartmentEntry compartmentEntry) {
		ImageDescriptor imageDescriptor = null;
		if (compartmentEntry instanceof IEclipseImageDescriptor) {
			imageDescriptor = ((IEclipseImageDescriptor) compartmentEntry).getImageDescriptor();
		} else {
			imageDescriptor = GraphitiUi.getImageService().getImageDescriptorForId(diagramTypeProvider.getProviderId(),
					compartmentEntry.getIconId());
		}
		return imageDescriptor;
	}

	@Override
	public boolean acceptsType(Object type) {
		if ("paletteFilter".equals(type)) {
			return true;
		}
		return super.acceptsType(type);
	}
}
