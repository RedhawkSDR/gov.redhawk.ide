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
package gov.redhawk.ide.graphiti.ui.diagram.patterns;

import gov.redhawk.diagram.util.FindByStubUtil;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.pattern.IPattern;

public class FindByDomainManagerPattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "Domain Manager";
	public static final String SHAPE_TITLE = "Domain Manager";

	public FindByDomainManagerPattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return NAME;
	}

	@Override
	public String getCreateDescription() {
		return "";
	}

	@Override
	public String getCreateImageId() {
		return ImageProvider.IMG_FIND_BY_DOMAIN_MANAGER;
	}

	// THE FOLLOWING METHOD DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	protected boolean isMatchingFindByType(FindByStub findByStub) {
		return FindByStubUtil.isFindByStubDomainManager(findByStub);
	}

	// DIAGRAM FEATURES
	@Override
	protected FindByStub createFindByStub(ICreateContext context) {

		final FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();

		// interface stub (lollipop)
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

		// domain finder service of type domain manager
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		domainFinder.setType(DomainFinderType.DOMAINMANAGER);
		findByStub.setDomainFinder(domainFinder);

		return findByStub;
	}

	/**
	 * Creates the FindByStub in the diagram
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @return
	 */
	public static FindByStub create() {
		FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();

		// interface stub (lollipop)
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

		// domain finder service of type domain manager
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		domainFinder.setType(DomainFinderType.DOMAINMANAGER);
		findByStub.setDomainFinder(domainFinder);

		return findByStub;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		return false;
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return null;
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return SHAPE_TITLE;
	}

	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}
