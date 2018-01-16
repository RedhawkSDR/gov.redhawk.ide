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
import org.eclipse.emf.ecore.EObject;

import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Soft Pkg Registry</b></em>'.
 * @since 8.0
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SoftPkgRegistry#getName <em>Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SoftPkgRegistry#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getSoftPkgRegistry()
 * @model abstract="true"
 * @generated
 */
public interface SoftPkgRegistry extends EObject {

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSoftPkgRegistry_Name()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.SoftPkgRegistry#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Components</b></em>' reference list.
	 * The list contents are of type {@link mil.jpeojtrs.sca.spd.SoftPkg}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * Gets the components in this container (i.e. in this namespace).
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
	 * Finds a {@link SoftPkg} by ID. Descendant containers will also be searched for the specified {@link SoftPkg} if
	 * it is not found in this container.
	 * <!-- end-user-doc -->
	 * @model softPkgIdDataType="mil.jpeojtrs.sca.prf.DceUUID"
	 * @generated
	 */
	SoftPkg getSoftPkg(String softPkgId);

	/**
	 * <!-- begin-user-doc -->
	 * Gets all components in this container and any descendant containers. The list is computed on invocation, and
	 * the operation should be called in a protected model context.
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	EList<SoftPkg> getAllComponents();

} // SoftPkgRegistry
