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
package gov.redhawk.ide.debug.util;

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
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.CorbaObjWrapper;
import gov.redhawk.model.sca.DataProviderObject;
import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.model.sca.IRefreshable;
import gov.redhawk.model.sca.IStatusProvider;
import gov.redhawk.model.sca.ProfileObjectWrapper;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaExecutableDevice;
import gov.redhawk.model.sca.ScaFileManager;
import gov.redhawk.model.sca.ScaFileStore;
import gov.redhawk.model.sca.ScaFileSystem;
import gov.redhawk.model.sca.ScaLoadableDevice;
import gov.redhawk.model.sca.ScaPortContainer;
import gov.redhawk.model.sca.ScaPropertyContainer;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.ScaWaveform;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.jacorb.naming.Name;
import org.omg.CORBA.portable.IDLEntity;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExtOperations;

import CF.ApplicationOperations;
import CF.Device;
import CF.DeviceManagerOperations;
import CF.DeviceOperations;
import CF.ExecutableDeviceOperations;
import CF.FileManagerOperations;
import CF.FileSystem;
import CF.FileSystemOperations;
import CF.LifeCycleOperations;
import CF.LoadableDevice;
import CF.LoadableDeviceOperations;
import CF.PortSupplierOperations;
import CF.PropertySetOperations;
import CF.Resource;
import CF.ResourceOperations;
import CF.TestableObjectOperations;
import ExtendedCF.ApplicationExtOperations;
import ExtendedCF.Sandbox;
import ExtendedCF.SandboxOperations;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.debug.ScaDebugPackage
 * @generated
 */
