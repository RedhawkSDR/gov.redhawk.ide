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
package gov.redhawk.ide.sdr.tests;

import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.ide.sdr.WaveformsContainer;
import junit.framework.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Waveforms Container</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class WaveformsContainerTest extends TestCase {

	/**
	 * The fixture for this Waveforms Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaveformsContainer fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(WaveformsContainerTest.class);
	}

	/**
	 * Constructs a new Waveforms Container test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public WaveformsContainerTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Waveforms Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(WaveformsContainer fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Waveforms Container test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected WaveformsContainer getFixture() {
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
		setFixture(this.sdrRoot.getWaveformsContainer());
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

	public void testWaveformsContainer() {
		final EList<SoftwareAssembly> waveforms = this.fixture.getWaveforms();
		Assert.assertEquals(1, waveforms.size());
		Assert.assertNotNull(waveforms.get(0));
		Assert.assertEquals(this.sdrRoot, this.fixture.getSdrRoot());
	}

} //WaveformsContainerTest
