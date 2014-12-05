/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage
 * @generated
 */
public interface RHSadGxFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	RHSadGxFactory eINSTANCE = gov.redhawk.ide.sad.graphiti.ext.impl.RHSadGxFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Component Shape</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Component Shape</em>'.
	 * @generated
	 */
	ComponentShape createComponentShape();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	RHSadGxPackage getRHSadGxPackage();

} //RHSadGxFactory
