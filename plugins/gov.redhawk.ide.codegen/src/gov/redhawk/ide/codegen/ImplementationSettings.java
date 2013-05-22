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

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '
 * <em><b>Implementation Settings</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getName <em>Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getOutputDir <em>Output Dir</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getTemplate <em>Template</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getProperties <em>Properties</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratorId <em>Generator Id</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedOn <em>Generated On</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getId <em>Id</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedFileCRCs <em>Generated File CR Cs</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#getPortGenerators <em>Port Generators</em>}</li>
 *   <li>{@link gov.redhawk.ide.codegen.ImplementationSettings#isPrimary <em>Primary</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings()
 * @model
 * @generated
 */
public interface ImplementationSettings extends EObject {

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * @deprecated
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_Name()
	 * @model
	 * @generated
	 */
	@Deprecated
	String getName();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * @deprecated
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	@Deprecated
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Output Dir</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Output Dir</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Output Dir</em>' attribute.
	 * @see #setOutputDir(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_OutputDir()
	 * @model
	 * @generated
	 */
	String getOutputDir();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#getOutputDir <em>Output Dir</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Output Dir</em>' attribute.
	 * @see #getOutputDir()
	 * @generated
	 */
	void setOutputDir(String value);

	/**
	 * Returns the value of the '<em><b>Template</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Template</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Template</em>' attribute.
	 * @see #setTemplate(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_Template()
	 * @model
	 * @generated
	 */
	String getTemplate();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#getTemplate <em>Template</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Template</em>' attribute.
	 * @see #getTemplate()
	 * @generated
	 */
	void setTemplate(String value);

	/**
	 * Returns the value of the '<em><b>Properties</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.codegen.Property}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Properties</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Properties</em>' containment reference list.
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_Properties()
	 * @model containment="true"
	 * @generated
	 */
	EList<Property> getProperties();

	/**
	 * Returns the value of the '<em><b>Generator Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generator Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generator Id</em>' attribute.
	 * @see #setGeneratorId(String)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_GeneratorId()
	 * @model
	 * @generated
	 */
	String getGeneratorId();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratorId <em>Generator Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generator Id</em>' attribute.
	 * @see #getGeneratorId()
	 * @generated
	 */
	void setGeneratorId(String value);

	/**
	 * Returns the value of the '<em><b>Generated On</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generated On</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generated On</em>' attribute.
	 * @see #setGeneratedOn(Date)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_GeneratedOn()
	 * @model
	 * @generated
	 */
	Date getGeneratedOn();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#getGeneratedOn <em>Generated On</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Generated On</em>' attribute.
	 * @see #getGeneratedOn()
	 * @generated
	 */
	void setGeneratedOn(Date value);

	/**
	 * Returns the value of the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Id</em>' attribute.
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_Id()
	 * @model id="true" transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	String getId();

	/**
	 * Returns the value of the '<em><b>Generated File CR Cs</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.codegen.FileToCRCMap}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Generated File CR Cs</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Generated File CR Cs</em>' containment reference list.
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_GeneratedFileCRCs()
	 * @model containment="true"
	 * @generated
	 */
	EList<FileToCRCMap> getGeneratedFileCRCs();

	/**
	 * Returns the value of the '<em><b>Port Generators</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.codegen.PortRepToGeneratorMap}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Port Generators</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Port Generators</em>' containment reference list.
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_PortGenerators()
	 * @model containment="true"
	 * @generated
	 */
	EList<PortRepToGeneratorMap> getPortGenerators();

	/**
	 * Returns the value of the '<em><b>Primary</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Primary</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Primary</em>' attribute.
	 * @see #setPrimary(boolean)
	 * @see gov.redhawk.ide.codegen.CodegenPackage#getImplementationSettings_Primary()
	 * @model default="false"
	 * @generated
	 */
	boolean isPrimary();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.codegen.ImplementationSettings#isPrimary <em>Primary</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Primary</em>' attribute.
	 * @see #isPrimary()
	 * @generated
	 */
	void setPrimary(boolean value);

} // ImplementationSettings
