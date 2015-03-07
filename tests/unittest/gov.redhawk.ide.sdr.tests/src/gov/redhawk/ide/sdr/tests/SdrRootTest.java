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

import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.ide.sdr.LoadState;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import java.net.URI;
import org.junit.Assert;
import junit.framework.TestCase;
import junit.textui.TestRunner;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;

/**
 * <!-- begin-user-doc --> A test case for the model object '
 * <em><b>Root</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getLoadStatus() <em>Load Status</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer() <em>Components Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer() <em>Waveforms Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer() <em>Devices Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getServicesContainer() <em>Services Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getNodesContainer() <em>Nodes Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getSharedLibrariesContainer() <em>Shared Libraries Container</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration() <em>Domain Configuration</em>}</li>
 * </ul>
 * </p>
 * <p>
 * The following operations are tested:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#load(org.eclipse.core.runtime.IProgressMonitor) <em>Load</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#unload(org.eclipse.core.runtime.IProgressMonitor) <em>Unload</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#reload(org.eclipse.core.runtime.IProgressMonitor) <em>Reload</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#setSdrRoot(org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Set Sdr Root</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDevResource(java.lang.String) <em>Get Dev Resource</em>}</li>
 *   <li>{@link gov.redhawk.ide.sdr.SdrRoot#getDomResource(java.lang.String) <em>Get Dom Resource</em>}</li>
 * </ul>
 * </p>
 * @generated
 */
public class SdrRootTest extends TestCase {

