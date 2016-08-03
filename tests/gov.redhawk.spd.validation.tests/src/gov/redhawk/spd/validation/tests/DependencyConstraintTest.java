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

import gov.redhawk.spd.internal.validation.DependencyConstraint;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.Os;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DependencyConstraintTest {

	private AbstractModelConstraint constraint;
	private SpdValidationTestResourceProvider provider;

	@Before
	public void setUp() throws Exception {
		this.constraint = new DependencyConstraint();
		this.provider = new SpdValidationTestResourceProvider();
	}

	@After
	public void tearDown() throws Exception {
		this.constraint = null;
		this.provider.deleteWorkspaceResources();
		this.provider = null;
	}

	@Test
	public void testValidateIValidationContextNoDependency() throws Exception {
		IValidationContext context = new TestValidationContext(DependencyConstraint.ID, SpdFactory.eINSTANCE.createImplementation());
		//Should error since the constraint is not satisfied
		Assert.assertEquals(IStatus.ERROR, this.constraint.validate(context).getSeverity());
	}

	@Test
	public void testValidateIValidationContextWithDependency() throws Exception {
		Implementation impl = this.provider.createWorkspaceSoftPkgResource().getImplementation().get(0);
		Os os = SpdFactory.eINSTANCE.createOs();
		os.setName("Linux");
		impl.getOs().add(os);
		IValidationContext context = new TestValidationContext(DependencyConstraint.ID, impl);
		//Validation should succeed since the implementation has an os dependency
		Assert.assertEquals(IStatus.OK, this.constraint.validate(context).getSeverity());
	}

	@Test
	public void testValidateIValidationContextNull() throws Exception {
		IValidationContext context = new TestValidationContext(DependencyConstraint.ID, null);
		//Validation should succeed since the target is not an impl
		Assert.assertEquals(IStatus.OK, this.constraint.validate(context).getSeverity());
	}

}
