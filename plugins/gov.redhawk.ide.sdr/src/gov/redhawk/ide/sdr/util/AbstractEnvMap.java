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
package gov.redhawk.ide.sdr.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.Bundle;

import gov.redhawk.ide.sdr.IdeSdrActivator;
import mil.jpeojtrs.sca.spd.Dependency;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SoftPkgRef;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * @since 8.2
 */
public abstract class AbstractEnvMap implements IEnvMap {

	/**
	 * @since 10.0
	 */
	protected void addToPath(Set<String> path, Implementation impl) throws CoreException {
		String relativeCodePath = ScaEcoreUtils.getFeature(impl, SpdPackage.Literals.IMPLEMENTATION__CODE, SpdPackage.Literals.CODE__LOCAL_FILE,
			SpdPackage.Literals.LOCAL_FILE__NAME);

		if (impl.eResource() != null) {
			String newPath = createPath(relativeCodePath, impl.eResource().getURI());
			if (newPath != null) {
				path.add(newPath);
			}
		}
	}

	/**
	 * Create and return an appropriate absolute file path using the relative path and the SPD's URI
	 * @param relativeCodePath The location relative to the SPD's base location
	 * @param spdUri The SPD's URI
	 * @return The appropriate path, or null if none
	 * @throws CoreException
	 * @since 10.0
	 */
	protected abstract String createPath(String relativeCodePath, URI spdUri) throws CoreException;

	protected String getAbsolutePath(URI pathUri) throws CoreException {
		if (pathUri.isPlatformPlugin()) {
			String bundleId = pathUri.segment(1);
			Bundle bundle = Platform.getBundle(bundleId);
			IPath path = new Path(pathUri.toPlatformString(true));
			path = path.removeFirstSegments(1);
			URL url = FileLocator.find(bundle, path, null);
			if (url != null) {
				try {
					url = FileLocator.toFileURL(url);
					return new File(url.toURI()).getAbsolutePath();
				} catch (IOException e) {
					throw new CoreException(new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, "Failed to find bundle file: " + pathUri, e));
				} catch (URISyntaxException e) {
					throw new CoreException(new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, "Failed to find bundle file: " + pathUri, e));
				}
			} else {
				throw new CoreException(new Status(Status.ERROR, IdeSdrActivator.PLUGIN_ID, "Failed to find bundle file: " + pathUri, null));
			}
		} else if (pathUri.isPlatformResource()) {
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(pathUri.toPlatformString(true)));
			if (resource != null) {
				return "${workspace_loc:" + resource.getFullPath() + "}";
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

	/**
	 * Gets the list of shared library implementations which are dependencies of this implementation. The list does
	 * not contain duplicates, and the ordering is intended to match the core framework's traversal.
	 * <p/>
	 * A dependency tree is traversed in pre-order, depth-first. The list of dependencies within an implementation is
	 * processed in reverse order.
	 * <p/>
	 * The first implementation of a given dependency is always selected as the one to be used (we don't evaluate
	 * whether one implementation is more suitable than another).
	 * @param impl
	 * @return
	 * @throws CoreException A referenced dependency cannot be found/loaded
	 * @since 10.0
	 */
	protected List<Implementation> getDependencyImplementations(final Implementation impl) throws CoreException {
		if (impl == null) {
			return Collections.emptyList();
		}

		// Get the SPDs which are dependencies of our starting implementation
		LinkedList<SoftPkg> depQueue = new LinkedList<SoftPkg>();
		for (Dependency dep : impl.getDependency()) {
			if (dep.getSoftPkgRef() != null) {
				SoftPkgRef spdRef = dep.getSoftPkgRef();
				SoftPkg spd = spdRef.getSoftPkg();
				if (spd == null) {
					String errorMsg = String.format("Unable to find / load softpkg dependency '%s' (dependency of softpkg '%s', implementation '%s')",
						spdRef.getLocalFile().getName(), impl.getSoftPkg().getName(), impl.getId());
					throw new CoreException(new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, errorMsg));
				}

				// Prevent circular self-reference
				if (spd.equals(impl.getSoftPkg())) {
					continue;
				}

				depQueue.push(spd);
			}
		}

		// Work off dependencies in pre-order, depth-first
		Set<SoftPkg> visitedSpds = new HashSet<SoftPkg>();
		List<Implementation> depImpls = new ArrayList<Implementation>();
		while (depQueue.size() > 0) {
			SoftPkg currentSpd = depQueue.pop();

			// Prevent circular recursion
			if (visitedSpds.contains(currentSpd)) {
				continue;
			}
			visitedSpds.add(currentSpd);

			// We choose the first implementation, and add to the result list (pre-order)
			Implementation currentImpl = currentSpd.getImplementation().get(0);
			if (!currentImpl.isSharedLibrary()) {
				continue;
			}
			depImpls.add(currentImpl);

			// Push the selected implementation's SPD dependencies on the stack (depth-first)
			for (Dependency currentImplDep : currentImpl.getDependency()) {
				if (currentImplDep.getSoftPkgRef() != null) {
					SoftPkgRef spdRef = currentImplDep.getSoftPkgRef();
					SoftPkg depSpd = spdRef.getSoftPkg();
					if (depSpd == null) {
						String errorMsg = String.format("Unable to find / load softpkg dependency '%s' (dependency of softpkg '%s', implementation '%s')",
							spdRef.getLocalFile().getName(), currentImpl.getSoftPkg().getName(), currentImpl.getId());
						throw new CoreException(new Status(IStatus.ERROR, IdeSdrActivator.PLUGIN_ID, errorMsg));
					}

					depQueue.push(depSpd);
				}
			}
		}

		return depImpls;
	}
}
