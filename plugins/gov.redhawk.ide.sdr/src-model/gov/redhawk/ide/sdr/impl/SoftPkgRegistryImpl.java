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
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import mil.jpeojtrs.sca.spd.SoftPkg;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Soft Pkg Registry</b></em>'.
 * @since 8.0
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl#getName <em>Name</em>}</li>
 * <li>{@link gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl#getComponents <em>Components</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class SoftPkgRegistryImpl extends EObjectImpl implements SoftPkgRegistry {
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
	 * The cached value of the '{@link #getComponents() <em>Components</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getComponents()
	 * @generated
	 * @ordered
	 */
	protected EList<SoftPkg> components;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SoftPkgRegistryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SdrPackage.Literals.SOFT_PKG_REGISTRY;
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
			eNotify(new ENotificationImpl(this, Notification.SET, SdrPackage.SOFT_PKG_REGISTRY__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EList<SoftPkg> getComponents() {
		if (components == null) {
			components = new EObjectResolvingEList<SoftPkg>(SoftPkg.class, this, SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS);
		}
		return components;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public SoftPkg getSoftPkg(String softPkgId) {
		// END GENERATED CODE
		if (softPkgId == null) {
			return null;
		}

		// Search this container's contents
		for (SoftPkg spd : getComponents()) {
			if (softPkgId.equals(spd.getId())) {
				return spd;
			}
		}

		// Search child containers' contents
		for (EObject child : eContents()) {
			if (child instanceof SoftPkgRegistry) {
				SoftPkg spd = ((SoftPkgRegistry) child).getSoftPkg(softPkgId);
				if (spd != null) {
					return spd;
				}
			}
		}
		return null;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public EList<SoftPkg> getAllComponents() {
		// END GENERATED CODE
		EList<SoftPkg> retVal = new BasicEList<>(getComponents());
		TreeIterator<SoftPkgRegistry> iter = EcoreUtil.getAllContents(this, false);
		while (iter.hasNext()) {
			retVal.addAll(iter.next().getComponents());
		}
		return retVal;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
		case SdrPackage.SOFT_PKG_REGISTRY__NAME:
			return getName();
		case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
			return getComponents();
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
		case SdrPackage.SOFT_PKG_REGISTRY__NAME:
			setName((String) newValue);
			return;
		case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
			getComponents().clear();
			getComponents().addAll((Collection< ? extends SoftPkg>) newValue);
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
		case SdrPackage.SOFT_PKG_REGISTRY__NAME:
			setName(NAME_EDEFAULT);
			return;
		case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
			getComponents().clear();
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
		case SdrPackage.SOFT_PKG_REGISTRY__NAME:
			return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
		case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
			return components != null && !components.isEmpty();
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

} // SoftPkgRegistryImpl
