/**
 */
package gov.redhawk.ide.graphiti.ext;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Port Update Status</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * *
 * Used to enable/disable style updates on ports, such as highlighting ports during certain events
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.PortUpdateStatus#isPortsUpdatable <em>Ports Updatable</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.PortUpdateStatus#getSettingObject <em>Setting Object</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.graphiti.ext.RHGxPackage#getPortUpdateStatus()
 * @model
 * @generated
 */
public interface PortUpdateStatus extends EObject {
	/**
	 * Returns the value of the '<em><b>Ports Updatable</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ports Updatable</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ports Updatable</em>' attribute.
	 * @see #setPortsUpdatable(boolean)
	 * @see gov.redhawk.ide.graphiti.ext.RHGxPackage#getPortUpdateStatus_PortsUpdatable()
	 * @model unique="false"
	 * @generated
	 */
	boolean isPortsUpdatable();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.graphiti.ext.PortUpdateStatus#isPortsUpdatable <em>Ports Updatable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ports Updatable</em>' attribute.
	 * @see #isPortsUpdatable()
	 * @generated
	 */
	void setPortsUpdatable(boolean value);

	/**
	 * Returns the value of the '<em><b>Setting Object</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Setting Object</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Setting Object</em>' attribute.
	 * @see #setSettingObject(Object)
	 * @see gov.redhawk.ide.graphiti.ext.RHGxPackage#getPortUpdateStatus_SettingObject()
	 * @model unique="false"
	 * @generated
	 */
	Object getSettingObject();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.graphiti.ext.PortUpdateStatus#getSettingObject <em>Setting Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Setting Object</em>' attribute.
	 * @see #getSettingObject()
	 * @generated
	 */
	void setSettingObject(Object value);

} // PortUpdateStatus
