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
//BEGIN GENERATED CODE
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples <em>Number Of Samples</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getTime <em>Time</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getBulkIOType <em>Bulk IO Type</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getStreamSRI <em>Stream SRI</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel()
 * @model extendedMetaData="name='Model' kind='mixed'"
 * @generated
 */
public interface Model extends EObject {
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
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>Number Of Samples</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Number Of Samples</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Number Of Samples</em>' attribute.
	 * @see #isSetNumberOfSamples()
	 * @see #unsetNumberOfSamples()
	 * @see #setNumberOfSamples(long)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel_NumberOfSamples()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Long" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='numberOfSamples' namespace='##targetNamespace'"
	 * @generated
	 */
	long getNumberOfSamples();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples <em>Number Of Samples</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Number Of Samples</em>' attribute.
	 * @see #isSetNumberOfSamples()
	 * @see #unsetNumberOfSamples()
	 * @see #getNumberOfSamples()
	 * @generated
	 */
	void setNumberOfSamples(long value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples <em>Number Of Samples</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetNumberOfSamples()
	 * @see #getNumberOfSamples()
	 * @see #setNumberOfSamples(long)
	 * @generated
	 */
	void unsetNumberOfSamples();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getNumberOfSamples <em>Number Of Samples</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Number Of Samples</em>' attribute is set.
	 * @see #unsetNumberOfSamples()
	 * @see #getNumberOfSamples()
	 * @see #setNumberOfSamples(long)
	 * @generated
	 */
	boolean isSetNumberOfSamples();

	/**
	 * Returns the value of the '<em><b>Time</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Time</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Time</em>' containment reference.
	 * @see #setTime(Time)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel_Time()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='time' namespace='##targetNamespace'"
	 * @generated
	 */
	Time getTime();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getTime <em>Time</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Time</em>' containment reference.
	 * @see #getTime()
	 * @generated
	 */
	void setTime(Time value);

	/**
	 * Returns the value of the '<em><b>Bulk IO Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Bulk IO Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Bulk IO Type</em>' attribute.
	 * @see #setBulkIOType(String)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel_BulkIOType()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='BulkIOType' namespace='##targetNamespace'"
	 * @generated
	 */
	String getBulkIOType();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getBulkIOType <em>Bulk IO Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Bulk IO Type</em>' attribute.
	 * @see #getBulkIOType()
	 * @generated
	 */
	void setBulkIOType(String value);

	/**
	 * Returns the value of the '<em><b>Stream SRI</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stream SRI</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stream SRI</em>' containment reference.
	 * @see #setStreamSRI(SRI)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getModel_StreamSRI()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='StreamSRI' namespace='##targetNamespace'"
	 * @generated
	 */
	SRI getStreamSRI();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model#getStreamSRI <em>Stream SRI</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stream SRI</em>' containment reference.
	 * @see #getStreamSRI()
	 * @generated
	 */
	void setStreamSRI(SRI value);

} // Model
