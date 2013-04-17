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
package gov.redhawk.ide.debug;

import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.model.sca.ScaWaveform;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Local Sca</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getOrb <em>Orb</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getPoa <em>Poa</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getWaveforms <em>Waveforms</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getSandboxWaveform <em>Sandbox Waveform</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getSandboxDeviceManager <em>Sandbox Device Manager</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getRootContext <em>Root Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalSca#getFileManager <em>File Manager</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca()
 * @model
 * @generated
 */
public interface LocalSca extends IDisposable {

	/**
	 * Returns the value of the '<em><b>Orb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Orb</em>' attribute.
	 * @see #setOrb(ORB)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_Orb()
	 * @model dataType="mil.jpeojtrs.sca.cf.ORB" transient="true"
	 * @generated
	 */
	ORB getOrb();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalSca#getOrb <em>Orb</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Orb</em>' attribute.
	 * @see #getOrb()
	 * @generated
	 */
	void setOrb(ORB value);

	/**
	 * Returns the value of the '<em><b>Poa</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Poa</em>' attribute.
	 * @see #setPoa(POA)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_Poa()
	 * @model dataType="gov.redhawk.model.sca.POA" transient="true"
	 * @generated
	 */
	POA getPoa();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.LocalSca#getPoa <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Poa</em>' attribute.
	 * @see #getPoa()
	 * @generated
	 */
	void setPoa(POA value);

	/**
	 * Returns the value of the '<em><b>Waveforms</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.model.sca.ScaWaveform}.
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
	EList<ScaWaveform> getWaveforms();

	/**
	 * Returns the value of the '<em><b>Sandbox Waveform</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sandbox Waveform</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sandbox Waveform</em>' containment reference.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getLocalSca_SandboxWaveform()
	 * @model containment="true" required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
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
	 * @since 3.0
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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model exceptions="gov.redhawk.ide.debug.CoreException"
	 * @generated
	 */
	void init() throws CoreException;

} // LocalSca
