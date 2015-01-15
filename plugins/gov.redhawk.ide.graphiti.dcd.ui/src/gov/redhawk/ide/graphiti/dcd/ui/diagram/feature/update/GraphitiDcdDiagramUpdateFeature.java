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
package gov.redhawk.ide.graphiti.dcd.ui.diagram.feature.update;

import gov.redhawk.ide.graphiti.dcd.ext.DeviceShape;
import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.dcd.ui.diagram.patterns.DevicePattern;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.features.layout.LayoutDiagramFeature;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.FindByUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

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
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class GraphitiDcdDiagramUpdateFeature extends DefaultUpdateDiagramFeature {

	public GraphitiDcdDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}
	/**
	 * Updates the Diagram to reflect the underlying business model
	 * Make sure all elements in dcd model (hosts/components/findby) are accounted for as
	 * children of diagram, if they aren't then add them, if they are then check to see if
	 * they need to be updated, if they exist in the diagram yet not in the model, remove them
	 * @param context
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	public Reason internalUpdate(IUpdateContext context, boolean performUpdate) throws CoreException {

		boolean updateStatus = false;

		PictogramElement pe = context.getPictogramElement();
		if (pe instanceof Diagram) {
			Diagram d = (Diagram) pe;

			// get dcd from diagram
			final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

			// model devices // TODO: Need to do this for services too
			List<DcdComponentInstantiation> componentInstantiations = new ArrayList<DcdComponentInstantiation>();
			if (dcd != null && dcd.getPartitioning() != null && dcd.getPartitioning().getComponentPlacement() != null) {
				// Get list of componentInstantiations from model
				for (DcdComponentPlacement p : dcd.getPartitioning().getComponentPlacement()) {
					Collections.addAll(componentInstantiations,
						(DcdComponentInstantiation[]) p.getComponentInstantiation().toArray(new DcdComponentInstantiation[0]));
				}
			}
			
			// shape devices
			// TODO: Need to update this to look for services
			List<DeviceShape> deviceShapes = DevicePattern.getAllDeviceShapes(d);
			for (Iterator<DeviceShape> iter = deviceShapes.iterator(); iter.hasNext();) {
				if (!(iter.next().eContainer() instanceof Diagram)) {
					iter.remove();
				}
			}

			// model connections
			List<DcdConnectInterface> dcdConnectInterfaces = new ArrayList<DcdConnectInterface>();
			if (dcd != null && dcd.getConnections() != null && dcd.getConnections().getConnectInterface() != null) {
				// Get list of DcdConnectInterfaces from model
				Collections.addAll(dcdConnectInterfaces, (DcdConnectInterface[]) dcd.getConnections().getConnectInterface().toArray(new DcdConnectInterface[0]));
			}
			// remove invalid model connections
			removeInvalidConnections(dcdConnectInterfaces);

			// shape connections
			List<Connection> connections = new ArrayList<Connection>();
			Collections.addAll(connections, (Connection[]) d.getConnections().toArray(new Connection[0]));

			// If inconsistencies are found remove all objects of that type and redraw
			// we must do this because the diagram uses indexed lists to refer to components in the dcd file.
			if (performUpdate) {
				updateStatus = true;

				List<PictogramElement> pesToRemove = new ArrayList<PictogramElement>(); // gather all shapes to remove
				List<Object> objsToAdd = new ArrayList<Object>(); // gather all model object to add

				// If inconsistencies found, redraw diagram elements based on model objects
				boolean layoutNeeded = false;
				if (deviceShapes.size() != componentInstantiations.size() || !devicesResolved(deviceShapes)) {
					Collections.addAll(pesToRemove, (PictogramElement[]) deviceShapes.toArray(new PictogramElement[0]));
					Collections.addAll(objsToAdd, (Object[]) componentInstantiations.toArray(new Object[0]));
					layoutNeeded = true;
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
					// update components
					super.update(context);
				}

				// add shapes to diagram
				if (!objsToAdd.isEmpty()) {
					for (Object objToAdd : objsToAdd) {
						DUtil.addShapeViaFeature(getFeatureProvider(), getDiagram(), objToAdd);
					}
				}

				// add connections to diagram
				addConnections(dcdConnectInterfaces, getDiagram(), getFeatureProvider());

				if (layoutNeeded) {
					LayoutDiagramFeature layoutFeature = new LayoutDiagramFeature(getFeatureProvider());
					layoutFeature.execute(null);
				}
			} else {
				if (deviceShapes.size() != componentInstantiations.size() || !devicesResolved(deviceShapes)) {
					return new Reason(true, "The dcd.xml file and diagram component objects do not match.  Reload the diagram from the xml file.");
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/** Checks if componentShape has lost its reference to the model object */
	private boolean devicesResolved(List<DeviceShape> deviceShapes) {
		for (DeviceShape deviceShape : deviceShapes) {
			DcdComponentInstantiation dcdComponentInstantiation = (DcdComponentInstantiation) DUtil.getBusinessObject(deviceShape, DcdComponentInstantiation.class);
			if (dcdComponentInstantiation == null || dcdComponentInstantiation.getPlacement() != null || dcdComponentInstantiation.getPlacement().getComponentFileRef() != null) {
				return false;
			} else if (deviceShape.getProvidesPortStubs().size() > 0 && !deviceShape.getProvidesPortStubs().get(0).eContainer().equals(dcdComponentInstantiation)) {
				return false;
			} else if (deviceShape.getUsesPortStubs().size() > 0 && !deviceShape.getUsesPortStubs().get(0).eContainer().equals(dcdComponentInstantiation)) {
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
			return reason.toBoolean();
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
	public static void addFindBy(List<DcdConnectInterface> dcdConnectInterfaces, Diagram diagram, IFeatureProvider featureProvider) {

		// contains all the findByStubs that should exist in the diagram
		ArrayList<FindByStub> findByStubs = new ArrayList<FindByStub>();

		// populate list of findByStubs with existing instances from diagram
		List<RHContainerShape> findByShapes = AbstractFindByPattern.getAllFindByShapes(diagram);
		for (RHContainerShape findByShape : findByShapes) {
			findByStubs.add((FindByStub) DUtil.getBusinessObject(findByShape));
		}

		// look for findby in connections to add
		for (DcdConnectInterface dcdConnectInterface : dcdConnectInterfaces) {

			// FindBy is always used inside usesPort
			if (dcdConnectInterface.getUsesPort() != null && dcdConnectInterface.getUsesPort().getFindBy() != null) {

				// get FindBy model object
				FindBy findBy = (FindBy) dcdConnectInterface.getUsesPort().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				// does findBy exist in diagram already?
				if (findByStub == null) {
					// create FindBy Shape for Source
					findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}
				// add provides port to stub if doesn't already exist
				boolean uPFound = false;
				for (UsesPortStub p : findByStub.getUses()) {
					if (p.equals(dcdConnectInterface.getUsesPort())) {
						uPFound = true;
					}
				}
				if (!uPFound) {
					// add the required usesPort
					AbstractFindByPattern.addUsesPortStubToFindByStub(findByStub, dcdConnectInterface.getUsesPort(), featureProvider);
				}
			}

			// lookup Target Anchor
			// dcdConnectInterface.getComponentSupportedInterface().getFindBy()
			if (dcdConnectInterface.getComponentSupportedInterface() != null
				&& dcdConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
				&& dcdConnectInterface.getComponentSupportedInterface().getFindBy() != null) {

				// The model provides us with interface information for the FindBy we are connecting to
				FindBy findBy = (FindBy) dcdConnectInterface.getComponentSupportedInterface().getFindBy();

				// search for findByStub in the list
				FindByStub findByStub = findFindByStub(findBy, findByStubs);

				if (findByStub == null) {
					// create findByStub
					findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

					// add to list
					findByStubs.add(findByStub);
				}

				// findBy nested in ProvidesPort
			} else if (dcdConnectInterface.getProvidesPort() != null && dcdConnectInterface.getProvidesPort().getFindBy() != null) {

				FindBy findBy = (FindBy) dcdConnectInterface.getProvidesPort().getFindBy();

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
				boolean ppFound = false;
				for (ProvidesPortStub p : findByStub.getProvides()) {
					if (p.equals(dcdConnectInterface.getProvidesPort())) {
						ppFound = true;
					}
				}
				if (!ppFound) {
					// add the required providesPort
					AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, dcdConnectInterface.getProvidesPort(), featureProvider);
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

	/**
	 * Removes invalid connections where start/end points no longer exist
	 * TODO:Consider moving this method to another class
	 * @param dcdConnectInterfaces
	 * @return
	 */
	public static void removeInvalidConnections(List<DcdConnectInterface> dcdConnectInterfaces) {

		for (Iterator<DcdConnectInterface> connIter = dcdConnectInterfaces.iterator(); connIter.hasNext();) {

			// delete connection in model if either uses port is present but the referenced component isn't, 
			// or provides port is present but references component isn't
			DcdConnectInterface con = connIter.next();
			if ((con.getUsesPort() != null && con.getUsesPort().getComponentInstantiationRef() != null && con.getUsesPort().getComponentInstantiationRef().getInstantiation() == null)
				|| (con.getProvidesPort() != null && con.getProvidesPort().getComponentInstantiationRef() != null && con.getProvidesPort().getComponentInstantiationRef().getInstantiation() == null)) {

				// endpoint missing, delete connection
				connIter.remove();
				EcoreUtil.delete(con, true);
			}
		}
	}

	/**
	 * Add new Connections and also add FindBy Shapes where necessary
	 * @param dcdConnectInterfaces
	 * @param pictogramLabel
	 * @param featureProvider
	 * @param performUpdate
	 * @return
	 * @throws CoreException
	 */
	public static void addConnections(List<DcdConnectInterface> dcdConnectInterfaces, Diagram diagram, IFeatureProvider featureProvider) throws CoreException {

		// add findByStub shapes
		addFindBy(dcdConnectInterfaces, diagram, featureProvider);

		// add Connections found in model, but not in diagram
		for (DcdConnectInterface dcdConnectInterface : dcdConnectInterfaces) {

			// wasn't found, add Connection
			// lookup sourceAnchor
			Anchor sourceAnchor = DUtil.lookupSourceAnchor(dcdConnectInterface, diagram);

			// if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
			if (sourceAnchor == null) {

				// FindBy is always used inside usesPort
				if (dcdConnectInterface.getUsesPort() != null && dcdConnectInterface.getUsesPort().getFindBy() != null) {

					FindBy findBy = (FindBy) dcdConnectInterface.getUsesPort().getFindBy();

					// search for findByStub in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// determine which usesPortStub
					UsesPortStub usesPortStub = null;
					for (UsesPortStub p : findByStub.getUses()) {
						if (p != null && dcdConnectInterface.getUsesPort().getUsesIndentifier() != null
							&& p.getName().equals(dcdConnectInterface.getUsesPort().getUsesIndentifier())) {
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
			PictogramElement targetAnchorPe = DUtil.getPictogramElementForBusinessObject(diagram, dcdConnectInterface.getTarget(), Anchor.class);
			if (targetAnchorPe != null) {
				targetAnchor = (Anchor) targetAnchorPe;
			} else {

				// dcdConnectInterface.getComponentSupportedInterface().getFindBy()
				if (dcdConnectInterface.getComponentSupportedInterface() != null
					&& dcdConnectInterface.getComponentSupportedInterface().getSupportedIdentifier() != null
					&& dcdConnectInterface.getComponentSupportedInterface().getFindBy() != null) {

					// The model provides us with interface information for the FindBy we are connecting to
					FindBy findBy = (FindBy) dcdConnectInterface.getComponentSupportedInterface().getFindBy();

					// iterate through FindByStubs in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// determine port anchor for FindByMatch
					if (findByStub.getInterface() != null) {
						PictogramElement pe = DUtil.getPictogramElementForBusinessObject(diagram, findByStub.getInterface(), Anchor.class);
						targetAnchor = (Anchor) pe;
					}

					// findBy nested in ProvidesPort
				} else if (dcdConnectInterface.getProvidesPort() != null && dcdConnectInterface.getProvidesPort().getFindBy() != null) {

					FindBy findBy = (FindBy) dcdConnectInterface.getProvidesPort().getFindBy();

					// iterate through FindByStubs in diagram
					FindByStub findByStub = DUtil.findFindByStub(findBy, diagram);

					if (findByStub == null) {
						// should never occur, addRemoveUpdateFindBy() takes care of this
						throw new CoreException(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Unable to locate FindBy Shape in Diagram"));
					}

					// ensure the providesPort exists in FindByStub that already exists in diagram
					boolean foundProvidesPortStub = false;
					for (ProvidesPortStub p : findByStub.getProvides()) {
						if (p.getName().equals(dcdConnectInterface.getProvidesPort().getProvidesIdentifier())) {
							foundProvidesPortStub = true;
						}
					}
					if (!foundProvidesPortStub) {
						// add the required providesPort
						AbstractFindByPattern.addProvidesPortStubToFindByStub(findByStub, dcdConnectInterface.getProvidesPort(), featureProvider);
						// Update on FindByStub PE
						DUtil.updateShapeViaFeature(featureProvider, diagram,
							DUtil.getPictogramElementForBusinessObject(diagram, findByStub, RHContainerShape.class));

						// maybe call layout?

					}

					// determine which providesPortStub we are targeting
					ProvidesPortStub providesPortStub = null;
					for (ProvidesPortStub p : findByStub.getProvides()) {
						if (p != null && dcdConnectInterface.getProvidesPort().getProvidesIdentifier() != null
							&& p.getName().equals(dcdConnectInterface.getProvidesPort().getProvidesIdentifier())) {
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
				DUtil.addConnectionViaFeature(featureProvider, dcdConnectInterface, sourceAnchor, targetAnchor);
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
