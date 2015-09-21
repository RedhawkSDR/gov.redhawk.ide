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
import gov.redhawk.ide.graphiti.ui.diagram.dialogs.AbstractInputValidationDialog;
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
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.pattern.IPattern;

public class FindByEventChannelPattern extends AbstractFindByPattern implements IPattern {

	public static final String NAME = "Event Channel";

	public FindByEventChannelPattern() {
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
		return FindByStubUtil.isFindByStubEventChannel(findByStub);
	}

	// DIAGRAM FEATURES

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public Object[] create(ICreateContext context) {
		final String eventChannelName = getUserInput();
		if (eventChannelName == null) {
			return null;
		}

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
				domainFinder.setType(DomainFinderType.EVENTCHANNEL);
				domainFinder.setName(eventChannelName);
				findByStubs[0].setDomainFinder(domainFinder);

				// add to diagram resource file
				getDiagram().eResource().getContents().add(findByStubs[0]);

			}
		});

		addGraphicalRepresentation(context, findByStubs[0]);

		return new Object[] { findByStubs[0] };
	}

	/**
	 * Creates the FindByStub in the diagram with the provided eventChannel
	 * Has no real purpose in this class except that it's logic is extremely similar to the above create method. It's
	 * purpose
	 * is to create a FindByStub using information in the model sad.xml file when no diagram file is available
	 * @param namingServiceText
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub create(final String eventChannel, final IFeatureProvider featureProvider, final Diagram diagram) {

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

				// domain finder service of type domain manager
				DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
				domainFinder.setType(DomainFinderType.EVENTCHANNEL);
				domainFinder.setName(eventChannel);
				findByStubs[0].setDomainFinder(domainFinder);

				// add to diagram resource file
				diagram.eResource().getContents().add(findByStubs[0]);

			}
		});

		return findByStubs[0];
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
	}
	
	@Override
	public void setInnerTitle(FindByStub findByStub, String value) {
		findByStub.getDomainFinder().setName(value);
	}

	/**
	 * Creates a dialog which prompts the user for an event channel name.
	 * Will return <code>null</code> if the user terminates the dialog via
	 * 'Cancel' or otherwise.
	 * @return event channel name
	 */
	private String getUserInput() {
		// prompt user for FindBy Event Channel name
		return getDialog().getInput();
	}
	
	public AbstractInputValidationDialog getDialog() {
		return new AbstractInputValidationDialog(
			NAME, "Enter the name of the event channel to find", "Name") {
			@Override
			public String inputValidity(String value) {
				return checkValueValid(value, null);
			}
		};
	}
	
	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}

}
