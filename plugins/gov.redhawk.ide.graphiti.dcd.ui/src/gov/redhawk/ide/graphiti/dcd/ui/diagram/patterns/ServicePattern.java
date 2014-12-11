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
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

//TODO: This should extend AbstractContainerPattern, which is currently located in the graphiti.sad plugin.  
// Waiting for that class to be refactored out before pointing it here
public class ServicePattern extends AbstractContainerPattern implements IPattern {

	public ServicePattern() {
		super(null);
	}
	
	@Override
	public String getCreateName() {
		return "Service";
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof DcdComponentInstantiation;
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof DcdComponentInstantiation) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
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

		StyleUtil.createStyleForUsesPort(getDiagram());
		StyleUtil.createStyleForProvidesPort(getDiagram());

		// layout
		serviceShape.layout();

		// Check for any needed location adjustments, avoids accidentally stacking shapes
		// TODO: Implement this method, see ComponentPattern
		// adjustServiceLocation(serviceShape);

		return serviceShape;
	}

	// TODO add canRemove, see Component Pattern
	// TODO add canDelete, see Component Pattern
	// TODO add delete, see Component Pattern

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof DeviceConfiguration) {
			return true;
		}
		return false;
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		// something is always changing.
		((ServiceShape) context.getPictogramElement()).layout();
		return true;
	}

	public boolean canMoveShape(IMoveShapeContext context) {
		DcdComponentInstantiation dcdComponentInstantiation = (DcdComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());
		if (dcdComponentInstantiation == null) {
			return false;
		}

		// TODO: Another place that needs to be changed if supporting host collocation
		if (context.getTargetContainer() instanceof Diagram) {
			return true;
		}

		return false;
	}

	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return ((ServiceShape) context.getPictogramElement()).updateNeeded(context, this);
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ServiceShape serviceShape = (ServiceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(serviceShape);
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
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ServiceShape serviceShape = (ServiceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		DcdComponentInstantiation ci = (DcdComponentInstantiation) getBusinessObjectForPictogramElement(serviceShape);
		return ci.getUsageName();
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ServiceShape serviceShape = (ServiceShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final DcdComponentInstantiation ci = (DcdComponentInstantiation) getBusinessObjectForPictogramElement(serviceShape);

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
		updatePictogramElement(serviceShape);
	}
	
	/**
	 * Return all ComponentShape in Diagram (recursively)
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
	public String getOuterTitle(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			try {
				return ((DcdComponentInstantiation) obj).getPlacement().getComponentFileRef().getFile().getSoftPkg().getName();
			} catch (NullPointerException e) {
				return "< Component Bad Reference >";
			}
		}
		return null;
	}

	@Override
	public String getInnerTitle(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			return ((DcdComponentInstantiation) obj).getUsageName();
		}
		return null;
	}

	@Override
	public EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			return ((DcdComponentInstantiation) obj).getUses();
		}
		return null;
	}

	@Override
	public EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			return ((DcdComponentInstantiation) obj).getProvides();
		}
		return null;
	}

	@Override
	public ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof DcdComponentInstantiation) {
			return ((DcdComponentInstantiation) obj).getInterfaceStub();
		}
		return null;
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
