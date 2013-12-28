/******************************************************************************* 
 * Copyright (c) 2011, 2012 Red Hat, Inc. 
 *  All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 *
 * @author Ivar Meikas
 ******************************************************************************/
package org.eclipse.bpmn2.modeler.core.utils;

import static org.eclipse.bpmn2.modeler.core.features.activity.AbstractAddActivityFeature.ACTIVITY_DECORATOR;

import java.awt.Dimension;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.BoundaryEvent;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.CallActivity;
import org.eclipse.bpmn2.CallableElement;
import org.eclipse.bpmn2.ChoreographyActivity;
import org.eclipse.bpmn2.ChoreographyTask;
import org.eclipse.bpmn2.CorrelationPropertyRetrievalExpression;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.EndEvent;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.FlowElement;
import org.eclipse.bpmn2.FlowElementsContainer;
import org.eclipse.bpmn2.Group;
import org.eclipse.bpmn2.ImplicitThrowEvent;
import org.eclipse.bpmn2.IntermediateCatchEvent;
import org.eclipse.bpmn2.IntermediateThrowEvent;
import org.eclipse.bpmn2.Lane;
import org.eclipse.bpmn2.Message;
import org.eclipse.bpmn2.MessageEventDefinition;
import org.eclipse.bpmn2.MessageFlow;
import org.eclipse.bpmn2.Operation;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.ReceiveTask;
import org.eclipse.bpmn2.SendTask;
import org.eclipse.bpmn2.StartEvent;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.TextAnnotation;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.bpmn2.modeler.core.ModelHandler;
import org.eclipse.bpmn2.modeler.core.ModelHandlerLocator;
import org.eclipse.bpmn2.modeler.core.di.DIUtils;
import org.eclipse.bpmn2.modeler.core.features.AbstractConnectionRouter;
import org.eclipse.bpmn2.modeler.core.preferences.Bpmn2Preferences;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.ILayoutFeature;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IContext;
import org.eclipse.graphiti.features.context.IPictogramElementContext;
import org.eclipse.graphiti.features.context.ITargetContext;
import org.eclipse.graphiti.features.context.impl.LayoutContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.algorithms.AbstractText;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.AnchorContainer;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ConnectionDecorator;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FreeFormConnection;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaService;
import org.eclipse.graphiti.services.IPeService;

public class FeatureSupport {
	public static final String IS_HORIZONTAL_PROPERTY = "isHorizontal"; //$NON-NLS-1$

	public static boolean isValidFlowElementTarget(ITargetContext context) {
		boolean intoDiagram = context.getTargetContainer() instanceof Diagram;
		boolean intoLane = isTargetLane(context) && isTargetLaneOnTop(context);
		boolean intoParticipant = isTargetParticipant(context);
		boolean intoFlowElementContainer = isTargetFlowElementsContainer(context);
		boolean intoGroup = isTargetGroup(context);
		return (intoDiagram || intoLane || intoParticipant || intoFlowElementContainer) && !intoGroup;
	}
	
	public static boolean isValidArtifactTarget(ITargetContext context) {
		boolean intoDiagram = context.getTargetContainer() instanceof Diagram;
		boolean intoLane = isTargetLane(context) && isTargetLaneOnTop(context);
		boolean intoParticipant = isTargetParticipant(context) && !isChoreographyParticipantBand(context.getTargetContainer());
		boolean intoSubProcess = isTargetSubProcess(context);
		boolean intoSubChoreography = isTargetSubChoreography(context);
		boolean intoGroup = isTargetGroup(context);
		return (intoDiagram || intoLane || intoParticipant || intoSubProcess || intoSubChoreography) && !intoGroup;
	}

