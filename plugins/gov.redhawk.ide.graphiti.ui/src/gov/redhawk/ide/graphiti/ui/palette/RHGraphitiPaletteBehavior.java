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

import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;

/**
 * Class implemented to enable filtering of components in the palette.
 */
public class RHGraphitiPaletteBehavior extends DefaultPaletteBehavior {

	/**
	 * @param diagramBehavior
	 */
	public RHGraphitiPaletteBehavior(DiagramBehavior diagramBehavior) {
		super(diagramBehavior);
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

			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
				viewer.setEditPartFactory(new RHGraphitiPaletteEditPartFactory(RHGraphitiPaletteBehavior.this, diagramBehavior, viewer)); 
			}
		};
	}
}
