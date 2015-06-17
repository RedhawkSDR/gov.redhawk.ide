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
package gov.redhawk.ide.debug.tests;

import gov.redhawk.ide.debug.LocalScaWaveform;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.Launch;

import CF._ApplicationStub;
import CF.LifeCyclePackage.ReleaseError;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Waveform</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class LocalScaWaveformTest extends TestCase {

	/**
	 * The fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaWaveform fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaWaveformTest.class);
	}

	/**
	 * Constructs a new Local Sca Waveform test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaWaveformTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalScaWaveform fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca Waveform test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaWaveform getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(ScaDebugFactory.eINSTANCE.createLocalScaWaveform());
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

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaWaveform#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String)
	 * @generated
	 */
	public void testLaunch__String_DataType_URI_String_String() {
		// PASS - This would be difficult/impossible to test in a unit test environment
	}

	/**
	 * For IDE-1085. Test that disposing the object calls releaseObject() if it has an ILaunch.
	 *
	 * @see #testDisposeWithoutILaunch()
	 * @throws CoreException
	 */
	public void testDisposeWithILaunch() throws CoreException {
		// Ensure the local SCA initializes fully
		ScaDebugPlugin.getInstance().getLocalSca(new NullProgressMonitor());

		// Listen to the job manager for changes
		ReleaseJobListener jobListener = new ReleaseJobListener();
		Job.getJobManager().addJobChangeListener(jobListener);

		try {
			// Setup the LocalScaWaveform object
			FakeApplication resource = new FakeApplication();
			fixture.setObj(resource);
			fixture.setLaunch(new FakeILaunch());

			// Call dispose
			fixture.dispose();

			// Ensure the job to call releaseObject() is scheduled
			assertTrue(jobListener.jobScheduled);
		} finally {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	/**
	 * For IDE-1085. Test that disposing the object doesn't call releaseObject() if it has no ILaunch.
	 *
	 * @see #testDisposeWithILaunch()
	 * @throws CoreException
	 */
	public void testDisposeWithoutILaunch() throws CoreException {
		// Ensure the local SCA initializes fully
		ScaDebugPlugin.getInstance().getLocalSca(new NullProgressMonitor());

		// Listen to the job manager for changes
		ReleaseJobListener jobListener = new ReleaseJobListener();
		Job.getJobManager().addJobChangeListener(jobListener);

		try {
			// Setup the LocalScaWaveform object
			FakeApplication resource = new FakeApplication();
			fixture.setObj(resource);

			// Call dispose
			fixture.dispose();

			// Ensure the job to call releaseObject() is NOT scheduled
			assertFalse(jobListener.jobScheduled);
		} finally {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	private class FakeApplication extends _ApplicationStub {

		@Override
		public void releaseObject() throws ReleaseError {
		}

		@Override
		public void _release() {
		}

	}

	private class FakeILaunch extends Launch {

		public FakeILaunch() {
			super(null, null, null);
		}

	}

	private class ReleaseJobListener extends JobChangeAdapter {

		public volatile boolean jobScheduled = false;

		@Override
		public void scheduled(IJobChangeEvent event) {
			if ("Local Waveform Release".equals(event.getJob().getName())) {
				jobScheduled = true;
			}
		}

	}

} //LocalScaWaveformTest