	public static boolean isChoreographyParticipantBand(PictogramElement element) {
		EObject container = element.eContainer();
		if (container instanceof PictogramElement) {
			PictogramElement containerElem = (PictogramElement) container;
			Object bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(containerElem);
			if (bo instanceof ChoreographyActivity) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidDataTarget(ITargetContext context) {
		Object containerBO = BusinessObjectUtil.getBusinessObjectForPictogramElement( context.getTargetContainer() );
		boolean intoDiagram = containerBO instanceof BPMNDiagram;
		boolean intoSubProcess = containerBO instanceof SubProcess;
		if (intoSubProcess || intoDiagram)
			return true;
		if (FeatureSupport.isTargetLane(context) && FeatureSupport.isTargetLaneOnTop(context))
			return true;
		if (FeatureSupport.isTargetParticipant(context))
			return true;
		return false;
	}
	
	public static boolean isTargetSubProcess(ITargetContext context) {
		return BusinessObjectUtil.containsElementOfType(context.getTargetContainer(), SubProcess.class);
	}
	
	public static boolean isTargetSubChoreography(ITargetContext context) {
		return BusinessObjectUtil.containsElementOfType(context.getTargetContainer(), SubChoreography.class);
	}

	public static boolean isTargetLane(ITargetContext context) {
		return isLane(context.getTargetContainer());
	}

	public static boolean isLane(PictogramElement element) {
		return BusinessObjectUtil.containsElementOfType(element, Lane.class);
	}

	public static Lane getTargetLane(ITargetContext context) {
		PictogramElement element = context.getTargetContainer();
		return BusinessObjectUtil.getFirstElementOfType(element, Lane.class);
	}

	public static boolean isTargetGroup(ITargetContext context) {
		Group group = BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(), Group.class);
		return group != null;
	}
	
	public static boolean isTargetParticipant(ITargetContext context) {
		return isParticipant(context.getTargetContainer());
	}

	public static boolean isParticipant(PictogramElement element) {
		return BusinessObjectUtil.containsElementOfType(element, Participant.class);
	}

	public static Participant getTargetParticipant(ITargetContext context) {
		PictogramElement element = context.getTargetContainer();
		return BusinessObjectUtil.getFirstElementOfType(element, Participant.class);
	}

	public static SubProcess getTargetSubProcess(ITargetContext context) {
		PictogramElement element = context.getTargetContainer();
		return BusinessObjectUtil.getFirstElementOfType(element, SubProcess.class);
	}

	public static boolean isTargetFlowElementsContainer(ITargetContext context) {
		return BusinessObjectUtil.containsElementOfType(context.getTargetContainer(),
				FlowElementsContainer.class);
	}

	public static FlowElementsContainer getTargetFlowElementsContainer(ITargetContext context) {
		PictogramElement element = context.getTargetContainer();
		return BusinessObjectUtil.getFirstElementOfType(element, FlowElementsContainer.class);
	}
	
	public static boolean isLaneOnTop(Lane lane) {
		return lane.getChildLaneSet() == null || lane.getChildLaneSet().getLanes().isEmpty();
	}

	public static boolean isTargetLaneOnTop(ITargetContext context) {
		Lane lane = BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(), Lane.class);
		return lane.getChildLaneSet() == null || lane.getChildLaneSet().getLanes().isEmpty();
	}

	public static boolean isHorizontal(ContainerShape container) {
		EObject parent = container.eContainer();
		if (parent instanceof PictogramElement) {
			// participant bands are always "vertical" so that
			// the label is drawn horizontally by the LayoutFeature
			if (BusinessObjectUtil.getFirstElementOfType((PictogramElement)parent, ChoreographyTask.class) != null)
				return false;
		}
		String v = Graphiti.getPeService().getPropertyValue(container, IS_HORIZONTAL_PROPERTY);
		if (v==null) {
			return Bpmn2Preferences.getInstance(container).isHorizontalDefault();
		}
		return Boolean.parseBoolean(v);
	}
	
	public static void setHorizontal(ContainerShape container, boolean isHorizontal) {
		Graphiti.getPeService().setPropertyValue(container, IS_HORIZONTAL_PROPERTY, Boolean.toString(isHorizontal));
		BPMNShape bs = BusinessObjectUtil.getFirstElementOfType(container, BPMNShape.class);
		if (bs!=null)
			bs.setIsHorizontal(isHorizontal);
	}
	
	public static boolean isHorizontal(IContext context) {
		Object v = context.getProperty(IS_HORIZONTAL_PROPERTY);
		if (v==null) {
			// TODO: get default orientation from preferences
			return true;
		}
		return (Boolean)v;
	}
	
	public static void setHorizontal(IContext context, boolean isHorizontal) {
		context.putProperty(IS_HORIZONTAL_PROPERTY, isHorizontal);
	}
	
