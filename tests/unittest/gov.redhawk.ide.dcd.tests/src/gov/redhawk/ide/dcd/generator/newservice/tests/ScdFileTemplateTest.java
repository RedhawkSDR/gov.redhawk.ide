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

package gov.redhawk.ide.dcd.generator.newservice.tests;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import gov.redhawk.eclipsecorba.idl.IdlFactory;
import gov.redhawk.eclipsecorba.idl.IdlInterfaceDcl;
import gov.redhawk.eclipsecorba.idl.Module;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.eclipsecorba.library.RepositoryModule;
import gov.redhawk.ide.dcd.generator.newservice.GeneratorArgs;
import gov.redhawk.ide.dcd.generator.newservice.ScdFileTemplate;
import gov.redhawk.ide.dcd.tests.TestUtils;
import gov.redhawk.ide.sdr.ui.SdrUiPlugin;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.util.DceUuidUtil;

/**
 * A class to test {@link ScdFileTemplate}.
 */
public class ScdFileTemplateTest {

	private IdlLibrary library;

	@Before
	public void before() {
		library = SdrUiPlugin.getDefault().getTargetSdrRoot().getIdlLibrary();
	}

	/**
	 * Tests generating an SCD file for an IDL the IDE doesn't know about
	 * @throws IOException
	 * @throws CoreException
	 */
	@Test
	public void testCreateServiceSCDFile_UnknownIDL() throws IOException, CoreException {
		// Generate XML using the template
		final ScdFileTemplate scdTemplate = new ScdFileTemplate();
		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName("project");
		args.setSoftPkgId(DceUuidUtil.createDceUUID());
		args.setAuthorName("Doe");
		args.setLibrary(library);
		args.setLibrary(library);
		args.setRepId("IDL:FOO/FooService:1.0");
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File scdFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(scdFile.toString()), true));
		standardChecks(component);
		Assert.assertEquals("IDL:FOO/FooService:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals(1, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertEquals(1, component.getInterfaces().getInterface().size());
	}

	/**
	 * Tests generating an SCD file for a service with an inheritance hierarchy
	 * @throws IOException
	 * @throws CoreException
	 */
	@Test
	public void testCreateServiceSCDFile_ResourceIDL() throws IOException, CoreException {
		library.load(new NullProgressMonitor());
		ScaModelCommand.execute(library, new ScaModelCommand() {
			@Override
			public void execute() {
				RepositoryModule barRepoModule = LibraryFactory.eINSTANCE.createRepositoryModule();
				barRepoModule.setName("BAR");
				library.getDefinitions().add(barRepoModule);

				Module barModule = IdlFactory.eINSTANCE.createModule();
				barModule.setName("BAR");

				IdlInterfaceDcl fooBarA = IdlFactory.eINSTANCE.createIdlInterfaceDcl();
				fooBarA.setName("FooBarA");
				IdlInterfaceDcl fooBarB = IdlFactory.eINSTANCE.createIdlInterfaceDcl();
				fooBarB.setName("FooBarB");
				fooBarA.getInheritedInterfaces().add(fooBarB);
				barModule.getDefinitions().add(fooBarA);
				barModule.getDefinitions().add(fooBarB);

				barRepoModule.getModuleDefinitions().add(barModule);
			}
		});

		// Generate XML using the template
		final ScdFileTemplate scdTemplate = new ScdFileTemplate();
		final GeneratorArgs args = new GeneratorArgs();
		args.setProjectName("project");
		args.setSoftPkgId(DceUuidUtil.createDceUUID());
		args.setAuthorName("Doe");
		args.setLibrary(library);
		args.setRepId("IDL:BAR/FooBarA:1.0");
		final String scdContent = scdTemplate.generate(args);

		// Create an XML file with the content
		final File scdFile = TestUtils.createFile(scdContent, ScdPackage.FILE_EXTENSION);

		// Try to create a model from the file
		final ResourceSet resourceSet = new ResourceSetImpl();
		final SoftwareComponent component = SoftwareComponent.Util.getSoftwareComponent(resourceSet.getResource(URI.createFileURI(scdFile.toString()), true));
		standardChecks(component);
		Assert.assertEquals("IDL:BAR/FooBarA:1.0", component.getComponentRepID().getRepid());
		Assert.assertEquals(2, component.getComponentFeatures().getSupportsInterface().size());
		Assert.assertEquals(2, component.getInterfaces().getInterface().size());
	}

	private void standardChecks(SoftwareComponent component) {
		Assert.assertEquals("2.2", component.getCorbaVersion());
		Assert.assertEquals("service", component.getComponentType());
		Assert.assertNotNull(component.getComponentFeatures().getPorts());
	}
}
