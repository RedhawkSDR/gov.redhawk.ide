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

import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class SharedLibraryPropertyTester extends PropertyTester {

	private static final String IS_SHARED_LIBRARY = "isSharedLibrary";

	/**
	 * 
	 */
	public SharedLibraryPropertyTester() {
		// TODO Auto-generated constructor stub
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
			CodeFileType spdType = spd.getImplementation().get(0).getCode().getType();
			if (CodeFileType.SHARED_LIBRARY.equals(spdType)) {
				return false;
			}
		}

		return true;
	}

}
