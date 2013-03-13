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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;

public class CppEnvMap extends AbstractEnvMap {

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("LD_LIBRARY_PATH", get_LD_LIBRARY_Path(impl));
	}

	@Override
	protected boolean addToPath(Set<String> path, Implementation impl) throws CoreException {
		boolean retVal = super.addToPath(path, impl);

		if (retVal) {
			String relativeCodePath = ScaEcoreUtils.getFeature(impl, SpdPackage.Literals.IMPLEMENTATION__CODE, SpdPackage.Literals.CODE__LOCAL_FILE,
			        SpdPackage.Literals.LOCAL_FILE__NAME);
			
			if (impl.eResource() != null) {
				String newPath = createPath(relativeCodePath, impl.eResource().getURI());
				if (newPath != null) {
					path.add(newPath);
				}
			}
		}
		return retVal;
	}
	
	public String createPath(String relativeCodePath, URI spdUri) throws CoreException {
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

	public String get_LD_LIBRARY_Path(Implementation impl) throws CoreException {
		LinkedHashSet<String> ldPath = new LinkedHashSet<String>();
		ldPath.add("${env_var:LD_LIBRARY_PATH}");

		String libFolder = "lib";
		if (Platform.getOSArch().equals(Platform.ARCH_X86_64)) {
			libFolder = "lib64";
		}
		ldPath.add("${OssieHome}/" + libFolder);

		if (impl != null) {
			addToPath(ldPath, impl);
		}

		StringBuilder ldPathString = new StringBuilder();
		Iterator<String> i = ldPath.iterator();
		String s = i.next();
		ldPathString.append(s);
		// Insert in reverse order
		for (; i.hasNext();) {
			s = i.next();
			ldPathString.insert(0, s + File.pathSeparatorChar);
		}
		return ldPathString.toString();
	}

	@Override
	public boolean handles(Implementation impl) {
		String language = getImplProgrammingLanguage(impl);
		return "C++".equalsIgnoreCase(language);
	}

}
