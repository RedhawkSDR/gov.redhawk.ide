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
import gov.redhawk.ide.codegen.CodegenPackage;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Impl Id To Settings Map</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ImplIdToSettingsMapTest extends TestCase {

	/**
	 * The fixture for this Impl Id To Settings Map test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected Map.Entry<String, ImplementationSettings> fixture = null;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ImplIdToSettingsMapTest.class);
	}

	/**
	 * Constructs a new Impl Id To Settings Map test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ImplIdToSettingsMapTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Impl Id To Settings Map test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(Map.Entry<String, ImplementationSettings> fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Impl Id To Settings Map test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected Map.Entry<String, ImplementationSettings> getFixture() {
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
	@SuppressWarnings("unchecked")
	protected void setUp() throws Exception {
		setFixture((Map.Entry<String, ImplementationSettings>) CodegenFactory.eINSTANCE.create(CodegenPackage.Literals.IMPL_ID_TO_SETTINGS_MAP));
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
		this.resourceSet = null;
		this.wave = null;
	}

	public void test_parse() throws Exception {
		Assert.assertNotNull(this.wave);
		Assert.assertNotNull(this.wave.getImplSettings());

		final EMap<String, ImplementationSettings> implMap = this.wave.getImplSettings();

		final Iterator<Entry<String, ImplementationSettings>> iter = implMap.iterator();

		while (iter.hasNext()) {
			final Entry<String, ImplementationSettings> tempEntry = iter.next();

			Assert.assertNotNull(tempEntry);
			Assert.assertNotNull(tempEntry.getKey());
			Assert.assertNotNull(tempEntry.getValue());

			Assert.assertEquals(tempEntry.getValue() instanceof ImplementationSettings, true);
			Assert.assertEquals(tempEntry.getKey() instanceof String, true);
		}

	}

} //ImplIdToSettingsMapTest
