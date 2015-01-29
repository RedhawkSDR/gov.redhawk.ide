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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class RHGraphitiDiagramLabelProvider extends BaseLabelProvider implements ILabelProvider {

	/**
	 * 
	 */
	public RHGraphitiDiagramLabelProvider() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof IStructuredSelection) {
			element = ((IStructuredSelection) element).getFirstElement();
		}
		if (element instanceof AbstractEditPart) {
			element = ((AbstractEditPart) element).getModel();
		}
		if (element instanceof PictogramElement) {
			element = ((PictogramElement) element).getLink().getBusinessObjects().get(0);
		}
		if (element instanceof ComponentInstantiation) {
			return ((ComponentInstantiation) element).getId();
		}
		if (element instanceof UsesPortStub) {
			return ((UsesPortStub) element).getName();
		}
		if (element instanceof ProvidesPortStub) {
			return ((ProvidesPortStub) element).getName();
		}
		return null;
	}

}
