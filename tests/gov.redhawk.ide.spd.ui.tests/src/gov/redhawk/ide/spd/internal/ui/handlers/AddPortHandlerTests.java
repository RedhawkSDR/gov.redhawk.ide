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
package gov.redhawk.ide.spd.internal.ui.handlers;

import gov.redhawk.eclipsecorba.idl.Definition;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.eclipsecorba.library.RepositoryModule;
import gov.redhawk.eclipsecorba.library.URIPathSet;
import gov.redhawk.ide.spd.internal.ui.editor.wizard.PortWizardPage.PortWizardModel;
import gov.redhawk.ide.spd.ui.tests.SpdUiTestUtils;

import java.io.IOException;

import junit.framework.Assert;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.junit.Before;
import org.junit.Test;

public class AddPortHandlerTests {

	private SoftwareComponent scd;
	private IdlLibrary library;
	private EditingDomain editingDomain;
	private AddPortHandler addhandler;

	@Before
	public void setUp() throws IOException, CoreException {
		this.editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();

		final ResourceSet set = this.editingDomain.getResourceSet();
		final Resource libraryResource = set.createResource(URI.createFileURI(".library"));
		final URIPathSet uriPath = LibraryFactory.eINSTANCE.createURIPathSet();
		uriPath.getDirs().add(SpdUiTestUtils.getURI("idl"));
		this.library = LibraryFactory.eINSTANCE.createIdlLibrary();
		this.library.getPaths().add(uriPath);
		this.editingDomain.getCommandStack().execute(new AddCommand(this.editingDomain, libraryResource.getContents(), this.library));
		this.library.load(new NullProgressMonitor());

		final Resource spdResource = set.getResource(SpdUiTestUtils.getURI("testFiles/defaultCpp.spd.xml"), true);

		final SoftPkg softPkg = SoftPkg.Util.getSoftPkg(spdResource);
		this.scd = softPkg.getDescriptor().getComponent();

		this.addhandler = new AddPortHandler(this.editingDomain, spdResource, softPkg);
	}

	/**
	 * Add a single port.
	 */
	@Test
	public void addPort() {
		//Add a port
		final Provides provides = SpdUiTestUtils.createProvides("IDL:omg.org/CosContainment/ContainedInRole:1.0");
		PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
		Assert.assertEquals("The provides port " + provides.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides));
	}

	/**
	 * Make sure we can create and execute add commands for each repID in a standard IdlLibrary
	 */
	@Test
	public void testAddAll() {
		for (final Definition def : this.library.getDefinitions()) {
			if (def instanceof RepositoryModule) {
				final RepositoryModule module = (RepositoryModule) def;
				addRecursive(module);
			}
		}
	}

	public void addRecursive(final RepositoryModule module) {
		for (final Definition definition : module.getDefinitions()) {
			if (definition instanceof IdlInterfaceDcl) {
				final Provides provides = SpdUiTestUtils.createProvides(definition.getRepId());
				PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
				Assert.assertEquals("The provides port " + provides.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
				        .getPorts()
				        .getProvides()
				        .contains(provides));
				final Uses uses = SpdUiTestUtils.createUses(definition.getRepId());
				PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(uses)), this.editingDomain);
				Assert.assertEquals("The uses port " + uses.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
				        .getPorts()
				        .getUses()
				        .contains(uses));
			} else if (definition instanceof RepositoryModule) {
				addRecursive((RepositoryModule) definition);
			}
		}
	}
}
