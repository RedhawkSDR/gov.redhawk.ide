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
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

public class UsesPortPattern extends AbstractPortPattern<UsesPortStub> {

	public UsesPortPattern() {
		super(UsesPortStub.class);
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		return RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER.equals(Graphiti.getPeService().getPropertyValue(pictogramElement, DUtil.SHAPE_TYPE));
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		return false;
	}

	@Override
	protected String getStyleId(UsesPortStub usesPortStub) {
		if (isExternalPort(usesPortStub)) {
			return StyleUtil.EXTERNAL_USES_PORT;
		} else {
			return StyleUtil.USES_PORT;
		}
	}

	@Override
	protected String getPortName(UsesPortStub usesPortStub) {
		return usesPortStub.getName();
	}

	protected boolean isExternalPort(UsesPortStub usesPortStub) {
		EObject container = usesPortStub.eContainer();
		if (container instanceof SadComponentInstantiation) {
			SadComponentInstantiation instantiation = (SadComponentInstantiation) container;
			SoftwareAssembly sad = ScaEcoreUtils.getEContainerOfType(instantiation, SoftwareAssembly.class);
			if (sad != null && sad.getExternalPorts() != null) {
				for (Port externalPort : sad.getExternalPorts().getPort()) {
					if (instantiation == externalPort.getComponentInstantiationRef().getInstantiation()
						&& usesPortStub.getName().equals(externalPort.getUsesIdentifier())) {
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
		ContainerShape usesPortContainerShape = Graphiti.getPeService().createContainerShape(parentShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortContainerShape, DUtil.SHAPE_TYPE, RHContainerShapeImpl.SHAPE_USES_PORT_CONTAINER);
		Rectangle usesPortContainerShapeRectangle = Graphiti.getCreateService().createPlainRectangle(usesPortContainerShape);
		usesPortContainerShapeRectangle.setFilled(false);
		usesPortContainerShapeRectangle.setLineVisible(false);
		UsesPortStub usesPortStub = (UsesPortStub) context.getNewObject();
		link(usesPortContainerShape, usesPortStub);

		// Port rectangle; this is created as its own shape because Anchors do not support decorators (for things
		// like highlighting)
		ContainerShape usesPortShape = Graphiti.getPeService().createContainerShape(usesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortShape, DUtil.SHAPE_TYPE, RHContainerShapeImpl.SHAPE_USES_PORT_RECTANGLE);
		Rectangle usesPortRectangle = Graphiti.getCreateService().createPlainRectangle(usesPortShape);
		StyleUtil.setStyle(usesPortRectangle, getStyleId(usesPortStub));
		Graphiti.getGaLayoutService().setSize(usesPortRectangle, AbstractPortPattern.PORT_SHAPE_WIDTH, AbstractPortPattern.PORT_SHAPE_HEIGHT);
		link(usesPortShape, usesPortStub);

		// Port anchor
		FixPointAnchor fixPointAnchor = createPortAnchor(usesPortShape, AbstractPortPattern.PORT_SHAPE_WIDTH);
		link(fixPointAnchor, usesPortStub);

		// Port text
		Shape usesPortTextShape = Graphiti.getPeService().createShape(usesPortContainerShape, false);
		Text usesPortText = Graphiti.getCreateService().createPlainText(usesPortTextShape, usesPortStub.getName());
		StyleUtil.setStyle(usesPortText, StyleUtil.PORT_TEXT);
		usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		Graphiti.getGaLayoutService().setLocation(usesPortText, 0, 0);

		return usesPortContainerShape;
	}

	@Override
	public boolean canAdd(IAddContext context) {
		return true;
	}
}
