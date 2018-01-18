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
package gov.redhawk.ide.sdr.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import gov.redhawk.ide.sdr.WaveformsContainer;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SdrFactoryImpl extends EFactoryImpl implements SdrFactory {

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static SdrFactory init() {
		try {
			SdrFactory theSdrFactory = (SdrFactory) EPackage.Registry.INSTANCE.getEFactory(SdrPackage.eNS_URI);
			if (theSdrFactory != null) {
				return theSdrFactory;
			}
		} catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new SdrFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
		case SdrPackage.SDR_ROOT:
			return createSdrRoot();
		case SdrPackage.COMPONENTS_CONTAINER:
			return createComponentsContainer();
		case SdrPackage.WAVEFORMS_CONTAINER:
			return createWaveformsContainer();
		case SdrPackage.DEVICES_CONTAINER:
			return createDevicesContainer();
		case SdrPackage.SERVICES_CONTAINER:
			return createServicesContainer();
		case SdrPackage.SHARED_LIBRARIES_CONTAINER:
			return createSharedLibrariesContainer();
		case SdrPackage.NODES_CONTAINER:
			return createNodesContainer();
		default:
			throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
		case SdrPackage.LOAD_STATE:
			return createLoadStateFromString(eDataType, initialValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
		case SdrPackage.LOAD_STATE:
			return convertLoadStateToString(eDataType, instanceValue);
		default:
			throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SdrRoot createSdrRoot() {
		SdrRootImpl sdrRoot = new SdrRootImpl();
		return sdrRoot;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ComponentsContainer createComponentsContainer() {
		ComponentsContainerImpl componentsContainer = new ComponentsContainerImpl();
		return componentsContainer;
	}

	// END GENERATED CODE

	@Override
	public ComponentsContainer createComponentsContainer(String name) {
		ComponentsContainer container = createComponentsContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public WaveformsContainer createWaveformsContainer() {
		WaveformsContainerImpl waveformsContainer = new WaveformsContainerImpl();
		return waveformsContainer;
	}

	// END GENERATED CODE

	@Override
	public WaveformsContainer createWaveformsContainer(String name) {
		WaveformsContainer container = createWaveformsContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DevicesContainer createDevicesContainer() {
		DevicesContainerImpl devicesContainer = new DevicesContainerImpl();
		return devicesContainer;
	}

	// END GENERATED CODE

	@Override
	public DevicesContainer createDevicesContainer(String name) {
		DevicesContainer container = createDevicesContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ServicesContainer createServicesContainer() {
		ServicesContainerImpl servicesContainer = new ServicesContainerImpl();
		return servicesContainer;
	}

	// END GENERATED CODE

	@Override
	public ServicesContainer createServicesContainer(String name) {
		ServicesContainer container = createServicesContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SharedLibrariesContainer createSharedLibrariesContainer() {
		SharedLibrariesContainerImpl sharedLibrariesContainer = new SharedLibrariesContainerImpl();
		return sharedLibrariesContainer;
	}

	// END GENERATED CODE

	@Override
	public SharedLibrariesContainer createSharedLibrariesContainer(String name) {
		SharedLibrariesContainer container = createSharedLibrariesContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NodesContainer createNodesContainer() {
		NodesContainerImpl nodesContainer = new NodesContainerImpl();
		return nodesContainer;
	}

	// END GENERATED CODE

	@Override
	public NodesContainer createNodesContainer(String name) {
		NodesContainer container = createNodesContainer();
		container.setName(name);
		return container;
	}

	// BEGIN GENERATED CODE

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LoadState createLoadStateFromString(EDataType eDataType, String initialValue) {
		LoadState result = LoadState.get(initialValue);
		if (result == null)
			throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertLoadStateToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public SdrPackage getSdrPackage() {
		return (SdrPackage) getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static SdrPackage getPackage() {
		return SdrPackage.eINSTANCE;
	}

} //SdrFactoryImpl
