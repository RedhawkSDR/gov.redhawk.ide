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
import org.omg.CosNaming.NamingContext;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Name To Naming Context Entry</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class NameToNamingContextEntryTest extends TestCase {

	/**
	 * The fixture for this Name To Naming Context Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Entry<Name, NamingContext> fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(NameToNamingContextEntryTest.class);
	}

	/**
	 * Constructs a new Name To Naming Context Entry test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NameToNamingContextEntryTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Name To Naming Context Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Entry<Name, NamingContext> fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Name To Naming Context Entry test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Entry<Name, NamingContext> getFixture() {
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
		setFixture((Entry<Name, NamingContext>)ScaDebugFactory.eINSTANCE.create(ScaDebugPackage.Literals.NAME_TO_NAMING_CONTEXT_ENTRY));
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
} //NameToNamingContextEntryTest
