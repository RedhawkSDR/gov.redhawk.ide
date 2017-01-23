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

import java.util.List;

import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.ScaWaveform;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import ExtendedCF.Sandbox;
import ExtendedCF.SandboxOperations;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Sca</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getWaveforms <em>Waveforms</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getSandboxWaveform <em>Sandbox Waveform</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getSandboxDeviceManager <em>Sandbox Device Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getRootContext <em>Root Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getFileManager <em>File Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getSandbox <em>Sandbox</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca()
 * @model superTypes="gov.redhawk.model.sca.CorbaObjWrapper<gov.redhawk.ide.debug.Sandbox>"
 * @generated
 */
public interface LocalSca extends CorbaObjWrapper<Sandbox> {

	/**
	 * Returns the value of the '<em><b>Waveforms</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.debug.LocalScaWaveform}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waveforms</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waveforms</em>' containment reference list.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_Waveforms()
	 * @model containment="true" transient="true"
	 * @generated
	 */
	EList<LocalScaWaveform> getWaveforms();

	/**
	 * @since 6.0
	 */
	List<ScaWaveform> fetchWaveforms(IProgressMonitor monitor);

	/**
	 * Returns the value of the '<em><b>Sandbox Waveform</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sandbox Waveform</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sandbox Waveform</em>' reference.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_SandboxWaveform()
	 * @model required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	LocalScaWaveform getSandboxWaveform();

	/**
	 * Returns the value of the '<em><b>Sandbox Device Manager</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sandbox Device Manager</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sandbox Device Manager</em>' containment reference.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_SandboxDeviceManager()
	 * @model containment="true" required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	LocalScaDeviceManager getSandboxDeviceManager();

	/**
	 * Returns the value of the '<em><b>Root Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Root Context</em>' containment reference.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_RootContext()
	 * @model containment="true" required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	NotifyingNamingContext getRootContext();

	/**
	 * Returns the value of the '<em><b>File Manager</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File Manager</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File Manager</em>' containment reference.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_FileManager()
	 * @model containment="true" required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	LocalFileManager getFileManager();

	/**
	 * Returns the value of the '<em><b>Sandbox</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sandbox</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sandbox</em>' attribute.
	 * @see #setSandbox(SandboxOperations)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_Sandbox()
	 * @model unique="false" dataType="gov.redhawk.ide.debug.AttrSandboxOperations" transient="true" ordered="false"
	 * @generated
	 */
	SandboxOperations getSandbox();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalSca#getSandbox <em>Sandbox</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Sandbox</em>' attribute.
	 * @see #getSandbox()
	 * @generated
	 */
	void setSandbox(SandboxOperations value);

} // LocalSca
