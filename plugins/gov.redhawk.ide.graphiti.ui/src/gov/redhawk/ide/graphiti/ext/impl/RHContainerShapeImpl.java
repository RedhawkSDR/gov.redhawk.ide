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
package gov.redhawk.ide.graphiti.ext.impl;

import gov.redhawk.ide.graphiti.ext.Event;
import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
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
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isStarted <em>Started</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#getIStatusErrorState <em>IStatus Error State</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#getEvent <em>Event</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
@SuppressWarnings("restriction")
public class RHContainerShapeImpl extends ContainerShapeImpl implements RHContainerShape {
	/**
	 * The default value of the '{@link #isStarted() <em>Started</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStarted()
	 * @generated
	 * @ordered
	 */
	protected static final boolean STARTED_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isStarted() <em>Started</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isStarted()
	 * @generated
	 * @ordered
	 */
	protected boolean started = STARTED_EDEFAULT;

	/**
	 * The default value of the '{@link #getIStatusErrorState() <em>IStatus Error State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIStatusErrorState()
	 * @generated NOT
	 * @ordered
	 */
	protected static final int ISTATUS_ERROR_STATE_EDEFAULT = IStatus.OK;

	/**
	 * The cached value of the '{@link #getIStatusErrorState() <em>IStatus Error State</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIStatusErrorState()
	 * @generated
	 * @ordered
	 */
	protected int iStatusErrorState = ISTATUS_ERROR_STATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getEvent() <em>Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEvent()
	 * @generated
	 * @ordered
	 */
	protected static final Event EVENT_EDEFAULT = Event.RELEASE;

	/**
	 * The cached value of the '{@link #getEvent() <em>Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEvent()
	 * @generated
	 * @ordered
	 */
	protected Event event = EVENT_EDEFAULT;

	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_OUTER_ROUNDED_RECTANGLE = "outerRoundedRectangle", GA_INNER_ROUNDED_RECTANGLE = "innerRoundedRectangle",
			GA_OUTER_ROUNDED_RECTANGLE_TEXT = "outerRoundedRectangleText", GA_INNER_ROUNDED_RECTANGLE_TEXT = "innerRoundedRectangleText",
			GA_OUTER_ROUNDED_RECTANGLE_IMAGE = "outerRoundedRectangleImage", GA_INNER_ROUNDED_RECTANGLE_IMAGE = "innerRoundedRectangleImage",
			GA_INNER_ROUNDED_RECTANGLE_LINE = "innerRoundedRectangleLine", GA_PROVIDES_PORT_RECTANGLE = "providesPortsRectangle",
			GA_PROVIDES_PORT_TEXT = "GA_PROVIDES_PORT_TEXT", GA_FIX_POINT_ANCHOR_RECTANGLE = "GA_FIX_POINT_ANCHOR_RECTANGLE",
			GA_USES_PORTS_RECTANGLE = "usesPortsRectangle", GA_USES_PORT_RECTANGLE = "usesPortRectangle", GA_USES_PORT_TEXT = "GA_USES_PORT_TEXT";

	// Property key/value pairs help us identify Shapes to enable/disable user actions
	// (move, resize, delete, remove, etc.)
	public static final String SHAPE_OUTER_CONTAINER = "outerContainerShape", SHAPE_INNER_CONTAINER = "innerContainerShape",
			SHAPE_USES_PORTS_CONTAINER = "usesPortsContainerShape", SHAPE_PROVIDES_PORTS_CONTAINER = "providesPortsContainerShape",
			SHAPE_USES_PORT_CONTAINER = "usesPortContainerShape", SHAPE_PROVIDES_PORT_CONTAINER = "providesPortContainerShape",
			SHAPE_USES_PORT_RECTANGLE = "usesPortRectangleShape", SHAPE_PROVIDES_PORT_RECTANGLE = "providesPortRectangleShape",
			SHAPE_INTERFACE_CONTAINER = "interfaceContainerShape", SHAPE_INTERFACE_ELLIPSE = "interfaceEllipseShape";

