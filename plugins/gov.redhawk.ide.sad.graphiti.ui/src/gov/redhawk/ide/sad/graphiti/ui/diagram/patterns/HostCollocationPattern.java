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

import gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl;
import gov.redhawk.ide.sad.graphiti.ui.diagram.dialogs.AbstractInputValidationDialog;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.pattern.IPattern;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;

public class HostCollocationPattern extends AbstractContainerPattern implements IPattern {

	public static final String NAME = "Host Collocation";

	// Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove
	// etc.)
	public static final String HOST_COLLOCATION_OUTER_CONTAINER_SHAPE = "hostCollocationOuterContainerShape";

	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_OUTER_ROUNDED_RECTANGLE = "outerRoundedRectangle";
	public static final String GA_OUTER_ROUNDED_RECTANGLE_TEXT = "outerRoundedRectangleText";
	public static final String GA_OUTER_ROUNDED_RECTANGLE_IMAGE = "outerRoundedRectangleImage";

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
		return ImageProvider.IMG_HOST_COLLOCATION;
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
	 * Returns all ContainerShapes associated with HostCollocation
	 * @param diagram
	 * @return
	 */
	public static List<ContainerShape> getHostCollocationContainerShapes(Diagram diagram) {
		List<ContainerShape> hostCollocationContainerShapes = new ArrayList<ContainerShape>();
		// get all Shapes linked to a HostCollocation
		List<ContainerShape> containerShapes = DUtil.getAllContainerShapes(diagram, HOST_COLLOCATION_OUTER_CONTAINER_SHAPE);
		for (ContainerShape cs : containerShapes) {
			hostCollocationContainerShapes.add(cs);
		}
		return hostCollocationContainerShapes;
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
			StyleUtil.getStyleForHostCollocation(diagram));

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
		int minWidth = (context.getWidth() > 300) ? context.getWidth() : 300;
		int minHeight = (context.getHeight() > 300) ? context.getHeight() : 300;
		// outerRoundedRectangle
		gaLayoutService.setLocationAndSize(outerRoundedRectangle, context.getX(), context.getY(), minWidth, minHeight);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleText, RHContainerShapeImpl.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING
			+ RHContainerShapeImpl.ICON_IMAGE_LENGTH + 4, 0, minWidth
			- (RHContainerShapeImpl.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + RHContainerShapeImpl.ICON_IMAGE_LENGTH + 4), 20);
		gaLayoutService.setLocationAndSize(outerRoundedRectangleImage, RHContainerShapeImpl.INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0,
			RHContainerShapeImpl.ICON_IMAGE_LENGTH, RHContainerShapeImpl.ICON_IMAGE_LENGTH);

		// move all SadComponentInstantiation shapes into new HostCollocation shape
		// find all SadComponentInstantiation shapes
		List<Shape> containedShapes = DUtil.getContainersInArea(getDiagram(), context, GA_OUTER_ROUNDED_RECTANGLE);
		for (Shape shape : containedShapes) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					// reparent
					shape.setContainer(outerContainerShape);
					// reposition shape inside host shape
					int newX = shape.getGraphicsAlgorithm().getX() - context.getX();
					int newY = shape.getGraphicsAlgorithm().getY() - context.getY();
					gaLayoutService.setLocation(shape.getGraphicsAlgorithm(), newX, newY);
				}
			}
		}

		// layout
		layoutPictogramElement(outerContainerShape);

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
		final String hostCollocationName = getUserInput();
		if (hostCollocationName == null) {
			return null;
		}

		final HostCollocation[] hostCollocations = new HostCollocation[1];

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// find all SadComponentInstantiation
		List<Shape> containedShapes = DUtil.getContainersInArea(getDiagram(), context, GA_OUTER_ROUNDED_RECTANGLE);
		final List<SadComponentInstantiation> sadComponentInstantiations = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : containedShapes) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					sadComponentInstantiations.add((SadComponentInstantiation) obj);
				}
			}
		}

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// create hostCollocation
				hostCollocations[0] = SadFactory.eINSTANCE.createHostCollocation();
				hostCollocations[0].setName(hostCollocationName);

				// move components to hostCollocation
				// remove from sad partitioning
				sad.getPartitioning().getComponentPlacement().removeAll(sadComponentInstantiations);
				// add to hostCollocation
				for (SadComponentInstantiation ci : sadComponentInstantiations) {
					hostCollocations[0].getComponentPlacement().add((SadComponentPlacement) ci.getPlacement());
				}

				// add to sad partitioning
				sad.getPartitioning().getHostCollocation().add(hostCollocations[0]);
			}
		});

		addGraphicalRepresentation(context, hostCollocations[0]);

		return new Object[] { hostCollocations[0] }; 
	}

	/**
	 * Resizing a Component shape is always allowed
	 */
	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
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
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// find all components to remove (no longer inside the host collocation box, minimized)
		List<Shape> shapesToRemoveFromHostCollocation = DUtil.getContainersOutsideArea(containerShape, context, GA_OUTER_ROUNDED_RECTANGLE);
		final List<SadComponentInstantiation> ciToRemove = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : shapesToRemoveFromHostCollocation) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					ciToRemove.add((SadComponentInstantiation) obj);
				}
			}
		}

		// find all components to add to add (now inside host collocation, expanded)
		List<Shape> shapesToAddToHostCollocation = DUtil.getContainersInArea(getDiagram(), context, GA_OUTER_ROUNDED_RECTANGLE);
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
				int newX = s.getGraphicsAlgorithm().getX() + x;
				int newY = s.getGraphicsAlgorithm().getY() + y;
				Graphiti.getGaService().setLocation(s.getGraphicsAlgorithm(), newX, newY);
			}
		}

		// move shapes from host collocation to diagram
		for (Shape s : shapesToAddToHostCollocation) {
			Object obj = DUtil.getBusinessObject(s);
			if (obj instanceof SadComponentInstantiation) {
				// reparent
				s.setContainer(containerShape);
				// reposition shape outside host shape
				int newX = s.getGraphicsAlgorithm().getX() - x;
				int newY = s.getGraphicsAlgorithm().getY() - y;
				Graphiti.getGaService().setLocation(s.getGraphicsAlgorithm(), newX, newY);
			}
		}

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

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// get sad from diagram
		final SoftwareAssembly sad = DUtil.getDiagramSAD(getFeatureProvider(), getDiagram());

		// Create Component Related objects in SAD model
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {

				// null checks
				if (sad.getPartitioning() == null || sad.getPartitioning().getHostCollocation() == null) {
					return;
				}

				// remove all contained component's appropriately
				if (hostCollocationToDelete.getComponentPlacement() != null) {
					for (SadComponentPlacement placement : hostCollocationToDelete.getComponentPlacement()) {
						if (placement.getComponentInstantiation() != null) {
							for (SadComponentInstantiation ci : placement.getComponentInstantiation()) {
								DUtil.deleteComponentInstantiation(ci, sad);
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
		super.delete(context);
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
		String imageId, Style containerStyle) {
		ContainerShape outerContainerShape = Graphiti.getCreateService().createContainerShape(targetContainerShape, true);
		Graphiti.getPeService().setPropertyValue(outerContainerShape, DUtil.SHAPE_TYPE, HOST_COLLOCATION_OUTER_CONTAINER_SHAPE);
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(outerContainerShape, 5, 5);
		outerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE);
		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_IMAGE); // ref helps
		// with
		// resize
		// text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, text);
		cText.setStyle(StyleUtil.getStyleForOuterText(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getPeService().setPropertyValue(cText, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_TEXT);
		featureProvider.link(outerContainerShape, businessObject); // link container and business object

		return outerContainerShape;
	}

	@Override
	public boolean update(IUpdateContext context) {
		return false;
		// TODO
	}

	/**
	 * Determines whether we need to update the diagram from the model.
	 */
	@Override
	public IReason updateNeeded(IUpdateContext context) {
		return new Reason(false);
		// TODO
	}
	
	/**
	 * Creates a dialog which prompts the user for an event channel name.
	 * Will return <code>null</code> if the user terminates the dialog via
	 * 'Cancel' or otherwise.
	 * @return host collocation name
	 */
	private String getUserInput() {
		// prompt user for Host Collocation name
		AbstractInputValidationDialog dialog = new AbstractInputValidationDialog(
			NAME, "Enter " + NAME + " name", NAME) {
			@Override
			public String inputValidity(String value) {
				return checkValueValid(value, null);
			}
		};
		return dialog.getInput();
	}

	@Override
	public String getInnerTitle(EObject obj) {
		if (obj instanceof HostCollocation) {
			return ((HostCollocation) obj).getName();
		}
		return null;
	}



	@Override
	public boolean canDirectEdit(IDirectEditingContext context) {
		Object obj = DUtil.getBusinessObject(context.getPictogramElement());
		GraphicsAlgorithm ga = context.getGraphicsAlgorithm();

		// allow if we've selected the inner Text for the component
		if (obj instanceof HostCollocation && ga instanceof Text) {
			Text text = (Text) ga;
			for (Property prop : text.getProperties()) {
				if (prop.getValue().equals(GA_OUTER_ROUNDED_RECTANGLE_TEXT)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String getInitialValue(IDirectEditingContext context) {
		EObject hc = (EObject) DUtil.getBusinessObject(context.getPictogramElement());
		return getInnerTitle(hc);
	}

	@Override
	public void setValue(final String value, IDirectEditingContext context) {
		final HostCollocation hc = (HostCollocation) DUtil.getBusinessObject(context.getPictogramElement());

		// editing domain for our transaction
		TransactionalEditingDomain editingDomain = getFeatureProvider().getDiagramTypeProvider().getDiagramBehavior().getEditingDomain();

		// Perform business object manipulation in a Command
		TransactionalCommandStack stack = (TransactionalCommandStack) editingDomain.getCommandStack();
		stack.execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				// set usage name
				hc.setName(value);
			}
		});

		// perform update, redraw
		updatePictogramElement(context.getPictogramElement());
	}
}
