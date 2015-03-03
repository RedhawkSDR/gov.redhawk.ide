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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.sdr.SdrFactory
 * @model kind="package"
 * @generated
 */
public interface SdrPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "sdr";
	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.redhawk.gov/model/ide/sdr";
	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "sdr";
	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SdrPackage eINSTANCE = gov.redhawk.ide.sdr.impl.SdrPackageImpl.init();
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.SdrRootImpl <em>Root</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.SdrRootImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getSdrRoot()
	 * @generated
	 */
	int SDR_ROOT = 0;
	/**
	 * The feature id for the '<em><b>Load Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__LOAD_STATUS = 0;
	/**
	 * The feature id for the '<em><b>State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__STATE = 1;
	/**
	 * The feature id for the '<em><b>Components Container</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__COMPONENTS_CONTAINER = 2;
	/**
	 * The feature id for the '<em><b>Waveforms Container</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__WAVEFORMS_CONTAINER = 3;
	/**
	 * The feature id for the '<em><b>Devices Container</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__DEVICES_CONTAINER = 4;
	/**
	 * The feature id for the '<em><b>Services Container</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__SERVICES_CONTAINER = 5;
	/**
	 * The feature id for the '<em><b>Nodes Container</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__NODES_CONTAINER = 6;
	/**
	 * The feature id for the '<em><b>Domain Configuration</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__DOMAIN_CONFIGURATION = 7;
	/**
	 * The feature id for the '<em><b>Idl Library</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__IDL_LIBRARY = 8;
	/**
	 * The feature id for the '<em><b>Dev File System Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__DEV_FILE_SYSTEM_ROOT = 9;
	/**
	 * The feature id for the '<em><b>Dom File System Root</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT__DOM_FILE_SYSTEM_ROOT = 10;
	/**
	 * The number of structural features of the '<em>Root</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SDR_ROOT_FEATURE_COUNT = 11;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.PropertyRegistryImpl <em>Property Registry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.PropertyRegistryImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getPropertyRegistry()
	 * @generated
	 */
	int PROPERTY_REGISTRY = 8;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_REGISTRY__PROPERTIES = 0;
	/**
	 * The number of structural features of the '<em>Property Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int PROPERTY_REGISTRY_FEATURE_COUNT = 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl <em>Soft Pkg Registry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getSoftPkgRegistry()
	 * @generated
	 */
	int SOFT_PKG_REGISTRY = 9;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOFT_PKG_REGISTRY__PROPERTIES = PROPERTY_REGISTRY__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOFT_PKG_REGISTRY__COMPONENTS = PROPERTY_REGISTRY_FEATURE_COUNT + 0;
	/**
	 * The number of structural features of the '<em>Soft Pkg Registry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SOFT_PKG_REGISTRY_FEATURE_COUNT = PROPERTY_REGISTRY_FEATURE_COUNT + 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.ComponentsContainerImpl <em>Components Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.ComponentsContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getComponentsContainer()
	 * @generated
	 */
	int COMPONENTS_CONTAINER = 1;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_CONTAINER__PROPERTIES = SOFT_PKG_REGISTRY__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_CONTAINER__COMPONENTS = SOFT_PKG_REGISTRY__COMPONENTS;
	/**
	 * The feature id for the '<em><b>Sdr Root</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_CONTAINER__SDR_ROOT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 0;
	/**
	 * The number of structural features of the '<em>Components Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_CONTAINER_FEATURE_COUNT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl <em>Components Sub Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getComponentsSubContainer()
	 * @generated
	 */
	int COMPONENTS_SUB_CONTAINER = 2;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_SUB_CONTAINER__PROPERTIES = SOFT_PKG_REGISTRY__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_SUB_CONTAINER__COMPONENTS = SOFT_PKG_REGISTRY__COMPONENTS;
	/**
	 * The feature id for the '<em><b>Sub Containers</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS = SOFT_PKG_REGISTRY_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Container Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_SUB_CONTAINER__CONTAINER_NAME = SOFT_PKG_REGISTRY_FEATURE_COUNT + 1;
	/**
	 * The number of structural features of the '<em>Components Sub Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int COMPONENTS_SUB_CONTAINER_FEATURE_COUNT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl <em>Waveforms Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.WaveformsContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getWaveformsContainer()
	 * @generated
	 */
	int WAVEFORMS_CONTAINER = 3;
	/**
	 * The feature id for the '<em><b>Sdr Root</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAVEFORMS_CONTAINER__SDR_ROOT = 0;
	/**
	 * The feature id for the '<em><b>Waveforms</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAVEFORMS_CONTAINER__WAVEFORMS = 1;
	/**
	 * The number of structural features of the '<em>Waveforms Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int WAVEFORMS_CONTAINER_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.DevicesContainerImpl <em>Devices Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.DevicesContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getDevicesContainer()
	 * @generated
	 */
	int DEVICES_CONTAINER = 4;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICES_CONTAINER__PROPERTIES = SOFT_PKG_REGISTRY__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICES_CONTAINER__COMPONENTS = SOFT_PKG_REGISTRY__COMPONENTS;
	/**
	 * The feature id for the '<em><b>Sdr Root</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICES_CONTAINER__SDR_ROOT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 0;
	/**
	 * The number of structural features of the '<em>Devices Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int DEVICES_CONTAINER_FEATURE_COUNT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.ServicesContainerImpl <em>Services Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.ServicesContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getServicesContainer()
	 * @generated
	 */
	int SERVICES_CONTAINER = 5;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES_CONTAINER__PROPERTIES = SOFT_PKG_REGISTRY__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Components</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES_CONTAINER__COMPONENTS = SOFT_PKG_REGISTRY__COMPONENTS;
	/**
	 * The feature id for the '<em><b>Sdr Root</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES_CONTAINER__SDR_ROOT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 0;
	/**
	 * The number of structural features of the '<em>Services Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SERVICES_CONTAINER_FEATURE_COUNT = SOFT_PKG_REGISTRY_FEATURE_COUNT + 1;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.NodesContainerImpl <em>Nodes Container</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.NodesContainerImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getNodesContainer()
	 * @generated
	 */
	int NODES_CONTAINER = 6;
	/**
	 * The feature id for the '<em><b>Sdr Root</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODES_CONTAINER__SDR_ROOT = 0;
	/**
	 * The feature id for the '<em><b>Nodes</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODES_CONTAINER__NODES = 1;
	/**
	 * The number of structural features of the '<em>Nodes Container</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NODES_CONTAINER_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.impl.StringToAbstractPropertyImpl <em>String To Abstract Property</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.impl.StringToAbstractPropertyImpl
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getStringToAbstractProperty()
	 * @generated
	 */
	int STRING_TO_ABSTRACT_PROPERTY = 7;
	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ABSTRACT_PROPERTY__KEY = 0;
	/**
	 * The feature id for the '<em><b>Value</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ABSTRACT_PROPERTY__VALUE = 1;
	/**
	 * The number of structural features of the '<em>String To Abstract Property</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int STRING_TO_ABSTRACT_PROPERTY_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.sdr.LoadState <em>Load State</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.LoadState
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getLoadState()
	 * @generated
	 */
	int LOAD_STATE = 10;
	/**
	 * The meta object id for the '<em>IStatus</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.IStatus
	 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getIStatus()
	 * @generated
	 */
	int ISTATUS = 11;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.SdrRoot <em>Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Root</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot
	 * @generated
	 */
	EClass getSdrRoot();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sdr.SdrRoot#getLoadStatus <em>Load Status</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Load Status</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getLoadStatus()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EAttribute getSdrRoot_LoadStatus();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sdr.SdrRoot#getState <em>State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>State</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getState()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EAttribute getSdrRoot_State();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer <em>Components Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Components Container</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_ComponentsContainer();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer <em>Waveforms Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Waveforms Container</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_WaveformsContainer();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer <em>Devices Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Devices Container</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_DevicesContainer();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getServicesContainer <em>Services Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Services Container</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getServicesContainer()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_ServicesContainer();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getNodesContainer <em>Nodes Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Nodes Container</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getNodesContainer()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_NodesContainer();

	/**
	 * Returns the meta object for the reference '{@link gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration <em>Domain Configuration</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Domain Configuration</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_DomainConfiguration();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary <em>Idl Library</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Idl Library</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EReference getSdrRoot_IdlLibrary();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot <em>Dev File System Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dev File System Root</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EAttribute getSdrRoot_DevFileSystemRoot();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot <em>Dom File System Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Dom File System Root</em>'.
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot()
	 * @see #getSdrRoot()
	 * @generated
	 */
	EAttribute getSdrRoot_DomFileSystemRoot();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.ComponentsContainer <em>Components Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Components Container</em>'.
	 * @see gov.redhawk.ide.sdr.ComponentsContainer
	 * @generated
	 */
	EClass getComponentsContainer();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.sdr.ComponentsContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Sdr Root</em>'.
	 * @see gov.redhawk.ide.sdr.ComponentsContainer#getSdrRoot()
	 * @see #getComponentsContainer()
	 * @generated
	 */
	EReference getComponentsContainer_SdrRoot();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.ComponentsSubContainer <em>Components Sub Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Components Sub Container</em>'.
	 * @see gov.redhawk.ide.sdr.ComponentsSubContainer
	 * @generated
	 */
	EClass getComponentsSubContainer();

	/**
	 * Returns the meta object for the reference list '{@link gov.redhawk.ide.sdr.ComponentsSubContainer#getSubContainers <em>Sub Containers</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Sub Containers</em>'.
	 * @see gov.redhawk.ide.sdr.ComponentsSubContainer#getSubContainers()
	 * @see #getComponentsSubContainer()
	 * @generated
	 */
	EReference getComponentsSubContainer_SubContainers();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.sdr.ComponentsSubContainer#getContainerName <em>Container Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Container Name</em>'.
	 * @see gov.redhawk.ide.sdr.ComponentsSubContainer#getContainerName()
	 * @see #getComponentsSubContainer()
	 * @generated
	 */
	EAttribute getComponentsSubContainer_ContainerName();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.WaveformsContainer <em>Waveforms Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Waveforms Container</em>'.
	 * @see gov.redhawk.ide.sdr.WaveformsContainer
	 * @generated
	 */
	EClass getWaveformsContainer();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.sdr.WaveformsContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Sdr Root</em>'.
	 * @see gov.redhawk.ide.sdr.WaveformsContainer#getSdrRoot()
	 * @see #getWaveformsContainer()
	 * @generated
	 */
	EReference getWaveformsContainer_SdrRoot();

	/**
	 * Returns the meta object for the reference list '{@link gov.redhawk.ide.sdr.WaveformsContainer#getWaveforms <em>Waveforms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Waveforms</em>'.
	 * @see gov.redhawk.ide.sdr.WaveformsContainer#getWaveforms()
	 * @see #getWaveformsContainer()
	 * @generated
	 */
	EReference getWaveformsContainer_Waveforms();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.DevicesContainer <em>Devices Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Devices Container</em>'.
	 * @see gov.redhawk.ide.sdr.DevicesContainer
	 * @generated
	 */
	EClass getDevicesContainer();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.sdr.DevicesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Sdr Root</em>'.
	 * @see gov.redhawk.ide.sdr.DevicesContainer#getSdrRoot()
	 * @see #getDevicesContainer()
	 * @generated
	 */
	EReference getDevicesContainer_SdrRoot();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.ServicesContainer <em>Services Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Services Container</em>'.
	 * @see gov.redhawk.ide.sdr.ServicesContainer
	 * @generated
	 */
	EClass getServicesContainer();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.sdr.ServicesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Sdr Root</em>'.
	 * @see gov.redhawk.ide.sdr.ServicesContainer#getSdrRoot()
	 * @see #getServicesContainer()
	 * @generated
	 */
	EReference getServicesContainer_SdrRoot();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.NodesContainer <em>Nodes Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Nodes Container</em>'.
	 * @see gov.redhawk.ide.sdr.NodesContainer
	 * @generated
	 */
	EClass getNodesContainer();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.sdr.NodesContainer#getSdrRoot <em>Sdr Root</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Sdr Root</em>'.
	 * @see gov.redhawk.ide.sdr.NodesContainer#getSdrRoot()
	 * @see #getNodesContainer()
	 * @generated
	 */
	EReference getNodesContainer_SdrRoot();

	/**
	 * Returns the meta object for the reference list '{@link gov.redhawk.ide.sdr.NodesContainer#getNodes <em>Nodes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Nodes</em>'.
	 * @see gov.redhawk.ide.sdr.NodesContainer#getNodes()
	 * @see #getNodesContainer()
	 * @generated
	 */
	EReference getNodesContainer_Nodes();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>String To Abstract Property</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>String To Abstract Property</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="mil.jpeojtrs.sca.prf.DceUUID"
	 *        valueType="mil.jpeojtrs.sca.prf.AbstractProperty"
	 * @generated
	 */
	EClass getStringToAbstractProperty();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAbstractProperty()
	 * @generated
	 */
	EAttribute getStringToAbstractProperty_Key();

	/**
	 * Returns the meta object for the reference '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getStringToAbstractProperty()
	 * @generated
	 */
	EReference getStringToAbstractProperty_Value();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.PropertyRegistry <em>Property Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Property Registry</em>'.
	 * @see gov.redhawk.ide.sdr.PropertyRegistry
	 * @generated
	 */
	EClass getPropertyRegistry();

	/**
	 * Returns the meta object for the map '{@link gov.redhawk.ide.sdr.PropertyRegistry#getProperties <em>Properties</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Properties</em>'.
	 * @see gov.redhawk.ide.sdr.PropertyRegistry#getProperties()
	 * @see #getPropertyRegistry()
	 * @generated
	 */
	EReference getPropertyRegistry_Properties();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.sdr.SoftPkgRegistry <em>Soft Pkg Registry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Soft Pkg Registry</em>'.
	 * @see gov.redhawk.ide.sdr.SoftPkgRegistry
	 * @generated
	 */
	EClass getSoftPkgRegistry();

	/**
	 * Returns the meta object for the reference list '{@link gov.redhawk.ide.sdr.SoftPkgRegistry#getComponents <em>Components</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference list '<em>Components</em>'.
	 * @see gov.redhawk.ide.sdr.SoftPkgRegistry#getComponents()
	 * @see #getSoftPkgRegistry()
	 * @generated
	 */
	EReference getSoftPkgRegistry_Components();

	/**
	 * Returns the meta object for enum '{@link gov.redhawk.ide.sdr.LoadState <em>Load State</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Load State</em>'.
	 * @see gov.redhawk.ide.sdr.LoadState
	 * @generated
	 */
	EEnum getLoadState();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.IStatus <em>IStatus</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>IStatus</em>'.
	 * @see org.eclipse.core.runtime.IStatus
	 * @model instanceClass="org.eclipse.core.runtime.IStatus" serializeable="false"
	 * @generated
	 */
	EDataType getIStatus();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SdrFactory getSdrFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {

		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.SdrRootImpl <em>Root</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.SdrRootImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getSdrRoot()
		 * @generated
		 */
		EClass SDR_ROOT = eINSTANCE.getSdrRoot();
		/**
		 * The meta object literal for the '<em><b>Load Status</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SDR_ROOT__LOAD_STATUS = eINSTANCE.getSdrRoot_LoadStatus();
		/**
		 * The meta object literal for the '<em><b>State</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SDR_ROOT__STATE = eINSTANCE.getSdrRoot_State();
		/**
		 * The meta object literal for the '<em><b>Components Container</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__COMPONENTS_CONTAINER = eINSTANCE.getSdrRoot_ComponentsContainer();
		/**
		 * The meta object literal for the '<em><b>Waveforms Container</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__WAVEFORMS_CONTAINER = eINSTANCE.getSdrRoot_WaveformsContainer();
		/**
		 * The meta object literal for the '<em><b>Devices Container</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__DEVICES_CONTAINER = eINSTANCE.getSdrRoot_DevicesContainer();
		/**
		 * The meta object literal for the '<em><b>Services Container</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__SERVICES_CONTAINER = eINSTANCE.getSdrRoot_ServicesContainer();
		/**
		 * The meta object literal for the '<em><b>Nodes Container</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__NODES_CONTAINER = eINSTANCE.getSdrRoot_NodesContainer();
		/**
		 * The meta object literal for the '<em><b>Domain Configuration</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__DOMAIN_CONFIGURATION = eINSTANCE.getSdrRoot_DomainConfiguration();
		/**
		 * The meta object literal for the '<em><b>Idl Library</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SDR_ROOT__IDL_LIBRARY = eINSTANCE.getSdrRoot_IdlLibrary();
		/**
		 * The meta object literal for the '<em><b>Dev File System Root</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SDR_ROOT__DEV_FILE_SYSTEM_ROOT = eINSTANCE.getSdrRoot_DevFileSystemRoot();
		/**
		 * The meta object literal for the '<em><b>Dom File System Root</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SDR_ROOT__DOM_FILE_SYSTEM_ROOT = eINSTANCE.getSdrRoot_DomFileSystemRoot();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.ComponentsContainerImpl <em>Components Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.ComponentsContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getComponentsContainer()
		 * @generated
		 */
		EClass COMPONENTS_CONTAINER = eINSTANCE.getComponentsContainer();
		/**
		 * The meta object literal for the '<em><b>Sdr Root</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS_CONTAINER__SDR_ROOT = eINSTANCE.getComponentsContainer_SdrRoot();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl <em>Components Sub Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getComponentsSubContainer()
		 * @generated
		 */
		EClass COMPONENTS_SUB_CONTAINER = eINSTANCE.getComponentsSubContainer();
		/**
		 * The meta object literal for the '<em><b>Sub Containers</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS = eINSTANCE.getComponentsSubContainer_SubContainers();
		/**
		 * The meta object literal for the '<em><b>Container Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute COMPONENTS_SUB_CONTAINER__CONTAINER_NAME = eINSTANCE.getComponentsSubContainer_ContainerName();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl <em>Waveforms Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.WaveformsContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getWaveformsContainer()
		 * @generated
		 */
		EClass WAVEFORMS_CONTAINER = eINSTANCE.getWaveformsContainer();
		/**
		 * The meta object literal for the '<em><b>Sdr Root</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAVEFORMS_CONTAINER__SDR_ROOT = eINSTANCE.getWaveformsContainer_SdrRoot();
		/**
		 * The meta object literal for the '<em><b>Waveforms</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference WAVEFORMS_CONTAINER__WAVEFORMS = eINSTANCE.getWaveformsContainer_Waveforms();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.DevicesContainerImpl <em>Devices Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.DevicesContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getDevicesContainer()
		 * @generated
		 */
		EClass DEVICES_CONTAINER = eINSTANCE.getDevicesContainer();
		/**
		 * The meta object literal for the '<em><b>Sdr Root</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference DEVICES_CONTAINER__SDR_ROOT = eINSTANCE.getDevicesContainer_SdrRoot();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.ServicesContainerImpl <em>Services Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.ServicesContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getServicesContainer()
		 * @generated
		 */
		EClass SERVICES_CONTAINER = eINSTANCE.getServicesContainer();
		/**
		 * The meta object literal for the '<em><b>Sdr Root</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SERVICES_CONTAINER__SDR_ROOT = eINSTANCE.getServicesContainer_SdrRoot();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.NodesContainerImpl <em>Nodes Container</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.NodesContainerImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getNodesContainer()
		 * @generated
		 */
		EClass NODES_CONTAINER = eINSTANCE.getNodesContainer();
		/**
		 * The meta object literal for the '<em><b>Sdr Root</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODES_CONTAINER__SDR_ROOT = eINSTANCE.getNodesContainer_SdrRoot();
		/**
		 * The meta object literal for the '<em><b>Nodes</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NODES_CONTAINER__NODES = eINSTANCE.getNodesContainer_Nodes();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.StringToAbstractPropertyImpl <em>String To Abstract Property</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.StringToAbstractPropertyImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getStringToAbstractProperty()
		 * @generated
		 */
		EClass STRING_TO_ABSTRACT_PROPERTY = eINSTANCE.getStringToAbstractProperty();
		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute STRING_TO_ABSTRACT_PROPERTY__KEY = eINSTANCE.getStringToAbstractProperty_Key();
		/**
		 * The meta object literal for the '<em><b>Value</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference STRING_TO_ABSTRACT_PROPERTY__VALUE = eINSTANCE.getStringToAbstractProperty_Value();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.PropertyRegistryImpl <em>Property Registry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.PropertyRegistryImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getPropertyRegistry()
		 * @generated
		 */
		EClass PROPERTY_REGISTRY = eINSTANCE.getPropertyRegistry();
		/**
		 * The meta object literal for the '<em><b>Properties</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference PROPERTY_REGISTRY__PROPERTIES = eINSTANCE.getPropertyRegistry_Properties();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl <em>Soft Pkg Registry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getSoftPkgRegistry()
		 * @generated
		 */
		EClass SOFT_PKG_REGISTRY = eINSTANCE.getSoftPkgRegistry();
		/**
		 * The meta object literal for the '<em><b>Components</b></em>' reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SOFT_PKG_REGISTRY__COMPONENTS = eINSTANCE.getSoftPkgRegistry_Components();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.sdr.LoadState <em>Load State</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.sdr.LoadState
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getLoadState()
		 * @generated
		 */
		EEnum LOAD_STATE = eINSTANCE.getLoadState();
		/**
		 * The meta object literal for the '<em>IStatus</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.core.runtime.IStatus
		 * @see gov.redhawk.ide.sdr.impl.SdrPackageImpl#getIStatus()
		 * @generated
		 */
		EDataType ISTATUS = eINSTANCE.getIStatus();

	}

} //SdrPackage
