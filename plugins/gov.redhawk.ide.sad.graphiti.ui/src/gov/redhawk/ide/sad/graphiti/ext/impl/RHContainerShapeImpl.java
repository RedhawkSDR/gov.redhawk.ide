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

import gov.redhawk.ide.sad.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.Property;
import org.eclipse.graphiti.mm.PropertyContainer;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.Style;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.mm.pictograms.impl.ContainerShapeImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;
import org.eclipse.graphiti.ui.services.GraphitiUi;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>RH Container Shape</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
@SuppressWarnings("restriction")
public class RHContainerShapeImpl extends ContainerShapeImpl implements RHContainerShape {
	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_outerRoundedRectangle = "outerRoundedRectangle";
	public static final String GA_innerRoundedRectangle = "innerRoundedRectangle";
	public static final String GA_outerRoundedRectangleText = "outerRoundedRectangleText";
	public static final String GA_innerRoundedRectangleText = "innerRoundedRectangleText";
	public static final String GA_outerRoundedRectangleImage = "outerRoundedRectangleImage";
	public static final String GA_innerRoundedRectangleImage = "innerRoundedRectangleImage";
	public static final String GA_innerRoundedRectangleLine = "innerRoundedRectangleLine";
	public static final String GA_providesPortsRectangle = "providesPortsRectangle";
	public static final String GA_providesPortText = "GA_providesPortText";
	public static final String GA_fixPointAnchorRectangle = "GA_fixPointAnchorRectangle";
	public static final String GA_usesPortsRectangle = "usesPortsRectangle";
	public static final String GA_usesPortRectangle = "usesPortRectangle";
	public static final String GA_usesPortText = "GA_usesPortText";

	// Property key/value pairs help us identify Shapes to enable/disable user actions (move, resize, delete, remove
	// etc.)
	public static final String SHAPE_outerContainerShape = "outerContainerShape";
	public static final String SHAPE_innerContainerShape = "innerContainerShape";
	public static final String SHAPE_usesPortsContainerShape = "usesPortsContainerShape";
	public static final String SHAPE_providesPortsContainerShape = "providesPortsContainerShape";
	public static final String SHAPE_usesPortContainerShape = "usesPortContainerShape";
	public static final String SHAPE_providesPortContainerShape = "providesPortContainerShape";
	public static final String SHAPE_usesPortRectangleShape = "usesPortRectangleShape";
	public static final String SHAPE_providesPortRectangleShape = "providesPortRectangleShape";
	public static final String SHAPE_interfaceContainerShape = "interfaceContainerShape";
	public static final String SHAPE_interfaceEllipseShape = "interfaceEllipseShape";

	// Shape size constants
	public static final int OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING = 10;
	public static final int INNER_CONTAINER_SHAPE_TOP_PADDING = 20;
	public static final int INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15;
	public static final int INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING = 60;
	public static final int PROVIDES_PORTS_LEFT_PADDING = 5;
	public static final int INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING;
	public static final int PORTS_CONTAINER_SHAPE_TOP_PADDING = 60;
	public static final int INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING = 8;
	public static final int INNER_ROUNDED_RECTANGLE_LINE_Y = 28;
	public static final int NAME_CHAR_WIDTH = 7;
	public static final int LABEL_CHAR_WIDTH = 7;
	public static final int PORT_NAME_HORIZONTAL_PADDING = 5;
	public static final int PORT_ROW_HEIGHT = 15;
	public static final int PORT_ROW_PADDING_HEIGHT = 5;
	public static final int PORT_SHAPE_HEIGHT = 15;
	public static final int PORT_SHAPE_WIDTH = 15;
	public static final int PORT_CHAR_WIDTH = 7;
	public static final int LOLLIPOP_ELLIPSE_DIAMETER = 10;
	public static final int INTERFACE_SHAPE_WIDTH = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING;
	public static final int INTERFACE_SHAPE_HEIGHT = 10;
	public static final int REQ_PADDING_BETWEEN_PORT_TYPES = 0;
	public static final int ICON_IMAGE_LENGTH = 16;

	/**
	 * 
	 * @generated
	 */
	protected RHContainerShapeImpl() {
		super();
	}

