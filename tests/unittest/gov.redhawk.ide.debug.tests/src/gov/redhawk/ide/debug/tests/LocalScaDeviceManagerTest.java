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
package gov.redhawk.ide.debug.tests;

import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugFactory;
import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Device Manager</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class LocalScaDeviceManagerTest extends TestCase {

	/**
	 * The fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaDeviceManager fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaDeviceManagerTest.class);
	}

	/**
	 * Constructs a new Local Sca Device Manager test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaDeviceManagerTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalScaDeviceManager fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaDeviceManager getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ScaDebugFactory.eINSTANCE.createLocalScaDeviceManager());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String)
	 * @generated
	 */
	public void testLaunch__String_DataType_URI_String_String() {
		// PASS - This would be difficult/impossible to test in a unit test environment
	}

	public void testStub() {
		// stubTest
	}

} //LocalScaDeviceManagerTest
