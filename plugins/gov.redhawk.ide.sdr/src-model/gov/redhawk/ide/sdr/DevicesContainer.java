/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Devices Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.DevicesContainer#getChildContainers <em>Child Containers</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getDevicesContainer()
 * @model
 * @generated
 */
public interface DevicesContainer extends SoftPkgRegistry {
	/**
	 * Returns the value of the '<em><b>Child Containers</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.sdr.DevicesContainer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Containers</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Containers</em>' containment reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getDevicesContainer_ChildContainers()
	 * @model containment="true"
	 * @generated
	 */
	EList<DevicesContainer> getChildContainers();

} // DevicesContainer
