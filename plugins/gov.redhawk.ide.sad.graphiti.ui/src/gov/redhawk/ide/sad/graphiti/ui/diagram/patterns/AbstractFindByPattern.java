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
package gov.redhawk.ide.sad.graphiti.ui.diagram.patterns;

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.DomainFinderType;
import mil.jpeojtrs.sca.partitioning.FindBy;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.PartitioningFactory;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.SadProvidesPort;
import mil.jpeojtrs.sca.sad.SadUsesPort;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.AbstractPattern;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public abstract class AbstractFindByPattern extends AbstractPattern implements IPattern {

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

	/**
	 * Provides the title of the outer shape
	 * @param findByStub
	 * @return
	 */
	public String getOuterTitle(FindByStub findByStub) {
		return getCreateName();
	}

	/**
	 * Provides the title of the inner shape
	 * @param findByStub
	 * @return
	 */
	public abstract String getInnerTitle(FindByStub findByStub);

	@Override
	public PictogramElement add(IAddContext context) {
		FindByStub findByStub = (FindByStub) context.getNewObject();
		ContainerShape targetContainerShape = (ContainerShape) context.getTargetContainer();
		Diagram diagram = (Diagram) context.getTargetContainer();

		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);

		// create shape
		RHContainerShape rhContainerShape = RHGxFactory.eINSTANCE.createRHContainerShape();

		// initialize shape contents
		rhContainerShape.init(targetContainerShape, outerTitle, Arrays.asList((EObject) findByStub), getFeatureProvider(), ImageProvider.IMG_FIND_BY,
//				(EObject)findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
			StyleUtil.getStyleForFindByOuter(diagram), innerTitle, getCreateImageId(), StyleUtil.getStyleForFindByInner(diagram), findByStub.getInterface(),
			findByStub.getUses(), findByStub.getProvides(), null);

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

		// business object
		FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(context.getPictogramElement());

		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);

		Reason updated = ((RHContainerShape) context.getPictogramElement()).update(outerTitle, findByStub, getFeatureProvider(), ImageProvider.IMG_FIND_BY,
			StyleUtil.getStyleForFindByOuter(getDiagram()), innerTitle, getCreateImageId(), StyleUtil.getStyleForFindByInner(getDiagram()),
			findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);

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

		// business object
		FindByStub findByStub = (FindByStub) DUtil.getBusinessObject(context.getPictogramElement());

		String outerTitle = getOuterTitle(findByStub);
		String innerTitle = getInnerTitle(findByStub);

		Reason requiresUpdate = ((RHContainerShape) context.getPictogramElement()).updateNeeded(outerTitle, findByStub, getFeatureProvider(),
			ImageProvider.IMG_FIND_BY, StyleUtil.getStyleForFindByOuter(getDiagram()), innerTitle, getCreateImageId(),
			StyleUtil.getStyleForFindByInner(getDiagram()), findByStub.getInterface(), findByStub.getUses(), findByStub.getProvides(), null);

		return requiresUpdate;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		Object obj = getBusinessObjectForPictogramElement(rhContainerShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();
		// allow if we've selected Text for the FindByStub
		if (obj instanceof FindByStub && ga instanceof Text) {
			return true;
		}
		return false;
	}

	@Override
	public int getEditingType() {
		return TYPE_TEXT;
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		RHContainerShape rhContainerShape = (RHContainerShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_outerContainerShape);
		FindByStub findBy = (FindByStub) getBusinessObjectForPictogramElement(rhContainerShape);
		return getInnerTitle(findBy);
	}

	@Override
	public abstract String checkValueValid(String value, IDirectEditingContext context);

	@Override
	public abstract void setValue(final String value, IDirectEditingContext context);

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
	 * Create FindByStub in the diagram based on values in findBy
	 * @param findBy
	 * @param featureProvider
	 * @param diagram
	 * @return
	 */
	public static FindByStub createFindByStub(FindBy findBy, IFeatureProvider featureProvider, Diagram diagram) {

		// CORBA naming service
		if (findBy.getNamingService() != null && findBy.getNamingService().getName() != null) {
			return FindByCORBANamePattern.create(findBy.getNamingService().getName(), featureProvider, diagram);
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.DOMAINMANAGER) {
			// domain manager
			return FindByDomainManagerPattern.create(featureProvider, diagram);
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.FILEMANAGER) {
			// file manager
			return FindByFileManagerPattern.create(featureProvider, diagram);
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.EVENTCHANNEL
			&& findBy.getDomainFinder().getName() != null) {
			// event manager
			return FindByEventChannelPattern.create(findBy.getDomainFinder().getName(), featureProvider, diagram);
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.SERVICENAME
			&& findBy.getDomainFinder().getName() != null) {
			// service name
			return FindByServicePattern.create(findBy.getDomainFinder().getName(), null, featureProvider, diagram);
		} else if (findBy.getDomainFinder() != null && findBy.getDomainFinder().getType() == DomainFinderType.SERVICETYPE
			&& findBy.getDomainFinder().getName() != null) {
			// service type
			return FindByServicePattern.create(null, findBy.getDomainFinder().getName(), featureProvider, diagram);
		}

		return null;

	}

	/**
	 * Add UsesPortStub to FindByStub
	 * @param findByStub
	 * @param usesPort
	 * @param featureProvider
	 */
<<<<<<< HEAD
	public static void addUsesPortStubToFindByStub(final FindByStub findByStub, final SadUsesPort usesPortStub, IFeatureProvider featureProvider) {

		final String usesPortName = usesPortStub.getUsesIndentifier();

		// editing domain for our transaction
=======
	public static void addUsesPortStubToFindByStub(final FindByStub findByStub, final SadUsesPort usesPort, IFeatureProvider featureProvider) {
	    
		final String usesPortName = usesPort.getUsesIndentifier();
		
		//editing domain for our transaction
>>>>>>> Draw appropriate FindBy shapes when loading populated SAD model file
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramEditor().getEditingDomain();

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
		
		//add uses port stub
		if(usesPortName != null && !usesPortName.isEmpty()){
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
	public static void addProvidesPortStubToFindByStub(final FindByStub findByStub, final SadProvidesPort sadProvidesPort, IFeatureProvider featureProvider) {

		final String providesPortName = sadProvidesPort.getProvidesIdentifier();

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramEditor().getEditingDomain();

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

}
