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
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;

import mil.jpeojtrs.sca.spd.Implementation;

public class CppEnvMap extends AbstractEnvMap {

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("LD_LIBRARY_PATH", get_LD_LIBRARY_Path(impl));
	}

	private String get_LD_LIBRARY_Path(Implementation impl) throws CoreException {
		LinkedHashSet<String> ldPaths = new LinkedHashSet<String>();

		// Dependencies
		List<Implementation> depImpls = getDependencyImplementations(impl);
		for (Implementation depImpl : depImpls) {
			addToPath(ldPaths, depImpl);
		}

		// CF
		if (Platform.getOSArch().equals(Platform.ARCH_X86_64)) {
			ldPaths.add("${OssieHome}/lib64");
		}
		ldPaths.add("${OssieHome}/lib");

		// Pre-existing
		ldPaths.add("${env_var:LD_LIBRARY_PATH}");

		StringBuilder ldPathString = new StringBuilder();
		for (String ldPath : ldPaths) {
			ldPathString.append(ldPath);
			ldPathString.append(File.pathSeparatorChar);
		}
		ldPathString.setLength(ldPathString.length() - 1);
		return ldPathString.toString();
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
