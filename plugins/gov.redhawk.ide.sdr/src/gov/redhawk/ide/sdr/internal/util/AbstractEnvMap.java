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
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;

public abstract class AbstractEnvMap {
	
	
	public abstract boolean handles(Implementation impl);

	protected static String getImplProgrammingLanguage(Implementation impl) {
		return (String) ScaEcoreUtils.getFeature(
				impl, SpdPackage.Literals.IMPLEMENTATION__PROGRAMMING_LANGUAGE, SpdPackage.Literals.PROGRAMMING_LANGUAGE__NAME);
	}
	
	protected boolean addToPath(Set<String> path, Implementation impl) throws CoreException {	
		if (!handles(impl)) {
			return false;
		}
		for (Dependency d : impl.getDependency()) {
			if (d.getSoftPkgRef() != null) {
				SoftPkgRef spdRef = d.getSoftPkgRef();
				SoftPkg spd = spdRef.getSoftPkg();
				for (Implementation refImpl : spd.getImplementation()) {
					addToPath(path, refImpl);
				}
			}
		}
		return true;
	}

	protected String reversePath(Collection<String> path) {
		StringBuilder retVal = new StringBuilder();
		Iterator<String> i = path.iterator();
		String s = i.next();
		while (s == null && i.hasNext()) {
			s = i.next();
		}
		if (s != null) {
			retVal.append(s);
		}
		// Insert in reverse order
		while(i.hasNext()) {
			s = i.next();
			if (s != null) {
				retVal.insert(0, s + File.pathSeparatorChar);
			}
		}
		return retVal.toString();
	}
	
	protected String getAbsolutePath(URI pathUri) throws CoreException {
		if (pathUri.isPlatformResource()) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(pathUri.toPlatformString(true)));
			if (resource != null) {
				return "${workspace_loc:" + resource.getFullPath()+"}";
			}
		} else {
			IFileStore store = EFS.getStore(java.net.URI.create(pathUri.toString()));
			if (store.fetchInfo().exists()) {
				File localStore = store.toLocalFile(0, null);
				return localStore.getAbsolutePath();
			}
		}
		return null;
    }

	public abstract void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException;
}
