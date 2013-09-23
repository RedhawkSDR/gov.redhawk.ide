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

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>SRI</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getHversion <em>Hversion</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getXstart <em>Xstart</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getXdelta <em>Xdelta</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getXunits <em>Xunits</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getSubsize <em>Subsize</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getYstart <em>Ystart</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getYdelta <em>Ydelta</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getYunits <em>Yunits</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getStreamID <em>Stream ID</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#isBlocking <em>Blocking</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.impl.SRIImpl#getKeywords <em>Keywords</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SRIImpl extends EObjectImpl implements SRI {
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
	 * The default value of the '{@link #getStreamID() <em>Stream ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getStreamID()
	 * @generated
	 * @ordered
	 */
	protected static final String STREAM_ID_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SRIImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnapshotMetadataPackage.Literals.SRI;
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
			mixed = new BasicFeatureMap(this, SnapshotMetadataPackage.SRI__MIXED);
		}
		return mixed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int getHversion() {
		return (Integer)getMixed().get(SnapshotMetadataPackage.Literals.SRI__HVERSION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setHversion(int newHversion) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__HVERSION, newHversion);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetHversion() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__HVERSION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetHversion() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__HVERSION);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getXstart() {
		return (Double)getMixed().get(SnapshotMetadataPackage.Literals.SRI__XSTART, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setXstart(double newXstart) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__XSTART, newXstart);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetXstart() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__XSTART);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetXstart() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__XSTART);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getXdelta() {
		return (Double)getMixed().get(SnapshotMetadataPackage.Literals.SRI__XDELTA, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setXdelta(double newXdelta) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__XDELTA, newXdelta);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetXdelta() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__XDELTA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetXdelta() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__XDELTA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public short getXunits() {
		return (Short)getMixed().get(SnapshotMetadataPackage.Literals.SRI__XUNITS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setXunits(short newXunits) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__XUNITS, newXunits);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetXunits() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__XUNITS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetXunits() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__XUNITS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getSubsize() {
		return (Double)getMixed().get(SnapshotMetadataPackage.Literals.SRI__SUBSIZE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setSubsize(double newSubsize) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__SUBSIZE, newSubsize);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetSubsize() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__SUBSIZE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetSubsize() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__SUBSIZE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getYstart() {
		return (Double)getMixed().get(SnapshotMetadataPackage.Literals.SRI__YSTART, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setYstart(double newYstart) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__YSTART, newYstart);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetYstart() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__YSTART);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetYstart() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__YSTART);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public double getYdelta() {
		return (Double)getMixed().get(SnapshotMetadataPackage.Literals.SRI__YDELTA, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setYdelta(double newYdelta) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__YDELTA, newYdelta);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetYdelta() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__YDELTA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetYdelta() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__YDELTA);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public short getYunits() {
		return (Short)getMixed().get(SnapshotMetadataPackage.Literals.SRI__YUNITS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setYunits(short newYunits) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__YUNITS, newYunits);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetYunits() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__YUNITS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetYunits() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__YUNITS);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public short getMode() {
		return (Short)getMixed().get(SnapshotMetadataPackage.Literals.SRI__MODE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setMode(short newMode) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__MODE, newMode);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetMode() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__MODE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetMode() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__MODE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getStreamID() {
		return (String)getMixed().get(SnapshotMetadataPackage.Literals.SRI__STREAM_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setStreamID(String newStreamID) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__STREAM_ID, newStreamID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isBlocking() {
		return (Boolean)getMixed().get(SnapshotMetadataPackage.Literals.SRI__BLOCKING, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setBlocking(boolean newBlocking) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__BLOCKING, newBlocking);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void unsetBlocking() {
		((FeatureMap.Internal)getMixed()).clear(SnapshotMetadataPackage.Literals.SRI__BLOCKING);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean isSetBlocking() {
		return !((FeatureMap.Internal)getMixed()).isEmpty(SnapshotMetadataPackage.Literals.SRI__BLOCKING);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public KeywordsType getKeywords() {
		return (KeywordsType)getMixed().get(SnapshotMetadataPackage.Literals.SRI__KEYWORDS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetKeywords(KeywordsType newKeywords, NotificationChain msgs) {
		return ((FeatureMap.Internal)getMixed()).basicAdd(SnapshotMetadataPackage.Literals.SRI__KEYWORDS, newKeywords, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setKeywords(KeywordsType newKeywords) {
		((FeatureMap.Internal)getMixed()).set(SnapshotMetadataPackage.Literals.SRI__KEYWORDS, newKeywords);
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
			case SnapshotMetadataPackage.SRI__MIXED:
				return ((InternalEList<?>)getMixed()).basicRemove(otherEnd, msgs);
			case SnapshotMetadataPackage.SRI__KEYWORDS:
				return basicSetKeywords(null, msgs);
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
			case SnapshotMetadataPackage.SRI__MIXED:
				if (coreType) return getMixed();
				return ((FeatureMap.Internal)getMixed()).getWrapper();
			case SnapshotMetadataPackage.SRI__HVERSION:
				return getHversion();
			case SnapshotMetadataPackage.SRI__XSTART:
				return getXstart();
			case SnapshotMetadataPackage.SRI__XDELTA:
				return getXdelta();
			case SnapshotMetadataPackage.SRI__XUNITS:
				return getXunits();
			case SnapshotMetadataPackage.SRI__SUBSIZE:
				return getSubsize();
			case SnapshotMetadataPackage.SRI__YSTART:
				return getYstart();
			case SnapshotMetadataPackage.SRI__YDELTA:
				return getYdelta();
			case SnapshotMetadataPackage.SRI__YUNITS:
				return getYunits();
			case SnapshotMetadataPackage.SRI__MODE:
				return getMode();
			case SnapshotMetadataPackage.SRI__STREAM_ID:
				return getStreamID();
			case SnapshotMetadataPackage.SRI__BLOCKING:
				return isBlocking();
			case SnapshotMetadataPackage.SRI__KEYWORDS:
				return getKeywords();
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
			case SnapshotMetadataPackage.SRI__MIXED:
				((FeatureMap.Internal)getMixed()).set(newValue);
				return;
			case SnapshotMetadataPackage.SRI__HVERSION:
				setHversion((Integer)newValue);
				return;
			case SnapshotMetadataPackage.SRI__XSTART:
				setXstart((Double)newValue);
				return;
			case SnapshotMetadataPackage.SRI__XDELTA:
				setXdelta((Double)newValue);
				return;
			case SnapshotMetadataPackage.SRI__XUNITS:
				setXunits((Short)newValue);
				return;
			case SnapshotMetadataPackage.SRI__SUBSIZE:
				setSubsize((Double)newValue);
				return;
			case SnapshotMetadataPackage.SRI__YSTART:
				setYstart((Double)newValue);
				return;
			case SnapshotMetadataPackage.SRI__YDELTA:
				setYdelta((Double)newValue);
				return;
			case SnapshotMetadataPackage.SRI__YUNITS:
				setYunits((Short)newValue);
				return;
			case SnapshotMetadataPackage.SRI__MODE:
				setMode((Short)newValue);
				return;
			case SnapshotMetadataPackage.SRI__STREAM_ID:
				setStreamID((String)newValue);
				return;
			case SnapshotMetadataPackage.SRI__BLOCKING:
				setBlocking((Boolean)newValue);
				return;
			case SnapshotMetadataPackage.SRI__KEYWORDS:
				setKeywords((KeywordsType)newValue);
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
			case SnapshotMetadataPackage.SRI__MIXED:
				getMixed().clear();
				return;
			case SnapshotMetadataPackage.SRI__HVERSION:
				unsetHversion();
				return;
			case SnapshotMetadataPackage.SRI__XSTART:
				unsetXstart();
				return;
			case SnapshotMetadataPackage.SRI__XDELTA:
				unsetXdelta();
				return;
			case SnapshotMetadataPackage.SRI__XUNITS:
				unsetXunits();
				return;
			case SnapshotMetadataPackage.SRI__SUBSIZE:
				unsetSubsize();
				return;
			case SnapshotMetadataPackage.SRI__YSTART:
				unsetYstart();
				return;
			case SnapshotMetadataPackage.SRI__YDELTA:
				unsetYdelta();
				return;
			case SnapshotMetadataPackage.SRI__YUNITS:
				unsetYunits();
				return;
			case SnapshotMetadataPackage.SRI__MODE:
				unsetMode();
				return;
			case SnapshotMetadataPackage.SRI__STREAM_ID:
				setStreamID(STREAM_ID_EDEFAULT);
				return;
			case SnapshotMetadataPackage.SRI__BLOCKING:
				unsetBlocking();
				return;
			case SnapshotMetadataPackage.SRI__KEYWORDS:
				setKeywords((KeywordsType)null);
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
			case SnapshotMetadataPackage.SRI__MIXED:
				return mixed != null && !mixed.isEmpty();
			case SnapshotMetadataPackage.SRI__HVERSION:
				return isSetHversion();
			case SnapshotMetadataPackage.SRI__XSTART:
				return isSetXstart();
			case SnapshotMetadataPackage.SRI__XDELTA:
				return isSetXdelta();
			case SnapshotMetadataPackage.SRI__XUNITS:
				return isSetXunits();
			case SnapshotMetadataPackage.SRI__SUBSIZE:
				return isSetSubsize();
			case SnapshotMetadataPackage.SRI__YSTART:
				return isSetYstart();
			case SnapshotMetadataPackage.SRI__YDELTA:
				return isSetYdelta();
			case SnapshotMetadataPackage.SRI__YUNITS:
				return isSetYunits();
			case SnapshotMetadataPackage.SRI__MODE:
				return isSetMode();
			case SnapshotMetadataPackage.SRI__STREAM_ID:
				return STREAM_ID_EDEFAULT == null ? getStreamID() != null : !STREAM_ID_EDEFAULT.equals(getStreamID());
			case SnapshotMetadataPackage.SRI__BLOCKING:
				return isSetBlocking();
			case SnapshotMetadataPackage.SRI__KEYWORDS:
				return getKeywords() != null;
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

} //SRIImpl
