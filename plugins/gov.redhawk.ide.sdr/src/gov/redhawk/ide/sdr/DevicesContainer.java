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
package gov.redhawk.ide.sdr;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Devices Container</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.DevicesContainer#getSdrRoot <em>Sdr Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getDevicesContainer()
 * @model
 * @generated
 */
public interface DevicesContainer extends SoftPkgRegistry {

	/**
	 * Returns the value of the '<em><b>Sdr Root</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer <em>Devices Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sdr Root</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sdr Root</em>' container reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getDevicesContainer_SdrRoot()
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer
	 * @model opposite="devicesContainer" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	SdrRoot getSdrRoot();

} // DevicesContainer
