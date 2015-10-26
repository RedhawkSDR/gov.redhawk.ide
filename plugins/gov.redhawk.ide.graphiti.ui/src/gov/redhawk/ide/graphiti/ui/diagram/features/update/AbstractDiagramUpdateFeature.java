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
package gov.redhawk.ide.graphiti.ui.diagram.features.update;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.FindByUtil;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

public class AbstractDiagramUpdateFeature extends DefaultUpdateDiagramFeature {

	public AbstractDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Add necessary FindByShapes based on connections in model
	 * @return
	 */
	protected < E extends ConnectInterface< ? , ? , ? > > void addFindBy(List<E> connectInterfaces, Diagram diagram, IFeatureProvider featureProvider) {

		// contains all the findByStubs that should exist in the diagram
		ArrayList<FindByStub> findByStubs = new ArrayList<FindByStub>();

		// populate list of findByStubs with existing instances from diagram
		List<RHContainerShape> findByShapes = AbstractFindByPattern.getAllFindByShapes(diagram);
		for (RHContainerShape findByShape : findByShapes) {
			findByStubs.add((FindByStub) DUtil.getBusinessObject(findByShape));
		}

		// look for findby in connections to add
		for (E connectInterface : connectInterfaces) {

			// FindBy is always used inside usesPort
			if (connectInterface.getUsesPort() != null && connectInterface.getUsesPort().getFindBy() != null) {

				// get FindBy model object
				FindBy findBy = connectInterface.getUsesPort().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				// does findBy exist in diagram already?
				if (findByStub == null) {
					// create FindBy Shape for Source
					findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}
				// add uses port to stub if doesn't already exist
				if (!hasMatchingUsesPort(findByStub, connectInterface.getUsesPort().getUsesIdentifier())) {
					// add the required usesPort
					AbstractFindByPattern.addUsesPortStubToFindByStub(findByStub, connectInterface.getUsesPort(), featureProvider);
				}
			}

			// lookup Target Anchor
			// sadConnectInterface.getComponentSupportedInterface().getFindBy()
			if (connectInterface.getComponentSupportedInterface() != null && connectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
				&& connectInterface.getComponentSupportedInterface().getFindBy() != null) {

				// The model provides us with interface information for the FindBy we are connecting to
				FindBy findBy = connectInterface.getComponentSupportedInterface().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				if (findByStub == null) {
					// create findByStub
					findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}

				// findBy nested in ProvidesPort
			} else if (connectInterface.getProvidesPort() != null && connectInterface.getProvidesPort().getFindBy() != null) {

				FindBy findBy = connectInterface.getProvidesPort().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				// does findBy exist in diagram already?
				if (findByStub == null) {
					// create findByStub
					findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}

				// add provides port to stub if doesn't already exist
				if (!hasMatchingProvidesPort(findByStub, connectInterface.getProvidesPort().getProvidesIdentifier())) {
					// add the required providesPort
					AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, connectInterface.getProvidesPort(), featureProvider);
				}
			}
		}

