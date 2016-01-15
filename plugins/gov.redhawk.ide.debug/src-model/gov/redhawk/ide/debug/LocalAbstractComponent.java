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
package gov.redhawk.ide.debug;

/**
 * <!-- begin-user-doc -->
 * An abstract base class for sandbox objects.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalAbstractComponent#getImplementationID <em>Implementation ID</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParam <em>Exec Param</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalAbstractComponent()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface LocalAbstractComponent extends LocalLaunch {

	/**
	 * Returns the value of the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Gets the ID of the implementation that was launched.
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

	/**
	 * Returns the value of the '<em><b>Exec Param</b></em>' attribute.
	 * The default value is <code>""</code>.
	 * <!-- begin-user-doc -->
	 * Gets the string generated from execparam properties that was added to the command-line of the resource.
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Exec Param</em>' attribute.
	 * @see #setExecParam(String)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalAbstractComponent_ExecParam()
	 * @model default=""
	 * @generated
	 */
	String getExecParam();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParam <em>Exec Param</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Exec Param</em>' attribute.
	 * @see #getExecParam()
	 * @generated
	 */
	void setExecParam(String value);

} // LocalAbstractComponent
