/**
 */
package gov.redhawk.ide.graphiti.dcd.ext;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.graphiti.dcd.ext.RHDeviceGxPackage
 * @generated
 */
public interface RHDeviceGxFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHDeviceGxFactory eINSTANCE = gov.redhawk.ide.graphiti.dcd.ext.impl.RHDeviceGxFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Device Shape</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Device Shape</em>'.
	 * @generated
	 */
	DeviceShape createDeviceShape();

	/**
	 * Returns a new object of class '<em>Service Shape</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Service Shape</em>'.
	 * @generated
	 */
	ServiceShape createServiceShape();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RHDeviceGxPackage getRHDeviceGxPackage();

} //RHDeviceGxFactory
