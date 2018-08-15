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
 * A representation of the model object '<em><b>Waveforms Sub Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsSubContainer#getSubContainers <em>Sub Containers</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsSubContainer#getContainerName <em>Container Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.WaveformsSubContainer#getWaveforms <em>Waveforms</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsSubContainer()
 * @model
 * @generated
 */
public interface WaveformsSubContainer extends EObject {
	/**
	 * Returns the value of the '<em><b>Sub Containers</b></em>' reference list.
	 * The list contents are of type {@link gov.redhawk.ide.sdr.WaveformsSubContainer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Containers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Containers</em>' reference list.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsSubContainer_SubContainers()
	 * @model
	 * @generated
	 */
	EList<WaveformsSubContainer> getSubContainers();

	/**
	 * Returns the value of the '<em><b>Container Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Container Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Container Name</em>' attribute.
	 * @see #setContainerName(String)
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsSubContainer_ContainerName()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String"
	 * @generated
	 */
	String getContainerName();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.WaveformsSubContainer#getContainerName <em>Container
	 * Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Container Name</em>' attribute.
	 * @see #getContainerName()
	 * @generated
	 */
	void setContainerName(String value);

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
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsSubContainer_Waveforms()
	 * @model
	 * @generated
	 */
	EList<SoftwareAssembly> getWaveforms();

} // WaveformsSubContainer
