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
package gov.redhawk.spd.validation.tests;

import gov.redhawk.spd.internal.validation.LocalFileConstraint;
import org.junit.Assert;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LocalFileConstraintTest {

	private AbstractModelConstraint constraint;
	private SpdValidationTestResourceProvider provider;

	@Before
	public void setUp() throws Exception {
		this.constraint = new LocalFileConstraint();
		this.provider = new SpdValidationTestResourceProvider();
	}

	@After
	public void tearDown() throws Exception {
		this.constraint = null;
		this.provider.deleteWorkspaceResources();
		this.provider = null;
	}

	@Test
	public void testPropertyFileImplementation() throws Exception {
		IValidationContext context = new TestValidationContext(LocalFileConstraint.PROPERTY_ID, this.provider.createWorkspaceSoftPkgResource()
		        .getImplementation().get(0));
		//should succeed because there is no prf file associated with the impl
		Assert.assertEquals(IStatus.OK, this.constraint.validate(context).getSeverity());
		
	}

	@Test
	public void testPropertyFileSoftPkg() throws Exception {
		IValidationContext context = new TestValidationContext(LocalFileConstraint.PROPERTY_ID, this.provider.createWorkspaceSoftPkgResource());
		//should succeed because the prf file has been created.
		IStatus status = this.constraint.validate(context);
		Assert.assertEquals(status.getMessage(), IStatus.OK, status.getSeverity());
	}
	
	@Test
	public void testSCDFile() throws Exception {
		IValidationContext context = new TestValidationContext(LocalFileConstraint.SCD_ID, this.provider.createWorkspaceSoftPkgResource());
		//should succeed because the scd file has been created.
		IStatus status = this.constraint.validate(context);
		Assert.assertEquals(status.getMessage(), IStatus.OK, status.getSeverity());
	}
	
	@Test
	public void testCode() throws Exception {
		Code code = this.provider.createWorkspaceSoftPkgResource().getImplementation().get(0).getCode();
		LocalFile file = SpdFactory.eINSTANCE.createLocalFile();
		file.setName("test");
		code.setLocalFile(file);
		IValidationContext context = new TestValidationContext(LocalFileConstraint.CODE_ID, code);
		//Should error because the test file does not exist in the project
		IStatus status = this.constraint.validate(context);
		Assert.assertEquals(IStatus.ERROR, status.getSeverity());
	}

}
