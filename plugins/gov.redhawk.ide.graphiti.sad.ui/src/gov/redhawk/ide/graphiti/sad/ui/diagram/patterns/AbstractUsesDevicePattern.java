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
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.spd.UsesDevice;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
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
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractUsesDevicePattern extends AbstractContainerPattern implements IPattern {

	public AbstractUsesDevicePattern() {
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
		if (context.getNewObject() instanceof UsesDevice) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String getOuterTitle(EObject obj) {
		if (obj instanceof UsesDevice) {
			return getOuterTitle((UsesDevice) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the outer shape
	 * @param usesDevice
	 * @return
	 */
	public String getOuterTitle(UsesDevice usesDevice) {
		return getCreateName();
	}

	@Override
	public String getInnerTitle(EObject obj) {
		if (obj instanceof UsesDevice) {
			return getInnerTitle((UsesDevice) obj);
		}
		return null;
	}

	/**
	 * Provides the title of the inner shape
	 * @param usesDevice
	 * @return
	 */
	public abstract String getInnerTitle(UsesDevice usesDevice);

	/**
	 * Sets the title of the inner shape
	 * @param usesDevice
	 * @return
	 */
	public void setInnerTitle(UsesDevice usesDevice, String value) {
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
		if (obj instanceof UsesDevice) {
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
		if (obj instanceof UsesDevice) {
			return true;
		}
		return super.canDelete(context);
	}

	@Override
	public void delete(IDeleteContext context) {
//		// set Find By to delete
//		final FindByStub findByToDelete = (FindByStub) DUtil.getBusinessObject(context.getPictogramElement());
//
//		// get sad from diagram
//		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
//
//		final DeviceConfiguration dcd = DUtil.getDiagramDCD(getDiagram());
//
//		// editing domain for our transaction
//		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();
//
//		// Perform business object manipulation in a Command
//		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
//		stack.execute(new RecordingCommand(editingDomain) {
//			@Override
//			protected void doExecute() {
//
//				if (sad != null) {
//					// delete component from SoftwareAssembly
//					deleteFindByConnections(findByToDelete, sad);
//				} else if (dcd != null) {
//					// delete component from DeviceConfiguration
//					deleteFindByConnections(findByToDelete, dcd);
//				}
//
//			}
//		});
//
//		PictogramElement pe = context.getPictogramElement();
//		Object[] businessObjects = getFeatureProvider().getAllBusinessObjectsForPictogramElement(pe);
//
//		preDelete(context);
//		if (businessObjects != null) {
//			for (Object bo : businessObjects) {
//				if (bo instanceof EObject) {
//					EcoreUtil.delete((EObject) bo, true);
//				}
//			}
//		}
//		postDelete(context);
//
//		super.delete(context);
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
		UsesDevice usesDevice = (UsesDevice) getBusinessObjectForPictogramElement(rhContainerShape);
		return getInnerTitle(usesDevice);
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final UsesDevice usesDevice = (UsesDevice) getBusinessObjectForPictogramElement(rhContainerShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				setInnerTitle(usesDevice, value);
			}
		});

		// perform update, redraw
		updatePictogramElement(rhContainerShape);
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
	public abstract String getOuterImageId();

	@Override
	public String getInnerImageId() {
		return getCreateImageId();
	}

	@Override
	public Style createStyleForOuter() {
		return StyleUtil.createStyleForUsesDeviceOuter(getDiagram());
	}

	@Override
	public Style createStyleForInner() {
		return StyleUtil.createStyleForUsesDeviceInner(getDiagram());
	}

	@Override
	public List<EObject> getBusinessObjectsToLink(EObject obj) {
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		businessObjectsToLink.add(obj);
		return businessObjectsToLink;
	}
	
	/**
	 * Provides list of UsesPortStubs (if applicable)
	 * @param obj
	 * @return
	 */
	public EList<UsesPortStub> getUses(EObject obj) {
		//TODO: fix this.  problem is RHContainerSHape can't handle null list and we can't create Elist on our own
		return (EList<UsesPortStub>) new ArrayList<UsesPortStub>();
	}
	
	/**
	 * Provides list of ProvidesPortStubs (if applicable)
	 * @param obj
	 * @return
	 */
	public EList<ProvidesPortStub> getProvides(EObject obj) {
		//TODO: fix this.  problem is RHContainerSHape can't handle null list and we can't create Elist on our own
		return (EList<ProvidesPortStub>) new ArrayList<ProvidesPortStub>();
	}
}