	// Shape size constants
	public static final int OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING = 10, INNER_CONTAINER_SHAPE_TOP_PADDING = 20,
			INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15, INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING = 60, PROVIDES_PORTS_LEFT_PADDING = 5,
			INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING,
			PORTS_CONTAINER_SHAPE_TOP_PADDING = 60, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING = 8, INNER_ROUNDED_RECTANGLE_LINE_Y = 28, NAME_CHAR_WIDTH = 7,
			LABEL_CHAR_WIDTH = 7, PORT_NAME_HORIZONTAL_PADDING = 5, PORT_ROW_HEIGHT = 15, PORT_ROW_PADDING_HEIGHT = 5, PORT_SHAPE_HEIGHT = 15,
			PORT_SHAPE_WIDTH = 15, PORT_CHAR_WIDTH = 7, LOLLIPOP_ELLIPSE_DIAMETER = 10, INTERFACE_SHAPE_WIDTH = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING
				+ PROVIDES_PORTS_LEFT_PADDING, INTERFACE_SHAPE_HEIGHT = 10, REQ_PADDING_BETWEEN_PORT_TYPES = 0, ICON_IMAGE_LENGTH = 16;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setStarted(boolean newStarted) {
		boolean oldStarted = started;
		started = newStarted;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__STARTED, oldStarted, started));

		// update color according to value
		final Diagram diagram = DUtil.findDiagram(this);
		RoundedRectangle innerRoundedRectangle = (RoundedRectangle) getInnerContainerShape().getGraphicsAlgorithm();
		if (innerRoundedRectangle != null) {
			if (newStarted) {
				// started
				innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInnerStarted(diagram));
			} else {
				// not started
				innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInner(diagram));
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIStatusErrorState() {
		return iStatusErrorState;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setIStatusErrorState(int newIStatusErrorState) {
		int oldIStatusErrorState = iStatusErrorState;
		iStatusErrorState = newIStatusErrorState;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE, oldIStatusErrorState, iStatusErrorState));
		
		// update color according to value
		final Diagram diagram = DUtil.findDiagram(this);
		RoundedRectangle innerRoundedRectangle = (RoundedRectangle) getInnerContainerShape().getGraphicsAlgorithm();
		if (innerRoundedRectangle != null) {
			if (iStatusErrorState == IStatus.ERROR) {
				// errored
				innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInnerError(diagram));
			} else {
				// not errored
				innerRoundedRectangle.setStyle(StyleUtil.createStyleForComponentInner(diagram));
			}
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Event getEvent() {
		return event;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEvent(Event newEvent) {
		Event oldEvent = event;
		event = newEvent == null ? EVENT_EDEFAULT : newEvent;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__EVENT, oldEvent, event));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public void init(IAddContext context, AbstractContainerPattern pattern) {
		init(context, pattern, null);
	}

	/**
	 * Creates the inner shapes that make up this container shape
	 */
	public void init(final IAddContext context, AbstractContainerPattern pattern, List<Port> externalPorts) {
		EObject newObject = (EObject) context.getNewObject();
		ContainerShape targetContainerShape = (ContainerShape) context.getTargetContainer();

		getProperties().addAll(new ArrayList<Property>(0));
		setVisible(true);
		setActive(true);
		setContainer(targetContainerShape);

		// add property for this shape
		Graphiti.getPeService().setPropertyValue(this, DUtil.GA_TYPE, SHAPE_OUTER_CONTAINER);

		// graphic
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(this, 5, 5);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE);
		outerRoundedRectangle.setStyle(pattern.createStyleForOuter());
		outerRoundedRectangle.setTransparency(0.5); // 50%

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, pattern.getOuterImageId());
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_IMAGE);

		// text
		Text cText = Graphiti.getCreateService().createText(outerRoundedRectangle, pattern.getOuterTitle(newObject));
		cText.setStyle(StyleUtil.createStyleForOuterText(DUtil.findDiagram(targetContainerShape)));
		Graphiti.getPeService().setPropertyValue(cText, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_TEXT);

		IFeatureProvider featureProvider = pattern.getFeatureProvider();

		// link objects
		featureProvider.link(this, pattern.getBusinessObjectsToLink(newObject).toArray());

		addInnerContainer(pattern.getInnerTitle(newObject), pattern.getInnerImageId(), pattern.createStyleForInner());
		addLollipop(pattern.getInterface(newObject), featureProvider);
		addProvidesPorts(pattern.getProvides(newObject), featureProvider, externalPorts);
		addUsesPorts(pattern.getUses(newObject), featureProvider, externalPorts);
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
	public Reason update(final IUpdateContext context, AbstractContainerPattern pattern, final List<Port> externalPorts) {
		return internalUpdate(pattern, (EObject) DUtil.getBusinessObject(context.getPictogramElement()), externalPorts, true);
	}

	/**
	 * Updates the shape's contents using the supplied fields. Return true if an update occurred, false otherwise.
	 */
	public Reason update(IUpdateContext context, AbstractContainerPattern pattern) {
		return update(context, pattern, null);
	}

	/**
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 */
	public Reason updateNeeded(final IUpdateContext context, final AbstractContainerPattern pattern, final List<Port> externalPorts) {
		return internalUpdate(pattern, (EObject) DUtil.getBusinessObject(context.getPictogramElement()), externalPorts, false);
	}

	/**
	 * Return true (through Reason) if the shape's contents require an update based on the field supplied.
	 * Also returns a textual reason why an update is needed. Returns false otherwise.
	 */
	public Reason updateNeeded(final IUpdateContext context, AbstractContainerPattern pattern) {
		return updateNeeded(context, pattern, null);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
			return isStarted();
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE:
			return getIStatusErrorState();
		case RHGxPackage.RH_CONTAINER_SHAPE__EVENT:
			return getEvent();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
			setStarted((Boolean) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE:
			setIStatusErrorState((Integer) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__EVENT:
			setEvent((Event) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
			setStarted(STARTED_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE:
			setIStatusErrorState(ISTATUS_ERROR_STATE_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__EVENT:
			setEvent(EVENT_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
			return started != STARTED_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE:
			return iStatusErrorState != ISTATUS_ERROR_STATE_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__EVENT:
			return event != EVENT_EDEFAULT;
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (started: ");
		result.append(started);
		result.append(", iStatusErrorState: ");
		result.append(iStatusErrorState);
		result.append(", event: ");
		result.append(event);
		result.append(')');
		return result.toString();
	}

	/**
	 * add inner container
	 */
	public ContainerShape addInnerContainer(String text, String imageId, Style containerStyle) {
		ContainerShape innerContainerShape = Graphiti.getCreateService().createContainerShape(this, false);
		Graphiti.getPeService().setPropertyValue(innerContainerShape, DUtil.GA_TYPE, SHAPE_INNER_CONTAINER);
		RoundedRectangle innerRoundedRectangle = Graphiti.getCreateService().createRoundedRectangle(innerContainerShape, 5, 5);
		innerRoundedRectangle.setStyle(containerStyle);
		Graphiti.getPeService().setPropertyValue(innerRoundedRectangle, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE);

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(innerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_IMAGE);

		// text
		Text ciText = Graphiti.getCreateService().createText(innerRoundedRectangle, text);
		ciText.setStyle(StyleUtil.createStyleForInnerText(DUtil.findDiagram(this)));
		Graphiti.getPeService().setPropertyValue(ciText, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_TEXT);

		// line
		Polyline polyline = Graphiti.getGaCreateService().createPolyline(innerRoundedRectangle,
			new int[] { 0, INNER_ROUNDED_RECTANGLE_LINE_Y, innerRoundedRectangle.getWidth(), INNER_ROUNDED_RECTANGLE_LINE_Y });
		polyline.setLineWidth(1);
		polyline.setBackground(Graphiti.getGaService().manageColor(DUtil.findDiagram(this), StyleUtil.BLACK));
		polyline.setForeground(Graphiti.getGaService().manageColor(DUtil.findDiagram(this), StyleUtil.BLACK));
		Graphiti.getPeService().setPropertyValue(polyline, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_LINE);

		return innerContainerShape;
	}

	/**
	 * Add lollipop to targetContainerShape. Lollipop anchor will link to the provided business object.T
	 */
	public ContainerShape addLollipop(Object anchorBusinessObject, IFeatureProvider featureProvider) {

		// interface container lollipop
		ContainerShape interfaceContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(interfaceContainerShape, DUtil.GA_TYPE, SHAPE_INTERFACE_CONTAINER);
		Rectangle interfaceRectangle = Graphiti.getCreateService().createRectangle(interfaceContainerShape);
		featureProvider.link(interfaceContainerShape, anchorBusinessObject);
		interfaceRectangle.setTransparency(.99d);
		Graphiti.getGaLayoutService().setLocationAndSize(interfaceRectangle, 0, 25, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);

		// interface lollipop ellipse
		Shape lollipopEllipseShape = Graphiti.getCreateService().createShape(interfaceContainerShape, true);
		Graphiti.getPeService().setPropertyValue(lollipopEllipseShape, DUtil.GA_TYPE, SHAPE_INTERFACE_ELLIPSE);
		Ellipse lollipopEllipse = Graphiti.getCreateService().createEllipse(lollipopEllipseShape);
		lollipopEllipse.setStyle(StyleUtil.createStyleForLollipopEllipse(DUtil.findDiagram(this)));
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopEllipse, 0, 0, LOLLIPOP_ELLIPSE_DIAMETER, LOLLIPOP_ELLIPSE_DIAMETER);

		// interface lollipop line
		Shape lollipopLineShape = Graphiti.getCreateService().createContainerShape(interfaceContainerShape, false);
		Rectangle lollipopLine = Graphiti.getCreateService().createRectangle(lollipopLineShape);
		lollipopLine.setStyle(StyleUtil.createStyleForLollipopLine(DUtil.findDiagram(this)));
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
	private void addProvidesPortContainerShape(ProvidesPortStub p, ContainerShape providesPortsContainerShape, int providesPortNameLength,
		IFeatureProvider featureProvider, Port externalPort) {

		// determine how many provides port are already there.
		int iter = 0;
		for (PropertyContainer child : DUtil.collectPropertyContainerChildren(providesPortsContainerShape)) {
			if (DUtil.isPropertyElementType(child, SHAPE_PROVIDES_PORT_CONTAINER)) {
				iter++;
			}
		}

		Diagram diagram = DUtil.findDiagram(providesPortsContainerShape);

		ContainerShape providesPortContainerShape = Graphiti.getCreateService().createContainerShape(providesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORT_CONTAINER);// ref
		// prevent
		// selection/deletion/removal
		Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(providesPortContainerShape);
		providesPortContainerShapeRectangle.setTransparency(1d);
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortContainerShapeRectangle, 0, iter++ * (PORT_SHAPE_HEIGHT + 5),
			PORT_SHAPE_WIDTH + providesPortNameLength, PORT_SHAPE_HEIGHT);
		featureProvider.link(providesPortContainerShape, p);

		// port shape
		ContainerShape providesPortRectangleShape = Graphiti.getCreateService().createContainerShape(providesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortRectangleShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORT_RECTANGLE);// ref
		// prevent
		// move
		Rectangle providesPortRectangle = Graphiti.getCreateService().createRectangle(providesPortRectangleShape);
		featureProvider.link(providesPortRectangleShape, p);
		providesPortRectangle.setStyle(StyleUtil.createStyleForProvidesPort(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortRectangle, 0, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

		// port text
		Shape providesPortTextShape = Graphiti.getCreateService().createShape(providesPortContainerShape, false);
		Text providesPortText = Graphiti.getCreateService().createText(providesPortTextShape, p.getName());
		providesPortText.setStyle(StyleUtil.createStyleForProvidesPort(diagram));
		Graphiti.getPeService().setPropertyValue(providesPortText, DUtil.GA_TYPE, GA_PROVIDES_PORT_TEXT);
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
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_FIX_POINT_ANCHOR_RECTANGLE);
		if (externalPort != null) {
			fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForExternalProvidesPort(diagram));
			featureProvider.link(fixPointAnchor, externalPort); // link to externalPort so that update fires when it
			// changes
		} else {
			fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForProvidesPort(diagram));
		}
		Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, 0, -PORT_SHAPE_HEIGHT / 2, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

	}

	/**
	 * Adds provides port container to provided container shape. Adds a port shape with name and anchor for each
	 * providesPortStub.
	 */
	private void addProvidesPorts(EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {

		// provides (input)
		int providesPortNameLength = DUtil.getLongestProvidesPortWidth(providesPortStubs, DUtil.findDiagram(this));
		ContainerShape providesPortsContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORTS_CONTAINER);
		Rectangle providesPortsRectangle = Graphiti.getCreateService().createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, DUtil.GA_TYPE, GA_PROVIDES_PORT_RECTANGLE);
		int width = PORT_SHAPE_WIDTH + providesPortNameLength;
		int height = (providesPortStubs != null) ? providesPortStubs.size() * (PORT_ROW_HEIGHT + PORT_ROW_PADDING_HEIGHT) : 0;
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortsRectangle, PROVIDES_PORTS_LEFT_PADDING, PORTS_CONTAINER_SHAPE_TOP_PADDING, width, height);

		if (providesPortStubs != null) {
			featureProvider.link(providesPortsContainerShape, providesPortStubs.toArray());

			// iterate over all provides ports
			for (ProvidesPortStub p : providesPortStubs) {
				addProvidesPortContainerShape(p, providesPortsContainerShape, providesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
			}
		}
	}

	/**
	 * Adds provides ports if an RHContainerShape is edited after initial creation
	 */
	public void addNewProvidesPorts(EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// Manually clean up any links
		for (Shape child : getProvidesPortsContainerShape().getChildren()) {
			EcoreUtil.delete(child.getLink()); // provides port individual container link
			EcoreUtil.delete(((ContainerShape) child).getChildren().get(0).getLink()); // provides port shape link
			EcoreUtil.delete(((ContainerShape) child).getChildren().get(0).getAnchors().get(0).getLink()); // anchor
		}

		// Manually clean up the provides port parent container. Easier to just rebuild from scratch
		EcoreUtil.delete(getProvidesPortsContainerShape().getLink());
		EcoreUtil.delete(getProvidesPortsContainerShape());

		int providesPortNameLength = DUtil.getLongestProvidesPortWidth(providesPortStubs, DUtil.findDiagram(this));
		ContainerShape providesPortsContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORTS_CONTAINER);
		Rectangle providesPortsRectangle = Graphiti.getCreateService().createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, DUtil.GA_TYPE, GA_PROVIDES_PORT_RECTANGLE);

		int height = (providesPortStubs != null) ? providesPortStubs.size() * (PORT_ROW_HEIGHT + PORT_ROW_PADDING_HEIGHT) : 0;
		Graphiti.getGaLayoutService().setLocationAndSize(providesPortsRectangle, PROVIDES_PORTS_LEFT_PADDING, PORTS_CONTAINER_SHAPE_TOP_PADDING,
			PORT_SHAPE_WIDTH + providesPortNameLength, height);

		if (providesPortStubs != null) {
			// Reset links
			featureProvider.link(providesPortsContainerShape, providesPortStubs.toArray());
			for (ProvidesPortStub p : providesPortStubs) {
				addProvidesPortContainerShape(p, providesPortsContainerShape, providesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
			}
		}
	}

	/**
	 * Adds a UsesPort shape to the usesPortsContainerShape
	 */
	private void addUsesPortContainerShape(UsesPortStub p, ContainerShape usesPortsContainerShape, int usesPortNameLength, IFeatureProvider featureProvider,
		Port externalPort) {
		// determine how many uses port are already there.
		int iter = 0;
		for (PropertyContainer child : DUtil.collectPropertyContainerChildren(usesPortsContainerShape)) {
			if (DUtil.isPropertyElementType(child, SHAPE_USES_PORT_CONTAINER)) {
				iter++;
			}
		}

		Diagram diagram = DUtil.findDiagram(usesPortsContainerShape);

		// port container
		ContainerShape usesPortContainerShape = Graphiti.getPeService().createContainerShape(usesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORT_CONTAINER);
		Rectangle usesPortContainerShapeRectangle = Graphiti.getCreateService().createRectangle(usesPortContainerShape);
		usesPortContainerShapeRectangle.setTransparency(1d);
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortContainerShapeRectangle,
			usesPortsContainerShape.getGraphicsAlgorithm().getWidth() - (PORT_SHAPE_WIDTH + usesPortNameLength), iter++ * (PORT_SHAPE_HEIGHT + 5),
			PORT_SHAPE_WIDTH + usesPortNameLength, PORT_SHAPE_HEIGHT);
		featureProvider.link(usesPortContainerShape, p);

		// port shape
		ContainerShape usesPortRectangleShape = Graphiti.getPeService().createContainerShape(usesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortRectangleShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORT_RECTANGLE);
		Rectangle usesPortRectangle = Graphiti.getCreateService().createRectangle(usesPortRectangleShape);
		Graphiti.getPeService().setPropertyValue(usesPortRectangle, DUtil.GA_TYPE, GA_USES_PORT_RECTANGLE);
		featureProvider.link(usesPortRectangleShape, p);
		usesPortRectangle.setStyle(StyleUtil.createStyleForUsesPort(diagram));
		Graphiti.getGaLayoutService().setLocationAndSize(usesPortRectangle, usesPortNameLength, 0, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);

		// port text
		Shape usesPortTextShape = Graphiti.getPeService().createShape(usesPortContainerShape, false);
		Text usesPortText = Graphiti.getCreateService().createText(usesPortTextShape, p.getName());
		usesPortText.setStyle(StyleUtil.createStyleForUsesPort(diagram));
		usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		Graphiti.getPeService().setPropertyValue(usesPortText, DUtil.GA_TYPE, GA_USES_PORT_TEXT);
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
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_FIX_POINT_ANCHOR_RECTANGLE);
		if (externalPort != null) {
			fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForExternalUsesPort(diagram));
			featureProvider.link(fixPointAnchor, externalPort); // link to externalPort so that update fires when it
			// changes
		} else {
			fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForUsesPort(diagram));
		}
		Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, -PORT_SHAPE_WIDTH, -PORT_SHAPE_HEIGHT / 2, PORT_SHAPE_WIDTH,
			PORT_SHAPE_HEIGHT);

	}

	/**
	 * Adds uses port container to provided container shape. Adds a port shape with name and anchor for each
	 * usesPortStub.
	 */
	private void addUsesPorts(EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// uses (output)
		int usesPortNameLength = DUtil.getLongestUsesPortWidth(usesPortStubs, DUtil.findDiagram(this));
		ContainerShape usesPortsContainerShape = Graphiti.getPeService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORTS_CONTAINER);
		Rectangle usesPortsRectangle = Graphiti.getCreateService().createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, DUtil.GA_TYPE, GA_USES_PORTS_RECTANGLE);

		int height = (usesPortStubs != null) ? usesPortStubs.size() * (PORT_SHAPE_HEIGHT) : 0;
		Graphiti.getGaLayoutService().setSize(usesPortsRectangle, PORT_SHAPE_WIDTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, height);

		if (usesPortStubs != null) {
			featureProvider.link(usesPortsContainerShape, usesPortStubs.toArray());
			// add uses ports
			for (UsesPortStub p : usesPortStubs) {
				addUsesPortContainerShape(p, usesPortsContainerShape, usesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
			}
		}
	}

	/**
	 * Adds uses ports if an RHContainerShape is edited after initial creation
	 */
	public void addNewUsesPorts(EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// Manually clean up any links
		for (Shape child : getUsesPortsContainerShape().getChildren()) {
			EcoreUtil.delete(child.getLink()); // uses port individual container link
			EcoreUtil.delete(((ContainerShape) child).getChildren().get(0).getLink()); // uses port shape link
			EcoreUtil.delete(((ContainerShape) child).getChildren().get(0).getAnchors().get(0).getLink()); // anchor
		}

		// Manually clean up the uses port parent container. Easier to just rebuild from scratch
		EcoreUtil.delete(getUsesPortsContainerShape().getLink());
		EcoreUtil.delete(getUsesPortsContainerShape());

		int usesPortNameLength = DUtil.getLongestUsesPortWidth(usesPortStubs, DUtil.findDiagram(this));
		ContainerShape usesPortsContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORTS_CONTAINER);
		Rectangle usesPortsRectangle = Graphiti.getCreateService().createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, DUtil.GA_TYPE, GA_USES_PORTS_RECTANGLE);

		int height = (usesPortStubs != null) ? usesPortStubs.size() * (PORT_SHAPE_HEIGHT) : 0;
		Graphiti.getGaLayoutService().setSize(usesPortsRectangle, PORT_SHAPE_WIDTH + usesPortNameLength + PORT_NAME_HORIZONTAL_PADDING, height);

		if (usesPortStubs != null) {
			// Reset links
			featureProvider.link(usesPortsContainerShape, usesPortStubs.toArray());
			for (UsesPortStub p : usesPortStubs) {
				addUsesPortContainerShape(p, usesPortsContainerShape, usesPortNameLength, featureProvider, findExternalPort(p, externalPorts));
			}
		}
	}

	/**
	 * Return the usesPortsContainerShape
	 */
	public ContainerShape getUsesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_USES_PORTS_CONTAINER);
	}

	/**
	 * Return the providesPortsContainerShape
	 */
	public ContainerShape getProvidesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_PROVIDES_PORTS_CONTAINER);
	}

	/**
	 * Returns usesPortsStubs business object list linked to getUsesPortsContainerShape()
	 */
	@SuppressWarnings("unchecked")
	public EList<UsesPortStub> getUsesPortStubs() {
		ContainerShape usesPortsContainerShape = getUsesPortsContainerShape();
		if (usesPortsContainerShape.getLink() != null) {
			return (EList<UsesPortStub>) (EList< ? >) usesPortsContainerShape.getLink().getBusinessObjects();
		}
		return null;
	}

	/**
	 * Returns providesPortsStubs business object list linked to getProvidesPortsContainerShape()
	 */
	@SuppressWarnings("unchecked")
	public EList<ProvidesPortStub> getInternalProvidesPortStubs() {
		ContainerShape providesPortsContainerShape = getProvidesPortsContainerShape();
		if (providesPortsContainerShape.getLink() != null) {
			return (EList<ProvidesPortStub>) (EList< ? >) providesPortsContainerShape.getLink().getBusinessObjects();
		}
		return null;
	}

	/**
	 * Return the text for outer container
	 */
	public Text getOuterText() {
		return (Text) DUtil.findFirstPropertyContainer(this, GA_OUTER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Return the image for outer container
	 */
	public Image getOuterImage() {
		return (Image) DUtil.findFirstPropertyContainer(this, GA_OUTER_ROUNDED_RECTANGLE_IMAGE);
	}

	/**
	 * Return the text for inner container
	 */
	public Text getInnerText() {
		return (Text) DUtil.findFirstPropertyContainer(this, GA_INNER_ROUNDED_RECTANGLE_TEXT);
	}

	/**
	 * Return the image for inner container
	 */
	public Image getInnerImage() {
		return (Image) DUtil.findFirstPropertyContainer(this, GA_INNER_ROUNDED_RECTANGLE_IMAGE);
	}

	/**
	 * Return the inner container polyline
	 */
	public Polyline getInnerPolyline() {
		return (Polyline) DUtil.findFirstPropertyContainer(this, GA_INNER_ROUNDED_RECTANGLE_LINE);
	}

	/**
	 * Return the innerContainerShape
	 */
	public ContainerShape getInnerContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_INNER_CONTAINER);
	}

	/**
	 * Return the lollipop container shape
	 * @return
	 */
	public ContainerShape getLollipop() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SHAPE_INTERFACE_CONTAINER);
	}

	/**
	 * handles both determining whether an update is needed and performing an update for the shape.
	 * @return
	 */
	public Reason internalUpdate(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts, boolean performUpdate) {

		boolean updateStatus = false;

		IFeatureProvider featureProvider = pattern.getFeatureProvider();
		Diagram diagram = DUtil.findDiagram(this);

		EList<ProvidesPortStub> provides = pattern.getProvides(businessObject);
		EList<UsesPortStub> uses = pattern.getUses(businessObject);

		// outerText
		Text outerTextGA = getOuterText();
		String outerText = pattern.getOuterTitle(businessObject);
		if (outerTextGA != null && !outerTextGA.getValue().equals(outerText)) {
			if (performUpdate) {
				outerTextGA.setValue(outerText);
			}
			return new Reason(true, "Outer title requires update");
		}

		// innerText
		Text innerTextGA = getInnerText();
		String innerText = pattern.getInnerTitle(businessObject);
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
				if (DUtil.isPropertyElementType(providesPortContainerShape, SHAPE_PROVIDES_PORT_CONTAINER)) {
					// find all providesPortText, and fixPointAnchorRectangle
					for (PropertyContainer providesPortChild : DUtil.collectPropertyContainerChildren(providesPortContainerShape)) {
						// text?
						if (DUtil.isPropertyElementType(providesPortChild, GA_PROVIDES_PORT_TEXT)) {
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
						if (DUtil.isPropertyElementType(providesPortChild, GA_FIX_POINT_ANCHOR_RECTANGLE)) {
							Rectangle fixPointAnchorRectangle = (Rectangle) providesPortChild;
							// get business object linked to fixPointAnchor
							Object portObject = DUtil.getBusinessObject(fixPointAnchorRectangle.getPictogramElement());
							if (portObject != null) {
								// ProvidesPortStub
								if (isExternalPort(portObject, externalPorts)) {
									// external port
									if (StyleUtil.needsUpdateForExternalProvidesPort(DUtil.findDiagram(this), fixPointAnchorRectangle.getStyle())) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForExternalProvidesPort(DUtil.findDiagram(this)));
											fixPointAnchorRectangle.getPictogramElement().getLink().getBusinessObjects().add(
												findExternalPort(portObject, externalPorts));// link to externalPort so
											// to externalPort so that update fires when it changes
										} else {
											return new Reason(true, "Port style requires update");
										}
									}
								} else {
									// non-external port
									if (StyleUtil.needsUpdateForProvidesPort(DUtil.findDiagram(this), fixPointAnchorRectangle.getStyle())) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForProvidesPort(DUtil.findDiagram(this)));
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
				if (DUtil.isPropertyElementType(usesPortContainerShape, SHAPE_USES_PORT_CONTAINER)) {
					// find all usesPortText
					for (PropertyContainer usesPortChild : DUtil.collectPropertyContainerChildren(usesPortContainerShape)) {
						// compare text
						if (DUtil.isPropertyElementType(usesPortChild, GA_USES_PORT_TEXT)) {
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
						if (DUtil.isPropertyElementType(usesPortChild, GA_FIX_POINT_ANCHOR_RECTANGLE)) {
							Rectangle fixPointAnchorRectangle = (Rectangle) usesPortChild;
							// get business object linked to fixPointAnchor
							Object portObject = DUtil.getBusinessObject(fixPointAnchorRectangle.getPictogramElement());
							if (portObject != null) {
								// usesPortStub
								if (isExternalPort(portObject, externalPorts)) {
									// external port
									if (StyleUtil.needsUpdateForExternalUsesPort(DUtil.findDiagram(this), fixPointAnchorRectangle.getStyle())) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForExternalUsesPort(DUtil.findDiagram(this)));
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
									if (StyleUtil.needsUpdateForUsesPort(DUtil.findDiagram(this), fixPointAnchorRectangle.getStyle())) {
										if (performUpdate) {
											updateStatus = true;
											// update style
											fixPointAnchorRectangle.setStyle(StyleUtil.createStyleForUsesPort(DUtil.findDiagram(this)));
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
			+ OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING + 4;

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
		int providesPortStubsSize = (providesPortStubs != null) ? providesPortStubs.size() : 0;
		int usesPortStubsSize = (usesPortStubs != null) ? usesPortStubs.size() : 0;

		int numPorts = Math.max(providesPortStubsSize, usesPortStubsSize);

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
		ContainerShape cs = DUtil.findContainerShapeParentWithProperty(pe, SHAPE_OUTER_CONTAINER);
		if (cs instanceof RHContainerShape) {
			return (RHContainerShape) cs;
		}
		return null;
	}

} // RHContainerShapeImpl
