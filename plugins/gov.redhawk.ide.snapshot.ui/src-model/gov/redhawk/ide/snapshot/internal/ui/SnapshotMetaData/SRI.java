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
 * A representation of the model object '<em><b>SRI</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMixed <em>Mixed</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion <em>Hversion</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart <em>Xstart</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta <em>Xdelta</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits <em>Xunits</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize <em>Subsize</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart <em>Ystart</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta <em>Ydelta</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits <em>Yunits</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode <em>Mode</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getStreamID <em>Stream ID</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking <em>Blocking</em>}</li>
 *   <li>{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getKeywords <em>Keywords</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI()
 * @model extendedMetaData="name='SRI' kind='mixed'"
 * @generated
 */
public interface SRI extends EObject {
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
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Mixed()
	 * @model unique="false" dataType="org.eclipse.emf.ecore.EFeatureMapEntry" many="true"
	 *        extendedMetaData="kind='elementWildcard' name=':mixed'"
	 * @generated
	 */
	FeatureMap getMixed();

	/**
	 * Returns the value of the '<em><b>Hversion</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Hversion</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Hversion</em>' attribute.
	 * @see #isSetHversion()
	 * @see #unsetHversion()
	 * @see #setHversion(int)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Hversion()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Int" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='hversion' namespace='##targetNamespace'"
	 * @generated
	 */
	int getHversion();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion <em>Hversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Hversion</em>' attribute.
	 * @see #isSetHversion()
	 * @see #unsetHversion()
	 * @see #getHversion()
	 * @generated
	 */
	void setHversion(int value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion <em>Hversion</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetHversion()
	 * @see #getHversion()
	 * @see #setHversion(int)
	 * @generated
	 */
	void unsetHversion();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getHversion <em>Hversion</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Hversion</em>' attribute is set.
	 * @see #unsetHversion()
	 * @see #getHversion()
	 * @see #setHversion(int)
	 * @generated
	 */
	boolean isSetHversion();

	/**
	 * Returns the value of the '<em><b>Xstart</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Xstart</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Xstart</em>' attribute.
	 * @see #isSetXstart()
	 * @see #unsetXstart()
	 * @see #setXstart(double)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Xstart()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='xstart' namespace='##targetNamespace'"
	 * @generated
	 */
	double getXstart();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart <em>Xstart</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Xstart</em>' attribute.
	 * @see #isSetXstart()
	 * @see #unsetXstart()
	 * @see #getXstart()
	 * @generated
	 */
	void setXstart(double value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart <em>Xstart</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetXstart()
	 * @see #getXstart()
	 * @see #setXstart(double)
	 * @generated
	 */
	void unsetXstart();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXstart <em>Xstart</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Xstart</em>' attribute is set.
	 * @see #unsetXstart()
	 * @see #getXstart()
	 * @see #setXstart(double)
	 * @generated
	 */
	boolean isSetXstart();

	/**
	 * Returns the value of the '<em><b>Xdelta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Xdelta</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Xdelta</em>' attribute.
	 * @see #isSetXdelta()
	 * @see #unsetXdelta()
	 * @see #setXdelta(double)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Xdelta()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='xdelta' namespace='##targetNamespace'"
	 * @generated
	 */
	double getXdelta();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta <em>Xdelta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Xdelta</em>' attribute.
	 * @see #isSetXdelta()
	 * @see #unsetXdelta()
	 * @see #getXdelta()
	 * @generated
	 */
	void setXdelta(double value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta <em>Xdelta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetXdelta()
	 * @see #getXdelta()
	 * @see #setXdelta(double)
	 * @generated
	 */
	void unsetXdelta();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXdelta <em>Xdelta</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Xdelta</em>' attribute is set.
	 * @see #unsetXdelta()
	 * @see #getXdelta()
	 * @see #setXdelta(double)
	 * @generated
	 */
	boolean isSetXdelta();

	/**
	 * Returns the value of the '<em><b>Xunits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Xunits</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Xunits</em>' attribute.
	 * @see #isSetXunits()
	 * @see #unsetXunits()
	 * @see #setXunits(short)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Xunits()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Short" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='xunits' namespace='##targetNamespace'"
	 * @generated
	 */
	short getXunits();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits <em>Xunits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Xunits</em>' attribute.
	 * @see #isSetXunits()
	 * @see #unsetXunits()
	 * @see #getXunits()
	 * @generated
	 */
	void setXunits(short value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits <em>Xunits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetXunits()
	 * @see #getXunits()
	 * @see #setXunits(short)
	 * @generated
	 */
	void unsetXunits();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getXunits <em>Xunits</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Xunits</em>' attribute is set.
	 * @see #unsetXunits()
	 * @see #getXunits()
	 * @see #setXunits(short)
	 * @generated
	 */
	boolean isSetXunits();

	/**
	 * Returns the value of the '<em><b>Subsize</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Subsize</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Subsize</em>' attribute.
	 * @see #isSetSubsize()
	 * @see #unsetSubsize()
	 * @see #setSubsize(double)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Subsize()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='subsize' namespace='##targetNamespace'"
	 * @generated
	 */
	double getSubsize();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize <em>Subsize</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Subsize</em>' attribute.
	 * @see #isSetSubsize()
	 * @see #unsetSubsize()
	 * @see #getSubsize()
	 * @generated
	 */
	void setSubsize(double value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize <em>Subsize</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetSubsize()
	 * @see #getSubsize()
	 * @see #setSubsize(double)
	 * @generated
	 */
	void unsetSubsize();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getSubsize <em>Subsize</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Subsize</em>' attribute is set.
	 * @see #unsetSubsize()
	 * @see #getSubsize()
	 * @see #setSubsize(double)
	 * @generated
	 */
	boolean isSetSubsize();

	/**
	 * Returns the value of the '<em><b>Ystart</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ystart</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ystart</em>' attribute.
	 * @see #isSetYstart()
	 * @see #unsetYstart()
	 * @see #setYstart(double)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Ystart()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ystart' namespace='##targetNamespace'"
	 * @generated
	 */
	double getYstart();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart <em>Ystart</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ystart</em>' attribute.
	 * @see #isSetYstart()
	 * @see #unsetYstart()
	 * @see #getYstart()
	 * @generated
	 */
	void setYstart(double value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart <em>Ystart</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetYstart()
	 * @see #getYstart()
	 * @see #setYstart(double)
	 * @generated
	 */
	void unsetYstart();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYstart <em>Ystart</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Ystart</em>' attribute is set.
	 * @see #unsetYstart()
	 * @see #getYstart()
	 * @see #setYstart(double)
	 * @generated
	 */
	boolean isSetYstart();

	/**
	 * Returns the value of the '<em><b>Ydelta</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Ydelta</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Ydelta</em>' attribute.
	 * @see #isSetYdelta()
	 * @see #unsetYdelta()
	 * @see #setYdelta(double)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Ydelta()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Double" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='ydelta' namespace='##targetNamespace'"
	 * @generated
	 */
	double getYdelta();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta <em>Ydelta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Ydelta</em>' attribute.
	 * @see #isSetYdelta()
	 * @see #unsetYdelta()
	 * @see #getYdelta()
	 * @generated
	 */
	void setYdelta(double value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta <em>Ydelta</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetYdelta()
	 * @see #getYdelta()
	 * @see #setYdelta(double)
	 * @generated
	 */
	void unsetYdelta();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYdelta <em>Ydelta</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Ydelta</em>' attribute is set.
	 * @see #unsetYdelta()
	 * @see #getYdelta()
	 * @see #setYdelta(double)
	 * @generated
	 */
	boolean isSetYdelta();

	/**
	 * Returns the value of the '<em><b>Yunits</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Yunits</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Yunits</em>' attribute.
	 * @see #isSetYunits()
	 * @see #unsetYunits()
	 * @see #setYunits(short)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Yunits()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Short" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='yunits' namespace='##targetNamespace'"
	 * @generated
	 */
	short getYunits();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits <em>Yunits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Yunits</em>' attribute.
	 * @see #isSetYunits()
	 * @see #unsetYunits()
	 * @see #getYunits()
	 * @generated
	 */
	void setYunits(short value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits <em>Yunits</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetYunits()
	 * @see #getYunits()
	 * @see #setYunits(short)
	 * @generated
	 */
	void unsetYunits();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getYunits <em>Yunits</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Yunits</em>' attribute is set.
	 * @see #unsetYunits()
	 * @see #getYunits()
	 * @see #setYunits(short)
	 * @generated
	 */
	boolean isSetYunits();

	/**
	 * Returns the value of the '<em><b>Mode</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mode</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mode</em>' attribute.
	 * @see #isSetMode()
	 * @see #unsetMode()
	 * @see #setMode(short)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Mode()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Short" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='mode' namespace='##targetNamespace'"
	 * @generated
	 */
	short getMode();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mode</em>' attribute.
	 * @see #isSetMode()
	 * @see #unsetMode()
	 * @see #getMode()
	 * @generated
	 */
	void setMode(short value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode <em>Mode</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetMode()
	 * @see #getMode()
	 * @see #setMode(short)
	 * @generated
	 */
	void unsetMode();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getMode <em>Mode</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Mode</em>' attribute is set.
	 * @see #unsetMode()
	 * @see #getMode()
	 * @see #setMode(short)
	 * @generated
	 */
	boolean isSetMode();

	/**
	 * Returns the value of the '<em><b>Stream ID</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Stream ID</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Stream ID</em>' attribute.
	 * @see #setStreamID(String)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_StreamID()
	 * @model dataType="org.eclipse.emf.ecore.xml.type.String" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='streamID' namespace='##targetNamespace'"
	 * @generated
	 */
	String getStreamID();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getStreamID <em>Stream ID</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Stream ID</em>' attribute.
	 * @see #getStreamID()
	 * @generated
	 */
	void setStreamID(String value);

	/**
	 * Returns the value of the '<em><b>Blocking</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Blocking</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Blocking</em>' attribute.
	 * @see #isSetBlocking()
	 * @see #unsetBlocking()
	 * @see #setBlocking(boolean)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Blocking()
	 * @model unsettable="true" dataType="org.eclipse.emf.ecore.xml.type.Boolean" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='blocking' namespace='##targetNamespace'"
	 * @generated
	 */
	boolean isBlocking();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking <em>Blocking</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Blocking</em>' attribute.
	 * @see #isSetBlocking()
	 * @see #unsetBlocking()
	 * @see #isBlocking()
	 * @generated
	 */
	void setBlocking(boolean value);

	/**
	 * Unsets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking <em>Blocking</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isSetBlocking()
	 * @see #isBlocking()
	 * @see #setBlocking(boolean)
	 * @generated
	 */
	void unsetBlocking();

	/**
	 * Returns whether the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#isBlocking <em>Blocking</em>}' attribute is set.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return whether the value of the '<em>Blocking</em>' attribute is set.
	 * @see #unsetBlocking()
	 * @see #isBlocking()
	 * @see #setBlocking(boolean)
	 * @generated
	 */
	boolean isSetBlocking();

	/**
	 * Returns the value of the '<em><b>Keywords</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Keywords</em>' containment reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Keywords</em>' containment reference.
	 * @see #setKeywords(KeywordsType)
	 * @see gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage#getSRI_Keywords()
	 * @model containment="true" required="true" transient="true" volatile="true" derived="true"
	 *        extendedMetaData="kind='element' name='keywords' namespace='##targetNamespace'"
	 * @generated
	 */
	KeywordsType getKeywords();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI#getKeywords <em>Keywords</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Keywords</em>' containment reference.
	 * @see #getKeywords()
	 * @generated
	 */
	void setKeywords(KeywordsType value);

} // SRI
