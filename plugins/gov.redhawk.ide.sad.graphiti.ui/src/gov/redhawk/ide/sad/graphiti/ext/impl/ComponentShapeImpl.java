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

import gov.redhawk.ide.sad.graphiti.ext.ComponentShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.ImageProvider;
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
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.services.GraphitiUi;

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
	public void init(final ContainerShape targetContainerShape, final SadComponentInstantiation ci, final IFeatureProvider featureProvider,
		ExternalPorts externalPorts, final AssemblyController assemblyController) {
		// get sad from diagram, we need to link it to all shapes so the diagram will update when changes occur to
		// assembly controller and external ports
		List<EObject> businessObjectsToLink = new ArrayList<EObject>();
		final SoftwareAssembly sad = DUtil.getDiagramSAD(featureProvider, DUtil.findDiagram(targetContainerShape));
		// ORDER MATTERS, CI must be first
		businessObjectsToLink.add(ci);
		businessObjectsToLink.add(sad);
		if (externalPorts != null) {
			businessObjectsToLink.add(externalPorts);
		}

		// get external ports relevant to component instantiation
		final List<Port> ciExternalPorts = getComponentExternalPorts(ci, externalPorts);

		super.init(targetContainerShape, ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), businessObjectsToLink, featureProvider,
			ImageProvider.IMG_COMPONENT_PLACEMENT, StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(targetContainerShape)), ci.getUsageName(),
			ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(DUtil.findDiagram(targetContainerShape)), ci.getInterfaceStub(),
			ci.getUses(), ci.getProvides(), ciExternalPorts);

		// get inner ContainerShape
		ContainerShape innerContainerShape = getInnerContainerShape();

		// add start order ellipse
		addStartOrderEllipse(innerContainerShape, ci, assemblyController, featureProvider);
	}

	/**
	 * Updates the shape's contents using the supplied fields. Return true if an update occurred, false otherwise.>
	 */
	public Reason update(final SadComponentInstantiation ci, final IFeatureProvider featureProvider, ExternalPorts externalPorts,
		final AssemblyController assemblyController) {
		return internalUpdate(ci, featureProvider, externalPorts, assemblyController, true);
	}

	/**
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 */
	public Reason updateNeeded(final SadComponentInstantiation ci, final IFeatureProvider featureProvider, ExternalPorts externalPorts,
		final AssemblyController assemblyController) {
		return internalUpdate(ci, featureProvider, externalPorts, assemblyController, false);
	}

	/**
	 * Add an Ellipse to provided container shape that will contain the start order from sadComponentInstantiation
	 */
	public ContainerShape addStartOrderEllipse(ContainerShape innerContainerShape, SadComponentInstantiation sadComponentInstantiation,
		AssemblyController assemblyController, IFeatureProvider featureProvider) {
		Diagram diagram = DUtil.findDiagram(innerContainerShape);

		// start order ellipse
		ContainerShape startOrderEllipseShape = Graphiti.getCreateService().createContainerShape(innerContainerShape, false);
<<<<<<< HEAD
		Graphiti.getPeService().setPropertyValue(startOrderEllipseShape, DUtil.SHAPE_TYPE, SHAPE_START_ORDER_ELLIPSE);
=======
		Graphiti.getPeService().setPropertyValue(startOrderEllipseShape, DUtil.SHAPE_TYPE, SHAPE_START_ORDER_ELLIPSE_SHAPE);
>>>>>>> Checkstyle and commenting edits
		Ellipse startOrderEllipse = Graphiti.getCreateService().createEllipse(startOrderEllipseShape);
		// if start order zero (assembly controller), then use special style
		if (assemblyController != null) {
			startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderAssemblyControllerEllipse(diagram));
			featureProvider.link(startOrderEllipseShape, assemblyController);
		} else {
			startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderEllipse(diagram));
		}
		Graphiti.getPeService().setPropertyValue(startOrderEllipse, DUtil.GA_TYPE, GA_START_ORDER_ELLIPSE);
		Graphiti.getGaLayoutService().setSize(startOrderEllipse, START_ORDER_ELLIPSE_DIAMETER, START_ORDER_ELLIPSE_DIAMETER);

		// port text
		Shape startOrderTextShape = Graphiti.getCreateService().createShape(startOrderEllipseShape, false);
		Text startOrderText = Graphiti.getCreateService().createText(startOrderTextShape, sadComponentInstantiation.getStartOrder().toString());
		Graphiti.getPeService().setPropertyValue(startOrderText, DUtil.GA_TYPE, GA_START_ORDER_TEXT);
		startOrderText.setStyle(StyleUtil.getStyleForStartOrderText(diagram));
		// TODO: bwhoff2 we need to handle the x for the text inside the shape
		IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(sadComponentInstantiation.getStartOrder().toString(),
			StyleUtil.getStartOrderFont(diagram));
		int textX = START_ORDER_ELLIPSE_DIAMETER / 2 - textDimension.getWidth() / 2;
		Graphiti.getGaLayoutService().setLocationAndSize(startOrderText, textX, START_ORDER_TOP_TEXT_PADDING, START_ORDER_ELLIPSE_DIAMETER,
			START_ORDER_ELLIPSE_DIAMETER);

		return startOrderEllipseShape;
	}

	/**
	 * Return the startOrderEllipseShape
	 */
	public ContainerShape getStartOrderEllipseShape() {
<<<<<<< HEAD
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_START_ORDER_ELLIPSE);
=======
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_START_ORDER_ELLIPSE_SHAPE);
>>>>>>> Checkstyle and commenting edits
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

		// start order ellipse
		Graphiti.getGaLayoutService().setLocation(getStartOrderEllipseShape().getGraphicsAlgorithm(),
			getInnerContainerShape().getGraphicsAlgorithm().getWidth() - (START_ORDER_ELLIPSE_DIAMETER + START_ORDER_ELLIPSE_RIGHT_PADDING),
			START_ORDER_ELLIPSE_TOP_PADDING);

	}

	/**
	 * Performs either an update or a check to determine if update is required.
	 * if performUpdate flag is true it will update the shape,
	 * otherwise it will return reason why update is required.
	 * @param ci
	 * @param performUpdate
	 * @return
	 */
	public Reason internalUpdate(SadComponentInstantiation ci, IFeatureProvider featureProvider, ExternalPorts externalPorts,
		AssemblyController assemblyController, boolean performUpdate) {
		Diagram diagram = DUtil.findDiagram(this);
		// get external ports relavent to ci
		final List<Port> ciExternalPorts = getComponentExternalPorts(ci, externalPorts);
		Reason superReason = null;
		if (performUpdate) {
			superReason = super.update(ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), ci, featureProvider,
				ImageProvider.IMG_COMPONENT_PLACEMENT, StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(this)), ci.getUsageName(),
				ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(diagram), ci.getInterfaceStub(), ci.getUses(), ci.getProvides(),
				ciExternalPorts);
		} else {
			superReason = super.updateNeeded(ci.getPlacement().getComponentFileRef().getFile().getSoftPkg().getName(), ci, featureProvider,
				ImageProvider.IMG_COMPONENT_PLACEMENT, StyleUtil.getStyleForComponentOuter(DUtil.findDiagram(this)), ci.getUsageName(),
				ImageProvider.IMG_COMPONENT_INSTANCE, StyleUtil.getStyleForComponentInner(diagram), ci.getInterfaceStub(), ci.getUses(), ci.getProvides(),
				ciExternalPorts);
		}

		boolean updateStatus;

		// if parent says we need to update, return now
		if (!performUpdate && superReason.toBoolean()) {
			return superReason;
		} else {
			updateStatus = superReason.toBoolean();
		}

		// startOrderText
		Text startOrderTextGA = getStartOrderText();
		if (startOrderTextGA != null && ci.getStartOrder().compareTo(new BigInteger(startOrderTextGA.getValue())) != 0) {
			if (performUpdate) {
				updateStatus = true;
				startOrderTextGA.setValue(ci.getStartOrder().toString());
				// adjust for startOrderText size
				IDimension textDimension = GraphitiUi.getUiLayoutService().calculateTextSize(ci.getStartOrder().toString(),
					StyleUtil.getStartOrderFont(diagram));
				int textX = START_ORDER_ELLIPSE_DIAMETER / 2 - textDimension.getWidth() / 2;
				Graphiti.getGaLayoutService().setLocationAndSize(startOrderTextGA, textX, START_ORDER_TOP_TEXT_PADDING, START_ORDER_ELLIPSE_DIAMETER,
					START_ORDER_ELLIPSE_DIAMETER);
			} else {
				return new Reason(true, "Component start order requires update");
			}
		}

		// assembly controller
		// Style
		Ellipse startOrderEllipse = (Ellipse) getStartOrderEllipseShape().getGraphicsAlgorithm();
		// if start order zero (assembly controller), then use special style
		if (startOrderEllipse.getStyle() != StyleUtil.getStyleForStartOrderAssemblyControllerEllipse(diagram) && assemblyController != null) {
			if (performUpdate) {
				updateStatus = true;
				startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderAssemblyControllerEllipse(diagram));
				featureProvider.link(startOrderEllipse.getPictogramElement(), assemblyController);
			} else {
				return new Reason(true, "Component start order requires update");
			}
		} else if (startOrderEllipse.getStyle() != StyleUtil.getStyleForStartOrderEllipse(diagram) && assemblyController == null) {
			if (performUpdate) {
				startOrderEllipse.setStyle(StyleUtil.getStyleForStartOrderEllipse(diagram));
				// remove assembly controller links
				EcoreUtil.delete((EObject) startOrderEllipse.getPictogramElement().getLink());
			} else {
				return new Reason(true, "Component start order requires update");
			}
		}

		// we must make sure externalPorts is linked with this object if its set,
		// otherwise we need to remove it
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
	 * @param ci
	 * @return
	 */
	public int getMinimumWidth(final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs,
		final EList<UsesPortStub> usesPortStubs) {

		// determine width of parentshape
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
