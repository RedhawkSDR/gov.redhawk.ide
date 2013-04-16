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
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.ide.sad.internal.ui.editor.CustomDiagramEditor;
import gov.redhawk.ide.sad.ui.providers.SpdToolEntry;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.ui.progress.WorkbenchJob;

public class SandboxDiagramEditor extends CustomDiagramEditor {
	protected static final EStructuralFeature[] SCD = new EStructuralFeature[] {
	        SpdPackage.Literals.SOFT_PKG__DESCRIPTOR, SpdPackage.Literals.DESCRIPTOR__COMPONENT
	};
	private final Job getComponentsJob = new Job("Refreshing workspace components") {

		{
			setSystem(true);
		}

		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final List<PaletteEntry> entriesToAdd = getWorkspaceComponentTools();
			final WorkbenchJob job = new WorkbenchJob("Refresh palette") {

				{
					setSystem(true);
				}

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					SandboxDiagramEditor.this.workspaceDrawer.setChildren(entriesToAdd);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
			return Status.OK_STATUS;
		}
	};
	private final PaletteDrawer workspaceDrawer = new PaletteDrawer("Workspace");
	private final IResourceChangeListener listener = new IResourceChangeListener() {

		public void resourceChanged(final IResourceChangeEvent event) {
			final boolean[] shouldRefresh = {
				false
			};
			switch (event.getType()) {
			case IResourceChangeEvent.POST_CHANGE:
				if (event.getDelta() == null) {
					return;
				}
				switch (event.getDelta().getKind()) {
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
				case IResourceDelta.REMOVED:
					try {
						event.getDelta().accept(new IResourceDeltaVisitor() {

							public boolean visit(final IResourceDelta delta) throws CoreException {
								if (shouldRefresh[0]) {
									return false;
								}
								final IResource resource = delta.getResource();
								if (resource instanceof IFile) {
									shouldRefresh[0] = delta.getResource().getName().endsWith(SpdPackage.FILE_EXTENSION);
								}
								return !shouldRefresh[0];
							}
						});
					} catch (final CoreException e) {
						// PASS
					}
					break;
				default:
					break;
				}
				if (shouldRefresh[0]) {
					SandboxDiagramEditor.this.getComponentsJob.schedule(1000);
				}
				break;
			default:
				break;
			}
		}
	};

	public SandboxDiagramEditor(final SCAFormEditor editor) {
		super(editor);

		this.workspaceDrawer.add(new PaletteSeparator("components"));
		this.workspaceDrawer.setInitialState(PaletteDrawer.INITIAL_STATE_PINNED_OPEN);
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.listener);
		super.dispose();
	}

	@Override
	protected PaletteRoot createPaletteRoot(final PaletteRoot existingPaletteRoot) {
		final PaletteRoot result = super.createPaletteRoot(existingPaletteRoot);
		final List<PaletteEntry> entriesToRemove = new ArrayList<PaletteEntry>();
		for (final Iterator< ? > iterator = result.getChildren().iterator(); iterator.hasNext();) {
			final Object obj = iterator.next();
			if (obj instanceof PaletteEntry) {
				final PaletteEntry entry = (PaletteEntry) obj;
				if (entry.getId().equals("findBy")) {
					entriesToRemove.add(entry);
				} else if (entry.getId().equals("createBaseTypes1Group")) {
					entriesToRemove.add(entry);
				}
			}
		}
		for (final PaletteEntry entry : entriesToRemove) {
			result.remove(entry);
		}

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.listener);
		this.workspaceDrawer.setChildren(getWorkspaceComponentTools());
		result.add(this.workspaceDrawer);

		return result;
	}

	private List<PaletteEntry> getWorkspaceComponentTools() {
		final List<PaletteEntry> entriesToAdd = new ArrayList<PaletteEntry>();
		final ResourceSet resourceSet = new ResourceSetImpl();

		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		try {
			root.accept(new IResourceVisitor() {

				public boolean visit(final IResource resource) throws CoreException {
					if (resource instanceof IWorkspaceRoot) {
						return true;
					}
					if (resource == null || !resource.isAccessible() || resource.getProject() == null || resource.getProject().getDescription() == null
					        || !resource.getProject().getDescription().hasNature(ScaComponentProjectNature.ID)) {
						return false;
					}
					if (resource instanceof IFile) {
						if (resource.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
							try {
								final Resource spdResource = resourceSet.getResource(URI.createPlatformResourceURI(resource.getFullPath().toPortableString(),
								        true), true);
								final SoftPkg spd = SoftPkg.Util.getSoftPkg(spdResource);
								boolean add = false;
								out:for (Implementation impl : spd.getImplementation()) {
									Code code = impl.getCode();
									if (code == null) {
										add = false;
										break out;
									}
									CodeFileType type = code.getType();
									if (type == null) {
										add = false;
										break out;
									} 
									switch(type) {
									case EXECUTABLE:
										add=true;
										break out;
									default:
										add = false;
										break out;
									}
								}
								if (add) {
									final SpdToolEntry entry = new SpdToolEntry(spd);
									entriesToAdd.add(entry);
								}
							} catch (final Exception e) {
								// PASS
							}
						}
						return false;
					}
					return true;
				}
			});
		} catch (final CoreException e) {
			// PASS
		}
		Collections.sort(entriesToAdd, new Comparator<PaletteEntry>() {

			public int compare(final PaletteEntry o1, final PaletteEntry o2) {
				final String str1 = o1.getLabel();
				final String str2 = o2.getLabel();
				if (str1 == null) {
					if (str2 == null) {
						return 0;
					} else {
						return 1;
					}
				} else if (str2 == null) {
					return -1;
				} else {
					return str1.compareToIgnoreCase(str2);
				}
			}

		});
		return entriesToAdd;
	}
}
