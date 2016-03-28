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

public class JavaEnvMap extends AbstractEnvMap {

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("CLASSPATH", getJavaClassPath(impl));
	}

	private String getJavaClassPath(Implementation impl) throws CoreException {
		LinkedHashSet<String> classPaths = new LinkedHashSet<String>();

		// Dependencies
		List<Implementation> depImpls = getDependencyImplementations(impl);
		for (Implementation depImpl : depImpls) {
			addToPath(classPaths, depImpl);
		}

		// CF
		classPaths.add("${OssieHome}/lib/*");

		// Pre-existing
		classPaths.add("${env_var:CLASSPATH}");

		StringBuilder classPathString = new StringBuilder();
		for (String classPath : classPaths) {
			classPathString.append(classPath);
			classPathString.append(File.pathSeparatorChar);
		}
		classPathString.setLength(classPathString.length() - 1);
		return classPathString.toString();
	}

	protected String createPath(String relativePath, URI spdUri) throws CoreException {
		if (spdUri == null || relativePath == null) {
			return null;
		}

		URI fullPath = spdUri.trimSegments(1).appendSegments(URI.createFileURI(relativePath).segments());
		if (fullPath.isPlatformResource()) {
			fullPath = CommonPlugin.resolve(spdUri);
		}

		IFileStore store = EFS.getStore(java.net.URI.create(fullPath.toString()));
		IFileInfo info = store.fetchInfo();
		if (info.exists()) {
			/* Functionality not currently in the CF
			if (info.isDirectory()) {
				File dir = store.toLocalFile(0, null);
				return dir.toString() + File.separator + "*";
			} */
			if (info.getName().endsWith(".jar")) {
				File file = store.toLocalFile(0, null);
				return file.toString();
			}
		}

		return null;
	}

}
