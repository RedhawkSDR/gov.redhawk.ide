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

// BEGIN GENERATED CODE
package gov.redhawk.ide.graphiti.ext.impl;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ext.RHGxPackage;
import gov.redhawk.ide.graphiti.ui.GraphitiUIPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern;
import gov.redhawk.ide.graphiti.ui.diagram.preferences.DiagramPreferenceConstants;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.diagram.util.StyleUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub;
import mil.jpeojtrs.sca.partitioning.ProvidesPortStub;
import mil.jpeojtrs.sca.partitioning.UsesPortStub;
import mil.jpeojtrs.sca.sad.Port;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;
import org.eclipse.graphiti.mm.algorithms.Ellipse;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Image;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.algorithms.Rectangle;
import org.eclipse.graphiti.mm.algorithms.RoundedRectangle;
import org.eclipse.graphiti.mm.algorithms.Text;
import org.eclipse.graphiti.mm.algorithms.styles.Orientation;
import org.eclipse.graphiti.mm.algorithms.styles.Point;
import org.eclipse.graphiti.mm.algorithms.styles.StylesFactory;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.FixPointAnchor;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.mm.pictograms.impl.ContainerShapeImpl;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IGaLayoutService;
import org.eclipse.graphiti.util.IColorConstant;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>RH Container Shape</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isStarted <em>Started</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#getIStatusSeverity <em>IStatus Severity</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#getConnectionMap <em>Connection Map</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isHasSuperPortsContainerShape <em>Has Super Ports Container Shape</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isHasPortsContainerShape <em>Has Ports Container Shape</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl#isHideUnusedPorts <em>Hide Unused Ports</em>}</li>
 * </ul>
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
	 * The default value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnabled()
	 * @generated
	 * @ordered
	 */
	protected static final boolean ENABLED_EDEFAULT = true;

	/**
	 * The cached value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isEnabled()
	 * @generated
	 * @ordered
	 */
	protected boolean enabled = ENABLED_EDEFAULT;

	/**
	 * The default value of the '{@link #getIStatusSeverity() <em>IStatus Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIStatusSeverity()
	 * @generated NOT
	 * @ordered
	 */
	protected static final int ISTATUS_SEVERITY_EDEFAULT = IStatus.OK;

	/**
	 * The cached value of the '{@link #getIStatusSeverity() <em>IStatus Severity</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIStatusSeverity()
	 * @generated
	 * @ordered
	 */
	protected int iStatusSeverity = ISTATUS_SEVERITY_EDEFAULT;

	/**
	 * The cached value of the '{@link #getConnectionMap() <em>Connection Map</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConnectionMap()
	 * @generated NOT
	 * @ordered
	 */
	protected Map<String, IColorConstant> connectionMap = Collections.synchronizedMap(new HashMap<String, IColorConstant>());

	/**
	 * The default value of the '{@link #isHasSuperPortsContainerShape() <em>Has Super Ports Container Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHasSuperPortsContainerShape()
	 * @generated
	 * @ordered
	 */
	protected static final boolean HAS_SUPER_PORTS_CONTAINER_SHAPE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isHasSuperPortsContainerShape() <em>Has Super Ports Container Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHasSuperPortsContainerShape()
	 * @generated
	 * @ordered
	 */
	protected boolean hasSuperPortsContainerShape = HAS_SUPER_PORTS_CONTAINER_SHAPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isHasPortsContainerShape() <em>Has Ports Container Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHasPortsContainerShape()
	 * @generated
	 * @ordered
	 */
	protected static final boolean HAS_PORTS_CONTAINER_SHAPE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isHasPortsContainerShape() <em>Has Ports Container Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHasPortsContainerShape()
	 * @generated
	 * @ordered
	 */
	protected boolean hasPortsContainerShape = HAS_PORTS_CONTAINER_SHAPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isHideUnusedPorts() <em>Hide Unused Ports</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHideUnusedPorts()
	 * @generated
	 * @ordered
	 */
	protected static final boolean HIDE_UNUSED_PORTS_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isHideUnusedPorts() <em>Hide Unused Ports</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isHideUnusedPorts()
	 * @generated
	 * @ordered
	 */
	protected boolean hideUnusedPorts = HIDE_UNUSED_PORTS_EDEFAULT;

	// END GENERATED CODE

	// These are property key/value pairs that help us resize an existing shape by properly identifying
	// graphicsAlgorithms
	public static final String GA_OUTER_ROUNDED_RECTANGLE = "outerRoundedRectangle", GA_INNER_ROUNDED_RECTANGLE = "innerRoundedRectangle",
			GA_OUTER_ROUNDED_RECTANGLE_TEXT = "outerRoundedRectangleText", GA_INNER_ROUNDED_RECTANGLE_TEXT = "innerRoundedRectangleText",
			GA_OUTER_ROUNDED_RECTANGLE_IMAGE = "outerRoundedRectangleImage", GA_INNER_ROUNDED_RECTANGLE_IMAGE = "innerRoundedRectangleImage",
			GA_INNER_ROUNDED_RECTANGLE_LINE = "innerRoundedRectangleLine", GA_PROVIDES_PORT_RECTANGLE = "providesPortsRectangle",
			GA_FIX_POINT_ANCHOR_RECTANGLE = "fixPointAnchorRectangle", GA_USES_PORTS_RECTANGLE = "usesPortsRectangle";

	// Property key/value pairs help us identify Shapes to enable/disable user actions
	// (move, resize, delete, remove, etc.)
	public static final String SHAPE_OUTER_CONTAINER = "outerContainerShape", SHAPE_INNER_CONTAINER = "innerContainerShape",
			SHAPE_USES_PORTS_CONTAINER = "usesPortsContainerShape", SHAPE_PROVIDES_PORTS_CONTAINER = "providesPortsContainerShape",
			SHAPE_USES_PORT_CONTAINER = "usesPortContainerShape", SHAPE_PROVIDES_PORT_CONTAINER = "providesPortContainerShape",
			SHAPE_USES_PORT_RECTANGLE = "usesPortRectangleShape", SHAPE_PROVIDES_PORT_RECTANGLE = "providesPortRectangleShape",
			SHAPE_INTERFACE_CONTAINER = "interfaceContainerShape", SUPER_PROVIDES_PORTS_RECTANGLE = "superProvidesPortsContainer",
			SUPER_USES_PORTS_RECTANGLE = "superUsesPortsContainer";

	// Shape size constants
	public static final int OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING = 10, INNER_CONTAINER_SHAPE_TOP_PADDING = 20,
			INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING = 15, INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING = 60, PROVIDES_PORTS_LEFT_PADDING = 5,
			INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING,
			PORTS_CONTAINER_SHAPE_TOP_PADDING = 60, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING = 8, INNER_ROUNDED_RECTANGLE_LINE_Y = 28,
			PORT_NAME_HORIZONTAL_PADDING = 5, SUPER_PORT_SHAPE_HEIGHT = 25, SUPER_PORT_SHAPE_WIDTH = 10, SUPER_PORT_SHAPE_HEIGHT_MARGIN = 5,
			LOLLIPOP_ELLIPSE_DIAMETER = 10, INTERFACE_SHAPE_WIDTH = INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING + PROVIDES_PORTS_LEFT_PADDING,
			INTERFACE_SHAPE_HEIGHT = 10, ICON_IMAGE_LENGTH = 16;

	protected static final int INNER_ROUNDED_RECTANGLE_CORNER_WIDTH = 10;
	protected static final int INNER_ROUNDED_RECTANGLE_CORNER_HEIGHT = 10;
	protected static final int PORT_SHAPE_HEIGHT = 15;
	protected static final int PORT_SHAPE_WIDTH = PORT_SHAPE_HEIGHT;
	protected static final int PORT_ROW_PADDING_HEIGHT = 5;
	protected static final int REQ_PADDING_BETWEEN_PORT_TYPES = 10;
	// BEGIN GENERATED CODE

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
	 * @generated
	 */
	public void setStartedGen(boolean newStarted) {
		boolean oldStarted = started;
		started = newStarted;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__STARTED, oldStarted, started));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setStarted(boolean newStarted) {
		setStartedGen(newStarted);
		updateStyleForComponentInner();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEnabledGen(boolean newEnabled) {
		boolean oldEnabled = enabled;
		enabled = newEnabled;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__ENABLED, oldEnabled, enabled));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setEnabled(boolean newEnabled) {
		setEnabledGen(newEnabled);
		updateStyleForComponentInner();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getIStatusSeverity() {
		return iStatusSeverity;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIStatusSeverityGen(int newIStatusSeverity) {
		int oldIStatusSeverity = iStatusSeverity;
		iStatusSeverity = newIStatusSeverity;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_SEVERITY, oldIStatusSeverity, iStatusSeverity));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setIStatusSeverity(int newIStatusSeverity) {
		setIStatusSeverityGen(newIStatusSeverity);
		updateStyleForComponentInner();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Map<String, IColorConstant> getConnectionMap() {
		return connectionMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConnectionMap(Map<String, IColorConstant> newConnectionMap) {
		Map<String, IColorConstant> oldConnectionMap = connectionMap;
		connectionMap = newConnectionMap;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP, oldConnectionMap, connectionMap));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isHasSuperPortsContainerShape() {
		return hasSuperPortsContainerShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHasSuperPortsContainerShape(boolean newHasSuperPortsContainerShape) {
		boolean oldHasSuperPortsContainerShape = hasSuperPortsContainerShape;
		hasSuperPortsContainerShape = newHasSuperPortsContainerShape;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE,
				oldHasSuperPortsContainerShape, hasSuperPortsContainerShape));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isHasPortsContainerShape() {
		return hasPortsContainerShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHasPortsContainerShape(boolean newHasPortsContainerShape) {
		boolean oldHasPortsContainerShape = hasPortsContainerShape;
		hasPortsContainerShape = newHasPortsContainerShape;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE, oldHasPortsContainerShape,
				hasPortsContainerShape));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isHideUnusedPorts() {
		return hideUnusedPorts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHideUnusedPorts(boolean newHideUnusedPorts) {
		boolean oldHideUnusedPorts = hideUnusedPorts;
		hideUnusedPorts = newHideUnusedPorts;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS, oldHideUnusedPorts, hideUnusedPorts));
	}

	// END GENERATED CODE

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

		setVisible(true);
		setActive(true);
		setContainer(targetContainerShape);

		//configure preferences
		boolean hideDetailsPref = GraphitiUIPlugin.getDefault().getPreferenceStore().getBoolean(DiagramPreferenceConstants.HIDE_DETAILS);
		boolean hidePortsPref = GraphitiUIPlugin.getDefault().getPreferenceStore().getBoolean(DiagramPreferenceConstants.HIDE_UNUSED_PORTS);
		setHasSuperPortsContainerShape(hideDetailsPref);
		setHasPortsContainerShape(!hideDetailsPref);
		setHideUnusedPorts(hidePortsPref);

		// add property for this shape
		Graphiti.getPeService().setPropertyValue(this, DUtil.GA_TYPE, SHAPE_OUTER_CONTAINER);

		// graphic
		RoundedRectangle outerRoundedRectangle = Graphiti.getCreateService().createPlainRoundedRectangle(this, 5, 5);
		Graphiti.getPeService().setPropertyValue(outerRoundedRectangle, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE);
		StyleUtil.setStyle(outerRoundedRectangle, pattern.getStyleForOuter());
		outerRoundedRectangle.setTransparency(null); // inherit from style

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(outerRoundedRectangle, pattern.getOuterImageId());
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_IMAGE);

		// text
		Text cText = Graphiti.getCreateService().createPlainText(outerRoundedRectangle, pattern.getOuterTitle(newObject));
		StyleUtil.setStyle(cText, StyleUtil.OUTER_TEXT);
		Graphiti.getPeService().setPropertyValue(cText, DUtil.GA_TYPE, GA_OUTER_ROUNDED_RECTANGLE_TEXT);

		IFeatureProvider featureProvider = pattern.getFeatureProvider();

		// link objects
		featureProvider.link(this, pattern.getBusinessObjectsToLink(newObject).toArray());

		addInnerContainer(pattern.getInnerTitle(newObject), pattern.getInnerImageId(), pattern.getStyleForInner());

		EList<ProvidesPortStub> provides = pattern.getProvides(newObject);
		EList<UsesPortStub> uses = pattern.getUses(newObject);
		ComponentSupportedInterfaceStub interfaceStub = pattern.getInterface(newObject);

		if (isHasSuperPortsContainerShape()) {
			//hide shape details (only hides ports for now)

			if (provides != null && provides.size() > 0 || interfaceStub != null) {
				addSuperProvidesPortContainerShape(provides, interfaceStub, featureProvider, externalPorts);
			}
			if (uses != null && uses.size() > 0) {
				addSuperUsesPortContainerShape(uses, featureProvider, externalPorts);
			}
		}

		if (isHasPortsContainerShape()) {
			//draw all shape details (only ports)
			addLollipop(interfaceStub, featureProvider);
			addProvidesPorts(provides, featureProvider, externalPorts);
			addUsesPorts(uses, featureProvider, externalPorts);
		}
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
		int minimumHeight = 0;
		int minimumWidth = 0;
		if (isHasPortsContainerShape()) {
			// Show inner line
			getInnerPolyline().setLineVisible(true);

			// Layout provides ports
			ContainerShape providesPortsContainer = getProvidesPortsContainerShape();
			layoutProvidesPorts(providesPortsContainer);
			int providesHeight = providesPortsContainer.getGraphicsAlgorithm().getHeight();
			int providesWidth = providesPortsContainer.getGraphicsAlgorithm().getWidth();

			// Layout uses ports
			ContainerShape usesPortsContainer = getUsesPortsContainerShape();
			layoutUsesPorts(usesPortsContainer);
			adjustUsesPortsPosition(usesPortsContainer);
			int usesHeight = usesPortsContainer.getGraphicsAlgorithm().getHeight();
			int usesWidth = usesPortsContainer.getGraphicsAlgorithm().getWidth();

			// Account for port containers in outer sizing
			minimumHeight = Math.max(providesHeight, usesHeight) + 10;
			minimumWidth = providesWidth + usesWidth + REQ_PADDING_BETWEEN_PORT_TYPES;
		}

		// Resize height if necessary to accommodate contents (always requires padding)
		RoundedRectangle outerRoundedRectangle = (RoundedRectangle) this.getGraphicsAlgorithm();
		minimumHeight += PORTS_CONTAINER_SHAPE_TOP_PADDING;
		if (outerRoundedRectangle.getHeight() < minimumHeight) {
			outerRoundedRectangle.setHeight(minimumHeight);
		}

		// width
		minimumWidth = Math.max(minimumWidth, getMinimumWidth(getOuterText(), getInnerText()));
		if (outerRoundedRectangle.getWidth() < minimumWidth) {
			outerRoundedRectangle.setWidth(minimumWidth);

			// If the width changes, move the uses ports container
			adjustUsesPortsPosition(getUsesPortsContainerShape());
		}

		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();

		// outerRoundedRectangle
		int containerWidth = outerRoundedRectangle.getWidth();
		gaLayoutService.setLocationAndSize(getOuterText(), INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4, 0,
			containerWidth - (INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + ICON_IMAGE_LENGTH + 4), 20);
		gaLayoutService.setLocationAndSize(getOuterImage(), INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, 0, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);

		// innerRoundedRectangle
		ContainerShape innerShape = getInnerContainerShape();
		layoutInnerShape(innerShape);

		if (isHasSuperPortsContainerShape()) {
			ContainerShape superProvidesPortsContainerShape = getSuperProvidesPortsContainerShape();
			if (superProvidesPortsContainerShape != null) {
				layoutSuperProvidesPorts(superProvidesPortsContainerShape);
			}

			ContainerShape superUsesPortsContainerShape = getSuperUsesPortsContainerShape();
			if (superUsesPortsContainerShape != null) {
				layoutSuperUsesPorts(superUsesPortsContainerShape);
			}

			// Hide inner line
			getInnerPolyline().setLineVisible(false);
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

	// BEGIN GENERATED CODE

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
		case RHGxPackage.RH_CONTAINER_SHAPE__ENABLED:
			return isEnabled();
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_SEVERITY:
			return getIStatusSeverity();
		case RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP:
			return getConnectionMap();
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE:
			return isHasSuperPortsContainerShape();
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE:
			return isHasPortsContainerShape();
		case RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS:
			return isHideUnusedPorts();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case RHGxPackage.RH_CONTAINER_SHAPE__STARTED:
			setStarted((Boolean) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__ENABLED:
			setEnabled((Boolean) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_SEVERITY:
			setIStatusSeverity((Integer) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP:
			setConnectionMap((Map<String, IColorConstant>) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE:
			setHasSuperPortsContainerShape((Boolean) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE:
			setHasPortsContainerShape((Boolean) newValue);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS:
			setHideUnusedPorts((Boolean) newValue);
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
		case RHGxPackage.RH_CONTAINER_SHAPE__ENABLED:
			setEnabled(ENABLED_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_SEVERITY:
			setIStatusSeverity(ISTATUS_SEVERITY_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP:
			setConnectionMap((Map<String, IColorConstant>) null);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE:
			setHasSuperPortsContainerShape(HAS_SUPER_PORTS_CONTAINER_SHAPE_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE:
			setHasPortsContainerShape(HAS_PORTS_CONTAINER_SHAPE_EDEFAULT);
			return;
		case RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS:
			setHideUnusedPorts(HIDE_UNUSED_PORTS_EDEFAULT);
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
		case RHGxPackage.RH_CONTAINER_SHAPE__ENABLED:
			return enabled != ENABLED_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_SEVERITY:
			return iStatusSeverity != ISTATUS_SEVERITY_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP:
			return connectionMap != null;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE:
			return hasSuperPortsContainerShape != HAS_SUPER_PORTS_CONTAINER_SHAPE_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE:
			return hasPortsContainerShape != HAS_PORTS_CONTAINER_SHAPE_EDEFAULT;
		case RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS:
			return hideUnusedPorts != HIDE_UNUSED_PORTS_EDEFAULT;
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
		result.append(", enabled: ");
		result.append(enabled);
		result.append(", iStatusSeverity: ");
		result.append(iStatusSeverity);
		result.append(", connectionMap: ");
		result.append(connectionMap);
		result.append(", hasSuperPortsContainerShape: ");
		result.append(hasSuperPortsContainerShape);
		result.append(", hasPortsContainerShape: ");
		result.append(hasPortsContainerShape);
		result.append(", hideUnusedPorts: ");
		result.append(hideUnusedPorts);
		result.append(')');
		return result.toString();
	}

	// END GENERATED CODE

	/**
	 * add inner container
	 */
	protected ContainerShape addInnerContainer(String text, String imageId, String styleId) {
		ContainerShape innerContainerShape = Graphiti.getCreateService().createContainerShape(this, false);
		Graphiti.getPeService().setPropertyValue(innerContainerShape, DUtil.GA_TYPE, SHAPE_INNER_CONTAINER);
		RoundedRectangle innerRoundedRectangle = Graphiti.getCreateService().createPlainRoundedRectangle(innerContainerShape,
			INNER_ROUNDED_RECTANGLE_CORNER_WIDTH, INNER_ROUNDED_RECTANGLE_CORNER_HEIGHT);
		StyleUtil.setStyle(innerRoundedRectangle, styleId);
		Graphiti.getPeService().setPropertyValue(innerRoundedRectangle, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE);
		Graphiti.getGaLayoutService().setLocation(innerRoundedRectangle, INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING, INNER_CONTAINER_SHAPE_TOP_PADDING);

		// image
		Image imgIcon = Graphiti.getGaCreateService().createImage(innerRoundedRectangle, imageId);
		Graphiti.getPeService().setPropertyValue(imgIcon, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_IMAGE);

		// text
		Text ciText = Graphiti.getCreateService().createPlainText(innerRoundedRectangle, text);
		StyleUtil.setStyle(ciText, StyleUtil.INNER_TEXT);
		Graphiti.getPeService().setPropertyValue(ciText, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_TEXT);

		// draw line if showing shape details (ports)
		Polyline polyline = Graphiti.getGaCreateService().createPlainPolyline(innerRoundedRectangle,
			new int[] { 0, INNER_ROUNDED_RECTANGLE_LINE_Y, innerRoundedRectangle.getWidth(), INNER_ROUNDED_RECTANGLE_LINE_Y });
		StyleUtil.setStyle(polyline, styleId);
		Graphiti.getPeService().setPropertyValue(polyline, DUtil.GA_TYPE, GA_INNER_ROUNDED_RECTANGLE_LINE);

		return innerContainerShape;
	}

	/**
	 * Add lollipop to targetContainerShape. Lollipop anchor will link to the provided business object.T
	 */
	protected ContainerShape addLollipop(Object anchorBusinessObject, IFeatureProvider featureProvider) {

		// interface container lollipop
		ContainerShape interfaceContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(interfaceContainerShape, DUtil.GA_TYPE, SHAPE_INTERFACE_CONTAINER);
		Rectangle interfaceRectangle = Graphiti.getCreateService().createRectangle(interfaceContainerShape);
		featureProvider.link(interfaceContainerShape, anchorBusinessObject);
		interfaceRectangle.setTransparency(.99d);
		Graphiti.getGaLayoutService().setLocationAndSize(interfaceRectangle, 0, 25, INTERFACE_SHAPE_WIDTH, INTERFACE_SHAPE_HEIGHT);

		// interface lollipop line
		Shape lollipopLineShape = Graphiti.getCreateService().createContainerShape(interfaceContainerShape, false);
		Rectangle lollipopLine = Graphiti.getCreateService().createPlainRectangle(lollipopLineShape);
		StyleUtil.setStyle(lollipopLine, StyleUtil.LOLLIPOP);
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopLine, LOLLIPOP_ELLIPSE_DIAMETER, LOLLIPOP_ELLIPSE_DIAMETER / 2,
			INTERFACE_SHAPE_WIDTH - LOLLIPOP_ELLIPSE_DIAMETER, 1);

		// Interface lollipop ellipse and anchor
		FixPointAnchor fixPointAnchor = Graphiti.getPeCreateService().createFixPointAnchor(interfaceContainerShape);
		Point fixAnchorPoint = StylesFactory.eINSTANCE.createPoint();
		fixAnchorPoint.setX(0);
		fixAnchorPoint.setY(PORT_SHAPE_HEIGHT / 2);
		fixPointAnchor.setLocation(fixAnchorPoint);
		featureProvider.link(fixPointAnchor, anchorBusinessObject);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(interfaceRectangle);
		Ellipse lollipopEllipse = Graphiti.getCreateService().createPlainEllipse(fixPointAnchor);
		StyleUtil.setStyle(lollipopEllipse, StyleUtil.LOLLIPOP);
		Graphiti.getGaLayoutService().setLocationAndSize(lollipopEllipse, 0, -PORT_SHAPE_HEIGHT / 2, LOLLIPOP_ELLIPSE_DIAMETER, LOLLIPOP_ELLIPSE_DIAMETER);

		return interfaceContainerShape;
	}

	/**
	 * Adds Super Uses Port to shape
	 * @param usesPortStubs
	 * @param featureProvider
	 * @param externalPorts
	 */
	private void addSuperUsesPortContainerShape(EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// port shape
		ContainerShape superUsesPortsRectangleShape = Graphiti.getCreateService().createContainerShape(this, true);
		// ref prevent move
		Graphiti.getPeService().setPropertyValue(superUsesPortsRectangleShape, DUtil.SHAPE_TYPE, SUPER_USES_PORTS_RECTANGLE);
		Rectangle superUsesPortsRectangle = Graphiti.getCreateService().createRectangle(superUsesPortsRectangleShape);
		DUtil.addLinks(featureProvider, superUsesPortsRectangleShape, usesPortStubs);
		StyleUtil.setStyle(superUsesPortsRectangle, StyleUtil.SUPER_USES_PORT);

		// fix point anchor
		FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(superUsesPortsRectangleShape);
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(0);
		point.setY((SUPER_PORT_SHAPE_HEIGHT / 2) - 2); // Needs to slide up, or appears layered on rectangle shape
		fixPointAnchor.setLocation(point);
		DUtil.addLinks(featureProvider, fixPointAnchor, usesPortStubs);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(false);
		fixPointAnchor.setReferencedGraphicsAlgorithm(superUsesPortsRectangle);
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_FIX_POINT_ANCHOR_RECTANGLE);
		StyleUtil.setStyle(fixPointAnchorRectangle, StyleUtil.SUPER_USES_PORT);
		if (externalPorts != null && externalPorts.size() > 0) {
			fixPointAnchor.getLink().getBusinessObjects().addAll(externalPorts);
		}
		Graphiti.getGaLayoutService().setLocation(fixPointAnchorRectangle, 0, -SUPER_PORT_SHAPE_HEIGHT_MARGIN * 2);
	}

	/**
	 * Adds Super Provides Port to shape
	 * @param providesPortStubs
	 * @param featureProvider
	 * @param externalPorts
	 */
	private void addSuperProvidesPortContainerShape(EList<ProvidesPortStub> providesPortStubs, ComponentSupportedInterfaceStub interfaceStub,
		IFeatureProvider featureProvider, List<Port> externalPorts) {

		// port shape
		ContainerShape superProvidesPortsRectangleShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(superProvidesPortsRectangleShape, DUtil.SHAPE_TYPE, SUPER_PROVIDES_PORTS_RECTANGLE); // ref

		Rectangle superProvidesPortsRectangle = Graphiti.getCreateService().createRectangle(superProvidesPortsRectangleShape);
		DUtil.addLinks(featureProvider, superProvidesPortsRectangleShape, providesPortStubs);
		DUtil.addLink(featureProvider, superProvidesPortsRectangleShape, interfaceStub);
		StyleUtil.setStyle(superProvidesPortsRectangle, StyleUtil.SUPER_PROVIDES_PORT);
		Graphiti.getGaLayoutService().setLocation(superProvidesPortsRectangle, INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING - SUPER_PORT_SHAPE_WIDTH,
			INNER_CONTAINER_SHAPE_TOP_PADDING + SUPER_PORT_SHAPE_HEIGHT_MARGIN);

		// fix point anchor
		FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(superProvidesPortsRectangleShape);
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(0);
		point.setY((SUPER_PORT_SHAPE_HEIGHT / 2) - 2); // Needs to slide up, or appears layered on rectangle shape
		fixPointAnchor.setLocation(point);
		DUtil.addLinks(featureProvider, fixPointAnchor, providesPortStubs);
		DUtil.addLink(featureProvider, fixPointAnchor, interfaceStub);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(superProvidesPortsRectangle);
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_FIX_POINT_ANCHOR_RECTANGLE);
		StyleUtil.setStyle(fixPointAnchorRectangle, StyleUtil.SUPER_PROVIDES_PORT);
		if (externalPorts != null && externalPorts.size() > 0) {
			fixPointAnchor.getLink().getBusinessObjects().addAll(externalPorts);
		}
		Graphiti.getGaLayoutService().setLocation(fixPointAnchorRectangle, 0, -SUPER_PORT_SHAPE_HEIGHT_MARGIN * 2);
	}

	private FixPointAnchor createInvisibleAnchor(ContainerShape portShape, int x, int y) {
		FixPointAnchor fixPointAnchor = Graphiti.getCreateService().createFixPointAnchor(portShape);
		Point point = StylesFactory.eINSTANCE.createPoint();
		point.setX(x);
		point.setY(y);
		fixPointAnchor.setLocation(point);
		fixPointAnchor.setUseAnchorLocationAsConnectionEndpoint(true);
		fixPointAnchor.setReferencedGraphicsAlgorithm(portShape.getGraphicsAlgorithm());
		Rectangle fixPointAnchorRectangle = Graphiti.getCreateService().createPlainRectangle(fixPointAnchor);
		Graphiti.getPeService().setPropertyValue(fixPointAnchorRectangle, DUtil.GA_TYPE, GA_FIX_POINT_ANCHOR_RECTANGLE);
		fixPointAnchorRectangle.setFilled(false);
		fixPointAnchorRectangle.setLineVisible(false);
		Graphiti.getGaLayoutService().setLocationAndSize(fixPointAnchorRectangle, -x, -y, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);
		return fixPointAnchor;
	}

	/**
	 * Adds a ProvidesPortStub shape to the providesPortsContainerShape
	 */
	private void addProvidesPortContainerShape(ProvidesPortStub p, ContainerShape providesPortsContainerShape, IFeatureProvider featureProvider,
		Port externalPort) {

		ContainerShape providesPortContainerShape = Graphiti.getCreateService().createContainerShape(providesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORT_CONTAINER); // ref
		// prevent
		// selection/deletion/removal
		Rectangle providesPortContainerShapeRectangle = Graphiti.getCreateService().createPlainRectangle(providesPortContainerShape);
		providesPortContainerShapeRectangle.setFilled(false);
		providesPortContainerShapeRectangle.setLineVisible(false);
		featureProvider.link(providesPortContainerShape, p);

		// Port rectangle; this is created as its own shape because Anchors do not support decorators (for things
		// like highlighting)
		ContainerShape providesPortShape = Graphiti.getPeService().createContainerShape(providesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(providesPortShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORT_RECTANGLE);
		Rectangle providesPortRectangle = Graphiti.getCreateService().createPlainRectangle(providesPortShape);
		if (externalPort != null) {
			StyleUtil.setStyle(providesPortRectangle, StyleUtil.EXTERNAL_PROVIDES_PORT);
		} else {
			StyleUtil.setStyle(providesPortRectangle, StyleUtil.PROVIDES_PORT);
		}
		Graphiti.getGaLayoutService().setSize(providesPortRectangle, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);
		featureProvider.link(providesPortShape, p);

		// Port shape anchor
		FixPointAnchor fixPointAnchor = createInvisibleAnchor(providesPortShape, 0, PORT_SHAPE_HEIGHT / 2);
		DUtil.addLink(featureProvider, fixPointAnchor, p);
		if (externalPort != null) {
			DUtil.addLink(featureProvider, fixPointAnchor, externalPort);
		}

		// Port text
		Shape providesPortTextShape = Graphiti.getCreateService().createShape(providesPortContainerShape, false);
		Text providesPortText = Graphiti.getCreateService().createPlainText(providesPortTextShape, p.getName());
		StyleUtil.setStyle(providesPortText, StyleUtil.PORT_TEXT);
		Graphiti.getGaLayoutService().setLocation(providesPortText, PORT_SHAPE_WIDTH + PORT_NAME_HORIZONTAL_PADDING, 0);
	}

	private void resizePortText(Text text) {
		IDimension textSize = DUtil.calculateTextSize(text);
		// Graphiti appears to underestimate the width for some strings (e.g., those ending in "r"), so add a small
		// amount of padding to ensure the entire letter is drawn
		int textWidth = textSize.getWidth() + 2;
		Graphiti.getGaLayoutService().setSize(text, textWidth, PORT_SHAPE_HEIGHT);
	}

	protected void layoutInnerShape(ContainerShape innerContainerShape) {
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		IDimension parentSize = gaLayoutService.calculateSize(innerContainerShape.getContainer().getGraphicsAlgorithm());
		int width = parentSize.getWidth() - INNER_CONTAINER_SHAPE_HORIZONTAL_PADDING * 2 - PROVIDES_PORTS_LEFT_PADDING;
		int height = parentSize.getHeight() - INNER_CONTAINER_SHAPE_TOP_PADDING;
		gaLayoutService.setSize(innerContainerShape.getGraphicsAlgorithm(), width, height);
		IDimension innerRoundedRectangleTextSize = DUtil.calculateTextSize(getInnerText());
		int xForImage = (innerContainerShape.getGraphicsAlgorithm().getWidth() - (innerRoundedRectangleTextSize.getWidth() + ICON_IMAGE_LENGTH + 5)) / 2;
		gaLayoutService.setLocationAndSize(getInnerImage(), xForImage, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING, ICON_IMAGE_LENGTH, ICON_IMAGE_LENGTH);
		gaLayoutService.setLocationAndSize(getInnerText(), xForImage + ICON_IMAGE_LENGTH + 5, INNER_ROUNDED_RECTANGLE_TEXT_TOP_PADDING,
			innerRoundedRectangleTextSize.getWidth() + 10, innerRoundedRectangleTextSize.getHeight());
		getInnerPolyline().getPoints().get(1).setX(width);
	}

	private void layoutProvidesPorts(ContainerShape providesPortsContainer) {
		int currentY = 0;
		int maxWidth = 0;
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		for (Shape shape : providesPortsContainer.getChildren()) {
			ContainerShape providesPort = (ContainerShape) shape;

			// Resize the text
			Text portText = getPortText(providesPort);
			resizePortText(portText);
			int portWidth = portText.getWidth() + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_WIDTH;
			gaLayoutService.setSize(providesPort.getGraphicsAlgorithm(), portWidth, PORT_SHAPE_HEIGHT);

			// Place the container at the next Y position
			gaLayoutService.setLocation(providesPort.getGraphicsAlgorithm(), 0, currentY);
			currentY += providesPort.getGraphicsAlgorithm().getHeight() + PORT_ROW_PADDING_HEIGHT;
			maxWidth = Math.max(maxWidth, portWidth);
		}
		// Resize container to contents and adjust position so that ports are aligned to the outer edge
		currentY = Math.max(currentY - 5, 0); // remove extra spacing, if it was added above
		gaLayoutService.setSize(providesPortsContainer.getGraphicsAlgorithm(), maxWidth, currentY);
		// NB: For FindBy shapes and the like, the normal layout was not occurring for the provides port container
		Graphiti.getGaLayoutService().setLocation(providesPortsContainer.getGraphicsAlgorithm(), PROVIDES_PORTS_LEFT_PADDING,
			PORTS_CONTAINER_SHAPE_TOP_PADDING);
	}

	private void layoutUsesPorts(ContainerShape usesPortsContainer) {
		int maxWidth = 0;
		IGaLayoutService gaLayoutService = Graphiti.getGaLayoutService();
		// First pass: resize and layout contained ports, remembering max width
		for (Shape shape : usesPortsContainer.getChildren()) {
			ContainerShape usesPort = (ContainerShape) shape;

			// Resize the text
			Text portText = getPortText(usesPort);
			resizePortText(portText);
			int portWidth = portText.getWidth() + PORT_NAME_HORIZONTAL_PADDING + PORT_SHAPE_WIDTH;

			// Move the rectangle
			Rectangle portRectangle = getPortRectangle(usesPort);
			gaLayoutService.setLocation(portRectangle, portText.getWidth() + PORT_NAME_HORIZONTAL_PADDING, 0);

			// Resize container
			gaLayoutService.setSize(usesPort.getGraphicsAlgorithm(), portWidth, PORT_SHAPE_HEIGHT);
			maxWidth = Math.max(maxWidth, portWidth);
		}

		// Second pass: layout vertically and adjust X coordinates so that right edges line up (depends on max width)
		int currentY = 0;
		for (Shape shape : usesPortsContainer.getChildren()) {
			int xOffset = maxWidth - shape.getGraphicsAlgorithm().getWidth();
			gaLayoutService.setLocation(shape.getGraphicsAlgorithm(), xOffset, currentY);
			currentY += shape.getGraphicsAlgorithm().getHeight() + PORT_ROW_PADDING_HEIGHT;
		}

		// Resize container to contents
		currentY = Math.max(currentY - 5, 0); // remove extra spacing, if it was added above
		gaLayoutService.setSize(usesPortsContainer.getGraphicsAlgorithm(), maxWidth, currentY);
	}

	/**
	 * Adjusts the position of the uses ports container so that ports are aligned to the outer edge
	 */
	private void adjustUsesPortsPosition(ContainerShape usesPortsContainer) {
		if (usesPortsContainer != null) {
			int parentWidth = usesPortsContainer.getContainer().getGraphicsAlgorithm().getWidth();
			int xOffset = parentWidth - usesPortsContainer.getGraphicsAlgorithm().getWidth();
			Graphiti.getGaLayoutService().setLocation(usesPortsContainer.getGraphicsAlgorithm(), xOffset, PORTS_CONTAINER_SHAPE_TOP_PADDING);
		}
	}

	private void resizeSuperPort(ContainerShape superPortContainerShape) {
		// Resize relative to inner shape
		int height = getInnerContainerShape().getGraphicsAlgorithm().getHeight() - SUPER_PORT_SHAPE_HEIGHT_MARGIN * 2;
		Graphiti.getGaLayoutService().setSize(superPortContainerShape.getGraphicsAlgorithm(), SUPER_PORT_SHAPE_WIDTH, height);

		// Resize anchor
		Rectangle fixPointAnchorRectangle = (Rectangle) superPortContainerShape.getAnchors().get(0).getGraphicsAlgorithm();
		Graphiti.getGaLayoutService().setSize(fixPointAnchorRectangle, SUPER_PORT_SHAPE_WIDTH, height);
	}

	protected void layoutSuperProvidesPorts(ContainerShape superProvidesPortsContainerShape) {
		resizeSuperPort(superProvidesPortsContainerShape);
	}

	protected void layoutSuperUsesPorts(ContainerShape superUsesPortsContainerShape) {
		resizeSuperPort(superUsesPortsContainerShape);

		// Position at right edge of inner shape
		GraphicsAlgorithm innerGa = getInnerContainerShape().getGraphicsAlgorithm();
		int y = innerGa.getY() + SUPER_PORT_SHAPE_HEIGHT_MARGIN;
		int x = innerGa.getX() + innerGa.getWidth();
		Graphiti.getGaLayoutService().setLocation(superUsesPortsContainerShape.getGraphicsAlgorithm(), x, y);
	}

	/**
	 * Adds provides port container to provided container shape. Adds a port shape with name and anchor for each
	 * providesPortStub.
	 */
	private void addProvidesPorts(EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {

		// provides (input)
		ContainerShape providesPortsContainerShape = Graphiti.getCreateService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(providesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_PROVIDES_PORTS_CONTAINER);
		Rectangle providesPortsRectangle = Graphiti.getCreateService().createRectangle(providesPortsContainerShape);
		providesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(providesPortsRectangle, DUtil.GA_TYPE, GA_PROVIDES_PORT_RECTANGLE);
		Graphiti.getGaLayoutService().setLocation(providesPortsRectangle, PROVIDES_PORTS_LEFT_PADDING, PORTS_CONTAINER_SHAPE_TOP_PADDING);

		if (providesPortStubs != null) {
			featureProvider.link(providesPortsContainerShape, providesPortStubs.toArray());

			// iterate over all provides ports
			for (ProvidesPortStub p : providesPortStubs) {
				addProvidesPortContainerShape(p, providesPortsContainerShape, featureProvider, findExternalPort(p, externalPorts));
			}
		}
		layoutProvidesPorts(providesPortsContainerShape);
	}

	/**
	 * Adds provides ports if an RHContainerShape is edited after initial creation
	 */
	public void setProvidesPorts(EList<ProvidesPortStub> providesPortStubs, IFeatureProvider featureProvider) {
		// Manually clean up the provides port parent container. Easier to just rebuild from scratch
		DUtil.fastDeletePictogramElement(getProvidesPortsContainerShape());
		addProvidesPorts(providesPortStubs, featureProvider, null);
	}

	/**
	 * Adds a UsesPort shape to the usesPortsContainerShape
	 */
	private void addUsesPortContainerShape(UsesPortStub p, ContainerShape usesPortsContainerShape, IFeatureProvider featureProvider, Port externalPort) {
		// port container
		ContainerShape usesPortContainerShape = Graphiti.getPeService().createContainerShape(usesPortsContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortContainerShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORT_CONTAINER);
		Rectangle usesPortContainerShapeRectangle = Graphiti.getCreateService().createPlainRectangle(usesPortContainerShape);
		usesPortContainerShapeRectangle.setFilled(false);
		usesPortContainerShapeRectangle.setLineVisible(false);
		featureProvider.link(usesPortContainerShape, p);

		// Port rectangle; this is created as its own shape because Anchors do not support decorators (for things
		// like highlighting)
		ContainerShape usesPortShape = Graphiti.getPeService().createContainerShape(usesPortContainerShape, true);
		Graphiti.getPeService().setPropertyValue(usesPortShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORT_RECTANGLE);
		Rectangle usesPortRectangle = Graphiti.getCreateService().createPlainRectangle(usesPortShape);
		if (externalPort != null) {
			StyleUtil.setStyle(usesPortRectangle, StyleUtil.EXTERNAL_USES_PORT);
		} else {
			StyleUtil.setStyle(usesPortRectangle, StyleUtil.USES_PORT);
		}
		Graphiti.getGaLayoutService().setSize(usesPortRectangle, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT);
		featureProvider.link(usesPortShape, p);

		// Port anchor
		FixPointAnchor fixPointAnchor = createInvisibleAnchor(usesPortShape, PORT_SHAPE_WIDTH, PORT_SHAPE_HEIGHT / 2);
		featureProvider.link(fixPointAnchor, p);
		if (externalPort != null) {
			fixPointAnchor.getLink().getBusinessObjects().add(externalPort); // link to externalPort so that update fires when it changes
		}

		// port text
		Shape usesPortTextShape = Graphiti.getPeService().createShape(usesPortContainerShape, false);
		Text usesPortText = Graphiti.getCreateService().createPlainText(usesPortTextShape, p.getName());
		StyleUtil.setStyle(usesPortText, StyleUtil.PORT_TEXT);
		usesPortText.setHorizontalAlignment(Orientation.ALIGNMENT_RIGHT);
		Graphiti.getGaLayoutService().setLocation(usesPortText, 0, 0);
	}

	/**
	 * Adds uses port container to provided container shape. Adds a port shape with name and anchor for each
	 * usesPortStub.
	 */
	private void addUsesPorts(EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider, List<Port> externalPorts) {
		// uses (output)
		ContainerShape usesPortsContainerShape = Graphiti.getPeService().createContainerShape(this, true);
		Graphiti.getPeService().setPropertyValue(usesPortsContainerShape, DUtil.SHAPE_TYPE, SHAPE_USES_PORTS_CONTAINER);
		Rectangle usesPortsRectangle = Graphiti.getCreateService().createRectangle(usesPortsContainerShape);
		usesPortsRectangle.setTransparency(1d);
		Graphiti.getPeService().setPropertyValue(usesPortsRectangle, DUtil.GA_TYPE, GA_USES_PORTS_RECTANGLE);

		if (usesPortStubs != null) {
			featureProvider.link(usesPortsContainerShape, usesPortStubs.toArray());
			// add uses ports
			for (UsesPortStub p : usesPortStubs) {
				addUsesPortContainerShape(p, usesPortsContainerShape, featureProvider, findExternalPort(p, externalPorts));
			}
		}
		layoutUsesPorts(usesPortsContainerShape);
	}

	/**
	 * Adds uses ports if an RHContainerShape is edited after initial creation
	 */
	public void setUsesPorts(EList<UsesPortStub> usesPortStubs, IFeatureProvider featureProvider) {
		// Manually clean up the uses port parent container. Easier to just rebuild from scratch
		DUtil.fastDeletePictogramElement(getUsesPortsContainerShape());
		addUsesPorts(usesPortStubs, featureProvider, null);
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
		if (isHasSuperPortsContainerShape()) {
			ContainerShape usesSuperPortsContainerShape = getSuperUsesPortsContainerShape();
			if (usesSuperPortsContainerShape != null && usesSuperPortsContainerShape.getLink() != null) {
				return (EList<UsesPortStub>) (EList< ? >) usesSuperPortsContainerShape.getLink().getBusinessObjects();
			}
		} else if (isHasPortsContainerShape()) {
			ContainerShape usesPortsContainerShape = getUsesPortsContainerShape();
			if (usesPortsContainerShape != null && usesPortsContainerShape.getLink() != null) {
				return (EList<UsesPortStub>) (EList< ? >) usesPortsContainerShape.getLink().getBusinessObjects();
			}
		}
		return new BasicEList<UsesPortStub>();
	}

	/**
	 * Returns providesPortsStubs business object list linked to getProvidesPortsContainerShape()
	 */
	@SuppressWarnings("unchecked")
	public EList<ProvidesPortStub> getInternalProvidesPortStubs() {
		if (isHasSuperPortsContainerShape()) {
			ContainerShape providesSuperPortsContainerShape = getSuperProvidesPortsContainerShape();
			if (providesSuperPortsContainerShape != null && providesSuperPortsContainerShape.getLink() != null) {
				EList<ProvidesPortStub> returnList = new BasicEList<ProvidesPortStub>();
				EList<EObject> providesAndInterfaceObjects = (EList<EObject>) (EList< ? >) providesSuperPortsContainerShape.getLink().getBusinessObjects();
				for (EObject o : providesAndInterfaceObjects) {
					if (o instanceof ProvidesPortStub) {
						returnList.add((ProvidesPortStub) o);
					}
				}

				return returnList;
			}
		} else if (isHasPortsContainerShape()) {
			ContainerShape providesPortsContainerShape = getProvidesPortsContainerShape();
			if (providesPortsContainerShape != null && providesPortsContainerShape.getLink() != null) {
				return (EList<ProvidesPortStub>) (EList< ? >) providesPortsContainerShape.getLink().getBusinessObjects();
			}
		}
		return new BasicEList<ProvidesPortStub>();
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
	 * Return Super Provides Ports ContainerShape
	 * @return
	 */
	public ContainerShape getSuperProvidesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SUPER_PROVIDES_PORTS_RECTANGLE);
	}

	/**
	 * Return Super Uses Ports ContainerShape
	 * @return
	 */
	public ContainerShape getSuperUsesPortsContainerShape() {
		return (ContainerShape) DUtil.findFirstPropertyContainer(this, SUPER_USES_PORTS_RECTANGLE);
	}

	//Updates Provides Ports Container Shape
	protected Reason internalUpdateProvidesPortsContainerShape(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts,
		EList<ProvidesPortStub> provides, Diagram diagram, boolean performUpdate, boolean updateStatus) {

		// providesPortsContainerShape
		ContainerShape providesPortsContainerShape = getProvidesPortsContainerShape();

		if (!isHasPortsContainerShape()) {
			//Do NOT show ports container

			if (providesPortsContainerShape != null) {
				//port container exists, delete it

				if (performUpdate) {
					updateStatus = true;
					DUtil.fastDeletePictogramElement(providesPortsContainerShape);
				} else {
					return new Reason(true, "Provides Ports Shape requires deletion");
				}
			}
		} else if (providesPortsContainerShape == null) {
			//ports container does NOT exist, create it

			if (performUpdate) {
				updateStatus = true;
				EObject eObject = this.getLink().getBusinessObjects().get(0);

				//draw all shape details (only ports)
				addProvidesPorts(pattern.getProvides(eObject), pattern.getFeatureProvider(), externalPorts);

			} else {
				return new Reason(true, "Provides Ports ContainerShape require creation");
			}
		} else {
			//provides ports container exists, update it
			IFeatureProvider featureProvider = pattern.getFeatureProvider();

			if (!DUtil.isConnecting(diagram) && providesPortsContainerShape != null && provides != null) {
				List<Text> providesPortTexts = new ArrayList<Text>();

				// capture all providesPortText values
				for (Shape providesPortShape : providesPortsContainerShape.getChildren()) {
					ContainerShape providesPortContainerShape = (ContainerShape) providesPortShape;
					Text providesPortText = getPortText(providesPortContainerShape);
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
							DUtil.fastDeletePictogramElement((PictogramElement) providesPortContainerShape);
						} else {
							return new Reason(true, "Provides ports requires update");
						}
					} else {
						FixPointAnchor fixPointAnchor = getPortAnchor(providesPortContainerShape);
						Object portObject = DUtil.getBusinessObject(fixPointAnchor);
						if (portObject != null) {
							Rectangle fixPointAnchorRectangle = getPortRectangle(providesPortContainerShape);
							// ProvidesPortStub
							if (isExternalPort(portObject, externalPorts)) {
								// external port
								if (!StyleUtil.isStyleSet(fixPointAnchorRectangle, StyleUtil.EXTERNAL_PROVIDES_PORT)) {
									if (performUpdate) {
										updateStatus = true;
										// update style
										StyleUtil.setStyle(fixPointAnchorRectangle, StyleUtil.EXTERNAL_PROVIDES_PORT);
										// link to externalPort so that update fires when it changes
										fixPointAnchor.getLink().getBusinessObjects().add(findExternalPort(portObject, externalPorts));
									} else {
										return new Reason(true, "Port style requires update");
									}
								}
							} else {
								// non-external port
								if (!StyleUtil.isStyleSet(fixPointAnchorRectangle, StyleUtil.PROVIDES_PORT)) {
									if (performUpdate) {
										updateStatus = true;
										// update style
										StyleUtil.setStyle(fixPointAnchorRectangle, StyleUtil.PROVIDES_PORT);
									} else {
										return new Reason(true, "Port style requires update");
									}
								}
							}
						}
					}
				}
				// check number provides ports changed
				if (provides.size() != providesPortTexts.size()) {
					// add new provides ports
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
								addProvidesPortContainerShape(p, providesPortsContainerShape, featureProvider, findExternalPort(p, externalPorts));
							} else {
								return new Reason(true, "Provides ports requires update");
							}
						}
					}
					if (performUpdate) {
						layoutProvidesPorts(providesPortsContainerShape);
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update Provides Ports successful");
		}

		return new Reason(false, "No updates required");
	}

	//update lollipop
	protected Reason internalUpdateLollipop(AbstractContainerPattern pattern, Diagram diagram, boolean performUpdate, boolean updateStatus) {

		if (isHasSuperPortsContainerShape()) {
			//super ports exist, hide lollipop

			if (getLollipop() != null) {
				if (performUpdate) {
					updateStatus = true;
					ContainerShape lollipopContainerShape = getLollipop();
					DUtil.fastDeletePictogramElement(lollipopContainerShape);
				} else {
					return new Reason(true, "Lollipop Shape requires deletion");
				}
			}
		}
		if (isHasPortsContainerShape()) {
			//individual ports exist, display lollipop

			if (getLollipop() == null) {
				if (performUpdate) {
					updateStatus = true;
					EObject eObject = this.getLink().getBusinessObjects().get(0);

					//draw lollipop details
					addLollipop(pattern.getInterface(eObject), pattern.getFeatureProvider());
				} else {
					return new Reason(true, "Lollipop shape requires creation");
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update Lollipop successful");
		}

		return new Reason(false, "No updates required");
	}

	//Updates Uses Ports Container Shape
	protected Reason internalUpdateUsesPortsContainerShape(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts,
		EList<UsesPortStub> uses, Diagram diagram, boolean performUpdate, boolean updateStatus) {

		// usesPortsContainerShape
		ContainerShape usesPortsContainerShape = getUsesPortsContainerShape();

		if (!isHasPortsContainerShape()) {
			//Do NOT show ports container

			if (usesPortsContainerShape != null) {
				//port container exists, delete it

				if (performUpdate) {
					updateStatus = true;
					DUtil.fastDeletePictogramElement(usesPortsContainerShape);
				} else {
					return new Reason(true, "Uses Ports Shape requires deletion");
				}
			}
		} else if (usesPortsContainerShape == null) {
			//ports container does NOT exist, create it

			if (performUpdate) {
				updateStatus = true;
				EObject eObject = this.getLink().getBusinessObjects().get(0);

				//draw all shape details (only ports)
				addUsesPorts(pattern.getUses(eObject), pattern.getFeatureProvider(), externalPorts);

			} else {
				return new Reason(true, "Uses Ports ContainerShape require creation");
			}
		} else {
			//uses ports container exists, update it
			IFeatureProvider featureProvider = pattern.getFeatureProvider();
			if (!DUtil.isConnecting(diagram) && usesPortsContainerShape != null && uses != null && uses.size() > 0) {

				List<Text> usesPortTexts = new ArrayList<Text>();

				// verify uses port quantity and text haven't changed
				// find THE usesPortContainerShape
				for (Shape usesPortShape : usesPortsContainerShape.getChildren()) {
					ContainerShape usesPortContainerShape = (ContainerShape) usesPortShape;
					Text usesPortText = getPortText(usesPortContainerShape);
					usesPortTexts.add(usesPortText);
					// search for text in model
					boolean found = false;
					for (UsesPortStub portStub : uses) {
						if (portStub.getName().equals(usesPortText.getValue())) {
							found = true;
							break;
						}
					}
					if (!found) {
						if (performUpdate) {
							updateStatus = true;
							// delete shape
							DUtil.fastDeletePictogramElement(usesPortContainerShape);
						} else {
							return new Reason(true, "Uses ports requires update");
						}
					} else {
						FixPointAnchor fixPointAnchor = getPortAnchor(usesPortContainerShape);
						// get business object linked to fixPointAnchor
						Object portObject = DUtil.getBusinessObject(fixPointAnchor);
						if (portObject != null) {
							Rectangle portRectangle = getPortRectangle(usesPortContainerShape);
							// usesPortStub
							if (isExternalPort(portObject, externalPorts)) {
								// external port
								if (!StyleUtil.isStyleSet(portRectangle, StyleUtil.EXTERNAL_USES_PORT)) {
									if (performUpdate) {
										updateStatus = true;
										// update style
										StyleUtil.setStyle(portRectangle, StyleUtil.EXTERNAL_USES_PORT);
										// link to externalPort so that update fires when it changes
										fixPointAnchor.getLink().getBusinessObjects().add(findExternalPort(portObject, externalPorts));
									} else {
										return new Reason(true, "Port style requires update");
									}
								}
							} else {
								// non-external port
								if (!StyleUtil.isStyleSet(portRectangle, StyleUtil.USES_PORT)) {
									if (performUpdate) {
										updateStatus = true;
										// update style
										StyleUtil.setStyle(portRectangle, StyleUtil.USES_PORT);
										// this line will actually remove existing links (which will include an
										// external port) and simply add the portObject (which already existed)
										featureProvider.link(portRectangle.getPictogramElement(), portObject);
									} else {
										return new Reason(true, "Port style requires update");
									}
								}
							}
						}
					}
				}
				// check number uses ports changed
				if (uses.size() != usesPortTexts.size()) {
					// add new uses ports
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
								addUsesPortContainerShape(p, usesPortsContainerShape, featureProvider, findExternalPort(p, externalPorts));
							} else {
								return new Reason(true, "Uses ports requires update");
							}
						}
					}
					if (performUpdate) {
						layoutUsesPorts(usesPortsContainerShape);
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update Uses Ports successful");
		}

		return new Reason(false, "No updates required");
	}

	//update super provides port container shape
	protected Reason internalUpdateSuperProvidesPortsContainerShape(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts,
		EList<ProvidesPortStub> provides, ComponentSupportedInterfaceStub interfaceStub, Diagram diagram, boolean performUpdate, boolean updateStatus) {

		// super port shape
		ContainerShape superProvidesPortsRectangleShape = getSuperProvidesPortsContainerShape();

		if (!isHasSuperPortsContainerShape()) {
			//Do NOT show super ports

			if (superProvidesPortsRectangleShape != null) {
				//super ports exist, delete them
				if (performUpdate) {
					updateStatus = true;
					DUtil.fastDeletePictogramElement(superProvidesPortsRectangleShape);
				} else {
					return new Reason(true, "Super Provides Ports Shape requires deletion");
				}
			}
		} else {

			IFeatureProvider featureProvider = pattern.getFeatureProvider();

			if (superProvidesPortsRectangleShape == null && ((provides != null && provides.size() > 0) || interfaceStub != null)) {
				//add super ports shape

				if (performUpdate) {
					updateStatus = true;
					EObject eObject = this.getLink().getBusinessObjects().get(0);
					//create super ports
					addSuperProvidesPortContainerShape(pattern.getProvides(eObject), pattern.getInterface(eObject), pattern.getFeatureProvider(),
						externalPorts);
				} else {
					return new Reason(true, "Super Provides Ports require creation");
				}
			} else if (superProvidesPortsRectangleShape != null) {
				//update super ports shape

				//update Anchor links
				FixPointAnchor fixPointAnchor = (FixPointAnchor) superProvidesPortsRectangleShape.getAnchors().get(0);

				List<EObject> eObjects = new ArrayList<EObject>();
				Collections.addAll(eObjects, provides.toArray(new ProvidesPortStub[0]));
				if (externalPorts != null && externalPorts.size() > 0) {
					Collections.addAll(eObjects, externalPorts.toArray(new Port[0]));
				}
				if (interfaceStub != null) {
					eObjects.add(interfaceStub);
				}

				//add all objects that aren't already there
				for (EObject o : eObjects) {
					if (fixPointAnchor.getLink() == null || !fixPointAnchor.getLink().getBusinessObjects().contains(o)) {
						if (performUpdate) {
							updateStatus = true;
							DUtil.addLink(featureProvider, fixPointAnchor, o);
						} else {
							return new Reason(true, "Super Provides Ports requires update");
						}
					}
				}

				//remove any out-dated objects
				if (fixPointAnchor.getLink() != null) {
					for (Iterator<EObject> iter = fixPointAnchor.getLink().getBusinessObjects().iterator(); iter.hasNext();) {
						if (!eObjects.contains(iter.next())) {
							if (performUpdate) {
								updateStatus = true;
								iter.remove();
							} else {
								return new Reason(true, "Super Provides Ports requires update");
							}
						}
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update Super Provides Ports successful");
		}

		return new Reason(false, "No updates required");

	}

	//updates super uses ports container shape
	protected Reason internalUpdateSuperUsesPortsContainerShape(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts,
		EList<UsesPortStub> uses, Diagram diagram, boolean performUpdate, boolean updateStatus) {

		// super port shape
		ContainerShape superUsesPortsRectangleShape = getSuperUsesPortsContainerShape();

		if (!isHasSuperPortsContainerShape()) {
			//Do NOT show super ports

			if (superUsesPortsRectangleShape != null) {
				//super ports exist, delete them
				if (performUpdate) {
					updateStatus = true;
					DUtil.fastDeletePictogramElement(superUsesPortsRectangleShape);
				} else {
					return new Reason(true, "Super Uses Ports Shape requires deletion");
				}
			}
		} else {

			IFeatureProvider featureProvider = pattern.getFeatureProvider();

			if (superUsesPortsRectangleShape == null && uses != null && uses.size() > 0) {
				//add super ports shape

				if (performUpdate) {
					updateStatus = true;
					EObject eObject = this.getLink().getBusinessObjects().get(0);
					//create super ports
					addSuperUsesPortContainerShape(pattern.getUses(eObject), pattern.getFeatureProvider(), externalPorts);
				} else {
					return new Reason(true, "Super Provides Ports require creation");
				}
			} else if (superUsesPortsRectangleShape != null) {
				//update super ports shape

				//update Anchor links
				FixPointAnchor fixPointAnchor = (FixPointAnchor) superUsesPortsRectangleShape.getAnchors().get(0);

				List<EObject> eObjects = new ArrayList<EObject>();
				Collections.addAll(eObjects, uses.toArray(new UsesPortStub[0]));
				if (externalPorts != null && externalPorts.size() > 0) {
					Collections.addAll(eObjects, externalPorts.toArray(new Port[0]));
				}

				//add all objects that aren't already there
				for (EObject o : eObjects) {
					if (fixPointAnchor.getLink() == null || !fixPointAnchor.getLink().getBusinessObjects().contains(o)) {
						if (performUpdate) {
							updateStatus = true;
							DUtil.addLink(featureProvider, fixPointAnchor, o);
						} else {
							return new Reason(true, "Super Uses Ports requires update");
						}
					}
				}

				//remove any out-dated objects
				if (fixPointAnchor.getLink() != null) {
					for (Iterator<EObject> iter = fixPointAnchor.getLink().getBusinessObjects().iterator(); iter.hasNext();) {
						if (!eObjects.contains(iter.next())) {
							if (performUpdate) {
								updateStatus = true;
								iter.remove();
							} else {
								return new Reason(true, "Super Uses Ports requires update");
							}
						}
					}
				}
			}
		}

		if (updateStatus && performUpdate) {
			return new Reason(true, "Update Super Uses Ports successful");
		}

		return new Reason(false, "No updates required");

	}

	/**
	 * handles both determining whether an update is needed and performing an update for the shape.
	 * @return
	 */
	protected Reason internalUpdate(AbstractContainerPattern pattern, EObject businessObject, List<Port> externalPorts, boolean performUpdate) {

		boolean updateStatus = false;

		Diagram diagram = DUtil.findDiagram(this);

		EList<ProvidesPortStub> provides = pattern.getProvides(businessObject);
		ComponentSupportedInterfaceStub interfaceStub = pattern.getInterface(businessObject);
		EList<UsesPortStub> uses = pattern.getUses(businessObject);

		// outerText
		Text outerTextGA = getOuterText();
		String outerText = pattern.getOuterTitle(businessObject);
		if (outerTextGA != null && !outerTextGA.getValue().equals(outerText)) {
			if (performUpdate) {
				updateStatus = true;
				outerTextGA.setValue(outerText);
			}
			return new Reason(true, "Outer title requires update");
		}

		// innerText
		Text innerTextGA = getInnerText();
		String innerText = pattern.getInnerTitle(businessObject);
		if (innerTextGA != null && innerTextGA.getValue() != null && !innerTextGA.getValue().equals(innerText)) {
			if (performUpdate) {
				updateStatus = true;
				innerTextGA.setValue(innerText);
			}
			return new Reason(true, "Inner title requires update");
		}

		//update super provides ports if they exist
		Reason updateSuperProvidesPortsReason = internalUpdateSuperProvidesPortsContainerShape(pattern, businessObject, externalPorts, provides, interfaceStub,
			diagram, performUpdate, updateStatus);
		if (!performUpdate && updateSuperProvidesPortsReason.toBoolean()) {
			//if updates required return true
			return updateSuperProvidesPortsReason;
		}

		//update super uses ports if they exist
		Reason updateSuperUsesPortsReason = internalUpdateSuperUsesPortsContainerShape(pattern, businessObject, externalPorts, uses, diagram, performUpdate,
			updateStatus);
		if (!performUpdate && updateSuperUsesPortsReason.toBoolean()) {
			//if updates required return true
			return updateSuperUsesPortsReason;
		}

		//draw all shape details (only ports)
		//update provides ports
		Reason updateProvidesPortsReason = internalUpdateProvidesPortsContainerShape(pattern, businessObject, externalPorts, provides, diagram, performUpdate,
			updateStatus);
		if (!performUpdate && updateProvidesPortsReason.toBoolean()) {
			//if updates required return true
			return updateProvidesPortsReason;
		}

		//update uses ports
		Reason updateUsesPortsReason = internalUpdateUsesPortsContainerShape(pattern, businessObject, externalPorts, uses, diagram, performUpdate,
			updateStatus);
		if (!performUpdate && updateUsesPortsReason.toBoolean()) {
			//if updates required return true
			return updateUsesPortsReason;
		}

		//update lollipop
		Reason updateLollipopReason = internalUpdateLollipop(pattern, diagram, performUpdate, updateStatus);
		if (!performUpdate && updateLollipopReason.toBoolean()) {
			//if updates required return true
			return updateLollipopReason;
		}

		if (updateStatus && performUpdate) {
			layout();
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
	private static boolean isExternalPort(Object portObject, List<Port> externalPorts) {
		return findExternalPort(portObject, externalPorts) != null;
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
					if (p.getProvidesIdentifier() != null && p.getProvidesIdentifier().equals(usesPort.getName())) {
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
	protected int getMinimumWidth(Text outerTitle, Text innerTitle) {
		// inner title (potentially including start order)
		int innerTitleWidth = getInnerWidth(innerTitle);

		// outer title
		int outerTitleWidth = getOuterWidth(outerTitle);

		// Return the largest, plus the lollipop width
		return Math.max(innerTitleWidth, outerTitleWidth) + INTERFACE_SHAPE_WIDTH;
	}

	protected int getInnerWidth(Text innerTitle) {
		IDimension innerTitleDimension = DUtil.calculateTextSize(innerTitle);
		return innerTitleDimension.getWidth() + INNER_CONTAINER_SHAPE_TITLE_HORIZONTAL_PADDING;
	}

	protected int getOuterWidth(Text outerTitle) {
		IDimension outerTitleDimension = DUtil.calculateTextSize(outerTitle);
		return INNER_CONTAINER_SHAPE_HORIZONTAL_LEFT_PADDING + outerTitleDimension.getWidth() + OUTER_CONTAINER_SHAPE_TITLE_HORIZONTAL_RIGHT_PADDING + 4;
	}

	/**
	 * Updates the style of the component's inner rounded rectangle based on the current state.
	 */
	protected void updateStyleForComponentInner() {
		String styleId;
		if (iStatusSeverity == IStatus.ERROR) {
			styleId = StyleUtil.COMPONENT_INNER_ERROR;
		} else if (!enabled) {
			styleId = StyleUtil.COMPONENT_INNER_DISABLED;
		} else if (started) {
			styleId = StyleUtil.COMPONENT_INNER_STARTED;
		} else {
			styleId = StyleUtil.COMPONENT_INNER;
		}
		StyleUtil.setStyle(getInnerContainerShape().getGraphicsAlgorithm(), styleId);
		StyleUtil.setStyle(getInnerPolyline(), styleId);
	}

	protected Text getPortText(ContainerShape portContainerShape) {
		return (Text) portContainerShape.getChildren().get(1).getGraphicsAlgorithm();
	}

	protected Rectangle getPortRectangle(ContainerShape portContainerShape) {
		return (Rectangle) portContainerShape.getChildren().get(0).getGraphicsAlgorithm();
	}

	protected FixPointAnchor getPortAnchor(ContainerShape portContainerShape) {
		return (FixPointAnchor) portContainerShape.getChildren().get(0).getAnchors().get(0);
	}
} // RHContainerShapeImpl
