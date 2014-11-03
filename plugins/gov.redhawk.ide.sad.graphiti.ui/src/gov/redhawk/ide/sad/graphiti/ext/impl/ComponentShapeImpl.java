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
package gov.redhawk.ide.sad.graphiti.ext.impl;

import gov.redhawk.ide.sad.graphiti.debug.internal.ui.LocalGraphitiSadMultiPageScaEditor;
import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.AssemblyController;
import mil.jpeojtrs.sca.sad.ExternalPorts;
import mil.jpeojtrs.sca.sad.Port;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;
import org.eclipse.ui.IEditorPart;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Component Shape</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class ComponentShapeImpl extends RHContainerShapeImpl implements ComponentShape {

	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_START_ORDER_ELLIPSE = "startOrderEllipse";
	public static final String GA_START_ORDER_TEXT = "startOrderText";

	// Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove
	// etc.)
	public static final String SHAPE_START_ORDER_ELLIPSE_SHAPE = "startOrderEllipseShape";

	// Shape size constants
	public static final int START_ORDER_ELLIPSE_DIAMETER = 17;
	public static final int START_ORDER_TOP_TEXT_PADDING = 0;
	public static final int START_ORDER_ELLIPSE_LEFT_PADDING = 20;
	public static final int START_ORDER_ELLIPSE_RIGHT_PADDING = 5;
	public static final int START_ORDER_ELLIPSE_TOP_PADDING = 5;

	// Default start order text value for components that do not have a start order declared
	private static final String NO_START_ORDER_STRING = "*";

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentShapeImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RHGxPackage.Literals.COMPONENT_SHAPE;
	}

	/**
	 * Creates the inner shapes that make up this container shape
	 */
	public void init(IAddContext context, ComponentPattern pattern) {
		if (!(context.getNewObject() instanceof SadComponentInstantiation)) {
			return;
		}
		SadComponentInstantiation ci = (SadComponentInstantiation) context.getNewObject();
		IFeatureProvider featureProvider = pattern.getFeatureProvider();
		Diagram diagram = featureProvider.getDiagramTypeProvider().getDiagram();
		AssemblyController assemblyController = pattern.getComponentAssemblyController(ci);

		// get external ports
		ExternalPorts externalPorts = DUtil.getDiagramSAD(featureProvider, diagram).getExternalPorts();

		// get sad from diagram, we need to link it to all shapes so the diagram will update when changes occur to
		// assembly controller and external ports
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		final SoftwareAssembly sad = DUtil.getDiagramSAD(featureProvider, diagram);
		// ORDER MATTERS, CI must be first
		businessObjectsToLink.add(ci);
		businessObjectsToLink.add(sad);
		if (externalPorts != null) {
			businessObjectsToLink.add(externalPorts);
		}

		// get external ports relevant to component instantiation
		final List<Port> ciExternalPorts = getComponentExternalPorts(ci, externalPorts);

		// create graphical representation
		super.init(context, pattern, ciExternalPorts);

		// add start order ellipse
		if (sad.getId() != null && !( DUtil.isDiagramLocal(DUtil.findDiagram(this)))) {
			addStartOrderEllipse(ci, assemblyController, featureProvider);
		}
	}

	/**
	 * Updates the shape's contents using the supplied fields. Return true if an update occurred, false otherwise.>
	 */
	public Reason update(final IUpdateContext context, ComponentPattern pattern) {
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());
		return this.internalUpdate(pattern, ci, true);
	}

	/**
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 */
	public Reason updateNeeded(final IUpdateContext context, ComponentPattern pattern) {
		SadComponentInstantiation ci = (SadComponentInstantiation) DUtil.getBusinessObject(context.getPictogramElement());
		return this.internalUpdate(pattern, ci, false);
	}

	/**
	 * Add an Ellipse to provided container shape that will contain the start order from sadComponentInstantiation
	 */
	public ContainerShape addStartOrderEllipse(SadComponentInstantiation sadComponentInstantiation, AssemblyController assemblyController,
		IFeatureProvider featureProvider) {
		Diagram diagram = DUtil.findDiagram(getInnerContainerShape());

		// Create ellipse shape to display component start order
		ContainerShape startOrderEllipseShape = Graphiti.getCreateService().createContainerShape(getInnerContainerShape(), false);
		Graphiti.getPeService().setPropertyValue(startOrderEllipseShape, DUtil.SHAPE_TYPE, SHAPE_START_ORDER_ELLIPSE_SHAPE);
		Ellipse startOrderEllipse = Graphiti.getCreateService().createEllipse(startOrderEllipseShape);

		if (assemblyController != null) {
			// If component is assembly controller, then set background to a different color
			startOrderEllipse.setStyle(StyleUtil.createStyleForStartOrderAssemblyControllerEllipse(diagram));
			featureProvider.link(startOrderEllipseShape, assemblyController);
		} else {
			startOrderEllipse.setStyle(StyleUtil.createStyleForStartOrderEllipse(diagram));
		}
		Graphiti.getPeService().setPropertyValue(startOrderEllipse, DUtil.GA_TYPE, GA_START_ORDER_ELLIPSE);
		Graphiti.getGaLayoutService().setSize(startOrderEllipse, START_ORDER_ELLIPSE_DIAMETER, START_ORDER_ELLIPSE_DIAMETER);

		// Set start order value for pictogram element
		String startOrder;
		if (sadComponentInstantiation.getStartOrder() == null) {
			// if business object start order is null (possible in legacy waveforms), then set text to asterisk (*)
			startOrder = NO_START_ORDER_STRING;
		} else {
			// if business object start order != null, then set text to that value
			startOrder = sadComponentInstantiation.getStartOrder().toString();
		}

		// Create text shape to display start order
		Shape startOrderTextShape = Graphiti.getPeCreateService().createShape(startOrderEllipseShape, false);
		Text startOrderText = Graphiti.getCreateService().createText(startOrderTextShape, startOrder);
		Graphiti.getPeService().setPropertyValue(startOrderText, DUtil.GA_TYPE, GA_START_ORDER_TEXT);
		startOrderText.setStyle(StyleUtil.createStyleForStartOrderText(diagram));
		IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(startOrder, StyleUtil.getStartOrderFont(diagram));
		int textX = START_ORDER_ELLIPSE_DIAMETER / 2 - textDimension.getWidth() / 2;
		Graphiti.getGaLayoutService().setLocationAndSize(startOrderText, textX, START_ORDER_TOP_TEXT_PADDING, START_ORDER_ELLIPSE_DIAMETER,
			START_ORDER_ELLIPSE_DIAMETER);

		return startOrderEllipseShape;
	}

	/**
	 * Return the startOrderEllipseShape
	 */
	public ContainerShape getStartOrderEllipseShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_START_ORDER_ELLIPSE_SHAPE);
	}

	/**
	 * Return the startOrderText
	 */
	public Text getStartOrderText() {
		return (Text) DUtil.findFirstPropertyContainer(getStartOrderEllipseShape(), GA_START_ORDER_TEXT);
	}

	/**
	 * performs a layout on the contents of this shape
	 */
	public void layout() {
		super.layout();

		if (getStartOrderEllipseShape() != null && !DUtil.isDiagramLocal(DUtil.findDiagram(this))) {
			// Set the layout for the start order ellipse
			Graphiti.getGaLayoutService().setLocation(getStartOrderEllipseShape().getGraphicsAlgorithm(),
				getInnerContainerShape().getGraphicsAlgorithm().getWidth() - (START_ORDER_ELLIPSE_DIAMETER + START_ORDER_ELLIPSE_RIGHT_PADDING),
				START_ORDER_ELLIPSE_TOP_PADDING);
		}
	}

	/**
	 * Performs either an update or a check to determine if update is required.
	 * if performUpdate flag is true it will update the shape,
	 * otherwise it will return reason why update is required.
	 * @param component instantiation
	 * @param performUpdate
	 * @return
	 */
	public Reason internalUpdate(ComponentPattern pattern, SadComponentInstantiation ci, boolean performUpdate) {
		Diagram diagram = DUtil.findDiagram(this);
		IFeatureProvider featureProvider = pattern.getFeatureProvider();
		SoftwareAssembly sad = DUtil.getDiagramSAD(featureProvider, diagram);
		ExternalPorts externalPorts = DUtil.getDiagramSAD(featureProvider, diagram).getExternalPorts();
		AssemblyController assemblyController = pattern.getComponentAssemblyController(ci);

		// get external ports relevant to component instantiation
		final List<Port> ciExternalPorts = getComponentExternalPorts(ci, externalPorts);
		Reason superReason = super.internalUpdate(pattern, ci, ciExternalPorts, performUpdate);

		boolean updateStatus;

		// if parent says we need to update, return now
		if (!performUpdate && superReason.toBoolean()) {
			return superReason;
		} else {
			updateStatus = superReason.toBoolean();
		}

		if (sad.getId() != null && !( DUtil.isDiagramLocal(DUtil.findDiagram(this)))) {
			// update startOrderText
			Text startOrderTextGA = getStartOrderText();
			if (ci.getStartOrder() == null && !startOrderTextGA.getValue().equals(NO_START_ORDER_STRING)) {
				// Start order was removed from component business object that previously had one.
				if (performUpdate) {
					updateStatus = true;
					startOrderTextGA.setValue(NO_START_ORDER_STRING);
				} else {
					return new Reason(true, "Component start order removed, update required");
				}
			} else if (ci.getStartOrder() != null && startOrderTextGA.getValue().equals(NO_START_ORDER_STRING)) {
				// Start order was add to component business object that previously DID NOT have one
				if (performUpdate) {
					updateStatus = true;
					startOrderTextGA.setValue(ci.getStartOrder().toString());
				} else {
					return new Reason(true, "Component has been assigned a start order, update required");
				}
			} else if (ci.getStartOrder() != null && !startOrderTextGA.getValue().equals(NO_START_ORDER_STRING)
				&& ci.getStartOrder().compareTo(new BigInteger(startOrderTextGA.getValue())) != 0) {
				// Handle all other start order changes
				if (performUpdate) {
					updateStatus = true;
					startOrderTextGA.setValue(ci.getStartOrder().toString());
				} else {
					return new Reason(true, "Component start order changed, update required");
				}
			}

			// update assembly controller styling and text
			Ellipse startOrderEllipse = (Ellipse) getStartOrderEllipseShape().getGraphicsAlgorithm();
			boolean needsUpdate = StyleUtil.needsUpdateForStartOrderAssemblyControllerEllipse(diagram, startOrderEllipse.getStyle());
			boolean isTextCorrect = ci.getStartOrder() != null ? (ci.getStartOrder().compareTo(BigInteger.ZERO) == 0) : false;
			if ((needsUpdate || !isTextCorrect) && assemblyController != null) {
				// if assembly controller, then use special style
				if (performUpdate) {
					updateStatus = true;
					startOrderEllipse.setStyle(StyleUtil.createStyleForStartOrderAssemblyControllerEllipse(diagram));
					if (ci.getStartOrder() != null && ci.getStartOrder().compareTo(BigInteger.ZERO) != 0) {
						// Make sure start order is set to zero for assembly controller, if the update occurred from
						// elsewhere in the model
						ci.setStartOrder(BigInteger.ZERO);
						ComponentPattern.organizeStartOrder(sad, diagram, featureProvider);
					} else {
						// Organization check to make sure start order sequence is correct, if the update occurred from
						// elsewhere in the model
						ComponentPattern.organizeStartOrder(sad, diagram, featureProvider);
					}
					featureProvider.link(startOrderEllipse.getPictogramElement(), assemblyController);
				} else {
					return new Reason(true, "Component start order requires update");
				}
			} else if (StyleUtil.needsUpdateForStartOrderEllipse(diagram, startOrderEllipse.getStyle()) && assemblyController == null) {
				if (performUpdate) {
					startOrderEllipse.setStyle(StyleUtil.createStyleForStartOrderEllipse(diagram));
					// remove assembly controller links
					EcoreUtil.delete((EObject) startOrderEllipse.getPictogramElement().getLink());
				} else {
					return new Reason(true, "Component start order requires update");
				}
			}
		}
		// we must make sure externalPorts is linked with this object if its set, otherwise we need to remove it
		if (performUpdate) {
			if (externalPorts != null && !this.getLink().getBusinessObjects().contains(externalPorts)) {
				this.getLink().getBusinessObjects().add(externalPorts);
			} else if (externalPorts == null) {
				EObject objectToRemove = null;
				for (EObject obj : this.getLink().getBusinessObjects()) {
					if (obj instanceof ExternalPorts) {
						objectToRemove = obj;
					}
				}
				if (objectToRemove != null) {
					this.getLink().getBusinessObjects().remove(objectToRemove);
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");
	}

	/**
	 * Returns minimum width for Shape with provides and uses port stubs and name text
	 */
	public int getMinimumWidth(final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs,
		final EList<UsesPortStub> usesPortStubs) {

		// determine width of parent shape
		int rhContainerShapeMinWidth = super.getMinimumWidth(outerTitle, innerTitle, providesPortStubs, usesPortStubs);

		int innerTitleWidth = 0;
		Diagram diagram = DUtil.findDiagram(this);

		// inner title (including start order)
		IDimension innerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(innerTitle, StyleUtil.getInnerTitleFont(diagram));
		innerTitleWidth = innerTitleDimension.getWidth() + INTERFACE_SHAPE_WIDTH + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING
			+ ComponentShapeImpl.START_ORDER_ELLIPSE_DIAMETER + ComponentShapeImpl.START_ORDER_ELLIPSE_LEFT_PADDING
			+ ComponentShapeImpl.START_ORDER_ELLIPSE_RIGHT_PADDING;

		// return the largest width
		if (rhContainerShapeMinWidth > innerTitleWidth) {
			return rhContainerShapeMinWidth;
		} else {
			return innerTitleWidth;
		}

	}

	// returns all external ports that belong to the provided Component.
	private static List<Port> getComponentExternalPorts(SadComponentInstantiation ci, ExternalPorts externalPorts) {
		List<Port> ciExternalPorts = new ArrayList<Port>();
		if (externalPorts != null && externalPorts.getPort() != null) {
			for (Port p : externalPorts.getPort()) {
				if (p.getComponentInstantiationRef().getRefid().equals(ci.getId())) {
					ciExternalPorts.add(p);
				}
			}
		}
		return ciExternalPorts;
	}

} // ComponentShapeImpl
