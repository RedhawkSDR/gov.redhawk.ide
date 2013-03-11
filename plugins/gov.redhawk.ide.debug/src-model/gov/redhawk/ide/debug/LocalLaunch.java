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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Launch</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalLaunch#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalLaunch#getMode <em>Mode</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalLaunch()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface LocalLaunch extends EObject {

	/**
	 * Returns the value of the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Launch</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Launch</em>' attribute.
	 * @see #setLaunch(ILaunch)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalLaunch_Launch()
	 * @model dataType="gov.redhawk.ide.debug.ILaunch" transient="true"
	 * @generated
	 */
	ILaunch getLaunch();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalLaunch#getLaunch <em>Launch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Launch</em>' attribute.
	 * @see #getLaunch()
	 * @generated
	 */
	void setLaunch(ILaunch value);

	/**
	 * Returns the value of the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mode</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mode</em>' attribute.
	 * @see #setMode(String)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalLaunch_Mode()
	 * @model transient="true"
	 * @generated
	 */
	String getMode();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalLaunch#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mode</em>' attribute.
	 * @see #getMode()
	 * @generated
	 */
	void setMode(String value);

} // LocalLaunch
