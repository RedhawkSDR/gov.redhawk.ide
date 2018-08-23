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

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waveforms Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsContainer#getWaveforms <em>Waveforms</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsContainer#getName <em>Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsContainer#getChildContainers <em>Child Containers</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer()
 * @model
 * @generated
 */
public interface WaveformsContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Waveforms</b></em>' reference list.
	 * The list contents are of type {@link mil.jpeojtrs.sca.sad.SoftwareAssembly}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waveforms</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waveforms</em>' reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer_Waveforms()
	 * @model
	 * @generated
	 */
	EList<SoftwareAssembly> getWaveforms();

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
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.WaveformsContainer#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Child Containers</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.sdr.WaveformsContainer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child Containers</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child Containers</em>' containment reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer_ChildContainers()
	 * @model containment="true"
	 * @generated
	 */
	EList<WaveformsContainer> getChildContainers();

} // WaveformsContainer
