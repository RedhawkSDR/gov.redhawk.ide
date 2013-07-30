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
package gov.redhawk.ide.sdr.impl;

import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Devices Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.DevicesContainerImpl#getSdrRoot <em>Sdr Root</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DevicesContainerImpl extends SoftPkgRegistryImpl implements DevicesContainer {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DevicesContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.DEVICES_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrRoot getSdrRoot() {
		if (eContainerFeatureID() != SdrPackage.DEVICES_CONTAINER__SDR_ROOT) return null;
		return (SdrRoot)eInternalContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSdrRoot(SdrRoot newSdrRoot, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newSdrRoot, SdrPackage.DEVICES_CONTAINER__SDR_ROOT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSdrRoot(SdrRoot newSdrRoot) {
		if (newSdrRoot != eInternalContainer() || (eContainerFeatureID() != SdrPackage.DEVICES_CONTAINER__SDR_ROOT && newSdrRoot != null)) {
			if (EcoreUtil.isAncestor(this, newSdrRoot))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newSdrRoot != null)
				msgs = ((InternalEObject)newSdrRoot).eInverseAdd(this, SdrPackage.SDR_ROOT__DEVICES_CONTAINER, SdrRoot.class, msgs);
			msgs = basicSetSdrRoot(newSdrRoot, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.DEVICES_CONTAINER__SDR_ROOT, newSdrRoot, newSdrRoot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetSdrRoot((SdrRoot)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				return basicSetSdrRoot(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				return eInternalContainer().eInverseRemove(this, SdrPackage.SDR_ROOT__DEVICES_CONTAINER, SdrRoot.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				return getSdrRoot();
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
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				setSdrRoot((SdrRoot)newValue);
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
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				setSdrRoot((SdrRoot)null);
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
			case SdrPackage.DEVICES_CONTAINER__SDR_ROOT:
				return getSdrRoot() != null;
		}
		return super.eIsSet(featureID);
	}

} //DevicesContainerImpl
