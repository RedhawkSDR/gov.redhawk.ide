/**
 */
package gov.redhawk.ide.graphiti.sad.ext;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;

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
 * @see gov.redhawk.ide.graphiti.sad.ext.RHSadGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel prefix='RHSadGx' codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' contentTypeIdentifier='http://www.redhawk.gov/model/rhsadgext/1.0.0' operationReflection='false' modelDirectory='/gov.redhawk.ide.graphiti.sad.ui/src' basePackage='gov.redhawk.ide.graphiti.sad'"
 *        annotation="http://www.eclipse.org/emf/2011/Xcore GenModel='http://www.eclipse.org/emf/2002/GenModel' Ecore='http://www.eclipse.org/emf/2002/Ecore'"
 * @generated
 */
public interface RHSadGxPackage extends EPackage {

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
	String eNS_URI = "http://www.redhawk.gov/model/rhsadgext/1.0.0";
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
	String eCONTENT_TYPE = "http://www.redhawk.gov/model/rhsadgext/1.0.0";
	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHSadGxPackage eINSTANCE = gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl.init();
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentShape()
	 * @generated
	 */
	int COMPONENT_SHAPE = 0;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__PROPERTIES = RHGxPackage.RH_CONTAINER_SHAPE__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__VISIBLE = RHGxPackage.RH_CONTAINER_SHAPE__VISIBLE;
	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__GRAPHICS_ALGORITHM = RHGxPackage.RH_CONTAINER_SHAPE__GRAPHICS_ALGORITHM;
	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__ACTIVE = RHGxPackage.RH_CONTAINER_SHAPE__ACTIVE;
	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__LINK = RHGxPackage.RH_CONTAINER_SHAPE__LINK;
	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__ANCHORS = RHGxPackage.RH_CONTAINER_SHAPE__ANCHORS;
	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__CONTAINER = RHGxPackage.RH_CONTAINER_SHAPE__CONTAINER;
	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__CHILDREN = RHGxPackage.RH_CONTAINER_SHAPE__CHILDREN;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__STARTED = RHGxPackage.RH_CONTAINER_SHAPE__STARTED;
	/**
	 * The feature id for the '<em><b>IStatus Error State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__ISTATUS_ERROR_STATE = RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE;
	/**
	 * The feature id for the '<em><b>Connection Map</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__CONNECTION_MAP = RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP;
	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__EVENT = RHGxPackage.RH_CONTAINER_SHAPE__EVENT;
	/**
	 * The feature id for the '<em><b>Port Update Status</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__PORT_UPDATE_STATUS = RHGxPackage.RH_CONTAINER_SHAPE__PORT_UPDATE_STATUS;
	/**
	 * The feature id for the '<em><b>Has Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE;
	/**
	 * The feature id for the '<em><b>Has Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__HAS_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE;
	/**
	 * The feature id for the '<em><b>Hide Unused Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE__HIDE_UNUSED_PORTS = RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS;
	/**
	 * The number of structural features of the '<em>Component Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENT_SHAPE_FEATURE_COUNT = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 0;
	/**
	 * The meta object id for the '<em>Component Supported Interface Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentSupportedInterfaceStub()
	 * @generated
	 */
	int COMPONENT_SUPPORTED_INTERFACE_STUB = 1;
	/**
	 * The meta object id for the '<em>IFeature Provider</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.IFeatureProvider
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIFeatureProvider()
	 * @generated
	 */
	int IFEATURE_PROVIDER = 2;
	/**
	 * The meta object id for the '<em>Uses Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getUsesPortStub()
	 * @generated
	 */
	int USES_PORT_STUB = 3;
	/**
	 * The meta object id for the '<em>Provides Port Stub</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getProvidesPortStub()
	 * @generated
	 */
	int PROVIDES_PORT_STUB = 4;
	/**
	 * The meta object id for the '<em>Port</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.Port
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getPort()
	 * @generated
	 */
	int PORT = 5;
	/**
	 * The meta object id for the '<em>Assembly Controller</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.AssemblyController
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getAssemblyController()
	 * @generated
	 */
	int ASSEMBLY_CONTROLLER = 6;
	/**
	 * The meta object id for the '<em>External Ports</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.ExternalPorts
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getExternalPorts()
	 * @generated
	 */
	int EXTERNAL_PORTS = 7;
	/**
	 * The meta object id for the '<em>Reason</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.impl.Reason
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getReason()
	 * @generated
	 */
	int REASON = 8;
	/**
	 * The meta object id for the '<em>List</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see java.util.List
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getList()
	 * @generated
	 */
	int LIST = 9;
	/**
	 * The meta object id for the '<em>Sad Component Instantiation</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getSadComponentInstantiation()
	 * @generated
	 */
	int SAD_COMPONENT_INSTANTIATION = 10;
	/**
	 * The meta object id for the '<em>IAdd Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IAddContext
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIAddContext()
	 * @generated
	 */
	int IADD_CONTEXT = 11;
	/**
	 * The meta object id for the '<em>Component Pattern</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentPattern()
	 * @generated
	 */
	int COMPONENT_PATTERN = 12;
	/**
	 * The meta object id for the '<em>IUpdate Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.graphiti.features.context.IUpdateContext
	 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIUpdateContext()
	 * @generated
	 */
	int IUPDATE_CONTEXT = 13;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.graphiti.sad.ext.ComponentShape <em>Component Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Component Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.sad.ext.ComponentShape
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
	 * Returns the meta object for data type '{@link gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern <em>Component Pattern</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Component Pattern</em>'.
	 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
	 * @model instanceClass="gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern"
	 * @generated
	 */
	EDataType getComponentPattern();

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
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RHSadGxFactory getRHSadGxFactory();

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
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl <em>Component Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.ComponentShapeImpl
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentShape()
		 * @generated
		 */
		EClass COMPONENT_SHAPE = eINSTANCE.getComponentShape();

