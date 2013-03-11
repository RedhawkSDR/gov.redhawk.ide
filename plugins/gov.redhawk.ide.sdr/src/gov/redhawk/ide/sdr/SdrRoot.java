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
package gov.redhawk.ide.sdr;

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import mil.jpeojtrs.sca.dmd.DomainManagerConfiguration;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getLoadStatus <em>Load Status</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getState <em>State</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer <em>Components Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer <em>Waveforms Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer <em>Devices Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getServicesContainer <em>Services Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getNodesContainer <em>Nodes Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration <em>Domain Configuration</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary <em>Idl Library</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot <em>Dev File System Root</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot <em>Dom File System Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot()
 * @model
 * @generated
 */
public interface SdrRoot extends EObject {

	/**
	 * Returns the value of the '<em><b>Load Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Load Status</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Load Status</em>' attribute.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_LoadStatus()
	 * @model dataType="gov.redhawk.eclipsecorba.library.IStatus" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	IStatus getLoadStatus();

	/**
	 * Returns the value of the '<em><b>State</b></em>' attribute.
	 * The default value is <code>"UNLOADED"</code>.
	 * The literals are from the enumeration {@link gov.redhawk.ide.sdr.LoadState}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>State</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>State</em>' attribute.
	 * @see gov.redhawk.ide.sdr.LoadState
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_State()
	 * @model default="UNLOADED" transient="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	LoadState getState();

	/**
	 * Returns the value of the '<em><b>Components Container</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.ComponentsContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Components Container</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Components Container</em>' containment reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_ComponentsContainer()
	 * @see gov.redhawk.ide.sdr.ComponentsContainer#getSdrRoot
	 * @model opposite="sdrRoot" containment="true" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	ComponentsContainer getComponentsContainer();

	/**
	 * Returns the value of the '<em><b>Waveforms Container</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.WaveformsContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Waveforms Container</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Waveforms Container</em>' containment reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_WaveformsContainer()
	 * @see gov.redhawk.ide.sdr.WaveformsContainer#getSdrRoot
	 * @model opposite="sdrRoot" containment="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	WaveformsContainer getWaveformsContainer();

	/**
	 * Returns the value of the '<em><b>Devices Container</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.DevicesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Devices Container</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Devices Container</em>' containment reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_DevicesContainer()
	 * @see gov.redhawk.ide.sdr.DevicesContainer#getSdrRoot
	 * @model opposite="sdrRoot" containment="true" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	DevicesContainer getDevicesContainer();

	/**
	 * Returns the value of the '<em><b>Services Container</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.ServicesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Services Container</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Services Container</em>' containment reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_ServicesContainer()
	 * @see gov.redhawk.ide.sdr.ServicesContainer#getSdrRoot
	 * @model opposite="sdrRoot" containment="true" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	ServicesContainer getServicesContainer();

	/**
	 * Returns the value of the '<em><b>Nodes Container</b></em>' containment reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.NodesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Nodes Container</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Nodes Container</em>' containment reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_NodesContainer()
	 * @see gov.redhawk.ide.sdr.NodesContainer#getSdrRoot
	 * @model opposite="sdrRoot" containment="true" transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	NodesContainer getNodesContainer();

	/**
	 * Returns the value of the '<em><b>Domain Configuration</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain Configuration</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain Configuration</em>' reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_DomainConfiguration()
	 * @model transient="true" derived="true" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	DomainManagerConfiguration getDomainConfiguration();

	/**
	 * Returns the value of the '<em><b>Idl Library</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * @since 3.0
	 * <!-- end-model-doc -->
	 * @return the value of the '<em>Idl Library</em>' containment reference.
	 * @see #setIdlLibrary(IdlLibrary)
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_IdlLibrary()
	 * @model containment="true"
	 * @generated
	 */
	IdlLibrary getIdlLibrary();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary <em>Idl Library</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Idl Library</em>' containment reference.
	 * @see #getIdlLibrary()
	 * @generated
	 */
	void setIdlLibrary(IdlLibrary value);

	/**
	 * Returns the value of the '<em><b>Dev File System Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dev File System Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dev File System Root</em>' attribute.
	 * @see #setDevFileSystemRoot(URI)
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_DevFileSystemRoot()
	 * @model dataType="gov.redhawk.eclipsecorba.library.URI"
	 * @generated
	 */
	URI getDevFileSystemRoot();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot <em>Dev File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dev File System Root</em>' attribute.
	 * @see #getDevFileSystemRoot()
	 * @generated
	 */
	void setDevFileSystemRoot(URI value);

	/**
	 * Returns the value of the '<em><b>Dom File System Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Dom File System Root</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Dom File System Root</em>' attribute.
	 * @see #setDomFileSystemRoot(URI)
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSdrRoot_DomFileSystemRoot()
	 * @model dataType="gov.redhawk.eclipsecorba.library.URI"
	 * @generated
	 */
	URI getDomFileSystemRoot();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot <em>Dom File System Root</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Dom File System Root</em>' attribute.
	 * @see #getDomFileSystemRoot()
	 * @generated
	 */
	void setDomFileSystemRoot(URI value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model monitorDataType="gov.redhawk.eclipsecorba.library.IProgressMonitor"
	 * @generated
	 */
	void load(IProgressMonitor monitor);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model monitorDataType="gov.redhawk.eclipsecorba.library.IProgressMonitor"
	 * @generated
	 */
	void unload(IProgressMonitor monitor);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model monitorDataType="gov.redhawk.eclipsecorba.library.IProgressMonitor"
	 * @generated
	 */
	void reload(IProgressMonitor monitor);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model sdrRootDataType="gov.redhawk.eclipsecorba.library.URI"
	 * @generated
	 */
	void setSdrRoot(URI sdrRoot, String domPath, String devPath);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Resource getDevResource(String path);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model
	 * @generated
	 */
	Resource getDomResource(String path);

} // SdrRoot
