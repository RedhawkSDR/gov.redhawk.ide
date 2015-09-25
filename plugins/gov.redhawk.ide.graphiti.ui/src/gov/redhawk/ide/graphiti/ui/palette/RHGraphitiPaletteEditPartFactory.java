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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteEditPartFactory;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * 
 */
public class RHGraphitiPaletteEditPartFactory extends PaletteEditPartFactory {

	private PaletteViewer viewer;
	
	public RHGraphitiPaletteEditPartFactory(PaletteViewer theViewer) {
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
		return new RHGraphitiPaletteFilterEditPart(viewer);
	}
	
}
