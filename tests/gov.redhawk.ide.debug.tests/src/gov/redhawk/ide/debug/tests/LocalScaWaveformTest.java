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

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugFactory;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Waveform</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String) <em>Launch</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaWaveform#setLocalApp(gov.redhawk.ide.debug.impl.ApplicationImpl, org.omg.PortableServer.POA) <em>Set Local App</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class LocalScaWaveformTest extends TestCase {

	/**
	 * The fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaWaveform fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaWaveformTest.class);
	}

	/**
	 * Constructs a new Local Sca Waveform test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaWaveformTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalScaWaveform fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaWaveform getFixture() {
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
		setFixture(ScaDebugFactory.eINSTANCE.createLocalScaWaveform());
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
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String)
	 * @generated NOT
	 */
	public void testLaunch__String_String_DataType_URI_String() {
		// END GENERATED CODE
		// TODO
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaWaveform#setLocalApp(gov.redhawk.ide.debug.impl.ApplicationImpl, org.omg.PortableServer.POA) <em>Set Local App</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#setLocalApp(gov.redhawk.ide.debug.impl.ApplicationImpl, org.omg.PortableServer.POA)
	 * @generated NOT
	 */
	public void testSetLocalApp__ApplicationImpl_POA() throws ServantNotActive, WrongPolicy {
		// END GENERATED CODE
		getFixture().setLocalApp(null, null);
		// BEGIN GENERATED CODE
	}

} //LocalScaWaveformTest
