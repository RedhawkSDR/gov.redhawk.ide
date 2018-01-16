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

import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SharedLibrariesContainer;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Shared Libraries Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.SharedLibrariesContainerImpl#getChildContainers <em>Child Containers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SharedLibrariesContainerImpl extends SoftPkgRegistryImpl implements SharedLibrariesContainer {
	/**
	 * The cached value of the '{@link #getChildContainers() <em>Child Containers</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChildContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<SharedLibrariesContainer> childContainers;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SharedLibrariesContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.SHARED_LIBRARIES_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<SharedLibrariesContainer> getChildContainers() {
		if (childContainers == null) {
			childContainers = new EObjectContainmentEList<SharedLibrariesContainer>(SharedLibrariesContainer.class, this,
				SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS);
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
		case SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS:
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
		case SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS:
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
		case SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS:
			getChildContainers().clear();
			getChildContainers().addAll((Collection< ? extends SharedLibrariesContainer>) newValue);
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
		case SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS:
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
		case SdrPackage.SHARED_LIBRARIES_CONTAINER__CHILD_CONTAINERS:
			return childContainers != null && !childContainers.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //SharedLibrariesContainerImpl
