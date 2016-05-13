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

import CF.DataType;
import gov.redhawk.model.sca.ScaDeviceManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import CF.DeviceManagerOperations;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Sca Device Manager</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getLocalDeviceManager <em>Local Device Manager</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalScaDeviceManager()
 * @model
 * @generated
 */
public interface LocalScaDeviceManager extends ScaDeviceManager, LocalLaunch {

	/**
	 * Returns the value of the '<em><b>Naming Context</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Naming Context</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Naming Context</em>' reference.
	 * @see #setNamingContext(NotifyingNamingContext)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalScaDeviceManager_NamingContext()
	 * @model required="true" transient="true"
	 * @generated
	 */
	NotifyingNamingContext getNamingContext();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getNamingContext <em>Naming Context</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Naming Context</em>' reference.
	 * @see #getNamingContext()
	 * @generated
	 */
	void setNamingContext(NotifyingNamingContext value);

	/**
	 * Returns the value of the '<em><b>Local Device Manager</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Local Device Manager</em>' attribute.
	 * @see #setLocalDeviceManager(DeviceManagerOperations)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalScaDeviceManager_LocalDeviceManager()
	 * @model unique="false" dataType="gov.redhawk.ide.debug.AttrDeviceManagerOperations" transient="true" ordered="false"
	 * @generated
	 */
	DeviceManagerOperations getLocalDeviceManager();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getLocalDeviceManager <em>Local Device Manager</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Local Device Manager</em>' attribute.
	 * @see #getLocalDeviceManager()
	 * @generated
	 */
	void setLocalDeviceManager(DeviceManagerOperations value);

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.2
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * @param initConfiguration The initial configuration of the resource (properties including exec params, configure params, etc).
	 * <!-- end-model-doc -->
	 * @model exceptions="gov.redhawk.ide.debug.CoreException" initConfigurationDataType="gov.redhawk.model.sca.DataTypeArray" spdURIDataType="mil.jpeojtrs.sca.spd.URI"
	 * @generated
	 */
	LocalAbstractComponent launch(String id, DataType[] initConfiguration, URI spdURI, String implID, String mode) throws CoreException;

} // LocalScaDeviceManager
