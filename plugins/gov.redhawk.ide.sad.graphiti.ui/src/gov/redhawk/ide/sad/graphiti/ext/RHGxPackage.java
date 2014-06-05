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
package gov.redhawk.ide.sad.graphiti.ext;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.graphiti.mm.pictograms.PictogramsPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.sad.graphiti.ext.RHGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel 
 *        	prefix='RHGx' 
 *        	codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' 
 *        	contentTypeIdentifier='http://www.redhawk.gov/model/rhgext/1.0.0' 
 *        	operationReflection='false' modelDirectory='/gov.redhawk.ide.sad.graphiti.ui/src' 
 *        	basePackage='gov.redhawk.ide.sad.graphiti'"
 * @generated
 */
public interface RHGxPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String E_NAME = "ext";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String E_NS_URI = "http://www.redhawk.gov/model/rhgext/1.0.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String E_NS_PREFIX = "ext";

	/**
	 * The package content type ID.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String E_CONTENT_TYPE = "http://www.redhawk.gov/model/rhgext/1.0.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHGxPackage E_INSTANCE = gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getContainerShapeImpl()
	 * @generated
	 */
	int CONTAINER_SHAPE_IMPL = 0;

	/**
	 * The number of structural features of the '<em>Container Shape Impl</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONTAINER_SHAPE_IMPL_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
	 * @generated
	 */
	int RH_CONTAINER_SHAPE = 1;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_PROPERTIES = PictogramsPackage.CONTAINER_SHAPE__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_VISIBLE = PictogramsPackage.CONTAINER_SHAPE__VISIBLE;

	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_GRAPHICS_ALGORITHM = PictogramsPackage.CONTAINER_SHAPE__GRAPHICS_ALGORITHM;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_ACTIVE = PictogramsPackage.CONTAINER_SHAPE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_LINK = PictogramsPackage.CONTAINER_SHAPE__LINK;

	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_ANCHORS = PictogramsPackage.CONTAINER_SHAPE__ANCHORS;

	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_CONTAINER = PictogramsPackage.CONTAINER_SHAPE__CONTAINER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_CHILDREN = PictogramsPackage.CONTAINER_SHAPE__CHILDREN;

	/**
	 * The number of structural features of the '<em>RH Container Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RH_CONTAINER_SHAPE_FEATURE_COUNT = PictogramsPackage.CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getComponentShape()
	 * @generated
	 */
	int COMPONENT_SHAPE = 2;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_PROPERTIES = RH_CONTAINER_SHAPE_PROPERTIES;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_VISIBLE = RH_CONTAINER_SHAPE_VISIBLE;

	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_GRAPHICS_ALGORITHM = RH_CONTAINER_SHAPE_GRAPHICS_ALGORITHM;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_ACTIVE = RH_CONTAINER_SHAPE_ACTIVE;

	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_LINK = RH_CONTAINER_SHAPE_LINK;

	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_ANCHORS = RH_CONTAINER_SHAPE_ANCHORS;

	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_CONTAINER = RH_CONTAINER_SHAPE_CONTAINER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_CHILDREN = RH_CONTAINER_SHAPE_CHILDREN;

	/**
	 * The number of structural features of the '<em>Component Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_FEATURE_COUNT = RH_CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '<em>Component Supported Interface Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
	 * @generated
	 */
	int COMPONENT_SUPPORTED_INTERFACE_STUB = 3;

	/**
	 * The meta object id for the '<em>IFeature Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
	 * @generated
	 */
	int IFEATURE_PROVIDER = 4;

	/**
	 * The meta object id for the '<em>Uses Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
	 * @generated
	 */
	int USES_PORT_STUB = 5;

	/**
	 * The meta object id for the '<em>Provides Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
	 * @generated
	 */
	int PROVIDES_PORT_STUB = 6;

	/**
	 * The meta object id for the '<em>Port</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 7;

	/**
	 * The meta object id for the '<em>Assembly Controller</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
	 * @generated
	 */
	int ASSEMBLY_CONTROLLER = 8;

	/**
	 * The meta object id for the '<em>External Ports</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
	 * @generated
	 */
	int EXTERNAL_PORTS = 9;

	/**
	 * The meta object id for the '<em>Reason</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getReason()
	 * @generated
	 */
	int REASON = 10;

	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getList()
	 * @generated
	 */
	int LIST = 11;

	/**
	 * The meta object id for the '<em>Sad Component Instantiation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
	 * @generated
	 */
	int SAD_COMPONENT_INSTANTIATION = 12;

	/**
	 * Returns the meta object for class '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Container Shape Impl</em>'.
	 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
	 * @model instanceClass="org.eclipse.graphiti.mm.pictograms.ContainerShape"
	 * @generated
	 */
	EClass getContainerShapeImpl();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sad.graphiti.ext.RHContainerShape <em>RH Container Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RH Container Shape</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.RHContainerShape
	 * @generated
	 */
	EClass getRHContainerShape();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape <em>Component Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Shape</em>'.
	 * @see gov.redhawk.ide.sad.graphiti.ext.ComponentShape
	 * @generated
	 */
	EClass getComponentShape();

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
	 * <li>each class,</li>
	 * <li>each feature of each class,</li>
	 * <li>each enum,</li>
	 * <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.graphiti.mm.pictograms.ContainerShape <em>Container Shape Impl</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.mm.pictograms.ContainerShape
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getContainerShapeImpl()
		 * @generated
		 */
		EClass CONTAINER_SHAPE_IMPL = E_INSTANCE.getContainerShapeImpl();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl <em>RH Container Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHContainerShapeImpl
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getRHContainerShape()
		 * @generated
		 */
		EClass RH_CONTAINER_SHAPE = E_INSTANCE.getRHContainerShape();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.ComponentShapeImpl
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getComponentShape()
		 * @generated
		 */
		EClass COMPONENT_SHAPE = E_INSTANCE.getComponentShape();

		/**
		 * The meta object literal for the '<em>Component Supported Interface Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getComponentSupportedInterfaceStub()
		 * @generated
		 */
		EDataType COMPONENT_SUPPORTED_INTERFACE_STUB = E_INSTANCE.getComponentSupportedInterfaceStub();

		/**
		 * The meta object literal for the '<em>IFeature Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.IFeatureProvider
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getIFeatureProvider()
		 * @generated
		 */
		EDataType IFEATURE_PROVIDER = E_INSTANCE.getIFeatureProvider();

		/**
		 * The meta object literal for the '<em>Uses Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getUsesPortStub()
		 * @generated
		 */
		EDataType USES_PORT_STUB = E_INSTANCE.getUsesPortStub();

		/**
		 * The meta object literal for the '<em>Provides Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getProvidesPortStub()
		 * @generated
		 */
		EDataType PROVIDES_PORT_STUB = E_INSTANCE.getProvidesPortStub();

		/**
		 * The meta object literal for the '<em>Port</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.Port
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getPort()
		 * @generated
		 */
		EDataType PORT = E_INSTANCE.getPort();

		/**
		 * The meta object literal for the '<em>Assembly Controller</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.AssemblyController
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getAssemblyController()
		 * @generated
		 */
		EDataType ASSEMBLY_CONTROLLER = E_INSTANCE.getAssemblyController();

		/**
		 * The meta object literal for the '<em>External Ports</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.ExternalPorts
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getExternalPorts()
		 * @generated
		 */
		EDataType EXTERNAL_PORTS = E_INSTANCE.getExternalPorts();

		/**
		 * The meta object literal for the '<em>Reason</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.impl.Reason
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getReason()
		 * @generated
		 */
		EDataType REASON = E_INSTANCE.getReason();

		/**
		 * The meta object literal for the '<em>List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getList()
		 * @generated
		 */
		EDataType LIST = E_INSTANCE.getList();

		/**
		 * The meta object literal for the '<em>Sad Component Instantiation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
		 * @see gov.redhawk.ide.sad.graphiti.ext.impl.RHGxPackageImpl#getSadComponentInstantiation()
		 * @generated
		 */
		EDataType SAD_COMPONENT_INSTANTIATION = E_INSTANCE.getSadComponentInstantiation();

	}

} // RHGxPackage
