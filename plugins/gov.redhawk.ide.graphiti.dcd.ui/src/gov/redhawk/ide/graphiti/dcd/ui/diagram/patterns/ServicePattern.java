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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns;

import gov.redhawk.ide.graphiti.dcd.ext.RHDeviceGxFactory;
import gov.redhawk.ide.graphiti.dcd.ext.ServiceShape;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.NodeImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public class ServicePattern extends AbstractNodeComponentPattern implements IPattern {

	public ServicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Service";
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof DcdComponentInstantiation) {
			DcdComponentInstantiation ci = (DcdComponentInstantiation) context.getNewObject();
			SoftPkg spd = ci.getPlacement().getComponentFileRef().getFile().getSoftPkg();
			if (spd.getDescriptor().getComponent().getComponentType().equals(mil.jpeojtrs.sca.scd.ComponentType.SERVICE.getLiteral())) {
				return true;
			}
		}
		return super.canAdd(context);
	}

	/**
	 * Adds a Service to the diagram. Immediately calls resize at the end to keep sizing and location in one place.
	 */
	@Override
	public PictogramElement add(IAddContext context) {

		// creates shape
		ServiceShape serviceShape = RHDeviceGxFactory.eINSTANCE.createServiceShape();
		serviceShape.init(context, this);

		// set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(serviceShape.getGraphicsAlgorithm(), context.getX(), context.getY());

		// layout
		serviceShape.layout();

		// Check for any needed location adjustments, avoids accidentally stacking shapes
		adjustShapeLocation(serviceShape);

		return serviceShape;
	}

	/**
	 * Return all ServiceShape in Diagram (recursively)
	 * @param containerShape
	 * @return
	 */
	public static List<ServiceShape> getAllServiceShapes(ContainerShape containerShape) {
		List<ServiceShape> children = new ArrayList<ServiceShape>();
		if (containerShape instanceof ServiceShape) {
			children.add((ServiceShape) containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllServiceShapes((ContainerShape) s));
				}
			}
		}
		return children;
	}

	@Override
	public String getOuterImageId() {
		return NodeImageProvider.IMG_COMPONENT_PLACEMENT;
	}

	@Override
	public String getInnerImageId() {
		return NodeImageProvider.IMG_SCA_SERVICE;
	}

	@Override
	public String getStyleForOuter() {
		return StyleUtil.OUTER_SHAPE;
	}

	@Override
	public String getStyleForInner() {
		return StyleUtil.COMPONENT_INNER;
	}

	/**
	 * Returns service, dcd, ports. Order does matter.
	 */
	public List<EObject> getBusinessObjectsToLink(EObject componentInstantiation) {
		// get dcd from diagram, we need to link it to all shapes so the diagram will update when changes occur
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		// ORDER MATTERS, CI must be first
		businessObjectsToLink.add(componentInstantiation);
		businessObjectsToLink.add(dcd);

		return businessObjectsToLink;
	}
}
