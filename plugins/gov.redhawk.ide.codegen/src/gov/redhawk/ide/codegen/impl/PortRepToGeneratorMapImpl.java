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
import gov.redhawk.ide.codegen.PortRepToGeneratorMap;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Port Rep To Generator Map</b></em>'.
 * @since 7.0
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl#getGenerator <em>Generator</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.impl.PortRepToGeneratorMapImpl#getRepId <em>Rep Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PortRepToGeneratorMapImpl extends EObjectImpl implements PortRepToGeneratorMap {

	/**
	 * The default value of the '{@link #getGenerator() <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGenerator()
	 * @generated
	 * @ordered
	 */
	protected static final String GENERATOR_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getGenerator() <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGenerator()
	 * @generated
	 * @ordered
	 */
	protected String generator = GENERATOR_EDEFAULT;
	/**
	 * The default value of the '{@link #getRepId() <em>Rep Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRepId()
	 * @generated
	 * @ordered
	 */
	protected static final String REP_ID_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getRepId() <em>Rep Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRepId()
	 * @generated
	 * @ordered
	 */
	protected String repId = REP_ID_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected PortRepToGeneratorMapImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return CodegenPackage.Literals.PORT_REP_TO_GENERATOR_MAP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGenerator() {
		return generator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGenerator(String newGenerator) {
		String oldGenerator = generator;
		generator = newGenerator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.PORT_REP_TO_GENERATOR_MAP__GENERATOR, oldGenerator, generator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRepId() {
		return repId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRepId(String newRepId) {
		String oldRepId = repId;
		repId = newRepId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, CodegenPackage.PORT_REP_TO_GENERATOR_MAP__REP_ID, oldRepId, repId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__GENERATOR:
				return getGenerator();
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__REP_ID:
				return getRepId();
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
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__GENERATOR:
				setGenerator((String)newValue);
				return;
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__REP_ID:
				setRepId((String)newValue);
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
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__GENERATOR:
				setGenerator(GENERATOR_EDEFAULT);
				return;
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__REP_ID:
				setRepId(REP_ID_EDEFAULT);
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
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__GENERATOR:
				return GENERATOR_EDEFAULT == null ? generator != null : !GENERATOR_EDEFAULT.equals(generator);
			case CodegenPackage.PORT_REP_TO_GENERATOR_MAP__REP_ID:
				return REP_ID_EDEFAULT == null ? repId != null : !REP_ID_EDEFAULT.equals(repId);
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
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (generator: ");
		result.append(generator);
		result.append(", repId: ");
		result.append(repId);
		result.append(')');
		return result.toString();
	}

} //PortRepToGeneratorMapImpl
