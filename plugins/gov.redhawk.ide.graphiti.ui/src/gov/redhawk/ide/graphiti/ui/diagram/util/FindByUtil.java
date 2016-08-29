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

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByDomainManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByFileManagerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;

public class FindByUtil {

	private FindByUtil() {
	}

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

}
