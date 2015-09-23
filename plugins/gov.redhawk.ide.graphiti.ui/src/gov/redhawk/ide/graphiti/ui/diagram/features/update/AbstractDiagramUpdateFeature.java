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
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.DefaultUpdateDiagramFeature;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.ui.services.GraphitiUi;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
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
				// add provides port to stub if doesn't already exist
				boolean uPFound = false;
				for (UsesPortStub p : findByStub.getUses()) {
					if (p.equals(connectInterface.getUsesPort())) {
						uPFound = true;
					}
				}
				if (!uPFound) {
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
				boolean ppFound = false;
				for (ProvidesPortStub p : findByStub.getProvides()) {
					if (p.equals(connectInterface.getProvidesPort())) {
						ppFound = true;
					}
				}
				if (!ppFound) {
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
			if ((con.getUsesPort() != null && con.getUsesPort().getComponentInstantiationRef() != null && con.getUsesPort().getComponentInstantiationRef().getInstantiation() == null)
				|| (con.getProvidesPort() != null && con.getProvidesPort().getComponentInstantiationRef() != null && con.getProvidesPort().getComponentInstantiationRef().getInstantiation() == null)) {
				// endpoint missing, delete connection
				connIter.remove();
				EcoreUtil.delete(con, true);
			}
		}
	}

}
