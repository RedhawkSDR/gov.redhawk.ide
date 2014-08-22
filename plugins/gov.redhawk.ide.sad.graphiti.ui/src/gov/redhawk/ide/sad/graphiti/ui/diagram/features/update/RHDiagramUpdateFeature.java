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
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.update;

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.HostCollocationPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class RHDiagramUpdateFeature extends DefaultUpdateDiagramFeature {

	public RHDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	/**
	 * Updates the Diagram to reflect the underlying business model
	 * Make sure all elements in sad model (hosts/components/findby) are accounted for as
	 * children of diagram, if they aren't then add them, if they are then check to see if
	 * they need to be updated, if they exist in the diagram yet not in the model, remove them
	 * @param context
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	@SuppressWarnings("unchecked")
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {

		boolean updateStatus = false;

		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram d = (Diagram) pe;

			// get sad from diagram
			final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

			// TODO: ensure our SAD has an assembly controller
			// set one if necessary, why bother the user?

			// model HostCollocation
			List<HostCollocation> hostCollocations = new ArrayList<HostCollocation>();
			if (sad != null && sad.getPartitioning() != null && sad.getPartitioning().getHostCollocation() != null) {
				// Elist -> List
				Collections.addAll(hostCollocations, (HostCollocation[]) sad.getPartitioning().getHostCollocation().toArray(new HostCollocation[0]));
			}
			// shape HostCollocation
			List<ContainerShape> hostCollocationShapes = HostCollocationPattern.getHostCollocationContainerShapes(d);

			// model components
			List<SadComponentInstantiation> componentInstantiations = new ArrayList<SadComponentInstantiation>();
			if (sad != null && sad.getPartitioning() != null && sad.getPartitioning().getComponentPlacement() != null) {
				// Get list of componentInstantiations from model
				for (SadComponentPlacement p : sad.getPartitioning().getComponentPlacement()) {
					Collections.addAll(componentInstantiations,
						(SadComponentInstantiation[]) p.getComponentInstantiation().toArray(new SadComponentInstantiation[0]));
				}
			}
			// shape components
			List<ComponentShape> componentShapes = ComponentPattern.getAllComponentShapes(d);

			// model connections
			List<SadConnectInterface> sadConnectInterfaces = new ArrayList<SadConnectInterface>();
			if (sad != null && sad.getConnections() != null && sad.getConnections().getConnectInterface() != null) {
				// Get list of SadConnectInterfaces from model
				Collections.addAll(sadConnectInterfaces, (SadConnectInterface[]) sad.getConnections().getConnectInterface().toArray(new SadConnectInterface[0]));
			}
			// remove invalid model connections
			removeInvalidConnections(sadConnectInterfaces);

			// shape connections
			List<Connection> connections = new ArrayList<Connection>();
			Collections.addAll(connections, (Connection[]) d.getConnections().toArray(new Connection[0]));

			// Check for inconsistencies in hostcollocation and number of components and connections
			// If found remove all objects of that type and redraw
			// we must do this because the diagram uses indexed lists to refer to components in the sad file.
			if (performUpdate) {
				updateStatus = true;

				List<PictogramElement> pesToRemove = new ArrayList<PictogramElement>(); // gather all shapes to remove
				List<Object> objsToAdd = new ArrayList<Object>(); // gather all model object to add

				// If number of model and diagram objects don't match, mark them to be removed
				// and query the model for the correct number of objects to be drawn
				if (hostCollocations.size() != hostCollocationShapes.size()) {
					Collections.addAll(pesToRemove, (PictogramElement[]) hostCollocationShapes.toArray(new PictogramElement[0]));
					Collections.addAll(objsToAdd, (Object[]) hostCollocations.toArray(new Object[0]));
				}
				if (componentShapes.size() != componentInstantiations.size() || !componentsResolved(componentShapes)) {
					Collections.addAll(pesToRemove, (PictogramElement[]) componentShapes.toArray(new PictogramElement[0]));
					Collections.addAll(objsToAdd, (Object[]) componentInstantiations.toArray(new Object[0]));
				}
				
				// Easiest just to remove and redraw connections every time
				Collections.addAll(pesToRemove, (PictogramElement[]) connections.toArray(new PictogramElement[0]));

				if (!pesToRemove.isEmpty()) {
					// remove shapes from diagram
					for (PictogramElement peToRemove : pesToRemove) {
						// remove shape
						RemoveContext rc = new RemoveContext(peToRemove);
						IRemoveFeature removeFeature = getFeatureProvider().getRemoveFeature(rc);
						if (removeFeature != null) {
							removeFeature.remove(rc);
						}
					}
				} else {
					super.update(context);
					// update components
					// TODO is this actually necessary, will the update happen on its own?
					// update component shapes
					// getFeatureProvider().updateIfPossibleAndNeeded(new UpdateContext(pe));
				}

				// add shapes to diagram
				if (!objsToAdd.isEmpty()) {
					for (Object objToAdd : objsToAdd) {
						DUtil.addShapeViaFeature(getFeatureProvider(), getDiagram(), objToAdd);
					}
				}
				
				// add connections to diagram
				addConnections(sadConnectInterfaces, getDiagram(), getFeatureProvider());

			} else {
				return new Reason(true, "The sad.xml file and diagram have different number of components and the only reasonable "
					+ "action is to reload the components onto the diagram from the xml file.");
			}

			// TODO: we should probably do this in the model prior to drawing
			// Ensure assembly controller is set. It's possible a component was deleted that used to be the assembly
			// controller
			ComponentPattern.organizeStartOrder(sad, getDiagram(), getFeatureProvider());
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/** Checks if componentShape has lost its reference to the model object*/
	private boolean componentsResolved(List<ComponentShape> componentShapes) {
		for (ComponentShape componentShape : componentShapes) {
			if (!(DUtil.getBusinessObject(componentShape) instanceof SadComponentInstantiation)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public Reason updateNeeded(IUpdateContext context) {
		try {
			return internalUpdate(context, false);
		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
		}
		return null;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason reason;
		try {
			reason = internalUpdate(context, true);
			reason.toBoolean();

			// TODO: THIS IS NOT THE RIGHT WAY, JUST DO IT TO GET SOMETHING WORKING
			// if we changed something lets layout the diagram
//			if (reason.toBoolean()) {
//	        	ZestLayoutDiagramFeature layoutFeature = new ZestLayoutDiagramFeature(getFeatureProvider());
//	        	layoutFeature.execute(null);
//			}

		} catch (CoreException e) {
			// PASS
			// TODO: catch exception
			e.printStackTrace(); // SUPPRESS CHECKSTYLE INLINE
		}

		return false;
	}

	/**
	 * Add necessary FindByShapes based on connections in model
	 * @return
	 */
	public static void addFindBy(List<SadConnectInterface> sadConnectInterfaces, Diagram diagram, IFeatureProvider featureProvider) {

		// contains all the findByStubs that should exist in the diagram
		ArrayList<FindByStub> findByStubs = new ArrayList<FindByStub>();

		// look for findby in connections to add
		for (SadConnectInterface sadConnectInterface : sadConnectInterfaces) {

			// FindBy is always used inside usesPort
			if (sadConnectInterface.getUsesPort() != null && sadConnectInterface.getUsesPort().getFindBy() != null) {

				// get FindBy model object
				FindBy findBy = (FindBy) sadConnectInterface.getUsesPort().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				// does findBy exist in diagram already?
				if (findByStub == null) {
					// create FindBy Shape for Source
					findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}
				// add provides port to stub if doesn't already exist
				boolean uPFound = false;
				for (UsesPortStub p : findByStub.getUses()) {
					if (p.equals(sadConnectInterface.getUsesPort())) {
						uPFound = true;
					}
				}
				if (!uPFound) {
					// add the required usesPort
					AbstractFindByPattern.addUsesPortStubToFindByStub(findByStub, sadConnectInterface.getUsesPort(), featureProvider);
				}
			}

			// lookup Target Anchor
			// sadConnectInterface.getComponentSupportedInterface().getFindBy()
			if (sadConnectInterface.getComponentSupportedInterface() != null
				&& sadConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
				&& sadConnectInterface.getComponentSupportedInterface().getFindBy() != null) {

				// The model provides us with interface information for the FindBy we are connecting to
				FindBy findBy = (FindBy) sadConnectInterface.getComponentSupportedInterface().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				if (findByStub == null) {
					// create findByStub
					findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}

				// findBy nested in ProvidesPort
			} else if (sadConnectInterface.getProvidesPort() != null && sadConnectInterface.getProvidesPort().getFindBy() != null) {

				FindBy findBy = (FindBy) sadConnectInterface.getProvidesPort().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				// does findBy exist in diagram already?
				if (findByStub == null) {
					// create findByStub
					findByStub = AbstractFindByPattern.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}

				// add provides port to stub if doesn't already exist
				boolean ppFound = false;
				for (ProvidesPortStub p : findByStub.getProvides()) {
					if (p.equals(sadConnectInterface.getProvidesPort())) {
						ppFound = true;
					}
				}
				if (!ppFound) {
					// add the required providesPort
					AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, sadConnectInterface.getProvidesPort(), featureProvider);
				}
			}
		}

		// TODO: Should we do this, I don't think it hurts to leave them.
		// get all FindByStub(s) in diagram and remove any that connections aren't using

		// TODO: Should we be removing the ports on the FindBy automatically if they arent's used
		// I don't see the harm in leaving them. User could "edit" the FindByShape if they really wanted to remove them

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

	/**
	 * Removes invalid connections where start/end points no longer exist
	 * TODO:Consider moving this method to another class
	 * @param sadConnectInterfaces
	 * @return
	 */
	public static void removeInvalidConnections(List<SadConnectInterface> sadConnectInterfaces) {

		for (Iterator<SadConnectInterface> connIter = sadConnectInterfaces.iterator(); connIter.hasNext();) {

			// delete connection in model if
			// uses port is present but the referenced component isn't
			// provides port is present but references component isn't
			SadConnectInterface conn = connIter.next();
			if ((conn.getUsesPort() != null && conn.getUsesPort().getComponentInstantiationRef() != null && conn.getUsesPort().getComponentInstantiationRef().getInstantiation() == null)
				|| (conn.getProvidesPort() != null && conn.getProvidesPort().getComponentInstantiationRef() != null && conn.getProvidesPort().getComponentInstantiationRef().getInstantiation() == null)) {

				// endpoint missing, delete connection
				connIter.remove();
				EcoreUtil.delete(conn, true);
			}
		}
	}

	/**
	 * Add new Connections and also add FindBy Shapes where necessary
	 * @param sadConnectInterfaces
	 * @param pictogramLabel
	 * @param featureProvider
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void addConnections(List<SadConnectInterface> sadConnectInterfaces, Diagram diagram, IFeatureProvider featureProvider) throws CoreException {

		// add findByStub shapes
		addFindBy(sadConnectInterfaces, diagram, featureProvider);

		// add Connections found in model, but not in diagram
		for (SadConnectInterface sadConnectInterface : sadConnectInterfaces) {

			// wasn't found, add Connection
			// lookup sourceAnchor
			Anchor sourceAnchor = DUtil.lookupSourceAnchor(sadConnectInterface, diagram);

			// if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
			if (sourceAnchor == null) {

				// FindBy is always used inside usesPort
				if (sadConnectInterface.getUsesPort() != null && sadConnectInterface.getUsesPort().getFindBy() != null) {

					FindBy findBy = (FindBy) sadConnectInterface.getUsesPort().getFindBy();

					// search for findByStub in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// determine which usesPortStub
					UsesPortStub usesPortStub = null;
					for (UsesPortStub p : findByStub.getUses()) {
						if (p != null && sadConnectInterface.getUsesPort().getUsesIndentifier() != null
							&& p.getName().equals(sadConnectInterface.getUsesPort().getUsesIndentifier())) {
							usesPortStub = p;
						}
					}
					// determine port anchor for FindByMatch
					if (usesPortStub != null) {
						PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, (EObject) usesPortStub, Anchor.class);
						sourceAnchor = (Anchor) pe;
					} else {
						System.out.println("our source port is not getting set"); // SUPPRESS CHECKSTYLE INLINE
						// TODO: this means the provides port didn't exist in the existing findByStub..we need
						// to add it
					}
				}
			}

			// lookup Target Anchor
			Anchor targetAnchor = null;
			PictogramElement targetAnchorPe = DUtil.getPictogramElementForBusinessObject(diagram, sadConnectInterface.getTarget(), Anchor.class);
			if (targetAnchorPe != null) {
				targetAnchor = (Anchor) targetAnchorPe;
			} else {

				// sadConnectInterface.getComponentSupportedInterface().getFindBy()
				if (sadConnectInterface.getComponentSupportedInterface() != null
					&& sadConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
					&& sadConnectInterface.getComponentSupportedInterface().getFindBy() != null) {

					// The model provides us with interface information for the FindBy we are connecting to
					FindBy findBy = (FindBy) sadConnectInterface.getComponentSupportedInterface().getFindBy();

					// iterate through FindByStubs in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// determine port anchor for FindByMatch
					if (findByStub.getInterface() != null) {
						PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, findByStub.getInterface(), Anchor.class);
						targetAnchor = (Anchor) pe;
					}

					// findBy nested in ProvidesPort
				} else if (sadConnectInterface.getProvidesPort() != null && sadConnectInterface.getProvidesPort().getFindBy() != null) {

					FindBy findBy = (FindBy) sadConnectInterface.getProvidesPort().getFindBy();

					// iterate through FindByStubs in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// ensure the providesPort exists in FindByStub that already exists in diagram
					boolean foundProvidesPortStub = false;
					for (ProvidesPortStub p : findByStub.getProvides()) {
						if (p.getName().equals(sadConnectInterface.getProvidesPort().getProvidesIdentifier())) {
							foundProvidesPortStub = true;
						}
					}
					if (!foundProvidesPortStub) {
						// add the required providesPort
						AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, sadConnectInterface.getProvidesPort(), featureProvider);
						// Update on FindByStub PE
						DUtil.updateShapeViaFeature(featureProvider, diagram,
							DUtil.getPictogramElementForBusinessObject(diagram, findByStub, RHContainerShape.class));

						// maybe call layout?

					}

					// determine which providesPortStub we are targeting
					ProvidesPortStub providesPortStub = null;
					for (ProvidesPortStub p : findByStub.getProvides()) {
						if (p != null && sadConnectInterface.getProvidesPort().getProvidesIdentifier() != null
							&& p.getName().equals(sadConnectInterface.getProvidesPort().getProvidesIdentifier())) {
							providesPortStub = p;
							break;
						}
					}

					// determine port anchor for FindByMatch
					if (providesPortStub != null) {
						PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, (EObject) providesPortStub, Anchor.class);
						targetAnchor = (Anchor) pe;
					} else {
						// PASS
						// TODO: this means the provides port didn't exist in the existing findByStub..we need
						// to add it
					}
				}
			}

			// add Connection if anchors
			if (sourceAnchor != null && targetAnchor != null) {
				DUtil.addConnectionViaFeature(featureProvider, sadConnectInterface, sourceAnchor, targetAnchor);
			} else {
				// PASS
				// TODO: how do we handle this?
			}
		}
	}

	/**
	 * Search for the FindByStub in the diagram given the findBy object
	 * @param findBy
	 * @param diagram
	 * @return
	 */
	public static FindByStub findFindByStub(FindBy findBy, List<FindByStub> findByStubs) {
		for (FindByStub findByStub : findByStubs) {
			if (AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {
				// it matches
				return findByStub;
			}
		}
		return null;
	}

}
