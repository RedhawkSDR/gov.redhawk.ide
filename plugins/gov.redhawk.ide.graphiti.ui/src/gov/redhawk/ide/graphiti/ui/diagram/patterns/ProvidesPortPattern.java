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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class ProvidesPortPattern extends AbstractPortPattern<ProvidesPortStub> {

	public ProvidesPortPattern() {
		super(ProvidesPortStub.class);
	}

	@Override
	protected String getStyleId(ProvidesPortStub port) {
		if (isExternalPort(port)) {
			return StyleUtil.EXTERNAL_PROVIDES_PORT;
		} else {
			return StyleUtil.PROVIDES_PORT;
		}
	}

	@Override
	protected String getPortName(ProvidesPortStub port) {
		return port.getName();
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		return RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER.equals(Graphiti.getPeService().getPropertyValue(pictogramElement, DUtil.SHAPE_TYPE));
	}

	protected boolean isExternalPort(ProvidesPortStub providesPortStub) {
		EObject container = providesPortStub.eContainer();
		if (container instanceof SadComponentInstantiation) {
			SadComponentInstantiation instantiation = (SadComponentInstantiation) container;
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(instantiation, SoftwareAssembly.class);
			if (sad != null && sad.getExternalPorts() != null) {
				for (Port externalPort : sad.getExternalPorts().getPort()) {
					if (instantiation == externalPort.getComponentInstantiationRef().getInstantiation()
						&& providesPortStub.getName().equals(externalPort.getProvidesIdentifier())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public PictogramElement add(IAddContext context) {
		// Outer invisible container
		ContainerShape parentShape = context.getTargetContainer();
		ContainerShape providesPortContainerShape = Graphiti.getCreateService().createContainerShape(parentShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortContainerShape, DUtil.SHAPE_TYPE, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_CONTAINER);
		Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createPlainRectangle(providesPortContainerShape);
		providesPortContainerShapeRectangle.setFilled(false);
		providesPortContainerShapeRectangle.setLineVisible(false);
		ProvidesPortStub providesPortStub = (ProvidesPortStub) context.getNewObject();
		link(providesPortContainerShape, providesPortStub);

		// Port rectangle; this is created as its own shape because Anchors do not support decorators (for things
		// like highlighting)
		ContainerShape providesPortShape = Graphiti.getPeService().createContainerShape(providesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortShape, DUtil.SHAPE_TYPE, RHContainerShapeImpl.SHAPE_PROVIDES_PORT_RECTANGLE);
		Rectangle providesPortRectangle = Graphiti.getCreateService().createPlainRectangle(providesPortShape);
		String styleId = getStyleId(providesPortStub);
		StyleUtil.setStyle(providesPortRectangle, styleId);
		Graphiti.getGaLayoutService().setSize(providesPortRectangle, AbstractPortPattern.PORT_SHAPE_WIDTH, AbstractPortPattern.PORT_SHAPE_HEIGHT);
		link(providesPortShape, providesPortStub);

		// Port anchor
		FixPointAnchor fixPointAnchor = createPortAnchor(providesPortShape, 0);
		link(fixPointAnchor, providesPortStub);

		// Port text
		Shape providesPortTextShape = Graphiti.getCreateService().createShape(providesPortContainerShape, false);
		Text providesPortText = Graphiti.getCreateService().createPlainText(providesPortTextShape, providesPortStub.getName());
		StyleUtil.setStyle(providesPortText, StyleUtil.PORT_TEXT);
		Graphiti.getGaLayoutService().setLocation(providesPortText, AbstractPortPattern.PORT_SHAPE_WIDTH + RHContainerShapeImpl.PORT_NAME_HORIZONTAL_PADDING,
			0);

		return providesPortContainerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return true;
	}
}
