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
import gov.redhawk.ide.sdr.WaveformsSubContainer;

import java.util.Collection;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waveforms Sub Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.WaveformsSubContainerImpl#getSubContainers <em>Sub Containers</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.impl.WaveformsSubContainerImpl#getContainerName <em>Container Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.impl.WaveformsSubContainerImpl#getWaveforms <em>Waveforms</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WaveformsSubContainerImpl extends EObjectImpl implements WaveformsSubContainer {
	/**
	 * The cached value of the '{@link #getSubContainers() <em>Sub Containers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<WaveformsSubContainer> subContainers;

	/**
	 * The default value of the '{@link #getContainerName() <em>Container Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainerName()
	 * @generated
	 * @ordered
	 */
	protected static final String CONTAINER_NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getContainerName() <em>Container Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContainerName()
	 * @generated
	 * @ordered
	 */
	protected String containerName = CONTAINER_NAME_EDEFAULT;

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
	protected WaveformsSubContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.WAVEFORMS_SUB_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WaveformsSubContainer> getSubContainers() {
		if (subContainers == null) {
			subContainers = new EObjectResolvingEList<WaveformsSubContainer>(WaveformsSubContainer.class, this,
				SdrPackage.WAVEFORMS_SUB_CONTAINER__SUB_CONTAINERS);
		}
		return subContainers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getContainerName() {
		return containerName;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContainerName(String newContainerName) {
		String oldContainerName = containerName;
		containerName = newContainerName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.WAVEFORMS_SUB_CONTAINER__CONTAINER_NAME, oldContainerName, containerName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SoftwareAssembly> getWaveforms() {
		if (waveforms == null) {
			waveforms = new EObjectResolvingEList<SoftwareAssembly>(SoftwareAssembly.class, this, SdrPackage.WAVEFORMS_SUB_CONTAINER__WAVEFORMS);
		}
		return waveforms;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__SUB_CONTAINERS:
			return getSubContainers();
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__CONTAINER_NAME:
			return getContainerName();
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__WAVEFORMS:
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
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			getSubContainers().addAll((Collection< ? extends WaveformsSubContainer>) newValue);
			return;
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName((String) newValue);
			return;
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__WAVEFORMS:
			getWaveforms().clear();
			getWaveforms().addAll((Collection< ? extends SoftwareAssembly>) newValue);
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
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			return;
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName(CONTAINER_NAME_EDEFAULT);
			return;
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__WAVEFORMS:
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
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__SUB_CONTAINERS:
			return subContainers != null && !subContainers.isEmpty();
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__CONTAINER_NAME:
			return CONTAINER_NAME_EDEFAULT == null ? containerName != null : !CONTAINER_NAME_EDEFAULT.equals(containerName);
		case SdrPackage.WAVEFORMS_SUB_CONTAINER__WAVEFORMS:
			return waveforms != null && !waveforms.isEmpty();
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
		result.append(" (containerName: ");
		result.append(containerName);
		result.append(')');
		return result.toString();
	}

} //WaveformsSubContainerImpl
