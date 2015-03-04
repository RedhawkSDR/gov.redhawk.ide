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

import gov.redhawk.ide.sdr.ComponentsSubContainer;
import gov.redhawk.ide.sdr.SdrPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Components Sub Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl#getSubContainers <em>Sub Containers</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.impl.ComponentsSubContainerImpl#getContainerName <em>Container Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 * @since 9.0
 */
public class ComponentsSubContainerImpl extends SoftPkgRegistryImpl implements ComponentsSubContainer {
	/**
	 * The cached value of the '{@link #getSubContainers() <em>Sub Containers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<ComponentsSubContainer> subContainers;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ComponentsSubContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.COMPONENTS_SUB_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ComponentsSubContainer> getSubContainers() {
		if (subContainers == null) {
			subContainers = new EObjectResolvingEList<ComponentsSubContainer>(ComponentsSubContainer.class, this,
				SdrPackage.COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS);
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
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.COMPONENTS_SUB_CONTAINER__CONTAINER_NAME, oldContainerName, containerName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SdrPackage.COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS:
			return getSubContainers();
		case SdrPackage.COMPONENTS_SUB_CONTAINER__CONTAINER_NAME:
			return getContainerName();
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
		case SdrPackage.COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			getSubContainers().addAll((Collection< ? extends ComponentsSubContainer>) newValue);
			return;
		case SdrPackage.COMPONENTS_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName((String) newValue);
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
		case SdrPackage.COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			return;
		case SdrPackage.COMPONENTS_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName(CONTAINER_NAME_EDEFAULT);
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
		case SdrPackage.COMPONENTS_SUB_CONTAINER__SUB_CONTAINERS:
			return subContainers != null && !subContainers.isEmpty();
		case SdrPackage.COMPONENTS_SUB_CONTAINER__CONTAINER_NAME:
			return CONTAINER_NAME_EDEFAULT == null ? containerName != null : !CONTAINER_NAME_EDEFAULT.equals(containerName);
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

} //ComponentsSubContainerImpl
