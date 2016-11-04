/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.ui.templates;

import java.util.ArrayList;
import java.util.List;

import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * Perhaps confusingly, this property tester returns <b>false</b> if the {@link IFile} for the SPD passed to it is a
 * shared library, and true otherwise.
 */
public class SharedLibraryPropertyTester extends PropertyTester {

	public SharedLibraryPropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		@SuppressWarnings("unchecked")
		List<ArrayList<Object>> receiverArray = (List<ArrayList<Object>>) receiver;
		IFile spdXml = (IFile) receiverArray.get(0);
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final URI spdUri = URI.createPlatformResourceURI(spdXml.getFullPath().toString(), true).appendFragment(SoftPkg.EOBJECT_PATH);
		final SoftPkg spd = (SoftPkg) resourceSet.getEObject(spdUri, true);

		if (spd != null) {
			if (spd.getImplementation().get(0).isSharedLibrary()) {
				return false;
			}
		}

		return true;
	}

}
