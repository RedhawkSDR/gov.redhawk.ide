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
 * A representation of the model object '<em><b>Port Rep To Generator Map</b></em>'.
 * @since 7.0
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getGenerator <em>Generator</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getRepId <em>Rep Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.codegen.CodegenPackage#getPortRepToGeneratorMap()
 * @model
 * @generated
 */
public interface PortRepToGeneratorMap extends EObject {

	/**
	 * Returns the value of the '<em><b>Generator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generator</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generator</em>' attribute.
	 * @see #setGenerator(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getPortRepToGeneratorMap_Generator()
	 * @model
	 * @generated
	 */
	String getGenerator();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getGenerator <em>Generator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generator</em>' attribute.
	 * @see #getGenerator()
	 * @generated
	 */
	void setGenerator(String value);

	/**
	 * Returns the value of the '<em><b>Rep Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rep Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rep Id</em>' attribute.
	 * @see #setRepId(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getPortRepToGeneratorMap_RepId()
	 * @model
	 * @generated
	 */
	String getRepId();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.PortRepToGeneratorMap#getRepId <em>Rep Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rep Id</em>' attribute.
	 * @see #getRepId()
	 * @generated
	 */
	void setRepId(String value);

} // PortRepToGeneratorMap
