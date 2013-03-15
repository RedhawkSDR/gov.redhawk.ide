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
package gov.redhawk.ide.debug.impl;

import ExtendedCF.ApplicationExtOperations;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.LocalFileManager;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaComponent;
import gov.redhawk.ide.debug.LocalScaDevice;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.LocalScaExecutableDevice;
import gov.redhawk.ide.debug.LocalScaLoadableDevice;
import gov.redhawk.ide.debug.LocalScaService;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import gov.redhawk.ide.debug.internal.cf.impl.DeviceManagerImpl;
import gov.redhawk.model.sca.ScaPackage;

import java.util.Map.Entry;

import mil.jpeojtrs.sca.cf.CfPackage;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.jacorb.naming.Name;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtOperations;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ScaDebugPackageImpl extends EPackageImpl implements ScaDebugPackage {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass namingContextExtOperationsEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass notifyingNamingContextEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nameToObjectEntryEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nameToNamingContextEntryEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localFileManagerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localLaunchEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localAbstractComponentEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaWaveformEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaComponentEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaDeviceManagerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaExecutableDeviceEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaLoadableDeviceEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaDeviceEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass localScaServiceEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass applicationExtOperationsEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType nameEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType namingContextEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iLaunchEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType nameComponentArrayEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType servantNotActiveEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType wrongPolicyEDataType = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType coreExceptionEDataType = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ScaDebugPackageImpl() {
		super(eNS_URI, ScaDebugFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link ScaDebugPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ScaDebugPackage init() {
		if (isInited) return (ScaDebugPackage)EPackage.Registry.INSTANCE.getEPackage(ScaDebugPackage.eNS_URI);

		// Obtain or create and register package
		ScaDebugPackageImpl theScaDebugPackage = (ScaDebugPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ScaDebugPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ScaDebugPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		ScaPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theScaDebugPackage.createPackageContents();

		// Initialize created meta-data
		theScaDebugPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theScaDebugPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(ScaDebugPackage.eNS_URI, theScaDebugPackage);
		return theScaDebugPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalSca() {
		return localScaEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalSca_Orb() {
		return (EAttribute)localScaEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalSca_Poa() {
		return (EAttribute)localScaEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalSca_Waveforms() {
		return (EReference)localScaEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalSca_SandboxWaveform() {
		return (EReference)localScaEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalSca_SandboxDeviceManager() {
		return (EReference)localScaEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalSca_RootContext() {
		return (EReference)localScaEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalSca_FileManager() {
		return (EReference)localScaEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNamingContextExtOperations() {
		return namingContextExtOperationsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNotifyingNamingContext() {
		return notifyingNamingContextEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNotifyingNamingContext_ObjectMap() {
		return (EReference)notifyingNamingContextEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNotifyingNamingContext_ContextMap() {
		return (EReference)notifyingNamingContextEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNotifyingNamingContext_NamingContext() {
		return (EAttribute)notifyingNamingContextEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNotifyingNamingContext_SubContexts() {
		return (EReference)notifyingNamingContextEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNotifyingNamingContext_ParentContext() {
		return (EReference)notifyingNamingContextEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNotifyingNamingContext_Poa() {
		return (EAttribute)notifyingNamingContextEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNotifyingNamingContext_Name() {
		return (EAttribute)notifyingNamingContextEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNameToObjectEntry() {
		return nameToObjectEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNameToObjectEntry_Key() {
		return (EAttribute)nameToObjectEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNameToObjectEntry_Value() {
		return (EAttribute)nameToObjectEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNameToNamingContextEntry() {
		return nameToNamingContextEntryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNameToNamingContextEntry_Key() {
		return (EAttribute)nameToNamingContextEntryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getNameToNamingContextEntry_Value() {
		return (EAttribute)nameToNamingContextEntryEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalFileManager() {
		return localFileManagerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalLaunch() {
		return localLaunchEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalLaunch_Launch() {
		return (EAttribute)localLaunchEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalLaunch_Mode() {
		return (EAttribute)localLaunchEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalAbstractComponent() {
		return localAbstractComponentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalAbstractComponent_ImplementationID() {
		return (EAttribute)localAbstractComponentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getLocalAbstractComponent_ExecParam() {
		return (EAttribute)localAbstractComponentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaWaveform() {
		return localScaWaveformEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalScaWaveform_NamingContext() {
		return (EReference)localScaWaveformEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalScaWaveform_LocalApp() {
		return (EReference)localScaWaveformEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaComponent() {
		return localScaComponentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaDeviceManager() {
		return localScaDeviceManagerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalScaDeviceManager_NamingContext() {
		return (EReference)localScaDeviceManagerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getLocalScaDeviceManager_LocalDeviceManager() {
		return (EReference)localScaDeviceManagerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaExecutableDevice() {
		return localScaExecutableDeviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaLoadableDevice() {
		return localScaLoadableDeviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaDevice() {
		return localScaDeviceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getLocalScaService() {
		return localScaServiceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getName_() {
		return nameEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getNamingContext() {
		return namingContextEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getILaunch() {
		return iLaunchEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getNameComponentArray() {
		return nameComponentArrayEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getApplicationExtOperations() {
		return applicationExtOperationsEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getServantNotActive() {
		return servantNotActiveEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getWrongPolicy() {
		return wrongPolicyEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getCoreException() {
		return coreExceptionEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaDebugFactory getScaDebugFactory() {
		return (ScaDebugFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		localScaEClass = createEClass(LOCAL_SCA);
		createEAttribute(localScaEClass, LOCAL_SCA__ORB);
		createEAttribute(localScaEClass, LOCAL_SCA__POA);
		createEReference(localScaEClass, LOCAL_SCA__WAVEFORMS);
		createEReference(localScaEClass, LOCAL_SCA__SANDBOX_WAVEFORM);
		createEReference(localScaEClass, LOCAL_SCA__SANDBOX_DEVICE_MANAGER);
		createEReference(localScaEClass, LOCAL_SCA__ROOT_CONTEXT);
		createEReference(localScaEClass, LOCAL_SCA__FILE_MANAGER);

		namingContextExtOperationsEClass = createEClass(NAMING_CONTEXT_EXT_OPERATIONS);

		notifyingNamingContextEClass = createEClass(NOTIFYING_NAMING_CONTEXT);
		createEReference(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__OBJECT_MAP);
		createEReference(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP);
		createEAttribute(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT);
		createEReference(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS);
		createEReference(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT);
		createEAttribute(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__POA);
		createEAttribute(notifyingNamingContextEClass, NOTIFYING_NAMING_CONTEXT__NAME);

		nameToObjectEntryEClass = createEClass(NAME_TO_OBJECT_ENTRY);
		createEAttribute(nameToObjectEntryEClass, NAME_TO_OBJECT_ENTRY__KEY);
		createEAttribute(nameToObjectEntryEClass, NAME_TO_OBJECT_ENTRY__VALUE);

		nameToNamingContextEntryEClass = createEClass(NAME_TO_NAMING_CONTEXT_ENTRY);
		createEAttribute(nameToNamingContextEntryEClass, NAME_TO_NAMING_CONTEXT_ENTRY__KEY);
		createEAttribute(nameToNamingContextEntryEClass, NAME_TO_NAMING_CONTEXT_ENTRY__VALUE);

		localFileManagerEClass = createEClass(LOCAL_FILE_MANAGER);

		localLaunchEClass = createEClass(LOCAL_LAUNCH);
		createEAttribute(localLaunchEClass, LOCAL_LAUNCH__LAUNCH);
		createEAttribute(localLaunchEClass, LOCAL_LAUNCH__MODE);

		localAbstractComponentEClass = createEClass(LOCAL_ABSTRACT_COMPONENT);
		createEAttribute(localAbstractComponentEClass, LOCAL_ABSTRACT_COMPONENT__IMPLEMENTATION_ID);
		createEAttribute(localAbstractComponentEClass, LOCAL_ABSTRACT_COMPONENT__EXEC_PARAM);

		localScaWaveformEClass = createEClass(LOCAL_SCA_WAVEFORM);
		createEReference(localScaWaveformEClass, LOCAL_SCA_WAVEFORM__NAMING_CONTEXT);
		createEReference(localScaWaveformEClass, LOCAL_SCA_WAVEFORM__LOCAL_APP);

		localScaComponentEClass = createEClass(LOCAL_SCA_COMPONENT);

		localScaDeviceManagerEClass = createEClass(LOCAL_SCA_DEVICE_MANAGER);
		createEReference(localScaDeviceManagerEClass, LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT);
		createEReference(localScaDeviceManagerEClass, LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER);

		localScaExecutableDeviceEClass = createEClass(LOCAL_SCA_EXECUTABLE_DEVICE);

		localScaLoadableDeviceEClass = createEClass(LOCAL_SCA_LOADABLE_DEVICE);

		localScaDeviceEClass = createEClass(LOCAL_SCA_DEVICE);

		localScaServiceEClass = createEClass(LOCAL_SCA_SERVICE);

		applicationExtOperationsEClass = createEClass(APPLICATION_EXT_OPERATIONS);

		// Create data types
		nameEDataType = createEDataType(NAME);
		namingContextEDataType = createEDataType(NAMING_CONTEXT);
		iLaunchEDataType = createEDataType(ILAUNCH);
		nameComponentArrayEDataType = createEDataType(NAME_COMPONENT_ARRAY);
		servantNotActiveEDataType = createEDataType(SERVANT_NOT_ACTIVE);
		wrongPolicyEDataType = createEDataType(WRONG_POLICY);
		coreExceptionEDataType = createEDataType(CORE_EXCEPTION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Obtain other dependent packages
		ScaPackage theScaPackage = (ScaPackage)EPackage.Registry.INSTANCE.getEPackage(ScaPackage.eNS_URI);
		CfPackage theCfPackage = (CfPackage)EPackage.Registry.INSTANCE.getEPackage(CfPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		SpdPackage theSpdPackage = (SpdPackage)EPackage.Registry.INSTANCE.getEPackage(SpdPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		localScaEClass.getESuperTypes().add(theScaPackage.getIDisposable());
		notifyingNamingContextEClass.getESuperTypes().add(this.getNamingContextExtOperations());
		notifyingNamingContextEClass.getESuperTypes().add(theScaPackage.getIDisposable());
		localFileManagerEClass.getESuperTypes().add(theScaPackage.getScaFileManager());
		localAbstractComponentEClass.getESuperTypes().add(this.getLocalLaunch());
		localScaWaveformEClass.getESuperTypes().add(theScaPackage.getScaWaveform());
		localScaWaveformEClass.getESuperTypes().add(this.getLocalLaunch());
		localScaWaveformEClass.getESuperTypes().add(this.getApplicationExtOperations());
		localScaComponentEClass.getESuperTypes().add(theScaPackage.getScaComponent());
		localScaComponentEClass.getESuperTypes().add(this.getLocalAbstractComponent());
		localScaDeviceManagerEClass.getESuperTypes().add(theScaPackage.getScaDeviceManager());
		localScaDeviceManagerEClass.getESuperTypes().add(this.getLocalLaunch());
		localScaExecutableDeviceEClass.getESuperTypes().add(theScaPackage.getScaExecutableDevice());
		localScaExecutableDeviceEClass.getESuperTypes().add(this.getLocalAbstractComponent());
		EGenericType g1 = createEGenericType(theScaPackage.getScaLoadableDevice());
		EGenericType g2 = createEGenericType(theCfPackage.getLoadableDevice());
		g1.getETypeArguments().add(g2);
		localScaLoadableDeviceEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getLocalAbstractComponent());
		localScaLoadableDeviceEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(theScaPackage.getScaDevice());
		g2 = createEGenericType(theCfPackage.getDevice());
		g1.getETypeArguments().add(g2);
		localScaDeviceEClass.getEGenericSuperTypes().add(g1);
		g1 = createEGenericType(this.getLocalAbstractComponent());
		localScaDeviceEClass.getEGenericSuperTypes().add(g1);
		localScaServiceEClass.getESuperTypes().add(theScaPackage.getScaService());
		localScaServiceEClass.getESuperTypes().add(this.getLocalAbstractComponent());
		applicationExtOperationsEClass.getESuperTypes().add(theCfPackage.getApplicationOperations());

		// Initialize classes and features; add operations and parameters
		initEClass(localScaEClass, LocalSca.class, "LocalSca", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLocalSca_Orb(), theCfPackage.getORB(), "orb", null, 0, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLocalSca_Poa(), theScaPackage.getPOA(), "poa", null, 0, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalSca_Waveforms(), theScaPackage.getScaWaveform(), null, "waveforms", null, 0, -1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalSca_SandboxWaveform(), this.getLocalScaWaveform(), null, "sandboxWaveform", null, 1, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalSca_SandboxDeviceManager(), this.getLocalScaDeviceManager(), null, "sandboxDeviceManager", null, 1, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalSca_RootContext(), this.getNotifyingNamingContext(), null, "rootContext", null, 1, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalSca_FileManager(), this.getLocalFileManager(), null, "fileManager", null, 1, 1, LocalSca.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(localScaEClass, null, "init", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getCoreException());

		initEClass(namingContextExtOperationsEClass, NamingContextExtOperations.class, "NamingContextExtOperations", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		initEClass(notifyingNamingContextEClass, NotifyingNamingContext.class, "NotifyingNamingContext", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNotifyingNamingContext_ObjectMap(), this.getNameToObjectEntry(), null, "objectMap", null, 0, -1, NotifyingNamingContext.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNotifyingNamingContext_ContextMap(), this.getNameToNamingContextEntry(), null, "contextMap", null, 0, -1, NotifyingNamingContext.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNotifyingNamingContext_NamingContext(), theCfPackage.getNamingContextExt(), "namingContext", null, 1, 1, NotifyingNamingContext.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNotifyingNamingContext_SubContexts(), this.getNotifyingNamingContext(), this.getNotifyingNamingContext_ParentContext(), "subContexts", null, 0, -1, NotifyingNamingContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNotifyingNamingContext_ParentContext(), this.getNotifyingNamingContext(), this.getNotifyingNamingContext_SubContexts(), "parentContext", null, 0, 1, NotifyingNamingContext.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNotifyingNamingContext_Poa(), theScaPackage.getPOA(), "poa", null, 1, 1, NotifyingNamingContext.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNotifyingNamingContext_Name(), theEcorePackage.getEString(), "name", null, 0, 1, NotifyingNamingContext.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		op = addEOperation(notifyingNamingContextEClass, this.getNameComponentArray(), "getName", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theSpdPackage.getURI(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(notifyingNamingContextEClass, theSpdPackage.getURI(), "getURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getNameComponentArray(), "name", 0, 1, IS_UNIQUE, IS_ORDERED);

		addEOperation(notifyingNamingContextEClass, theEcorePackage.getEString(), "getFullName", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(notifyingNamingContextEClass, this.getNotifyingNamingContext(), "getResourceContext", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theSpdPackage.getURI(), "uri", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(notifyingNamingContextEClass, this.getNotifyingNamingContext(), "findContext", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getNamingContext(), "context", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(nameToObjectEntryEClass, Entry.class, "NameToObjectEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNameToObjectEntry_Key(), this.getName_(), "key", null, 0, 1, Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNameToObjectEntry_Value(), theScaPackage.getObject(), "value", null, 0, 1, Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(nameToNamingContextEntryEClass, Entry.class, "NameToNamingContextEntry", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getNameToNamingContextEntry_Key(), this.getName_(), "key", null, 0, 1, Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getNameToNamingContextEntry_Value(), this.getNamingContext(), "value", null, 0, 1, Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(localFileManagerEClass, LocalFileManager.class, "LocalFileManager", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(localLaunchEClass, LocalLaunch.class, "LocalLaunch", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLocalLaunch_Launch(), this.getILaunch(), "launch", null, 0, 1, LocalLaunch.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLocalLaunch_Mode(), theEcorePackage.getEString(), "mode", null, 0, 1, LocalLaunch.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(localAbstractComponentEClass, LocalAbstractComponent.class, "LocalAbstractComponent", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getLocalAbstractComponent_ImplementationID(), theEcorePackage.getEString(), "implementationID", null, 0, 1, LocalAbstractComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getLocalAbstractComponent_ExecParam(), theEcorePackage.getEString(), "execParam", "", 0, 1, LocalAbstractComponent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(localScaWaveformEClass, LocalScaWaveform.class, "LocalScaWaveform", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLocalScaWaveform_NamingContext(), this.getNotifyingNamingContext(), null, "namingContext", null, 1, 1, LocalScaWaveform.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalScaWaveform_LocalApp(), this.getApplicationExtOperations(), null, "localApp", null, 1, 1, LocalScaWaveform.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(localScaWaveformEClass, this.getLocalScaComponent(), "launch", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "id", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theScaPackage.getDataTypeArray(), "execParams", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theSpdPackage.getURI(), "spdURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "implID", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "mode", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getCoreException());

		op = addEOperation(localScaWaveformEClass, null, "setLocalApp", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, this.getApplicationExtOperations(), "app", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theScaPackage.getPOA(), "poa", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getServantNotActive());
		addEException(op, this.getWrongPolicy());

		initEClass(localScaComponentEClass, LocalScaComponent.class, "LocalScaComponent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(localScaDeviceManagerEClass, LocalScaDeviceManager.class, "LocalScaDeviceManager", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getLocalScaDeviceManager_NamingContext(), this.getNotifyingNamingContext(), null, "namingContext", null, 1, 1, LocalScaDeviceManager.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getLocalScaDeviceManager_LocalDeviceManager(), theCfPackage.getDeviceManagerOperations(), null, "localDeviceManager", null, 1, 1, LocalScaDeviceManager.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(localScaDeviceManagerEClass, null, "setLocalDeviceManager", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theCfPackage.getDeviceManagerOperations(), "impl", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theScaPackage.getPOA(), "poa", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, this.getServantNotActive());
		addEException(op, this.getWrongPolicy());

		initEClass(localScaExecutableDeviceEClass, LocalScaExecutableDevice.class, "LocalScaExecutableDevice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(localScaLoadableDeviceEClass, LocalScaLoadableDevice.class, "LocalScaLoadableDevice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(localScaDeviceEClass, LocalScaDevice.class, "LocalScaDevice", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(localScaServiceEClass, LocalScaService.class, "LocalScaService", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(applicationExtOperationsEClass, ApplicationExtOperations.class, "ApplicationExtOperations", IS_ABSTRACT, IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);

		op = addEOperation(applicationExtOperationsEClass, theCfPackage.getResource(), "launch", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, ecorePackage.getEString(), "id", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theScaPackage.getDataTypeArray(), "execParams", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "spdURI", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "implID", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "mode", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theCfPackage.getExecuteFail());

		op = addEOperation(applicationExtOperationsEClass, theCfPackage.getResource(), "reset", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "compInstId", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEException(op, theCfPackage.getReleaseError());
		addEException(op, theCfPackage.getExecuteFail());

		// Initialize data types
		initEDataType(nameEDataType, Name.class, "Name", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(namingContextEDataType, NamingContext.class, "NamingContext", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(iLaunchEDataType, ILaunch.class, "ILaunch", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(nameComponentArrayEDataType, NameComponent[].class, "NameComponentArray", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(servantNotActiveEDataType, ServantNotActive.class, "ServantNotActive", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(wrongPolicyEDataType, WrongPolicy.class, "WrongPolicy", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
		initEDataType(coreExceptionEDataType, CoreException.class, "CoreException", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //ScaDebugPackageImpl
