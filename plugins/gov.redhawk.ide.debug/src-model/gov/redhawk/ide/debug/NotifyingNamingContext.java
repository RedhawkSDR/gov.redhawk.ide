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
package gov.redhawk.ide.debug;

import gov.redhawk.model.sca.IDisposable;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.URI;
import org.jacorb.naming.Name;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtOperations;
import org.omg.PortableServer.POA;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Notifying Naming Context</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getObjectMap <em>Object Map</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getContextMap <em>Context Map</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getSubContexts <em>Sub Contexts</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext <em>Parent Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getPoa <em>Poa</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext()
 * @model superTypes="gov.redhawk.ide.debug.NamingContextExtOperations gov.redhawk.model.sca.IDisposable"
 * @generated
 */
public interface NotifyingNamingContext extends NamingContextExtOperations, IDisposable {

	/**
	 * Returns the value of the '<em><b>Object Map</b></em>' map.
	 * The key is of type {@link org.jacorb.naming.Name},
	 * and the value is of type {@link org.omg.CORBA.Object},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Object Map</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Object Map</em>' map.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_ObjectMap()
	 * @model mapType="gov.redhawk.ide.debug.NameToObjectEntry<gov.redhawk.ide.debug.Name, gov.redhawk.model.sca.Object>" transient="true"
	 * @generated
	 */
	EMap<Name, org.omg.CORBA.Object> getObjectMap();

	/**
	 * Returns the value of the '<em><b>Context Map</b></em>' map.
	 * The key is of type {@link org.jacorb.naming.Name},
	 * and the value is of type {@link org.omg.CosNaming.NamingContext},
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Context Map</em>' map isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Context Map</em>' map.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_ContextMap()
	 * @model mapType="gov.redhawk.ide.debug.NameToNamingContextEntry<gov.redhawk.ide.debug.Name, gov.redhawk.ide.debug.NamingContext>" transient="true"
	 * @generated
	 */
	EMap<Name, NamingContext> getContextMap();

	/**
	 * Returns the value of the '<em><b>Naming Context</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Naming Context</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Naming Context</em>' attribute.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_NamingContext()
	 * @model dataType="mil.jpeojtrs.sca.cf.NamingContextExt" required="true" transient="true" suppressedSetVisibility="true" suppressedIsSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	NamingContextExt getNamingContext();

	/**
	 * Returns the value of the '<em><b>Sub Contexts</b></em>' containment reference list.
	 * The list contents are of type {@link gov.redhawk.ide.debug.NotifyingNamingContext}.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext <em>Parent Context</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sub Contexts</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sub Contexts</em>' containment reference list.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_SubContexts()
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext
	 * @model opposite="parentContext" containment="true"
	 * @generated
	 */
	EList<NotifyingNamingContext> getSubContexts();

	/**
	 * Returns the value of the '<em><b>Parent Context</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getSubContexts <em>Sub Contexts</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Context</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Context</em>' container reference.
	 * @see #setParentContext(NotifyingNamingContext)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_ParentContext()
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getSubContexts
	 * @model opposite="subContexts" transient="false"
	 * @generated
	 */
	NotifyingNamingContext getParentContext();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getParentContext <em>Parent Context</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Context</em>' container reference.
	 * @see #getParentContext()
	 * @generated
	 */
	void setParentContext(NotifyingNamingContext value);

	/**
	 * Returns the value of the '<em><b>Poa</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Poa</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Poa</em>' attribute.
	 * @see #setPoa(POA)
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_Poa()
	 * @model dataType="gov.redhawk.model.sca.POA" required="true" transient="true"
	 * @generated
	 */
	POA getPoa();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getPoa <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Poa</em>' attribute.
	 * @see #getPoa()
	 * @generated
	 */
	void setPoa(POA value);

	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see gov.redhawk.ide.debug.ScaDebugPackage#getNotifyingNamingContext_Name()
	 * @model transient="true" changeable="false" volatile="true" derived="true"
	 * @generated
	 */
	String getName();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="gov.redhawk.ide.debug.NameComponentArray" uriDataType="mil.jpeojtrs.sca.spd.URI"
	 * @generated
	 */
	NameComponent[] getName(URI uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model dataType="mil.jpeojtrs.sca.spd.URI" nameDataType="gov.redhawk.ide.debug.NameComponentArray"
	 * @generated
	 */
	URI getURI(NameComponent[] name);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	String getFullName();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model uriDataType="mil.jpeojtrs.sca.spd.URI"
	 * @generated
	 */
	NotifyingNamingContext getResourceContext(URI uri);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model contextDataType="gov.redhawk.ide.debug.NamingContext"
	 * @generated
	 */
	NotifyingNamingContext findContext(NamingContext context);

} // NotifyingNamingContext
