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
package gov.redhawk.ide.codegen.tests;

import org.junit.Assert;
import org.junit.Test;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.util.CodegenFileHelper;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdFactory;

public class CodegenFileHelperTests {

	/**
	 * Check that CodegenFileHelper handles namespaced projects correctly
	 */
	@Test
	public void getPreferredFilePrefixTest() {
		String projectBaseName = "Component";
		String projectFullName = "A.Namespaced." + projectBaseName;

		ImplementationSettings settings = CodegenFactory.eINSTANCE.createImplementationSettings();
		SoftPkg softPkg = SpdFactory.eINSTANCE.createSoftPkg();
		softPkg.setName(projectFullName);

		String result = CodegenFileHelper.getPreferredFilePrefix(softPkg, settings);
		Assert.assertEquals("CodegenFileHelper returned unexpected prefix", projectBaseName, result);
	}
}
