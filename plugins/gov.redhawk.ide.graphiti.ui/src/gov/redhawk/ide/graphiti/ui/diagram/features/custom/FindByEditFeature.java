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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.dialogs.AbstractInputValidationDialog;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByCORBANamePattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByEventChannelPattern;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.FindByServicePattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByCORBANameWizardPage;
import gov.redhawk.ide.graphiti.ui.diagram.wizards.FindByServiceWizardPage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.partitioning.DomainFinder;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.ICreateConnectionFeature;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.impl.CreateConnectionContext;
import org.eclipse.graphiti.features.context.impl.DeleteContext;
import org.eclipse.graphiti.features.custom.AbstractCustomFeature;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Connection;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.jface.wizard.Wizard;

public class FindByEditFeature extends AbstractCustomFeature {

	private final IFeatureProvider featureProvider;

	/*
	 * Constructor
	 */
	public FindByEditFeature(IFeatureProvider fp) {
		super(fp);
		this.featureProvider = fp;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Edit Find By";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.impl.AbstractFeature#getName()
	 */
	@Override
	public String getName() {
		return "&Edit Find By";
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.AbstractCustomFeature#canExecute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public boolean canExecute(ICustomContext context) {

		// We only want the edit context to show up for certain objects
		PictogramElement[] pes = context.getPictogramElements();
		if (pes != null && pes.length == 1) {
			if (DUtil.getBusinessObject(pes[0]) instanceof FindByStub) {
				FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(pes[0]);
				DomainFinderType type = (findByStub.getDomainFinder() != null) ? findByStub.getDomainFinder().getType() : null;
				return (findByStub.getNamingService() != null || DomainFinderType.SERVICENAME.equals(type) || DomainFinderType.SERVICETYPE.equals(type) || DomainFinderType.EVENTCHANNEL.equals(type));
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.graphiti.features.custom.ICustomFeature#execute(org.eclipse.graphiti.features.context.ICustomContext)
	 */
	@Override
	public void execute(ICustomContext context) {
		RHContainerShapeImpl findByShape = (RHContainerShapeImpl) context.getPictogramElements()[0];
		final FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(findByShape);
		if (findByStub.getNamingService() != null) {
			editFindByCorbaName(findByStub, findByShape);
		} else if (findByStub.getDomainFinder() != null) {
			DomainFinderType type = findByStub.getDomainFinder().getType();
			if (DomainFinderType.SERVICENAME.equals(type) || DomainFinderType.SERVICETYPE.equals(type)) {
				editFindByService(findByStub, findByShape);
			} else if (DomainFinderType.EVENTCHANNEL.equals(type)) {
				editFindByEventChannel(findByStub);
			}
			// DUtil.updateShapeViaFeature(getFeatureProvider(), getDiagram(), pe);
		}
		updatePictogramElement(findByShape);
		layoutPictogramElement(findByShape);
	}

	// Create the edit wizard to be used
	private static Wizard getEditWizard() {
		return new Wizard() {
			public boolean performFinish() {
				return true;
			}
		};
	}

	private void editFindByCorbaName(final FindByStub findByStub, final RHContainerShapeImpl findByShape) {
		// Find By CORBA Name
		FindByCORBANameWizardPage page = FindByCORBANamePattern.openWizard(findByStub, getEditWizard());
		if (page == null) {
			return;
		}

		// get user selections
		final String corbaNameText = page.getModel().getCorbaName();
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null) ? page.getModel().getUsesPortNames() : null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null) ? page.getModel().getProvidesPortNames() : null;

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				findByStub.getNamingService().setName(corbaNameText);
				// if applicable, add uses and provides port stub(s)
				updatePorts(findByStub, findByShape, usesPortNames, providesPortNames);
			}
		});
	}

	// ports
	private void editFindByService(final FindByStub findByStub, final RHContainerShapeImpl findByShape) {
		// Find By Service
		FindByServiceWizardPage page = FindByServicePattern.getWizardPage(findByStub, getEditWizard());
		if (page == null) {
			return;
		}

		// Push any new values to the FindByStub object
		final String serviceNameText = page.getModel().getEnableServiceName() ? page.getModel().getServiceName() : null;
		final String serviceTypeText = page.getModel().getEnableServiceType() ? page.getModel().getServiceType() : null;
		final List<String> usesPortNames = (page.getModel().getUsesPortNames() != null) ? page.getModel().getUsesPortNames() : null;
		final List<String> providesPortNames = (page.getModel().getProvidesPortNames() != null) ? page.getModel().getProvidesPortNames() : null;

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {

			@Override
			protected void doExecute() {
				final DomainFinder domainFinder = findByStub.getDomainFinder();
				if (serviceNameText != null && !serviceNameText.isEmpty() && !serviceNameText.equals(domainFinder.getName())) {
					domainFinder.setType(DomainFinderType.SERVICENAME);
					domainFinder.setName(serviceNameText);
				} else if (serviceTypeText != null && !serviceTypeText.isEmpty()) {
					domainFinder.setType(DomainFinderType.SERVICETYPE);
					domainFinder.setName(serviceTypeText);
				}

				// if applicable, add uses and provides port stub(s)
				updatePorts(findByStub, findByShape, usesPortNames, providesPortNames);
			}
		});

	}

	private void editFindByEventChannel(final FindByStub findByStub) {
		AbstractInputValidationDialog dialog = (new FindByEventChannelPattern()).getDialog();
		final String eventChannelName = dialog.getInput(findByStub.getDomainFinder().getName());

		if (eventChannelName == null) {
			return;
		}

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set event name
				findByStub.getDomainFinder().setName(eventChannelName);
			}
		});
	}

	private void updatePorts(final FindByStub findByStub, final RHContainerShapeImpl findByShape, final List<String> usesPortNames,
		final List<String> providesPortNames) {
		Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();

		// Update uses port stub(s)
		if (usesPortNames != null) {
			// Mark the ports to delete
			List<UsesPortStub> portsToDelete = new ArrayList<UsesPortStub>();
			for (UsesPortStub uses : findByStub.getUses()) {
				portsToDelete.add(uses);
			}

			// Capture the existing connection information and delete the connection
			HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
			for (UsesPortStub portStub : portsToDelete) {
				Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
				EList<Connection> connections = portStubPe.getOutgoingConnections();
				if (!connections.isEmpty()) {
					for (Connection connection : connections) {
						oldConnectionMap.put(connection, portStub.getName());
					}
				}
			}

			// Add new ports to the FindBy element
			EList<UsesPortStub> usesPortStubs = new BasicEList<UsesPortStub>();
			for (String usesPortName : usesPortNames) {
				// Add the new port to the Domain model
				UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
				usesPortStub.setName(usesPortName);
				findByStub.getUses().add(usesPortStub);
				usesPortStubs.add(usesPortStub);
			}

			// Add the new ports to the Diagram model
			findByShape.setUsesPorts((EList<UsesPortStub>) usesPortStubs, featureProvider);

			// Build the new connections using the reconnect feature
			for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
				// First check if port still even exists
				Anchor sourceAnchor = DUtil.getUsesAnchor(diagram, usesPortStubs, cursor.getValue());
				if (sourceAnchor != null) {
					CreateConnectionContext createContext = new CreateConnectionContext();
					createContext.setSourceAnchor(sourceAnchor);
					createContext.setTargetAnchor(cursor.getKey().getEnd());

					ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
					for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
						if (createConnectionFeature.canCreate(createContext)) {
							createConnectionFeature.create(createContext);
						}
					}
				}

				// Delete the old connection
				for (int i = 0; i < oldConnectionMap.size(); i++) {
					DeleteContext deleteContext = new DeleteContext(cursor.getKey());
					featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
				}
			}

			// Delete all ports and rebuild from the list provided by the FindByEdit wizard
			findByStub.getUses().clear();
			findByStub.getUses().addAll(usesPortStubs);
		}

		// Update provides port stub(s)
		if (providesPortNames != null) {
			// Mark the ports to delete
			List<ProvidesPortStub> portsToDelete = new ArrayList<ProvidesPortStub>();
			for (ProvidesPortStub provides : findByStub.getProvides()) {
				portsToDelete.add(provides);
			}

			// Capture the existing connection information and delete the connection
			HashMap<Connection, String> oldConnectionMap = new HashMap<Connection, String>();
			for (ProvidesPortStub portStub : portsToDelete) {
				Anchor portStubPe = (Anchor) DUtil.getPictogramElementForBusinessObject(diagram, (EObject) portStub, Anchor.class);
				EList<Connection> connections = portStubPe.getIncomingConnections();
				if (!connections.isEmpty()) {
					for (Connection connection : connections) {
						oldConnectionMap.put(connection, portStub.getName());
					}
				}
			}

			// Add new ports to the FindBy element
			EList<ProvidesPortStub> providesPortStubs = new BasicEList<ProvidesPortStub>();
			for (String providesPortName : providesPortNames) {
				// Add the new port to the Domain model
				ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
				providesPortStub.setName(providesPortName);
				findByStub.getProvides().add(providesPortStub);
				providesPortStubs.add(providesPortStub);
			}

			// Add the new ports to the Diagram model
			findByShape.setProvidesPorts((EList<ProvidesPortStub>) providesPortStubs, featureProvider);

			// Build the new connections using the reconnect feature
			for (Map.Entry<Connection, String> cursor : oldConnectionMap.entrySet()) {
				// First check if port still even exists
				Anchor targetAnchor = DUtil.getProvidesAnchor(diagram, providesPortStubs, cursor.getValue());
				if (targetAnchor != null) {
					CreateConnectionContext createContext = new CreateConnectionContext();
					createContext.setSourceAnchor(cursor.getKey().getStart());
					createContext.setTargetAnchor(targetAnchor);

					ICreateConnectionFeature[] createConnectionFeatures = featureProvider.getCreateConnectionFeatures();
					for (ICreateConnectionFeature createConnectionFeature : createConnectionFeatures) {
						if (createConnectionFeature.canCreate(createContext)) {
							createConnectionFeature.create(createContext);
						}
					}
				}

				// Delete the old connection
				for (int i = 0; i < oldConnectionMap.size(); i++) {
					DeleteContext deleteContext = new DeleteContext(cursor.getKey());
					featureProvider.getDeleteFeature(deleteContext).delete(deleteContext);
				}
			}

			// Delete the old ports
			findByStub.getProvides().removeAll(portsToDelete);
		}

		// Update the shape layout to account for any changes
		findByShape.layout(featureProvider);
	}


}
