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
package gov.redhawk.ide.graphiti.ui.diagram.features.update;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;

import gov.redhawk.core.graphiti.ui.diagram.features.AbstractDiagramUpdateFeature;
import gov.redhawk.core.graphiti.ui.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.FindByUtil;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;

public abstract class AbstractDesignDiagramUpdateFeature extends AbstractDiagramUpdateFeature {

	public AbstractDesignDiagramUpdateFeature(IFeatureProvider fp) {
		super(fp);
	}

	protected FindByStub addFindByStub(FindBy findBy, Diagram diagram) {
		return FindByUtil.createFindByStub(findBy, getFeatureProvider(), diagram);
	}

	protected List<FindByStub> getFindByStubs(Diagram diagram) {
		// contains all the findByStubs that should exist in the diagram
		ArrayList<FindByStub> findByStubs = new ArrayList<FindByStub>();

		// populate list of findByStubs with existing instances from diagram
		List<RHContainerShape> findByShapes = AbstractFindByPattern.getAllFindByShapes(diagram);
		for (RHContainerShape findByShape : findByShapes) {
			findByStubs.add(DUtil.getBusinessObject(findByShape, FindByStub.class));
		}
		return findByStubs;
	}

	protected FindByStub getFindByStub(FindBy findBy, Diagram diagram) {
		for (FindByStub findByStub : getFindByStubs(diagram)) {
			if (AbstractFindByPattern.doFindByObjectsMatch(findBy, findByStub)) {
				return findByStub;
			}
		}
		return null;
	}

}