	/**
	 * The fixture for this Root test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SdrRoot fixture = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static void main(String[] args) {
		TestRunner.run(SdrRootTest.class);
	}

	/**
	 * Constructs a new Root test case with the given name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrRootTest(String name) {
		super(name);
	}

	/**
	 * Sets the fixture for this Root test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void setFixture(SdrRoot fixture) {
		this.fixture = fixture;
	}

	/**
	 * Returns the fixture for this Root test case.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SdrRoot getFixture() {
		return fixture;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @see junit.framework.TestCase#setUp()
	 * @generated NOT
	 */
	@Override
	protected void setUp() throws Exception {
		setFixture(SdrTestsUtil.getSdrTestsSdrRoot());
		this.fixture.load(null);
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
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getLoadStatus() <em>Load Status</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getLoadStatus()
	 * @generated NOT
	 */
	public void testGetLoadStatus() {
		// END GENERATED CODE
		Assert.assertEquals("SDRROOT Load Status not OK: " + this.getFixture().getLoadStatus().getMessage(), IStatus.OK,
			this.fixture.getLoadStatus().getSeverity());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer() <em>Components Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getComponentsContainer()
	 * @generated NOT
	 */
	public void testGetComponentsContainer() {
		// END GENERATED CODE
		Assert.assertNotNull("The Components Container should not be null.", this.fixture.getComponentsContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer() <em>Waveforms Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getWaveformsContainer()
	 * @generated NOT
	 */
	public void testGetWaveformsContainer() {
		// END GENERATED CODE
		Assert.assertNotNull("The Waveforms Container should not be null.", this.fixture.getWaveformsContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer() <em>Devices Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevicesContainer()
	 * @generated NOT
	 */
	public void testGetDevicesContainer() {
		// END GENERATED CODE
		Assert.assertNotNull("The Devices Container should not be null.", this.fixture.getDevicesContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getServicesContainer() <em>Services Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getServicesContainer()
	 * @generated NOT
	 */
	public void testGetServicesContainer() {
		// END GENERATED CODE
		Assert.assertNotNull(getFixture().getServicesContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getNodesContainer() <em>Nodes Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getNodesContainer()
	 * @generated NOT
	 */
	public void testGetNodesContainer() {
		// END GENERATED CODE
		Assert.assertNotNull("The Nodes Container should not be null.", this.fixture.getNodesContainer());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getSharedLibrariesContainer() <em>Shared Libraries Container</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getSharedLibrariesContainer()
	 * @generated
	 */
	public void testGetSharedLibrariesContainer() {
		// TODO: implement this feature getter test method
		// Ensure that you remove @generated or mark it @generated NOT
		fail();
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration() <em>Domain Configuration</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDomainConfiguration()
	 * @generated NOT
	 */
	public void testGetDomainConfiguration() {
		// END GENERATED CODE
		Assert.assertNotNull("The Domain Configuration should not be null.", this.fixture.getDomainConfiguration());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary() <em>Idl Library</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getIdlLibrary()
	 * @generated NOT
	 */
	public void testGetIdlLibrary() {
		// END GENERATED CODE
		Assert.assertNull("The IDL Library should be null.", this.fixture.getIdlLibrary());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#setIdlLibrary(gov.redhawk.eclipsecorba.library.IdlLibrary) <em>Idl Library</em>}' feature setter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#setIdlLibrary(gov.redhawk.eclipsecorba.library.IdlLibrary)
	 * @generated NOT
	 */
	public void testSetIdlLibrary() {
		// END GENERATED CODE
		final TransactionalEditingDomain editingDomain = TransactionUtil.getEditingDomain(this.fixture);
		editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this.fixture, SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY, null));
		Assert.assertNull(this.fixture.getIdlLibrary());
		final IdlLibrary library = LibraryFactory.eINSTANCE.createIdlLibrary();
		editingDomain.getCommandStack().execute(SetCommand.create(editingDomain, this.fixture, SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY, library));
		Assert.assertNotNull(this.fixture.getIdlLibrary());
		// END GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot() <em>Dev File System Root</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws CoreException 
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevFileSystemRoot()
	 * @generated NOT
	 */
	public void testGetDevFileSystemRoot() throws CoreException {
		// END GENERATED CODE
		Assert.assertNotNull(this.fixture.getDevFileSystemRoot());
		Assert.assertTrue(EFS.getStore(URI.create(this.fixture.getDevFileSystemRoot().toString())).fetchInfo().exists());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot() <em>Dom File System Root</em>}' feature getter.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @throws CoreException 
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDomFileSystemRoot()
	 * @generated NOT
	 */
	public void testGetDomFileSystemRoot() throws CoreException {
		// END GENERATED CODE
		Assert.assertNotNull(this.fixture.getDomFileSystemRoot());
		Assert.assertTrue(EFS.getStore(URI.create(this.fixture.getDomFileSystemRoot().toString())).fetchInfo().exists());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '
	 * {@link gov.redhawk.ide.sdr.SdrRoot#load(org.eclipse.core.runtime.IProgressMonitor)
	 * <em>Load</em>}' operation. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @see gov.redhawk.ide.sdr.SdrRoot#load(org.eclipse.core.runtime.IProgressMonitor)
	 * @generated NOT
	 */
	public void testLoad__IProgressMonitor() {
		// END GENERATED CODE
		Assert.assertEquals(fixture.getLoadStatus().getMessage(), LoadState.LOADED, this.fixture.getState());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#unload(org.eclipse.core.runtime.IProgressMonitor) <em>Unload</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#unload(org.eclipse.core.runtime.IProgressMonitor)
	 * @generated NOT
	 */
	public void testUnload__IProgressMonitor() {
		// END GENERATED CODE
		getFixture().unload(null);
		Assert.assertEquals(LoadState.UNLOADED, getFixture().getState());
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#reload(org.eclipse.core.runtime.IProgressMonitor) <em>Reload</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#reload(org.eclipse.core.runtime.IProgressMonitor)
	 * @generated NOT
	 */
	public void testReload__IProgressMonitor() {
		// END GENERATED CODE
		getFixture().reload(null);
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#setSdrRoot(org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String) <em>Set Sdr Root</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#setSdrRoot(org.eclipse.emf.common.util.URI, java.lang.String, java.lang.String)
	 * @generated NOT
	 */
	public void testSetSdrRoot__URI_String_String() {
		// END GENERATED CODE
		ScaModelCommand.execute(getFixture(), new ScaModelCommand() {

			@Override
			public void execute() {
				getFixture().setSdrRoot(null, null, null);
			}
		});

		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDevResource(java.lang.String) <em>Get Dev Resource</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDevResource(java.lang.String)
	 * @generated NOT
	 */
	public void testGetDevResource__String() {
		// END GENERATED CODE
		getFixture().getDevResource(null);
		// BEGIN GENERATED CODE
	}

	/**
	 * Tests the '{@link gov.redhawk.ide.sdr.SdrRoot#getDomResource(java.lang.String) <em>Get Dom Resource</em>}' operation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see gov.redhawk.ide.sdr.SdrRoot#getDomResource(java.lang.String)
	 * @generated NOT
	 */
	public void testGetDomResource__String() {
		// END GENERATED CODE
		getFixture().getDomResource(null);
		// BEGIN GENERATED CODE
	}

} // SdrRootTest
