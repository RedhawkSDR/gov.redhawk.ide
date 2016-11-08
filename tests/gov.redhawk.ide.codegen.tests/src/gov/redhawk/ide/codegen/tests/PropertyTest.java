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
package gov.redhawk.ide.codegen.tests;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.WaveDevSettings;
import org.junit.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Property</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class PropertyTest extends TestCase {

	/**
	 * The fixture for this Property test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected Property fixture = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(PropertyTest.class);
	}

	/**
	 * Constructs a new Property test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public PropertyTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Property test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Property fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Property test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Property getFixture() {
		return fixture;
	}

	private ResourceSet resourceSet;
	private WaveDevSettings wave;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(CodegenFactory.eINSTANCE.createProperty());
		this.resourceSet = new ResourceSetImpl();
		this.wave = (WaveDevSettings) this.resourceSet.getResource(CodegenTestSuite.getURI("testFiles/sample.waveDev"), true).getEObject("/");
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated NOT
	 */
	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
		this.wave = null;
		this.resourceSet = null;
	}

	public void test_parse() throws Exception {
		Assert.assertNotNull(this.wave);

		for (final ImplementationSettings implSettings : this.wave.getImplSettings().values()) {
			Assert.assertNotNull(implSettings);
			Assert.assertEquals("DCE:726800a4-8af7-47b0-ba37-77952254b144", implSettings.getId());
			Assert.assertNotNull(implSettings.getProperties());

			final Property prop1 = implSettings.getProperties().get(0);
			final Property prop2 = implSettings.getProperties().get(1);

			Assert.assertNotNull(prop1);
			Assert.assertNotNull(prop2);

			Assert.assertEquals("queued_ports", prop1.getId());
			Assert.assertEquals("true", prop1.getValue());

			Assert.assertEquals("memcpy_buffer", prop2.getId());
			Assert.assertEquals("true", prop2.getValue());

			break;
		}
	}

} //PropertyTest
