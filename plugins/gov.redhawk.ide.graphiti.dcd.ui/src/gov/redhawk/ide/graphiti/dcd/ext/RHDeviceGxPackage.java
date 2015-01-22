/**
 */
package gov.redhawk.ide.graphiti.dcd.ext;

import gov.redhawk.ide.graphiti.ext.RHGxPackage;

import org.eclipse.emf.ecore.EClass;
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
 * @see gov.redhawk.ide.graphiti.dcd.ext.RHDeviceGxFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel prefix='RHDeviceGx' codeFormatting='true' modelPluginVariables='org.eclipse.xtext.xbase.lib' contentTypeIdentifier='http://www.redhawk.gov/model/rhdevicegext/1.0.0' operationReflection='false' modelDirectory='/gov.redhawk.ide.graphiti.dcd.ui/src' basePackage='gov.redhawk.ide.graphiti.dcd'"
 * @generated
 */
public interface RHDeviceGxPackage extends EPackage {
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
	String eNS_URI = "http://www.redhawk.gov/model/rhdevicegext/1.0.0";

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
	String eCONTENT_TYPE = "http://www.redhawk.gov/model/rhdevicegext/1.0.0";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHDeviceGxPackage eINSTANCE = gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxPackageImpl.init();

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl <em>Device Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl
	 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxPackageImpl#getDeviceShape()
	 * @generated
	 */
	int DEVICE_SHAPE = 0;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__PROPERTIES = RHGxPackage.RH_CONTAINER_SHAPE__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__VISIBLE = RHGxPackage.RH_CONTAINER_SHAPE__VISIBLE;

	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__GRAPHICS_ALGORITHM = RHGxPackage.RH_CONTAINER_SHAPE__GRAPHICS_ALGORITHM;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__ACTIVE = RHGxPackage.RH_CONTAINER_SHAPE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__LINK = RHGxPackage.RH_CONTAINER_SHAPE__LINK;

	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__ANCHORS = RHGxPackage.RH_CONTAINER_SHAPE__ANCHORS;

	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__CONTAINER = RHGxPackage.RH_CONTAINER_SHAPE__CONTAINER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__CHILDREN = RHGxPackage.RH_CONTAINER_SHAPE__CHILDREN;

	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__STARTED = RHGxPackage.RH_CONTAINER_SHAPE__STARTED;

	/**
	 * The feature id for the '<em><b>Update Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__UPDATE_PORTS = RHGxPackage.RH_CONTAINER_SHAPE__UPDATE_PORTS;

	/**
	 * The feature id for the '<em><b>IStatus Error State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__ISTATUS_ERROR_STATE = RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE;

	/**
	 * The feature id for the '<em><b>Connection Map</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__CONNECTION_MAP = RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP;

	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__EVENT = RHGxPackage.RH_CONTAINER_SHAPE__EVENT;

	/**
	 * The feature id for the '<em><b>Create Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Create Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__CREATE_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__CREATE_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Has Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Has Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__HAS_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Hide Unused Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE__HIDE_UNUSED_PORTS = RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS;

	/**
	 * The number of structural features of the '<em>Device Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICE_SHAPE_FEATURE_COUNT = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl <em>Service Shape</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl
	 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxPackageImpl#getServiceShape()
	 * @generated
	 */
	int SERVICE_SHAPE = 1;

	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__PROPERTIES = RHGxPackage.RH_CONTAINER_SHAPE__PROPERTIES;

	/**
	 * The feature id for the '<em><b>Visible</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__VISIBLE = RHGxPackage.RH_CONTAINER_SHAPE__VISIBLE;

	/**
	 * The feature id for the '<em><b>Graphics Algorithm</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__GRAPHICS_ALGORITHM = RHGxPackage.RH_CONTAINER_SHAPE__GRAPHICS_ALGORITHM;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__ACTIVE = RHGxPackage.RH_CONTAINER_SHAPE__ACTIVE;

	/**
	 * The feature id for the '<em><b>Link</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__LINK = RHGxPackage.RH_CONTAINER_SHAPE__LINK;

	/**
	 * The feature id for the '<em><b>Anchors</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__ANCHORS = RHGxPackage.RH_CONTAINER_SHAPE__ANCHORS;

	/**
	 * The feature id for the '<em><b>Container</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__CONTAINER = RHGxPackage.RH_CONTAINER_SHAPE__CONTAINER;

	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__CHILDREN = RHGxPackage.RH_CONTAINER_SHAPE__CHILDREN;

	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__STARTED = RHGxPackage.RH_CONTAINER_SHAPE__STARTED;

	/**
	 * The feature id for the '<em><b>Update Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__UPDATE_PORTS = RHGxPackage.RH_CONTAINER_SHAPE__UPDATE_PORTS;

	/**
	 * The feature id for the '<em><b>IStatus Error State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__ISTATUS_ERROR_STATE = RHGxPackage.RH_CONTAINER_SHAPE__ISTATUS_ERROR_STATE;

	/**
	 * The feature id for the '<em><b>Connection Map</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__CONNECTION_MAP = RHGxPackage.RH_CONTAINER_SHAPE__CONNECTION_MAP;

	/**
	 * The feature id for the '<em><b>Event</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__EVENT = RHGxPackage.RH_CONTAINER_SHAPE__EVENT;

	/**
	 * The feature id for the '<em><b>Create Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__CREATE_SUPER_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Create Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__CREATE_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__CREATE_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Has Super Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_SUPER_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Has Ports Container Shape</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__HAS_PORTS_CONTAINER_SHAPE = RHGxPackage.RH_CONTAINER_SHAPE__HAS_PORTS_CONTAINER_SHAPE;

	/**
	 * The feature id for the '<em><b>Hide Unused Ports</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE__HIDE_UNUSED_PORTS = RHGxPackage.RH_CONTAINER_SHAPE__HIDE_UNUSED_PORTS;

	/**
	 * The number of structural features of the '<em>Service Shape</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICE_SHAPE_FEATURE_COUNT = RHGxPackage.RH_CONTAINER_SHAPE_FEATURE_COUNT + 0;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.graphiti.dcd.ext.DeviceShape <em>Device Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Device Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.dcd.ext.DeviceShape
	 * @generated
	 */
	EClass getDeviceShape();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.graphiti.dcd.ext.ServiceShape <em>Service Shape</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Service Shape</em>'.
	 * @see gov.redhawk.ide.graphiti.dcd.ext.ServiceShape
	 * @generated
	 */
	EClass getServiceShape();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	RHDeviceGxFactory getRHDeviceGxFactory();

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
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl <em>Device Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.DeviceShapeImpl
		 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxPackageImpl#getDeviceShape()
		 * @generated
		 */
		EClass DEVICE_SHAPE = eINSTANCE.getDeviceShape();

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl <em>Service Shape</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.ServiceShapeImpl
		 * @see gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxPackageImpl#getServiceShape()
		 * @generated
		 */
		EClass SERVICE_SHAPE = eINSTANCE.getServiceShape();

	}

} //RHDeviceGxPackage
