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

import gov.redhawk.ide.sdr.NodesContainer;
import gov.redhawk.ide.sdr.SdrRoot;
import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;
import org.eclipse.emf.common.util.EList;

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
		this.sdrRoot = SdrTests.getSdrTestsSdrRoot();
		this.sdrRoot.load(null);
		setFixture(this.sdrRoot.getNodesContainer());
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
		final EList<DeviceConfiguration> nodes = this.fixture.getNodes();
		Assert.assertEquals(1, nodes.size());
		for (final DeviceConfiguration devConfig : nodes) {
			Assert.assertNotNull(devConfig);
		}
		Assert.assertEquals(this.sdrRoot, this.fixture.getSdrRoot());
	}

} //NodesContainerTest
