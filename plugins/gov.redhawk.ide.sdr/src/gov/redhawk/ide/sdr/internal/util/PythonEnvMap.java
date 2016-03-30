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
package gov.redhawk.ide.sdr.internal.util;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;

import mil.jpeojtrs.sca.spd.Implementation;

public class PythonEnvMap extends AbstractEnvMap {

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("PYTHONPATH", getPythonPath(impl));
	}

	private String getPythonPath(Implementation impl) throws CoreException {
		LinkedHashSet<String> pythonPaths = new LinkedHashSet<String>();

		// Dependencies
		List<Implementation> depImpls = getDependencyImplementations(impl);
		for (Implementation depImpl : depImpls) {
			addToPath(pythonPaths, depImpl);
		}

		// CF
		pythonPaths.add("${OssieHome}/lib/python");

		// Pre-existing
		pythonPaths.add("${env_var:PYTHONPATH}");

		StringBuilder pythonPathString = new StringBuilder();
		for (String pythonPath : pythonPaths) {
			pythonPathString.append(pythonPath);
			pythonPathString.append(File.pathSeparatorChar);
		}
		pythonPathString.setLength(pythonPathString.length() - 1);
		return pythonPathString.toString();
	}

	@Override
	protected String createPath(String relativeCodePath, URI spdUri) throws CoreException {
		if (relativeCodePath == null || spdUri == null) {
			return null;
		}

		URI fullPathUri = spdUri.trimSegments(1).appendSegments(URI.createFileURI(relativeCodePath).segments());
		if (fullPathUri.isPlatformResource()) {
			fullPathUri = CommonPlugin.resolve(spdUri);
		}

		IFileStore store = EFS.getStore(java.net.URI.create(fullPathUri.toString()));
		IFileInfo info = store.fetchInfo();
		if (info.exists()) {
			if (info.isDirectory()) {
				return getAbsolutePath(fullPathUri);
			} else {
				return getAbsolutePath(fullPathUri.trimSegments(1));
			}
		}

		return null;
	}

}
