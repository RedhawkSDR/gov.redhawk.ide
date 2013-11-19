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
package gov.redhawk.codegen.validation.tests;

import gov.redhawk.codegen.internal.validation.ValidImplementationNameConstraint;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.WaveDevSettings;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.junit.Assert;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidImplementationNameConstraintTest {

	private WaveDevSettings waveSettings;
	private ResourceSet resourceSet;
	private AbstractModelConstraint constraint;

	@Before
	public void setUp() throws Exception {
		this.constraint = new ValidImplementationNameConstraint();
		final ResourceSet set = new ResourceSetImpl();
		this.resourceSet = set;
	}

	@After
	public void tearDown() throws Exception {
		this.resourceSet = null;
		this.waveSettings = null;
	}

	@Test
	public void testValidImplementationNameConstraints() throws Exception {
		this.waveSettings = (WaveDevSettings) this.resourceSet.getResource(ValidImplementationNameConstraintTest.getURI("testFiles/good_name.wavedev"), true)
		        .getEObject("/");

		final Collection<ImplementationSettings> implSettings = this.waveSettings.getImplSettings().values();

		for (final ImplementationSettings impl : implSettings) {
			IValidationContext context = new TestValidationContext(ValidImplementationNameConstraint.ID, impl);
			//Validation should succeed since the implementation has an os dependency
			Assert.assertEquals(IStatus.OK, this.constraint.validate(context).getSeverity());

		}
	}

	@Test
	public void testNotUniqueImplementationNameConstraints() throws Exception {
		this.waveSettings = (WaveDevSettings) this.resourceSet.getResource(ValidImplementationNameConstraintTest.getURI("testFiles/bad_name.wavedev"), true)
		        .getEObject("/");

		final Collection<ImplementationSettings> implSettings = this.waveSettings.getImplSettings().values();

		for (final ImplementationSettings impl : implSettings) {
			IValidationContext context = new TestValidationContext(ValidImplementationNameConstraint.ID, impl);
			//Validation should succeed since the implementation has an os dependency
			Assert.assertEquals(IStatus.ERROR, this.constraint.validate(context).getSeverity());
		}
	}

	public static URI getURI(final String filePath) throws IOException {
		final URL url = FileLocator.toFileURL(FileLocator.find(Platform.getBundle("gov.redhawk.codegen.validation.tests"), new Path(filePath), null));
		return URI.createURI(url.toString());
	}
}
