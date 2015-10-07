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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.tb.ColorDecorator;
import org.eclipse.graphiti.tb.IColorDecorator;
import org.eclipse.graphiti.tb.IDecorator;
import org.eclipse.graphiti.util.IColorConstant;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class PortMonitorDecoratorProvider implements IDecoratorProvider {

	private static final IDecorator[] NO_DECORATORS = new IDecorator[0];

	@Override
	public IDecorator[] getDecorators(PictogramElement pe) {
		if ((pe instanceof AnchorContainer) && !((AnchorContainer) pe).getAnchors().isEmpty()) {
			ProvidesPortStub portStub = getProvidesPort(pe);
			if (portStub != null) {
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(pe, RHContainerShape.class);
				if (portStub.getProvides() != null && componentShape != null) {
					String portName = portStub.getProvides().getName();
					IDecorator decorator = getMonitoredPortDecorator(componentShape, portName);
					if (decorator != null) {
						return new IDecorator[] { decorator };
					}
				}
			}
		} else if (pe instanceof Connection){
			ConnectInterface< ? , ? , ? > connectInterface = (ConnectInterface< ?, ?, ? >) Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
			if (connectInterface != null) {
				Connection connection = (Connection) pe;
				RHContainerShape componentShape = ScaEcoreUtils.getEContainerOfType(connection.getStart(), RHContainerShape.class);
				String styleId = componentShape.getConnectionMap().get(connectInterface.getId());
				if (styleId != null) {
					IColorConstant color = getMonitorColor(styleId);
					IColorDecorator decorator = new ColorDecorator(color, color);
					return new IDecorator[] { decorator };
				}
			}
		}
		return NO_DECORATORS;
	}

	private ProvidesPortStub getProvidesPort(PictogramElement pe) {
		ProvidesPortStub portStub = null;
		for (EObject object : Graphiti.getLinkService().getAllBusinessObjectsForLinkedPictogramElement(pe)) {
			if (object instanceof ProvidesPortStub) {
				if (portStub == null) {
					portStub = (ProvidesPortStub) object;
				} else {
					// More than one provides port is linked with the PictogramElement, so it must be a super port
					return null;
				}
			}
		}
		return portStub;
	}

	protected IColorConstant getMonitorColor(String styleId) {
		if (styleId != null) {
			if (StyleUtil.PORT_STYLE_OK.equals(styleId) || StyleUtil.CONNECTION_OK.equals(styleId)) {
				return StyleUtil.COLOR_OK;
			} else if (StyleUtil.PORT_STYLE_WARN4.equals(styleId) || StyleUtil.CONNECTION_ERROR.equals(styleId)) {
				return StyleUtil.COLOR_ERROR;
			} else {
				return StyleUtil.COLOR_WARN;
			}
		}
		return null;
	}

	protected IDecorator getMonitoredPortDecorator(RHContainerShape componentShape, String portName) {
		String state = componentShape.getPortStates().get(portName);
		if (state != null) {
			return new ColorDecorator(null, getMonitorColor(state));
		}
		return null;
	}
}
