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

import gov.redhawk.ide.sdr.SdrRoot;
import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.junit.After;
import org.junit.Before;

public class SdrRootInvalidPathTest extends TestCase {

	/**
	 * The fixture for this Root test case.
	 */
	protected SdrRoot fixture = null;

	/**
	 */
	public static void main(final String[] args) {
		TestRunner.run(SdrRootTest.class);
	}

	/**
	 * Sets the fixture for this Root test case.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected void setFixture(final SdrRoot fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Root test case.
	 * <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * @generated
	 */
	protected SdrRoot getFixture() {
		return this.fixture;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		setFixture(SdrTests.getSdrRoot(URI.createFileURI("/invalidPath")));
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see junit.framework.TestCase#tearDown()
	 * @generated
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		setFixture(null);
	}

	public void testContainers() {
		Assert.assertNotNull("The Components Container should not be null.", this.fixture.getComponentsContainer());
		Assert.assertEquals(0, this.fixture.getComponentsContainer().getComponents().size());
		Assert.assertNotNull("The Waveforms Container should not be null.", this.fixture.getWaveformsContainer());
		Assert.assertEquals(0, this.fixture.getWaveformsContainer().getWaveforms().size());
		Assert.assertNotNull("The Devices Container should not be null.", this.fixture.getDevicesContainer());
		Assert.assertEquals(0, this.fixture.getDevicesContainer().getComponents().size());
		Assert.assertNotNull("The Nodes Container should not be null.", this.fixture.getNodesContainer());
		Assert.assertEquals(0, this.fixture.getNodesContainer().getNodes().size());
		Assert.assertNull("The Domain Configuration should be null.", this.fixture.getDomainConfiguration());
	}

	public void testErrorStatus() {
		Assert.assertEquals(IStatus.ERROR, this.fixture.getLoadStatus().getSeverity());
	}

}
