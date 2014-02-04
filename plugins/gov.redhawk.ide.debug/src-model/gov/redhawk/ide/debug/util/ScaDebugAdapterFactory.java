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
import java.util.Map.Entry;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
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
import ExtendedCF.Sandbox;
import ExtendedCF.SandboxOperations;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see gov.redhawk.ide.debug.ScaDebugPackage
 * @generated
 */
public class ScaDebugAdapterFactory extends AdapterFactoryImpl {

	/**
	 * The cached model package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ScaDebugPackage modelPackage;

	/**
	 * Creates an instance of the adapter factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaDebugAdapterFactory() {
		if (modelPackage == null) {
			modelPackage = ScaDebugPackage.eINSTANCE;
		}
	}

	/**
	 * Returns whether this factory is applicable for the type of the object.
	 * <!-- begin-user-doc -->
	 * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
	 * <!-- end-user-doc -->
	 * @return whether this factory is applicable for the type of the object.
	 * @generated
	 */
	@Override
	public boolean isFactoryForType(Object object) {
		if (object == modelPackage) {
			return true;
		}
		if (object instanceof EObject) {
			return ((EObject) object).eClass().getEPackage() == modelPackage;
		}
		return false;
	}

	/**
	 * The switch that delegates to the <code>createXXX</code> methods.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ScaDebugSwitch<Adapter> modelSwitch = new ScaDebugSwitch<Adapter>() {
		@Override
		public Adapter caseLocalSca(LocalSca object) {
			return createLocalScaAdapter();
		}

		@Override
		public Adapter caseNamingContextExtOperations(NamingContextExtOperations object) {
			return createNamingContextExtOperationsAdapter();
		}

		@Override
		public Adapter caseNotifyingNamingContext(NotifyingNamingContext object) {
			return createNotifyingNamingContextAdapter();
		}

		@Override
		public Adapter caseNameToObjectEntry(Entry<Name, org.omg.CORBA.Object> object) {
			return createNameToObjectEntryAdapter();
		}

		@Override
		public Adapter caseNameToNamingContextEntry(Entry<Name, NamingContext> object) {
			return createNameToNamingContextEntryAdapter();
		}

		@Override
		public Adapter caseLocalFileManager(LocalFileManager object) {
			return createLocalFileManagerAdapter();
		}

		@Override
		public Adapter caseLocalLaunch(LocalLaunch object) {
			return createLocalLaunchAdapter();
		}

		@Override
		public Adapter caseLocalAbstractComponent(LocalAbstractComponent object) {
			return createLocalAbstractComponentAdapter();
		}

		@Override
		public Adapter caseLocalScaWaveform(LocalScaWaveform object) {
			return createLocalScaWaveformAdapter();
		}

		@Override
		public Adapter caseLocalScaComponent(LocalScaComponent object) {
			return createLocalScaComponentAdapter();
		}

		@Override
		public Adapter caseLocalScaDeviceManager(LocalScaDeviceManager object) {
			return createLocalScaDeviceManagerAdapter();
		}

		@Override
		public Adapter caseLocalScaExecutableDevice(LocalScaExecutableDevice object) {
			return createLocalScaExecutableDeviceAdapter();
		}

		@Override
		public Adapter caseLocalScaLoadableDevice(LocalScaLoadableDevice object) {
			return createLocalScaLoadableDeviceAdapter();
		}

		@Override
		public Adapter caseLocalScaDevice(LocalScaDevice object) {
			return createLocalScaDeviceAdapter();
		}

		@Override
		public Adapter caseLocalScaService(LocalScaService object) {
			return createLocalScaServiceAdapter();
		}

		@Override
		public Adapter caseSandboxOperations(SandboxOperations object) {
			return createSandboxOperationsAdapter();
		}

		@Override
		public Adapter caseSandbox(Sandbox object) {
			return createSandboxAdapter();
		}

		@Override
		public Adapter caseIStatusProvider(IStatusProvider object) {
			return createIStatusProviderAdapter();
		}

		@Override
		public Adapter caseIDisposable(IDisposable object) {
			return createIDisposableAdapter();
		}

		@Override
		public Adapter caseIRefreshable(IRefreshable object) {
			return createIRefreshableAdapter();
		}

		@Override
		public Adapter caseDataProviderObject(DataProviderObject object) {
			return createDataProviderObjectAdapter();
		}

		@Override
		public < T extends org.omg.CORBA.Object > Adapter caseCorbaObjWrapper(CorbaObjWrapper<T> object) {
			return createCorbaObjWrapperAdapter();
		}

		@Override
		public Adapter caseFileSystemOperations(FileSystemOperations object) {
			return createFileSystemOperationsAdapter();
		}

		@Override
		public Adapter caseScaFileStore(ScaFileStore object) {
			return createScaFileStoreAdapter();
		}

		@Override
		public < F extends FileSystem > Adapter caseScaFileSystem(ScaFileSystem<F> object) {
			return createScaFileSystemAdapter();
		}

		@Override
		public Adapter caseFileManagerOperations(FileManagerOperations object) {
			return createFileManagerOperationsAdapter();
		}

		@Override
		public Adapter caseScaFileManager(ScaFileManager object) {
			return createScaFileManagerAdapter();
		}

		@Override
		public < O extends Object > Adapter caseProfileObjectWrapper(ProfileObjectWrapper<O> object) {
			return createProfileObjectWrapperAdapter();
		}

		@Override
		public Adapter casePropertySetOperations(PropertySetOperations object) {
			return createPropertySetOperationsAdapter();
		}

		@Override
		public < P extends org.omg.CORBA.Object, E extends Object > Adapter caseScaPropertyContainer(ScaPropertyContainer<P, E> object) {
			return createScaPropertyContainerAdapter();
		}

		@Override
		public Adapter caseLifeCycleOperations(LifeCycleOperations object) {
			return createLifeCycleOperationsAdapter();
		}

		@Override
		public Adapter caseTestableObjectOperations(TestableObjectOperations object) {
			return createTestableObjectOperationsAdapter();
		}

		@Override
		public Adapter casePortSupplierOperations(PortSupplierOperations object) {
			return createPortSupplierOperationsAdapter();
		}

		@Override
		public Adapter caseResourceOperations(ResourceOperations object) {
			return createResourceOperationsAdapter();
		}

		@Override
		public Adapter caseApplicationOperations(ApplicationOperations object) {
			return createApplicationOperationsAdapter();
		}

		@Override
		public Adapter caseScaPortContainer(ScaPortContainer object) {
			return createScaPortContainerAdapter();
		}

		@Override
		public Adapter caseScaWaveform(ScaWaveform object) {
			return createScaWaveformAdapter();
		}

		@Override
		public < R extends Resource > Adapter caseScaAbstractComponent(ScaAbstractComponent<R> object) {
			return createScaAbstractComponentAdapter();
		}

		@Override
		public Adapter caseScaComponent(ScaComponent object) {
			return createScaComponentAdapter();
		}

		@Override
		public Adapter caseDeviceManagerOperations(DeviceManagerOperations object) {
			return createDeviceManagerOperationsAdapter();
		}

		@Override
		public Adapter caseScaDeviceManager(ScaDeviceManager object) {
			return createScaDeviceManagerAdapter();
		}

		@Override
		public Adapter caseDeviceOperations(DeviceOperations object) {
			return createDeviceOperationsAdapter();
		}

		@Override
		public < D extends Device > Adapter caseScaDevice(ScaDevice<D> object) {
			return createScaDeviceAdapter();
		}

		@Override
		public Adapter caseLoadableDeviceOperations(LoadableDeviceOperations object) {
			return createLoadableDeviceOperationsAdapter();
		}

		@Override
		public < L extends LoadableDevice > Adapter caseScaLoadableDevice(ScaLoadableDevice<L> object) {
			return createScaLoadableDeviceAdapter();
		}

		@Override
		public Adapter caseExecutableDeviceOperations(ExecutableDeviceOperations object) {
			return createExecutableDeviceOperationsAdapter();
		}

		@Override
		public Adapter caseScaExecutableDevice(ScaExecutableDevice object) {
			return createScaExecutableDeviceAdapter();
		}

		@Override
		public Adapter caseScaService(ScaService object) {
			return createScaServiceAdapter();
		}

		@Override
		public Adapter caseObject(org.omg.CORBA.Object object) {
			return createObjectAdapter();
		}

		@Override
		public Adapter caseIDLEntity(IDLEntity object) {
			return createIDLEntityAdapter();
		}

		@Override
		public Adapter defaultCase(EObject object) {
			return createEObjectAdapter();
		}
	};

	/**
	 * Creates an adapter for the <code>target</code>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param target the object to adapt.
	 * @return the adapter for the <code>target</code>.
	 * @generated
	 */
	@Override
	public Adapter createAdapter(Notifier target) {
		return modelSwitch.doSwitch((EObject) target);
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalSca <em>Local Sca</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalSca
	 * @generated
	 */
	public Adapter createLocalScaAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.omg.CosNaming.NamingContextExtOperations <em>Naming Context Ext Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.omg.CosNaming.NamingContextExtOperations
	 * @generated
	 */
	public Adapter createNamingContextExtOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.NotifyingNamingContext <em>Notifying Naming Context</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext
	 * @generated
	 */
	public Adapter createNotifyingNamingContextAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Name To Object Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createNameToObjectEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link java.util.Map.Entry <em>Name To Naming Context Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see java.util.Map.Entry
	 * @generated
	 */
	public Adapter createNameToNamingContextEntryAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalFileManager <em>Local File Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalFileManager
	 * @generated
	 */
	public Adapter createLocalFileManagerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalLaunch <em>Local Launch</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalLaunch
	 * @generated
	 */
	public Adapter createLocalLaunchAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalAbstractComponent <em>Local Abstract Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalAbstractComponent
	 * @generated
	 */
	public Adapter createLocalAbstractComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaWaveform <em>Local Sca Waveform</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaWaveform
	 * @generated
	 */
	public Adapter createLocalScaWaveformAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaComponent <em>Local Sca Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaComponent
	 * @generated
	 */
	public Adapter createLocalScaComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaDeviceManager <em>Local Sca Device Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager
	 * @generated
	 */
	public Adapter createLocalScaDeviceManagerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaExecutableDevice <em>Local Sca Executable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaExecutableDevice
	 * @generated
	 */
	public Adapter createLocalScaExecutableDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaLoadableDevice <em>Local Sca Loadable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaLoadableDevice
	 * @generated
	 */
	public Adapter createLocalScaLoadableDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaDevice <em>Local Sca Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaDevice
	 * @generated
	 */
	public Adapter createLocalScaDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.ide.debug.LocalScaService <em>Local Sca Service</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.ide.debug.LocalScaService
	 * @generated
	 */
	public Adapter createLocalScaServiceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ExtendedCF.SandboxOperations <em>Sandbox Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ExtendedCF.SandboxOperations
	 * @generated
	 */
	public Adapter createSandboxOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link ExtendedCF.Sandbox <em>Sandbox</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see ExtendedCF.Sandbox
	 * @generated
	 */
	public Adapter createSandboxAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.IDisposable <em>IDisposable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.IDisposable
	 * @generated
	 */
	public Adapter createIDisposableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.IStatusProvider <em>IStatus Provider</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.IStatusProvider
	 * @generated
	 */
	public Adapter createIStatusProviderAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.IRefreshable <em>IRefreshable</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.IRefreshable
	 * @generated
	 */
	public Adapter createIRefreshableAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.DataProviderObject <em>Data Provider Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.DataProviderObject
	 * @generated
	 */
	public Adapter createDataProviderObjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.CorbaObjWrapper <em>Corba Obj Wrapper</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.CorbaObjWrapper
	 * @generated
	 */
	public Adapter createCorbaObjWrapperAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.FileSystemOperations <em>File System Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.FileSystemOperations
	 * @generated
	 */
	public Adapter createFileSystemOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaFileStore <em>File Store</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaFileStore
	 * @generated
	 */
	public Adapter createScaFileStoreAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaFileSystem <em>File System</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaFileSystem
	 * @generated
	 */
	public Adapter createScaFileSystemAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.FileManagerOperations <em>File Manager Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.FileManagerOperations
	 * @generated
	 */
	public Adapter createFileManagerOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaFileManager <em>File Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaFileManager
	 * @generated
	 */
	public Adapter createScaFileManagerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ProfileObjectWrapper <em>Profile Object Wrapper</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ProfileObjectWrapper
	 * @generated
	 */
	public Adapter createProfileObjectWrapperAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.PropertySetOperations <em>Property Set Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.PropertySetOperations
	 * @generated
	 */
	public Adapter createPropertySetOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaPropertyContainer <em>Property Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaPropertyContainer
	 * @generated
	 */
	public Adapter createScaPropertyContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.LifeCycleOperations <em>Life Cycle Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.LifeCycleOperations
	 * @generated
	 */
	public Adapter createLifeCycleOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.TestableObjectOperations <em>Testable Object Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.TestableObjectOperations
	 * @generated
	 */
	public Adapter createTestableObjectOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.PortSupplierOperations <em>Port Supplier Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.PortSupplierOperations
	 * @generated
	 */
	public Adapter createPortSupplierOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.ResourceOperations <em>Resource Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.ResourceOperations
	 * @generated
	 */
	public Adapter createResourceOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.ApplicationOperations <em>Application Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.ApplicationOperations
	 * @generated
	 */
	public Adapter createApplicationOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaPortContainer <em>Port Container</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaPortContainer
	 * @generated
	 */
	public Adapter createScaPortContainerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaWaveform <em>Waveform</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaWaveform
	 * @generated
	 */
	public Adapter createScaWaveformAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaAbstractComponent <em>Abstract Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaAbstractComponent
	 * @generated
	 */
	public Adapter createScaAbstractComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaComponent <em>Component</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaComponent
	 * @generated
	 */
	public Adapter createScaComponentAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.DeviceManagerOperations <em>Device Manager Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.DeviceManagerOperations
	 * @generated
	 */
	public Adapter createDeviceManagerOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaDeviceManager <em>Device Manager</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaDeviceManager
	 * @generated
	 */
	public Adapter createScaDeviceManagerAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.DeviceOperations <em>Device Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.DeviceOperations
	 * @generated
	 */
	public Adapter createDeviceOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaDevice <em>Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaDevice
	 * @generated
	 */
	public Adapter createScaDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.LoadableDeviceOperations <em>Loadable Device Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.LoadableDeviceOperations
	 * @generated
	 */
	public Adapter createLoadableDeviceOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaLoadableDevice <em>Loadable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaLoadableDevice
	 * @generated
	 */
	public Adapter createScaLoadableDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link CF.ExecutableDeviceOperations <em>Executable Device Operations</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see CF.ExecutableDeviceOperations
	 * @generated
	 */
	public Adapter createExecutableDeviceOperationsAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaExecutableDevice <em>Executable Device</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaExecutableDevice
	 * @generated
	 */
	public Adapter createScaExecutableDeviceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link gov.redhawk.model.sca.ScaService <em>Service</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see gov.redhawk.model.sca.ScaService
	 * @generated
	 */
	public Adapter createScaServiceAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.omg.CORBA.Object <em>Object</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.omg.CORBA.Object
	 * @generated
	 */
	public Adapter createObjectAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for an object of class '{@link org.omg.CORBA.portable.IDLEntity <em>IDL Entity</em>}'.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null so that we can easily ignore cases;
	 * it's useful to ignore a case when inheritance will catch all the cases anyway.
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @see org.omg.CORBA.portable.IDLEntity
	 * @generated
	 */
	public Adapter createIDLEntityAdapter() {
		return null;
	}

	/**
	 * Creates a new adapter for the default case.
	 * <!-- begin-user-doc -->
	 * This default implementation returns null.
	 * <!-- end-user-doc -->
	 * @return the new adapter.
	 * @generated
	 */
	public Adapter createEObjectAdapter() {
		return null;
	}

} //ScaDebugAdapterFactory