public class ScaDebugSwitch< T1 > {

	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ScaDebugPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaDebugSwitch() {
		if (modelPackage == null) {
			modelPackage = ScaDebugPackage.eINSTANCE;
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	public T1 doSwitch(EObject theEObject) {
		return doSwitch(theEObject.eClass(), theEObject);
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T1 doSwitch(EClass theEClass, EObject theEObject) {
		if (theEClass.eContainer() == modelPackage) {
			return doSwitch(theEClass.getClassifierID(), theEObject);
		}
		else {
			List<EClass> eSuperTypes = theEClass.getESuperTypes();
			return
				eSuperTypes.isEmpty() ?
					defaultCase(theEObject) :
					doSwitch(eSuperTypes.get(0), theEObject);
		}
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	protected T1 doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case ScaDebugPackage.LOCAL_SCA: {
				LocalSca localSca = (LocalSca)theEObject;
				T1 result = caseLocalSca(localSca);
				if (result == null) result = caseCorbaObjWrapper(localSca);
				if (result == null) result = caseDataProviderObject(localSca);
				if (result == null) result = caseIStatusProvider(localSca);
				if (result == null) result = caseIDisposable(localSca);
				if (result == null) result = caseIRefreshable(localSca);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT: {
				NotifyingNamingContext notifyingNamingContext = (NotifyingNamingContext)theEObject;
				T1 result = caseNotifyingNamingContext(notifyingNamingContext);
				if (result == null) result = caseNamingContextExtOperations(notifyingNamingContext);
				if (result == null) result = caseIDisposable(notifyingNamingContext);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.NAME_TO_OBJECT_ENTRY: {
				@SuppressWarnings("unchecked") Entry<Name, org.omg.CORBA.Object> nameToObjectEntry = (Entry<Name, org.omg.CORBA.Object>)theEObject;
				T1 result = caseNameToObjectEntry(nameToObjectEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.NAME_TO_NAMING_CONTEXT_ENTRY: {
				@SuppressWarnings("unchecked") Entry<Name, NamingContext> nameToNamingContextEntry = (Entry<Name, NamingContext>)theEObject;
				T1 result = caseNameToNamingContextEntry(nameToNamingContextEntry);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_FILE_MANAGER: {
				LocalFileManager localFileManager = (LocalFileManager)theEObject;
				T1 result = caseLocalFileManager(localFileManager);
				if (result == null) result = caseScaFileManager(localFileManager);
				if (result == null) result = caseScaFileSystem(localFileManager);
				if (result == null) result = caseFileManagerOperations(localFileManager);
				if (result == null) result = caseCorbaObjWrapper(localFileManager);
				if (result == null) result = caseFileSystemOperations(localFileManager);
				if (result == null) result = caseScaFileStore(localFileManager);
				if (result == null) result = caseDataProviderObject(localFileManager);
				if (result == null) result = caseIStatusProvider(localFileManager);
				if (result == null) result = caseIDisposable(localFileManager);
				if (result == null) result = caseIRefreshable(localFileManager);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_LAUNCH: {
				LocalLaunch localLaunch = (LocalLaunch)theEObject;
				T1 result = caseLocalLaunch(localLaunch);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_ABSTRACT_COMPONENT: {
				LocalAbstractComponent localAbstractComponent = (LocalAbstractComponent)theEObject;
				T1 result = caseLocalAbstractComponent(localAbstractComponent);
				if (result == null) result = caseLocalLaunch(localAbstractComponent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_WAVEFORM: {
				LocalScaWaveform localScaWaveform = (LocalScaWaveform)theEObject;
				T1 result = caseLocalScaWaveform(localScaWaveform);
				if (result == null) result = caseScaWaveform(localScaWaveform);
				if (result == null) result = caseLocalLaunch(localScaWaveform);
				if (result == null) result = caseApplicationExtOperations(localScaWaveform);
				if (result == null) result = caseScaPropertyContainer(localScaWaveform);
				if (result == null) result = caseApplicationOperations(localScaWaveform);
				if (result == null) result = caseScaPortContainer(localScaWaveform);
				if (result == null) result = caseCorbaObjWrapper(localScaWaveform);
				if (result == null) result = caseProfileObjectWrapper(localScaWaveform);
				if (result == null) result = caseResourceOperations(localScaWaveform);
				if (result == null) result = caseDataProviderObject(localScaWaveform);
				if (result == null) result = casePropertySetOperations(localScaWaveform);
				if (result == null) result = caseLifeCycleOperations(localScaWaveform);
				if (result == null) result = caseTestableObjectOperations(localScaWaveform);
				if (result == null) result = casePortSupplierOperations(localScaWaveform);
				if (result == null) result = caseIStatusProvider(localScaWaveform);
				if (result == null) result = caseIDisposable(localScaWaveform);
				if (result == null) result = caseIRefreshable(localScaWaveform);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_COMPONENT: {
				LocalScaComponent localScaComponent = (LocalScaComponent)theEObject;
				T1 result = caseLocalScaComponent(localScaComponent);
				if (result == null) result = caseScaComponent(localScaComponent);
				if (result == null) result = caseLocalAbstractComponent(localScaComponent);
				if (result == null) result = caseScaAbstractComponent(localScaComponent);
				if (result == null) result = caseLocalLaunch(localScaComponent);
				if (result == null) result = caseScaPropertyContainer(localScaComponent);
				if (result == null) result = caseResourceOperations(localScaComponent);
				if (result == null) result = caseScaPortContainer(localScaComponent);
				if (result == null) result = caseCorbaObjWrapper(localScaComponent);
				if (result == null) result = caseProfileObjectWrapper(localScaComponent);
				if (result == null) result = casePropertySetOperations(localScaComponent);
				if (result == null) result = caseLifeCycleOperations(localScaComponent);
				if (result == null) result = caseTestableObjectOperations(localScaComponent);
				if (result == null) result = casePortSupplierOperations(localScaComponent);
				if (result == null) result = caseDataProviderObject(localScaComponent);
				if (result == null) result = caseIStatusProvider(localScaComponent);
				if (result == null) result = caseIDisposable(localScaComponent);
				if (result == null) result = caseIRefreshable(localScaComponent);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER: {
				LocalScaDeviceManager localScaDeviceManager = (LocalScaDeviceManager)theEObject;
				T1 result = caseLocalScaDeviceManager(localScaDeviceManager);
				if (result == null) result = caseScaDeviceManager(localScaDeviceManager);
				if (result == null) result = caseLocalLaunch(localScaDeviceManager);
				if (result == null) result = caseScaPropertyContainer(localScaDeviceManager);
				if (result == null) result = caseDeviceManagerOperations(localScaDeviceManager);
				if (result == null) result = caseScaPortContainer(localScaDeviceManager);
				if (result == null) result = caseCorbaObjWrapper(localScaDeviceManager);
				if (result == null) result = caseProfileObjectWrapper(localScaDeviceManager);
				if (result == null) result = casePropertySetOperations(localScaDeviceManager);
				if (result == null) result = casePortSupplierOperations(localScaDeviceManager);
				if (result == null) result = caseDataProviderObject(localScaDeviceManager);
				if (result == null) result = caseIStatusProvider(localScaDeviceManager);
				if (result == null) result = caseIDisposable(localScaDeviceManager);
				if (result == null) result = caseIRefreshable(localScaDeviceManager);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE: {
				LocalScaExecutableDevice localScaExecutableDevice = (LocalScaExecutableDevice)theEObject;
				T1 result = caseLocalScaExecutableDevice(localScaExecutableDevice);
				if (result == null) result = caseScaExecutableDevice(localScaExecutableDevice);
				if (result == null) result = caseLocalAbstractComponent(localScaExecutableDevice);
				if (result == null) result = caseScaLoadableDevice(localScaExecutableDevice);
				if (result == null) result = caseExecutableDeviceOperations(localScaExecutableDevice);
				if (result == null) result = caseLocalLaunch(localScaExecutableDevice);
				if (result == null) result = caseScaDevice(localScaExecutableDevice);
				if (result == null) result = caseLoadableDeviceOperations(localScaExecutableDevice);
				if (result == null) result = caseScaAbstractComponent(localScaExecutableDevice);
				if (result == null) result = caseDeviceOperations(localScaExecutableDevice);
				if (result == null) result = caseScaPropertyContainer(localScaExecutableDevice);
				if (result == null) result = caseResourceOperations(localScaExecutableDevice);
				if (result == null) result = caseScaPortContainer(localScaExecutableDevice);
				if (result == null) result = caseCorbaObjWrapper(localScaExecutableDevice);
				if (result == null) result = caseProfileObjectWrapper(localScaExecutableDevice);
				if (result == null) result = casePropertySetOperations(localScaExecutableDevice);
				if (result == null) result = caseLifeCycleOperations(localScaExecutableDevice);
				if (result == null) result = caseTestableObjectOperations(localScaExecutableDevice);
				if (result == null) result = casePortSupplierOperations(localScaExecutableDevice);
				if (result == null) result = caseDataProviderObject(localScaExecutableDevice);
				if (result == null) result = caseIStatusProvider(localScaExecutableDevice);
				if (result == null) result = caseIDisposable(localScaExecutableDevice);
				if (result == null) result = caseIRefreshable(localScaExecutableDevice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_LOADABLE_DEVICE: {
				LocalScaLoadableDevice localScaLoadableDevice = (LocalScaLoadableDevice)theEObject;
				T1 result = caseLocalScaLoadableDevice(localScaLoadableDevice);
				if (result == null) result = caseScaLoadableDevice(localScaLoadableDevice);
				if (result == null) result = caseLocalAbstractComponent(localScaLoadableDevice);
				if (result == null) result = caseScaDevice(localScaLoadableDevice);
				if (result == null) result = caseLoadableDeviceOperations(localScaLoadableDevice);
				if (result == null) result = caseLocalLaunch(localScaLoadableDevice);
				if (result == null) result = caseScaAbstractComponent(localScaLoadableDevice);
				if (result == null) result = caseDeviceOperations(localScaLoadableDevice);
				if (result == null) result = caseScaPropertyContainer(localScaLoadableDevice);
				if (result == null) result = caseResourceOperations(localScaLoadableDevice);
				if (result == null) result = caseScaPortContainer(localScaLoadableDevice);
				if (result == null) result = caseCorbaObjWrapper(localScaLoadableDevice);
				if (result == null) result = caseProfileObjectWrapper(localScaLoadableDevice);
				if (result == null) result = casePropertySetOperations(localScaLoadableDevice);
				if (result == null) result = caseLifeCycleOperations(localScaLoadableDevice);
				if (result == null) result = caseTestableObjectOperations(localScaLoadableDevice);
				if (result == null) result = casePortSupplierOperations(localScaLoadableDevice);
				if (result == null) result = caseDataProviderObject(localScaLoadableDevice);
				if (result == null) result = caseIStatusProvider(localScaLoadableDevice);
				if (result == null) result = caseIDisposable(localScaLoadableDevice);
				if (result == null) result = caseIRefreshable(localScaLoadableDevice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_DEVICE: {
				LocalScaDevice localScaDevice = (LocalScaDevice)theEObject;
				T1 result = caseLocalScaDevice(localScaDevice);
				if (result == null) result = caseScaDevice(localScaDevice);
				if (result == null) result = caseLocalAbstractComponent(localScaDevice);
				if (result == null) result = caseScaAbstractComponent(localScaDevice);
				if (result == null) result = caseDeviceOperations(localScaDevice);
				if (result == null) result = caseLocalLaunch(localScaDevice);
				if (result == null) result = caseScaPropertyContainer(localScaDevice);
				if (result == null) result = caseResourceOperations(localScaDevice);
				if (result == null) result = caseScaPortContainer(localScaDevice);
				if (result == null) result = caseCorbaObjWrapper(localScaDevice);
				if (result == null) result = caseProfileObjectWrapper(localScaDevice);
				if (result == null) result = casePropertySetOperations(localScaDevice);
				if (result == null) result = caseLifeCycleOperations(localScaDevice);
				if (result == null) result = caseTestableObjectOperations(localScaDevice);
				if (result == null) result = casePortSupplierOperations(localScaDevice);
				if (result == null) result = caseDataProviderObject(localScaDevice);
				if (result == null) result = caseIStatusProvider(localScaDevice);
				if (result == null) result = caseIDisposable(localScaDevice);
				if (result == null) result = caseIRefreshable(localScaDevice);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ScaDebugPackage.LOCAL_SCA_SERVICE: {
				LocalScaService localScaService = (LocalScaService)theEObject;
				T1 result = caseLocalScaService(localScaService);
				if (result == null) result = caseScaService(localScaService);
				if (result == null) result = caseLocalAbstractComponent(localScaService);
				if (result == null) result = caseCorbaObjWrapper(localScaService);
				if (result == null) result = caseLocalLaunch(localScaService);
				if (result == null) result = caseDataProviderObject(localScaService);
				if (result == null) result = caseIStatusProvider(localScaService);
				if (result == null) result = caseIDisposable(localScaService);
				if (result == null) result = caseIRefreshable(localScaService);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalSca(LocalSca object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Naming Context Ext Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Naming Context Ext Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNamingContextExtOperations(NamingContextExtOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Notifying Naming Context</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Notifying Naming Context</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNotifyingNamingContext(NotifyingNamingContext object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Name To Object Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Name To Object Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNameToObjectEntry(Entry<Name, org.omg.CORBA.Object> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Name To Naming Context Entry</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Name To Naming Context Entry</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseNameToNamingContextEntry(Entry<Name, NamingContext> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local File Manager</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local File Manager</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalFileManager(LocalFileManager object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Launch</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Launch</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalLaunch(LocalLaunch object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Abstract Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Abstract Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalAbstractComponent(LocalAbstractComponent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Waveform</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Waveform</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaWaveform(LocalScaWaveform object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaComponent(LocalScaComponent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Device Manager</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Device Manager</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaDeviceManager(LocalScaDeviceManager object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Executable Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Executable Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaExecutableDevice(LocalScaExecutableDevice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Loadable Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Loadable Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaLoadableDevice(LocalScaLoadableDevice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaDevice(LocalScaDevice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Local Sca Service</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Local Sca Service</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLocalScaService(LocalScaService object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Application Ext Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Application Ext Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseApplicationExtOperations(ApplicationExtOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sandbox Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sandbox Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseSandboxOperations(SandboxOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sandbox</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sandbox</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseSandbox(Sandbox object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IDisposable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IDisposable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIDisposable(IDisposable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IStatus Provider</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IStatus Provider</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIStatusProvider(IStatusProvider object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IRefreshable</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IRefreshable</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIRefreshable(IRefreshable object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Data Provider Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Data Provider Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseDataProviderObject(DataProviderObject object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Corba Obj Wrapper</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Corba Obj Wrapper</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <T extends org.omg.CORBA.Object> T1 caseCorbaObjWrapper(CorbaObjWrapper<T> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File System Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File System Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseFileSystemOperations(FileSystemOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Store</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Store</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaFileStore(ScaFileStore object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File System</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File System</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <F extends FileSystem> T1 caseScaFileSystem(ScaFileSystem<F> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Manager Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Manager Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseFileManagerOperations(FileManagerOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>File Manager</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>File Manager</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaFileManager(ScaFileManager object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Profile Object Wrapper</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Profile Object Wrapper</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <O extends Object> T1 caseProfileObjectWrapper(ProfileObjectWrapper<O> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Set Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Set Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 casePropertySetOperations(PropertySetOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Property Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Property Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <P extends org.omg.CORBA.Object, E extends Object> T1 caseScaPropertyContainer(ScaPropertyContainer<P, E> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Life Cycle Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Life Cycle Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLifeCycleOperations(LifeCycleOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Testable Object Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Testable Object Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseTestableObjectOperations(TestableObjectOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Port Supplier Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Port Supplier Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 casePortSupplierOperations(PortSupplierOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Resource Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Resource Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseResourceOperations(ResourceOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Application Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Application Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseApplicationOperations(ApplicationOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Port Container</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Port Container</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaPortContainer(ScaPortContainer object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Waveform</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Waveform</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaWaveform(ScaWaveform object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Abstract Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Abstract Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <R extends Resource> T1 caseScaAbstractComponent(ScaAbstractComponent<R> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Component</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Component</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaComponent(ScaComponent object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Device Manager Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Device Manager Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseDeviceManagerOperations(DeviceManagerOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Device Manager</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Device Manager</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaDeviceManager(ScaDeviceManager object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Device Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Device Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseDeviceOperations(DeviceOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <D extends Device> T1 caseScaDevice(ScaDevice<D> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Loadable Device Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Loadable Device Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseLoadableDeviceOperations(LoadableDeviceOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Loadable Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Loadable Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public <L extends LoadableDevice> T1 caseScaLoadableDevice(ScaLoadableDevice<L> object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Executable Device Operations</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Executable Device Operations</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseExecutableDeviceOperations(ExecutableDeviceOperations object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Executable Device</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Executable Device</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaExecutableDevice(ScaExecutableDevice object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Service</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Service</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseScaService(ScaService object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Object</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Object</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseObject(org.omg.CORBA.Object object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>IDL Entity</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>IDL Entity</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T1 caseIDLEntity(IDLEntity object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	public T1 defaultCase(EObject object) {
		return null;
	}

} //ScaDebugSwitch
