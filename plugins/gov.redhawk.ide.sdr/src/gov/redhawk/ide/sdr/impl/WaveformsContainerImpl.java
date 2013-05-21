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

import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.WaveformsContainer;

import java.util.Collection;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waveforms Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl#getSdrRoot <em>Sdr Root</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl#getWaveforms <em>Waveforms</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaveformsContainerImpl extends EObjectImpl implements WaveformsContainer {

	/**
	 * The cached value of the '{@link #getWaveforms() <em>Waveforms</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWaveforms()
	 * @generated
	 * @ordered
	 */
	protected EList<SoftwareAssembly> waveforms;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaveformsContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.WAVEFORMS_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrRoot getSdrRoot() {
		if (eContainerFeatureID() != SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT) return null;
		return (SdrRoot)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetSdrRoot(SdrRoot newSdrRoot, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newSdrRoot, SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSdrRoot(SdrRoot newSdrRoot) {
		if (newSdrRoot != eInternalContainer() || (eContainerFeatureID() != SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT && newSdrRoot != null)) {
			if (EcoreUtil.isAncestor(this, newSdrRoot))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newSdrRoot != null)
				msgs = ((InternalEObject)newSdrRoot).eInverseAdd(this, SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, SdrRoot.class, msgs);
			msgs = basicSetSdrRoot(newSdrRoot, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT, newSdrRoot, newSdrRoot));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SoftwareAssembly> getWaveforms() {
		if (waveforms == null) {
			waveforms = new EObjectResolvingEList<SoftwareAssembly>(SoftwareAssembly.class, this, SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS);
		}
		return waveforms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
				return eInternalContainer().eInverseRemove(this, SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER, SdrRoot.class, msgs);
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
				return getSdrRoot();
			case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
				return getWaveforms();
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
				setSdrRoot((SdrRoot)newValue);
				return;
			case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
				getWaveforms().clear();
				getWaveforms().addAll((Collection<? extends SoftwareAssembly>)newValue);
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
				setSdrRoot((SdrRoot)null);
				return;
			case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
				getWaveforms().clear();
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
			case SdrPackage.WAVEFORMS_CONTAINER__SDR_ROOT:
				return getSdrRoot() != null;
			case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
				return waveforms != null && !waveforms.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //WaveformsContainerImpl
