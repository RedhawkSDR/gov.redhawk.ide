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

import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * An interface providing methods to access information about an object launched locally in the IDE's sandbox.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalLaunch#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalLaunch#getMode <em>Mode</em>}</li>
 * </ul>
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
	 * Returns the {@link ILaunch} for this object, provided it was launched by the IDE. The launch may be null if the
	 * object is just a proxy for an object in a domain.
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
	 * A constant from {@link org.eclipse.debug.core.ILaunchManager} indicating what mode the launch is in (e.g. debug,
	 * run, etc).
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
