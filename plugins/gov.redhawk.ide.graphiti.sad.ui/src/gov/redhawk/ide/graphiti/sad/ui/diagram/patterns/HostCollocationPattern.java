/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.graphiti.sad.ui.diagram.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.datatypes.ILocation;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IMoveShapeFeature;
import org.eclipse.graphiti.features.IRemoveFeature;
import org.eclipse.graphiti.features.context.ICreateContext;
import org.eclipse.graphiti.features.context.ICustomContext;
import org.eclipse.graphiti.features.context.IDeleteContext;
import org.eclipse.graphiti.features.context.IDirectEditingContext;
import org.eclipse.graphiti.features.context.IRemoveContext;
import org.eclipse.graphiti.features.context.IResizeShapeContext;
import org.eclipse.graphiti.features.context.impl.AddContext;
import org.eclipse.graphiti.features.context.impl.MoveShapeContext;
import org.eclipse.graphiti.features.context.impl.RemoveContext;
import org.eclipse.graphiti.features.impl.DefaultResizeShapeFeature;
import org.eclipse.graphiti.func.IDirectEditing;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;

import gov.redhawk.core.graphiti.sad.ui.diagram.providers.WaveformImageProvider;
import gov.redhawk.core.graphiti.sad.ui.ext.ComponentShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.wizards.HostCollocationWizard;
import gov.redhawk.ide.graphiti.ui.diagram.features.custom.IDialogEditingPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import mil.jpeojtrs.sca.partitioning.FindByStub;
import mil.jpeojtrs.sca.partitioning.UsesDeviceStub;
import mil.jpeojtrs.sca.sad.HostCollocation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentPlacement;
import mil.jpeojtrs.sca.sad.SadFactory;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * Provides a host collocation pattern that provides full-featured editing.
 */
public class HostCollocationPattern extends gov.redhawk.core.graphiti.sad.ui.diagram.patterns.HostCollocationExplorerPattern implements IDialogEditingPattern {

	public HostCollocationPattern() {
		super();
	}

	@Override
	public String getCreateName() {
		return Messages.HostCollocationPattern_CreateName;
	}

	@Override
	public String getCreateDescription() {
		return Messages.HostCollocationPattern_CreateDescription;
	}

	@Override
	public String getCreateImageId() {
		return WaveformImageProvider.IMG_HOST_COLLOCATION;
	}

	@Override
	public String getEditName() {
		return Messages.HostCollocationPattern_EditName;
	}

	// DIAGRAM FEATURES

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
		List<Shape> containedShapes = DUtil.getShapesInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(), context.getY());
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

	@Override
	public boolean canResizeShape(IResizeShapeContext context) {
		// If a findby or usesdevice would be covered by the host collocation then disallow
		List<Shape> shapesToAddToHostCollocation = DUtil.getShapesInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(), context.getY());
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
		List<Shape> shapesToRemoveFromHostCollocation = DUtil.getShapesOutsideArea(containerShape, context.getWidth(), context.getHeight(), context.getX(),
			context.getY());
		final List<SadComponentInstantiation> ciToRemove = new ArrayList<SadComponentInstantiation>();
		for (Shape shape : shapesToRemoveFromHostCollocation) {
			for (EObject obj : shape.getLink().getBusinessObjects()) {
				if (obj instanceof SadComponentInstantiation) {
					ciToRemove.add((SadComponentInstantiation) obj);
				}
			}
		}

		// find all components to add to add (now inside host collocation, expanded)
		List<Shape> shapesToAddToHostCollocation = DUtil.getShapesInArea(getDiagram(), context.getWidth(), context.getHeight(), context.getX(), context.getY());
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
		for (int index = 1;; index++) {
			String name = "collocation_" + index; //$NON-NLS-1$
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

	@Override
	public boolean canDialogEdit(ICustomContext context) {
		return context.getPictogramElements() != null && context.getPictogramElements().length == 1;
	}

	@Override
	public boolean dialogEdit(ICustomContext context) {
		PictogramElement pe = context.getPictogramElements()[0];
		HostCollocation hc = (HostCollocation) getBusinessObjectForPictogramElement(pe);
		SoftwareAssembly sad = DUtil.getDiagramSAD(getDiagram());

		// Create and show the wizard. It performs all the work.
		HostCollocationWizard wizard = new HostCollocationWizard(hc, sad.getUsesDeviceDependencies());
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		int result = dialog.open();
		return result == WizardDialog.OK;
	}
}
