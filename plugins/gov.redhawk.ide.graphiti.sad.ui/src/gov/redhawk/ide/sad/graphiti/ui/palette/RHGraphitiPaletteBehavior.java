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
package gov.redhawk.ide.sad.graphiti.ui.palette;

import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.WaveformToolBehaviorProvider;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.internal.ui.palette.editparts.GroupEditPart;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.swt.widgets.Composite;

/**
 * Class implemented to enable filtering of components in the palette.
 */
public class RHGraphitiPaletteBehavior extends DefaultPaletteBehavior {

	private DiagramBehavior diagramBehavior;
	private RHGraphitiPaletteFilter paletteFilter;
	
	/**
	 * @param diagramBehavior
	 */
	public RHGraphitiPaletteBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
		this.diagramBehavior = diagramBehavior;
	}
	
	@Override
	protected PaletteRoot createPaletteRoot() {
		return new RHGraphitiPaletteRoot(diagramBehavior.getDiagramTypeProvider());
	}
	
	@Override
	public void refreshPalette() {
		RHGraphitiPaletteRoot root = (RHGraphitiPaletteRoot) this.getPaletteRoot();
		root.updatePaletteEntries();
	}
	
	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(diagramBehavior.getEditDomain()) {
			
			public PaletteViewer createPaletteViewer(Composite parent) {
				final PaletteViewer viewer = new PaletteViewer();
				viewer.setEditPartFactory(new PaletteEditPartFactory() {
					
					/*
					 * @see org.eclipse.gef.ui.palette.PaletteEditPartFactory#createToolbarEditPart(org.eclipse.gef.EditPart, java.lang.Object)
					 */
					@SuppressWarnings({ "unchecked", "restriction" })
					@Override
					protected EditPart createGroupEditPart(EditPart parentEditPart, final Object model) {
						GroupEditPart retVal = new GroupEditPart((PaletteGroup) model) {
							
							/*
							 * @see org.eclipse.gef.editparts.AbstractEditPart#createChild(java.lang.Object)
							 */
							@Override
							protected EditPart createChild(Object model) {
								if ("Text".equals(model)) {
									// This part is the reason every class in this package exists
									RHGraphitiPaletteFilterEditPart label = new RHGraphitiPaletteFilterEditPart(viewer);
									paletteFilter = label.getPaletteFilter();
									paletteFilter.setPaletteBehavior(RHGraphitiPaletteBehavior.this);
									((WaveformToolBehaviorProvider) diagramBehavior.getDiagramTypeProvider().getCurrentToolBehaviorProvider()).setFilter(paletteFilter);
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
								// Stick in placeholder for filter and separator at the top
								retVal.add(0, "Text");
								retVal.add(1, new PaletteSeparator());
								return retVal;
							}
						};
						retVal.setParent(parentEditPart);
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
