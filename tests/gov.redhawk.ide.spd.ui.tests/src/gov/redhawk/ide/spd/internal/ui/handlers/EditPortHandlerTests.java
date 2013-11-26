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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
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

public class EditPortHandlerTests {

	private SoftwareComponent scd;
	private IdlLibrary library;
	private EditingDomain editingDomain;
	private AddPortHandler addhandler;
	private EditPortHandler editHandler;
	private RemovePortsHandler removeHandler;

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
		this.editHandler = new EditPortHandler(this.editingDomain, spdResource, softPkg);
		this.removeHandler = new RemovePortsHandler(this.editingDomain, spdResource, softPkg);
	}

	@Test
	public void testHandleEditPort() {
		//Add a port
		final Provides provides = SpdUiTestUtils.createProvides("IDL:BULKIO/dataChar:1.0");
		PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
		Assert.assertEquals("The provides port " + provides.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides));

		//Create a second port
		final Provides provides2 = SpdUiTestUtils.createProvides("IDL:BULKIO/dataChar:1.0");
		this.editHandler.handleEditPort(this.library, provides, new PortWizardModel(provides2));
		//Make sure the first port is gone
		Assert.assertEquals("The provides port " + provides.getRepID() + " should not be contained in the SCD", false, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides));
		//Make sure the second port is there
		Assert.assertEquals("The uses port " + provides2.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides2));
		SpdUiTestUtils.testNoDuplicateOrMissingInterfaces(this.scd.getComponentFeatures().getPorts(), this.scd);
	}

	@Test
	public void testHandleEditMultiplePorts() {
		for (final Definition def : this.library.getDefinitions()) {
			if (def instanceof RepositoryModule) {
				final RepositoryModule module = (RepositoryModule) def;
				if ("BULKIO".equals(module.getName())) {
					for (final Definition definition : module.getDefinitions()) {
						final List<Object> ports = new ArrayList<Object>();
						if (definition instanceof IdlInterfaceDcl) {
							final Provides provides = SpdUiTestUtils.createProvides(definition.getRepId());
							ports.add(provides);
							PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
							final Provides provides2 = SpdUiTestUtils.createProvides(definition.getRepId());
							ports.add(provides2);
							PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides2)), this.editingDomain);
							for (final Definition newDef : module.getDefinitions()) {
								if (newDef instanceof IdlInterfaceDcl) {
									final Provides newPort = SpdUiTestUtils.createProvides(newDef.getRepId());
									//Edit the port
									this.editHandler.handleEditPort(this.library, provides2, new PortWizardModel(newPort));
									//Make sure interfaces are okay
									SpdUiTestUtils.testNoDuplicateOrMissingInterfaces(this.scd.getComponentFeatures().getPorts(), this.scd);
									//Swap it back
									this.editHandler.handleEditPort(this.library, newPort, new PortWizardModel(provides2));
									//Make sure interfaces are okay
									SpdUiTestUtils.testNoDuplicateOrMissingInterfaces(this.scd.getComponentFeatures().getPorts(), this.scd);
								}
							}
							this.removeHandler.createRemovePortCommand(ports, new HashSet<String>());
						}
					}
				}

			}
		}
	}

}
