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

import gov.redhawk.core.resourcefactory.AbstractResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ComponentDesc;
import gov.redhawk.core.resourcefactory.IResourceFactoryProvider;
import gov.redhawk.core.resourcefactory.ResourceDesc;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.SpdResourceFactory;
import gov.redhawk.ide.debug.WorkspaceWaveformFactory;
import gov.redhawk.ide.natures.ScaProjectNature;
import gov.redhawk.sca.util.MutexRule;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import CF.ResourceFactoryOperations;

/**
 * 
 */
public class WorkspaceResourceFactoryProvider extends AbstractResourceFactoryProvider implements IResourceFactoryProvider {

	private static final MutexRule RULE = new MutexRule(WorkspaceResourceFactoryProvider.class);
	private static final String WORKSPACE_CATEGORY = "Workspace";
	private final IResourceChangeListener listener = new IResourceChangeListener() {

		public void resourceChanged(final IResourceChangeEvent event) {
			if (disposed) {
				return;
			}
			try {
				if (event.getResource() == null || !WorkspaceResourceFactoryProvider.shouldVisit(event.getResource().getProject())) {
					return;
				}
			} catch (final CoreException e1) {
				return;
			}
			switch (event.getType()) {
			case IResourceChangeEvent.PRE_REFRESH:
			case IResourceChangeEvent.POST_CHANGE:

				if (event.getResource() instanceof IFile) {
					final IFile file = (IFile) event.getResource();
					try {
						if (file.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
							addResource(file, createSpdResourceFactory(file));
						} else if (file.getName().endsWith(SadPackage.FILE_EXTENSION)) {
							addResource(file, new WorkspaceWaveformFactory(file));
						}
					} catch (IOException e) {
						ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to add workspace resource: "
						                                              + file.getFullPath(), e));
					}
				} else if (event.getResource() instanceof IProject) {
					final IProject project = (IProject) event.getResource();
					try {
						for (final IResource resource : project.members()) {
							if (resource instanceof IFile) {
								try {
									final IFile file = (IFile) resource;
									if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
										addResource(file, createSpdResourceFactory((IFile) resource));
									} else if (resource.getName().endsWith(SadPackage.FILE_EXTENSION)) {
										addResource(file, new WorkspaceWaveformFactory((IFile) resource));
									}
								} catch (IOException e) {
									ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to add workspace resource: "
									                                              + resource.getFullPath(), e));
								}
							}
						}
					} catch (CoreException e) {
						ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID,
						                                              "Failed to search project contents for new resources to add: "
						                                                  + event.getResource().getFullPath(), e));
					}
				}
				break;

			case IResourceChangeEvent.PRE_CLOSE:
			case IResourceChangeEvent.PRE_DELETE:
				if (event.getResource() instanceof IFile) {
					removeComponent((IFile) event.getResource());
				} else if (event.getResource() instanceof IProject) {
					final IProject project = (IProject) event.getResource();
					try {
						for (final IResource resource : project.members()) {
							if (resource instanceof IFile) {
								final IFile file = (IFile) resource;
								if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION) || resource.getName().endsWith(SadPackage.FILE_EXTENSION)) {
									removeComponent(file);
								}
							}
						}
					} catch (final CoreException e) {
						// PASS
					}
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

				public boolean visit(final IResource resource) throws CoreException {
					try {
						if (resource instanceof IWorkspaceRoot) {
							return true;
						} else if (resource instanceof IProject) {
							return WorkspaceResourceFactoryProvider.shouldVisit((IProject) resource);
						} else if (resource instanceof IFile && resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
							addResource((IFile) resource, createSpdResourceFactory((IFile) resource));
						} else if (resource instanceof IFile && resource.getName().endsWith(SadPackage.FILE_EXTENSION)) {
							addResource((IFile) resource, new WorkspaceWaveformFactory((IFile) resource));
						}
					} catch (IOException e) {
						ScaDebugPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ScaDebugPlugin.ID, "Failed to add workspace resource: "
						                                              + resource.getFullPath(), e));
					}
					return false;
				}
			});
		} catch (final CoreException e) {
			ScaDebugPlugin.getInstance().getLog().log(e.getStatus());
		}
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.listener,
		                                                         IResourceChangeEvent.POST_CHANGE | IResourceChangeEvent.PRE_CLOSE
		                                                             | IResourceChangeEvent.PRE_DELETE | IResourceChangeEvent.PRE_REFRESH);
	}

	protected ResourceFactoryOperations createSpdResourceFactory(IFile file) {
		SoftPkg spd = loadSpd(file);
		if (spd == null) {
			return null;
		}
		return new SpdResourceFactory(spd);
	}

	protected String getTypeDir(final IFile resource) {
		if (resource.getName().endsWith(SadPackage.FILE_EXTENSION)) {
			return "waveforms";
		}
		if (resource.getName().endsWith(DcdPackage.FILE_EXTENSION)) {
			return "nodes";
		}
		SoftPkg spd = loadSpd(resource);
		SoftwareComponent scd = ScaEcoreUtils.getFeature(spd, SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, SpdPackage.Literals.DESCRIPTOR__COMPONENT);
		switch (SoftwareComponent.Util.getWellKnownComponentType(scd)) {
		case DEVICE:
			return "devices";
		case SERVICE:
			return "services";
		default:
			return "components";
		}
	}

	private SoftPkg loadSpd(IFile resource) {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final SoftPkg spd = SoftPkg.Util.getSoftPkg(resourceSet.getResource(URI.createPlatformResourceURI(resource.getFullPath().toPortableString(), true),
		                                                                    true));
		return spd;
	}

	private void addResource(final IFile resource, final ResourceFactoryOperations resourceFactory) {
		if (resourceFactory == null) {
			return;
		}
		SoftPkg spd = loadSpd(resource);
		if (spd == null) {
			return;
		}
		ComponentDesc desc = new ComponentDesc(spd, new SpdResourceFactory(spd));
		desc.setCategory(WORKSPACE_CATEGORY);
		this.componentMap.put(resource, desc);
		addResourceDesc(desc);
	}

	private void removeComponent(final IFile resource) {
		final ResourceDesc desc = this.componentMap.remove(resource);
		if (desc != null) {
			removeResourceDesc(desc);
		}
	}

	public static boolean shouldVisit(final IProject project) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final List<String> natures = Arrays.asList(desc.getNatureIds());
		return natures.contains(ScaProjectNature.ID) && desc.getName().charAt(0) != '.';
	}

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
