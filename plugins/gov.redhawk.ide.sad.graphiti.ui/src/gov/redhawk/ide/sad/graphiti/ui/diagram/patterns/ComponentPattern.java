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

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxFactory;
import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiationRef;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.features.context.IMoveShapeContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;

public class ComponentPattern extends AbstractContainerPattern implements IPattern {

	private URI spdUri = null;

	public ComponentPattern() {
		super(null);
	}

	public URI getSpdUri() {
		return spdUri;
	}

	public void setSpdUri(URI spdUri) {
		this.spdUri = spdUri;
	}

	@Override
	public String getCreateName() {
		return "Component";
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		return mainBusinessObject instanceof SadComponentInstantiation;
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

	// DIAGRAM FEATURES

	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof SadComponentInstantiation) {
			if (context.getTargetContainer() instanceof Diagram || DUtil.getHostCollocation(context.getTargetContainer()) != null) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}

	/**
	 * Return true if the user has selected a pictogram element that is linked with
	 * a SADComponentInstantiation instance
	 */
	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		if (obj instanceof SadComponentInstantiation) {
			return true;
		}
		return false;
	}

	/**
	 * Delete the SadComponentInstantiation linked to the PictogramElement.
	 */
	@Override
	public void delete(IDeleteContext context) {

		// set componentToDelete
		final SadComponentInstantiation ciToDelete = (SadComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());

		final Diagram diagram = DUtil.findDiagram((ContainerShape) context.getPictogramElement());

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// delete component from SoftwareAssembly
				DUtil.deleteComponentInstantiation(ciToDelete, sad);

				// re-organize start order
				organizeStartOrder(sad, diagram, getFeatureProvider());

			}
		});

		// delete graphical component
		IRemoveContext rc = new RemoveContext(context.getPictogramElement());
		IFeatureProvider featureProvider = getFeatureProvider();
		IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
		if (removeFeature != null) {
			removeFeature.remove(rc);
		}

		// redraw start order
		// DUtil.organizeDiagramStartOrder(diagram);
	}

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		return true;
	}

	/**
	 * Adds a Component to the diagram. Immediately calls resize at the end to keep sizing and location in one place.
	 */
	public PictogramElement add(IAddContext context) {

		// create shape
		ComponentShape componentShape = RHGxFactory.eINSTANCE.createComponentShape();
		componentShape.init(context, this);
		// set shape location to user's selection
		Graphiti.getGaLayoutService().setLocation(componentShape.getGraphicsAlgorithm(), context.getX(), context.getY());

		// TODO: should we handle this differently?
		// pre-add a few styles that will be used for updates. This is necessary because if the style isn't present
		// during updateNeeded (Not a Transaction)
		// it will try to create it and an exception will occur
		StyleUtil.getStyleForExternalUsesPort(getDiagram());
		StyleUtil.getStyleForExternalProvidesPort(getDiagram());
		StyleUtil.getStyleForUsesPort(getDiagram());
		StyleUtil.getStyleForProvidesPort(getDiagram());
		StyleUtil.getStyleForStartOrderEllipse(getDiagram());

		// layout
		layoutPictogramElement(componentShape);

		return componentShape;

	}

	@Override
	public boolean canLayout(ILayoutContext context) {
		ContainerShape containerShape = (ContainerShape) context.getPictogramElement();
		Object obj = DUtil.getBusinessObject(containerShape);
		if (obj instanceof SadComponentInstantiation) {
			return true;
		}
		return false;
	}

	/**
	 * Layout children of component
	 */
	@Override
	public boolean layout(ILayoutContext context) {

		((ComponentShape) context.getPictogramElement()).layout();

		// something is always changing.
		return true;
	}

	public boolean canMoveShape(IMoveShapeContext context) {

		SadComponentInstantiation sadComponentInstantiation = (SadComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());
		if (sadComponentInstantiation == null) {
			return false;
		}

		// if moving to HostCollocation to Sad Partitioning
		if (context.getTargetContainer() instanceof Diagram || DUtil.getHostCollocation(context.getTargetContainer()) != null) {
			return true;
		}
		return false;

	}

	/**
	 * Moves Component shape.
	 * if moving to HostCollocation or away from one modify underlying model and allow parent class to perform graphical
	 * move
	 * if moving within the same container allow parent class to perform graphical move
	 */
	public void moveShape(IMoveShapeContext context) {
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());

		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// if moving inside the same container
		if (context.getSourceContainer() == context.getTargetContainer()) {
			super.moveShape(context);
		}

		// if moving from HostCollocation to a different HostCollocation
		if (DUtil.getHostCollocation(context.getSourceContainer()) != null && DUtil.getHostCollocation(context.getTargetContainer()) != null
			&& DUtil.getHostCollocation(context.getSourceContainer()) != DUtil.getHostCollocation(context.getTargetContainer())) {
			// swap parents
			DUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement) ci.getPlacement());
			DUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
			super.moveShape(context);
		}

		// if moving to HostCollocation from Sad Partitioning
		if (DUtil.getHostCollocation(context.getTargetContainer()) != null && context.getSourceContainer() instanceof Diagram) {
			// swap parents
			sad.getPartitioning().getComponentPlacement().remove(ci.getPlacement());
			DUtil.getHostCollocation(context.getTargetContainer()).getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
			super.moveShape(context);
		}

		// if moving to Sad Partitioning from HostCollocation
		if (DUtil.getHostCollocation(context.getSourceContainer()) != null && context.getTargetContainer() instanceof Diagram) {
			// swap parents
			sad.getPartitioning().getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
			DUtil.getHostCollocation(context.getSourceContainer()).getComponentPlacement().remove((SadComponentPlacement) ci.getPlacement());
			super.moveShape(context);
		}

	}

	/**
	 * Return the highest start order for all components in the SAD.
	 * Returns null if no components are found
	 * @param sad
	 * @return
	 */
	public static BigInteger determineHighestStartOrder(final SoftwareAssembly sad) {

		BigInteger highestStartOrder = null;
		List<SadComponentInstantiation> cis = getAllComponents(sad);
		if (cis != null && cis.size() > 0) {
			highestStartOrder = cis.get(0).getStartOrder();

		}
		for (int i = 1; i < cis.size(); i++) {
			SadComponentInstantiation c = cis.get(i);
			
			// If a component is found, and it's start order is null, assume it is the assembly controller
			// Assembly controllers should always be at the beginning of the start order, so mark highest start order as zero
			if (highestStartOrder == null) {
				highestStartOrder = BigInteger.ZERO;
			}

			// check for higher start order
			if (c.getStartOrder() != null && c.getStartOrder().compareTo(highestStartOrder) >= 0) {
				highestStartOrder = c.getStartOrder();
			}
		}
		
		// If there are no components, highestStartOrder will be null
		return highestStartOrder;
	}

	/**
	 * Get all components in sad
	 * @param sad
	 * @return
	 */
	public static List<SadComponentInstantiation> getAllComponents(final SoftwareAssembly sad) {
		final List<SadComponentInstantiation> retVal = new ArrayList<SadComponentInstantiation>();
		if (sad.getPartitioning() != null) {
			for (final SadComponentPlacement cp : sad.getPartitioning().getComponentPlacement()) {
				retVal.addAll(cp.getComponentInstantiation());
			}
			for (final HostCollocation h : sad.getPartitioning().getHostCollocation()) {
				for (final SadComponentPlacement cp : h.getComponentPlacement()) {
					retVal.addAll(cp.getComponentInstantiation());
				}
			}
		}

		return retVal;
	}

	/**
	 *  swap start order of provided components. Change assembly controller if start order zero
	 * @param sad
	 * @param featureProvider
	 * @param lowerCi - The component that currently has the lower start order
	 * @param higherCi - The component that currently has the higher start order
	 */
	public static void swapStartOrder(SoftwareAssembly sad, IFeatureProvider featureProvider, final SadComponentInstantiation lowerCi, final SadComponentInstantiation higherCi) {

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = featureProvider.getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get AssemblyController
		final AssemblyController assemblyController = sad.getAssemblyController();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// Increment start order
				lowerCi.setStartOrder(higherCi.getStartOrder());
				// Decrement start order
				higherCi.setStartOrder(higherCi.getStartOrder().subtract(BigInteger.ONE));

				// set assembly controller if start order is zero
				if (lowerCi.getStartOrder().compareTo(BigInteger.ZERO) == 0) {
					assemblyController.getComponentInstantiationRef().setInstantiation(lowerCi);
				} else if (higherCi.getStartOrder().compareTo(BigInteger.ZERO) == 0) {
					assemblyController.getComponentInstantiationRef().setInstantiation(higherCi);
				}
			}
		});
	}

	/*
	 *  returns component instantiation with provided start order
	 */
	public static SadComponentInstantiation getComponentInstantiationViaStartOrder(final SoftwareAssembly sad, final BigInteger startOrder) {
		for (SadComponentInstantiation ci : sad.getAllComponentInstantiations()) {
			if (ci.getStartOrder() != null && ci.getStartOrder().compareTo(startOrder) == 0) {
				return ci;
			}
		}
		return null;
	}

	// adjust the start order for a component
	public static void organizeStartOrder(final SoftwareAssembly sad, final Diagram diagram, final IFeatureProvider featureProvider) {
		BigInteger startOrder = BigInteger.ZERO;

		// get components by start order
		EList<SadComponentInstantiation> componentInstantiationsInStartOrder = sad.getComponentInstantiationsInStartOrder();

		// if assembly controller was deleted, set a new assembly controller
		AssemblyController assemblyController = getAssemblyController(featureProvider, diagram);
		if (assemblyController == null && componentInstantiationsInStartOrder.size() > 0) {
			// assign assembly controller assign to first component
			assemblyController = SadFactory.eINSTANCE.createAssemblyController();
			SadComponentInstantiation ci = componentInstantiationsInStartOrder.get(0);
			SadComponentInstantiationRef sadComponentInstantiationRef = SadFactory.eINSTANCE.createSadComponentInstantiationRef();
			sadComponentInstantiationRef.setInstantiation(ci);
			assemblyController.setComponentInstantiationRef(sadComponentInstantiationRef);
			sad.setAssemblyController(assemblyController);
			
			// If the component has a start order defined, update it to run first
			if (ci.getStartOrder() != null) {
				ci.setStartOrder(BigInteger.ZERO);
			}

		}

		// remove assembly controller from list, it has already been updated
		componentInstantiationsInStartOrder.remove(assemblyController.getComponentInstantiationRef().getInstantiation());
		
		// set start order
		for (SadComponentInstantiation ci : componentInstantiationsInStartOrder) { 
			// Don't update start order if it has not already been declared for this component
			if (ci.getStartOrder() != null) {
				startOrder = startOrder.add(BigInteger.ONE);
				ci.setStartOrder(startOrder);
			} 

			// get external ports
			// TODO: This breaks the assembly update logic, can end up changing start order ellipse style for non-assembly controllers
//			ExternalPorts externalPorts = DUtil.getDiagramSAD(featureProvider, diagram).getExternalPorts();
//
//			List<PictogramElement> elements = Graphiti.getLinkService().getPictogramElements(diagram, ci);
//			for (PictogramElement e : elements) {
//				if (e instanceof ComponentShape) {
//					((ComponentShape) e).update(ci, featureProvider, externalPorts, assemblyController);
//				}
//			}
		}
	}

