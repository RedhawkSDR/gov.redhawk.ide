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

import gov.redhawk.ide.sdr.ComponentsContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc --> A test case for the model object '
 * <em><b>Components Container</b></em>'. <!-- end-user-doc -->
 * @generated
 */
public class ComponentsContainerTest extends SoftPkgRegistryTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(ComponentsContainerTest.class);
	}

	/**
	 * Constructs a new Components Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ComponentsContainerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Components Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected ComponentsContainer getFixture() {
		return (ComponentsContainer) fixture;
	}

	private SdrRoot sdrRoot;

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		// END GENERATED CODE
		this.sdrRoot = SdrTestsUtil.getSdrTestsSdrRoot();
		setFixture(this.sdrRoot.getComponentsContainer());
		Assert.assertNotNull(fixture);
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

		// Components in root container (no namespace)
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:e0cfeb24-1039-4b4c-93cb-33c42008d64f"));
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:859ebb11-4767-4e8e-874a-101e6efb3440"));
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:38279be0-4650-40c4-9084-352e6ebeedeb"));

		// Namespaced component
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:c7dc1f48-16d3-11e5-9335-3417ebc4aab5"));
	}

	@Override
	public void testGetAllComponents() {
		Assert.assertEquals(6, getFixture().getAllComponents().size());
	}

	public void testComponentsContainer() {
		Assert.assertNull(getFixture().getName());
		Assert.assertEquals(5, getFixture().getComponents().size());
		Assert.assertEquals(1, getFixture().getChildContainers().size());

		ComponentsContainer childContainer = getFixture().getChildContainers().get(0);
		Assert.assertEquals("rh", childContainer.getName());
		Assert.assertEquals(1, childContainer.getComponents().size());
		Assert.assertEquals(0, childContainer.getChildContainers().size());
	}

} // ComponentsContainerTest
