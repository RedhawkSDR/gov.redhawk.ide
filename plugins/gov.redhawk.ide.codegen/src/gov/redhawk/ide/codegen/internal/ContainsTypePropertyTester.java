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
package gov.redhawk.ide.codegen.internal;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ImplementationSettings;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * 
 */
public class ContainsTypePropertyTester extends PropertyTester {
	public static final String PROP_CODEGEN_TYPE = "containsType";

	/**
	 * 
	 */
	public ContainsTypePropertyTester() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (receiver instanceof IFile) {
			IFile file = (IFile) receiver;
			if (PROP_CODEGEN_TYPE.equals(property)) {
				ResourceSet resourceSet = new ResourceSetImpl();
				SoftPkg spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI(file.getFullPath().toString(), true), true));
				for (Object obj : args) {
					for (Implementation impl : spd.getImplementation()) {
						ImplementationSettings settings = CodegenUtil.getImplementationSettings(impl);
						if (settings != null && settings.getGeneratorId() != null && settings.getGeneratorId().equals(obj)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

}
