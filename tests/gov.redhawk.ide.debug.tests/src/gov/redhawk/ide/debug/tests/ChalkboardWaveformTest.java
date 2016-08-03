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
 // BEGIN GENERATED CODE
package gov.redhawk.ide.debug.tests;

import gov.redhawk.ide.debug.LocalSca;
import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.ScaDebugInstance;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.junit.Assert;
import org.junit.Test;

/**
 * Performs tests on a special case of a local waveform - the chalkboard (sandbox) waveform
 */
@SuppressWarnings("restriction")
public class ChalkboardWaveformTest extends TestCase {

	/**
	 * The fixture for this Local Sca Waveform test case.
	 */
	protected LocalScaWaveform fixture = null;

	public static void main(String[] args) {
		TestRunner.run(ChalkboardWaveformTest.class);
	}

	/**
	 * Constructs a new test case with the given name.
	 */
	public ChalkboardWaveformTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this test case.
	 */
	protected void setFixture(LocalScaWaveform fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this test case.
	 */
	protected LocalScaWaveform getFixture() {
		return fixture;
	}

	@Override
	protected void setUp() throws Exception {
		ScaDebugInstance.INSTANCE.init(null);
		setFixture(ScaDebugPlugin.getInstance().getLocalSca().getSandboxWaveform());
	}

	@Override
	protected void tearDown() throws Exception {
		setFixture(null);
	}

	@Test
	public void test_IDE_824() throws Exception {
		final LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(null);
		final LocalScaWaveform localWaveform = ScaDebugFactory.eINSTANCE.createLocalScaWaveform();
		localWaveform.setName("TestWaveform");
		ScaModelCommand.execute(localSca, new ScaModelCommand() {

			@Override
			public void execute() {
				localSca.getWaveforms().add(localWaveform);
			}
		});
		Assert.assertEquals(2, localSca.getWaveforms().size());
		localWaveform.releaseObject();
		Assert.assertEquals(1, localSca.getWaveforms().size());
	}

} //LocalScaWaveformTest
