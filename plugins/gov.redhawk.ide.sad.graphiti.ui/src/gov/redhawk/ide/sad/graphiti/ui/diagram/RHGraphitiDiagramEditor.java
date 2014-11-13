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
package gov.redhawk.ide.sad.graphiti.ui.diagram;

import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryRegistry;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.core.resourcefactory.ResourceFactoryPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.RHToolBehaviorProvider;
import gov.redhawk.ide.sad.graphiti.ui.palette.RHGraphitiPaletteFilter;
import gov.redhawk.ide.sad.graphiti.ui.palette.RHGraphitiPaletteFilterEditPart;
import gov.redhawk.ide.sad.graphiti.ui.palette.RHGraphitiPaletteRoot;
import gov.redhawk.ide.sad.ui.providers.SpdToolEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.scd.ComponentType;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.GroupEditPart;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.PaletteStack;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.palette.IPaletteCompartmentEntry;
import org.eclipse.graphiti.tb.IToolBehaviorProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.internal.editor.GFPaletteRoot;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.widgets.Composite;

public class RHGraphitiDiagramEditor extends DiagramEditor {

	private EditingDomain editingDomain;
	protected RHGraphitiPaletteFilter paletteFilter = null;
	private static final ImageDescriptor TOOL_ICON = SpdToolEntry.getDefaultIcon();

