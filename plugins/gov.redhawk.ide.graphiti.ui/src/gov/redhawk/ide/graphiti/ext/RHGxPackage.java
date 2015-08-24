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
package gov.redhawk.ide.graphiti.ext;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.graphiti.ext.RHGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel prefix='RHGx' codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' contentTypeIdentifier='http://www.redhawk.gov/model/rhgext/1.0.0' operationReflection='false' modelDirectory='/gov.redhawk.ide.graphiti.ui/src' basePackage='gov.redhawk.ide.graphiti'"
 *        annotation="http://www.eclipse.org/emf/2011/Xcore GenModel='http://www.eclipse.org/emf/2002/GenModel' Ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface RHGxPackage extends EPackage {

	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "ext";
	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.redhawk.gov/model/rhgext/1.0.0";
	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ext";
	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eCONTENT_TYPE = "http://www.redhawk.gov/model/rhgext/1.0.0";
	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHGxPackage eINSTANCE = gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl.init();
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
	 * @generated
	 */
	int RH_CONTAINER_SHAPE = 0;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__PROPERTIES = PictogramsPackage.CONTAINER_SHAPE__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__VISIBLE = PictogramsPackage.CONTAINER_SHAPE__VISIBLE;
	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__GRAPHICS_ALGORITHM = PictogramsPackage.CONTAINER_SHAPE__GRAPHICS_ALGORITHM;
	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ACTIVE = PictogramsPackage.CONTAINER_SHAPE__ACTIVE;
	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__LINK = PictogramsPackage.CONTAINER_SHAPE__LINK;
	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ANCHORS = PictogramsPackage.CONTAINER_SHAPE__ANCHORS;
	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CONTAINER = PictogramsPackage.CONTAINER_SHAPE__CONTAINER;
	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CHILDREN = PictogramsPackage.CONTAINER_SHAPE__CHILDREN;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__STARTED = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>IStatus Severity</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__ISTATUS_SEVERITY = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Connection Map</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__CONNECTION_MAP = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Has Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 3;
	/**
	 * The feature id for the '<em><b>Has Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 4;
	/**
	 * The feature id for the '<em><b>Hide Unused Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 5;
	/**
	 * The number of structural features of the '<em>RH Container Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_FEATURE_COUNT = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 6;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ext.Event
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getEvent()
	 * @generated
	 */
	int EVENT = 1;
	/**
	 * The meta object id for the '<em>Component Supported Interface Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
	 * @generated
	 */
	int COMPONENT_SUPPORTED_INTERFACE_STUB = 2;
	/**
	 * The meta object id for the '<em>IFeature Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
	 * @generated
	 */
	int IFEATURE_PROVIDER = 3;
	/**
	 * The meta object id for the '<em>Uses Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
	 * @generated
	 */
	int USES_PORT_STUB = 4;
	/**
	 * The meta object id for the '<em>Provides Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
	 * @generated
	 */
	int PROVIDES_PORT_STUB = 5;
	/**
	 * The meta object id for the '<em>Port</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 6;
	/**
	 * The meta object id for the '<em>Assembly Controller</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
	 * @generated
	 */
	int ASSEMBLY_CONTROLLER = 7;
	/**
	 * The meta object id for the '<em>External Ports</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
	 * @generated
	 */
	int EXTERNAL_PORTS = 8;
	/**
	 * The meta object id for the '<em>Reason</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getReason()
	 * @generated
	 */
	int REASON = 9;
	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getList()
	 * @generated
	 */
	int LIST = 10;
	/**
	 * The meta object id for the '<em>Sad Component Instantiation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
	 * @generated
	 */
	int SAD_COMPONENT_INSTANTIATION = 11;
	/**
	 * The meta object id for the '<em>IAdd Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIAddContext()
	 * @generated
	 */
	int IADD_CONTEXT = 12;
	/**
	 * The meta object id for the '<em>Abstract Container Pattern</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAbstractContainerPattern()
	 * @generated
	 */
	int ABSTRACT_CONTAINER_PATTERN = 13;
	/**
	 * The meta object id for the '<em>IUpdate Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIUpdateContext()
	 * @generated
	 */
	int IUPDATE_CONTEXT = 14;
	/**
	 * The meta object id for the '<em>IColor Constant</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.util.IColorConstant
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIColorConstant()
	 * @generated
	 */
	int ICOLOR_CONSTANT = 15;
	/**
	 * The meta object id for the '<em>Map</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.Map
	 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getMap()
	 * @generated
	 */
	int MAP = 16;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape <em>RH Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RH Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape
	 * @generated
	 */
	EClass getRHContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isStarted <em>Started</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Started</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isStarted()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_Started();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#getIStatusSeverity <em>IStatus Severity</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>IStatus Severity</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#getIStatusSeverity()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_IStatusSeverity();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#getConnectionMap <em>Connection Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Connection Map</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#getConnectionMap()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_ConnectionMap();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isHasSuperPortsContainerShape <em>Has Super Ports Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Super Ports Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isHasSuperPortsContainerShape()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_HasSuperPortsContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isHasPortsContainerShape <em>Has Ports Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Has Ports Container Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isHasPortsContainerShape()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_HasPortsContainerShape();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.graphiti.ext.RHContainerShape#isHideUnusedPorts <em>Hide Unused Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Hide Unused Ports</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.RHContainerShape#isHideUnusedPorts()
	 * @see #getRHContainerShape()
	 * @generated
	 */
	EAttribute getRHContainerShape_HideUnusedPorts();

	/**
	 * Returns the meta object for enum '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Event</em>'.
	 * @see gov.redhawk.ide.graphiti.ext.Event
	 * @generated
	 */
	EEnum getEvent();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub <em>Component Supported Interface Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Component Supported Interface Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub"
	 * @generated
	 */
	EDataType getComponentSupportedInterfaceStub();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.IFeatureProvider <em>IFeature Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IFeature Provider</em>'.
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @model instanceClass="org.eclipse.graphiti.features.IFeatureProvider"
	 * @generated
	 */
	EDataType getIFeatureProvider();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.UsesPortStub <em>Uses Port Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Uses Port Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.UsesPortStub"
	 * @generated
	 */
	EDataType getUsesPortStub();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.partitioning.ProvidesPortStub <em>Provides Port Stub</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Provides Port Stub</em>'.
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @model instanceClass="mil.jpeojtrs.sca.partitioning.ProvidesPortStub"
	 * @generated
	 */
	EDataType getProvidesPortStub();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.Port <em>Port</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Port</em>'.
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @model instanceClass="mil.jpeojtrs.sca.sad.Port"
	 * @generated
	 */
	EDataType getPort();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.AssemblyController <em>Assembly Controller</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Assembly Controller</em>'.
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @model instanceClass="mil.jpeojtrs.sca.sad.AssemblyController"
	 * @generated
	 */
	EDataType getAssemblyController();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.ExternalPorts <em>External Ports</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>External Ports</em>'.
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @model instanceClass="mil.jpeojtrs.sca.sad.ExternalPorts"
	 * @generated
	 */
	EDataType getExternalPorts();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.impl.Reason <em>Reason</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Reason</em>'.
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @model instanceClass="org.eclipse.graphiti.features.impl.Reason"
	 * @generated
	 */
	EDataType getReason();

	/**
	 * Returns the meta object for data type '{@link java.util.List <em>List</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>List</em>'.
	 * @see java.util.List
	 * @model instanceClass="java.util.List" typeParameters="Port"
	 * @generated
	 */
	EDataType getList();

	/**
	 * Returns the meta object for data type '{@link mil.jpeojtrs.sca.sad.SadComponentInstantiation <em>Sad Component Instantiation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Sad Component Instantiation</em>'.
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @model instanceClass="mil.jpeojtrs.sca.sad.SadComponentInstantiation"
	 * @generated
	 */
	EDataType getSadComponentInstantiation();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.context.IAddContext <em>IAdd Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IAdd Context</em>'.
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @model instanceClass="org.eclipse.graphiti.features.context.IAddContext"
	 * @generated
	 */
	EDataType getIAddContext();

	/**
	 * Returns the meta object for data type '{@link gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern <em>Abstract Container Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Abstract Container Pattern</em>'.
	 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
	 * @model instanceClass="gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern"
	 * @generated
	 */
	EDataType getAbstractContainerPattern();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.features.context.IUpdateContext <em>IUpdate Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IUpdate Context</em>'.
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @model instanceClass="org.eclipse.graphiti.features.context.IUpdateContext"
	 * @generated
	 */
	EDataType getIUpdateContext();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.graphiti.util.IColorConstant <em>IColor Constant</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IColor Constant</em>'.
	 * @see org.eclipse.graphiti.util.IColorConstant
	 * @model instanceClass="org.eclipse.graphiti.util.IColorConstant"
	 * @generated
	 */
	EDataType getIColorConstant();

	/**
	 * Returns the meta object for data type '{@link java.util.Map <em>Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Map</em>'.
	 * @see java.util.Map
	 * @model instanceClass="java.util.Map<java.lang.String, org.eclipse.graphiti.util.IColorConstant>"
	 * @generated
	 */
	EDataType getMap();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RHGxFactory getRHGxFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHContainerShapeImpl
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
		 * @generated
		 */
		EClass RH_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape();

		/**
		 * The meta object literal for the '<em><b>Started</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__STARTED = eINSTANCE.getRHContainerShape_Started();

		/**
		 * The meta object literal for the '<em><b>IStatus Severity</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__ISTATUS_SEVERITY = eINSTANCE.getRHContainerShape_IStatusSeverity();

		/**
		 * The meta object literal for the '<em><b>Connection Map</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__CONNECTION_MAP = eINSTANCE.getRHContainerShape_ConnectionMap();

		/**
		 * The meta object literal for the '<em><b>Has Super Ports Container Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape_HasSuperPortsContainerShape();

		/**
		 * The meta object literal for the '<em><b>Has Ports Container Shape</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE = eINSTANCE.getRHContainerShape_HasPortsContainerShape();

		/**
		 * The meta object literal for the '<em><b>Hide Unused Ports</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS = eINSTANCE.getRHContainerShape_HideUnusedPorts();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.ext.Event <em>Event</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ext.Event
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getEvent()
		 * @generated
		 */
		EEnum EVENT = eINSTANCE.getEvent();

		/**
		 * The meta object literal for the '<em>Component Supported Interface Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
		 * @generated
		 */
		EDataType COMPONENT_SUPPORTED_INTERFACE_STUB = eINSTANCE.getComponentSupportedInterfaceStub();

		/**
		 * The meta object literal for the '<em>IFeature Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.IFeatureProvider
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
		 * @generated
		 */
		EDataType IFEATURE_PROVIDER = eINSTANCE.getIFeatureProvider();

		/**
		 * The meta object literal for the '<em>Uses Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
		 * @generated
		 */
		EDataType USES_PORT_STUB = eINSTANCE.getUsesPortStub();

		/**
		 * The meta object literal for the '<em>Provides Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
		 * @generated
		 */
		EDataType PROVIDES_PORT_STUB = eINSTANCE.getProvidesPortStub();

		/**
		 * The meta object literal for the '<em>Port</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.Port
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getPort()
		 * @generated
		 */
		EDataType PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em>Assembly Controller</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.AssemblyController
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
		 * @generated
		 */
		EDataType ASSEMBLY_CONTROLLER = eINSTANCE.getAssemblyController();

		/**
		 * The meta object literal for the '<em>External Ports</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.ExternalPorts
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
		 * @generated
		 */
		EDataType EXTERNAL_PORTS = eINSTANCE.getExternalPorts();

		/**
		 * The meta object literal for the '<em>Reason</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.impl.Reason
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getReason()
		 * @generated
		 */
		EDataType REASON = eINSTANCE.getReason();

		/**
		 * The meta object literal for the '<em>List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getList()
		 * @generated
		 */
		EDataType LIST = eINSTANCE.getList();

		/**
		 * The meta object literal for the '<em>Sad Component Instantiation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
		 * @generated
		 */
		EDataType SAD_COMPONENT_INSTANTIATION = eINSTANCE.getSadComponentInstantiation();

		/**
		 * The meta object literal for the '<em>IAdd Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IAddContext
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIAddContext()
		 * @generated
		 */
		EDataType IADD_CONTEXT = eINSTANCE.getIAddContext();

		/**
		 * The meta object literal for the '<em>Abstract Container Pattern</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.ui.diagram.patterns.AbstractContainerPattern
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getAbstractContainerPattern()
		 * @generated
		 */
		EDataType ABSTRACT_CONTAINER_PATTERN = eINSTANCE.getAbstractContainerPattern();

		/**
		 * The meta object literal for the '<em>IUpdate Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IUpdateContext
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIUpdateContext()
		 * @generated
		 */
		EDataType IUPDATE_CONTEXT = eINSTANCE.getIUpdateContext();

		/**
		 * The meta object literal for the '<em>IColor Constant</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.util.IColorConstant
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getIColorConstant()
		 * @generated
		 */
		EDataType ICOLOR_CONSTANT = eINSTANCE.getIColorConstant();

		/**
		 * The meta object literal for the '<em>Map</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.Map
		 * @see gov.redhawk.ide.graphiti.ext.impl.RHGxPackageImpl#getMap()
		 * @generated
		 */
		EDataType MAP = eINSTANCE.getMap();

	}

} //RHGxPackage
