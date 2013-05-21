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
package gov.redhawk.ide.sdr.tests;

import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.framework.Assert;
import junit.textui.TestRunner;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Devices Container</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class DevicesContainerTest extends SoftPkgRegistryTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(DevicesContainerTest.class);
	}

	/**
	 * Constructs a new Devices Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DevicesContainerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Devices Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected DevicesContainer getFixture() {
		return (DevicesContainer)fixture;
	}

	private SdrRoot sdrRoot;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		this.sdrRoot = SdrTests.getSdrTestsSdrRoot();
		this.sdrRoot.load(null);
		setFixture(this.sdrRoot.getDevicesContainer());
		Assert.assertNotNull(this.fixture);
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

	public void testDevicesContainer() {
		final EList<SoftPkg> devices = getFixture().getComponents();
		Assert.assertEquals(1, devices.size());
		for (final SoftPkg softPkg : devices) {
			Assert.assertNotNull(softPkg);
		}
		Assert.assertEquals(this.sdrRoot, getFixture().getSdrRoot());
	}

} //DevicesContainerTest
