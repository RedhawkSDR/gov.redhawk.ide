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

import gov.redhawk.ide.graphiti.sad.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.WaveformImageProvider;
import gov.redhawk.ide.graphiti.ui.diagram.features.update.UpdateAction;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.UpdateUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.func.IDirectEditing;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;
import org.eclipse.graphiti.ui.services.GraphitiUi;

public class HostCollocationPattern extends AbstractContainerPattern {

	public static final String NAME = "Host Collocation";

	// Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove
	// etc.)
	public static final String HOST_COLLOCATION_OUTER_CONTAINER_SHAPE = "hostCollocationOuterContainerShape";

	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_OUTER_ROUNDED_RECTANGLE = "outerRoundedRectangle";
	public static final String GA_OUTER_ROUNDED_RECTANGLE_TEXT = "outerRoundedRectangleText";
	public static final String GA_OUTER_ROUNDED_RECTANGLE_IMAGE = "outerRoundedRectangleImage";

	private static final int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	private static final int PROVIDES_PORTS_LEFT_PADDING = 5;
	private static final int INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING;
	private static final int ICON_IMAGE_LENGTH = 16;

	public HostCollocationPattern() {
		super(null);
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
		return WaveformImageProvider.IMG_HOST_COLLOCATION;
	}

	// THE FOLLOWING THREE METHODS DETERMINE IF PATTERN IS APPLICABLE TO OBJECT
	@Override
	public boolean isMainBusinessObjectApplicable(Object mainBusinessObject) {
		if (mainBusinessObject instanceof HostCollocation) {
			return true;
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

	// DIAGRAM FEATURES
	@Override
	public boolean canAdd(IAddContext context) {
		if (context.getNewObject() instanceof HostCollocation) {
			if (context.getTargetContainer() instanceof Diagram) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Add HostCollocation shape, reparent components (from Diagram ContainerShape to new HostCollocation
	 * ContainerShape)
	 * if applicable
	 */
	@Override
	public PictogramElement add(IAddContext context) {
		HostCollocation hostCollocation = (HostCollocation) context.getNewObject();
		Diagram diagram = (Diagram) context.getTargetContainer();
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();

		// OUTER RECTANGLE
		ContainerShape outerContainerShape = addOuterRectangle(diagram, hostCollocation.getName(), hostCollocation, getFeatureProvider(), getCreateImageId(),
			StyleUtil.HOST_COLLOCATION);

		RoundedRectangle outerRoundedRectangle = null;
		Text outerRoundedRectangleText = null;
		Image outerRoundedRectangleImage = null;

		// find all of our diagram elements
		List<PropertyContainer> children = DUtil.collectPropertyContainerChildren(outerContainerShape);
		for (PropertyContainer pc : children) {
			if (DUtil.isPropertyElementType(pc, GA_OUTER_ROUNDED_RECTANGLE)) {
				outerRoundedRectangle = (RoundedRectangle) pc;
			} else if (DUtil.isPropertyElementType(pc, GA_OUTER_ROUNDED_RECTANGLE_TEXT)) {
				outerRoundedRectangleText = (Text) pc;
			} else if (DUtil.isPropertyElementType(pc, GA_OUTER_ROUNDED_RECTANGLE_IMAGE)) {
				outerRoundedRectangleImage = (Image) pc;
			}
		}

		// resize outerRoundedRectangle
		int minWidth = Math.max(context.getWidth(),  300);
		int minHeight = Math.max(context.getHeight(), 300);
		// outerRoundedRectangle
		gaLayoutService.setLocationAndSize(outerRoundedRectangle, context.getX(), context.getY(), minWidth, minHeight);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleText, HostCollocationPattern.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING
			+ HostCollocationPattern.ICON_IMAGE_LENGTH + 4, 0, minWidth
			- (HostCollocationPattern.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + HostCollocationPattern.ICON_IMAGE_LENGTH + 4), 20);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleImage, HostCollocationPattern.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0,
			HostCollocationPattern.ICON_IMAGE_LENGTH, HostCollocationPattern.ICON_IMAGE_LENGTH);

		// Add components inside host collocation model into the newly added shape
		updatePictogramElement(outerContainerShape);

		return outerContainerShape;
	}

	@Override
	public boolean canCreate(ICreateContext context) {
		return context.getTargetContainer() instanceof Diagram;
	}

	/**
	 * Create a new HostCollocation instance and given the area selected
	 * by user in the diagram move the components entirely
	 * contained in selection into HostCollocation
	 */
	@Override
	public Object[] create(ICreateContext context) {
		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		String hostCollocationName = getUniqueCollocationName(sad);
		final HostCollocation hostCollocation = SadFactory.eINSTANCE.createHostCollocation();
		hostCollocation.setName(hostCollocationName);

		ContainerShape collocationShape = (ContainerShape) add(new AddContext(context, hostCollocation));

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// find all SadComponentInstantiation
		List<Shape> containedShapes = DUtil.getContainersInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(), context.getY(),
			GA_OUTER_ROUNDED_RECTANGLE);
		final List<SadComponentInstantiation> sadComponentInstantiations = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : containedShapes) {
			EObject bo = (EObject) getBusinessObjectForPictogramElement(shape);
			if (bo instanceof SadComponentInstantiation) {
				sadComponentInstantiations.add((SadComponentInstantiation) bo);

				// Reparent shape and position inside of host collocation
				shape.setContainer(collocationShape);
				int newX = shape.getGraphicsAlgorithm().getX() - context.getX();
				int newY = shape.getGraphicsAlgorithm().getY() - context.getY();
				Graphiti.getGaLayoutService().setLocation(shape.getGraphicsAlgorithm(), newX, newY);
			}
		}

		// Create Component Related objects in SAD model
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// move components to hostCollocation
				// remove from sad partitioning
				sad.getPartitioning().getComponentPlacement().removeAll(sadComponentInstantiations);
				// add to hostCollocation
				for (SadComponentInstantiation ci : sadComponentInstantiations) {
					hostCollocation.getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
				}

				// add to sad partitioning
				sad.getPartitioning().getHostCollocation().add(hostCollocation);
			}
		});

		return new Object[] { hostCollocation };
	}

	/**
	 * Resizing a Component shape is always allowed
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		// If a FindBy object would be wrapped in the Host Collocation, then cancel the operation
		List<Shape> shapesToAddToHostCollocation = DUtil.getContainersInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(),
			context.getY(), GA_OUTER_ROUNDED_RECTANGLE);
		for (Shape shape : shapesToAddToHostCollocation) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof FindByStub || obj instanceof UsesDeviceStub) {
					return false;
				}
			}
		}

		// ...otherwise, allow the resize
		return true;
	}

	/**
	 * Resize the host collocation shape. When expanded if area consumes components,
	 * those components should be added into host collocation. When reduced those components
	 * that are no longer in the area should be moved to the sad partition. {@link IResizeShapeContext} . Corresponds to
	 * the method {@link DefaultResizeShapeFeature#resizeShape(IResizeShapeContext)}.
	 * 
	 * @param context
	 * The context holding information on the domain object to be
	 * resized.
	 */
	@Override
	public void resizeShape(IResizeShapeContext context) {
		ContainerShape containerShape = (ContainerShape) context.getShape();
		int x = context.getX();
		int y = context.getY();
		int width = context.getWidth();
		int height = context.getHeight();

		// set hostCollocationToDelete
		final HostCollocation hostCollocation = (HostCollocation) DUtil.getBusinessObject(context.getPictogramElement());

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// find all components to remove (no longer inside the host collocation box, minimized)
		List<Shape> shapesToRemoveFromHostCollocation = DUtil.getContainersOutsideArea(containerShape, context.getWidth(), context.getHeight(), context.getX(),
			context.getY(), GA_OUTER_ROUNDED_RECTANGLE);
		final List<SadComponentInstantiation> ciToRemove = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : shapesToRemoveFromHostCollocation) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					ciToRemove.add((SadComponentInstantiation) obj);
				}
			}
		}

		// find all components to add to add (now inside host collocation, expanded)
		List<Shape> shapesToAddToHostCollocation = DUtil.getContainersInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(),
			context.getY(), GA_OUTER_ROUNDED_RECTANGLE);
		final List<SadComponentInstantiation> ciToAdd = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : shapesToAddToHostCollocation) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					ciToAdd.add((SadComponentInstantiation) obj);
				}
			}
		}

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// move components from host collocation to diagram
				for (SadComponentInstantiation ci : ciToRemove) {
					sad.getPartitioning().getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
					hostCollocation.getComponentPlacement().remove((SadComponentPlacement) ci.getPlacement());
				}

				// move components from diagram to host collocation
				for (SadComponentInstantiation ci : ciToAdd) {
					hostCollocation.getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
					sad.getPartitioning().getComponentPlacement().remove((SadComponentPlacement) ci.getPlacement());
				}
			}
		});

		// move shapes to diagram from host collocation
		for (Shape s : shapesToRemoveFromHostCollocation) {
			Object obj = DUtil.getBusinessObject(s);
			if (obj instanceof SadComponentInstantiation) {
				// reparent
				s.setContainer(getDiagram());
				// reposition shape outside host shape
				int newX = s.getGraphicsAlgorithm().getX() + containerShape.getGraphicsAlgorithm().getX();
				int newY = s.getGraphicsAlgorithm().getY() + containerShape.getGraphicsAlgorithm().getY();
				Graphiti.getGaService().setLocation(s.getGraphicsAlgorithm(), newX, newY);
			}
		}

		// move shapes from diagram to host collocation
		for (Shape s : shapesToAddToHostCollocation) {
			Object obj = DUtil.getBusinessObject(s);
			if (obj instanceof SadComponentInstantiation) {
				// reparent
				s.setContainer(containerShape);
				// reposition shape inside host shape (this is necessary as a result of the
				// shiftChildrenRelativeToParentResize method that is below)
				int newX = 0;
				if (context.getDirection() == IResizeShapeContext.DIRECTION_EAST || context.getDirection() == IResizeShapeContext.DIRECTION_SOUTH_EAST
					|| context.getDirection() == IResizeShapeContext.DIRECTION_NORTH_EAST) {
					newX = s.getGraphicsAlgorithm().getX() - x;
				} else {
					newX = s.getGraphicsAlgorithm().getX() - x + (containerShape.getGraphicsAlgorithm().getWidth() - context.getWidth());
				}
				int newY = 0;
				if (context.getDirection() == IResizeShapeContext.DIRECTION_NORTH || context.getDirection() == IResizeShapeContext.DIRECTION_NORTH_EAST
					|| context.getDirection() == IResizeShapeContext.DIRECTION_NORTH_WEST) {
					newY = s.getGraphicsAlgorithm().getY() - y + (containerShape.getGraphicsAlgorithm().getHeight() - context.getHeight());
				} else {
					newY = s.getGraphicsAlgorithm().getY() - y;
				}
				Graphiti.getGaService().setLocation(s.getGraphicsAlgorithm(), newX, newY);
			}
		}

		// adjust children x/y so they remain in the same relative position after resize
		DUtil.shiftChildrenRelativeToParentResize(containerShape, context);

		// perform resize
		if (containerShape.getGraphicsAlgorithm() != null) {
			Graphiti.getGaService().setLocationAndSize(containerShape.getGraphicsAlgorithm(), x, y, width, height);
		}

		layoutPictogramElement(containerShape);
	}

	/**
	 * Never enable remove on its own
	 */
	@Override
	public boolean canRemove(IRemoveContext context) {
		return false;
	}

	/**
	 * Return true if the user has selected a pictogram element that is linked with
	 * a HostCollocation instance
	 */
	@Override
	public boolean canDelete(IDeleteContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		if (obj instanceof HostCollocation) {
			return true;
		}
		return false;
	}

	/**
	 * Delete the SadComponentInstantiation linked to the PictogramElement.
	 */
	@Override
	public void delete(IDeleteContext context) {

		// set hostCollocationToDelete
		final HostCollocation hostCollocationToDelete = (HostCollocation) DUtil.getBusinessObject(context.getPictogramElement());
		final ContainerShape hostCollocationShape = (ContainerShape) context.getPictogramElement();

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// null checks
				if (sad.getPartitioning() == null || sad.getPartitioning().getHostCollocation() == null) {
					return;
				}

				// move components from host collocation to diagram (don't delete them)
				if (hostCollocationToDelete.getComponentPlacement() != null) {
					// move on diagram
					for (Shape s : GraphitiUi.getPeService().getAllContainedShapes(hostCollocationShape)) {
						if (s instanceof ComponentShape) {
							final ComponentShape c = (ComponentShape) s;
							MoveShapeContext moveContext = new MoveShapeContext(c);
							moveContext.setSourceContainer(hostCollocationShape);
							moveContext.setTargetContainer(getDiagram());
							ILocation cLocation = GraphitiUi.getUiLayoutService().getLocationRelativeToDiagram(c);
							moveContext.setLocation(cLocation.getX(), cLocation.getY());
							IMoveShapeFeature moveShapeFeature = getFeatureProvider().getMoveShapeFeature(moveContext);
							if (moveShapeFeature.canMoveShape(moveContext)) {
								moveShapeFeature.moveShape(moveContext);
							}
						}
					}
				}

				// remove host collocation
				if (sad.getPartitioning() != null && sad.getPartitioning().getHostCollocation() != null) {
					sad.getPartitioning().getHostCollocation().remove(hostCollocationToDelete);
				}
			}
		});

		// delete the graphical component
		IRemoveContext rc = new RemoveContext(context.getPictogramElement());
		IFeatureProvider featureProvider = getFeatureProvider();
		IRemoveFeature removeFeature = featureProvider.getRemoveFeature(rc);
		if (removeFeature != null) {
			removeFeature.remove(rc);
		}
	}

	/**
	 * Creates a large rectangle intended to be an outside container and links the provided business object to it.
	 * @param targetContainerShape
	 * @param text
	 * @param businessObject
	 * @param featureProvider
	 * @return
	 */
	private ContainerShape addOuterRectangle(ContainerShape targetContainerShape, String text, Object businessObject, IFeatureProvider featureProvider,
		String imageId, String styleId) {
		ContainerShape outerContainerShape = Graphiti.getCreateService().createContainerShape(targetContainerShape, true);
		Graphiti.getPeService().setPropertyValue(outerContainerShape, DUtil.SHAPE_TYPE, HOST_COLLOCATION_OUTER_CONTAINER_SHAPE);
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(outerContainerShape, 5, 5);
		StyleUtil.setStyle(outerRoundedRectangle, styleId);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE);
		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_IMAGE); // ref helps
		// with
		// resize
		// text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, text);
		StyleUtil.setStyle(cText, StyleUtil.OUTER_TEXT);
		Graphiti.getPeService().setPropertyValue(cText, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_TEXT);
		featureProvider.link(outerContainerShape, businessObject); // link container and business object

		return outerContainerShape;
	}

	/**
	 * Determines whether we need to update the diagram from the model.
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		ContainerShape collocationShape = (ContainerShape) context.getPictogramElement();
		HostCollocation collocation = (HostCollocation) getBusinessObjectForPictogramElement(collocationShape);

		if (UpdateUtil.updateNeeded(getOuterText(collocationShape), collocation.getName())) {
			return Reason.createTrueReason("Need to update name");
		}

		List<SadComponentInstantiation> expectedComponents = getComponentInstantiations(collocation);
		Map<EObject,UpdateAction> actions = getChildrenToUpdate(collocationShape, expectedComponents);
		if (!actions.isEmpty()) {
			return Reason.createTrueReason("Need to update component shape(s)");
		}

		return Reason.createFalseReason();
	}

	protected Text getOuterText(ContainerShape containerShape) {
		return (Text) DUtil.findChildGraphicsAlgorithmByProperty(containerShape.getGraphicsAlgorithm(), DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Updates the host collocation if needed
	 */
	@Override
	public boolean update(IUpdateContext context) {
		ContainerShape collocationShape = (ContainerShape) context.getPictogramElement();
		HostCollocation collocation = (HostCollocation) getBusinessObjectForPictogramElement(collocationShape);

		// Update collocation name
		boolean updatePerformed = UpdateUtil.update(getOuterText(collocationShape), collocation.getName());

		List<SadComponentInstantiation> expectedComponents = getComponentInstantiations(collocation);
		Map<EObject,UpdateAction> actions = getChildrenToUpdate(collocationShape, expectedComponents);
		updateChildren(collocationShape, actions);

		if (updatePerformed || !actions.isEmpty()) {
			layoutPictogramElement(collocationShape);
			return true;
		}
		return false;
	}

	protected List<SadComponentInstantiation> getComponentInstantiations(HostCollocation collocation) {
		List<SadComponentInstantiation> instantiations = new ArrayList<SadComponentInstantiation>();
		for (SadComponentPlacement placement : collocation.getComponentPlacement()) {
			instantiations.addAll(placement.getComponentInstantiation());
		}
		return instantiations;
	}

	/**
	 * Creates a dialog which prompts the user for a host collocation name.
	 * Will return <code>null</code> if the user terminates the dialog via
	 * 'Cancel' or otherwise.
	 * @return host collocation name
	 */
	private String getUniqueCollocationName(SoftwareAssembly sad) {
		Set<String> existingNames = new HashSet<String>();
		for (HostCollocation collocation : sad.getPartitioning().getHostCollocation()) {
			existingNames.add(collocation.getName());
		}
		for (int index = 1; ; index++) {
			String name = "collocation_" + index;
			if (!existingNames.contains(name)) {
				return name;
			}
		}
	}

	@Override
	public int getEditingType() {
		return IDirectEditing.TYPE_TEXT;
	}

	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		String gaType = Graphiti.getPeService().getPropertyValue(context.getGraphicsAlgorithm(), DUtil.GA_TYPE);
		return GA_OUTER_ROUNDED_RECTANGLE_TEXT.equals(gaType);
	}

	@Override
	public String getInitialValue(IDirectEditingContext context) {
		HostCollocation hc = (HostCollocation) getBusinessObjectForPictogramElement(context.getPictogramElement());
		return hc.getName();
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		ContainerShape collocationShape = (ContainerShape) context.getPictogramElement();
		final HostCollocation hc = (HostCollocation) getBusinessObjectForPictogramElement(collocationShape);

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				hc.setName(value);
			}
		});

		// Perform update, redraw
		updatePictogramElement(collocationShape);
	}
	
}
