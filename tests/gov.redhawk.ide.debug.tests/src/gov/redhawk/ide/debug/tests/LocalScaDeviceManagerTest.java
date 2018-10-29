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

import java.util.Map;

import org.eclipse.core.externaltools.internal.IExternalToolConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.junit.Assert;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ObjectNotActive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongAdapter;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import CF.InvalidObjectReference;
import CF.LifeCyclePOA;
import CF.LifeCyclePackage.InitializeError;
import CF.LifeCyclePackage.ReleaseError;
import gov.redhawk.ide.debug.LocalScaDeviceManager;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.sdr.util.ScaEnvironmentUtil;
import gov.redhawk.sca.util.OrbSession;
import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca Device Manager</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class LocalScaDeviceManagerTest extends TestCase {

	/**
	 * The fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaDeviceManager fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaDeviceManagerTest.class);
	}

	/**
	 * Constructs a new Local Sca Device Manager test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaDeviceManagerTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalScaDeviceManager fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca Device Manager test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalScaDeviceManager getFixture() {
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
		setFixture(ScaDebugPlugin.getInstance().getLocalSca(null).getSandboxDeviceManager());
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
	 * Tests the '{@link gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Launch</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.LocalScaDeviceManager#launch(java.lang.String, CF.DataType[], org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String)
	 * @generated NOT
	 */
	public void testLaunch__String_DataType_URI_String_String() {
		// PASS - This would be difficult/impossible to test in a unit test environment
	}

	public void testRegisterService() throws ServantNotActive, WrongPolicy, InvalidObjectReference, ObjectNotActive, WrongAdapter, CoreException {
		// Launch two instances of "sleep" labeled as the same service
		ILaunchConfigurationWorkingCopy workingCopy = createSleepLaunch(LocalScaDeviceManagerTest.class.getSimpleName() + "_1");
		ILaunch launch1 = workingCopy.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		launch1.setAttribute("SERVICE_NAME", LocalScaDeviceManagerTest.class.getSimpleName() + "_1");
		workingCopy = createSleepLaunch(LocalScaDeviceManagerTest.class.getSimpleName() + "_2");
		workingCopy.setAttribute("SERVICE_NAME", LocalScaDeviceManagerTest.class.getSimpleName() + "_1");
		ILaunch launch2 = workingCopy.launch(ILaunchManager.RUN_MODE, new NullProgressMonitor());
		launch2.setAttribute("SERVICE_NAME", LocalScaDeviceManagerTest.class.getSimpleName() + "_1");

		// Create two CORBA objects to represent the services
		POA poa = OrbSession.createSession().getPOA();
		org.omg.CORBA.Object obj1 = poa.servant_to_reference(new LifeCyclePOA() {

			@Override
			public void releaseObject() throws ReleaseError {
				// PASS
			}

			@Override
			public void initialize() throws InitializeError {
				// PASS
			}
		});
		org.omg.CORBA.Object obj2 = poa.servant_to_reference(new LifeCyclePOA() {

			@Override
			public void releaseObject() throws ReleaseError {
				// PASS
			}

			@Override
			public void initialize() throws InitializeError {
				// PASS
			}
		});

		// Register the services with the same name
		boolean threw = false;
		getFixture().getLocalDeviceManager().registerService(obj1, LocalScaDeviceManagerTest.class.getSimpleName() + "_1");
		try {
			getFixture().getLocalDeviceManager().registerService(obj2, LocalScaDeviceManagerTest.class.getSimpleName() + "_1");
		} catch (InvalidObjectReference e) {
			threw = true;
		}
		Assert.assertTrue(threw);

		poa.deactivate_object(poa.reference_to_id(obj1));
		poa.deactivate_object(poa.reference_to_id(obj2));
		launch1.terminate();
		launch2.terminate();
	}

	private ILaunchConfigurationWorkingCopy createSleepLaunch(String name) throws CoreException {
		final ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		final ILaunchConfigurationType type = manager.getLaunchConfigurationType(IExternalToolConstants.ID_PROGRAM_LAUNCH_CONFIGURATION_TYPE);
		final ILaunchConfigurationWorkingCopy workingCopy = type.newInstance(null, name);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_LOCATION, "/bin/sleep");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_TOOL_ARGUMENTS, "10m");
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILDER_ENABLED, false);
		workingCopy.setAttribute(IExternalToolConstants.ATTR_BUILD_SCOPE, "${none}");
		return workingCopy;
	}

} //LocalScaDeviceManagerTest
