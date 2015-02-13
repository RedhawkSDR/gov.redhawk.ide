/**
 */
package gov.redhawk.ide.graphiti.ext.impl;

import gov.redhawk.ide.graphiti.ext.PortUpdateStatus;
import gov.redhawk.ide.graphiti.ext.RHGxPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Port Update Status</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.PortUpdateStatusImpl#isPortsUpdatable <em>Ports Updatable</em>}</li>
 *   <li>{@link gov.redhawk.ide.graphiti.ext.impl.PortUpdateStatusImpl#getSettingObject <em>Setting Object</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PortUpdateStatusImpl extends MinimalEObjectImpl.Container implements PortUpdateStatus {
	/**
	 * The default value of the '{@link #isPortsUpdatable() <em>Ports Updatable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPortsUpdatable()
	 * @generated
	 * @ordered
	 */
	protected static final boolean PORTS_UPDATABLE_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isPortsUpdatable() <em>Ports Updatable</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isPortsUpdatable()
	 * @generated
	 * @ordered
	 */
	protected boolean portsUpdatable = PORTS_UPDATABLE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSettingObject() <em>Setting Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSettingObject()
	 * @generated
	 * @ordered
	 */
	protected static final Object SETTING_OBJECT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSettingObject() <em>Setting Object</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSettingObject()
	 * @generated
	 * @ordered
	 */
	protected Object settingObject = SETTING_OBJECT_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PortUpdateStatusImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RHGxPackage.Literals.PORT_UPDATE_STATUS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isPortsUpdatable() {
		return portsUpdatable;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPortsUpdatable(boolean newPortsUpdatable) {
		boolean oldPortsUpdatable = portsUpdatable;
		portsUpdatable = newPortsUpdatable;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.PORT_UPDATE_STATUS__PORTS_UPDATABLE, oldPortsUpdatable, portsUpdatable));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Object getSettingObject() {
		return settingObject;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSettingObject(Object newSettingObject) {
		Object oldSettingObject = settingObject;
		settingObject = newSettingObject;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, RHGxPackage.PORT_UPDATE_STATUS__SETTING_OBJECT, oldSettingObject, settingObject));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case RHGxPackage.PORT_UPDATE_STATUS__PORTS_UPDATABLE:
			return isPortsUpdatable();
		case RHGxPackage.PORT_UPDATE_STATUS__SETTING_OBJECT:
			return getSettingObject();
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
		case RHGxPackage.PORT_UPDATE_STATUS__PORTS_UPDATABLE:
			setPortsUpdatable((Boolean) newValue);
			return;
		case RHGxPackage.PORT_UPDATE_STATUS__SETTING_OBJECT:
			setSettingObject(newValue);
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
		case RHGxPackage.PORT_UPDATE_STATUS__PORTS_UPDATABLE:
			setPortsUpdatable(PORTS_UPDATABLE_EDEFAULT);
			return;
		case RHGxPackage.PORT_UPDATE_STATUS__SETTING_OBJECT:
			setSettingObject(SETTING_OBJECT_EDEFAULT);
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
		case RHGxPackage.PORT_UPDATE_STATUS__PORTS_UPDATABLE:
			return portsUpdatable != PORTS_UPDATABLE_EDEFAULT;
		case RHGxPackage.PORT_UPDATE_STATUS__SETTING_OBJECT:
			return SETTING_OBJECT_EDEFAULT == null ? settingObject != null : !SETTING_OBJECT_EDEFAULT.equals(settingObject);
		}
		return super.eIsSet(featureID);
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
		result.append(" (portsUpdatable: ");
		result.append(portsUpdatable);
		result.append(", settingObject: ");
		result.append(settingObject);
		result.append(')');
		return result.toString();
	}

} //PortUpdateStatusImpl
