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
package gov.redhawk.ide.codegen.impl;

import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Wave Dev Settings</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.impl.WaveDevSettingsImpl#getImplSettings <em>Impl Settings</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaveDevSettingsImpl extends EObjectImpl implements WaveDevSettings {

	/**
	 * The cached value of the '{@link #getImplSettings() <em>Impl Settings</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImplSettings()
	 * @generated
	 * @ordered
	 */
	protected EMap<String, ImplementationSettings> implSettings;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaveDevSettingsImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.WAVE_DEV_SETTINGS;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<String, ImplementationSettings> getImplSettings() {
		if (implSettings == null) {
			implSettings = new EcoreEMap<String,ImplementationSettings>(CodegenPackage.Literals.IMPL_ID_TO_SETTINGS_MAP, ImplIdToSettingsMapImpl.class, this, CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS);
		}
		return implSettings;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS:
				return ((InternalEList<?>)getImplSettings()).basicRemove(otherEnd, msgs);
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
			case CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS:
				if (coreType) return getImplSettings();
				else return getImplSettings().map();
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
			case CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS:
				((EStructuralFeature.Setting)getImplSettings()).set(newValue);
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
			case CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS:
				getImplSettings().clear();
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
			case CodegenPackage.WAVE_DEV_SETTINGS__IMPL_SETTINGS:
				return implSettings != null && !implSettings.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} // WaveDevSettingsImpl
