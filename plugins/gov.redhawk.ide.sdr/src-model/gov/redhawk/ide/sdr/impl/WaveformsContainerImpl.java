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
package gov.redhawk.ide.sdr.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.WaveformsContainer;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Waveforms Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl#getWaveforms <em>Waveforms</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl#getName <em>Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.WaveformsContainerImpl#getChildContainers <em>Child Containers</em>}</li>
 * </ul>
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
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChildContainers() <em>Child Containers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<WaveformsContainer> childContainers;

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
	@Override
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
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.WAVEFORMS_CONTAINER__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<WaveformsContainer> getChildContainers() {
		if (childContainers == null) {
			childContainers = new EObjectContainmentEList<WaveformsContainer>(WaveformsContainer.class, this, SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS);
		}
		return childContainers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
		case SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS:
			return ((InternalEList< ? >) getChildContainers()).basicRemove(otherEnd, msgs);
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
		case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
			return getWaveforms();
		case SdrPackage.WAVEFORMS_CONTAINER__NAME:
			return getName();
		case SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS:
			return getChildContainers();
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
		case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
			getWaveforms().clear();
			getWaveforms().addAll((Collection< ? extends SoftwareAssembly>) newValue);
			return;
		case SdrPackage.WAVEFORMS_CONTAINER__NAME:
			setName((String) newValue);
			return;
		case SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS:
			getChildContainers().clear();
			getChildContainers().addAll((Collection< ? extends WaveformsContainer>) newValue);
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
		case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
			getWaveforms().clear();
			return;
		case SdrPackage.WAVEFORMS_CONTAINER__NAME:
			setName(NAME_EDEFAULT);
			return;
		case SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS:
			getChildContainers().clear();
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
		case SdrPackage.WAVEFORMS_CONTAINER__WAVEFORMS:
			return waveforms != null && !waveforms.isEmpty();
		case SdrPackage.WAVEFORMS_CONTAINER__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case SdrPackage.WAVEFORMS_CONTAINER__CHILD_CONTAINERS:
			return childContainers != null && !childContainers.isEmpty();
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

		StringBuilder result = new StringBuilder(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} // WaveformsContainerImpl
