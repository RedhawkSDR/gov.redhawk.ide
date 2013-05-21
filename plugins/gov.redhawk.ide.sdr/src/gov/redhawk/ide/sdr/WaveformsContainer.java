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

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Waveforms Container</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.WaveformsContainer#getSdrRoot <em>Sdr Root</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.WaveformsContainer#getWaveforms <em>Waveforms</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer()
 * @model
 * @generated
 */
public interface WaveformsContainer extends EObject {

	/**
	 * Returns the value of the '<em><b>Sdr Root</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer <em>Waveforms Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sdr Root</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sdr Root</em>' container reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getWaveformsContainer_SdrRoot()
	 * @see gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer
	 * @model opposite="waveformsContainer" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	SdrRoot getSdrRoot();

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

} // WaveformsContainer
