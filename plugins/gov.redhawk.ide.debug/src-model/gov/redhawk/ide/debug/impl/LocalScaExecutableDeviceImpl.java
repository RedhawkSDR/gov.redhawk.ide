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

import gov.redhawk.ide.debug.LocalAbstractComponent;
import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.LocalScaExecutableDevice;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.model.sca.impl.ScaExecutableDeviceImpl;

import java.util.Collection;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.InternalEList;

import CF.LifeCyclePackage.ReleaseError;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Local Sca Executable Device</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl#getLaunch <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl#getExecParams <em>Exec Params</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.LocalScaExecutableDeviceImpl#getImplementationID <em>Implementation ID</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class LocalScaExecutableDeviceImpl extends ScaExecutableDeviceImpl implements LocalScaExecutableDevice {
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
	 * The cached value of the '{@link #getExecParams() <em>Exec Params</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecParams()
	 * @generated
	 * @ordered
	 */
	protected EList<String> execParams;
	/**
	 * The default value of the '{@link #getImplementationID() <em>Implementation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementationID()
	 * @generated
	 * @ordered
	 */
	protected static final String IMPLEMENTATION_ID_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getImplementationID() <em>Implementation ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplementationID()
	 * @generated
	 * @ordered
	 */
	protected String implementationID = IMPLEMENTATION_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaExecutableDeviceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ScaDebugPackage.Literals.LOCAL_SCA_EXECUTABLE_DEVICE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ILaunch getLaunch() {
		return launch;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLaunch(ILaunch newLaunch) {
		ILaunch oldLaunch = launch;
		launch = newLaunch;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH, oldLaunch, launch));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMode(String newMode) {
		String oldMode = mode;
		mode = newMode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE, oldMode, mode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getExecParams() {
		if (execParams == null) {
			execParams = new EDataTypeUniqueEList.Unsettable<String>(String.class, this, ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS);
		}
		return execParams;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetExecParams() {
		if (execParams != null) ((InternalEList.Unsettable<?>)execParams).unset();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetExecParams() {
		return execParams != null && ((InternalEList.Unsettable<?>)execParams).isSet();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getImplementationID() {
		return implementationID;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImplementationID(String newImplementationID) {
		String oldImplementationID = implementationID;
		implementationID = newImplementationID;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID, oldImplementationID, implementationID));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH:
				return getLaunch();
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE:
				return getMode();
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS:
				return getExecParams();
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID:
				return getImplementationID();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH:
				setLaunch((ILaunch)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE:
				setMode((String)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS:
				getExecParams().clear();
				getExecParams().addAll((Collection<? extends String>)newValue);
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID:
				setImplementationID((String)newValue);
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
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH:
				setLaunch(LAUNCH_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE:
				setMode(MODE_EDEFAULT);
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS:
				unsetExecParams();
				return;
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID:
				setImplementationID(IMPLEMENTATION_ID_EDEFAULT);
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
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH:
				return LAUNCH_EDEFAULT == null ? launch != null : !LAUNCH_EDEFAULT.equals(launch);
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE:
				return MODE_EDEFAULT == null ? mode != null : !MODE_EDEFAULT.equals(mode);
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS:
				return isSetExecParams();
			case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID:
				return IMPLEMENTATION_ID_EDEFAULT == null ? implementationID != null : !IMPLEMENTATION_ID_EDEFAULT.equals(implementationID);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (derivedFeatureID) {
				case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH: return ScaDebugPackage.LOCAL_LAUNCH__LAUNCH;
				case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE: return ScaDebugPackage.LOCAL_LAUNCH__MODE;
				default: return -1;
			}
		}
		if (baseClass == LocalAbstractComponent.class) {
			switch (derivedFeatureID) {
				case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS: return ScaDebugPackage.LOCAL_ABSTRACT_COMPONENT__EXEC_PARAMS;
				case ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID: return ScaDebugPackage.LOCAL_ABSTRACT_COMPONENT__IMPLEMENTATION_ID;
				default: return -1;
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
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == LocalLaunch.class) {
			switch (baseFeatureID) {
				case ScaDebugPackage.LOCAL_LAUNCH__LAUNCH: return ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__LAUNCH;
				case ScaDebugPackage.LOCAL_LAUNCH__MODE: return ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__MODE;
				default: return -1;
			}
		}
		if (baseClass == LocalAbstractComponent.class) {
			switch (baseFeatureID) {
				case ScaDebugPackage.LOCAL_ABSTRACT_COMPONENT__EXEC_PARAMS: return ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__EXEC_PARAMS;
				case ScaDebugPackage.LOCAL_ABSTRACT_COMPONENT__IMPLEMENTATION_ID: return ScaDebugPackage.LOCAL_SCA_EXECUTABLE_DEVICE__IMPLEMENTATION_ID;
				default: return -1;
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
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (launch: ");
		result.append(launch);
		result.append(", mode: ");
		result.append(mode);
		result.append(", execParams: ");
		result.append(execParams);
		result.append(", implementationID: ");
		result.append(implementationID);
		result.append(')');
		return result.toString();
	}

	@Override
	public void releaseObject() throws ReleaseError {
		final String tmpName = getLabel();
	    super.releaseObject();
	    if (this.launch != null) {
	    	final Job terminateJob = new TerminateJob(this, tmpName);
			terminateJob.schedule(5000);
	    }
	}
	
	@Override
	public void dispose() {
		try {
	        releaseObject();
        } catch (final ReleaseError e) {
	        // PASS
        }
	    super.dispose();
	}

} //LocalScaExecutableDeviceImpl
