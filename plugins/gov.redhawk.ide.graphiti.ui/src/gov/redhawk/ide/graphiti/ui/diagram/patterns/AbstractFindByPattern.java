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

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ConnectInterface;
import mil.jpeojtrs.sca.partitioning.Connections;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPort;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPort;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.jface.wizard.Wizard;

public abstract class AbstractFindByPattern extends AbstractPortSupplierPattern {

	public AbstractFindByPattern() {
		super(null);
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof FindByStub) {
			return isMatchingFindByType((FindByStub) mainBusinessObject);
		}
		return false;
	}

	@Override
	protected boolean isPatternControlled(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	@Override
	protected boolean isPatternRoot(PictogramElement pictogramElement) {
		Object domainObject = getBusinessObjectForPictogramElement(pictogramElement);
		return isMainBusinessObjectApplicable(domainObject);
	}

	/**
	 * Checks whether the FindBy type is supported by this pattern. Must be implemented by subclasses. 
	 */
	protected abstract boolean isMatchingFindByType(FindByStub findByStub);

	// DIAGRAM FEATURES
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof FindByStub) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected String getOuterTitle(EObject obj) {
		if (obj instanceof FindByStub) {
			return getOuterTitle((FindByStub) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the outer shape
	 * @param findByStub
	 * @return
	 */
	public String getOuterTitle(FindByStub findByStub) {
		return getCreateName();
	}

	@Override
	protected String getInnerTitle(EObject obj) {
		if (obj instanceof FindByStub) {
			return getInnerTitle((FindByStub) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	public abstract String getInnerTitle(FindByStub findByStub);

	@Override
	protected void setInnerTitle(EObject businessObject, String value) {
		FindByStub findByStub = (FindByStub) businessObject;
		setInnerTitle((FindByStub) businessObject, getModelFindBys(findByStub), value);
	}

	/**
	 * Sets the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	protected void setInnerTitle(FindByStub findByStub, List<FindBy> findBys, String value) {
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public boolean canMoveShape(IMoveShapeContext context) {
		if (!super.canMoveShape(context)) {
			return false;
		}
		GraphicsAlgorithm ga = context.getShape().getGraphicsAlgorithm();
		if (DUtil.overlapsHostCollocation(getDiagram(), ga.getWidth(), ga.getHeight(), context.getX(), context.getY())) {
			return false;
		}
		return true;
	}

	@Override
	public Object[] create(ICreateContext context) {
		FindByStub findByStub = createFindByStub(context);
		AbstractFindByPattern.addFindByToDiagram(getDiagram(), getFeatureProvider(), findByStub);
		addGraphicalRepresentation(context, findByStub);
		return new Object[] { findByStub };
	}

	protected abstract FindByStub createFindByStub(ICreateContext context);

	public static void addFindByToDiagram(final Diagram diagram, IFeatureProvider featureProvider, final FindByStub findByStub) {
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// add to diagram resource file
				diagram.eResource().getContents().add(findByStub);
			}
		});
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		if (obj instanceof FindByStub) {
			return true;
		}
		return super.canDelete(context);
	}

	@Override
	public void delete(IDeleteContext context) {
		// set Find By to delete
		final FindByStub findByToDelete = (FindByStub) DUtil.getBusinessObject(context.getPictogramElement());

		Collection<?> toRemove = getFindByConnections(findByToDelete);
		if (!toRemove.isEmpty()) {
			// editing domain for our transaction
			TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

			// Perform business object manipulation in a Command
			Command command = RemoveCommand.create(editingDomain, toRemove);
			editingDomain.getCommandStack().execute(command);
		}

		PictogramElement pe = context.getPictogramElement();
		Object[] businessObjects = getFeatureProvider().getAllBusinessObjectsForPictogramElement(pe);

		preDelete(context);
		if (businessObjects != null) {
			for (Object bo : businessObjects) {
				if (bo instanceof EObject) {
					EcoreUtil.delete((EObject) bo, true);
				}
			}
		}
		postDelete(context);

		super.delete(context);
	}

	protected Connections<?> getModelConnections() {
		SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());
		if (sad != null) {
			return sad.getConnections();
		}
		DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
		if (dcd != null) {
			return dcd.getConnections();
		}
		return null;
	}

	protected FindBy getMatchingFindBy(ConnectInterface< ? , ? , ? > connection, FindByStub findByStub) {
		if (connection.getProvidesPort() != null) {
			FindBy findBy = connection.getProvidesPort().getFindBy();
			if (findBy != null && doFindByObjectsMatch(findBy, findByStub)) {
				return findBy;
			}
		}
		if (connection.getComponentSupportedInterface() != null) {
			FindBy findBy = connection.getComponentSupportedInterface().getFindBy();
			if (findBy != null && doFindByObjectsMatch(findBy, findByStub)) {
				return findBy;
			}
		}
		if (connection.getUsesPort() != null) {
			FindBy findBy = connection.getUsesPort().getFindBy();
			if (findBy != null && doFindByObjectsMatch(findBy, findByStub)) {
				return findBy;
			}
		}
		return null;
	}

	protected List< FindBy > getModelFindBys(FindByStub findByStub) {
		List< FindBy > findBys = new ArrayList< FindBy >();
		Connections< ? > connections = getModelConnections();
		if (connections != null) {
			for (ConnectInterface< ? , ? , ? > connection : connections.getConnectInterface()) {
				FindBy findBy = getMatchingFindBy(connection, findByStub);
				if (findBy != null) {
					findBys.add(findBy);
				}
			}
		}
		return findBys;
	}

	protected List< ConnectInterface< ? , ? , ? > > getFindByConnections(FindByStub findByToDelete) {
		List< ConnectInterface< ? , ? , ? > > connectionsToRemove = new ArrayList< ConnectInterface< ? , ? , ? > >();
		Connections< ? > connections = getModelConnections();
		if (connections != null) {
			// find and remove any attached connections
			// gather connections
			for (ConnectInterface< ? , ? , ? > connection : connections.getConnectInterface()) {
				if (getMatchingFindBy(connection, findByToDelete) != null) {
					connectionsToRemove.add(connection);
				}
			}
		}
		return connectionsToRemove;
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return super.checkValueValid(value, context);
	}

	/**
	 * Return all RHContainerShape in Diagram (recursively)
	 * @param containerShape
	 * @return
	 */
	public static List<RHContainerShape> getAllFindByShapes(ContainerShape containerShape) {
		List<RHContainerShape> children = new ArrayList<RHContainerShape>();
		if (containerShape instanceof RHContainerShape) {
			Object obj = DUtil.getBusinessObject(containerShape);
			if (obj != null && obj instanceof FindByStub) {
				children.add((RHContainerShape) containerShape);
			}
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllFindByShapes((ContainerShape) s));
				}
			}
		}
		return children;
	}

	/**
	 * Add UsesPortStub to FindByStub
	 * @param findByStub
	 * @param usesPort
	 * @param featureProvider
	 */

	public static void addUsesPortStubToFindByStub(final FindByStub findByStub, final UsesPort< ? > usesPortStub, IFeatureProvider featureProvider) {

		final String usesPortName = usesPortStub.getUsesIdentifier();

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// add uses port stub
				if (usesPortName != null && !usesPortName.isEmpty()) {
					UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
					usesPortStub.setName(usesPortName);
					findByStub.getUses().add(usesPortStub);
				}

			}
		});
	}

	/**
	 * Add ProvidesPortStub to FindByStub
	 * @param findByStub
	 * @param sadUsesPort
	 * @param featureProvider
	 */
	public static void addProvidesPortStubToFindByStub(final FindByStub findByStub, final ProvidesPort< ? > providesPortStub, IFeatureProvider featureProvider) {

		final String providesPortName = providesPortStub.getProvidesIdentifier();

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// add provides port stub
				if (providesPortName != null && !providesPortName.isEmpty()) {
					ProvidesPortStub providesPortStub = PartitioningFactory.eINSTANCE.createProvidesPortStub();
					providesPortStub.setName(providesPortName);
					findByStub.getProvides().add(providesPortStub);
				}

			}
		});
	}

	/**
	 * Return true if the FindBy and FindByStub match one another
	 * @param findBy
	 * @param findByStub
	 * @return
	 */
	public static boolean doFindByObjectsMatch(FindBy findBy, FindByStub findByStub) {

		// CORBA naming service
		if (findBy.getNamingService() != null && findBy.getNamingService().getName() != null && findByStub.getNamingService() != null
			&& findByStub.getNamingService().getName() != null && findBy.getNamingService().getName().equals(findByStub.getNamingService().getName())) {
			return true;
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.DOMAINMANAGER
			&& findByStub.getDomainFinder() != null && findByStub.getDomainFinder().getType() == DomainFinderType.DOMAINMANAGER) {
			// domain manager
			return true;
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.FILEMANAGER
			&& findByStub.getDomainFinder() != null && findByStub.getDomainFinder().getType() == DomainFinderType.FILEMANAGER) {
			// file manager
			return true;
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.EVENTCHANNEL
			&& findBy.getDomainFinder().getName() != null && findByStub.getDomainFinder() != null
			&& findByStub.getDomainFinder().getType() == DomainFinderType.EVENTCHANNEL && findByStub.getDomainFinder().getName() != null
			&& findBy.getDomainFinder().getName().equals(findByStub.getDomainFinder().getName())) {
			// event manager
			return true;
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.SERVICENAME
			&& findBy.getDomainFinder().getName() != null && findByStub.getDomainFinder() != null
			&& findByStub.getDomainFinder().getType() == DomainFinderType.SERVICENAME && findByStub.getDomainFinder().getName() != null
			&& findBy.getDomainFinder().getName().equals(findByStub.getDomainFinder().getName())) {
			// service name
			return true;
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.SERVICETYPE
			&& findBy.getDomainFinder().getName() != null && findByStub.getDomainFinder() != null
			&& findByStub.getDomainFinder().getType() == DomainFinderType.SERVICETYPE && findByStub.getDomainFinder().getName() != null
			&& findBy.getDomainFinder().getName().equals(findByStub.getDomainFinder().getName())) {
			// service type
			return true;
		}

		return false;
	}

	protected static Wizard getEditWizard() {
		return new Wizard() {
			public boolean performFinish() {
				return true;
			}
		};
	}

	@Override
	protected EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getUses();
		}
		return null;
	}

	@Override
	protected EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getProvides();
		}
		return null;
	}

	@Override
	protected ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getInterface();
		}
		return null;
	}

	@Override
	protected
	abstract String getOuterImageId();

	@Override
	protected String getInnerImageId() {
		return getCreateImageId();
	}

	@Override
	protected String getStyleForOuter() {
		return StyleUtil.OUTER_SHAPE;
	}

	@Override
	protected String getStyleForInner() {
		return StyleUtil.FIND_BY_INNER;
	}

	@Override
	protected List<EObject> getBusinessObjectsToLink(EObject obj) {
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		businessObjectsToLink.add(obj);
		return businessObjectsToLink;
	}

}
