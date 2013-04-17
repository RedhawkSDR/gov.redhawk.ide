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

import gov.redhawk.model.sca.ScaPackage;
import mil.jpeojtrs.sca.cf.CfPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
 * @see gov.redhawk.ide.debug.ScaDebugFactory
 * @model kind="package"
 * @generated
 */
public interface ScaDebugPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "debug";
	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://redhawk.gov/sca/debug";
	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "sca.debug";
	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ScaDebugPackage eINSTANCE = gov.redhawk.ide.debug.impl.ScaDebugPackageImpl.init();
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaImpl <em>Local Sca</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalSca()
	 * @generated
	 */
	int LOCAL_SCA = 0;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__DISPOSED = ScaPackage.IDISPOSABLE__DISPOSED;
	/**
	 * The feature id for the '<em><b>Orb</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__ORB = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Poa</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__POA = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Waveforms</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__WAVEFORMS = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Sandbox Waveform</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__SANDBOX_WAVEFORM = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 3;
	/**
	 * The feature id for the '<em><b>Sandbox Device Manager</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__SANDBOX_DEVICE_MANAGER = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 4;
	/**
	 * The feature id for the '<em><b>Root Context</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__ROOT_CONTEXT = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 5;
	/**
	 * The feature id for the '<em><b>File Manager</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA__FILE_MANAGER = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 6;
	/**
	 * The number of structural features of the '<em>Local Sca</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_FEATURE_COUNT = ScaPackage.IDISPOSABLE_FEATURE_COUNT + 7;
	/**
	 * The meta object id for the '{@link org.omg.CosNaming.NamingContextExtOperations <em>Naming Context Ext Operations</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.omg.CosNaming.NamingContextExtOperations
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNamingContextExtOperations()
	 * @generated
	 */
	int NAMING_CONTEXT_EXT_OPERATIONS = 1;
	/**
	 * The number of structural features of the '<em>Naming Context Ext Operations</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT = 0;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl <em>Notifying Naming Context</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNotifyingNamingContext()
	 * @generated
	 */
	int NOTIFYING_NAMING_CONTEXT = 2;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__DISPOSED = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Object Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__OBJECT_MAP = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Context Map</b></em>' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Naming Context</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 3;
	/**
	 * The feature id for the '<em><b>Sub Contexts</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 4;
	/**
	 * The feature id for the '<em><b>Parent Context</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 5;
	/**
	 * The feature id for the '<em><b>Poa</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__POA = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 6;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT__NAME = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 7;
	/**
	 * The number of structural features of the '<em>Notifying Naming Context</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOTIFYING_NAMING_CONTEXT_FEATURE_COUNT = NAMING_CONTEXT_EXT_OPERATIONS_FEATURE_COUNT + 8;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.NameToObjectEntryImpl <em>Name To Object Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.NameToObjectEntryImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameToObjectEntry()
	 * @generated
	 */
	int NAME_TO_OBJECT_ENTRY = 3;
	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_OBJECT_ENTRY__KEY = 0;
	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_OBJECT_ENTRY__VALUE = 1;
	/**
	 * The number of structural features of the '<em>Name To Object Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_OBJECT_ENTRY_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.NameToNamingContextEntryImpl <em>Name To Naming Context Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.NameToNamingContextEntryImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameToNamingContextEntry()
	 * @generated
	 */
	int NAME_TO_NAMING_CONTEXT_ENTRY = 4;
	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_NAMING_CONTEXT_ENTRY__KEY = 0;
	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_NAMING_CONTEXT_ENTRY__VALUE = 1;
	/**
	 * The number of structural features of the '<em>Name To Naming Context Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NAME_TO_NAMING_CONTEXT_ENTRY_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalFileManagerImpl <em>Local File Manager</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalFileManagerImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalFileManager()
	 * @generated
	 */
	int LOCAL_FILE_MANAGER = 5;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__STATUS = ScaPackage.SCA_FILE_MANAGER__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__DISPOSED = ScaPackage.SCA_FILE_MANAGER__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__DATA_PROVIDERS = ScaPackage.SCA_FILE_MANAGER__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_FILE_MANAGER__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__IOR = ScaPackage.SCA_FILE_MANAGER__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__OBJ = ScaPackage.SCA_FILE_MANAGER__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__CORBA_OBJ = ScaPackage.SCA_FILE_MANAGER__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__FILE_STORE = ScaPackage.SCA_FILE_MANAGER__FILE_STORE;
	/**
	 * The feature id for the '<em><b>Children</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__CHILDREN = ScaPackage.SCA_FILE_MANAGER__CHILDREN;
	/**
	 * The feature id for the '<em><b>Image Desc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__IMAGE_DESC = ScaPackage.SCA_FILE_MANAGER__IMAGE_DESC;
	/**
	 * The feature id for the '<em><b>Directory</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__DIRECTORY = ScaPackage.SCA_FILE_MANAGER__DIRECTORY;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__NAME = ScaPackage.SCA_FILE_MANAGER__NAME;
	/**
	 * The feature id for the '<em><b>File System URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER__FILE_SYSTEM_URI = ScaPackage.SCA_FILE_MANAGER__FILE_SYSTEM_URI;
	/**
	 * The number of structural features of the '<em>Local File Manager</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_FILE_MANAGER_FEATURE_COUNT = ScaPackage.SCA_FILE_MANAGER_FEATURE_COUNT + 0;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.LocalLaunch <em>Local Launch</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalLaunch
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalLaunch()
	 * @generated
	 */
	int LOCAL_LAUNCH = 6;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_LAUNCH__LAUNCH = 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_LAUNCH__MODE = 1;
	/**
	 * The number of structural features of the '<em>Local Launch</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_LAUNCH_FEATURE_COUNT = 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.LocalAbstractComponent <em>Local Abstract Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalAbstractComponent
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalAbstractComponent()
	 * @generated
	 */
	int LOCAL_ABSTRACT_COMPONENT = 7;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_ABSTRACT_COMPONENT__LAUNCH = LOCAL_LAUNCH__LAUNCH;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_ABSTRACT_COMPONENT__MODE = LOCAL_LAUNCH__MODE;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_ABSTRACT_COMPONENT__IMPLEMENTATION_ID = LOCAL_LAUNCH_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_ABSTRACT_COMPONENT__EXEC_PARAM = LOCAL_LAUNCH_FEATURE_COUNT + 1;
	/**
	 * The number of structural features of the '<em>Local Abstract Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_ABSTRACT_COMPONENT_FEATURE_COUNT = LOCAL_LAUNCH_FEATURE_COUNT + 2;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl <em>Local Sca Waveform</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaWaveformImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaWaveform()
	 * @generated
	 */
	int LOCAL_SCA_WAVEFORM = 8;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__STATUS = ScaPackage.SCA_WAVEFORM__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__DISPOSED = ScaPackage.SCA_WAVEFORM__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__DATA_PROVIDERS = ScaPackage.SCA_WAVEFORM__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_WAVEFORM__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__IOR = ScaPackage.SCA_WAVEFORM__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__OBJ = ScaPackage.SCA_WAVEFORM__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__CORBA_OBJ = ScaPackage.SCA_WAVEFORM__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__PROFILE_URI = ScaPackage.SCA_WAVEFORM__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__PROFILE_OBJ = ScaPackage.SCA_WAVEFORM__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__ROOT_FILE_STORE = ScaPackage.SCA_WAVEFORM__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__PROPERTIES = ScaPackage.SCA_WAVEFORM__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__PORTS = ScaPackage.SCA_WAVEFORM__PORTS;
	/**
	 * The feature id for the '<em><b>Components</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__COMPONENTS = ScaPackage.SCA_WAVEFORM__COMPONENTS;
	/**
	 * The feature id for the '<em><b>Assembly Controller</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__ASSEMBLY_CONTROLLER = ScaPackage.SCA_WAVEFORM__ASSEMBLY_CONTROLLER;
	/**
	 * The feature id for the '<em><b>Dom Mgr</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__DOM_MGR = ScaPackage.SCA_WAVEFORM__DOM_MGR;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__IDENTIFIER = ScaPackage.SCA_WAVEFORM__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__NAME = ScaPackage.SCA_WAVEFORM__NAME;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__STARTED = ScaPackage.SCA_WAVEFORM__STARTED;
	/**
	 * The feature id for the '<em><b>Profile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__PROFILE = ScaPackage.SCA_WAVEFORM__PROFILE;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__LAUNCH = ScaPackage.SCA_WAVEFORM_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__MODE = ScaPackage.SCA_WAVEFORM_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Naming Context</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__NAMING_CONTEXT = ScaPackage.SCA_WAVEFORM_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Local App</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM__LOCAL_APP = ScaPackage.SCA_WAVEFORM_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Waveform</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_WAVEFORM_FEATURE_COUNT = ScaPackage.SCA_WAVEFORM_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaComponentImpl <em>Local Sca Component</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaComponentImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaComponent()
	 * @generated
	 */
	int LOCAL_SCA_COMPONENT = 9;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__STATUS = ScaPackage.SCA_COMPONENT__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__DISPOSED = ScaPackage.SCA_COMPONENT__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__DATA_PROVIDERS = ScaPackage.SCA_COMPONENT__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_COMPONENT__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__IOR = ScaPackage.SCA_COMPONENT__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__OBJ = ScaPackage.SCA_COMPONENT__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__CORBA_OBJ = ScaPackage.SCA_COMPONENT__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__PROFILE_URI = ScaPackage.SCA_COMPONENT__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__PROFILE_OBJ = ScaPackage.SCA_COMPONENT__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__ROOT_FILE_STORE = ScaPackage.SCA_COMPONENT__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__PROPERTIES = ScaPackage.SCA_COMPONENT__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__PORTS = ScaPackage.SCA_COMPONENT__PORTS;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__IDENTIFIER = ScaPackage.SCA_COMPONENT__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__STARTED = ScaPackage.SCA_COMPONENT__STARTED;
	/**
	 * The feature id for the '<em><b>Component Instantiation</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__COMPONENT_INSTANTIATION = ScaPackage.SCA_COMPONENT__COMPONENT_INSTANTIATION;
	/**
	 * The feature id for the '<em><b>Devices</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__DEVICES = ScaPackage.SCA_COMPONENT__DEVICES;
	/**
	 * The feature id for the '<em><b>Instantiation Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__INSTANTIATION_IDENTIFIER = ScaPackage.SCA_COMPONENT__INSTANTIATION_IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Waveform</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__WAVEFORM = ScaPackage.SCA_COMPONENT__WAVEFORM;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__NAME = ScaPackage.SCA_COMPONENT__NAME;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__LAUNCH = ScaPackage.SCA_COMPONENT_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__MODE = ScaPackage.SCA_COMPONENT_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__IMPLEMENTATION_ID = ScaPackage.SCA_COMPONENT_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT__EXEC_PARAM = ScaPackage.SCA_COMPONENT_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Component</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_COMPONENT_FEATURE_COUNT = ScaPackage.SCA_COMPONENT_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl <em>Local Sca Device Manager</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaDeviceManager()
	 * @generated
	 */
	int LOCAL_SCA_DEVICE_MANAGER = 10;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__STATUS = ScaPackage.SCA_DEVICE_MANAGER__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__DISPOSED = ScaPackage.SCA_DEVICE_MANAGER__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__DATA_PROVIDERS = ScaPackage.SCA_DEVICE_MANAGER__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_DEVICE_MANAGER__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__IOR = ScaPackage.SCA_DEVICE_MANAGER__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__OBJ = ScaPackage.SCA_DEVICE_MANAGER__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__CORBA_OBJ = ScaPackage.SCA_DEVICE_MANAGER__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__PROFILE_URI = ScaPackage.SCA_DEVICE_MANAGER__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__PROFILE_OBJ = ScaPackage.SCA_DEVICE_MANAGER__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__ROOT_FILE_STORE = ScaPackage.SCA_DEVICE_MANAGER__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__PROPERTIES = ScaPackage.SCA_DEVICE_MANAGER__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__PORTS = ScaPackage.SCA_DEVICE_MANAGER__PORTS;
	/**
	 * The feature id for the '<em><b>Devices</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__DEVICES = ScaPackage.SCA_DEVICE_MANAGER__DEVICES;
	/**
	 * The feature id for the '<em><b>Root Devices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__ROOT_DEVICES = ScaPackage.SCA_DEVICE_MANAGER__ROOT_DEVICES;
	/**
	 * The feature id for the '<em><b>Child Devices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__CHILD_DEVICES = ScaPackage.SCA_DEVICE_MANAGER__CHILD_DEVICES;
	/**
	 * The feature id for the '<em><b>All Devices</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__ALL_DEVICES = ScaPackage.SCA_DEVICE_MANAGER__ALL_DEVICES;
	/**
	 * The feature id for the '<em><b>File System</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__FILE_SYSTEM = ScaPackage.SCA_DEVICE_MANAGER__FILE_SYSTEM;
	/**
	 * The feature id for the '<em><b>Dom Mgr</b></em>' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__DOM_MGR = ScaPackage.SCA_DEVICE_MANAGER__DOM_MGR;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__IDENTIFIER = ScaPackage.SCA_DEVICE_MANAGER__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__LABEL = ScaPackage.SCA_DEVICE_MANAGER__LABEL;
	/**
	 * The feature id for the '<em><b>Services</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__SERVICES = ScaPackage.SCA_DEVICE_MANAGER__SERVICES;
	/**
	 * The feature id for the '<em><b>Profile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__PROFILE = ScaPackage.SCA_DEVICE_MANAGER__PROFILE;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__LAUNCH = ScaPackage.SCA_DEVICE_MANAGER_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__MODE = ScaPackage.SCA_DEVICE_MANAGER_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Naming Context</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT = ScaPackage.SCA_DEVICE_MANAGER_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Local Device Manager</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER = ScaPackage.SCA_DEVICE_MANAGER_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Device Manager</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_MANAGER_FEATURE_COUNT = ScaPackage.SCA_DEVICE_MANAGER_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl <em>Local Sca Executable Device</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaExecutableDevice()
	 * @generated
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE = 11;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__STATUS = ScaPackage.SCA_EXECUTABLE_DEVICE__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__DISPOSED = ScaPackage.SCA_EXECUTABLE_DEVICE__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__DATA_PROVIDERS = ScaPackage.SCA_EXECUTABLE_DEVICE__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_EXECUTABLE_DEVICE__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__IOR = ScaPackage.SCA_EXECUTABLE_DEVICE__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__OBJ = ScaPackage.SCA_EXECUTABLE_DEVICE__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__CORBA_OBJ = ScaPackage.SCA_EXECUTABLE_DEVICE__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PROFILE_URI = ScaPackage.SCA_EXECUTABLE_DEVICE__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PROFILE_OBJ = ScaPackage.SCA_EXECUTABLE_DEVICE__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__ROOT_FILE_STORE = ScaPackage.SCA_EXECUTABLE_DEVICE__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PROPERTIES = ScaPackage.SCA_EXECUTABLE_DEVICE__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PORTS = ScaPackage.SCA_EXECUTABLE_DEVICE__PORTS;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__IDENTIFIER = ScaPackage.SCA_EXECUTABLE_DEVICE__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__STARTED = ScaPackage.SCA_EXECUTABLE_DEVICE__STARTED;
	/**
	 * The feature id for the '<em><b>Child Devices</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__CHILD_DEVICES = ScaPackage.SCA_EXECUTABLE_DEVICE__CHILD_DEVICES;
	/**
	 * The feature id for the '<em><b>Admin State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__ADMIN_STATE = ScaPackage.SCA_EXECUTABLE_DEVICE__ADMIN_STATE;
	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__LABEL = ScaPackage.SCA_EXECUTABLE_DEVICE__LABEL;
	/**
	 * The feature id for the '<em><b>Operational State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__OPERATIONAL_STATE = ScaPackage.SCA_EXECUTABLE_DEVICE__OPERATIONAL_STATE;
	/**
	 * The feature id for the '<em><b>Usage State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__USAGE_STATE = ScaPackage.SCA_EXECUTABLE_DEVICE__USAGE_STATE;
	/**
	 * The feature id for the '<em><b>Parent Device</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PARENT_DEVICE = ScaPackage.SCA_EXECUTABLE_DEVICE__PARENT_DEVICE;
	/**
	 * The feature id for the '<em><b>Dev Mgr</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__DEV_MGR = ScaPackage.SCA_EXECUTABLE_DEVICE__DEV_MGR;
	/**
	 * The feature id for the '<em><b>Profile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__PROFILE = ScaPackage.SCA_EXECUTABLE_DEVICE__PROFILE;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH = ScaPackage.SCA_EXECUTABLE_DEVICE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__MODE = ScaPackage.SCA_EXECUTABLE_DEVICE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID = ScaPackage.SCA_EXECUTABLE_DEVICE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAM = ScaPackage.SCA_EXECUTABLE_DEVICE_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Executable Device</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_EXECUTABLE_DEVICE_FEATURE_COUNT = ScaPackage.SCA_EXECUTABLE_DEVICE_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaLoadableDeviceImpl <em>Local Sca Loadable Device</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaLoadableDeviceImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaLoadableDevice()
	 * @generated
	 */
	int LOCAL_SCA_LOADABLE_DEVICE = 12;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__STATUS = ScaPackage.SCA_LOADABLE_DEVICE__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__DISPOSED = ScaPackage.SCA_LOADABLE_DEVICE__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__DATA_PROVIDERS = ScaPackage.SCA_LOADABLE_DEVICE__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_LOADABLE_DEVICE__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__IOR = ScaPackage.SCA_LOADABLE_DEVICE__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__OBJ = ScaPackage.SCA_LOADABLE_DEVICE__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__CORBA_OBJ = ScaPackage.SCA_LOADABLE_DEVICE__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PROFILE_URI = ScaPackage.SCA_LOADABLE_DEVICE__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PROFILE_OBJ = ScaPackage.SCA_LOADABLE_DEVICE__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__ROOT_FILE_STORE = ScaPackage.SCA_LOADABLE_DEVICE__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PROPERTIES = ScaPackage.SCA_LOADABLE_DEVICE__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PORTS = ScaPackage.SCA_LOADABLE_DEVICE__PORTS;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__IDENTIFIER = ScaPackage.SCA_LOADABLE_DEVICE__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__STARTED = ScaPackage.SCA_LOADABLE_DEVICE__STARTED;
	/**
	 * The feature id for the '<em><b>Child Devices</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__CHILD_DEVICES = ScaPackage.SCA_LOADABLE_DEVICE__CHILD_DEVICES;
	/**
	 * The feature id for the '<em><b>Admin State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__ADMIN_STATE = ScaPackage.SCA_LOADABLE_DEVICE__ADMIN_STATE;
	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__LABEL = ScaPackage.SCA_LOADABLE_DEVICE__LABEL;
	/**
	 * The feature id for the '<em><b>Operational State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__OPERATIONAL_STATE = ScaPackage.SCA_LOADABLE_DEVICE__OPERATIONAL_STATE;
	/**
	 * The feature id for the '<em><b>Usage State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__USAGE_STATE = ScaPackage.SCA_LOADABLE_DEVICE__USAGE_STATE;
	/**
	 * The feature id for the '<em><b>Parent Device</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PARENT_DEVICE = ScaPackage.SCA_LOADABLE_DEVICE__PARENT_DEVICE;
	/**
	 * The feature id for the '<em><b>Dev Mgr</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__DEV_MGR = ScaPackage.SCA_LOADABLE_DEVICE__DEV_MGR;
	/**
	 * The feature id for the '<em><b>Profile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__PROFILE = ScaPackage.SCA_LOADABLE_DEVICE__PROFILE;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__LAUNCH = ScaPackage.SCA_LOADABLE_DEVICE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__MODE = ScaPackage.SCA_LOADABLE_DEVICE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__IMPLEMENTATION_ID = ScaPackage.SCA_LOADABLE_DEVICE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE__EXEC_PARAM = ScaPackage.SCA_LOADABLE_DEVICE_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Loadable Device</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_LOADABLE_DEVICE_FEATURE_COUNT = ScaPackage.SCA_LOADABLE_DEVICE_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaDeviceImpl <em>Local Sca Device</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaDeviceImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaDevice()
	 * @generated
	 */
	int LOCAL_SCA_DEVICE = 13;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__STATUS = ScaPackage.SCA_DEVICE__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__DISPOSED = ScaPackage.SCA_DEVICE__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__DATA_PROVIDERS = ScaPackage.SCA_DEVICE__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_DEVICE__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__IOR = ScaPackage.SCA_DEVICE__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__OBJ = ScaPackage.SCA_DEVICE__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__CORBA_OBJ = ScaPackage.SCA_DEVICE__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Profile URI</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PROFILE_URI = ScaPackage.SCA_DEVICE__PROFILE_URI;
	/**
	 * The feature id for the '<em><b>Profile Obj</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PROFILE_OBJ = ScaPackage.SCA_DEVICE__PROFILE_OBJ;
	/**
	 * The feature id for the '<em><b>Root File Store</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__ROOT_FILE_STORE = ScaPackage.SCA_DEVICE__ROOT_FILE_STORE;
	/**
	 * The feature id for the '<em><b>Properties</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PROPERTIES = ScaPackage.SCA_DEVICE__PROPERTIES;
	/**
	 * The feature id for the '<em><b>Ports</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PORTS = ScaPackage.SCA_DEVICE__PORTS;
	/**
	 * The feature id for the '<em><b>Identifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__IDENTIFIER = ScaPackage.SCA_DEVICE__IDENTIFIER;
	/**
	 * The feature id for the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__STARTED = ScaPackage.SCA_DEVICE__STARTED;
	/**
	 * The feature id for the '<em><b>Child Devices</b></em>' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__CHILD_DEVICES = ScaPackage.SCA_DEVICE__CHILD_DEVICES;
	/**
	 * The feature id for the '<em><b>Admin State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__ADMIN_STATE = ScaPackage.SCA_DEVICE__ADMIN_STATE;
	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__LABEL = ScaPackage.SCA_DEVICE__LABEL;
	/**
	 * The feature id for the '<em><b>Operational State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__OPERATIONAL_STATE = ScaPackage.SCA_DEVICE__OPERATIONAL_STATE;
	/**
	 * The feature id for the '<em><b>Usage State</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__USAGE_STATE = ScaPackage.SCA_DEVICE__USAGE_STATE;
	/**
	 * The feature id for the '<em><b>Parent Device</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PARENT_DEVICE = ScaPackage.SCA_DEVICE__PARENT_DEVICE;
	/**
	 * The feature id for the '<em><b>Dev Mgr</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__DEV_MGR = ScaPackage.SCA_DEVICE__DEV_MGR;
	/**
	 * The feature id for the '<em><b>Profile</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__PROFILE = ScaPackage.SCA_DEVICE__PROFILE;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__LAUNCH = ScaPackage.SCA_DEVICE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__MODE = ScaPackage.SCA_DEVICE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__IMPLEMENTATION_ID = ScaPackage.SCA_DEVICE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE__EXEC_PARAM = ScaPackage.SCA_DEVICE_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Device</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_DEVICE_FEATURE_COUNT = ScaPackage.SCA_DEVICE_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '{@link gov.redhawk.ide.debug.impl.LocalScaServiceImpl <em>Local Sca Service</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.LocalScaServiceImpl
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaService()
	 * @generated
	 */
	int LOCAL_SCA_SERVICE = 14;
	/**
	 * The feature id for the '<em><b>Status</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__STATUS = ScaPackage.SCA_SERVICE__STATUS;
	/**
	 * The feature id for the '<em><b>Disposed</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__DISPOSED = ScaPackage.SCA_SERVICE__DISPOSED;
	/**
	 * The feature id for the '<em><b>Data Providers</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__DATA_PROVIDERS = ScaPackage.SCA_SERVICE__DATA_PROVIDERS;
	/**
	 * The feature id for the '<em><b>Data Providers Enabled</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__DATA_PROVIDERS_ENABLED = ScaPackage.SCA_SERVICE__DATA_PROVIDERS_ENABLED;
	/**
	 * The feature id for the '<em><b>Ior</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__IOR = ScaPackage.SCA_SERVICE__IOR;
	/**
	 * The feature id for the '<em><b>Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__OBJ = ScaPackage.SCA_SERVICE__OBJ;
	/**
	 * The feature id for the '<em><b>Corba Obj</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__CORBA_OBJ = ScaPackage.SCA_SERVICE__CORBA_OBJ;
	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__NAME = ScaPackage.SCA_SERVICE__NAME;
	/**
	 * The feature id for the '<em><b>Launch</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__LAUNCH = ScaPackage.SCA_SERVICE_FEATURE_COUNT + 0;
	/**
	 * The feature id for the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__MODE = ScaPackage.SCA_SERVICE_FEATURE_COUNT + 1;
	/**
	 * The feature id for the '<em><b>Implementation ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__IMPLEMENTATION_ID = ScaPackage.SCA_SERVICE_FEATURE_COUNT + 2;
	/**
	 * The feature id for the '<em><b>Exec Param</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE__EXEC_PARAM = ScaPackage.SCA_SERVICE_FEATURE_COUNT + 3;
	/**
	 * The number of structural features of the '<em>Local Sca Service</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int LOCAL_SCA_SERVICE_FEATURE_COUNT = ScaPackage.SCA_SERVICE_FEATURE_COUNT + 4;
	/**
	 * The meta object id for the '<em>Name</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.jacorb.naming.Name
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getName_()
	 * @generated
	 */
	int NAME = 16;
	/**
	 * The meta object id for the '<em>Naming Context</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.omg.CosNaming.NamingContext
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNamingContext()
	 * @generated
	 */
	int NAMING_CONTEXT = 17;
	/**
	 * The meta object id for the '<em>ILaunch</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.debug.core.ILaunch
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getILaunch()
	 * @generated
	 */
	int ILAUNCH = 18;
	/**
	 * The meta object id for the '<em>Name Component Array</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameComponentArray()
	 * @generated
	 */
	int NAME_COMPONENT_ARRAY = 19;
	/**
	 * The meta object id for the '{@link ExtendedCF.ApplicationExtOperations <em>Application Ext Operations</em>}' class.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @see ExtendedCF.ApplicationExtOperations
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getApplicationExtOperations()
	 * @generated
	 */
	int APPLICATION_EXT_OPERATIONS = 15;
	/**
	 * The number of structural features of the '<em>Application Ext Operations</em>' class.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int APPLICATION_EXT_OPERATIONS_FEATURE_COUNT = CfPackage.APPLICATION_OPERATIONS_FEATURE_COUNT + 0;
	/**
	 * The meta object id for the '<em>Servant Not Active</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.omg.PortableServer.POAPackage.ServantNotActive
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getServantNotActive()
	 * @generated
	 */
	int SERVANT_NOT_ACTIVE = 20;
	/**
	 * The meta object id for the '<em>Wrong Policy</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.omg.PortableServer.POAPackage.WrongPolicy
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getWrongPolicy()
	 * @generated
	 */
	int WRONG_POLICY = 21;
	/**
	 * The meta object id for the '<em>Core Exception</em>' data type.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.core.runtime.CoreException
	 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getCoreException()
	 * @generated
	 */
	int CORE_EXCEPTION = 22;

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalSca <em>Local Sca</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca
	 * @generated
	 */
	EClass getLocalSca();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalSca#getOrb <em>Orb</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Orb</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getOrb()
	 * @see #getLocalSca()
	 * @generated
	 */
	EAttribute getLocalSca_Orb();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalSca#getPoa <em>Poa</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Poa</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getPoa()
	 * @see #getLocalSca()
	 * @generated
	 */
	EAttribute getLocalSca_Poa();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.debug.LocalSca#getWaveforms <em>Waveforms</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Waveforms</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getWaveforms()
	 * @see #getLocalSca()
	 * @generated
	 */
	EReference getLocalSca_Waveforms();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalSca#getSandboxWaveform <em>Sandbox Waveform</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Sandbox Waveform</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getSandboxWaveform()
	 * @see #getLocalSca()
	 * @generated
	 */
	EReference getLocalSca_SandboxWaveform();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalSca#getSandboxDeviceManager <em>Sandbox Device Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Sandbox Device Manager</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getSandboxDeviceManager()
	 * @see #getLocalSca()
	 * @generated
	 */
	EReference getLocalSca_SandboxDeviceManager();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalSca#getRootContext <em>Root Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Root Context</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getRootContext()
	 * @see #getLocalSca()
	 * @generated
	 */
	EReference getLocalSca_RootContext();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalSca#getFileManager <em>File Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>File Manager</em>'.
	 * @see gov.redhawk.ide.debug.LocalSca#getFileManager()
	 * @see #getLocalSca()
	 * @generated
	 */
	EReference getLocalSca_FileManager();

	/**
	 * Returns the meta object for class '{@link org.omg.CosNaming.NamingContextExtOperations <em>Naming Context Ext Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Naming Context Ext Operations</em>'.
	 * @see org.omg.CosNaming.NamingContextExtOperations
	 * @model instanceClass="org.omg.CosNaming.NamingContextExtOperations"
	 * @generated
	 */
	EClass getNamingContextExtOperations();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.NotifyingNamingContext <em>Notifying Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Notifying Naming Context</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext
	 * @generated
	 */
	EClass getNotifyingNamingContext();

	/**
	 * Returns the meta object for the map '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getObjectMap <em>Object Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Object Map</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getObjectMap()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EReference getNotifyingNamingContext_ObjectMap();

	/**
	 * Returns the meta object for the map '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getContextMap <em>Context Map</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the map '<em>Context Map</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getContextMap()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EReference getNotifyingNamingContext_ContextMap();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getNamingContext <em>Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Naming Context</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getNamingContext()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EAttribute getNotifyingNamingContext_NamingContext();

	/**
	 * Returns the meta object for the containment reference list '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getSubContexts <em>Sub Contexts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Sub Contexts</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getSubContexts()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EReference getNotifyingNamingContext_SubContexts();

	/**
	 * Returns the meta object for the container reference '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext <em>Parent Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the container reference '<em>Parent Context</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EReference getNotifyingNamingContext_ParentContext();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getPoa <em>Poa</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Poa</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getPoa()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EAttribute getNotifyingNamingContext_Poa();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getName()
	 * @see #getNotifyingNamingContext()
	 * @generated
	 */
	EAttribute getNotifyingNamingContext_Name();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Name To Object Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Name To Object Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="gov.redhawk.ide.debug.Name"
	 *        valueDataType="gov.redhawk.model.sca.Object"
	 * @generated
	 */
	EClass getNameToObjectEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getNameToObjectEntry()
	 * @generated
	 */
	EAttribute getNameToObjectEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getNameToObjectEntry()
	 * @generated
	 */
	EAttribute getNameToObjectEntry_Value();

	/**
	 * Returns the meta object for class '{@link java.util.Map.Entry <em>Name To Naming Context Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Name To Naming Context Entry</em>'.
	 * @see java.util.Map.Entry
	 * @model keyDataType="gov.redhawk.ide.debug.Name"
	 *        valueDataType="gov.redhawk.ide.debug.NamingContext"
	 * @generated
	 */
	EClass getNameToNamingContextEntry();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see java.util.Map.Entry
	 * @see #getNameToNamingContextEntry()
	 * @generated
	 */
	EAttribute getNameToNamingContextEntry_Key();

	/**
	 * Returns the meta object for the attribute '{@link java.util.Map.Entry <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see java.util.Map.Entry
	 * @see #getNameToNamingContextEntry()
	 * @generated
	 */
	EAttribute getNameToNamingContextEntry_Value();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalFileManager <em>Local File Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local File Manager</em>'.
	 * @see gov.redhawk.ide.debug.LocalFileManager
	 * @generated
	 */
	EClass getLocalFileManager();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalLaunch <em>Local Launch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Launch</em>'.
	 * @see gov.redhawk.ide.debug.LocalLaunch
	 * @generated
	 */
	EClass getLocalLaunch();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalLaunch#getLaunch <em>Launch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Launch</em>'.
	 * @see gov.redhawk.ide.debug.LocalLaunch#getLaunch()
	 * @see #getLocalLaunch()
	 * @generated
	 */
	EAttribute getLocalLaunch_Launch();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalLaunch#getMode <em>Mode</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mode</em>'.
	 * @see gov.redhawk.ide.debug.LocalLaunch#getMode()
	 * @see #getLocalLaunch()
	 * @generated
	 */
	EAttribute getLocalLaunch_Mode();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalAbstractComponent <em>Local Abstract Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Abstract Component</em>'.
	 * @see gov.redhawk.ide.debug.LocalAbstractComponent
	 * @generated
	 */
	EClass getLocalAbstractComponent();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getImplementationID <em>Implementation ID</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Implementation ID</em>'.
	 * @see gov.redhawk.ide.debug.LocalAbstractComponent#getImplementationID()
	 * @see #getLocalAbstractComponent()
	 * @generated
	 */
	EAttribute getLocalAbstractComponent_ImplementationID();

	/**
	 * Returns the meta object for the attribute '{@link gov.redhawk.ide.debug.LocalAbstractComponent#getExecParam <em>Exec Param</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Exec Param</em>'.
	 * @see gov.redhawk.ide.debug.LocalAbstractComponent#getExecParam()
	 * @see #getLocalAbstractComponent()
	 * @generated
	 */
	EAttribute getLocalAbstractComponent_ExecParam();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaWaveform <em>Local Sca Waveform</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Waveform</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaWaveform
	 * @generated
	 */
	EClass getLocalScaWaveform();

	/**
	 * Returns the meta object for the reference '{@link gov.redhawk.ide.debug.LocalScaWaveform#getNamingContext <em>Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Naming Context</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#getNamingContext()
	 * @see #getLocalScaWaveform()
	 * @generated
	 */
	EReference getLocalScaWaveform_NamingContext();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalScaWaveform#getLocalApp <em>Local App</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Local App</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#getLocalApp()
	 * @see #getLocalScaWaveform()
	 * @generated
	 */
	EReference getLocalScaWaveform_LocalApp();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaComponent <em>Local Sca Component</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Component</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaComponent
	 * @generated
	 */
	EClass getLocalScaComponent();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaDeviceManager <em>Local Sca Device Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Device Manager</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager
	 * @generated
	 */
	EClass getLocalScaDeviceManager();

	/**
	 * Returns the meta object for the reference '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getNamingContext <em>Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Naming Context</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager#getNamingContext()
	 * @see #getLocalScaDeviceManager()
	 * @generated
	 */
	EReference getLocalScaDeviceManager_NamingContext();

	/**
	 * Returns the meta object for the containment reference '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#getLocalDeviceManager <em>Local Device Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Local Device Manager</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager#getLocalDeviceManager()
	 * @see #getLocalScaDeviceManager()
	 * @generated
	 */
	EReference getLocalScaDeviceManager_LocalDeviceManager();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaExecutableDevice <em>Local Sca Executable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Executable Device</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaExecutableDevice
	 * @generated
	 */
	EClass getLocalScaExecutableDevice();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaLoadableDevice <em>Local Sca Loadable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Loadable Device</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaLoadableDevice
	 * @generated
	 */
	EClass getLocalScaLoadableDevice();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaDevice <em>Local Sca Device</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Device</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaDevice
	 * @generated
	 */
	EClass getLocalScaDevice();

	/**
	 * Returns the meta object for class '{@link gov.redhawk.ide.debug.LocalScaService <em>Local Sca Service</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Local Sca Service</em>'.
	 * @see gov.redhawk.ide.debug.LocalScaService
	 * @generated
	 */
	EClass getLocalScaService();

	/**
	 * Returns the meta object for data type '{@link org.jacorb.naming.Name <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Name</em>'.
	 * @see org.jacorb.naming.Name
	 * @model instanceClass="org.jacorb.naming.Name"
	 * @generated
	 */
	EDataType getName_();

	/**
	 * Returns the meta object for data type '{@link org.omg.CosNaming.NamingContext <em>Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Naming Context</em>'.
	 * @see org.omg.CosNaming.NamingContext
	 * @model instanceClass="org.omg.CosNaming.NamingContext"
	 * @generated
	 */
	EDataType getNamingContext();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.debug.core.ILaunch <em>ILaunch</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>ILaunch</em>'.
	 * @see org.eclipse.debug.core.ILaunch
	 * @model instanceClass="org.eclipse.debug.core.ILaunch" serializeable="false"
	 * @generated
	 */
	EDataType getILaunch();

	/**
	 * Returns the meta object for data type '<em>Name Component Array</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Name Component Array</em>'.
	 * @model instanceClass="org.omg.CosNaming.NameComponent[]" serializeable="false"
	 * @generated
	 */
	EDataType getNameComponentArray();

	/**
	 * Returns the meta object for class '{@link ExtendedCF.ApplicationExtOperations <em>Application Ext Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Application Ext Operations</em>'.
	 * @see ExtendedCF.ApplicationExtOperations
	 * @model instanceClass="ExtendedCF.ApplicationExtOperations" superTypes="mil.jpeojtrs.sca.cf.ApplicationOperations"
	 * @generated
	 */
	EClass getApplicationExtOperations();

	/**
	 * Returns the meta object for data type '{@link org.omg.PortableServer.POAPackage.ServantNotActive <em>Servant Not Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Servant Not Active</em>'.
	 * @see org.omg.PortableServer.POAPackage.ServantNotActive
	 * @model instanceClass="org.omg.PortableServer.POAPackage.ServantNotActive" serializeable="false"
	 * @generated
	 */
	EDataType getServantNotActive();

	/**
	 * Returns the meta object for data type '{@link org.omg.PortableServer.POAPackage.WrongPolicy <em>Wrong Policy</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Wrong Policy</em>'.
	 * @see org.omg.PortableServer.POAPackage.WrongPolicy
	 * @model instanceClass="org.omg.PortableServer.POAPackage.WrongPolicy" serializeable="false"
	 * @generated
	 */
	EDataType getWrongPolicy();

	/**
	 * Returns the meta object for data type '{@link org.eclipse.core.runtime.CoreException <em>Core Exception</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for data type '<em>Core Exception</em>'.
	 * @see org.eclipse.core.runtime.CoreException
	 * @model instanceClass="org.eclipse.core.runtime.CoreException" serializeable="false"
	 * @generated
	 */
	EDataType getCoreException();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ScaDebugFactory getScaDebugFactory();

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
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaImpl <em>Local Sca</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalSca()
		 * @generated
		 */
		EClass LOCAL_SCA = eINSTANCE.getLocalSca();
		/**
		 * The meta object literal for the '<em><b>Orb</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_SCA__ORB = eINSTANCE.getLocalSca_Orb();
		/**
		 * The meta object literal for the '<em><b>Poa</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_SCA__POA = eINSTANCE.getLocalSca_Poa();
		/**
		 * The meta object literal for the '<em><b>Waveforms</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA__WAVEFORMS = eINSTANCE.getLocalSca_Waveforms();
		/**
		 * The meta object literal for the '<em><b>Sandbox Waveform</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA__SANDBOX_WAVEFORM = eINSTANCE.getLocalSca_SandboxWaveform();
		/**
		 * The meta object literal for the '<em><b>Sandbox Device Manager</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA__SANDBOX_DEVICE_MANAGER = eINSTANCE.getLocalSca_SandboxDeviceManager();
		/**
		 * The meta object literal for the '<em><b>Root Context</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA__ROOT_CONTEXT = eINSTANCE.getLocalSca_RootContext();
		/**
		 * The meta object literal for the '<em><b>File Manager</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA__FILE_MANAGER = eINSTANCE.getLocalSca_FileManager();
		/**
		 * The meta object literal for the '{@link org.omg.CosNaming.NamingContextExtOperations <em>Naming Context Ext Operations</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.omg.CosNaming.NamingContextExtOperations
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNamingContextExtOperations()
		 * @generated
		 */
		EClass NAMING_CONTEXT_EXT_OPERATIONS = eINSTANCE.getNamingContextExtOperations();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl <em>Notifying Naming Context</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNotifyingNamingContext()
		 * @generated
		 */
		EClass NOTIFYING_NAMING_CONTEXT = eINSTANCE.getNotifyingNamingContext();
		/**
		 * The meta object literal for the '<em><b>Object Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NOTIFYING_NAMING_CONTEXT__OBJECT_MAP = eINSTANCE.getNotifyingNamingContext_ObjectMap();
		/**
		 * The meta object literal for the '<em><b>Context Map</b></em>' map feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP = eINSTANCE.getNotifyingNamingContext_ContextMap();
		/**
		 * The meta object literal for the '<em><b>Naming Context</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT = eINSTANCE.getNotifyingNamingContext_NamingContext();
		/**
		 * The meta object literal for the '<em><b>Sub Contexts</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS = eINSTANCE.getNotifyingNamingContext_SubContexts();
		/**
		 * The meta object literal for the '<em><b>Parent Context</b></em>' container reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT = eINSTANCE.getNotifyingNamingContext_ParentContext();
		/**
		 * The meta object literal for the '<em><b>Poa</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTIFYING_NAMING_CONTEXT__POA = eINSTANCE.getNotifyingNamingContext_Poa();
		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NOTIFYING_NAMING_CONTEXT__NAME = eINSTANCE.getNotifyingNamingContext_Name();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.NameToObjectEntryImpl <em>Name To Object Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.NameToObjectEntryImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameToObjectEntry()
		 * @generated
		 */
		EClass NAME_TO_OBJECT_ENTRY = eINSTANCE.getNameToObjectEntry();
		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAME_TO_OBJECT_ENTRY__KEY = eINSTANCE.getNameToObjectEntry_Key();
		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAME_TO_OBJECT_ENTRY__VALUE = eINSTANCE.getNameToObjectEntry_Value();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.NameToNamingContextEntryImpl <em>Name To Naming Context Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.NameToNamingContextEntryImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameToNamingContextEntry()
		 * @generated
		 */
		EClass NAME_TO_NAMING_CONTEXT_ENTRY = eINSTANCE.getNameToNamingContextEntry();
		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAME_TO_NAMING_CONTEXT_ENTRY__KEY = eINSTANCE.getNameToNamingContextEntry_Key();
		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NAME_TO_NAMING_CONTEXT_ENTRY__VALUE = eINSTANCE.getNameToNamingContextEntry_Value();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalFileManagerImpl <em>Local File Manager</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalFileManagerImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalFileManager()
		 * @generated
		 */
		EClass LOCAL_FILE_MANAGER = eINSTANCE.getLocalFileManager();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.LocalLaunch <em>Local Launch</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.LocalLaunch
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalLaunch()
		 * @generated
		 */
		EClass LOCAL_LAUNCH = eINSTANCE.getLocalLaunch();
		/**
		 * The meta object literal for the '<em><b>Launch</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_LAUNCH__LAUNCH = eINSTANCE.getLocalLaunch_Launch();
		/**
		 * The meta object literal for the '<em><b>Mode</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_LAUNCH__MODE = eINSTANCE.getLocalLaunch_Mode();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.LocalAbstractComponent <em>Local Abstract Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.LocalAbstractComponent
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalAbstractComponent()
		 * @generated
		 */
		EClass LOCAL_ABSTRACT_COMPONENT = eINSTANCE.getLocalAbstractComponent();
		/**
		 * The meta object literal for the '<em><b>Implementation ID</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_ABSTRACT_COMPONENT__IMPLEMENTATION_ID = eINSTANCE.getLocalAbstractComponent_ImplementationID();
		/**
		 * The meta object literal for the '<em><b>Exec Param</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute LOCAL_ABSTRACT_COMPONENT__EXEC_PARAM = eINSTANCE.getLocalAbstractComponent_ExecParam();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaWaveformImpl <em>Local Sca Waveform</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaWaveformImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaWaveform()
		 * @generated
		 */
		EClass LOCAL_SCA_WAVEFORM = eINSTANCE.getLocalScaWaveform();
		/**
		 * The meta object literal for the '<em><b>Naming Context</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA_WAVEFORM__NAMING_CONTEXT = eINSTANCE.getLocalScaWaveform_NamingContext();
		/**
		 * The meta object literal for the '<em><b>Local App</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA_WAVEFORM__LOCAL_APP = eINSTANCE.getLocalScaWaveform_LocalApp();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaComponentImpl <em>Local Sca Component</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaComponentImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaComponent()
		 * @generated
		 */
		EClass LOCAL_SCA_COMPONENT = eINSTANCE.getLocalScaComponent();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl <em>Local Sca Device Manager</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaDeviceManager()
		 * @generated
		 */
		EClass LOCAL_SCA_DEVICE_MANAGER = eINSTANCE.getLocalScaDeviceManager();
		/**
		 * The meta object literal for the '<em><b>Naming Context</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT = eINSTANCE.getLocalScaDeviceManager_NamingContext();
		/**
		 * The meta object literal for the '<em><b>Local Device Manager</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER = eINSTANCE.getLocalScaDeviceManager_LocalDeviceManager();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl <em>Local Sca Executable Device</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaExecutableDevice()
		 * @generated
		 */
		EClass LOCAL_SCA_EXECUTABLE_DEVICE = eINSTANCE.getLocalScaExecutableDevice();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaLoadableDeviceImpl <em>Local Sca Loadable Device</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaLoadableDeviceImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaLoadableDevice()
		 * @generated
		 */
		EClass LOCAL_SCA_LOADABLE_DEVICE = eINSTANCE.getLocalScaLoadableDevice();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaDeviceImpl <em>Local Sca Device</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaDeviceImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaDevice()
		 * @generated
		 */
		EClass LOCAL_SCA_DEVICE = eINSTANCE.getLocalScaDevice();
		/**
		 * The meta object literal for the '{@link gov.redhawk.ide.debug.impl.LocalScaServiceImpl <em>Local Sca Service</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.LocalScaServiceImpl
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getLocalScaService()
		 * @generated
		 */
		EClass LOCAL_SCA_SERVICE = eINSTANCE.getLocalScaService();
		/**
		 * The meta object literal for the '<em>Name</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.jacorb.naming.Name
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getName_()
		 * @generated
		 */
		EDataType NAME = eINSTANCE.getName_();
		/**
		 * The meta object literal for the '<em>Naming Context</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.omg.CosNaming.NamingContext
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNamingContext()
		 * @generated
		 */
		EDataType NAMING_CONTEXT = eINSTANCE.getNamingContext();
		/**
		 * The meta object literal for the '<em>ILaunch</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.debug.core.ILaunch
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getILaunch()
		 * @generated
		 */
		EDataType ILAUNCH = eINSTANCE.getILaunch();
		/**
		 * The meta object literal for the '<em>Name Component Array</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getNameComponentArray()
		 * @generated
		 */
		EDataType NAME_COMPONENT_ARRAY = eINSTANCE.getNameComponentArray();
		/**
		 * The meta object literal for the '{@link ExtendedCF.ApplicationExtOperations <em>Application Ext Operations</em>}' class.
		 * <!-- begin-user-doc -->
		 * @since 3.0
		 * <!-- end-user-doc -->
		 * @see ExtendedCF.ApplicationExtOperations
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getApplicationExtOperations()
		 * @generated
		 */
		EClass APPLICATION_EXT_OPERATIONS = eINSTANCE.getApplicationExtOperations();
		/**
		 * The meta object literal for the '<em>Servant Not Active</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.omg.PortableServer.POAPackage.ServantNotActive
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getServantNotActive()
		 * @generated
		 */
		EDataType SERVANT_NOT_ACTIVE = eINSTANCE.getServantNotActive();
		/**
		 * The meta object literal for the '<em>Wrong Policy</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.omg.PortableServer.POAPackage.WrongPolicy
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getWrongPolicy()
		 * @generated
		 */
		EDataType WRONG_POLICY = eINSTANCE.getWrongPolicy();
		/**
		 * The meta object literal for the '<em>Core Exception</em>' data type.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.core.runtime.CoreException
		 * @see gov.redhawk.ide.debug.impl.ScaDebugPackageImpl#getCoreException()
		 * @generated
		 */
		EDataType CORE_EXCEPTION = eINSTANCE.getCoreException();

	}

} //ScaDebugPackage
