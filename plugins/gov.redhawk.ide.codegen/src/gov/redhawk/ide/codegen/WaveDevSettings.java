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
package gov.redhawk.ide.codegen;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Wave Dev Settings</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.WaveDevSettings#getImplSettings <em>Impl Settings</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.codegen.CodegenPackage#getWaveDevSettings()
 * @model
 * @generated
 */
public interface WaveDevSettings extends EObject {

	/**
	 * Returns the value of the '<em><b>Impl Settings</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link gov.redhawk.ide.codegen.ImplementationSettings},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Impl Settings</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Impl Settings</em>' map.
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getWaveDevSettings_ImplSettings()
	 * @model mapType="gov.redhawk.ide.codegen.ImplIdToSettingsMap<org.eclipse.emf.ecore.EString, gov.redhawk.ide.codegen.ImplementationSettings>"
	 * @generated
	 */
	EMap<String, ImplementationSettings> getImplSettings();

} // WaveDevSettings
