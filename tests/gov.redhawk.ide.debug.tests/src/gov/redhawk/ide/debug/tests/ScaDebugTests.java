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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.debug.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test suite for the '<em><b>debug</b></em>' package.
 * <!-- end-user-doc -->
 * @generated
 */
public class ScaDebugTests extends TestSuite {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(suite());
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static Test suite() {
		TestSuite suite = new ScaDebugTests("debug Tests");
		suite.addTestSuite(LocalScaTest.class);
		suite.addTestSuite(NotifyingNamingContextTest.class);
		suite.addTestSuite(LocalFileManagerTest.class);
		suite.addTestSuite(LocalScaWaveformTest.class);
		suite.addTestSuite(LocalScaComponentTest.class);
		suite.addTestSuite(LocalScaDeviceManagerTest.class);
		suite.addTestSuite(LocalScaExecutableDeviceTest.class);
		suite.addTestSuite(LocalScaLoadableDeviceTest.class);
		suite.addTestSuite(LocalScaDeviceTest.class);
		suite.addTestSuite(LocalScaServiceTest.class);
		return suite;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ScaDebugTests(String name) {
		super(name);
	}

} //ScaDebugTests
