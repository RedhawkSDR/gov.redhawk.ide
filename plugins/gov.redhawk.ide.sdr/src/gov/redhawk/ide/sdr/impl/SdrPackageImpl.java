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
package gov.redhawk.ide.sdr.impl;

import gov.redhawk.eclipsecorba.library.LibraryPackage;
import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.PropertyRegistry;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.ide.sdr.WaveformsContainer;

import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dmd.DmdPackage;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SdrPackageImpl extends EPackageImpl implements SdrPackage {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass sdrRootEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass componentsContainerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass waveformsContainerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass devicesContainerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass servicesContainerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass nodesContainerEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass stringToAbstractPropertyEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass propertyRegistryEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass softPkgRegistryEClass = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum loadStateEEnum = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EDataType iStatusEDataType = null;

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
	 * @see gov.redhawk.ide.sdr.SdrPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SdrPackageImpl() {
		super(eNS_URI, SdrFactory.eINSTANCE);
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
	 * <p>This method is used to initialize {@link SdrPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SdrPackage init() {
		if (isInited) return (SdrPackage)EPackage.Registry.INSTANCE.getEPackage(SdrPackage.eNS_URI);

		// Obtain or create and register package
		SdrPackageImpl theSdrPackage = (SdrPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SdrPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SdrPackageImpl());

		isInited = true;

		// Initialize simple dependencies
		DcdPackage.eINSTANCE.eClass();
		DmdPackage.eINSTANCE.eClass();
		LibraryPackage.eINSTANCE.eClass();
		SadPackage.eINSTANCE.eClass();

		// Create package meta-data objects
		theSdrPackage.createPackageContents();

		// Initialize created meta-data
		theSdrPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSdrPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SdrPackage.eNS_URI, theSdrPackage);
		return theSdrPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSdrRoot() {
		return sdrRootEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSdrRoot_LoadStatus() {
		return (EAttribute)sdrRootEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSdrRoot_State() {
		return (EAttribute)sdrRootEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_ComponentsContainer() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_WaveformsContainer() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_DevicesContainer() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_ServicesContainer() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_NodesContainer() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_DomainConfiguration() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSdrRoot_IdlLibrary() {
		return (EReference)sdrRootEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSdrRoot_DevFileSystemRoot() {
		return (EAttribute)sdrRootEClass.getEStructuralFeatures().get(9);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSdrRoot_DomFileSystemRoot() {
		return (EAttribute)sdrRootEClass.getEStructuralFeatures().get(10);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getComponentsContainer() {
		return componentsContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getComponentsContainer_SdrRoot() {
		return (EReference)componentsContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getWaveformsContainer() {
		return waveformsContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaveformsContainer_SdrRoot() {
		return (EReference)waveformsContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getWaveformsContainer_Waveforms() {
		return (EReference)waveformsContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getDevicesContainer() {
		return devicesContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getDevicesContainer_SdrRoot() {
		return (EReference)devicesContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getServicesContainer() {
		return servicesContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getServicesContainer_SdrRoot() {
		return (EReference)servicesContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getNodesContainer() {
		return nodesContainerEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodesContainer_SdrRoot() {
		return (EReference)nodesContainerEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getNodesContainer_Nodes() {
		return (EReference)nodesContainerEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getStringToAbstractProperty() {
		return stringToAbstractPropertyEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getStringToAbstractProperty_Key() {
		return (EAttribute)stringToAbstractPropertyEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getStringToAbstractProperty_Value() {
		return (EReference)stringToAbstractPropertyEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getPropertyRegistry() {
		return propertyRegistryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getPropertyRegistry_Properties() {
		return (EReference)propertyRegistryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSoftPkgRegistry() {
		return softPkgRegistryEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSoftPkgRegistry_Components() {
		return (EReference)softPkgRegistryEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getLoadState() {
		return loadStateEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDataType getIStatus() {
		return iStatusEDataType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrFactory getSdrFactory() {
		return (SdrFactory)getEFactoryInstance();
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
		sdrRootEClass = createEClass(SDR_ROOT);
		createEAttribute(sdrRootEClass, SDR_ROOT__LOAD_STATUS);
		createEAttribute(sdrRootEClass, SDR_ROOT__STATE);
		createEReference(sdrRootEClass, SDR_ROOT__COMPONENTS_CONTAINER);
		createEReference(sdrRootEClass, SDR_ROOT__WAVEFORMS_CONTAINER);
		createEReference(sdrRootEClass, SDR_ROOT__DEVICES_CONTAINER);
		createEReference(sdrRootEClass, SDR_ROOT__SERVICES_CONTAINER);
		createEReference(sdrRootEClass, SDR_ROOT__NODES_CONTAINER);
		createEReference(sdrRootEClass, SDR_ROOT__DOMAIN_CONFIGURATION);
		createEReference(sdrRootEClass, SDR_ROOT__IDL_LIBRARY);
		createEAttribute(sdrRootEClass, SDR_ROOT__DEV_FILE_SYSTEM_ROOT);
		createEAttribute(sdrRootEClass, SDR_ROOT__DOM_FILE_SYSTEM_ROOT);

		componentsContainerEClass = createEClass(COMPONENTS_CONTAINER);
		createEReference(componentsContainerEClass, COMPONENTS_CONTAINER__SDR_ROOT);

		waveformsContainerEClass = createEClass(WAVEFORMS_CONTAINER);
		createEReference(waveformsContainerEClass, WAVEFORMS_CONTAINER__SDR_ROOT);
		createEReference(waveformsContainerEClass, WAVEFORMS_CONTAINER__WAVEFORMS);

		devicesContainerEClass = createEClass(DEVICES_CONTAINER);
		createEReference(devicesContainerEClass, DEVICES_CONTAINER__SDR_ROOT);

		servicesContainerEClass = createEClass(SERVICES_CONTAINER);
		createEReference(servicesContainerEClass, SERVICES_CONTAINER__SDR_ROOT);

		nodesContainerEClass = createEClass(NODES_CONTAINER);
		createEReference(nodesContainerEClass, NODES_CONTAINER__SDR_ROOT);
		createEReference(nodesContainerEClass, NODES_CONTAINER__NODES);

		stringToAbstractPropertyEClass = createEClass(STRING_TO_ABSTRACT_PROPERTY);
		createEAttribute(stringToAbstractPropertyEClass, STRING_TO_ABSTRACT_PROPERTY__KEY);
		createEReference(stringToAbstractPropertyEClass, STRING_TO_ABSTRACT_PROPERTY__VALUE);

		propertyRegistryEClass = createEClass(PROPERTY_REGISTRY);
		createEReference(propertyRegistryEClass, PROPERTY_REGISTRY__PROPERTIES);

		softPkgRegistryEClass = createEClass(SOFT_PKG_REGISTRY);
		createEReference(softPkgRegistryEClass, SOFT_PKG_REGISTRY__COMPONENTS);

		// Create enums
		loadStateEEnum = createEEnum(LOAD_STATE);

		// Create data types
		iStatusEDataType = createEDataType(ISTATUS);
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
		LibraryPackage theLibraryPackage = (LibraryPackage)EPackage.Registry.INSTANCE.getEPackage(LibraryPackage.eNS_URI);
		DmdPackage theDmdPackage = (DmdPackage)EPackage.Registry.INSTANCE.getEPackage(DmdPackage.eNS_URI);
		EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);
		SadPackage theSadPackage = (SadPackage)EPackage.Registry.INSTANCE.getEPackage(SadPackage.eNS_URI);
		DcdPackage theDcdPackage = (DcdPackage)EPackage.Registry.INSTANCE.getEPackage(DcdPackage.eNS_URI);
		PrfPackage thePrfPackage = (PrfPackage)EPackage.Registry.INSTANCE.getEPackage(PrfPackage.eNS_URI);
		SpdPackage theSpdPackage = (SpdPackage)EPackage.Registry.INSTANCE.getEPackage(SpdPackage.eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		componentsContainerEClass.getESuperTypes().add(this.getSoftPkgRegistry());
		devicesContainerEClass.getESuperTypes().add(this.getSoftPkgRegistry());
		servicesContainerEClass.getESuperTypes().add(this.getSoftPkgRegistry());
		softPkgRegistryEClass.getESuperTypes().add(this.getPropertyRegistry());

		// Initialize classes and features; add operations and parameters
		initEClass(sdrRootEClass, SdrRoot.class, "SdrRoot", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSdrRoot_LoadStatus(), theLibraryPackage.getIStatus(), "loadStatus", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSdrRoot_State(), this.getLoadState(), "state", "UNLOADED", 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_ComponentsContainer(), this.getComponentsContainer(), this.getComponentsContainer_SdrRoot(), "componentsContainer", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_WaveformsContainer(), this.getWaveformsContainer(), this.getWaveformsContainer_SdrRoot(), "waveformsContainer", null, 0, 1, SdrRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_DevicesContainer(), this.getDevicesContainer(), this.getDevicesContainer_SdrRoot(), "devicesContainer", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_ServicesContainer(), this.getServicesContainer(), this.getServicesContainer_SdrRoot(), "servicesContainer", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_NodesContainer(), this.getNodesContainer(), this.getNodesContainer_SdrRoot(), "nodesContainer", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_DomainConfiguration(), theDmdPackage.getDomainManagerConfiguration(), null, "domainConfiguration", null, 0, 1, SdrRoot.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEReference(getSdrRoot_IdlLibrary(), theLibraryPackage.getIdlLibrary(), null, "idlLibrary", null, 0, 1, SdrRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSdrRoot_DevFileSystemRoot(), theLibraryPackage.getURI(), "devFileSystemRoot", null, 0, 1, SdrRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSdrRoot_DomFileSystemRoot(), theLibraryPackage.getURI(), "domFileSystemRoot", null, 0, 1, SdrRoot.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		EOperation op = addEOperation(sdrRootEClass, null, "load", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theLibraryPackage.getIProgressMonitor(), "monitor", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(sdrRootEClass, null, "unload", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theLibraryPackage.getIProgressMonitor(), "monitor", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(sdrRootEClass, null, "reload", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theLibraryPackage.getIProgressMonitor(), "monitor", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(sdrRootEClass, null, "setSdrRoot", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theLibraryPackage.getURI(), "sdrRoot", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "domPath", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "devPath", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(sdrRootEClass, theEcorePackage.getEResource(), "getDevResource", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "path", 0, 1, IS_UNIQUE, IS_ORDERED);

		op = addEOperation(sdrRootEClass, theEcorePackage.getEResource(), "getDomResource", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, theEcorePackage.getEString(), "path", 0, 1, IS_UNIQUE, IS_ORDERED);

		initEClass(componentsContainerEClass, ComponentsContainer.class, "ComponentsContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getComponentsContainer_SdrRoot(), this.getSdrRoot(), this.getSdrRoot_ComponentsContainer(), "sdrRoot", null, 0, 1, ComponentsContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(waveformsContainerEClass, WaveformsContainer.class, "WaveformsContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getWaveformsContainer_SdrRoot(), this.getSdrRoot(), this.getSdrRoot_WaveformsContainer(), "sdrRoot", null, 0, 1, WaveformsContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getWaveformsContainer_Waveforms(), theSadPackage.getSoftwareAssembly(), null, "waveforms", null, 0, -1, WaveformsContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(devicesContainerEClass, DevicesContainer.class, "DevicesContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getDevicesContainer_SdrRoot(), this.getSdrRoot(), this.getSdrRoot_DevicesContainer(), "sdrRoot", null, 0, 1, DevicesContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(servicesContainerEClass, ServicesContainer.class, "ServicesContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getServicesContainer_SdrRoot(), this.getSdrRoot(), this.getSdrRoot_ServicesContainer(), "sdrRoot", null, 0, 1, ServicesContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(nodesContainerEClass, NodesContainer.class, "NodesContainer", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getNodesContainer_SdrRoot(), this.getSdrRoot(), this.getSdrRoot_NodesContainer(), "sdrRoot", null, 0, 1, NodesContainer.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getNodesContainer_Nodes(), theDcdPackage.getDeviceConfiguration(), null, "nodes", null, 0, -1, NodesContainer.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(stringToAbstractPropertyEClass, Map.Entry.class, "StringToAbstractProperty", !IS_ABSTRACT, !IS_INTERFACE, !IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getStringToAbstractProperty_Key(), thePrfPackage.getDceUUID(), "key", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getStringToAbstractProperty_Value(), thePrfPackage.getAbstractProperty(), null, "value", null, 0, 1, Map.Entry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(propertyRegistryEClass, PropertyRegistry.class, "PropertyRegistry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getPropertyRegistry_Properties(), this.getStringToAbstractProperty(), null, "properties", null, 0, -1, PropertyRegistry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(softPkgRegistryEClass, SoftPkgRegistry.class, "SoftPkgRegistry", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSoftPkgRegistry_Components(), theSpdPackage.getSoftPkg(), null, "components", null, 0, -1, SoftPkgRegistry.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		op = addEOperation(softPkgRegistryEClass, theSpdPackage.getSoftPkg(), "getSoftPkg", 0, 1, IS_UNIQUE, IS_ORDERED);
		addEParameter(op, thePrfPackage.getDceUUID(), "softPkgId", 0, 1, IS_UNIQUE, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(loadStateEEnum, LoadState.class, "LoadState");
		addEEnumLiteral(loadStateEEnum, LoadState.UNLOADED);
		addEEnumLiteral(loadStateEEnum, LoadState.LOADING);
		addEEnumLiteral(loadStateEEnum, LoadState.LOADED);

		// Initialize data types
		initEDataType(iStatusEDataType, IStatus.class, "IStatus", !IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

		// Create resource
		createResource(eNS_URI);
	}

} //SdrPackageImpl
