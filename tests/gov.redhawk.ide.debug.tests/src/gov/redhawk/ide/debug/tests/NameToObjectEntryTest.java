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

import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPackage;

import java.util.Map.Entry;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.jacorb.naming.Name;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Name To Object Entry</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class NameToObjectEntryTest extends TestCase {

	/**
	 * The fixture for this Name To Object Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Entry<Name, org.omg.CORBA.Object> fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(NameToObjectEntryTest.class);
	}

	/**
	 * Constructs a new Name To Object Entry test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NameToObjectEntryTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Name To Object Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Entry<Name, org.omg.CORBA.Object> fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Name To Object Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Entry<Name, org.omg.CORBA.Object> getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		setFixture((Entry<Name, org.omg.CORBA.Object>)ScaDebugFactory.eINSTANCE.create(ScaDebugPackage.Literals.NAME_TO_OBJECT_ENTRY));
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

	public void testPass() {
		
	}
} //NameToObjectEntryTest
