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
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl#getValue <em>Value</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ValueImpl#getJavaType <em>Java Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ValueImpl extends EObjectImpl implements Value {
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
	protected ValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnapshotMetadataPackage.Literals.VALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public FeatureMap getMixed() {
		if (mixed == null)
		{
			mixed = new BasicFeatureMap(this, SnapshotMetadataPackage.VALUE__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getValue() {
		return (String)getMixed().get(SnapshotMetadataPackage.Literals.VALUE__VALUE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setValue(String newValue) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.VALUE__VALUE, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getJavaType() {
		return (String)getMixed().get(SnapshotMetadataPackage.Literals.VALUE__JAVA_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setJavaType(String newJavaType) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.VALUE__JAVA_TYPE, newJavaType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID)
		{
			case SnapshotMetadataPackage.VALUE__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
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
		switch (featureID)
		{
			case SnapshotMetadataPackage.VALUE__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case SnapshotMetadataPackage.VALUE__VALUE:
				return getValue();
			case SnapshotMetadataPackage.VALUE__JAVA_TYPE:
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
		switch (featureID)
		{
			case SnapshotMetadataPackage.VALUE__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case SnapshotMetadataPackage.VALUE__VALUE:
				setValue((String)newValue);
				return;
			case SnapshotMetadataPackage.VALUE__JAVA_TYPE:
				setJavaType((String)newValue);
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
		switch (featureID)
		{
			case SnapshotMetadataPackage.VALUE__MIXED:
				getMixed().clear();
				return;
			case SnapshotMetadataPackage.VALUE__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case SnapshotMetadataPackage.VALUE__JAVA_TYPE:
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
		switch (featureID)
		{
			case SnapshotMetadataPackage.VALUE__MIXED:
				return mixed != null && !mixed.isEmpty();
			case SnapshotMetadataPackage.VALUE__VALUE:
				return VALUE_EDEFAULT == null ? getValue() != null : !VALUE_EDEFAULT.equals(getValue());
			case SnapshotMetadataPackage.VALUE__JAVA_TYPE:
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
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (mixed: ");
		result.append(mixed);
		result.append(')');
		return result.toString();
	}

} //ValueImpl
