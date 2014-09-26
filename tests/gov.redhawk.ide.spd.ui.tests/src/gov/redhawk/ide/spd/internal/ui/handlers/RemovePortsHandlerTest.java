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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.junit.Assert;
import mil.jpeojtrs.sca.scd.Provides;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.scd.Uses;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.junit.Before;
import org.junit.Test;

public class RemovePortsHandlerTest {

	private SoftwareComponent scd;
	private IdlLibrary library;
	private RemovePortsHandler removeHandler;
	private EditingDomain editingDomain;
	private AddPortHandler addhandler;

	@Before
	public void setUp() throws IOException, CoreException {
		this.editingDomain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();

		final ResourceSet set = this.editingDomain.getResourceSet();
		final Resource libraryResource = set.createResource(URI.createFileURI(".library"));
		final URIPathSet uriPath = LibraryFactory.eINSTANCE.createURIPathSet();
		uriPath.getDirs().add(SpdUiTestUtils.getURI("ossie/share/idl"));
		this.library = LibraryFactory.eINSTANCE.createIdlLibrary();
		this.library.getPaths().add(uriPath);
		this.editingDomain.getCommandStack().execute(new AddCommand(this.editingDomain, libraryResource.getContents(), this.library));
		this.library.load(new NullProgressMonitor());

		final Resource spdResource = set.getResource(SpdUiTestUtils.getURI("testFiles/defaultCpp.spd.xml"), true);

		final SoftPkg softPkg = SoftPkg.Util.getSoftPkg(spdResource);
		this.scd = softPkg.getDescriptor().getComponent();

		this.removeHandler = new RemovePortsHandler(this.editingDomain, spdResource, softPkg);
		this.addhandler = new AddPortHandler(this.editingDomain, spdResource, softPkg);
	}

	@Test
	public void removePort() throws CoreException {
		//Add a port
		final Provides provides = SpdUiTestUtils.createProvides("IDL:SAMPLE/SampleInterface:1.0");
		PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
		Assert.assertEquals("The provides port " + provides.getRepID() + " should be contained in the SCD", true, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides));

		//Now remove it
		final Collection<Object> removePorts = Collections.singleton((Object) provides);
		PortsHandlerUtil.execute(this.removeHandler.createRemovePortCommand(removePorts, new HashSet<String>()), this.editingDomain);
		Assert.assertEquals("The provides port " + provides.getRepID() + " should not be contained in the SCD", false, this.scd.getComponentFeatures()
		        .getPorts()
		        .getProvides()
		        .contains(provides));
	}

	/**
	 * Adds all IdlInterfaces in the repository as ports and then removes them all individually.
	 * @throws CoreException 
	 */
	@Test
	public void removeAllPorts() throws CoreException {
		this.addAllFromRepo();
		final List<Object> removePorts = new ArrayList<Object>();
		for (final FeatureMap.Entry entry : this.scd.getComponentFeatures().getPorts().getGroup()) {
			removePorts.add(entry.getValue());
		}
		final Command command = this.removeHandler.createRemovePortCommand(removePorts, new HashSet<String>());
		PortsHandlerUtil.execute(command, this.editingDomain);
		Assert.assertEquals(true, this.scd.getComponentFeatures().getPorts().getGroup().size() == 0);
	}

	/**
	 * Adds all idlInterfaces in IdlLibrary as a Uses and Provides port.
	 * @throws CoreException 
	 */
	private void addAllFromRepo() throws CoreException {
		for (final Definition def : this.library.getDefinitions()) {
			if (def instanceof RepositoryModule) {
				final RepositoryModule module = (RepositoryModule) def;
				for (final Definition definition : module.getDefinitions()) {
					if (definition instanceof IdlInterfaceDcl) {
						final Provides provides = SpdUiTestUtils.createProvides(definition.getRepId());
						PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(provides)), this.editingDomain);
						final Uses uses = SpdUiTestUtils.createUses(definition.getRepId());
						PortsHandlerUtil.execute(this.addhandler.createAddPortCommand(this.library, new PortWizardModel(uses)), this.editingDomain);
					}
				}
			}
		}
	}
}
