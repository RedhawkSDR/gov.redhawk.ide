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

import gov.redhawk.ide.debug.LocalScaComponent;
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

import CF._ResourceStub;
import CF.LifeCyclePackage.ReleaseError;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Component</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class LocalScaComponentTest extends TestCase {

	/**
	 * The fixture for this Local Sca Component test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaComponent fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaComponentTest.class);
	}

	/**
	 * Constructs a new Local Sca Component test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaComponentTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca Component test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalScaComponent fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca Component test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaComponent getFixture() {
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
		setFixture(ScaDebugFactory.eINSTANCE.createLocalScaComponent());
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
			// Setup the LocalScaComponent object
			FakeResource resource = new FakeResource();
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
			// Setup the LocalScaComponent object
			FakeResource resource = new FakeResource();
			fixture.setObj(resource);

			// Call dispose
			fixture.dispose();

			// Ensure the job to call releaseObject() is NOT scheduled
			assertFalse(jobListener.jobScheduled);
		} finally {
			Job.getJobManager().removeJobChangeListener(jobListener);
		}
	}

	private class FakeResource extends _ResourceStub {

		@Override
		public void releaseObject() throws ReleaseError {
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
			if ("Local Component Release job".equals(event.getJob().getName())) {
				jobScheduled = true;
			}
		}

	}

} //LocalScaComponentTest
