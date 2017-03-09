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

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.impl.commands.LocalMergeServicesCommand;
import gov.redhawk.ide.debug.internal.cf.impl.DeviceManagerImpl;
import gov.redhawk.model.sca.RefreshDepth;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.impl.ScaDeviceManagerImpl;
import gov.redhawk.sca.util.OrbSession;
import gov.redhawk.sca.util.SilentJob;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.jdt.annotation.NonNull;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.DataType;
import CF.Device;
import CF.DeviceManager;
import CF.DeviceManagerHelper;
import CF.DeviceManagerOperations;
import CF.DeviceManagerPOATie;
import CF.ExecutableDeviceHelper;
import CF.LoadableDeviceHelper;
import CF.Resource;
import gov.redhawk.ide.debug.LocalAbstractComponent;
import CF.DeviceManagerPackage.ServiceType;
import CF.ExecutableDevicePackage.ExecuteFail;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca Device Manager</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaDeviceManagerImpl#getLocalDeviceManager <em>Local Device Manager</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LocalScaDeviceManagerImpl extends ScaDeviceManagerImpl implements LocalScaDeviceManager {
	/**
	 * The default value of the '{@link #getLaunch() <em>Launch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLaunch()
	 * @generated
	 * @ordered
	 */
	protected static final ILaunch LAUNCH_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getLaunch() <em>Launch</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLaunch()
	 * @generated
	 * @ordered
	 */
	protected ILaunch launch = LAUNCH_EDEFAULT;
	/**
	 * The default value of the '{@link #getMode() <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMode()
	 * @generated
	 * @ordered
	 */
	protected static final String MODE_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getMode() <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMode()
	 * @generated
	 * @ordered
	 */
	protected String mode = MODE_EDEFAULT;
	/**
	 * The cached value of the '{@link #getNamingContext() <em>Naming Context</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamingContext()
	 * @generated
	 * @ordered
	 */
	protected NotifyingNamingContext namingContext;
	/**
	 * The default value of the '{@link #getLocalDeviceManager() <em>Local Device Manager</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalDeviceManager()
	 * @generated
	 * @ordered
	 */
	protected static final DeviceManagerOperations LOCAL_DEVICE_MANAGER_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getLocalDeviceManager() <em>Local Device Manager</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocalDeviceManager()
	 * @generated
	 * @ordered
	 */
	protected DeviceManagerOperations localDeviceManager = LOCAL_DEVICE_MANAGER_EDEFAULT;

	private OrbSession session = OrbSession.createSession();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaDeviceManagerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ScaDebugPackage.Literals.LOCAL_SCA_DEVICE_MANAGER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setLaunch(ILaunch newLaunch) {
		ILaunch oldLaunch = launch;
		launch = newLaunch;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH, oldLaunch, launch));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getMode() {
		return mode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMode(String newMode) {
		String oldMode = mode;
		mode = newMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE, oldMode, mode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotifyingNamingContext getNamingContext() {
		if (namingContext != null && namingContext.eIsProxy()) {
			InternalEObject oldNamingContext = (InternalEObject) namingContext;
			namingContext = (NotifyingNamingContext) eResolveProxy(oldNamingContext);
			if (namingContext != oldNamingContext) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT, oldNamingContext,
						namingContext));
			}
		}
		return namingContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContext basicGetNamingContext() {
		return namingContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setNamingContext(NotifyingNamingContext newNamingContext) {
		NotifyingNamingContext oldNamingContext = namingContext;
		namingContext = newNamingContext;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT, oldNamingContext, namingContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public DeviceManagerOperations getLocalDeviceManager() {
		return localDeviceManager;
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocalDeviceManagerGen(DeviceManagerOperations newLocalDeviceManager) {
		DeviceManagerOperations oldLocalDeviceManager = localDeviceManager;
		localDeviceManager = newLocalDeviceManager;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER, oldLocalDeviceManager,
				localDeviceManager));
	}

	private final Job refreshJob = new SilentJob("Refresh") {
		{
			setSystem(true);
			setPriority(Job.SHORT);
		}

		@Override
		protected IStatus runSilent(final IProgressMonitor monitor) {
			fetchIdentifier(null);
			fetchLabel(null);
			try {
				refresh(monitor, RefreshDepth.FULL);
			} catch (final InterruptedException e) {
				// PASS
			}
			return Status.OK_STATUS;
		}

	};

	/**
	 * <!-- begin-user-doc -->
	 * @since 4.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public void setLocalDeviceManager(final DeviceManagerOperations impl) {
		// END GENERATED CODE
		setLocalDeviceManagerGen(impl);
		DeviceManager ref = null;
		if (impl != null) {
			try {
				ref = DeviceManagerHelper.narrow(session.getPOA().servant_to_reference(new DeviceManagerPOATie(impl)));
			} catch (ServantNotActive e) {
				ScaDebugPlugin.logError("Failed to setup Device manager servant.", e);
			} catch (WrongPolicy e) {
				ScaDebugPlugin.logError("Failed to setup Device manager servant.", e);
			} catch (CoreException e) {
				ScaDebugPlugin.logError("Failed to setup Device manager servant.", e);
			}
		}

		setCorbaObj(ref);
		setObj(ref);
		if (ref != null && impl != null) {
			setIdentifier(impl.identifier());
			setLabel(impl.label());
			this.refreshJob.schedule();
		} else {
			super.unsetProfileObj();
			super.unsetProfileURI();
			super.unsetProfile();
		}

		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * @since 8.2
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public LocalAbstractComponent launch(String id, DataType[] initConfiguration, URI spdURI, String implID, String mode) throws CoreException {
		// END GENERATED CODE
		return ((DeviceManagerImpl) getLocalDeviceManager()).launch(null, id, initConfiguration, spdURI, implID, mode);
		// BEGIN GENERATED CODE
	}

	@Override
	public void unsetProfile() {

	}

	@Override
	public void unsetProfileURI() {

	}

	@Override
	public void unsetProfileObj() {

	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH:
			return getLaunch();
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE:
			return getMode();
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT:
			if (resolve)
				return getNamingContext();
			return basicGetNamingContext();
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER:
			return getLocalDeviceManager();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH:
			setLaunch((ILaunch) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE:
			setMode((String) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT:
			setNamingContext((NotifyingNamingContext) newValue);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER:
			setLocalDeviceManager((DeviceManagerOperations) newValue);
			return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH:
			setLaunch(LAUNCH_EDEFAULT);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE:
			setMode(MODE_EDEFAULT);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT:
			setNamingContext((NotifyingNamingContext) null);
			return;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER:
			setLocalDeviceManager(LOCAL_DEVICE_MANAGER_EDEFAULT);
			return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH:
			return LAUNCH_EDEFAULT == null ? launch != null : !LAUNCH_EDEFAULT.equals(launch);
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE:
			return MODE_EDEFAULT == null ? mode != null : !MODE_EDEFAULT.equals(mode);
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__NAMING_CONTEXT:
			return namingContext != null;
		case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LOCAL_DEVICE_MANAGER:
			return LOCAL_DEVICE_MANAGER_EDEFAULT == null ? localDeviceManager != null : !LOCAL_DEVICE_MANAGER_EDEFAULT.equals(localDeviceManager);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class< ? > baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (derivedFeatureID) {
			case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH:
				return ScaDebugPackage.LOCAL_LAUNCH__LAUNCH;
			case ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE:
				return ScaDebugPackage.LOCAL_LAUNCH__MODE;
			default:
				return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class< ? > baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (baseFeatureID) {
			case ScaDebugPackage.LOCAL_LAUNCH__LAUNCH:
				return ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__LAUNCH;
			case ScaDebugPackage.LOCAL_LAUNCH__MODE:
				return ScaDebugPackage.LOCAL_SCA_DEVICE_MANAGER__MODE;
			default:
				return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy())
			return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (launch: ");
		result.append(launch);
		result.append(", mode: ");
		result.append(mode);
		result.append(", localDeviceManager: ");
		result.append(localDeviceManager);
		result.append(')');
		return result.toString();
	}

	@Override
	public void shutdown() {
		ScaModelCommand.execute(this, new ScaModelCommand() {
			@Override
			public void execute() {
				getDevices().clear();
				getServices().clear();
			}
		});
	}

	@Override
	public void dispose() {
		shutdown();
		super.dispose();
		if (session != null) {
			session.dispose();
			session = null;
		}
	}

	@Override
	protected EClass getType(Device dev) {
		EClass type = ScaDebugPackage.Literals.LOCAL_SCA_DEVICE;
		if (dev._is_a(ExecutableDeviceHelper.id())) {
			type = ScaDebugPackage.Literals.LOCAL_SCA_EXECUTABLE_DEVICE;
		} else if (dev._is_a(LoadableDeviceHelper.id())) {
			type = ScaDebugPackage.Literals.LOCAL_SCA_LOADABLE_DEVICE;
		}
		return type;
	}

	@Override
	protected ScaDevice< ? > createType(EClass type) {
		return (ScaDevice< ? >) ScaDebugFactory.eINSTANCE.create(type);
	}

	@Override
	protected Command createMergeServicesCommand(Map<String, ServiceType> newServices) {
		return new LocalMergeServicesCommand(this, newServices);
	}

	/**
	 * @since 6.0
	 * @deprecated Use {@link #launch(String, DataType[], URI, String, String)}.
	 */
	@Deprecated
	public Resource launch(final String compId, final DataType[] initConfiguration, @NonNull final String spdURI, final String implId, final String mode)
		throws ExecuteFail {
		return ((DeviceManagerImpl) getLocalDeviceManager()).launch(compId, initConfiguration, spdURI, implId, mode);
	}

} // LocalScaDeviceManagerImpl