//	@Override
//	public boolean canUpdate(IUpdateContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		return isPatternControlled(pictogramElement);
//	}

	// returns the assembly controller for this waveform
	private static AssemblyController getAssemblyController(IFeatureProvider featureProvider, Diagram diagram) {
		final SoftwareAssembly sad = DUtil.getDiagramSAD(featureProvider, diagram);
		if (sad.getAssemblyController() != null && sad.getAssemblyController().getComponentInstantiationRef() != null) {
			return sad.getAssemblyController();
		}
		return null;

	}

//	@Override
//	public boolean canUpdate(IUpdateContext context) {
//		PictogramElement pictogramElement = context.getPictogramElement();
//		return isPatternControlled(pictogramElement);
//	}

	// returns the assembly controller for this waveform if it happens to be the passed in Component
	public AssemblyController getComponentAssemblyController(SadComponentInstantiation ci) {
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());
		if (sad.getAssemblyController() != null && sad.getAssemblyController().getComponentInstantiationRef() != null
			&& sad.getAssemblyController().getComponentInstantiationRef().getRefid().equals(ci.getId())) {
			return sad.getAssemblyController();
		}
		return null;

	}

	@Override
	public boolean update(IUpdateContext context) {
		Reason updated = ((ComponentShape) context.getPictogramElement()).update(context, this);

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
		return ((ComponentShape) context.getPictogramElement()).updateNeeded(context, this);
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		Object obj = getBusinessObjectForPictogramElement(componentShape);
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof SadComponentInstantiation && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(RHContainerShapeImpl.GA_INNER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
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
		ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		SadComponentInstantiation ci = (SadComponentInstantiation) getBusinessObjectForPictogramElement(componentShape);
		return ci.getUsageName();
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		PictogramElement pe = context.getPictogramElement();
		ComponentShape componentShape = (ComponentShape) DUtil.findContainerShapeParentWithProperty(pe, RHContainerShapeImpl.SHAPE_OUTER_CONTAINER);
		final SadComponentInstantiation ci = (SadComponentInstantiation) getBusinessObjectForPictogramElement(componentShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				ci.setUsageName(value);
			}
		});

		// perform update, redraw
		updatePictogramElement(componentShape);
	}

	/**
	 * Return all ComponentShape in Diagram (recursively)
	 * @param containerShape
	 * @return
	 */
	public static List<ComponentShape> getAllComponentShapes(ContainerShape containerShape) {
		List<ComponentShape> children = new ArrayList<ComponentShape>();
		if (containerShape instanceof ComponentShape) {
			children.add((ComponentShape) containerShape);
		} else {
			for (Shape s : containerShape.getChildren()) {
				if (s instanceof ContainerShape) {
					children.addAll(getAllComponentShapes((ContainerShape) s));
				}
			}
		}
		return children;
	}

	
	@Override
	public String getOuterTitle(EObject obj) {
		if (obj instanceof SadComponentInstantiation) {
			return getOuterTitle((SadComponentInstantiation) obj);
		}
		return null;
	}

	@Override
	public String getInnerTitle(EObject obj) {
		if (obj instanceof SadComponentInstantiation) {
			return getInnerTitle((SadComponentInstantiation) obj);
		}
		return null;
	}
	
	/**
	 * Provides the title of the outer shape
	 * @param ci
	 * @return
	 */
	public String getOuterTitle(SadComponentInstantiation ci) {
		return ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName();
	}
	
	/**
	 * Provides the title of the inner shape
	 * @param ci
	 * @return
	 */
	public String getInnerTitle(SadComponentInstantiation ci) {
		return ci.getUsageName();
	}

	@Override
	public EList<UsesPortStub> getUses(EObject obj) {
		if (obj instanceof SadComponentInstantiation) {
			return ((SadComponentInstantiation) obj).getUses();
		}
		return null;
	}

	@Override
	public EList<ProvidesPortStub> getProvides(EObject obj) {
		if (obj instanceof SadComponentInstantiation) {
			return ((SadComponentInstantiation) obj).getProvides();
		}
		return null;
	}

	@Override
	public ComponentSupportedInterfaceStub getInterface(EObject obj) {
		if (obj instanceof SadComponentInstantiation) {
			return ((SadComponentInstantiation) obj).getInterfaceStub();
		}
		return null;
	}

	@Override
	public String getOuterImageId() {
		return ImageProvider.IMG_COMPONENT_PLACEMENT;
	}

	@Override
	public String getInnerImageId() {
		return ImageProvider.IMG_COMPONENT_INSTANCE;
	}

	@Override
	public Style getStyleForOuter() {
		return StyleUtil.getStyleForComponentOuter(getDiagram());
	}

	@Override
	public Style getStyleForInner() {
		return StyleUtil.getStyleForComponentInner(getDiagram());
	}
}
