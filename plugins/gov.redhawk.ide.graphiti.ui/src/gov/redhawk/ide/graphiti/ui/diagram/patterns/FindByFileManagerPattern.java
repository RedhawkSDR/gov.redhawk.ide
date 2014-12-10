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

import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractFindByPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.pattern.IPattern;

public class FindByFileManagerPattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "File Manager";
	public static final String SHAPE_TITLE = "File Manager";

	public FindByFileManagerPattern() {
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
		return ImageProvider.IMG_FIND_BY_FILE_MANAGER;
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof FindByStub) {
			FindByStub findByStub = (FindByStub) mainBusinessObject;
			if (findByStub.getDomainFinder() != null && findByStub.getDomainFinder().getType().equals(DomainFinderType.FILEMANAGER)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object[] create(ICreateContext context) {

		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				domainFinder.setType(DomainFinderType.FILEMANAGER);
				findByStubs[0].setDomainFinder(domainFinder);

				// add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);

			}
		});

		addGraphicalRepresentation(context, findByStubs[0]);

		return new Object[] { findByStubs[0] };
	}

	/**
	 * Creates the FindByStub in the diagram
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param namingServiceText
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub create(final IFeatureProvider featureProvider, final Diagram diagram) {
		final FindByStub[] findByStubs = new FindByStub[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				findByStubs[0] = PartitioningFactory.eINSTANCE.createFindByStub();

				// interface stub (lollipop)
				findByStubs[0].setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

				// domain finder service of type file manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				domainFinder.setType(DomainFinderType.FILEMANAGER);
				findByStubs[0].setDomainFinder(domainFinder);

				// add to diagram resource file
				diagram.eResource().getContents().add(findByStubs[0]);

			}
		});

		return findByStubs[0];
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return SHAPE_TITLE;
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
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}
}
