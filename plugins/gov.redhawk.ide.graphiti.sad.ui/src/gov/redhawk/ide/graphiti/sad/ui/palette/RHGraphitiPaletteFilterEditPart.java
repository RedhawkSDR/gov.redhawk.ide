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
package gov.redhawk.ide.graphiti.sad.ui.palette;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.ui.palette.PaletteViewer;

/**
 * 
 */
public class RHGraphitiPaletteFilterEditPart extends org.eclipse.gef.editparts.AbstractGraphicalEditPart {

	private final RHGraphitiPaletteFilter filter;

	public RHGraphitiPaletteFilterEditPart(PaletteViewer viewer) {
		filter = new RHGraphitiPaletteFilter(viewer, this);
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new RHGraphitiPaletteFilterDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, null);
		addEditPartListener(new EditPartListener() {

			@Override
			public void childAdded(EditPart child, int index) {
				
			}

			@Override
			public void partActivated(EditPart editpart) {
//				performDirectEdit();
			}

			@Override
			public void partDeactivated(EditPart editpart) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removingChild(EditPart child, int index) {
				
			}

			@Override
			public void selectedStateChanged(EditPart editpart) {
				performDirectEdit();
				
			}
			
		});
	}

	/**
	 * @return the filter
	 */
	public RHGraphitiPaletteFilter getPaletteFilter() {
		return filter;
	}

	protected IFigure createFigure() {
		RHGraphitiPaletteFilterFigure label = new RHGraphitiPaletteFilterFigure();
		return label;
	}

	private void performDirectEdit() {
		new RHGraphitiPaletteFilterEditManager(this, new RHGraphitiPaletteFilterCellEditorLocator((RHGraphitiPaletteFilterFigure) getFigure()), filter).show();
	}

	public void performRequest(Request request) {
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEdit();
		}
	}

	protected void refreshVisuals() {
		((RHGraphitiPaletteFilterFigure) getFigure()).setText(filter.getFilter());
		super.refreshVisuals();
	}
	
}
