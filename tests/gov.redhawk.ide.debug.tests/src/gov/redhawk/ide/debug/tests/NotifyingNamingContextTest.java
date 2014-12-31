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

import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.sca.efs.ScaFileSystemPlugin;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;
import junit.textui.TestRunner;

import org.eclipse.emf.common.util.URI;
import org.jacorb.naming.Name;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextPackage.InvalidName;

/**
 * <!-- begin-user-doc -->
 * A test case for the model object '<em><b>Notifying Naming Context</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName() <em>Name</em>}</li>
 * </ul>
 * </p>
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName(org.eclipse.emf.common.util.URI) <em>Get Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getURI(org.omg.CosNaming.NameComponent[]) <em>Get URI</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getFullName() <em>Get Full Name</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#getResourceContext(org.eclipse.emf.common.util.URI) <em>Get Resource Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.NotifyingNamingContext#findContext(org.omg.CosNaming.NamingContext) <em>Find Context</em>}</li>
 *   <li>{@link gov.redhawk.model.sca.IDisposable#dispose() <em>Dispose</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class NotifyingNamingContextTest extends TestCase {

	/**
	 * The fixture for this Notifying Naming Context test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NotifyingNamingContext fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(NotifyingNamingContextTest.class);
	}

	/**
	 * Constructs a new Notifying Naming Context test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContextTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Notifying Naming Context test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(NotifyingNamingContext fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Notifying Naming Context test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NotifyingNamingContext getFixture() {
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
		setFixture(ScaDebugFactory.eINSTANCE.createNotifyingNamingContext());
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
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName() <em>Name</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getName()
	 * @generated NOT
	 */
	public void testGetName() {
		// END GENERATED CODE
		getFixture().getName();
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getName(org.eclipse.emf.common.util.URI) <em>Get Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getName(org.eclipse.emf.common.util.URI)
	 * @generated NOT
	 */
	public void testGetName__URI() throws IOException {
		// END GENERATED CODE
		File tempDir = ScaFileSystemPlugin.getDefault().getTempDirectory();
		final File file = File.createTempFile("test", ".sad.xml", tempDir);
		file.deleteOnExit();

		assertNotNull(getFixture().getName(URI.createURI(file.toURI().toString())));

		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getURI(org.omg.CosNaming.NameComponent[]) <em>Get URI</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getURI(org.omg.CosNaming.NameComponent[])
	 * @generated NOT
	 */
	public void testGetURI__NameComponent() throws IOException, InvalidName {
		// END GENERATED CODE
		File tempDir = ScaFileSystemPlugin.getDefault().getTempDirectory();
		final File file = File.createTempFile("test", ".sad.xml", tempDir);
		file.deleteOnExit();

		final URI expected = URI.createURI(file.toURI().toString());
		final NameComponent[] context = getFixture().getName(expected);
		URI uri = getFixture().getURI(context);
		assertEquals(expected, uri);

		final String subContextStr = Name.toString(context);
		final NameComponent[] subContext = Name.toName("hello/" + subContextStr + "/child");
		uri = getFixture().getURI(subContext);
		assertEquals(expected, uri);
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getFullName() <em>Get Full Name</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getFullName()
	 * @generated NOT
	 */
	public void testGetFullName() {
		// END GENERATED CODE
		getFixture().getFullName();
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#getResourceContext(org.eclipse.emf.common.util.URI) <em>Get Resource Context</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#getResourceContext(org.eclipse.emf.common.util.URI)
	 * @generated NOT
	 */
	public void testGetResourceContext__URI() {
		// END GENERATED CODE
		getFixture().getResourceContext(null);
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.debug.NotifyingNamingContext#findContext(org.omg.CosNaming.NamingContext) <em>Find Context</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.debug.NotifyingNamingContext#findContext(org.omg.CosNaming.NamingContext)
	 * @generated NOT
	 */
	public void testFindContext__NamingContext() {
		// END GENERATED CODE
		getFixture().findContext(null);
		// BEGIN GENERATED CODE
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

} //NotifyingNamingContextTest
