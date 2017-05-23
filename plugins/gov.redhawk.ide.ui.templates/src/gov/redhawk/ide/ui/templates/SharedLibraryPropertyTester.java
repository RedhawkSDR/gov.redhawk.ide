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

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IURIEditorInput;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Tests an editor to see if its input is a Redhawk shared library (SPD).
 */
public class SharedLibraryPropertyTester extends PropertyTester {

	private long lastReceiver = 0;
	private boolean lastIsSharedLib = false;

	public SharedLibraryPropertyTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("isSharedLibrary".equals(property)) {
			if (receiver.hashCode() == lastReceiver) {
				return lastIsSharedLib;
			}
			boolean result = isSharedLibrary(receiver);
			lastReceiver = receiver.hashCode();
			lastIsSharedLib = result;
			return result;
		}

		return false;
	}

	private boolean isSharedLibrary(Object receiver) {
		IEditorInput editorInput = (IEditorInput) receiver;
		if (editorInput == null) {
			return false;
		}

		URI spdUri;
		IFile file = editorInput.getAdapter(IFile.class);
		if (file != null) {
			if (!file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				return false;
			}
			spdUri = URI.createPlatformResourceURI(file.getFullPath().toString(), true);
		} else if (editorInput instanceof IURIEditorInput) {
			spdUri = URI.createURI(((IURIEditorInput) editorInput).getURI().toString());
			if (!spdUri.segment(spdUri.segmentCount() - 1).endsWith(SpdPackage.FILE_EXTENSION)) {
				return false;
			}
		} else {
			return false;
		}

		// Load SPD
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final SoftPkg spd;
		try {
			spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(spdUri, true));
		} catch (WrappedException e) {
			return false;
		}

		for (Implementation impl : spd.getImplementation()) {
			if (impl.isSharedLibrary()) {
				return true;
			}
		}

		return false;
	}
}
