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
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.junit.Assert;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Local Sca</b></em>'.
 * <!-- end-user-doc -->
 * @generated
 */
public class LocalScaTest extends TestCase {

	/**
	 * The fixture for this Local Sca test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalSca fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(LocalScaTest.class);
	}

	/**
	 * Constructs a new Local Sca test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LocalScaTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Local Sca test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(LocalSca fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Local Sca test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected LocalSca getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		final ResourceSet rs = new ResourceSetImpl();
		final ResourceImpl resource = new ResourceImpl();
		rs.getResources().add(resource);
		final LocalSca localSca = ScaDebugFactory.eINSTANCE.createLocalSca();
		resource.getContents().add(localSca);
		setFixture(localSca);
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
	 * Tests the '{@link gov.redhawk.model.sca.IDisposable#dispose() <em>Dispose</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.model.sca.IDisposable#dispose()
	 * @generated NOT
	 */
	public void testDispose() {
		// END GENERATED CODE
		getFixture().dispose();
		// BEGIN GENERATED CODE
	}
	
	public void testPlugin() throws CoreException {
		LocalSca localSca = ScaDebugPlugin.getInstance().getLocalSca(null);
		Assert.assertNotNull(localSca);
		Assert.assertNotNull(localSca.getSandbox());
		Assert.assertNotNull(TransactionUtil.getEditingDomain(localSca));
	}

} //LocalScaTest