		// add new FindByStub(s), update existing FindByStub
		for (FindByStub fbs : findByStubs) {
			List<PictogramElement> elements = GraphitiUi.getLinkService().getPictogramElements(diagram, fbs);
			if (elements == null || elements.size() < 1) {
				DUtil.addShapeViaFeature(featureProvider, diagram, fbs);
			} else {
				DUtil.updateShapeViaFeature(featureProvider, diagram, elements.get(0));
			}
		}
	}

	protected boolean hasMatchingUsesPort(FindByStub findByStub, String name) {
		for (UsesPortStub uses : findByStub.getUses()) {
			if (uses.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	protected boolean hasMatchingProvidesPort(FindByStub findByStub, String name) {
		for (ProvidesPortStub provides : findByStub.getProvides()) {
			if (provides.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Search for the FindByStub in the diagram given the findBy object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	protected FindByStub findFindByStub(FindBy findBy, List<FindByStub> findByStubs) {
		for (FindByStub findByStub : findByStubs) {
			if (AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {
				// it matches
				return findByStub;
			}
		}
		return null;
	}

	/**
	 * Removes invalid connections where start/end points no longer exist
	 * TODO:Consider moving this method to another class
	 * @param connectInterfaces
	 * @return
	 */
	protected < E extends ConnectInterface< ? , ? , ? > > void removeInvalidConnections(List<E> connectInterfaces) {
		for (Iterator<E> connIter = connectInterfaces.iterator(); connIter.hasNext();) {
			// delete connection in model if either uses port is present but the referenced component isn't,
			// or provides port is present but references component isn't
			E con = connIter.next();
			if ((con.getUsesPort() != null && con.getUsesPort().getComponentInstantiationRef() != null
				&& con.getUsesPort().getComponentInstantiationRef().getInstantiation() == null)
				|| (con.getProvidesPort() != null && con.getProvidesPort().getComponentInstantiationRef() != null
					&& con.getProvidesPort().getComponentInstantiationRef().getInstantiation() == null)) {
				// endpoint missing, delete connection
				connIter.remove();
				EcoreUtil.delete(con, true);
			}
		}
	}

	protected < E extends ConnectInterface< ? , ? , ? > > Anchor addSourceAnchor(E connectInterface, Diagram diagram) throws CoreException {
		Anchor sourceAnchor = DUtil.lookupSourceAnchor(connectInterface, diagram);

		// if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
		if (sourceAnchor == null) {

			// FindBy is always used inside usesPort
			if (connectInterface.getUsesPort() != null && connectInterface.getUsesPort().getFindBy() != null) {

				FindBy findBy = connectInterface.getUsesPort().getFindBy();

				// search for findByStub in diagram
				FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

				if (findByStub == null) {
					// should never occur, addRemoveUpdateFindBy() takes care of this
					throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
				}

				// determine which usesPortStub
				UsesPortStub usesPortStub = null;
				for (UsesPortStub p : findByStub.getUses()) {
					if (p != null && connectInterface.getUsesPort().getUsesIdentifier() != null
						&& p.getName().equals(connectInterface.getUsesPort().getUsesIdentifier())) {
						usesPortStub = p;
					}
				}
				// determine port anchor for FindByMatch
				if (usesPortStub != null) {
					PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, usesPortStub, Anchor.class);
					sourceAnchor = (Anchor) pe;
				} else {
					System.out.println("our source port is not getting set"); // SUPPRESS CHECKSTYLE INLINE
					// TODO: this means the provides port didn't exist in the existing findByStub..we need
					// to add it
				}
			}
		}
		return sourceAnchor;
	}

	/**
	 * Add new Connections and also add FindBy Shapes where necessary
	 * @param connectInterfaces
	 * @param pictogramLabel
	 * @param featureProvider
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	protected < E extends ConnectInterface< ? , ? , ? > > void addConnections(List<E> connectInterfaces, Diagram diagram, IFeatureProvider featureProvider)
		throws CoreException {

		// add findByStub shapes
		addFindBy(connectInterfaces, diagram, featureProvider);

		// add Connections found in model, but not in diagram
		for (E connectInterface : connectInterfaces) {

			// wasn't found, add Connection
			// lookup sourceAnchor
			Anchor sourceAnchor = DUtil.lookupSourceAnchor(connectInterface, diagram);

			// if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
			if (sourceAnchor == null) {
				sourceAnchor = addSourceAnchor(connectInterface, diagram);
			}

			// lookup Target Anchor
			Anchor targetAnchor = null;
			PictogramElement targetAnchorPe = DUtil.getPictogramElementForBusinessObject(diagram, connectInterface.getTarget(), Anchor.class);
			if (targetAnchorPe != null) {
				targetAnchor = (Anchor) targetAnchorPe;
			} else {
				targetAnchor = addTargetAnchor(connectInterface, diagram, featureProvider);
			}

			// add Connection if anchors
			if (sourceAnchor != null && targetAnchor != null) {
				DUtil.addConnectionViaFeature(featureProvider, connectInterface, sourceAnchor, targetAnchor);
			} else {
				// PASS
				// TODO: how do we handle this?
			}
		}
	}

	protected < E extends ConnectInterface< ? , ? , ? > > Anchor addTargetAnchor(E connectInterface, Diagram diagram, IFeatureProvider featureProvider)
		throws CoreException {
		// dcdConnectInterface.getComponentSupportedInterface().getFindBy()
		if (connectInterface.getComponentSupportedInterface() != null && connectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
			&& connectInterface.getComponentSupportedInterface().getFindBy() != null) {

			// The model provides us with interface information for the FindBy we are connecting to
			FindBy findBy = connectInterface.getComponentSupportedInterface().getFindBy();

			// iterate through FindByStubs in diagram
			FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

			if (findByStub == null) {
				// should never occur, addRemoveUpdateFindBy() takes care of this
				throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
			}

			// determine port anchor for FindByMatch
			if (findByStub.getInterface() != null) {
				PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, findByStub.getInterface(), Anchor.class);
				return (Anchor) pe;
			}

			// findBy nested in ProvidesPort
		} else if (connectInterface.getProvidesPort() != null && connectInterface.getProvidesPort().getFindBy() != null) {

			FindBy findBy = connectInterface.getProvidesPort().getFindBy();

			// iterate through FindByStubs in diagram
			FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

			if (findByStub == null) {
				// should never occur, addRemoveUpdateFindBy() takes care of this
				throw new CoreException(new Status(IStatus.ERROR, GraphitiUIPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
			}

			// ensure the providesPort exists in FindByStub that already exists in diagram
			boolean foundProvidesPortStub = false;
			for (ProvidesPortStub p : findByStub.getProvides()) {
				if (p.getName().equals(connectInterface.getProvidesPort().getProvidesIdentifier())) {
					foundProvidesPortStub = true;
				}
			}
			if (!foundProvidesPortStub) {
				// add the required providesPort
				AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, connectInterface.getProvidesPort(), featureProvider);
				// Update on FindByStub PE
				DUtil.updateShapeViaFeature(featureProvider, diagram, DUtil.getPictogramElementForBusinessObject(diagram, findByStub, RHContainerShape.class));

				// maybe call layout?

			}

			// determine which providesPortStub we are targeting
			ProvidesPortStub providesPortStub = null;
			for (ProvidesPortStub p : findByStub.getProvides()) {
				if (p != null && connectInterface.getProvidesPort().getProvidesIdentifier() != null
					&& p.getName().equals(connectInterface.getProvidesPort().getProvidesIdentifier())) {
					providesPortStub = p;
					break;
				}
			}

			// determine port anchor for FindByMatch
			if (providesPortStub != null) {
				PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, providesPortStub, Anchor.class);
				return (Anchor) pe;
			} else {
				// PASS
				// TODO: this means the provides port didn't exist in the existing findByStub..we need
				// to add it
			}
		}
		return null;
	}

	protected boolean hasExistingShape(Diagram diagram, EObject eObject) {
		return !Graphiti.getLinkService().getPictogramElements(diagram, eObject).isEmpty();
	}

	protected boolean shouldRemoveShape(Shape shape) {
		if (!doesLinkedBusinessObjectExist(shape)) {
			return true;
		}
		return false;
	}

	protected List<EObject> getStubsToRemove(Diagram diagram) {
		List<EObject> removedStubs = new ArrayList<EObject>();
		for (EObject object : diagram.eResource().getContents()) {
			if (!(object instanceof Diagram)) {
				if (!hasExistingShape(diagram, object)) {
					removedStubs.add(object);
				}
			}
		}
		return removedStubs;
	}

	protected boolean haveEndpointsChanged(Connection connection, Diagram diagram) {
		ConnectInterface< ? , ? , ? > connectInterface = DUtil.getBusinessObject(connection, ConnectInterface.class);
		Set<Anchor> anchors = new HashSet<Anchor>();
		anchors.add(DUtil.getPictogramElementForBusinessObject(diagram, connectInterface.getSource(), Anchor.class));
		anchors.add(DUtil.getPictogramElementForBusinessObject(diagram, connectInterface.getTarget(), Anchor.class));
		return !anchors.contains(connection.getStart()) || !anchors.contains(connection.getEnd());
	}

	protected boolean doesModelObjectExist(EObject object) {
		if (object == null || object.eIsProxy()) {
			return false;
		}
		return true;
	}

	protected boolean doesLinkedBusinessObjectExist(PictogramElement pe) {
		EObject bo = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(pe);
		return doesModelObjectExist(bo);
	}

	protected List<EObject> getObjectsToAdd(Diagram diagram) {
		return Collections.emptyList();
	}

	protected List<Shape> getShapesToRemove(Diagram diagram) {
		List<Shape> removedShapes = new ArrayList<Shape>();
		for (Shape shape : diagram.getChildren()) {
			if (shouldRemoveShape(shape)) {
				removedShapes.add(shape);
			}
		}
		return removedShapes;
	}

	protected List<Connection> getConnectionsToRemove(Diagram diagram) {
		List<Connection> removedConnections = new ArrayList<Connection>();
		for (Connection connection : diagram.getConnections()) {
			if (!doesLinkedBusinessObjectExist(connection)) {
				removedConnections.add(connection);
			} else if (haveEndpointsChanged(connection, diagram)) {
				removedConnections.add(connection);
			}
		}
		return removedConnections;
	}

	protected List<ConnectInterface< ? , ? , ? >> getModelConnections(Diagram diagram) {
		return Collections.emptyList();
	}

	protected List<ConnectInterface< ? , ? , ? >> getConnectionsToAdd(Diagram diagram) {
		List<ConnectInterface < ? , ? , ? >> addedConnections = new ArrayList<ConnectInterface< ? , ? , ? >>();
		for (ConnectInterface< ? , ? , ? > connectInterface : getModelConnections(diagram)) {
			if (!hasExistingShape(diagram, connectInterface)) {
				addedConnections.add(connectInterface);
			}
		}
		return addedConnections;
	}

	@Override
	public Reason updateNeeded(IUpdateContext context) {
		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram diagram = (Diagram) pe;

			// Check for stale shapes
			List<Shape> removedChildren = getShapesToRemove(diagram);
			if (!removedChildren.isEmpty()) {
				return new Reason(true, "Need to remove " + removedChildren.size() + " shape(s)");
			}

			// Check for unused stubs
			List<EObject> removedStubs = getStubsToRemove(diagram);
			if (!removedStubs.isEmpty()) {
				return new Reason(true, "Diagram resource contents need pruning");
			}

			// Check for SAD objects that do not have shapes
			List<EObject> addedChildren = getObjectsToAdd(diagram);
			if (!addedChildren.isEmpty()) {
				return new Reason(true, "Missing " + addedChildren.size() + " shape(s)");
			}

			// Check for stale connections
			List<Connection> removedConnections = getConnectionsToRemove(diagram);
			if (!removedConnections.isEmpty()) {
				return new Reason(true, "Need to remove " + removedConnections.size() + " connection(s)");
			}

			// Check for SAD connections that do not have a diagram connection
			List<ConnectInterface< ? , ? , ? >> addedConnections = getConnectionsToAdd(diagram);
			if (!addedConnections.isEmpty()) {
				return new Reason(true, "Need to add " + addedConnections.size() + " connection(s)");
			}
		}

		IReason parentReason = super.updateNeeded(context);
		return new Reason(parentReason.toBoolean(), parentReason.getText());
	}
}