		/**
		 * The meta object literal for the '<em>Component Supported Interface Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ComponentSupportedInterfaceStub
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentSupportedInterfaceStub()
		 * @generated
		 */
		EDataType COMPONENT_SUPPORTED_INTERFACE_STUB = eINSTANCE.getComponentSupportedInterfaceStub();

		/**
		 * The meta object literal for the '<em>IFeature Provider</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.IFeatureProvider
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIFeatureProvider()
		 * @generated
		 */
		EDataType IFEATURE_PROVIDER = eINSTANCE.getIFeatureProvider();

		/**
		 * The meta object literal for the '<em>Uses Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.UsesPortStub
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getUsesPortStub()
		 * @generated
		 */
		EDataType USES_PORT_STUB = eINSTANCE.getUsesPortStub();

		/**
		 * The meta object literal for the '<em>Provides Port Stub</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.partitioning.ProvidesPortStub
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getProvidesPortStub()
		 * @generated
		 */
		EDataType PROVIDES_PORT_STUB = eINSTANCE.getProvidesPortStub();

		/**
		 * The meta object literal for the '<em>Port</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.Port
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getPort()
		 * @generated
		 */
		EDataType PORT = eINSTANCE.getPort();

		/**
		 * The meta object literal for the '<em>Assembly Controller</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.AssemblyController
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getAssemblyController()
		 * @generated
		 */
		EDataType ASSEMBLY_CONTROLLER = eINSTANCE.getAssemblyController();

		/**
		 * The meta object literal for the '<em>External Ports</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.ExternalPorts
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getExternalPorts()
		 * @generated
		 */
		EDataType EXTERNAL_PORTS = eINSTANCE.getExternalPorts();

		/**
		 * The meta object literal for the '<em>Reason</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.impl.Reason
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getReason()
		 * @generated
		 */
		EDataType REASON = eINSTANCE.getReason();

		/**
		 * The meta object literal for the '<em>List</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see java.util.List
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getList()
		 * @generated
		 */
		EDataType LIST = eINSTANCE.getList();

		/**
		 * The meta object literal for the '<em>Sad Component Instantiation</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see mil.jpeojtrs.sca.sad.SadComponentInstantiation
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getSadComponentInstantiation()
		 * @generated
		 */
		EDataType SAD_COMPONENT_INSTANTIATION = eINSTANCE.getSadComponentInstantiation();

		/**
		 * The meta object literal for the '<em>IAdd Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IAddContext
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIAddContext()
		 * @generated
		 */
		EDataType IADD_CONTEXT = eINSTANCE.getIAddContext();

		/**
		 * The meta object literal for the '<em>Component Pattern</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.sad.ui.diagram.patterns.ComponentPattern
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getComponentPattern()
		 * @generated
		 */
		EDataType COMPONENT_PATTERN = eINSTANCE.getComponentPattern();

		/**
		 * The meta object literal for the '<em>IUpdate Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.graphiti.features.context.IUpdateContext
		 * @see gov.redhawk.ide.graphiti.sad.ext.impl.RHSadGxPackageImpl#getIUpdateContext()
		 * @generated
		 */
		EDataType IUPDATE_CONTEXT = eINSTANCE.getIUpdateContext();

	}

} //RHSadGxPackage
