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
import gov.redhawk.ide.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.dcd.DcdConnectInterface;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPort;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPort;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadConnectInterface;
import mil.jpeojtrs.sca.sad.SadUsesPort;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractFindByPattern extends AbstractContainerPattern implements IPattern {

	public AbstractFindByPattern() {
		super(null);
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	public abstract boolean isMainBusinessObjectApplicable(Object mainBusinessObject);

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
	public String getOuterTitle(EObject obj) {
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
	public String getInnerTitle(EObject obj) {
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

	/**
	 * Sets the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	public void setInnerTitle(FindByStub findByStub, String value) {
	}

	@Override
	public PictogramElement add(IAddContext context) {
		// create shape
		RHContainerShape rhContainerShape = RHGxFactory.eINSTANCE.createRHContainerShape();

		// initialize shape contents
		rhContainerShape.init(context, this);

		// set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(rhContainerShape.getGraphicsAlgorithm(), context.getX(), context.getY());

		// layout
		layoutPictogramElement(rhContainerShape);

		return rhContainerShape;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	@Override
	public abstract Object[] create(ICreateContext context);

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof FindByStub) {
			return true;
		}
		return false;
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {
		((RHContainerShape) context.getPictogramElement()).layout();

		// something is always changing.
		return true;
	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason updated = ((RHContainerShape) context.getPictogramElement()).update(context, this);

		// if we updated redraw
		if (updated.toBoolean()) {
			layoutPictogramElement(context.getPictogramElement());
		}

		return updated.toBoolean();
	}

	/**
	 * Determines whether we need to update the diagram from the model.
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return ((RHContainerShape) context.getPictogramElement()).updateNeeded(context, this);
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

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				if (sad != null) {
					// delete component from SoftwareAssembly
					deleteFindByConnections(findByToDelete, sad);
				} else if (dcd != null) {
					// delete component from DeviceConfiguration
					deleteFindByConnections(findByToDelete, dcd);
				}

			}
		});

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

	private void deleteFindByConnections(FindByStub findByToDelete, SoftwareAssembly sad) {
		// find and remove any attached connections
		// gather connections
		List<SadConnectInterface> connectionsToRemove = new ArrayList<SadConnectInterface>();
		if (sad.getConnections() == null) {
			return;
		}
		for (SadConnectInterface connection : sad.getConnections().getConnectInterface()) {
			if (connection.getProvidesPort() != null && connection.getProvidesPort().getFindBy() != null && connection.getProvidesPort().getFindBy().getNamingService() != null
				&& connection.getProvidesPort().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
				connectionsToRemove.add(connection);
			} else if (connection.getComponentSupportedInterface() != null && connection.getComponentSupportedInterface().getFindBy() != null
					&& connection.getComponentSupportedInterface().getFindBy().getNamingService() != null
					&& connection.getComponentSupportedInterface().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
					connectionsToRemove.add(connection);
			} else if (connection.getUsesPort().getFindBy() != null && connection.getUsesPort().getFindBy().getNamingService() != null
					&& connection.getUsesPort().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
				connectionsToRemove.add(connection);
			}
		}

		// remove gathered connections
		if (sad.getConnections() != null) {
			sad.getConnections().getConnectInterface().removeAll(connectionsToRemove);
		}
	}

	private void deleteFindByConnections(FindByStub findByToDelete, DeviceConfiguration dcd) {
		// find and remove any attached connections
		// gather connections
		List<DcdConnectInterface> connectionsToRemove = new ArrayList<DcdConnectInterface>();
		if (dcd.getConnections() == null) {
			return;
		}
		for (DcdConnectInterface connection : dcd.getConnections().getConnectInterface()) {
			if (connection.getProvidesPort() != null && connection.getProvidesPort().getFindBy() != null && connection.getProvidesPort().getFindBy().getNamingService() != null
				&& connection.getProvidesPort().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
				connectionsToRemove.add(connection);
			} else if (connection.getComponentSupportedInterface() != null && connection.getComponentSupportedInterface().getFindBy() != null
					&& connection.getComponentSupportedInterface().getFindBy().getNamingService() != null
					&& connection.getComponentSupportedInterface().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
					connectionsToRemove.add(connection);
			} else if (connection.getUsesPort().getFindBy() != null && connection.getUsesPort().getFindBy().getNamingService() != null
					&& connection.getUsesPort().getFindBy().getNamingService().getName().equals(findByToDelete.getNamingService().getName())) {
				connectionsToRemove.add(connection);
			}
		}

		// remove gathered connections
		if (dcd.getConnections() != null) {
			dcd.getConnections().getConnectInterface().removeAll(connectionsToRemove);
		}
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String checkValueValid(String value, IDirectEditingContext context) {
		return super.checkValueValid(value, context);
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		FindByStub findBy = (FindByStub) getBusinessObjectForPictogramElement(rhContainerShape);
		return getInnerTitle(findBy);
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final FindByStub findByStub = (FindByStub) getBusinessObjectForPictogramElement(rhContainerShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				setInnerTitle(findByStub, value);
			}
		});

		// perform update, redraw
		updatePictogramElement(rhContainerShape);
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

		final String usesPortName = usesPortStub.getUsesIndentifier();

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
	 * Add UsesPortStub to FindByStub
	 * @param findByStub
	 * @param usesPort
	 * @param featureProvider
	 */
	public static void addUsesPortStubToFindByStub2(final FindByStub findByStub, final SadUsesPort usesPort, IFeatureProvider featureProvider) {

		final String usesPortName = usesPort.getUsesIndentifier();

		// add uses port stub
		if (usesPortName != null && !usesPortName.isEmpty()) {
			UsesPortStub usesPortStub = PartitioningFactory.eINSTANCE.createUsesPortStub();
			usesPortStub.setName(usesPortName);
			findByStub.getUses().add(usesPortStub);
		}
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

	/**
	 * Returns the {@link Diagram} this pattern lives for.
	 * 
	 * @return The diagram
	 */
	public Diagram getDiagram() {
		return getFeatureProvider().getDiagramTypeProvider().getDiagram();
	}

	@Override
	public EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getUses();
		}
		return null;
	}

	@Override
	public EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getProvides();
		}
		return null;
	}

	@Override
	public ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof FindByStub) {
			return ((FindByStub) obj).getInterface();
		}
		return null;
	}

	@Override
	public abstract String getOuterImageId();

	@Override
	public String getInnerImageId() {
		return getCreateImageId();
	}

	@Override
	public Style createStyleForOuter() {
		return StyleUtil.createStyleForFindByOuter(getDiagram());
	}

	@Override
	public Style createStyleForInner() {
		return StyleUtil.createStyleForFindByInner(getDiagram());
	}

	@Override
	public List<EObject> getBusinessObjectsToLink(EObject obj) {
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		businessObjectsToLink.add(obj);
		return businessObjectsToLink;
	}
}
