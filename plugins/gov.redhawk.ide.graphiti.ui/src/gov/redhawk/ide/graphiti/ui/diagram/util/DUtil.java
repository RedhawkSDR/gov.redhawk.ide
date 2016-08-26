/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.ui.diagram.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IAddFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AddConnectionContext;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.diagram.util.InterfacesUtil;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.SuperPortConnectionWizard;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.ConnectionTarget;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;

public class DUtil extends gov.redhawk.core.graphiti.ui.util.DUtil {

	/**
	 * Returns true if the provided context contains a pictogram element with one of the provided property values.
	 * False otherwise.
	 * @param context
	 * @param propertyKeys
	 * @return
	 */
	public static boolean doesPictogramContainProperty(PictogramElement pe, String[] propertyValues) {
		if (pe != null && pe.getProperties() != null) {
			for (Property p : pe.getProperties()) {
				for (String propValue : propertyValues) {
					if (p.getValue().equals(propValue)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns all of the shape children recursively
	 * @param diagramElement
	 * @return
	 */
	public static List<Shape> collectShapeChildren(Shape diagramElement) {

		List<Shape> children = new ArrayList<Shape>();
		children.add(diagramElement);
		// if containershape, collect children recursively
		if (diagramElement instanceof ContainerShape) {
			ContainerShape cs = (ContainerShape) diagramElement;
			for (Shape c : cs.getChildren()) {
				children.addAll(collectShapeChildren(c));
			}
		}
		return children;
	}

	/**
	 * Remove Business object from all linked PictogramElement
	 * @param diagram
	 * @param eObject
	 */
	public static void removeBusinessObjectFromAllPictogramElements(Diagram diagram, EObject eObject) {
		// get pe with link to bo
		List<PictogramElement> pictogramElements = Graphiti.getLinkService().getPictogramElements(diagram, eObject);

		// remove link
		for (PictogramElement pe : pictogramElements) {
			pe.getLink().getBusinessObjects().remove(eObject);
		}
	}

	public static void addLink(IFeatureProvider featureProvider, PictogramElement pe, EObject eObject) {
		if (eObject == null) {
			return;
		}

		if (pe.getLink() == null) {
			featureProvider.link(pe, eObject);
		} else {
			pe.getLink().getBusinessObjects().add(eObject);
		}
	}

	public static void addLinks(IFeatureProvider featureProvider, PictogramElement pe, Collection< ? extends EObject> eObjects) {
		if (eObjects == null || eObjects.size() < 1) {
			return;
		}

		if (pe.getLink() == null) {
			featureProvider.link(pe, eObjects.toArray());
		} else {
			pe.getLink().getBusinessObjects().addAll(eObjects);
		}
	}

	/**
	 * Returns the ancestor (parent chain) of the provided diagramElement with the provided PropertyContainer
	 * @param diagramElement
	 * @return
	 */
	public static ContainerShape findContainerShapeParentWithProperty(Shape shape, String propertyValue) {

		if (shape instanceof Diagram) {
			return null;
		}
		if (shape instanceof ContainerShape && DUtil.isPropertyElementType(shape, propertyValue)) {
			return (ContainerShape) shape;
		}
		if (DUtil.isPropertyElementType(shape.getContainer(), propertyValue)) {
			return shape.getContainer();
		}
		return findContainerShapeParentWithProperty(shape.getContainer(), propertyValue);

	}

	/**
	 * Returns the ancestor (parent chain) of the provided diagramElement with the provided PropertyContainer
	 * First checks self to see if it is a container with matching property
	 * @param diagramElement
	 * @return
	 */
	public static ContainerShape findContainerShapeParentWithProperty(PictogramElement pe, String propertyValue) {
		if (pe instanceof ContainerShape && DUtil.isPropertyElementType(pe, propertyValue)) {
			return (ContainerShape) pe;
		}
		PictogramElement peContainer = Graphiti.getPeService().getActiveContainerPe(pe);
		if (peContainer instanceof ContainerShape) {
			ContainerShape outerContainerShape = DUtil.findContainerShapeParentWithProperty((ContainerShape) peContainer, propertyValue);
			return outerContainerShape;
		}
		return null;
	}

	/**
	 * Checks the container shape and all its children and returns any which overlap any of the specified area.
	 * @param containerShape Usually this should be the {@link Diagram}
	 * @param width
	 * @param height
	 * @param x Absolute x
	 * @param y Absolute y
	 * @return
	 */
	public static List<Shape> getShapesInArea(final ContainerShape containerShape, int width, int height, int x, int y) {
		List<Shape> retList = new ArrayList<Shape>();
		EList<Shape> shapes = containerShape.getChildren();
		for (Shape s : shapes) {
			if (shapeExistsPartiallyInArea(s, width, height, x, y)) {
				retList.add(s);
			}
		}
		return retList;
	}

	/**
	 * Returns true if the specified area overlaps any part of a host collocation.
	 * @param diagram
	 * @param width
	 * @param height
	 * @param x Absolute x
	 * @param y Absolute y
	 * @return
	 */
	public static boolean overlapsHostCollocation(Diagram diagram, int width, int height, int x, int y) {
		for (Shape shape : diagram.getChildren()) {
			PictogramElement pe = shape.getLink().getPictogramElement();
			if (!(DUtil.getBusinessObject(pe) instanceof HostCollocation)) {
				continue;
			}
			if (shapeExistsPartiallyInArea(shape, width, height, x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adjust children x/y so they remain in the same relative position after resize
	 * @param containerShape
	 * @param context
	 */
	public static void shiftChildrenRelativeToParentResize(ContainerShape containerShape, IResizeShapeContext context) {

		int widthDiff = containerShape.getGraphicsAlgorithm().getWidth() - context.getWidth();
		int heightDiff = containerShape.getGraphicsAlgorithm().getHeight() - context.getHeight();
		switch (context.getDirection()) {
		case (IResizeShapeContext.DIRECTION_NORTH_EAST):
			shiftChildrenYPositionUp(containerShape, heightDiff);
			break;
		case (IResizeShapeContext.DIRECTION_WEST):
		case (IResizeShapeContext.DIRECTION_SOUTH_WEST):
			shiftChildrenXPositionLeft(containerShape, widthDiff);
			break;
		case (IResizeShapeContext.DIRECTION_NORTH_WEST):
			shiftChildrenXPositionLeft(containerShape, widthDiff);
			shiftChildrenYPositionUp(containerShape, heightDiff);
			break;
		case (IResizeShapeContext.DIRECTION_NORTH): // handle top of box getting smaller
			shiftChildrenYPositionUp(containerShape, heightDiff);
			break;
		default:
			break;
		}
	}

	/**
	 * Shifts children of container x value to the left by specified amount
	 * Can be negative
	 * @param ga
	 * @param shiftLeftAmount
	 */
	private static void shiftChildrenXPositionLeft(ContainerShape containerShape, int shiftLeftAmount) {
		for (Shape s : containerShape.getChildren()) {
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			Graphiti.getGaService().setLocation(ga, ga.getX() - shiftLeftAmount, ga.getY());
		}
	}

	/**
	 * Shifts children of container Y value up by specified amount
	 * Can be negative
	 * @param ga
	 * @param shiftUpAmount
	 */
	private static void shiftChildrenYPositionUp(ContainerShape containerShape, int shiftUpAmount) {
		for (Shape s : containerShape.getChildren()) {
			GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
			Graphiti.getGaService().setLocation(ga, ga.getX(), ga.getY() - shiftUpAmount);
		}
	}

	/**
	 * Checks the container shape and all its children and returns any which do not overlap any of the specified area.
	 * @param containerShape Usually this should be the {@link Diagram}
	 * @param width
	 * @param height
	 * @param x Absolute x
	 * @param y Absolute y
	 * @return
	 */
	public static List<Shape> getShapesOutsideArea(final ContainerShape containerShape, int width, int height, int x, int y) {
		List<Shape> retList = new ArrayList<Shape>();
		EList<Shape> shapes = containerShape.getChildren();
		for (Shape s : shapes) {
			if (!shapeExistsPartiallyInArea(s, width, height, x, y)) {
				retList.add(s);
			}
		}
		return retList;
	}

	/**
	 * Determine if a shape overlaps an area. Coordinates should be absolute.
	 * @param s
	 * @param width
	 * @param height
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean shapeExistsPartiallyInArea(final Shape s, int width, int height, int x, int y) {
		GraphicsAlgorithm ga = s.getGraphicsAlgorithm();
		ILocation shapeLoc = GraphitiUi.getUiLayoutService().getLocationRelativeToDiagram(s);
		return ((x + width) > ga.getX() && x < (shapeLoc.getX() + ga.getWidth()) && (y + height) > ga.getY() && y < (shapeLoc.getY() + ga.getHeight()));
	}

	/**
	 * Returns all ContainerShapes with the provided property value
	 * @param containerShape
	 * @param propertyValue
	 * @return
	 */
	public static List<ContainerShape> getAllContainerShapes(ContainerShape containerShape, String propertyValue) {
		List<ContainerShape> children = new ArrayList<ContainerShape>();
		if (containerShape instanceof ContainerShape && isPropertyElementType(containerShape, propertyValue)) {
			children.add(containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllContainerShapes((ContainerShape) s, propertyValue));
				}
			}
		}
		return children;
	}

	/**
	 * Returns true if Pictogram Link contains an object of the provided Class
	 * @param <T>
	 * @param link
	 * @param cls
	 * @return
	 */
	public static < T > boolean doesLinkContainObjectTypeInstance(PictogramLink link, Class<T> cls) {
		if (link != null) {
			for (EObject eObj : link.getBusinessObjects()) {
				if (cls.isInstance(eObj)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Update PictogramElement via feature
	 * Relies on the framework determining which feature should be used and whether it can be added to diagram
	 * @param featureProvider
	 * @param pe
	 * @return
	 */
	public static boolean updateShapeViaFeature(IFeatureProvider featureProvider, Diagram diagram, PictogramElement pe) {
		UpdateContext updateContext = new UpdateContext(pe);
		IUpdateFeature updateFeature = featureProvider.getUpdateFeature(updateContext);
		if (updateFeature.canUpdate(updateContext)) {
			return updateFeature.update(updateContext);
		}
		return false;
	}

	/**
	 * Add PictogramElement Connection via feature for the provided object and anchors.
	 * Relies on the framework determining which feature should be used and whether it can be added to diagram
	 * @param featureProvider
	 * @param object
	 * @param sourceAnchor
	 * @param targetAnchor
	 * @return
	 */
	public static PictogramElement addConnectionViaFeature(IFeatureProvider featureProvider, Object object, Anchor sourceAnchor, Anchor targetAnchor) {
		AddConnectionContext addConnectionContext = new AddConnectionContext(sourceAnchor, targetAnchor);
		addConnectionContext.setNewObject(object);
		IAddFeature addFeature = featureProvider.getAddFeature(addConnectionContext);
		if (addFeature.canAdd(addConnectionContext)) {
			return addFeature.add(addConnectionContext);
		}
		return null;
	}

	/**
	 * Lookup SourceAnchor for connection. Examines uses ports on Components as well as FindBys
	 * @param sadConnectInterface
	 * @param diagram
	 * @return
	 */
	public static Anchor lookupSourceAnchor(ConnectInterface< ? , ? , ? > sadConnectInterface, Diagram diagram) {
		// lookup sourceAnchor
		PictogramElement sourceAnchorPe = DUtil.getPictogramElementForBusinessObject(diagram, sadConnectInterface.getSource(), Anchor.class);
		if (sourceAnchorPe != null) {
			return (Anchor) sourceAnchorPe;
		} else {
			// All components have been created so source Anchor is likely null because provides findBy
			// <uses><findby>something</findby></uses>
			// or something is wrong with the xml
			if (sadConnectInterface.getUsesPort() != null && sadConnectInterface.getUsesPort().getFindBy() != null) {
				FindBy findBy = (FindBy) sadConnectInterface.getUsesPort().getFindBy();

				// iterate through all FindByStub objects stored in diagram and set sourceAnchor that matches findBy
				List<RHContainerShape> findByContainerShapes = AbstractFindByPattern.getAllFindByShapes(diagram);
				for (RHContainerShape findByShape : findByContainerShapes) {
					FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(findByShape);

					// determine findBy match
					if (AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {

						// determine which usesPortStub we are targeting
						UsesPortStub usesPortStub = null;
						for (UsesPortStub p : findByStub.getUses()) {
							if (p != null && sadConnectInterface.getUsesPort().getUsesIdentifier() != null
								&& p.getName().equals(sadConnectInterface.getUsesPort().getUsesIdentifier())) {
								usesPortStub = p;
							}
						}

						// determine port anchor for FindByMatch
						if (usesPortStub != null) {
							PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, usesPortStub, Anchor.class);
							return (Anchor) pe;
						}
					}
				}
			}
		}
		return null;
	}

	/**
	 * Search for the FindByStub in the diagram given the findBy object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	public static FindByStub findFindByStub(FindBy findBy, Diagram diagram) {
		for (RHContainerShape findByShape : AbstractFindByPattern.getAllFindByShapes(diagram)) {
			FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(findByShape);
			// determine findBy match
			if (findByStub != null && AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {
				// it matches
				return findByStub;
			}
		}
		return null;
	}

	/**
	 * Return true if target is HostCollocation ContainerShape
	 * @param context
	 */
	public static HostCollocation getHostCollocation(final ContainerShape targetContainerShape) {
		if (targetContainerShape instanceof ContainerShape) {
			if (targetContainerShape.getLink() != null && targetContainerShape.getLink().getBusinessObjects() != null) {
				for (EObject obj : targetContainerShape.getLink().getBusinessObjects()) {
					if (obj instanceof HostCollocation) {
						return (HostCollocation) obj;
					}
				}
			}
		}
		return null;
	}

	// convenient method for getting diagram for a ContainerShape
	public static Diagram findDiagram(ContainerShape containerShape) {
		return Graphiti.getPeService().getDiagramForShape(containerShape);
	}

	/**
	 * Returns true if the portContainer is a super port
	 * @param portContainer - The port container to be tested
	 * @return
	 */
	public static boolean isSuperPort(ContainerShape portContainer) {
		boolean isSuperProvides = DUtil.doesPictogramContainProperty(portContainer, new String[] { RHContainerShape.SUPER_PROVIDES_PORTS_RECTANGLE });
		boolean isSuperUses = DUtil.doesPictogramContainProperty(portContainer, new String[] { RHContainerShape.SUPER_USES_PORTS_RECTANGLE });
		return (isSuperProvides || isSuperUses);
	}

	/**
	 * If a provides port with the port name still exists, return the new anchor so the connection can be redrawn
	 * @param providesPortStubs
	 * @param oldPortName
	 * @return Anchor object that is associated with the port
	 */
	public static Anchor getProvidesAnchor(Diagram diagram, EList<ProvidesPortStub> providesPortStubs, String oldPortName) {
		for (ProvidesPortStub port : providesPortStubs) {
			if (port.getName().equals(oldPortName)) {
				Anchor anchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) port, Anchor.class);
				return anchor;
			}
		}
		return null;
	}

	/**
	 * If a uses port with the port name still exists, return the new anchor so the connection can be redrawn
	 * @param usesPortStubs
	 * @param oldPortName
	 * @return Anchor object that is associated with the port
	 */
	public static Anchor getUsesAnchor(Diagram diagram, EList<UsesPortStub> usesPortStubs, String oldPortName) {
		for (UsesPortStub port : usesPortStubs) {
			if (port.getName().equals(oldPortName)) {
				Anchor anchor = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) port, Anchor.class);
				return anchor;
			}
		}
		return null;
	}

	/**
	 * Add source and target values to ConnectInterface and return
	 * assume UsesPortStub is the first anchor, ConnectionTarget for second anchor
	 * return null if either source or target not found
	 * @param anchor1
	 * @param anchor2
	 * @return
	 */
	public static ConnectInterface< ? , ? , ? > assignAnchorObjectsToConnection(ConnectInterface< ? , ? , ? > connectInterface, Anchor anchor1, Anchor anchor2) {

		if (anchor1 == null || anchor2 == null) {
			return null;
		}

		// get business objects for both anchors
		EList<EObject> anchorObjects1 = anchor1.getParent().getLink().getBusinessObjects();
		EList<EObject> anchorObjects2 = anchor2.getParent().getLink().getBusinessObjects();

		UsesPortStub source = null;
		ConnectionTarget target = null;

		// Check to ensure the first anchor is a UsesPortStub and the second is a ConnectionTarget
		if (anchorObjects1.size() == 0 || anchorObjects2.size() == 0) {
			return null;
		}
		for (EObject sourceObj : anchorObjects1) {
			if (!(sourceObj instanceof UsesPortStub)) {
				return null;
			}
		}
		for (EObject targetObj : anchorObjects2) {
			if (!(targetObj instanceof ConnectionTarget)) {
				return null;
			}
		}

		List<UsesPortStub> possibleSources = new ArrayList<UsesPortStub>();
		List<ConnectionTarget> possibleTargets = new ArrayList<ConnectionTarget>();

		if (anchorObjects1.size() == 1 && anchorObjects2.size() == 1) {
			// Always attempt to honor direct connections
			possibleSources.add((UsesPortStub) anchorObjects1.get(0));
			possibleTargets.add((ConnectionTarget) anchorObjects2.get(0));
		} else {
			// If either side is a super port, then build a list of possible connections
			for (EObject sourceObj : anchorObjects1) {
				for (EObject targetObj : anchorObjects2) {
					if (InterfacesUtil.areSuggestedMatch((UsesPortStub) sourceObj, targetObj)) {
						if (!possibleSources.contains(sourceObj)) {
							possibleSources.add((UsesPortStub) sourceObj);
						}
						if (!possibleTargets.contains(targetObj)) {
							possibleTargets.add((ConnectionTarget) targetObj);
						}
					}
				}
			}
		}

		if (possibleSources.size() > 1 || possibleTargets.size() > 1) {
			// If more than one connection is possible, display a wizard to complete the action
			SuperPortConnectionWizard wizard = new SuperPortConnectionWizard(possibleSources, possibleTargets);
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			WizardDialog dialog = new WizardDialog(shell, wizard);
			int retVal = dialog.open();

			if (retVal == Window.OK) {
				// Get user selections
				source = wizard.getPage().getSource();
				target = wizard.getPage().getTarget();
			} else {
				return null;
			}
		} else if (!possibleSources.isEmpty() && !possibleTargets.isEmpty()) {
			// If only one connection is possible, just go ahead and do it
			source = (UsesPortStub) possibleSources.get(0);
			target = (ConnectionTarget) possibleTargets.get(0);
		}

		// source
		connectInterface.setSource(source);
		// target
		connectInterface.setTarget(target);

		// only return if we have source/target set
		if (source == null || target == null) {
			return null;
		}

		return connectInterface;
	}
}
