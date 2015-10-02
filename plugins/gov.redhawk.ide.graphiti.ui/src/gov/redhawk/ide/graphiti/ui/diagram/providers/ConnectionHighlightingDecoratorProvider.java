/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

class ConnectionHighlightingDecoratorProvider implements IDecoratorProvider {

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		// Ports always have an invisible anchor overlaid on top of them; ignore everything else
		if (pe instanceof AnchorContainer && !((AnchorContainer) pe).getAnchors().isEmpty()) {
			Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(pe);
			String anchorType = Graphiti.getPeService().getPropertyValue(diagram, DUtil.DIAGRAM_CONNECTION_ANCHOR);
			String repId = Graphiti.getPeService().getPropertyValue(diagram, DUtil.DIAGRAM_CONNECTION_REPID);
			if (isMatch(DUtil.getBusinessObject(pe), anchorType, repId)) {
				return new IDecorator[] {
					new ColorDecorator(null, StyleUtil.COLOR_OK)
				};
			}
		}
		return new IDecorator[0];
	}

	protected boolean isMatch(Object target, String anchorType, String repId) {
		if (anchorType == null || repId == null) {
			return false;
		} else if (target instanceof ProvidesPortStub) {
			return anchorType.equals("uses") && repId.equals(((ProvidesPortStub) target).getProvides().getRepID());
		}  else if (target instanceof UsesPortStub) {
			return anchorType.equals("provides") && repId.equals(((UsesPortStub) target).getUses().getRepID());			
		}
		return false;
	}
}