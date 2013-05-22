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

import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Nodes Container</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.NodesContainer#getSdrRoot <em>Sdr Root</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.NodesContainer#getNodes <em>Nodes</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer()
 * @model
 * @generated
 */
public interface NodesContainer extends EObject {

	/**
	 * Returns the value of the '<em><b>Sdr Root</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.SdrRoot#getNodesContainer <em>Nodes Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sdr Root</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sdr Root</em>' container reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getNodesContainer_SdrRoot()
	 * @see gov.redhawk.ide.sdr.SdrRoot#getNodesContainer
	 * @model opposite="nodesContainer" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	SdrRoot getSdrRoot();

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

} // NodesContainer
