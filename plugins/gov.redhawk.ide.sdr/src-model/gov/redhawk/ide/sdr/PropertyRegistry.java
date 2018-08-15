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
package gov.redhawk.ide.sdr;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EObject;

import mil.jpeojtrs.sca.prf.AbstractProperty;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Property Registry</b></em>'.
 * @since 8.0
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 * <li>{@link gov.redhawk.ide.sdr.PropertyRegistry#getProperties <em>Properties</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getPropertyRegistry()
 * @model abstract="true"
 * @generated
 */
public interface PropertyRegistry extends EObject {
	/**
	 * Returns the value of the '<em><b>Properties</b></em>' map.
	 * The key is of type {@link java.lang.String},
	 * and the value is of type {@link mil.jpeojtrs.sca.prf.AbstractProperty},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' map.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getPropertyRegistry_Properties()
	 * @model mapType="gov.redhawk.ide.sdr.StringToAbstractProperty&lt;mil.jpeojtrs.sca.prf.DceUUID,
	 * mil.jpeojtrs.sca.prf.AbstractProperty&gt;"
	 * @generated
	 */
	EMap<String, AbstractProperty> getProperties();

} // PropertyRegistry
