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
import gov.redhawk.ide.sdr.SoftPkgRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.FeatureMap.ValueListIterator;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Soft Pkg Registry</b></em>'.
 * @since 8.0
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.impl.SoftPkgRegistryImpl#getComponents <em>Components</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class SoftPkgRegistryImpl extends PropertyRegistryImpl implements SoftPkgRegistry {
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
	public EList<SoftPkg> getComponents() {
		if (components == null) {
			components = new EObjectResolvingEList<SoftPkg>(SoftPkg.class, this, SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS);
		}
		return components;
	}

	private Map<String, SoftPkg> spdMap = new HashMap<String, SoftPkg>();

	private Adapter mapAdapter = new AdapterImpl() {
		// END GENERATED CODE
		public void notifyChanged(org.eclipse.emf.common.notify.Notification msg) {
			switch (msg.getFeatureID(SoftPkgRegistry.class)) {
			case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
				switch (msg.getEventType()) {
				case Notification.ADD:
					if (msg.getNewValue() instanceof SoftPkg) {
						SoftPkg newValue = (SoftPkg) msg.getNewValue();
						addSpd(newValue);
					}
					break;
				case Notification.ADD_MANY:
					if (msg.getNewValue() instanceof List< ? >) {
						List< ? > newValues = (List< ? >) msg.getNewValue();
						for (Object o : newValues) {
							if (o instanceof SoftPkg) {
								SoftPkg newValue = (SoftPkg) o;
								addSpd(newValue);
							}
						}
					}
					break;
				case Notification.REMOVE:
					if (msg.getOldValue() instanceof SoftPkg) {
						SoftPkg oldValue = (SoftPkg) msg.getOldValue();
						removeSpd(oldValue);
					}
					break;
				case Notification.REMOVE_MANY:
					if (msg.getOldValue() instanceof List) {
						List< ? > oldValues = (List< ? >) msg.getOldValue();
						for (Object o : oldValues) {
							if (o instanceof SoftPkg) {
								SoftPkg newValue = (SoftPkg) o;
								removeSpd(newValue);
							}
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
		// BEGIN GENERATED CODE
	};
	{
		// END GENERATED CODE
		eAdapters().add(mapAdapter);
		// BEGIN GENERATED CODE
	}

	private void addSpd(SoftPkg spd) {
		// END GENERATED CODE
		if (spd == null) {
			return;
		}
		spdMap.put(spd.getId(), spd);
		if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			Properties prf = spd.getPropertyFile().getProperties();
			for (ValueListIterator<Object> iterator = prf.getProperties().valueListIterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if (obj instanceof AbstractProperty) {
					AbstractProperty prop = (AbstractProperty) obj;
					if (DceUuidUtil.isValid(prop.getId())) {
						getProperties().put(prop.getId(), prop);
					}
				}
			}
		}
		// BEGIN GENERATED CODE
	}

	private void removeSpd(SoftPkg spd) {
		// END GENERATED CODE
		if (spd == null) {
			return;
		}
		spdMap.remove(spd.getId());
		if (spd.getPropertyFile() != null && spd.getPropertyFile().getProperties() != null) {
			Properties prf = spd.getPropertyFile().getProperties();
			for (ValueListIterator<Object> iterator = prf.getProperties().valueListIterator(); iterator.hasNext();) {
				Object obj = iterator.next();
				if (obj instanceof AbstractProperty) {
					AbstractProperty prop = (AbstractProperty) obj;
					if (DceUuidUtil.isValid(prop.getId())) {
						getProperties().remove(prop.getId());
					}
				}
			}
		}
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public SoftPkg getSoftPkg(String softPkgId) {
		// END GENERATED CODE
		if (softPkgId != null) {
			for (SoftPkg spd : getComponents()) {
				if (softPkgId.equals(spd.getId())) {
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
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
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
			case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
				getComponents().clear();
				getComponents().addAll((Collection<? extends SoftPkg>)newValue);
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
			case SdrPackage.SOFT_PKG_REGISTRY__COMPONENTS:
				return components != null && !components.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //SoftPkgRegistryImpl
