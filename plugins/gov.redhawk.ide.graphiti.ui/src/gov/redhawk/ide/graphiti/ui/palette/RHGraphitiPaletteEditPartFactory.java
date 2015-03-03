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

import gov.redhawk.ide.graphiti.ui.diagram.providers.AbstractGraphitiToolBehaviorProvider;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

/**
 * 
 */
public class RHGraphitiPaletteEditPartFactory extends PaletteEditPartFactory {

	private DefaultPaletteBehavior paletteBehavior;
	private DiagramBehavior diagramBehavior;
	private PaletteViewer viewer;
	
	public RHGraphitiPaletteEditPartFactory(DefaultPaletteBehavior newPaletteBehavior, DiagramBehavior theDiagramBehavior, PaletteViewer theViewer) {
		paletteBehavior = newPaletteBehavior;
		diagramBehavior = theDiagramBehavior;
		viewer = theViewer;
	}
	
	@Override
	public EditPart createEditPart(EditPart parentEditPart, Object model) {
		if (model instanceof PaletteNamespaceFolder) {
			return createNamespaceFolderEditPart(parentEditPart, model);
		}
		if ("paletteFilter".equals(((PaletteEntry) model).getType())) {
			return createPaletteFilterEditPart(parentEditPart, model);
		}
		return super.createEditPart(parentEditPart, model);
	}
	
	protected EditPart createNamespaceFolderEditPart(EditPart parentEditPart, Object model) {
		return new PaletteNamespaceFolderEditPart((PaletteNamespaceFolder) model);
	}

	protected EditPart createPaletteFilterEditPart(EditPart parentEditPart, Object model) {
		RHGraphitiPaletteFilterEditPart label = new RHGraphitiPaletteFilterEditPart(viewer);
		RHGraphitiPaletteFilter paletteFilter = label.getPaletteFilter();
		paletteFilter.setPaletteBehavior(paletteBehavior);
		((AbstractGraphitiToolBehaviorProvider) diagramBehavior.getDiagramTypeProvider().getCurrentToolBehaviorProvider()).setFilter(paletteFilter);
		return label;
	}
	
}