	/**
	 * 
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RHGxPackage.Literals.RH_CONTAINER_SHAPE;
	}

	/**
	 * Creates the inner shapes that make up this container shape
	 */
	public void init(final ContainerShape targetContainerShape, final String outerText, final List<EObject> businessObjects,
		final IFeatureProvider featureProvider, final String outerImageId, final Style outerContainerStyle, final String innerText, final String innerImageId,
		final Style innerContainerStyle, final ComponentSupportedInterfaceStub interfaceStub, final EList<UsesPortStub> uses,
		final EList<ProvidesPortStub> provides, final List<Port> externalPorts) {
		getProperties().addAll(new ArrayList<Property>(0));
		setVisible(true);
		setActive(true);
		setContainer(targetContainerShape);

		// add property for this shape
		Graphiti.getPeService().setPropertyValue(this, DUtil.GA_TYPE, SHAPE_outerContainerShape);

		// graphic
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(this, 5, 5);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DUtil.GA_TYPE, GA_outerRoundedRectangle);
		outerRoundedRectangle.setStyle(outerContainerStyle);

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, outerImageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_outerRoundedRectangleImage);

		// text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, outerText);
		cText.setStyle(StyleUtil.getStyleForOuterText(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getPeService().setPropertyValue(cText, DUtil.GA_TYPE, GA_outerRoundedRectangleText);

		// link objects
		featureProvider.link(this, businessObjects.toArray());

		// inner container
		addInnerContainer(this, innerText, featureProvider, innerImageId, innerContainerStyle);

		// add lollipop
		addLollipop(this, interfaceStub, featureProvider);

		// add provides ports
		addProvidesPorts(this, provides, featureProvider, externalPorts);

		// add uses ports
		addUsesPorts(this, uses, featureProvider, externalPorts);
	}

	/**
   * 
   */
	public EList<ProvidesPortStub> getProvidesPortStubs() {
		return getInternalProvidesPortStubs();
	}

	/**
	 * Performs a layout on the contents of this shape
	 */
	public void layout() {
		// get shape being laid out
		ContainerShape containerShape = (ContainerShape) this;
		RoundedRectangle outerRoundedRectangle = (RoundedRectangle) this.getGraphicsAlgorithm();
		Diagram diagram = Graphiti.getPeService().getDiagramForPictogramElement(this);
		EList<ProvidesPortStub> provides = getProvidesPortStubs();
		EList<UsesPortStub> uses = getUsesPortStubs();

		// height
		int minimumHeight = getMinimumHeight(provides, uses);
		if (outerRoundedRectangle.getHeight() < minimumHeight) {
			outerRoundedRectangle.setHeight(minimumHeight);
		}

		// width
		int minimumWidth = getMinimumWidth(getOuterText().getValue(), getInnerText().getValue(), provides, uses);
		if (outerRoundedRectangle.getWidth() < minimumWidth) {
			outerRoundedRectangle.setWidth(minimumWidth);
		}

		int containerWidth = outerRoundedRectangle.getWidth();
		int containerHeight = outerRoundedRectangle.getHeight();

		// resize all diagram elements
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();

		// outerRoundedRectangle
		gaLayoutService.setLocationAndSize(getOuterText(), INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4, 0, containerWidth
			- (INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4), 20);
		gaLayoutService.setLocationAndSize(getOuterImage(), INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);

		// innerRoundedRectangle
		int innerContainerShapeWidth = containerWidth - INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING * 2 - PROVIDES_PORTS_LEFT_PADDING;
		int innerContainerShapeHeight = containerHeight - INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setLocationAndSize(getInnerContainerShape().getGraphicsAlgorithm(), INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING,
			INNER_CONTAINER_SHAPE_TOP_PADDING, innerContainerShapeWidth, innerContainerShapeHeight);
		IDimension innerRoundedRectangleTextSize = GraphitiUi.getUiLayoutService().calculateTextSize(getInnerText().getValue(),
			StyleUtil.getInnerTitleFont(Graphiti.getPeService().getDiagramForPictogramElement(containerShape)));
		int xForImage = (getInnerContainerShape().getGraphicsAlgorithm().getWidth() - (innerRoundedRectangleTextSize.getWidth() + ICON_IMAGE_LENGTH + 5)) / 2;
		gaLayoutService.setLocationAndSize(getInnerImage(), xForImage, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		gaLayoutService.setLocationAndSize(getInnerText(), xForImage + ICON_IMAGE_LENGTH + 5, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING,
			innerRoundedRectangleTextSize.getWidth() + 10, innerRoundedRectangleTextSize.getHeight());
		getInnerPolyline().getPoints().get(1).setX(innerContainerShapeWidth);

		// usesPortsRectangle
		if (uses != null && getUsesPortsContainerShape() != null) {
			int usesPortNameLength = DUtil.getLongestUsesPortWidth(uses, diagram);
			int intPortTextX = outerRoundedRectangle.getWidth() - (usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_WIDTH);
			gaLayoutService.setLocationAndSize(getUsesPortsContainerShape().getGraphicsAlgorithm(), intPortTextX, PORTS_CONTAINER_SHAPE_TOP_PADDING,
				PORT_SHAPE_WIDTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, uses.size() * (PORT_ROW_HEIGHT + PORT_ROW_PADDING_HEIGHT));
		}
	}

	/**
	 * Updates the shape's contents using the supplied fields. Return true if an update occurred, false otherwise.
	 */
	public Reason update(final String outerText, final Object businessObject, final IFeatureProvider featureProvider, final String outerImageId,
		final Style outerContainerStyle, final String innerText, final String innerImageId, final Style innerContainerStyle,
		final ComponentSupportedInterfaceStub interfaceStub, final EList<UsesPortStub> uses, final EList<ProvidesPortStub> provides,
		final List<Port> externalPorts) {
		return internalUpdate(outerText, businessObject, featureProvider, outerImageId, outerContainerStyle, innerText, innerImageId, innerContainerStyle,
			interfaceStub, uses, provides, externalPorts, true);
	}

//CHECKSTYLE:OFF
	/**
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 */
	public Reason updateNeeded(final String outerText, final Object businessObject, final IFeatureProvider featureProvider, final String outerImageId,
		final Style outerContainerStyle, final String innerText, final String innerImageId, final Style innerContainerStyle,
		final ComponentSupportedInterfaceStub interfaceStub, final EList<UsesPortStub> uses, final EList<ProvidesPortStub> provides,
		final List<Port> externalPorts) {
		return internalUpdate(outerText, businessObject, featureProvider, outerImageId, outerContainerStyle, innerText, innerImageId, innerContainerStyle,
			interfaceStub, uses, provides, externalPorts, false);
	}

	/**
	 * add inner container
	 */
	public ContainerShape addInnerContainer(ContainerShape targetContainerShape, String text, IFeatureProvider featureProvider, String imageId,
		Style containerStyle) {
		ContainerShape innerContainerShape = Graphiti.getCreateService().createContainerShape(targetContainerShape, false);
		Graphiti.getPeService().setPropertyValue(innerContainerShape, DUtil.GA_TYPE, SHAPE_innerContainerShape);
		RoundedRectangle innerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(innerContainerShape, 5, 5);
		innerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(innerRoundedRectangle, DUtil.GA_TYPE, GA_innerRoundedRectangle);

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(innerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_innerRoundedRectangleImage);

		// text
		Text ciText = Graphiti.getCreateService().createText(innerRoundedRectangle, text);
		ciText.setStyle(StyleUtil.getStyleForInnerText(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getPeService().setPropertyValue(ciText, DUtil.GA_TYPE, GA_innerRoundedRectangleText);

		// line
		Polyline polyline = Graphiti.getGaCreateService().createPolyline(innerRoundedRectangle,
			new int[] { 0, INNER_ROUNDED_RECTANGLE_LINE_Y, innerRoundedRectangle.getWidth(), INNER_ROUNDED_RECTANGLE_LINE_Y });
		polyline.setLineWidth(1);
		polyline.setBackground(Graphiti.getGaService().manageColor(DUtil.findDiagram(targetContainerShape), StyleUtil.BLACK));
		polyline.setForeground(Graphiti.getGaService().manageColor(DUtil.findDiagram(targetContainerShape), StyleUtil.BLACK));
		Graphiti.getPeService().setPropertyValue(polyline, DUtil.GA_TYPE, GA_innerRoundedRectangleLine);

		return innerContainerShape;
	}

	/**
	 * Add lollipop to targetContainerShape. Lollipop anchor will link to the provided business object.T
	 */
	public ContainerShape addLollipop(ContainerShape targetContainerShape, Object anchorBusinessObject, IFeatureProvider featureProvider) {

		// interface container lollipop
		ContainerShape interfaceContainerShape = Graphiti.getCreateService().createContainerShape(targetContainerShape, true);
		Graphiti.getPeService().setPropertyValue(interfaceContainerShape, DUtil.GA_TYPE, SHAPE_interfaceContainerShape);
		Rectangle interfaceRectangle = Graphiti.getCreateService().createRectangle(interfaceContainerShape);
		featureProvider.link(interfaceContainerShape, anchorBusinessObject);
		interfaceRectangle.setTransparency(.99d);
		Graphiti.getGaLayoutService().setLocationAndSize(interfaceRectangle, 0, 25, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);

		// interface lollipop ellipse
		Shape lollipopEllipseShape = Graphiti.getCreateService().createShape(interfaceContainerShape, true);
		Graphiti.getPeService().setPropertyValue(lollipopEllipseShape, DUtil.GA_TYPE, SHAPE_interfaceEllipseShape);
		Ellipse lollipopEllipse = Graphiti.getCreateService().createEllipse(lollipopEllipseShape);
		lollipopEllipse.setStyle(StyleUtil.getStyleForLollipopEllipse(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopEllipse, 0, 0, LOLLIPOP_ELLIPSE_DIAMETER, LOLLIPOP_ELLIPSE_DIAMETER);

		// interface lollipop line
		Shape lollipopLineShape = Graphiti.getCreateService().createContainerShape(interfaceContainerShape, true);
		Rectangle lollipopLine = Graphiti.getCreateService().createRectangle(lollipopLineShape);
		lollipopLine.setStyle(StyleUtil.getStyleForLollipopLine(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopLine, LOLLIPOP_ELLIPSE_DIAMETER, LOLLIPOP_ELLIPSE_DIAMETER / 2,
			INTERFACE_SHAPE_WIDTH - LOLLIPOP_ELLIPSE_DIAMETER, 1);

		// fix point anchor
		{
			FixPointAnchor fixPointAnchor = Graphiti.getPeCreateService().createFixPointAnchor(interfaceContainerShape);
			Point fixAnchorPoint = StylesFactory.eINSTANCE.createPoint();
			fixAnchorPoint.setX(0);
			fixAnchorPoint.setY(PORT_SHAPE_HEIGHT / 2);
			fixPointAnchor.setLocation(fixAnchorPoint);
			featureProvider.link(fixPointAnchor, anchorBusinessObject);
			fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
			fixPointAnchor.setReferencedGraphicsAlgorithm(interfaceRectangle);
			Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
			fixPointAnchorRectangle.setTransparency(.99d);
			Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, 0, -INTERFACE_SHAPE_HEIGHT / 2, INTERFACE_SHAPE_WIDTH,
				INTERFACE_SHAPE_HEIGHT);
		}

		return interfaceContainerShape;
	}

	/**
	 * Adds a ProvidesPortStub shape to the providesPortsContainerShape
	 */
	public void addProvidesPortContainerShape(ProvidesPortStub p, ContainerShape providesPortsContainerShape, int providesPortNameLength,
		IFeatureProvider featureProvider, Port externalPort) {

		// determine how many uses port are already there.
		int iter = 0;
		for (PropertyContainer child : DUtil.collectPropertyContainerChildren(providesPortsContainerShape)) {
			if (DUtil.isPropertyElementType(child, SHAPE_providesPortContainerShape)) {
				iter++;
			}
		}

		Diagram diagram = DUtil.findDiagram(providesPortsContainerShape);

		ContainerShape providesPortContainerShape = Graphiti.getCreateService().createContainerShape(providesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_providesPortContainerShape);// ref
																																	// prevent
																																	// selection/deletion/removal
		Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(providesPortContainerShape);
		providesPortContainerShapeRectangle.setTransparency(1d);
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortContainerShapeRectangle, 0, iter++ * (PORT_SHAPE_HEIGHT + 5),
			PORT_SHAPE_WIDTH + providesPortNameLength, PORT_SHAPE_HEIGHT);
		featureProvider.link(providesPortContainerShape, p);

		// port shape
		ContainerShape providesPortRectangleShape = Graphiti.getCreateService().createContainerShape(providesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortRectangleShape, DUtil.SHAPE_TYPE, SHAPE_providesPortRectangleShape);// ref
																																	// prevent
																																	// move
		Rectangle providesPortRectangle = Graphiti.getCreateService().createRectangle(providesPortRectangleShape);
		featureProvider.link(providesPortRectangleShape, p);
		providesPortRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortRectangle, 0, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

		// port text
		Shape providesPortTextShape = Graphiti.getCreateService().createShape(providesPortContainerShape, false);
		Text providesPortText = Graphiti.getCreateService().createText(providesPortTextShape, p.getName());
		providesPortText.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
		Graphiti.getPeService().setPropertyValue(providesPortText, DUtil.GA_TYPE, GA_providesPortText);
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortText, PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_HEIGHT, 0,
			providesPortNameLength * PORT_CHAR_WIDTH, 20);

		// fix point anchor
		FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(providesPortRectangleShape);
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(0);
		point.setY(PORT_SHAPE_HEIGHT / 2);
		fixPointAnchor.setLocation(point);
		featureProvider.link(fixPointAnchor, p);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(providesPortRectangle);
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_fixPointAnchorRectangle);
		if (externalPort != null) {
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForExternalProvidesPort(diagram));
			featureProvider.link(fixPointAnchor, externalPort); // link to externalPort so that update fires when it
																// changes
		} else {
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForProvidesPort(diagram));
		}
		Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, 0, -PORT_SHAPE_HEIGHT / 2, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

	}

	/**
	 * Adds provides port container to provided container shape. Adds a port shape with name and anchor for each
	 * providesPortStub.
	 */
	public void addProvidesPorts(ContainerShape outerContainerShape, EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider,
		List<Port> externalPorts) {

		// provides (input)
		int providesPortNameLength = DUtil.getLongestProvidesPortWidth(providesPortStubs, DUtil.findDiagram(outerContainerShape));
		ContainerShape providesPortsContainerShape = Graphiti.getCreateService().createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_providesPortsContainerShape);
		Rectangle providesPortsRectangle = Graphiti.getCreateService().createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, DUtil.GA_TYPE, GA_providesPortsRectangle);
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortsRectangle, PROVIDES_PORTS_LEFT_PADDING, PORTS_CONTAINER_SHAPE_TOP_PADDING,
			PORT_SHAPE_WIDTH + providesPortNameLength, providesPortStubs.size() * (PORT_ROW_HEIGHT + PORT_ROW_PADDING_HEIGHT));
		featureProvider.link(providesPortsContainerShape, providesPortStubs.toArray());

		// iterate over all provides ports
		for (ProvidesPortStub p : providesPortStubs) {
			addProvidesPortContainerShape(p, providesPortsContainerShape, providesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
		}
	}

	/**
	 * Adds a UsesPort shape to the usesPortsContainerShape
	 */
	public void addUsesPortContainerShape(UsesPortStub p, ContainerShape usesPortsContainerShape, int usesPortNameLength, IFeatureProvider featureProvider,
		Port externalPort) {
		// determine how many uses port are already there.
		int iter = 0;
		for (PropertyContainer child : DUtil.collectPropertyContainerChildren(usesPortsContainerShape)) {
			if (DUtil.isPropertyElementType(child, SHAPE_usesPortContainerShape)) {
				iter++;
			}
		}

		Diagram diagram = DUtil.findDiagram(usesPortsContainerShape);

		// port container
		ContainerShape usesPortContainerShape = Graphiti.getPeService().createContainerShape(usesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_usesPortContainerShape);
		Rectangle usesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(usesPortContainerShape);
		usesPortContainerShapeRectangle.setTransparency(1d);
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortContainerShapeRectangle,
			usesPortsContainerShape.getGraphicsAlgorithm().getWidth() - (PORT_SHAPE_WIDTH + usesPortNameLength), iter++ * (PORT_SHAPE_HEIGHT + 5),
			PORT_SHAPE_WIDTH + usesPortNameLength, PORT_SHAPE_HEIGHT);
		featureProvider.link(usesPortContainerShape, p);

		// port shape
		ContainerShape usesPortRectangleShape = Graphiti.getPeService().createContainerShape(usesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortRectangleShape, DUtil.SHAPE_TYPE, SHAPE_usesPortRectangleShape);
		Rectangle usesPortRectangle = Graphiti.getCreateService().createRectangle(usesPortRectangleShape);
		Graphiti.getPeService().setPropertyValue(usesPortRectangle, DUtil.GA_TYPE, GA_usesPortRectangle);
		featureProvider.link(usesPortRectangleShape, p);
		usesPortRectangle.setStyle(StyleUtil.getStyleForUsesPort(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortRectangle, usesPortNameLength, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

		// port text
		Shape usesPortTextShape = Graphiti.getPeService().createShape(usesPortContainerShape, false);
		Text usesPortText = Graphiti.getCreateService().createText(usesPortTextShape, p.getName());
		usesPortText.setStyle(StyleUtil.getStyleForUsesPort(diagram));
		usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		Graphiti.getPeService().setPropertyValue(usesPortText, DUtil.GA_TYPE, GA_usesPortText);
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortText, 0, 0,
			usesPortContainerShapeRectangle.getWidth() - (usesPortRectangle.getWidth() + PORT_NAME_HORIZONTAL_PADDING), 20);

		// fix point anchor
		FixPointAnchor fixPointAnchor = Graphiti.getPeService().createFixPointAnchor(usesPortRectangleShape);
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(PORT_SHAPE_WIDTH);
		point.setY(PORT_SHAPE_HEIGHT / 2);
		fixPointAnchor.setLocation(point);
		featureProvider.link(fixPointAnchor, p);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(usesPortRectangle);
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_fixPointAnchorRectangle);
		if (externalPort != null) {
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForExternalUsesPort(diagram));
			featureProvider.link(fixPointAnchor, externalPort); // link to externalPort so that update fires when it
																// changes
		} else {
			fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForUsesPort(diagram));
		}
		Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, -PORT_SHAPE_WIDTH, -PORT_SHAPE_HEIGHT / 2, PORT_SHAPE_WIDTH,
			PORT_SHAPE_HEIGHT);

	}

	/**
	 * Adds uses port container to provided container shape. Adds a port shape with name and anchor for each
	 * usesPortStub.
	 */
	public void addUsesPorts(ContainerShape outerContainerShape, EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// uses (output)
		int usesPortNameLength = DUtil.getLongestUsesPortWidth(usesPortStubs, DUtil.findDiagram(outerContainerShape));
		ContainerShape usesPortsContainerShape = Graphiti.getPeService().createContainerShape(outerContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_usesPortsContainerShape);
		Rectangle usesPortsRectangle = Graphiti.getCreateService().createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, DUtil.GA_TYPE, GA_usesPortsRectangle);
		Graphiti.getGaLayoutService().setSize(usesPortsRectangle, PORT_SHAPE_WIDTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING,
			usesPortStubs.size() * (PORT_SHAPE_HEIGHT));
		featureProvider.link(usesPortsContainerShape, usesPortStubs.toArray());

		// add uses ports
		for (UsesPortStub p : usesPortStubs) {
			addUsesPortContainerShape(p, usesPortsContainerShape, usesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
		}
	}

	/**
	 * Return the usesPortsContainerShape
	 */
	public ContainerShape getUsesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_usesPortsContainerShape);
	}

	/**
	 * Return the usesPortsContainerShape
	 */
	public ContainerShape getProvidesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_providesPortsContainerShape);
	}

	/**
	 * Returns usesPortsStubs business object list linked to getUsesPortsContainerShape()
	 */
	@SuppressWarnings("unchecked")
	public EList<UsesPortStub> getUsesPortStubs() {
		return (EList<UsesPortStub>) (EList< ? >) getUsesPortsContainerShape().getLink().getBusinessObjects();
	}

	/**
	 * Returns providesPortsStubs business object list linked to getProvidesPortsContainerShape()
	 */
	@SuppressWarnings("unchecked")
	public EList<ProvidesPortStub> getInternalProvidesPortStubs() {
		return (EList<ProvidesPortStub>) (EList< ? >) getProvidesPortsContainerShape().getLink().getBusinessObjects();
	}

	/**
	 * Return the text for outer container
	 */
	public Text getOuterText() {
		return (Text) DUtil.findFirstPropertyContainer(this, GA_outerRoundedRectangleText);
	}

	/**
	 * Return the image for outer container
	 */
	public Image getOuterImage() {
		return (Image) DUtil.findFirstPropertyContainer(this, GA_outerRoundedRectangleImage);
	}

	/**
	 * Return the text for inner container
	 */
	public Text getInnerText() {
		return (Text) DUtil.findFirstPropertyContainer(this, GA_innerRoundedRectangleText);
	}

	/**
	 * Return the image for inner container
	 */
	public Image getInnerImage() {
		return (Image) DUtil.findFirstPropertyContainer(this, GA_innerRoundedRectangleImage);
	}

	/**
	 * Return the inner container polyline
	 */
	public Polyline getInnerPolyline() {
		return (Polyline) DUtil.findFirstPropertyContainer(this, GA_innerRoundedRectangleLine);
	}

	/**
	 * Return the innerContainerShape
	 */
	public ContainerShape getInnerContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_innerContainerShape);
	}

	/**
	 * Return the lollipop container shape
	 * @return
	 */
	public ContainerShape getLollipop() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_interfaceContainerShape);
	}

	/**
	 * handles both determining whether an update is needed and performing an update for the shape.
	 * @return
	 */
	public Reason internalUpdate(String outerText, Object businessObject, IFeatureProvider featureProvider, String outerImageId, Style outerContainerStyle,
		String innerText, String innerImageId, Style innerContainerStyle, ComponentSupportedInterfaceStub interfaceStub, EList<UsesPortStub> uses,
		EList<ProvidesPortStub> provides, List<Port> externalPorts, boolean performUpdate) {

		boolean updateStatus = false;

		Diagram diagram = DUtil.findDiagram(this);

		// outerText
		Text outerTextGA = getOuterText();
		if (outerTextGA != null && !outerTextGA.getValue().equals(outerText)) {
			if (performUpdate) {
				outerTextGA.setValue(outerText);
			}
			return new Reason(true, "Outer title requires update");
		}

		// innerText
		Text innerTextGA = getInnerText();
		if (innerTextGA != null && innerTextGA.getValue() != null && !innerTextGA.getValue().equals(innerText)) {
			if (performUpdate) {
				innerTextGA.setValue(innerText);
			}
			return new Reason(true, "Inner title requires update");
		}

		// providesPortsContainerShape
		ContainerShape providesPortsContainerShape = getProvidesPortsContainerShape();
		if (providesPortsContainerShape != null && provides != null) {

			List<Text> providesPortTexts = new ArrayList<Text>();

			// capture all providesPortText values
			for (PropertyContainer providesPortContainerShape : DUtil.collectPropertyContainerChildren(providesPortsContainerShape)) {
				// if its a providesPortContainerShape
				if (DUtil.isPropertyElementType(providesPortContainerShape, SHAPE_providesPortContainerShape)) {
					// find all providesPortText, and fixPointAnchorRectangle
					for (PropertyContainer providesPortChild : DUtil.collectPropertyContainerChildren(providesPortContainerShape)) {
						// text?
						if (DUtil.isPropertyElementType(providesPortChild, GA_providesPortText)) {
							Text providesPortText = (Text) providesPortChild;
							providesPortTexts.add(providesPortText);
							// search for text in model
							boolean found = false;
							for (ProvidesPortStub portStub : provides) {
								if (portStub.getName().equals(providesPortText.getValue())) {
									found = true;
								}
							}
							if (!found) {
								// wasn't found, deleting this port
								if (performUpdate) {
									updateStatus = true;
									// delete shape
									EcoreUtil.delete(providesPortContainerShape);
								} else {
									return new Reason(true, "Provides ports requires update");
								}
							}
						}
						if (DUtil.isPropertyElementType(providesPortChild, GA_fixPointAnchorRectangle)) {
							Rectangle fixPointAnchorRectangle = (Rectangle) providesPortChild;
							// get business object linked to fixPointAnchor
							Object portObject = DUtil.getBusinessObject(fixPointAnchorRectangle.getPictogramElement());
							if (portObject != null) {
								// ProvidesPortStub
								if (isExternalPort(portObject, externalPorts)) {
									// external port
									if (!fixPointAnchorRectangle.getStyle().equals(StyleUtil.getStyleForExternalProvidesPort(DUtil.findDiagram(this)))) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForExternalProvidesPort(DUtil.findDiagram(this)));
											featureProvider.link(fixPointAnchorRectangle.getPictogramElement(), findExternalPort(portObject, externalPorts)); // link
																																								// to
																																								// externalPort
																																								// so
																																								// that
																																								// update
																																								// fires
																																								// when
																																								// it
																																								// changes
										} else {
											return new Reason(true, "Port style requires update");
										}
									}
								} else {
									// non-external port
									if (!fixPointAnchorRectangle.getStyle().equals(StyleUtil.getStyleForProvidesPort(DUtil.findDiagram(this)))) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForProvidesPort(DUtil.findDiagram(this)));
										} else {
											return new Reason(true, "Port style requires update");
										}
									}
								}
							}
						}
					}
				}
			}
			// check number provides ports changed
			if (provides.size() != providesPortTexts.size()) {
				// add new provides ports
				int providesPortNameLength = DUtil.getLongestProvidesPortWidth(provides, diagram);
				for (ProvidesPortStub p : provides) {
					// search to see if p exists in diagram
					boolean found = false;
					for (Text portText : providesPortTexts) {
						if (p.getName().equals(portText.getValue())) {
							found = true;
						}
					}
					// add p to diagram
					if (!found) {
						if (performUpdate) {
							updateStatus = true;
							addProvidesPortContainerShape(p, providesPortsContainerShape, providesPortNameLength, featureProvider,
								findExternalPort(p, externalPorts));
						} else {
							return new Reason(true, "Provides ports requires update");
						}
					}
				}
			}
		}

		// usesPortsContainerShape
		ContainerShape usesPortsContainerShape = getUsesPortsContainerShape();
		if (usesPortsContainerShape != null && uses != null && uses.size() > 0) {

			List<Text> usesPortTexts = new ArrayList<Text>();

			// verify uses port quantity and text haven't changed
			// find THE usesPortContainerShape
			for (PropertyContainer usesPortContainerShape : DUtil.collectPropertyContainerChildren(usesPortsContainerShape)) {
				// if its a usesPortContainerShape
				if (DUtil.isPropertyElementType(usesPortContainerShape, SHAPE_usesPortContainerShape)) {
					// find all usesPortText
					for (PropertyContainer usesPortChild : DUtil.collectPropertyContainerChildren(usesPortContainerShape)) {
						// compare text
						if (DUtil.isPropertyElementType(usesPortChild, GA_usesPortText)) {
							Text usesPortText = (Text) usesPortChild;
							usesPortTexts.add(usesPortText);
							// search for text in model
							boolean found = false;
							for (UsesPortStub portStub : uses) {
								if (portStub.getName().equals(usesPortText.getValue())) {
									found = true;
								}
							}
							if (!found) {
								if (performUpdate) {
									updateStatus = true;
									// delete shape
									EcoreUtil.delete(usesPortContainerShape);
								} else {
									return new Reason(true, "Uses ports requires update");
								}
							}
						}
						if (DUtil.isPropertyElementType(usesPortChild, GA_fixPointAnchorRectangle)) {
							Rectangle fixPointAnchorRectangle = (Rectangle) usesPortChild;
							// get business object linked to fixPointAnchor
							Object portObject = DUtil.getBusinessObject(fixPointAnchorRectangle.getPictogramElement());
							if (portObject != null) {
								// usesPortStub
								if (isExternalPort(portObject, externalPorts)) {
									// external port
									if (!fixPointAnchorRectangle.getStyle().equals(StyleUtil.getStyleForExternalUsesPort(DUtil.findDiagram(this)))) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForExternalUsesPort(DUtil.findDiagram(this)));
											fixPointAnchorRectangle.getPictogramElement().getLink().getBusinessObjects().add(
												findExternalPort(portObject, externalPorts));// link to externalPort so
																								// that update fires
																								// when it changes
										} else {
											return new Reason(true, "Port style requires update");
										}
									}
								} else {
									// non-external port
									if (!fixPointAnchorRectangle.getStyle().equals(StyleUtil.getStyleForUsesPort(DUtil.findDiagram(this)))) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.getStyleForUsesPort(DUtil.findDiagram(this)));
											// this line will actually remove existing links (which will include an
											// external port) and simply add the portObject (which already existed)
											featureProvider.link(fixPointAnchorRectangle.getPictogramElement(), portObject);
										} else {
											return new Reason(true, "Port style requires update");
										}
									}
								}
							}
						}
					}
				}
			}
			// check number uses ports changed
			if (uses.size() != usesPortTexts.size()) {
				// add new uses ports
				int usesPortNameLength = DUtil.getLongestUsesPortWidth(uses, diagram);
				for (UsesPortStub p : uses) {
					// search to see if p exists in diagram
					boolean found = false;
					for (Text portText : usesPortTexts) {
						if (p.getName().equals(portText.getValue())) {
							found = true;
						}
					}
					// add p to diagram
					if (!found) {
						if (performUpdate) {
							updateStatus = true;
							addUsesPortContainerShape(p, usesPortsContainerShape, usesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
						} else {
							return new Reason(true, "Uses ports requires update");
						}
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update successful");
		}

		return new Reason(false, "No updates required");

	}

	/**
	 * Returns true if the portObject identifier is listed in the externalPorts list
	 * @param portObject
	 * @param externalPorts
	 * @return
	 */
	public static boolean isExternalPort(Object portObject, List<Port> externalPorts) {
		if (findExternalPort(portObject, externalPorts) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns external port if the portObject identifier is listed in the externalPorts list
	 * @param portObject
	 * @param externalPorts
	 * @return
	 */
	public static Port findExternalPort(Object portObject, List<Port> externalPorts) {
		if (externalPorts != null) {
			if (portObject instanceof UsesPortStub) {
				UsesPortStub usesPort = (UsesPortStub) portObject;
				for (Port p : externalPorts) {
					if (p.getUsesIdentifier() != null && p.getUsesIdentifier().equals(usesPort.getName())) {
						return p;
					}
				}
			}
			if (portObject instanceof ProvidesPortStub) {
				ProvidesPortStub usesPort = (ProvidesPortStub) portObject;
				for (Port p : externalPorts) {
					if (p.getProvidesIndentifier() != null && p.getProvidesIndentifier().equals(usesPort.getName())) {
						return p;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Returns minimum width for Shape with provides and uses port stubs and name text
	 * @param ci
	 * @return
	 */
	public int getMinimumWidth(final String outerTitle, final String innerTitle, final EList<ProvidesPortStub> providesPortStubs,
		final EList<UsesPortStub> usesPortStubs) {

		int portsWidth = 0;
		int innerTitleWidth = 0;
		int outerTitleWidth = 0;
		Diagram diagram = DUtil.findDiagram(this);

		int usesWidth = PORT_SHAPE_WIDTH + DUtil.getLongestUsesPortWidth(usesPortStubs, diagram) + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_WIDTH;
		int providesWidth = PORT_SHAPE_WIDTH + DUtil.getLongestProvidesPortWidth(providesPortStubs, diagram);

		portsWidth = usesWidth + providesWidth + REQ_PADDING_BETWEEN_PORT_TYPES;

		// inner title (including start order)
		IDimension innerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(innerTitle, StyleUtil.getInnerTitleFont(diagram));
		innerTitleWidth = innerTitleDimension.getWidth() + INTERFACE_SHAPE_WIDTH + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING;

		// outer title
		IDimension outerTitleDimension = GraphitiUi.getUiLayoutService().calculateTextSize(outerTitle, StyleUtil.getOuterTitleFont(diagram));
		outerTitleWidth = INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + outerTitleDimension.getWidth() + INTERFACE_SHAPE_WIDTH
			+ OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING;

		// return the largest
		int largestWidth = portsWidth;
		if (largestWidth < innerTitleWidth) {
			largestWidth = innerTitleWidth;
		}
		if (largestWidth < outerTitleWidth) {
			largestWidth = outerTitleWidth;
		}
		return largestWidth;
	}

	/**
	 * Determine the height by which we need to expand by comparing the number of uses and provides ports and return the
	 * largest
	 * @return int Return the length by which we need to expand the height of the associated Shape
	 */
	private static int getAdjustedHeight(final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs) {
		;

		int numPorts = Math.max(providesPortStubs.size(), usesPortStubs.size());

		return numPorts;
	}

	/**
	 * Returns minimum height for Shape containing provides and uses ports
	 * @param ci
	 * @return
	 */
	private static int getMinimumHeight(final EList<ProvidesPortStub> providesPortStubs, final EList<UsesPortStub> usesPortStubs) {
		return getAdjustedHeight(providesPortStubs, usesPortStubs) * (PORT_ROW_HEIGHT + PORT_ROW_PADDING_HEIGHT) + PORTS_CONTAINER_SHAPE_TOP_PADDING + 10;
	}

	/**
	 * Returns a instance of RHContainerShape that contains the provide PictogramElement
	 * @param pe
	 * @return
	 */
	public static RHContainerShape findFromChild(PictogramElement pe) {
		ContainerShape cs = DUtil.findContainerShapeParentWithProperty(pe, SHAPE_outerContainerShape);
		if (cs instanceof RHContainerShape) {
			return (RHContainerShape) cs;
		}
		return null;
	}

} // RHContainerShapeImpl
