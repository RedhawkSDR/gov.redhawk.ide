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
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Keywords Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType#getCFDataType <em>CF Data Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getKeywordsType()
 * @model extendedMetaData="name='keywords_._type' kind='mixed'"
 * @generated
 */
public interface KeywordsType extends EObject {
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
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getKeywordsType_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>CF Data Type</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>CF Data Type</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>CF Data Type</em>' containment reference list.
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getKeywordsType_CFDataType()
	 * @model containment="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='CFDataType' namespace='##targetNamespace'"
	 * @generated
	 */
	EList<CFDataType> getCFDataType();

} // KeywordsType
