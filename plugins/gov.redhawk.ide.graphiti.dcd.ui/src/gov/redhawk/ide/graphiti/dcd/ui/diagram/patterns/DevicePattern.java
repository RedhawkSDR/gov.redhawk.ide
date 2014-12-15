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

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ext.RHDeviceGxFactory;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.NodeImageProvider;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public class DevicePattern extends AbstractNodeComponentPattern implements IPattern {

	public DevicePattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return "Device";
	}

	/**
	 * Adds a Device to the diagram. Immediately calls resize at the end to keep sizing and location in one place.
	 */
	@Override
	public PictogramElement add(IAddContext context) {

		// creates shape
		DeviceShape deviceShape = RHDeviceGxFactory.eINSTANCE.createDeviceShape();
		deviceShape.init(context, this);

		// set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(deviceShape.getGraphicsAlgorithm(), context.getX(), context.getY());

		StyleUtil.createStyleForUsesPort(getDiagram());
		StyleUtil.createStyleForProvidesPort(getDiagram());

		// layout
		deviceShape.layout();

		// Check for any needed location adjustments, avoids accidentally stacking shapes
		// TODO: Implement this method, see ComponentPattern
		// adjustDeviceLocation(deviceShape);

		return deviceShape;
	}

	/**
	 * Return all DeviceShape in Diagram (recursively)
	 * @param containerShape
	 * @return
	 */
	public static List<DeviceShape> getAllDeviceShapes(ContainerShape containerShape) {
		List<DeviceShape> children = new ArrayList<DeviceShape>();
		if (containerShape instanceof DeviceShape) {
			children.add((DeviceShape) containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllDeviceShapes((ContainerShape) s));
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
		return NodeImageProvider.IMG_COMPONENT_INSTANCE;
	}

	@Override
	public Style createStyleForOuter() {
		return StyleUtil.createStyleForComponentOuter(getDiagram());
	}

	@Override
	public Style createStyleForInner() {
		return StyleUtil.createStyleForComponentInner(getDiagram());
	}

	/**
	 * Returns device, dcd, ports. Order does matter.
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

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		DeviceShape deviceShape = (DeviceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(deviceShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof DcdComponentInstantiation && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInnerTitle(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			return getInnerTitle((DcdComponentInstantiation) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the inner shape
	 * @param ci
	 * @return
	 */
	public String getInnerTitle(DcdComponentInstantiation ci) {
		return ci.getUsageName();
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		DeviceShape deviceShape = (DeviceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final DcdComponentInstantiation ci = (DcdComponentInstantiation) getBusinessObjectForPictogramElement(deviceShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				ci.setUsageName(value);
			}
		});

		// perform update, redraw
		updatePictogramElement(deviceShape);
	}

	/**
	 * Determines whether we need to update the diagram from the model.
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return ((DeviceShape) context.getPictogramElement()).updateNeeded(context, this);
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		DeviceShape deviceShape = (DeviceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) getBusinessObjectForPictogramElement(deviceShape);
		return ci.getUsageName();
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason updated = ((DeviceShape) context.getPictogramElement()).update(context, this);

		// if we updated redraw
		if (updated.toBoolean()) {
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof DcdComponentInstantiation) {
			return true;
		}
		return false;
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {

		((DeviceShape) context.getPictogramElement()).layout();

		// something is always changing.
		return true;
	}

}
