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
package gov.redhawk.ide.internal.snapshot.metadata;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Value</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.Value#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.Value#getValue <em>Value</em>}</li>
 *   <li>{@link gov.redhawk.ide.internal.snapshot.metadata.Value#getJavaType <em>Java Type</em>}</li>
 * </ul>
 *
 * @see gov.redhawk.ide.internal.snapshot.metadata.SnapshotMetadataPackage#getValue()
 * @model extendedMetaData="name='Value' kind='mixed'"
 * @generated
 */
public interface Value extends EObject {
	/**
	 * Returns the value of the '<em><b>Mixed</b></em>' attribute list.
	 * The list contents are of type {@link org.eclipse.emf.ecore.util.FeatureMap.Entry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mixed</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mixed</em>' attribute list.
	 * @see gov.redhawk.ide.internal.snapshot.metadata.SnapshotMetadataPackage#getValue_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute.
	 * @see #setValue(String)
	 * @see gov.redhawk.ide.internal.snapshot.metadata.SnapshotMetadataPackage#getValue_Value()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='value' namespace='##targetNamespace'"
	 * @generated
	 */
	String getValue();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.internal.snapshot.metadata.Value#getValue <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Value</em>' attribute.
	 * @see #getValue()
	 * @generated
	 */
	void setValue(String value);

	/**
	 * Returns the value of the '<em><b>Java Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Java Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Java Type</em>' attribute.
	 * @see #setJavaType(String)
	 * @see gov.redhawk.ide.internal.snapshot.metadata.SnapshotMetadataPackage#getValue_JavaType()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='javaType' namespace='##targetNamespace'"
	 * @generated
	 */
	String getJavaType();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.internal.snapshot.metadata.Value#getJavaType <em>Java Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Java Type</em>' attribute.
	 * @see #getJavaType()
	 * @generated
	 */
	void setJavaType(String value);

} // Value
