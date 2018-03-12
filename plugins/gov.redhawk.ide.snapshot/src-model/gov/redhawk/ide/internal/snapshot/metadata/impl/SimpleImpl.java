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
package gov.redhawk.ide.internal.snapshot.metadata.impl;

import gov.redhawk.ide.internal.snapshot.metadata.Simple;
import gov.redhawk.ide.internal.snapshot.metadata.SnapshotMetadataPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Simple</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.impl.SimpleImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.impl.SimpleImpl#getId <em>Id</em>}</li>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.impl.SimpleImpl#getValue <em>Value</em>}</li>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.impl.SimpleImpl#getJavaType <em>Java Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SimpleImpl extends EObjectImpl implements Simple {
	/**
	 * The cached value of the '{@link #getMixed() <em>Mixed</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMixed()
	 * @generated
	 * @ordered
	 */
	protected FeatureMap mixed;

	/**
	 * The default value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected static final String ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getId()
	 * @generated
	 * @ordered
	 */
	protected String id = ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final String VALUE_EDEFAULT = null;

	/**
	 * The default value of the '{@link #getJavaType() <em>Java Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getJavaType()
	 * @generated
	 * @ordered
	 */
	protected static final String JAVA_TYPE_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SimpleImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnapshotMetadataPackage.Literals.SIMPLE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, SnapshotMetadataPackage.SIMPLE__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getId() {
		return id;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setId(String newId) {
		String oldId = id;
		id = newId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SnapshotMetadataPackage.SIMPLE__ID, oldId, id));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValue() {
		return (String) getMixed().get(SnapshotMetadataPackage.Literals.SIMPLE__VALUE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(String newValue) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.SIMPLE__VALUE, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getJavaType() {
		return (String) getMixed().get(SnapshotMetadataPackage.Literals.SIMPLE__JAVA_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setJavaType(String newJavaType) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.SIMPLE__JAVA_TYPE, newJavaType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case SnapshotMetadataPackage.SIMPLE__MIXED:
			return ((InternalEList< ? >) getMixed()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SnapshotMetadataPackage.SIMPLE__MIXED:
			if (coreType)
				return getMixed();
			return ((FeatureMap.Internal) getMixed()).getWrapper();
		case SnapshotMetadataPackage.SIMPLE__ID:
			return getId();
		case SnapshotMetadataPackage.SIMPLE__VALUE:
			return getValue();
		case SnapshotMetadataPackage.SIMPLE__JAVA_TYPE:
			return getJavaType();
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
		case SnapshotMetadataPackage.SIMPLE__MIXED:
			((FeatureMap.Internal) getMixed()).set(newValue);
			return;
		case SnapshotMetadataPackage.SIMPLE__ID:
			setId((String) newValue);
			return;
		case SnapshotMetadataPackage.SIMPLE__VALUE:
			setValue((String) newValue);
			return;
		case SnapshotMetadataPackage.SIMPLE__JAVA_TYPE:
			setJavaType((String) newValue);
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
		case SnapshotMetadataPackage.SIMPLE__MIXED:
			getMixed().clear();
			return;
		case SnapshotMetadataPackage.SIMPLE__ID:
			setId(ID_EDEFAULT);
			return;
		case SnapshotMetadataPackage.SIMPLE__VALUE:
			setValue(VALUE_EDEFAULT);
			return;
		case SnapshotMetadataPackage.SIMPLE__JAVA_TYPE:
			setJavaType(JAVA_TYPE_EDEFAULT);
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
		case SnapshotMetadataPackage.SIMPLE__MIXED:
			return mixed != null && !mixed.isEmpty();
		case SnapshotMetadataPackage.SIMPLE__ID:
			return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
		case SnapshotMetadataPackage.SIMPLE__VALUE:
			return VALUE_EDEFAULT == null ? getValue() != null : !VALUE_EDEFAULT.equals(getValue());
		case SnapshotMetadataPackage.SIMPLE__JAVA_TYPE:
			return JAVA_TYPE_EDEFAULT == null ? getJavaType() != null : !JAVA_TYPE_EDEFAULT.equals(getJavaType());
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
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(", id: ");
		result.append(id);
		result.append(')');
		return result.toString();
	}

} //SimpleImpl