	public static List<PictogramElement> getContainerChildren(ContainerShape container) {
		List<PictogramElement> list = new ArrayList<PictogramElement>();
		for (PictogramElement pe : container.getChildren()) {
			String value = Graphiti.getPeService().getPropertyValue(pe, ACTIVITY_DECORATOR);
			if (value!=null && "true".equals(value)) //$NON-NLS-1$
				continue;
			list.add(pe);
		}
		return list;
	}

	public static List<PictogramElement> getContainerDecorators(ContainerShape container) {
		List<PictogramElement> list = new ArrayList<PictogramElement>();
		for (PictogramElement pe : container.getChildren()) {
			String value = Graphiti.getPeService().getPropertyValue(pe, ACTIVITY_DECORATOR);
			if (value!=null && "true".equals(value)) //$NON-NLS-1$
				list.add(pe);
		}
		return list;
	}
	
	public static void setContainerChildrenVisible(ContainerShape container, boolean visible) {
		for (PictogramElement pe : getContainerChildren(container)) {
			pe.setVisible(visible);
			if (pe instanceof AnchorContainer) {
				AnchorContainer ac = (AnchorContainer)pe;
				for (Anchor a : ac.getAnchors()) {
					for (Connection c : a.getOutgoingConnections()) {
						c.setVisible(visible);
						for (ConnectionDecorator decorator : c.getConnectionDecorators()) {
							decorator.setVisible(visible);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Use ModelHandler.getInstance(diagram) instead
	 * 
	 * @param diagram
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public static ModelHandler getModelHanderInstance(Diagram diagram) throws IOException {
		return ModelHandlerLocator.getModelHandler(diagram.eResource());
	}

	public static void redraw(ContainerShape container) {
		ContainerShape root = getRootContainer(container);
		resizeRecursively(root);
		postResizeFixLenghts(root);
		updateDI(root);
	}

	private static void updateDI(ContainerShape root) {
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(root);

		Class<?> instanceClass = BusinessObjectUtil.getFirstElementOfType(root, BaseElement.class).eClass()
				.getInstanceClass();
		DIUtils.updateDIShape(root);
	}

	public static ContainerShape getRootContainer(ContainerShape container) {
		ContainerShape parent = container.getContainer();
		EObject bo = BusinessObjectUtil.getFirstElementOfType(parent, BaseElement.class);
		if (bo != null && (bo instanceof Lane || bo instanceof Participant)) {
			return getRootContainer(parent);
		}
		return container;
	}

	private static Dimension resize(ContainerShape container) {
		EObject elem = BusinessObjectUtil.getFirstElementOfType(container, BaseElement.class);
		IGaService service = Graphiti.getGaService();
		int height = 0;
		int width = container.getGraphicsAlgorithm().getWidth() - 30;
		boolean horz = isHorizontal(container);
		if (horz) {
			height = 0;
			width = container.getGraphicsAlgorithm().getWidth() - 30;
		}
		else {
			width = 0;
			height = container.getGraphicsAlgorithm().getHeight() - 30;
		}

		EList<Shape> children = container.getChildren();
		ECollections.sort(children, new SiblingLaneComparator());
		for (Shape s : children) {
			Object bo = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (bo != null && (bo instanceof Lane || bo instanceof Participant) && !bo.equals(elem)) {
				GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
				if (horz) {
					service.setLocation(ga, 30, height);
					height += ga.getHeight() - 1;
					if (ga.getWidth() >= width) {
						width = ga.getWidth();
					} else {
						service.setSize(ga, width, ga.getHeight());
					}
				}
				else {
					service.setLocation(ga, width, 30);
					width += ga.getWidth() - 1;
					if (ga.getHeight() >= height) {
						height = ga.getHeight();
					} else {
						service.setSize(ga, ga.getWidth(), height);
					}
				}
			}
		}

		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();

		if (horz) {
			if (height == 0) {
				return new Dimension(ga.getWidth(), ga.getHeight());
			} else {
				int newWidth = width + 30;
				int newHeight = height + 1;
				service.setSize(ga, newWidth, newHeight);
	
				for (Shape s : children) {
					GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
					if (childGa instanceof AbstractText) {
						AbstractText text = (AbstractText)childGa;
						text.setAngle(-90);
						service.setLocationAndSize(text, 5, 0, 15, newHeight);
					} else if (childGa instanceof Polyline) {
						Polyline line = (Polyline) childGa;
						Point p0 = line.getPoints().get(0);
						Point p1 = line.getPoints().get(1);
						p0.setX(30); p0.setY(0);
						p1.setX(30); p1.setY(newHeight);
					}
				}
	
				return new Dimension(newWidth, newHeight);
			}
		}
		else {
			if (width == 0) {
				return new Dimension(ga.getWidth(), ga.getHeight());
			} else {
				int newWidth = width + 1;
				int newHeight = height + 30;
				service.setSize(ga, newWidth, newHeight);
	
				for (Shape s : children) {
					GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
					if (childGa instanceof AbstractText) {
						AbstractText text = (AbstractText)childGa;
						text.setAngle(0);
						service.setLocationAndSize(text, 0, 5, newWidth, 15);
					} else if (childGa instanceof Polyline) {
						Polyline line = (Polyline) childGa;
						Point p0 = line.getPoints().get(0);
						Point p1 = line.getPoints().get(1);
						p0.setX(0); p0.setY(30);
						p1.setX(newWidth); p1.setY(30);
					}
				}
	
				return new Dimension(newWidth, newHeight);
			}
		}
	}
	
	private static Dimension resizeRecursively(ContainerShape root) {
		BaseElement elem = BusinessObjectUtil.getFirstElementOfType(root, BaseElement.class);
		List<Dimension> dimensions = new ArrayList<Dimension>();
		IGaService service = Graphiti.getGaService();
		int foundContainers = 0;
		boolean horz = isHorizontal(root);

		for (Shape s : root.getChildren()) {
			Object bo = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (checkForResize(elem, s, bo)) {
				foundContainers += 1;
				Dimension d = resizeRecursively((ContainerShape) s);
				if (d != null) {
					dimensions.add(d);
				}
			}
		}

		if (dimensions.isEmpty()) {
			GraphicsAlgorithm ga = root.getGraphicsAlgorithm();
			for (Shape s : root.getChildren()) {
				GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
				if (childGa instanceof AbstractText) {
					AbstractText text = (AbstractText)childGa;
					if (horz) {
						text.setAngle(-90);
						service.setLocationAndSize(text, 5, 0, 15, ga.getHeight());
					}
					else {
						text.setAngle(0);
						service.setLocationAndSize(text, 0, 5, ga.getWidth(), 15);
					}
				} else if (childGa instanceof Polyline) {
					Polyline line = (Polyline) childGa;
					Point p0 = line.getPoints().get(0);
					Point p1 = line.getPoints().get(1);
					if (horz) {
						p0.setX(30); p0.setY(0);
						p1.setX(30); p1.setY(ga.getHeight());
					}
					else {
						p0.setX(0); p0.setY(30);
						p1.setX(ga.getWidth()); p1.setY(30);
					}
				}
			}
			return new Dimension(ga.getWidth(), ga.getHeight());
		}

		if (foundContainers > 0) {
			return resize(root);
		}

		return getMaxDimension(horz, dimensions);
	}

	/**
	 * One can only resize lanes and participants
	 */
	private static boolean checkForResize(BaseElement currentBo, Shape s, Object bo) {
		if (!(s instanceof ContainerShape)) {
			return false;
		}
		if (bo == null) {
			return false;
		}
		if (!(bo instanceof Lane || bo instanceof Participant)) {
			return false;
		}
		return !bo.equals(currentBo);
	}

	private static Dimension getMaxDimension(boolean horz, List<Dimension> dimensions) {
		if (dimensions.isEmpty()) {
			return null;
		}
		int height = 0;
		int width = 0;

		if (horz) {
			for (Dimension d : dimensions) {
				height += d.height;
				if (d.width > width) {
					width = d.width;
				}
			}
		}
		else {
			for (Dimension d : dimensions) {
				width += d.width;
				if (d.height > height) {
					height = d.height;
				}
			}
		}
		return new Dimension(width, height);
	}

	private static void postResizeFixLenghts(ContainerShape root) {
		IGaService service = Graphiti.getGaService();
		BaseElement elem = BusinessObjectUtil.getFirstElementOfType(root, BaseElement.class);
		GraphicsAlgorithm ga = root.getGraphicsAlgorithm();
		int width = ga.getWidth() - 30;
		int height = ga.getHeight() - 30;
		boolean horz = isHorizontal(root);

		for (Shape s : root.getChildren()) {
			Object o = BusinessObjectUtil.getFirstElementOfType(s, BaseElement.class);
			if (checkForResize(elem, s, o)) {
				GraphicsAlgorithm childGa = s.getGraphicsAlgorithm();
				if (horz)
					service.setSize(childGa, width, childGa.getHeight());
				else
					service.setSize(childGa, childGa.getWidth(), height);
				DIUtils.updateDIShape(s);
				postResizeFixLenghts((ContainerShape) s);
			}
		}
		DIUtils.updateDIShape(root);
	}

	public static String getShapeValue(IPictogramElementContext context) {
		String value = null;

		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) pe;
			for (Shape shape : cs.getChildren()) {
				if (shape.getGraphicsAlgorithm() instanceof AbstractText) {
					AbstractText text = (AbstractText) shape.getGraphicsAlgorithm();
					value = text.getValue();
				}
			}
		}
		return value;
	}

	public static String getBusinessValue(IPictogramElementContext context) {
		Object o = BusinessObjectUtil.getFirstElementOfType(context.getPictogramElement(), BaseElement.class);
		if (o instanceof FlowElement) {
			FlowElement e = (FlowElement) o;
			return e.getName();
		} else if (o instanceof TextAnnotation) {
			TextAnnotation a = (TextAnnotation) o;
			return a.getText();
		} else if (o instanceof Participant) {
			Participant p = (Participant) o;
			return p.getName();
		} else if (o instanceof Lane) {
			Lane l = (Lane) o;
			return l.getName();
		}
		return null;
	}

	public static Participant getTargetParticipant(ITargetContext context, ModelHandler handler) {
		if (context.getTargetContainer() instanceof Diagram) {
			return handler.getInternalParticipant();
		}

		Object bo = BusinessObjectUtil.getFirstElementOfType(context.getTargetContainer(), BaseElement.class);

		if (bo instanceof Participant) {
			return (Participant) bo;
		}

		return handler.getParticipant(bo);
	}

	public static Shape getShape(ContainerShape container, String property, String expectedValue) {
		IPeService peService = Graphiti.getPeService();
		Iterator<Shape> iterator = peService.getAllContainedShapes(container).iterator();
		while (iterator.hasNext()) {
			Shape shape = iterator.next();
			String value = peService.getPropertyValue(shape, property);
			if (value != null && value.equals(expectedValue)) {
				return shape;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends EObject> T getChildElementOfType(PictogramElement container, String property, String expectedValue, Class<T> clazz) {
		IPeService peService = Graphiti.getPeService();
		Iterator<PictogramElement> iterator = peService.getAllContainedPictogramElements(container).iterator();
		while (iterator.hasNext()) {
			PictogramElement pe = iterator.next();
			String value = peService.getPropertyValue(pe, property);
			if (value != null && value.equals(expectedValue) && clazz.isInstance(pe)) {
				return (T) pe;
			}
		}
		return null;
	}
	
	/**
	 * Returns a list of {@link PictogramElement}s which contains an element to the
	 * assigned businessObjectClazz, i.e. the list contains {@link PictogramElement}s
	 * which meet the following constraint:<br>
	 * <code>
	 * 	foreach child of root:<br>
	 *  BusinessObjectUtil.containsChildElementOfType(child, businessObjectClazz) == true
	 * </code>
	 * @param root
	 * @param businessObjectClazz
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<PictogramElement> getChildsOfBusinessObjectType(ContainerShape root, Class businessObjectClazz) {
		List<PictogramElement> result = new ArrayList<PictogramElement>();
		for (Shape currentShape : root.getChildren()) {
			if (BusinessObjectUtil.containsChildElementOfType(currentShape, businessObjectClazz)) {
				result.add(currentShape);
			}
		}
		return result;
	}
	
	public static Shape getFirstLaneInContainer(ContainerShape root) {
		List<PictogramElement> laneShapes = getChildsOfBusinessObjectType(root, Lane.class);
		if (!laneShapes.isEmpty()) {
			Iterator<PictogramElement> iterator = laneShapes.iterator();
			PictogramElement result = iterator.next();
			GraphicsAlgorithm ga = result.getGraphicsAlgorithm();
			if (isHorizontal(root)) {
				while (iterator.hasNext()) {
					PictogramElement currentShape = iterator.next();
					if (currentShape.getGraphicsAlgorithm().getY() < ga.getY()) {
						result = currentShape;
					}
				}
			} else {
				while (iterator.hasNext()) {
					PictogramElement currentShape = iterator.next();
					if (currentShape.getGraphicsAlgorithm().getX() < ga.getX()) {
						result = currentShape;
					}
				}				
			}
			return (Shape) result;
		}
		return root;
	}
	
	public static Shape getLastLaneInContainer(ContainerShape root) {
		List<PictogramElement> laneShapes = getChildsOfBusinessObjectType(root, Lane.class);
		if (!laneShapes.isEmpty()) {
			Iterator<PictogramElement> iterator = laneShapes.iterator();
			PictogramElement result = iterator.next();
			GraphicsAlgorithm ga = result.getGraphicsAlgorithm();
			if (isHorizontal(root)) {
				while (iterator.hasNext()) {
					PictogramElement currentShape = iterator.next();
					if (currentShape.getGraphicsAlgorithm().getY() > ga.getY()) {
						result = currentShape;
					}
				}
			} else {
				while (iterator.hasNext()) {
					PictogramElement currentShape = iterator.next();
					if (currentShape.getGraphicsAlgorithm().getX() > ga.getX()) {
						result = currentShape;
					}
				}				
			}
			return (Shape) result;
		}
		return root;
	}
	
	public static ContainerShape getLaneBefore(ContainerShape container) {
		if (!BusinessObjectUtil.containsElementOfType(container, Lane.class)) {
			return null;
		}
		
		ContainerShape parentContainerShape = container.getContainer();
		if (parentContainerShape == null) {
			return null;
		}
		
		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
		int x = ga.getX();
		int y = ga.getY();
		boolean isHorizontal = isHorizontal(container);
		
		ContainerShape result = null;
		for (PictogramElement picElem : getChildsOfBusinessObjectType(parentContainerShape, Lane.class)) {
			if (picElem instanceof ContainerShape && !picElem.equals(container)) {
				ContainerShape currentContainerShape = (ContainerShape) picElem;
				GraphicsAlgorithm currentGA = currentContainerShape.getGraphicsAlgorithm();
				if (isHorizontal) {
					if (currentGA.getY() < y) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getY() < currentGA.getY()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				} else {
					if (currentGA.getX() < x) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getX() < currentGA.getX()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				}
			}
		}
		return result;
	}
	
	public static ContainerShape getLaneAfter(ContainerShape container) {
		if (!BusinessObjectUtil.containsElementOfType(container, Lane.class)) {
			return null;
		}
		
		ContainerShape parentContainerShape = container.getContainer();
		if (parentContainerShape == null) {
			return null;
		}
		
		GraphicsAlgorithm ga = container.getGraphicsAlgorithm();
		int x = ga.getX();
		int y = ga.getY();
		boolean isHorizontal = isHorizontal(container);
		
		ContainerShape result = null;
		for (PictogramElement picElem : getChildsOfBusinessObjectType(parentContainerShape, Lane.class)) {
			if (picElem instanceof ContainerShape && !picElem.equals(container)) {
				ContainerShape currentContainerShape = (ContainerShape) picElem;
				GraphicsAlgorithm currentGA = currentContainerShape.getGraphicsAlgorithm();
				if (isHorizontal) {
					if (currentGA.getY() > y) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getY() > currentGA.getY()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				} else {
					if (currentGA.getX() > x) {
						if (result != null) {
							GraphicsAlgorithm resultGA = result.getGraphicsAlgorithm();
							if (resultGA.getX() > currentGA.getX()) {
								result = currentContainerShape;
							}
						} else {
							result = currentContainerShape;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Check if the given BaseElement has 
	 * @param baseElement
	 * @return
	 */
	public static boolean hasBpmnDiagram(BaseElement baseElement) {
		BaseElement process = null;
		if (baseElement instanceof Participant) {
			process = ((Participant)baseElement).getProcessRef();
		}
		else if (baseElement instanceof CallActivity) {
			CallableElement ce = ((CallActivity)baseElement).getCalledElementRef();
			if (ce instanceof Process)
				process = (Process)ce;
		}
		
		if (process!=null) {
			baseElement = process;
		}
		
		try {
			Definitions definitions = ModelUtil.getDefinitions(baseElement);
			for (BPMNDiagram d : definitions.getDiagrams()) {
				if (d.getPlane().getBpmnElement() == baseElement)
					return true;
			}
		}
		catch (Exception e){
		}
		
		return false;
		
	}

	public static List<ContainerShape> findGroupedShapes(ContainerShape groupShape) {
		Diagram diagram = null;
		EObject parent = groupShape.eContainer();
		while (parent!=null) {
			if (parent instanceof Diagram) {
				diagram = (Diagram)parent;
				break;
			}
			parent = parent.eContainer();
		}
	
		// find all shapes that are inside this Group
		// these will be moved along with the Group
		List<ContainerShape> list = new ArrayList<ContainerShape>();
		if (diagram!=null && isGroupShape(groupShape)) {
			for (PictogramElement child : diagram.getChildren()) {
				if (child instanceof ContainerShape
						&& child!=groupShape
						&& !list.contains(child)) {
					ContainerShape shape = (ContainerShape)child;
					if (isGroupShape(shape)) {
						if (GraphicsUtil.contains(groupShape, shape)) {
							if (!list.contains(shape)) {
								list.add(shape);
							}
						}
					}
					else if (GraphicsUtil.contains(groupShape, shape)) {
						// find this shape's parent ContainerShape if it has one
						while (!(shape.getContainer() instanceof Diagram)) {
							shape = shape.getContainer();
						}
						if (!list.contains(shape)) {
							list.add(shape);
						}
					}
				}
			}
		}
		return list;
	}

	public static boolean isGroupShape(Shape shape) {
		return BusinessObjectUtil.getFirstBaseElement(shape) instanceof Group;
	}

	public static boolean isLabelShape(Shape shape) {
		return Graphiti.getPeService().getPropertyValue(shape, GraphicsUtil.LABEL_PROPERTY) != null;
	}

	public static List<EObject> findMessageReferences(Diagram diagram, Message message) {
		List<EObject> result = new ArrayList<EObject>();
		Definitions definitions = ModelUtil.getDefinitions(message);
		TreeIterator<EObject> iter = definitions.eAllContents();
		while (iter.hasNext()) {
			EObject o = iter.next();
			if (o instanceof MessageFlow) {
				if (((MessageFlow)o).getMessageRef() == message) {
					result.add(o);
				}
			}
			if (o instanceof MessageEventDefinition) {
				if (((MessageEventDefinition)o).getMessageRef() == message) {
					result.add(o);
				}
			}
			if (o instanceof Operation) {
				if (((Operation)o).getInMessageRef() == message ||
						((Operation)o).getOutMessageRef() == message) {
					result.add(o);
				}
			}
			if (o instanceof ReceiveTask) {
				if (((ReceiveTask)o).getMessageRef() == message) {
					result.add(o);
				}
			}
			if (o instanceof SendTask) {
				if (((SendTask)o).getMessageRef() == message) {
					result.add(o);
				}
			}
			if (o instanceof CorrelationPropertyRetrievalExpression) {
				if (((CorrelationPropertyRetrievalExpression)o).getMessageRef() == message) {
					result.add(o);
				}
			}
		}

		if (diagram!=null) {
			iter = diagram.eResource().getAllContents();
			while (iter.hasNext()) {
				EObject o = iter.next();
				if (o instanceof ContainerShape && !isLabelShape((ContainerShape)o)) {
					if (BusinessObjectUtil.getFirstBaseElement((ContainerShape)o) == message)
						result.add(o);
				}
			}
		}
		return result;
	}
	
	public static List<EClass> getAllowedEventDefinitions(Event event) {
		BaseElement eventOwner = null;
		if (event instanceof BoundaryEvent) {
			eventOwner = ((BoundaryEvent)event).getAttachedToRef();
		}
		else {
			EObject parent = event.eContainer();
			while (parent!=null) {
				if (parent instanceof FlowElementsContainer ) {
					eventOwner = (BaseElement)parent;
					break;
				}
				parent = parent.eContainer();
			}
		}
		
		List<EClass> allowedItems = new ArrayList<EClass>();
		if (event instanceof BoundaryEvent) {
			if (eventOwner instanceof Transaction) {
//				if (((BoundaryEvent)event).isCancelActivity())
					allowedItems.add(Bpmn2Package.eINSTANCE.getCancelEventDefinition());
			}
//			if (((BoundaryEvent)event).isCancelActivity())
				allowedItems.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getConditionalEventDefinition());
//			if (((BoundaryEvent)event).isCancelActivity())
				allowedItems.add(Bpmn2Package.eINSTANCE.getErrorEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getTimerEventDefinition());
		}
		else if (event instanceof IntermediateCatchEvent) {
			allowedItems.add(Bpmn2Package.eINSTANCE.getConditionalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getLinkEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getTimerEventDefinition());
		}
		else if (event instanceof StartEvent) {
			if (eventOwner instanceof SubProcess) {
//				if (((StartEvent)event).isIsInterrupting()) {
					allowedItems.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition());
					allowedItems.add(Bpmn2Package.eINSTANCE.getErrorEventDefinition());
//				}
				allowedItems.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition());
			}
			allowedItems.add(Bpmn2Package.eINSTANCE.getConditionalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getTimerEventDefinition());
		}
		else if (event instanceof EndEvent) {
			if (eventOwner instanceof Transaction)
				allowedItems.add(Bpmn2Package.eINSTANCE.getCancelEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getErrorEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getTerminateEventDefinition());
		}
		else if (event instanceof ImplicitThrowEvent) {
			allowedItems.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getLinkEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
		}
		else if (event instanceof IntermediateThrowEvent) {
			allowedItems.add(Bpmn2Package.eINSTANCE.getCompensateEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getEscalationEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getLinkEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getMessageEventDefinition());
			allowedItems.add(Bpmn2Package.eINSTANCE.getSignalEventDefinition());
		}
		return allowedItems;
	}

	public static boolean updateConnection(IFeatureProvider fp, Connection connection) {
		boolean layoutChanged = false;
		LayoutContext layoutContext = new LayoutContext(connection);
		ILayoutFeature layoutFeature = fp.getLayoutFeature(layoutContext);
		if (layoutFeature!=null) {
			layoutFeature.layout(layoutContext);
			layoutChanged = layoutFeature.hasDoneChanges();
		}
		
		boolean updateChanged = false;
		UpdateContext updateContext = new UpdateContext(connection);
		IUpdateFeature updateFeature = fp.getUpdateFeature(updateContext);
		if (updateFeature!=null && updateFeature.updateNeeded(updateContext).toBoolean()) {
			updateFeature.update(updateContext);
			updateChanged = updateFeature.hasDoneChanges();
		}
		
		return layoutChanged || updateChanged;
	}

	public static boolean updateConnection(IFeatureProvider fp, Connection connection, boolean force) {
		AbstractConnectionRouter.setForceRouting(connection, force);
		return updateConnection(fp,connection);
	}

	public static void updateConnections(IFeatureProvider fp, AnchorContainer ac, List<Connection> alreadyUpdated) {
		for (int ai=0; ai<ac.getAnchors().size(); ++ai) {
			Anchor a = ac.getAnchors().get(ai);
			for (int ci=0; ci<a.getIncomingConnections().size(); ++ci) {
				Connection c = a.getIncomingConnections().get(ci);
				if (c instanceof FreeFormConnection) {
					if (!alreadyUpdated.contains(c)) {
						updateConnection(fp, c, true);
						alreadyUpdated.add(c);
					}
				}
			}
		}
		
		for (int ai=0; ai<ac.getAnchors().size(); ++ai) {
			Anchor a = ac.getAnchors().get(ai);
			for (int ci=0; ci<a.getOutgoingConnections().size(); ++ci) {
				Connection c = a.getOutgoingConnections().get(ci);
				if (c instanceof FreeFormConnection) {
					if (!alreadyUpdated.contains(c)) {
						updateConnection(fp, c, true);
						alreadyUpdated.add(c);
					}
				}
			}
		}
	}

	public static void updateConnections(IFeatureProvider fp, AnchorContainer ac) {
		List<Connection> alreadyUpdated = new ArrayList<Connection>();
		if (ac instanceof ContainerShape) {
			for (Shape child : ((ContainerShape)ac).getChildren()) {
				if (child instanceof ContainerShape)
					updateConnections(fp, child, alreadyUpdated);
			}
		}
		updateConnections(fp, ac, alreadyUpdated);
	}
	
}
