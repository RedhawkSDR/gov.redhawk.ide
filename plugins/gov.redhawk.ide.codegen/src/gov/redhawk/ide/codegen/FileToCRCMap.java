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
package gov.redhawk.ide.codegen;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>File To CRC Map</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.FileToCRCMap#getCrc <em>Crc</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.FileToCRCMap#getFile <em>File</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.codegen.CodegenPackage#getFileToCRCMap()
 * @model
 * @generated
 */
public interface FileToCRCMap extends EObject {

	/**
	 * Returns the value of the '<em><b>Crc</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Crc</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Crc</em>' attribute.
	 * @see #setCrc(Long)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getFileToCRCMap_Crc()
	 * @model
	 * @generated
	 */
	Long getCrc();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.FileToCRCMap#getCrc <em>Crc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Crc</em>' attribute.
	 * @see #getCrc()
	 * @generated
	 */
	void setCrc(Long value);

	/**
	 * Returns the value of the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File</em>' attribute.
	 * @see #setFile(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getFileToCRCMap_File()
	 * @model
	 * @generated
	 */
	String getFile();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.FileToCRCMap#getFile <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File</em>' attribute.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(String value);

} // FileToCRCMap
