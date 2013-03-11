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
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

public class JavaEnvMap extends AbstractEnvMap {
	

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("CLASSPATH", getJavaClassPath(impl));
	}

	public String getJavaClassPath(Implementation impl) throws CoreException {
		// java lib is always in the "lib" sub directory
		final Set<String> classpath = new LinkedHashSet<String>();
		classpath.add("${env_var:CLASSPATH}");
		classpath.add("${OssieHome}/lib/*");
		if (impl != null) {
			addToPath(classpath, impl);
		}
		return reversePath(classpath);
	}

	protected boolean addToPath(Set<String> path, Implementation impl) throws CoreException {
		boolean retVal = super.addToPath(path, impl);
		if (retVal) {
			String relativeCodePath = ScaEcoreUtils.getFeature(impl, SpdPackage.Literals.IMPLEMENTATION__CODE, SpdPackage.Literals.CODE__LOCAL_FILE,
			        SpdPackage.Literals.LOCAL_FILE__NAME);
	
			String[] dir = URI.createFileURI(relativeCodePath).segments();
			if (impl.eResource() != null && impl.eResource().getURI() != null) {
				URI pathUri = impl.eResource().getURI().trimSegments(1).appendSegments(dir);
				if ("jar".equals(pathUri.fileExtension())) {
					path.add(getAbsolutePath(pathUri));
				} else {
					URI uri = pathUri.appendSegment("bin");
					IFileStore store = EFS.getStore(java.net.URI.create(uri.toString()));
					if (store.fetchInfo().exists()) {
						path.add(getAbsolutePath(uri));
					}
					path.add(getAbsolutePath(pathUri) + File.separator + "*");
				}
			}
		}
		return retVal;
	}
	
	public String createPath(String relativePath, URI spdUri) throws CoreException {
		if (spdUri == null || relativePath == null) {
			return null;
		}
		
		URI fullPath = spdUri.trimSegments(1).appendSegments(URI.createFileURI(relativePath).segments());
		IFileStore store = EFS.getStore(java.net.URI.create(fullPath.toString()));
		IFileInfo info = store.fetchInfo();
		if (info.exists()) {
			if (info.isDirectory()) {
				File dir = store.toLocalFile(0, null);
				return dir.toString() + File.separator + "*";
			} else {
				File file = store.toLocalFile(0, null);
				return file.toString();
			}
		}
		
		return null;
	}

	@Override
	public boolean handles(Implementation impl) {
		String language = getImplProgrammingLanguage(impl);
	    return "java".equalsIgnoreCase(language);
    }

}
