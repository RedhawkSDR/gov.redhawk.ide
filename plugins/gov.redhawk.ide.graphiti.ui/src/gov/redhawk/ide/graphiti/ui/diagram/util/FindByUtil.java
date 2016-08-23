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
package gov.redhawk.ide.graphiti.ui.diagram.util;

import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;

import java.util.List;
import java.util.ListIterator;

import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FindByUtil { // SUPPRESS CHECKSTYLE INLINE

	/**
	 * Create FindByStub in the diagram based on values in findBy
	 * @param findBy
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub createFindByStub(FindBy findBy, IFeatureProvider featureProvider, Diagram diagram) {
		FindByStub findByStub = FindByUtil.createFindByStub(findBy);
		if (findByStub != null) {
			AbstractFindByPattern.addFindByToDiagram(diagram, featureProvider, findByStub);
		}
		return findByStub;
	}

	/**
	 * Creates a FindByStub instance based on the input FindBy. The result is not contained by any resource.
	 * @param findBy
	 * @return
	 */
	private static FindByStub createFindByStub(FindBy findBy) {
		if (findBy.getNamingService() != null) {
			String name = findBy.getNamingService().getName();
			if (name != null) {
				return FindByCORBANamePattern.create(name);
			}
		} else if (findBy.getDomainFinder() != null) {
			DomainFinder domainFinder = findBy.getDomainFinder();
			String name = domainFinder.getName();
			switch (domainFinder.getType()) {
			case DOMAINMANAGER:
				return FindByDomainManagerPattern.create();
			case EVENTCHANNEL:
				if (name != null) {
					return FindByEventChannelPattern.create(name);
				}
				break;
			case FILEMANAGER:
				return FindByFileManagerPattern.create();
			case LOG:
				// Log is not supported
				break;
			case NAMINGSERVICE:
				if (name != null) {
					return FindByCORBANamePattern.create(name);
				}
				break;
			case SERVICENAME:
				if (name != null) {
					return FindByServicePattern.createFindByServiceName(name);
				}
				break;
			case SERVICETYPE:
				if (name != null) {
					return FindByServicePattern.createFindByServiceType(name);
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	/**
	 * Iterates through all sadConnectInterfaces to look for connections that use findBy tag that are not contained
	 * within ComponentInterface or Provides
	 * All FindBy tags without one of the two parents will be contained within a ComponentInterface afterwards. The
	 * purpose of this is to draw connections
	 * to the lollipop port in the diagram as apposed to directly connecting to the FindBy Box.
	 * @param sadConnectInterfaces
	 * @param objectClass
	 * @param pictogramLabel
	 * @param diagram
	 * @param featureProvider
	 * @param performUpdate
	 * @return
	 */
	public static Reason replaceDirectFindByConnection(List<SadConnectInterface> sadConnectInterfaces, Class<?> objectClass, String pictogramLabel,
		Diagram diagram, IFeatureProvider featureProvider, boolean performUpdate) {

		boolean updateStatus = false;

		// iterate through all connections
		for (ListIterator<SadConnectInterface> iter = sadConnectInterfaces.listIterator(); iter.hasNext();) {
			SadConnectInterface sadConnectInterface = iter.next();

			if (sadConnectInterface.getFindBy() != null && sadConnectInterface.getFindBy() instanceof FindBy) {

				// The Graphiti SAD diagram doesn't support this type of connection and therefore it will not exist
				// in the diagram, it may however exist in the model and we will convert it to use
				// ComponentSupportedInterface

				// lookup source anchor
				Anchor sourceAnchor = DUtil.lookupSourceAnchor(sadConnectInterface, diagram);

				// if sourceAnchor wasn't found its because the findBy needs to be added to the diagram
				if (sourceAnchor == null) {

					// FindBy is always used inside usesPort
					if (sadConnectInterface.getUsesPort() != null && sadConnectInterface.getUsesPort().getFindBy() != null) {
						if (performUpdate) {
							updateStatus = true;
							FindBy findBy = (FindBy) sadConnectInterface.getUsesPort().getFindBy();

							// create FindBy Shape for Source
							FindByStub findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

							// add FindBYShape for Target
							if (findByStub != null) {
								DUtil.addShapeViaFeature(featureProvider, diagram, findByStub);
							}
						} else {
							return new Reason(true, "Add FindBy Shape");
						}
					}
				}

				// The model isn't providing us with an interface for the FindBy. First step is to modify the model and
				// select an interface
				FindBy findBy = (FindBy) sadConnectInterface.getFindBy();

				// create FindBy Shape for Target
				FindByStub findByStub = FindByUtil.createFindByStub(findBy, featureProvider, diagram);

				// add FindBYShape for Target
				PictogramElement pe = DUtil.addShapeViaFeature(featureProvider, diagram, findByStub);

				Anchor targetAnchor = null;
				if (pe != null && pe instanceof RHContainerShape) {
					// determine lollipop anchor
					PictogramElement peAnchor = DUtil.getPictogramElementForBusinessObject(diagram, (EObject) findByStub.getInterface(), Anchor.class);
					targetAnchor = (Anchor) peAnchor;
				}

				// create a new connection and remove the connection we are currently analyzing
				if (sourceAnchor == null || targetAnchor == null) {
					// TODO: we might want to alert user of this...log maybe
					break;
				}

				// remove the current connection, and replace it with this new connection
				// remove
				iter.remove();

				// create
				CreateConnectionContext createContext = new CreateConnectionContext();
				createContext.setSourceAnchor(sourceAnchor);
				createContext.setTargetAnchor(targetAnchor);
				for (ICreateConnectionFeature createFeature : featureProvider.getCreateConnectionFeatures()) {
					if (createFeature.canCreate(createContext)) {
						featureProvider.getDiagramTypeProvider().getDiagramBehavior().executeFeature(createFeature, createContext);
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}
}
