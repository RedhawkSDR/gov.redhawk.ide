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
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ImageProvider;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.Connections;
import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;

import java.util.List;

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;

public class FindByEventChannelPattern extends AbstractFindByPattern implements IDialogEditingPattern {

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
	protected FindByStub createFindByStub(ICreateContext context) {
		final String eventChannelName = getUserInput();
		if (eventChannelName == null) {
			return null;
		}

		return FindByEventChannelPattern.create(eventChannelName);
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
	public static FindByStub create(String eventChannel) {
		FindByStub findByStub = PartitioningFactory.eINSTANCE.createFindByStub();

		// interface stub (lollipop)
		findByStub.setInterface(PartitioningFactory.eINSTANCE.createComponentSupportedInterfaceStub());

		// domain finder service of type domain manager
		DomainFinder domainFinder = PartitioningFactory.eINSTANCE.createDomainFinder();
		domainFinder.setType(DomainFinderType.EVENTCHANNEL);
		domainFinder.setName(eventChannel);
		findByStub.setDomainFinder(domainFinder);

		return findByStub;
	}

	@Override
	public String getInnerTitle(FindByStub findByStub) {
		return findByStub.getDomainFinder().getName();
	}
	
	@Override
	protected void setInnerTitle(FindByStub findByStub, List<FindBy> findBys, String value) {
		findByStub.getDomainFinder().setName(value);
		for (FindBy findBy : findBys) {
			findBy.getDomainFinder().setName(value);
		}
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
	
	private AbstractInputValidationDialog getDialog() {
		return new AbstractInputValidationDialog(
			NAME, "Enter the name of the event channel to find", "Name") {
			@Override
			public String inputValidity(String value) {
				return checkValueValid(value, null);
			}
		};
	}
	
	@Override
	protected String getOuterImageId() {
		return ImageProvider.IMG_FIND_BY;
	}

	@Override
	public boolean canDialogEdit(ICustomContext context) {
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			return isMainBusinessObjectApplicable(getBusinessObjectForPictogramElement(pes[0]));
		}
		return false;
	}

	@Override
	public boolean dialogEdit(ICustomContext context) {
		final PictogramElement findByPE = context.getPictogramElements()[0];
		final FindByStub findByStub = (FindByStub) getBusinessObjectForPictogramElement(findByPE);

		String oldName = findByStub.getDomainFinder().getName();
		final String eventChannelName = getDialog().getInput(oldName);

		if (eventChannelName == null || eventChannelName.equals(oldName)) {
			return false;
		}

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set event name
				findByStub.getDomainFinder().setName(eventChannelName);
				
				// Update the domain finder of all associated findBy model elements
				String connectId = null;
				for (Property prop : findByPE.getProperties()) {
					if (AbstractConnectInterfacePattern.CONNECT_INTERFACE_ID.equals(prop.getKey())) {
						connectId = prop.getValue();
						Connections< ? > modelConnections = getModelConnections();
						for (ConnectInterface<?, ?, ?> ci : modelConnections.getConnectInterface()) {
							if (connectId.equals(ci.getId())) {
								FindBy fb = null;
								if (ci.getProvidesPort() != null) {
									fb = ci.getProvidesPort().getFindBy();
								} else {
									fb = ci.getComponentSupportedInterface().getFindBy();
								}
								if (fb != null) {
									fb.getDomainFinder().setName(eventChannelName);
								}
								
								fb = ci.getUsesPort().getFindBy();
								if (fb != null) {
									fb.getDomainFinder().setName(eventChannelName);
								}								
							}
						}
					}
				}
			}
		});
		updatePictogramElement(findByPE);
		layoutPictogramElement(findByPE);
		return true;
	}

	@Override
	public String getEditName() {
		return NAME;
	}
}