	public RHGraphitiDiagramEditor(EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

			@Override
			protected DefaultUpdateBehavior createUpdateBehavior() {
				return new DefaultUpdateBehavior(this) {

					// We need to provide our own editing domain so that all editors are working on the same resource.
					// In order to work with a Graphiti diagram, our form creates an editing domain with the Graphiti
					// supplied Command stack.
					@Override
					protected void createEditingDomain() {
						initializeEditingDomain((TransactionalEditingDomain) editingDomain);
					}

					@Override
					protected boolean handleDirtyConflict() {
						return true;
					}

					@Override
					protected Delegate createWorkspaceSynchronizerDelegate() {
						return null;
					}

					@Override
					protected void closeContainer() {

					}

					@Override
					protected void disposeEditingDomain() {

					}

				};
			}
			
			final class RHGraphitiPaletteBehavior extends DefaultPaletteBehavior {

				public RHGraphitiPaletteBehavior(DiagramBehavior diagramBehavior) {
					super(diagramBehavior);
				}
				
				@Override
				protected PaletteRoot createPaletteRoot() {
					return new RHGraphitiPaletteRoot(diagramBehavior.getDiagramTypeProvider());
				}
				
				@SuppressWarnings("restriction")
				@Override
				public void refreshPalette() {
//					List<PaletteEntry> entries = RHGraphitiDiagramEditor.this.getFilteredContents(this.getPaletteRoot());
//					this.getPaletteRoot().setChildren(entries);
//					((RHToolBehaviorProvider) RHGraphitiDiagramEditor.this.getDiagramTypeProvider().getCurrentToolBehaviorProvider()).getPalette();
//					super.refreshPalette();
					RHGraphitiPaletteRoot root = (RHGraphitiPaletteRoot) this.getPaletteRoot();
//					PaletteEntry filterEntry = (PaletteEntry) ((PaletteGroup) root.getChildren().get(0)).getChildren().get(0);
					root.updatePaletteEntries();
//					((PaletteGroup) root.getChildren().get(0)).getChildren().remove(0);
//					((PaletteGroup) root.getChildren().get(0)).getChildren().add(0, filterEntry);
				}
				
				@Override
				protected PaletteViewerProvider createPaletteViewerProvider() {
					return new PaletteViewerProvider(getEditDomain()) {
						
						public PaletteViewer createPaletteViewer(Composite parent) {
							final PaletteViewer viewer = new PaletteViewer();
							viewer.setEditPartFactory(new PaletteEditPartFactory() {
								
								/*
								 * @see org.eclipse.gef.ui.palette.PaletteEditPartFactory#createToolbarEditPart(org.eclipse.gef.EditPart, java.lang.Object)
								 */
								@SuppressWarnings("unchecked")
								@Override
								protected EditPart createGroupEditPart(EditPart parentEditPart, final Object model) {
									GroupEditPart retVal = new GroupEditPart((PaletteGroup) model) {
										
										/*
										 * @see org.eclipse.gef.editparts.AbstractEditPart#createChild(java.lang.Object)
										 */
										@Override
										protected EditPart createChild(Object model) {
											if ("Text".equals(model)) {
												RHGraphitiPaletteFilterEditPart label = new RHGraphitiPaletteFilterEditPart(viewer);
												paletteFilter = label.getPaletteFilter();
												paletteFilter.setPaletteBehavior(RHGraphitiPaletteBehavior.this);
												((RHToolBehaviorProvider) RHGraphitiDiagramEditor.this.getDiagramTypeProvider().getCurrentToolBehaviorProvider()).setFilter(paletteFilter);
												return label;
											}
											return super.createChild(model);
										}
										
										/*
										 * @see org.eclipse.gef.ui.palette.editparts.PaletteEditPart#getModelChildren()
										 */
										@Override
										public List<Object> getModelChildren() {
											List<Object> retVal =  super.getModelChildren();
											retVal.add(0, "Text");
											retVal.add(1, new PaletteSeparator());
											return retVal;
										}
									};
									
									return retVal;
								}
							});

							viewer.createControl(parent);
							configurePaletteViewer(viewer);
							hookPaletteViewer(viewer);
							return viewer;
						}

					};
				}
			}
			
			@Override
			protected DefaultPaletteBehavior createPaletteBehaviour() {
				final DefaultPaletteBehavior paletteBehavior = new RHGraphitiPaletteBehavior(this);
				return paletteBehavior;
			}

			@Override
			protected List<TransferDropTargetListener> createBusinessObjectDropTargetListeners() {
				List<TransferDropTargetListener> retVal = super.createBusinessObjectDropTargetListeners();
				retVal.add(0, new DiagramDropTargetListener(getDiagramContainer().getGraphicalViewer(), this));
				return retVal;
			}

		};
	}

	protected List<PaletteEntry> getFilteredContents(PaletteRoot root) {
		Map<String, List<PaletteEntry>> containerMap = new HashMap<String, List<PaletteEntry>>();

		IResourceFactoryRegistry registry = ResourceFactoryPlugin.getDefault().getResourceFactoryRegistry();
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
		Object tools = root.getChildren().get(0);
		if (tools instanceof PaletteGroup) {
			retVal.add((PaletteGroup) tools);
		}
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
			} else if (paletteFilter != null && paletteFilter.getFilter() != null && !paletteFilter.getFilter().isEmpty() 
					&& !paletteFilter.matches(type)) {
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
		RHToolBehaviorProvider toolBehaviorProvider = (RHToolBehaviorProvider) this.getDiagramTypeProvider().getCurrentToolBehaviorProvider();
//		IPaletteCompartmentEntry[] existingEntries = toolBehaviorProvider.getPalette();
		List<PaletteEntry> retVal = new ArrayList<PaletteEntry>(desc.getImplementationIds().size());
//		retVal.add(new CombinedTemplateCreationEntry(desc.getName(), desc.getDescription(), toolBehaviorProvider., imageDescriptor, imageDescriptor));
//		if (desc.getImplementationIds().size() == 1) {
//			retVal.add(new SpdToolEntry(desc.getName(), desc.getDescription(), desc.getResourceURI(), desc.getIdentifier(), desc.getImplementationIds().get(0),
//				TOOL_ICON));
//		} else {
//			for (String implID : desc.getImplementationIds()) {
//				retVal.add(new SpdToolEntry(desc.getName() + " (" + implID + ")", desc.getDescription(), desc.getResourceURI(), desc.getIdentifier(), implID,
//					TOOL_ICON));
//			}
//		}
		return retVal;
	}

	// This is a major hack that lets the diagram update when changes are made in the overview tab
	@Override
	public void setFocus() {
		super.setFocus();
		Diagram diagram = getDiagramTypeProvider().getDiagram();
		IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();
		if (diagram != null && featureProvider != null) {
			final UpdateContext updateContext = new UpdateContext(diagram);
			final IUpdateFeature updateFeature = featureProvider.getUpdateFeature(new UpdateContext(diagram));
			final IReason updateNeeded = updateFeature.updateNeeded(updateContext);
			if (updateNeeded.toBoolean()) {
				updateFeature.update(updateContext);
			}
		}
	}
}
