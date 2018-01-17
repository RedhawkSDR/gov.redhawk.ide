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

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.ServicesContainer;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Services Container</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class ServicesContainerTest extends SoftPkgRegistryTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ServicesContainerTest.class);
	}

	private SdrRoot sdrRoot;

	/**
	 * Constructs a new Services Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ServicesContainerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Services Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected ServicesContainer getFixture() {
		return (ServicesContainer) fixture;
	}

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
		setFixture(this.sdrRoot.getServicesContainer());
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
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:09dfff50-60e9-4e3d-9c3b-dd3c13b12f35"));

		// Namespaced device
		getFixture().getSoftPkg("DCE:cc1b5f19-b14a-42b4-84db-aa99d14ba488");
	}

	@Override
	public void testGetAllComponents() {
		Assert.assertEquals(2, getFixture().getAllComponents().size());
	}

	public void testServicesContainer() {
		Assert.assertNull(getFixture().getName());
		Assert.assertEquals(1, getFixture().getComponents().size());
		Assert.assertEquals(1, getFixture().getChildContainers().size());

		ServicesContainer childContainer = getFixture().getChildContainers().get(0);
		Assert.assertEquals("name", childContainer.getName());
		Assert.assertEquals(0, childContainer.getComponents().size());
		Assert.assertEquals(1, childContainer.getChildContainers().size());

		childContainer = childContainer.getChildContainers().get(0);
		Assert.assertEquals("space", childContainer.getName());
		Assert.assertEquals(1, childContainer.getComponents().size());
		Assert.assertEquals(0, childContainer.getChildContainers().size());
	}

} //ServicesContainerTest
