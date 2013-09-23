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
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.LocalFileManager;
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
import java.util.Map.Entry;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.jacorb.naming.Name;
import org.omg.CosNaming.NamingContext;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ScaDebugFactoryImpl extends EFactoryImpl implements ScaDebugFactory {

	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ScaDebugFactory init() {
		try {
			ScaDebugFactory theScaDebugFactory = (ScaDebugFactory)EPackage.Registry.INSTANCE.getEFactory(ScaDebugPackage.eNS_URI);
			if (theScaDebugFactory != null) {
				return theScaDebugFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ScaDebugFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaDebugFactoryImpl() {
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
			case ScaDebugPackage.LOCAL_SCA: return createLocalSca();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT: return createNotifyingNamingContext();
			case ScaDebugPackage.NAME_TO_OBJECT_ENTRY: return (EObject)createNameToObjectEntry();
			case ScaDebugPackage.NAME_TO_NAMING_CONTEXT_ENTRY: return (EObject)createNameToNamingContextEntry();
			case ScaDebugPackage.LOCAL_FILE_MANAGER: return createLocalFileManager();
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM: return createLocalScaWaveform();
			case ScaDebugPackage.LOCAL_SCA_COMPONENT: return createLocalScaComponent();
			case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER: return createLocalScaDeviceManager();
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE: return createLocalScaExecutableDevice();
			case ScaDebugPackage.LOCAL_SCA_LOADABLE_DEVICE: return createLocalScaLoadableDevice();
			case ScaDebugPackage.LOCAL_SCA_DEVICE: return createLocalScaDevice();
			case ScaDebugPackage.LOCAL_SCA_SERVICE: return createLocalScaService();
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
			case ScaDebugPackage.NAME:
				return createNameFromString(eDataType, initialValue);
			case ScaDebugPackage.NAMING_CONTEXT:
				return createNamingContextFromString(eDataType, initialValue);
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
			case ScaDebugPackage.NAME:
				return convertNameToString(eDataType, instanceValue);
			case ScaDebugPackage.NAMING_CONTEXT:
				return convertNamingContextToString(eDataType, instanceValue);
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
	public LocalSca createLocalSca() {
		LocalScaImpl localSca = new LocalScaImpl();
		return localSca;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotifyingNamingContext createNotifyingNamingContext() {
		NotifyingNamingContextImpl notifyingNamingContext = new NotifyingNamingContextImpl();
		return notifyingNamingContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Entry<Name, org.omg.CORBA.Object> createNameToObjectEntry() {
		NameToObjectEntryImpl nameToObjectEntry = new NameToObjectEntryImpl();
		return nameToObjectEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Entry<Name, NamingContext> createNameToNamingContextEntry() {
		NameToNamingContextEntryImpl nameToNamingContextEntry = new NameToNamingContextEntryImpl();
		return nameToNamingContextEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalFileManager createLocalFileManager() {
		LocalFileManagerImpl localFileManager = new LocalFileManagerImpl();
		return localFileManager;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaWaveform createLocalScaWaveform() {
		LocalScaWaveformImpl localScaWaveform = new LocalScaWaveformImpl();
		return localScaWaveform;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaComponent createLocalScaComponent() {
		LocalScaComponentImpl localScaComponent = new LocalScaComponentImpl();
		return localScaComponent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaDeviceManager createLocalScaDeviceManager() {
		LocalScaDeviceManagerImpl localScaDeviceManager = new LocalScaDeviceManagerImpl();
		return localScaDeviceManager;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaExecutableDevice createLocalScaExecutableDevice() {
		LocalScaExecutableDeviceImpl localScaExecutableDevice = new LocalScaExecutableDeviceImpl();
		return localScaExecutableDevice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaLoadableDevice createLocalScaLoadableDevice() {
		LocalScaLoadableDeviceImpl localScaLoadableDevice = new LocalScaLoadableDeviceImpl();
		return localScaLoadableDevice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaDevice createLocalScaDevice() {
		LocalScaDeviceImpl localScaDevice = new LocalScaDeviceImpl();
		return localScaDevice;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public LocalScaService createLocalScaService() {
		LocalScaServiceImpl localScaService = new LocalScaServiceImpl();
		return localScaService;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Name createNameFromString(EDataType eDataType, String initialValue) {
		return (Name)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertNameToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NamingContext createNamingContextFromString(EDataType eDataType, String initialValue) {
		return (NamingContext)super.createFromString(eDataType, initialValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertNamingContextToString(EDataType eDataType, Object instanceValue) {
		return super.convertToString(eDataType, instanceValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ScaDebugPackage getScaDebugPackage() {
		return (ScaDebugPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ScaDebugPackage getPackage() {
		return ScaDebugPackage.eINSTANCE;
	}

} //ScaDebugFactoryImpl
