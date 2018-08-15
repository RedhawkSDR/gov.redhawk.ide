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

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nodes Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.NodesContainer#getNodes <em>Nodes</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.NodesContainer#getName <em>Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.NodesContainer#getChildContainers <em>Child Containers</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer()
 * @model
 * @generated
 */
public interface NodesContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Nodes</b></em>' reference list.
	 * The list contents are of type {@link mil.jpeojtrs.sca.dcd.DeviceConfiguration}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nodes</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nodes</em>' reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer_Nodes()
	 * @model
	 * @generated
	 */
	EList<DeviceConfiguration> getNodes();

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
	 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.NodesContainer#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Child Containers</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.sdr.NodesContainer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Containers</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Containers</em>' containment reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer_ChildContainers()
	 * @model containment="true"
	 * @generated
	 */
	EList<NodesContainer> getChildContainers();

} // NodesContainer
