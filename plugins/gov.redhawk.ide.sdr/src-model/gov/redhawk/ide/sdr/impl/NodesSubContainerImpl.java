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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import gov.redhawk.ide.sdr.NodesSubContainer;
import gov.redhawk.ide.sdr.SdrPackage;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Nodes Sub Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.impl.NodesSubContainerImpl#getSubContainers <em>Sub Containers</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.NodesSubContainerImpl#getContainerName <em>Container Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.NodesSubContainerImpl#getNodes <em>Nodes</em>}</li>
 * </ul>
 *
 * @generated
 */
public class NodesSubContainerImpl extends EObjectImpl implements NodesSubContainer {
	/**
	 * The cached value of the '{@link #getSubContainers() <em>Sub Containers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubContainers()
	 * @generated
	 * @ordered
	 */
	protected EList<NodesSubContainer> subContainers;

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
	 * The cached value of the '{@link #getNodes() <em>Nodes</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNodes()
	 * @generated
	 * @ordered
	 */
	protected EList<DeviceConfiguration> nodes;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodesSubContainerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.NODES_SUB_CONTAINER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NodesSubContainer> getSubContainers() {
		if (subContainers == null) {
			subContainers = new EObjectResolvingEList<NodesSubContainer>(NodesSubContainer.class, this, SdrPackage.NODES_SUB_CONTAINER__SUB_CONTAINERS);
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
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.NODES_SUB_CONTAINER__CONTAINER_NAME, oldContainerName, containerName));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<DeviceConfiguration> getNodes() {
		if (nodes == null) {
			nodes = new EObjectResolvingEList<DeviceConfiguration>(DeviceConfiguration.class, this, SdrPackage.NODES_SUB_CONTAINER__NODES);
		}
		return nodes;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SdrPackage.NODES_SUB_CONTAINER__SUB_CONTAINERS:
			return getSubContainers();
		case SdrPackage.NODES_SUB_CONTAINER__CONTAINER_NAME:
			return getContainerName();
		case SdrPackage.NODES_SUB_CONTAINER__NODES:
			return getNodes();
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
		case SdrPackage.NODES_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			getSubContainers().addAll((Collection< ? extends NodesSubContainer>) newValue);
			return;
		case SdrPackage.NODES_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName((String) newValue);
			return;
		case SdrPackage.NODES_SUB_CONTAINER__NODES:
			getNodes().clear();
			getNodes().addAll((Collection< ? extends DeviceConfiguration>) newValue);
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
		case SdrPackage.NODES_SUB_CONTAINER__SUB_CONTAINERS:
			getSubContainers().clear();
			return;
		case SdrPackage.NODES_SUB_CONTAINER__CONTAINER_NAME:
			setContainerName(CONTAINER_NAME_EDEFAULT);
			return;
		case SdrPackage.NODES_SUB_CONTAINER__NODES:
			getNodes().clear();
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
		case SdrPackage.NODES_SUB_CONTAINER__SUB_CONTAINERS:
			return subContainers != null && !subContainers.isEmpty();
		case SdrPackage.NODES_SUB_CONTAINER__CONTAINER_NAME:
			return CONTAINER_NAME_EDEFAULT == null ? containerName != null : !CONTAINER_NAME_EDEFAULT.equals(containerName);
		case SdrPackage.NODES_SUB_CONTAINER__NODES:
			return nodes != null && !nodes.isEmpty();
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
		result.append(" (containerName: ");
		result.append(containerName);
		result.append(')');
		return result.toString();
	}

} // NodesSubContainerImpl
