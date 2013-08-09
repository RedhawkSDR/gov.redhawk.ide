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
//BEGIN GENERATED CODE
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl#getNumberOfSamples <em>Number Of Samples</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl#getTime <em>Time</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl#getBulkIOType <em>Bulk IO Type</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.ModelImpl#getStreamSRI <em>Stream SRI</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelImpl extends EObjectImpl implements Model {
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
	 * The default value of the '{@link #getBulkIOType() <em>Bulk IO Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBulkIOType()
	 * @generated
	 * @ordered
	 */
	protected static final String BULK_IO_TYPE_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ModelImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnapshotMetadataPackage.Literals.MODEL;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public FeatureMap getMixed() {
		if (mixed == null) {
			mixed = new BasicFeatureMap(this, SnapshotMetadataPackage.MODEL__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public long getNumberOfSamples() {
		return (Long) getMixed().get(SnapshotMetadataPackage.Literals.MODEL__NUMBER_OF_SAMPLES, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumberOfSamples(long newNumberOfSamples) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.MODEL__NUMBER_OF_SAMPLES, newNumberOfSamples);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void unsetNumberOfSamples() {
		((FeatureMap.Internal) getMixed()).clear(SnapshotMetadataPackage.Literals.MODEL__NUMBER_OF_SAMPLES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isSetNumberOfSamples() {
		return !((FeatureMap.Internal) getMixed()).isEmpty(SnapshotMetadataPackage.Literals.MODEL__NUMBER_OF_SAMPLES);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Time getTime() {
		return (Time) getMixed().get(SnapshotMetadataPackage.Literals.MODEL__TIME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetTime(Time newTime, NotificationChain msgs) {
		return ((FeatureMap.Internal) getMixed()).basicAdd(SnapshotMetadataPackage.Literals.MODEL__TIME, newTime, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTime(Time newTime) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.MODEL__TIME, newTime);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBulkIOType() {
		return (String) getMixed().get(SnapshotMetadataPackage.Literals.MODEL__BULK_IO_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBulkIOType(String newBulkIOType) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.MODEL__BULK_IO_TYPE, newBulkIOType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SRI getStreamSRI() {
		return (SRI) getMixed().get(SnapshotMetadataPackage.Literals.MODEL__STREAM_SRI, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetStreamSRI(SRI newStreamSRI, NotificationChain msgs) {
		return ((FeatureMap.Internal) getMixed()).basicAdd(SnapshotMetadataPackage.Literals.MODEL__STREAM_SRI, newStreamSRI, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStreamSRI(SRI newStreamSRI) {
		((FeatureMap.Internal) getMixed()).set(SnapshotMetadataPackage.Literals.MODEL__STREAM_SRI, newStreamSRI);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case SnapshotMetadataPackage.MODEL__MIXED:
			return ((InternalEList< ? >) getMixed()).basicRemove(otherEnd, msgs);
		case SnapshotMetadataPackage.MODEL__TIME:
			return basicSetTime(null, msgs);
		case SnapshotMetadataPackage.MODEL__STREAM_SRI:
			return basicSetStreamSRI(null, msgs);
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
		case SnapshotMetadataPackage.MODEL__MIXED:
			if (coreType)
				return getMixed();
			return ((FeatureMap.Internal) getMixed()).getWrapper();
		case SnapshotMetadataPackage.MODEL__NUMBER_OF_SAMPLES:
			return getNumberOfSamples();
		case SnapshotMetadataPackage.MODEL__TIME:
			return getTime();
		case SnapshotMetadataPackage.MODEL__BULK_IO_TYPE:
			return getBulkIOType();
		case SnapshotMetadataPackage.MODEL__STREAM_SRI:
			return getStreamSRI();
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
		case SnapshotMetadataPackage.MODEL__MIXED:
			((FeatureMap.Internal) getMixed()).set(newValue);
			return;
		case SnapshotMetadataPackage.MODEL__NUMBER_OF_SAMPLES:
			setNumberOfSamples((Long) newValue);
			return;
		case SnapshotMetadataPackage.MODEL__TIME:
			setTime((Time) newValue);
			return;
		case SnapshotMetadataPackage.MODEL__BULK_IO_TYPE:
			setBulkIOType((String) newValue);
			return;
		case SnapshotMetadataPackage.MODEL__STREAM_SRI:
			setStreamSRI((SRI) newValue);
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
		case SnapshotMetadataPackage.MODEL__MIXED:
			getMixed().clear();
			return;
		case SnapshotMetadataPackage.MODEL__NUMBER_OF_SAMPLES:
			unsetNumberOfSamples();
			return;
		case SnapshotMetadataPackage.MODEL__TIME:
			setTime((Time) null);
			return;
		case SnapshotMetadataPackage.MODEL__BULK_IO_TYPE:
			setBulkIOType(BULK_IO_TYPE_EDEFAULT);
			return;
		case SnapshotMetadataPackage.MODEL__STREAM_SRI:
			setStreamSRI((SRI) null);
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
		case SnapshotMetadataPackage.MODEL__MIXED:
			return mixed != null && !mixed.isEmpty();
		case SnapshotMetadataPackage.MODEL__NUMBER_OF_SAMPLES:
			return isSetNumberOfSamples();
		case SnapshotMetadataPackage.MODEL__TIME:
			return getTime() != null;
		case SnapshotMetadataPackage.MODEL__BULK_IO_TYPE:
			return BULK_IO_TYPE_EDEFAULT == null ? getBulkIOType() != null : !BULK_IO_TYPE_EDEFAULT.equals(getBulkIOType());
		case SnapshotMetadataPackage.MODEL__STREAM_SRI:
			return getStreamSRI() != null;
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
		result.append(')');
		return result.toString();
	}

} //ModelImpl
