/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.tests;

import org.junit.Assert;

import gov.redhawk.ide.sdr.DevicesContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.textui.TestRunner;

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
		return (DevicesContainer) fixture;
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
		// END GENERATED CODE
		this.sdrRoot = SdrTestsUtil.getSdrTestsSdrRoot();
		setFixture(this.sdrRoot.getDevicesContainer());
		Assert.assertNotNull(this.fixture);
		// BEGIN GENERATED CODE
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

	// END GENERATED CODE

	@Override
	public void testGetSoftPkg__String() {
		Assert.assertNull(getFixture().getSoftPkg(null));
		Assert.assertNull(getFixture().getSoftPkg("InvalidId"));

		// Device in root container (no namespace)
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:28412a77-28d4-40aa-8b63-c9c6c9eb6e4a"));

		// Namespaced device
		getFixture().getSoftPkg("DCE:59a7d548-1a3a-11e5-85d9-3417ebc4aab5");
	}

	@Override
	public void testGetAllComponents() {
		Assert.assertEquals(3, getFixture().getAllComponents().size());
	}

	public void testDevicesContainer() {
		Assert.assertNull(getFixture().getName());
		Assert.assertEquals(2, getFixture().getComponents().size());
		Assert.assertEquals(1, getFixture().getChildContainers().size());

		DevicesContainer childContainer = getFixture().getChildContainers().get(0);
		Assert.assertEquals("rh", childContainer.getName());
		Assert.assertEquals(1, childContainer.getComponents().size());
		Assert.assertEquals(0, childContainer.getChildContainers().size());
	}

} //DevicesContainerTest
