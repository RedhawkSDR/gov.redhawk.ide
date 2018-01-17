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

import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Nodes Container</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class NodesContainerTest extends TestCase {

	/**
	 * The fixture for this Nodes Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodesContainer fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(NodesContainerTest.class);
	}

	/**
	 * Constructs a new Nodes Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NodesContainerTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Nodes Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(NodesContainer fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Nodes Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NodesContainer getFixture() {
		return fixture;
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
		setFixture(this.sdrRoot.getNodesContainer());
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

	public void testNodesContainer() {
		Assert.assertNull(getFixture().getName());
		Assert.assertEquals(1, getFixture().getNodes().size());
		Assert.assertEquals(1, getFixture().getChildContainers().size());

		NodesContainer childContainer = getFixture().getChildContainers().get(0);
		Assert.assertEquals("demo", childContainer.getName());
		Assert.assertEquals(1, childContainer.getNodes().size());
		Assert.assertEquals(0, childContainer.getChildContainers().size());
	}

} //NodesContainerTest
