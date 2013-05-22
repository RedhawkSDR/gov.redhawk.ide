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
package gov.redhawk.ide.sdr;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Soft Pkg Registry</b></em>'.
 * @since 8.0
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SoftPkgRegistry#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getSoftPkgRegistry()
 * @model abstract="true"
 * @generated
 */
public interface SoftPkgRegistry extends PropertyRegistry {

	/**
	 * Returns the value of the '<em><b>Components</b></em>' reference list.
	 * The list contents are of type {@link mil.jpeojtrs.sca.spd.SoftPkg}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Components</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components</em>' reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSoftPkgRegistry_Components()
	 * @model
	 * @generated
	 */
	EList<SoftPkg> getComponents();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model softPkgIdDataType="mil.jpeojtrs.sca.prf.DceUUID"
	 * @generated
	 */
	SoftPkg getSoftPkg(String softPkgId);

} // SoftPkgRegistry
