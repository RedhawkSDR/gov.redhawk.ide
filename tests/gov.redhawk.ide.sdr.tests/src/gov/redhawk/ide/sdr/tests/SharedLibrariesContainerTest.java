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
import gov.redhawk.ide.sdr.SharedLibrariesContainer;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Shared Libraries Container</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class SharedLibrariesContainerTest extends SoftPkgRegistryTest {

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(SharedLibrariesContainerTest.class);
	}

	private SdrRoot sdrRoot;

	/**
	 * Constructs a new Shared Libraries Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SharedLibrariesContainerTest(String name) {
		super(name);
	}

	/**
	 * Returns the fixture for this Shared Libraries Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected SharedLibrariesContainer getFixture() {
		return (SharedLibrariesContainer) fixture;
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
		setFixture(this.sdrRoot.getSharedLibrariesContainer());
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
		Assert.assertNotNull(getFixture().getSoftPkg("DCE:b02a169c-6211-4779-b2d2-e050f3088425"));

		// Namespaced device
		getFixture().getSoftPkg("DCE:cfa0c8b6-f2b6-49ce-ace1-5533dec9f008");
	}

	@Override
	public void testGetAllComponents() {
		Assert.assertEquals(6, getFixture().getAllComponents().size());
	}

	public void testServicesContainer() {
		Assert.assertNull(getFixture().getName());
		Assert.assertEquals(5, getFixture().getComponents().size());
		Assert.assertEquals(1, getFixture().getChildContainers().size());

		SharedLibrariesContainer childContainer = getFixture().getChildContainers().get(0);
		Assert.assertEquals("rh", childContainer.getName());
		Assert.assertEquals(1, childContainer.getComponents().size());
		Assert.assertEquals(0, childContainer.getChildContainers().size());
	}

} //SharedLibrariesContainerTest
