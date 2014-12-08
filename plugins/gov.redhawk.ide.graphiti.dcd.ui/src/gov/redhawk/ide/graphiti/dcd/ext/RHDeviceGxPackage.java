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
