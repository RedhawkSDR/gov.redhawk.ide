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
package gov.redhawk.ide.debug;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Abstract Component</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParams <em>Exec Params</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalAbstractComponent#getImplementationID <em>Implementation ID</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalAbstractComponent()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface LocalAbstractComponent extends LocalLaunch {

	/**
	 * Returns the value of the '<em><b>Exec Params</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Exec Params</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exec Params</em>' attribute list.
	 * @see #isSetExecParams()
	 * @see #unsetExecParams()
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalAbstractComponent_ExecParams()
	 * @model unsettable="true" transient="true"
	 * @generated
	 */
	EList<String> getExecParams();

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParams <em>Exec Params</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetExecParams()
	 * @see #getExecParams()
	 * @generated
	 */
	void unsetExecParams();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParams <em>Exec Params</em>}' attribute list is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Exec Params</em>' attribute list is set.
	 * @see #unsetExecParams()
	 * @see #getExecParams()
	 * @generated
	 */
	boolean isSetExecParams();

	/**
	 * Returns the value of the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Implementation ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Implementation ID</em>' attribute.
	 * @see #setImplementationID(String)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalAbstractComponent_ImplementationID()
	 * @model
	 * @generated
	 */
	String getImplementationID();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getImplementationID <em>Implementation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Implementation ID</em>' attribute.
	 * @see #getImplementationID()
	 * @generated
	 */
	void setImplementationID(String value);

} // LocalAbstractComponent
