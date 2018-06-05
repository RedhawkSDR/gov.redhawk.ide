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
package gov.redhawk.ide.debug.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.ResourceFactoryOperations;
import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdResourceFactory;
import gov.redhawk.ide.natures.ScaProjectNature;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.sca.util.Debug;
import gov.redhawk.sca.util.MutexRule;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

/**
 * Provides descriptions of resources in the workspace which can be launched in the sandbox.
 */
public class WorkspaceResourceFactoryProvider extends AbstractResourceFactoryProvider {

	private static final MutexRule RULE = new MutexRule(WorkspaceResourceFactoryProvider.class);
	private static final String WORKSPACE_CATEGORY = "Workspace";
	private static final Debug TRACE_LOGGER = new Debug(ScaModelPlugin.ID, "WorkspaceResourceFactoryProvider");

	private final IResourceChangeListener listener = new IResourceChangeListener() {

		@Override
		public void resourceChanged(final IResourceChangeEvent event) {
			if (disposed) {
				return;
			}

			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				Set<IFile> spdsToAdd = new HashSet<>();
				Set<IFile> spdsToRemove = new HashSet<>();
				try {
					event.getDelta().accept(delta -> {
						IResource resource = delta.getResource();
						if (resource instanceof IWorkspaceRoot) {
							return true;
						} else if (resource instanceof IProject) {
							// Check project natures before exploring
							return WorkspaceResourceFactoryProvider.shouldVisit((IProject) resource);
						} else if (resource instanceof IFile && resource.getParent() instanceof IProject) {
							// We only check XML files at the root of the project
							if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
								// SPD file change
								switch (delta.getKind()) {
								case IResourceDelta.ADDED:
								case IResourceDelta.CHANGED:
									spdsToAdd.add((IFile) resource);
									break;
								case IResourceDelta.REMOVED:
									spdsToRemove.remove((IFile) resource);
									break;
								default:
									break;
								}
							} else if (resource.getName().endsWith(PrfPackage.FILE_EXTENSION) || resource.getName().endsWith(ScdPackage.FILE_EXTENSION)) {
								// PRF or SCD file change. Rather than trying to determine which SPD(s) this affects,
								// we'll use a heuristic. All SPDs in the same directory will be assumed to be changed.
								for (IResource sibling : resource.getParent().members()) {
									if (sibling instanceof IFile && sibling.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
										spdsToAdd.add((IFile) sibling);
									}
								}
							}
						}
						return false;
					});
				} catch (CoreException e) {
					ScaDebugPlugin.logWarning("Exception while handling changed resources", e);
				}

				// If a PRF/SCD was touced in any way, and the SPD was deleted, the SPD file could end up in both lists.
				spdsToAdd.removeAll(spdsToRemove);

				// Update the descriptors
				for (IFile spdFile : spdsToRemove) {
					removeSpd(spdFile);
				}
				for (IFile spdFile : spdsToAdd) {
					addSpd(spdFile);
				}
				break;
			case IResourceChangeEvent.PRE_CLOSE:
			case IResourceChangeEvent.PRE_DELETE:
				// Entire project about to be closed or deleted
				final IProject project = (IProject) event.getResource();
				try {
					if (project == null || !WorkspaceResourceFactoryProvider.shouldVisit(project)) {
						return;
					}
					for (final IResource resource : project.members()) {
						if (resource instanceof IFile) {
							final IFile file = (IFile) resource;
							if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
								removeSpd(file);
							}
						}
					}
				} catch (final CoreException e) {
					ScaDebugPlugin.logWarning("Exception while listing project members", e);
				}
				break;
			default:
				break;
			}
		}
	};

	private final Map<IFile, ResourceDesc> componentMap = Collections.synchronizedMap(new HashMap<IFile, ResourceDesc>());
	private boolean disposed;

	public WorkspaceResourceFactoryProvider() {
		try {
			ResourcesPlugin.getWorkspace().getRoot().accept(new IResourceVisitor() {

				@Override
				public boolean visit(final IResource resource) throws CoreException {
					if (resource instanceof IWorkspaceRoot) {
						return true;
					} else if (resource instanceof IProject) {
						return WorkspaceResourceFactoryProvider.shouldVisit((IProject) resource);
					} else if (resource instanceof IFile && resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
						addSpd((IFile) resource);
					}
					return false;
				}
			});
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(
				new Status(e.getStatus().getSeverity(), ScaDebugPlugin.ID, "Error while to creating WorkspaceResourceFactoryProvider", e));
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.listener,
			IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_CLOSE | IResourceChangeEvent.PRE_DELETE);
	}

	private SoftPkg loadSpd(IFile resource) {
		try {
			final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
			final SoftPkg spd = SoftPkg.Util.getSoftPkg(
				resourceSet.getResource(URI.createPlatformResourceURI(resource.getFullPath().toPortableString(), true), true));
			return spd;
		} catch (WrappedException we) {
			if (TRACE_LOGGER.enabled) {
				TRACE_LOGGER.catching("Unable to load SPD: " + resource, we);
			}
			return null;
		}
	}

	private void addSpd(final IFile resource) {
		if (componentMap.containsKey(resource)) {
			// Remove old component previously mapped
			removeSpd(resource);
		}

		SoftPkg spd = loadSpd(resource);
		if (spd == null) {
			return;
		}

		ResourceFactoryOperations resourceFactory;
		try {
			resourceFactory = SpdResourceFactory.createResourceFactory(spd);
		} catch (IllegalArgumentException e) {
			// Ignore invalid SPDs
			return;
		}
		ComponentDesc desc = new ComponentDesc(spd, resourceFactory);
		desc.setCategory(WORKSPACE_CATEGORY);
		this.componentMap.put(resource, desc);
		addResourceDesc(desc);
	}

	private void removeSpd(final IFile resource) {
		final ResourceDesc desc = this.componentMap.remove(resource);
		if (desc != null) {
			removeResourceDesc(desc);
		}
	}

	public static boolean shouldVisit(final IProject project) throws CoreException {
		if (!project.isOpen()) {
			return false;
		}
		final IProjectDescription desc = project.getDescription();
		final List<String> natures = Arrays.asList(desc.getNatureIds());
		return natures.contains(ScaProjectNature.ID) && desc.getName().charAt(0) != '.';
	}

	@Override
	public void dispose() {
		Job.getJobManager().beginRule(RULE, null);
		try {
			if (disposed) {
				return;
			}
			disposed = true;
		} finally {
			Job.getJobManager().endRule(RULE);
		}
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.listener);

		synchronized (this.componentMap) {
			for (final ResourceDesc desc : this.componentMap.values()) {
				removeResourceDesc(desc);
			}
			this.componentMap.clear();
		}
	}

}
