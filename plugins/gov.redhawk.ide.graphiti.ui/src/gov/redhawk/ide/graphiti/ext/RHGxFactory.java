/**
 */
package gov.redhawk.ide.graphiti.ext;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.graphiti.ext.RHGxPackage
 * @generated
 */
public interface RHGxFactory extends EFactory {

	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHGxFactory eINSTANCE = gov.redhawk.ide.graphiti.ext.impl.RHGxFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>RH Container Shape</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>RH Container Shape</em>'.
	 * @generated
	 */
	RHContainerShape createRHContainerShape();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RHGxPackage getRHGxPackage();

} //RHGxFactory
