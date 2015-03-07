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
package gov.redhawk.ide.sdr;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Shared Libraries Container</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SharedLibrariesContainer#getSdrRoot <em>Sdr Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sdr.SdrPackage#getSharedLibrariesContainer()
 * @model
 * @generated
 */
public interface SharedLibrariesContainer extends SoftPkgRegistry {
	/**
	 * Returns the value of the '<em><b>Sdr Root</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gov.redhawk.ide.sdr.SdrRoot#getSharedLibrariesContainer <em>Shared Libraries Container</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Sdr Root</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Sdr Root</em>' container reference.
	 * @see gov.redhawk.ide.sdr.SdrPackage#getSharedLibrariesContainer_SdrRoot()
	 * @see gov.redhawk.ide.sdr.SdrRoot#getSharedLibrariesContainer
	 * @model opposite="sharedLibrariesContainer" suppressedSetVisibility="true" suppressedUnsetVisibility="true"
	 * @generated
	 */
	SdrRoot getSdrRoot();

} // SharedLibrariesContainer
