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

import gov.redhawk.spd.internal.validation.EntryPointConstraint;
import org.junit.Assert;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EntryPointContstraintTest {

	private AbstractModelConstraint constraint;
	private SpdValidationTestResourceProvider provider;

	@Before
	public void setUp() throws Exception {
		this.constraint = new EntryPointConstraint();
		this.provider = new SpdValidationTestResourceProvider();
	}

	@After
	public void tearDown() throws Exception {
		this.constraint = null;
		this.provider.deleteWorkspaceResources();
		this.provider = null;
	}

	@Test
	public void testValidateIValidationContextDefaultCode() {
		IValidationContext ctx = new TestValidationContext(EntryPointConstraint.CODE_ENTRYPOINT_ID, SpdFactory.eINSTANCE.createCode());
		Assert.assertEquals(IStatus.OK, this.constraint.validate(ctx).getSeverity());
	}

	@Test
	public void testValidateIValidationContextCodeNodeBooter() {
		Code code = SpdFactory.eINSTANCE.createCode();
		code.setLocalFile(SpdFactory.eINSTANCE.createLocalFile());
		code.setType(CodeFileType.NODE_BOOTER);
		IValidationContext ctx = new TestValidationContext(EntryPointConstraint.CODE_ENTRYPOINT_ID, code);
		Assert.assertEquals(IStatus.OK, this.constraint.validate(ctx).getSeverity());
	}

	@Test
	public void testValidateIValidationContextCodeNoEntryPoint() {
		Code code = SpdFactory.eINSTANCE.createCode();
		code.setLocalFile(SpdFactory.eINSTANCE.createLocalFile());
		IValidationContext ctx = new TestValidationContext(EntryPointConstraint.CODE_ENTRYPOINT_ID, code);
		Assert.assertEquals(IStatus.OK, this.constraint.validate(ctx).getSeverity());
	}

	@Test
	public void testValidateIValidationContextCodeExecutable() throws Exception {
		Code code = this.provider.createWorkspaceSoftPkgResource().getImplementation().get(0).getCode();
		code.setLocalFile(SpdFactory.eINSTANCE.createLocalFile());
		code.setEntryPoint("test");
		code.setType(CodeFileType.EXECUTABLE);
		IValidationContext ctx = new TestValidationContext(EntryPointConstraint.CODE_ENTRYPOINT_ID, code);
		Assert.assertEquals(IStatus.ERROR, this.constraint.validate(ctx).getSeverity());
	}
}
