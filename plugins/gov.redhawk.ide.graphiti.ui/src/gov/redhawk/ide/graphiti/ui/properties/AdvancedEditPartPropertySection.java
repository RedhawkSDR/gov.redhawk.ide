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
package gov.redhawk.ide.graphiti.ui.properties;

import gov.redhawk.ide.graphiti.ui.adapters.PortEditPartAdapterFactory;
import gov.redhawk.model.sca.ScaProvidesPort;
import gov.redhawk.model.sca.ScaUsesPort;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AdvancedPropertySection;

/**
 * 
 */
public class AdvancedEditPartPropertySection extends AdvancedPropertySection {

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		ISelection newSelection = selection;
		if (selection instanceof StructuredSelection) {
			StructuredSelection ss = (StructuredSelection) selection;
			Object obj = ss.getFirstElement();
			if (obj instanceof EditPart) {
				EditPart ep = (EditPart) obj;
				obj = getScaObjectForEditPart(ep);
			}
			newSelection = new StructuredSelection(new Object[]{obj});
		}
		super.setInput(part, newSelection);
	}
	
	protected Object getScaObjectForEditPart(EditPart ep) {
		IAdapterFactory af = null;
		Class<?> newClass = null;
		Object obj = ep.getModel();
		if (obj instanceof Anchor) {
			af = new PortEditPartAdapterFactory();
			obj = ((Anchor) obj).getLink().getBusinessObjects().get(0);
			if (obj instanceof ProvidesPortStub) {
				newClass = ScaProvidesPort.class;
			} else if (obj instanceof UsesPortStub) {
				newClass = ScaUsesPort.class;
			}
		}
		if (newClass != null) {
			return af.getAdapter(ep, newClass);
		}
		return null;
	}
	
}
