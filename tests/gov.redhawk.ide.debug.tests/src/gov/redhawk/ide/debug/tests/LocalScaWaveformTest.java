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

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.ide.debug.internal.cf.extended.impl.ApplicationImpl;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;

import CF.DataType;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.LifeCyclePackage.ReleaseError;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Waveform</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
@SuppressWarnings("restriction")
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
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		ScaDebugInstance.INSTANCE.init(null);
		setFixture(ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform());
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
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String)
	 * @generated
	 */
	public void testLaunch__String_DataType_URI_String_String() throws CoreException {
		try {
			getFixture().launch(null, null, null, null, null);
			fail();
		} catch (AssertionFailedException e) {
			
		}
		// TODO Add more tests
	}

	/**
	 * Tests the '{@link ExtendedCF.ApplicationExtOperations#launch(java.lang.String, CF.DataType[], java.lang.String, java.lang.String, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws ExecuteFail 
	 * @see ExtendedCF.ApplicationExtOperations#launch(java.lang.String, CF.DataType[], java.lang.String, java.lang.String, java.lang.String)
	 * @generated NOT
	 */
	public void testLaunch__String_DataType_String_String_String() throws ExecuteFail {
		// TODO Add more tests
	}

	/**
	 * Tests the '{@link ExtendedCF.ApplicationExtOperations#reset(java.lang.String) <em>Reset</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws ExecuteFail 
	 * @throws ReleaseError 
	 * @see ExtendedCF.ApplicationExtOperations#reset(java.lang.String)
	 * @generated NOT
	 */
	public void testReset__String() throws ReleaseError, ExecuteFail {
		try {
			((ApplicationImpl) getFixture().getLocalApp()).reset(null);
			fail();
		} catch (ReleaseError e) {
			
		}
		// TODO Add more tests
		try {
			((ApplicationImpl) getFixture().getLocalApp()).reset("");
			fail();
		} catch (ReleaseError e) {
			
		}
		
		// TODO Add more tests
	}


} //LocalScaWaveformTest
