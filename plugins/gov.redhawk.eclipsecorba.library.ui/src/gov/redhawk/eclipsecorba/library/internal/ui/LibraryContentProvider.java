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
package gov.redhawk.eclipsecorba.library.internal.ui;

import gov.redhawk.eclipsecorba.idl.expressions.util.ExpressionsAdapterFactory;
import gov.redhawk.eclipsecorba.idl.operations.provider.OperationsItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.provider.IdlItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.idl.types.provider.TypesItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.IdlLibrary;
import gov.redhawk.eclipsecorba.library.provider.IdlLibraryItemProviderAdapterFactory;
import gov.redhawk.eclipsecorba.library.ui.LibraryUIPlugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.progress.DeferredTreeContentManager;
import org.eclipse.ui.progress.IDeferredWorkbenchAdapter;
import org.eclipse.ui.progress.PendingUpdateAdapter;
import org.eclipse.ui.progress.WorkbenchJob;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class LibraryContentProvider extends DynamicTransactionalAdapterFactoryContentProvider {

	private DeferredTreeContentManager contentManager;

	private final DeferredLibraryAdapter adapter = new DeferredLibraryAdapter();

	private final IResourceChangeListener resourceListener = new IResourceChangeListener() {

		public void resourceChanged(final IResourceChangeEvent event) {
			final IResourceDelta delta = event.getDelta();
			if (delta == null) {
				return;
			}
			try {
				final ResourceDeltaVisitor visitor = new ResourceDeltaVisitor();
				delta.accept(visitor);

				for (final IResource resource : visitor.getChangedResources()) {
					refresh(resource.getProject());
				}

			} catch (final CoreException exception) {
				StatusManager.getManager().handle(exception, LibraryUIPlugin.PLUGIN_ID);
			}
		}
	};

	private void refresh(final Object obj) {
		if (obj == null) {
			return;
		}
		this.adapter.getLibraryMap().remove(obj);
		if (this.viewer != null) {
			final WorkbenchJob job = new WorkbenchJob(this.viewer.getControl().getDisplay(), "Refresh") {

				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor) {
					if (LibraryContentProvider.this.viewer instanceof StructuredViewer) {
						((StructuredViewer) LibraryContentProvider.this.viewer).refresh(obj);
					} else {
						LibraryContentProvider.this.viewer.refresh();
					}
					return Status.OK_STATUS;
				}
			};
			job.setSystem(true);
			job.schedule();
		}
	}

	public LibraryContentProvider() {
		super(LibraryContentProvider.createAdapterFactory());
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this.resourceListener);
	}

	private static AdapterFactory createAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new IdlLibraryItemProviderAdapterFactory());
		factory.addAdapterFactory(new IdlItemProviderAdapterFactory());
		factory.addAdapterFactory(new OperationsItemProviderAdapterFactory());
		factory.addAdapterFactory(new ExpressionsAdapterFactory());
		factory.addAdapterFactory(new TypesItemProviderAdapterFactory());
		return factory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(final Object object) {
		if (object instanceof IProject) {
			return this.contentManager.mayHaveChildren(object);
		}
		return super.hasChildren(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object object) {
		if (object instanceof IProject) {
			final IdlLibrary library = this.adapter.getLibraryMap().get(object);
			if (library != null) {
				return new Object[] {
					library
				};
			}
			return this.contentManager.getChildren(object);
		}
		return super.getChildren(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this.resourceListener);
		((ComposedAdapterFactory) getAdapterFactory()).dispose();
		super.dispose();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);
		this.viewer = viewer;
		this.contentManager = new DeferredTreeContentManager((AbstractTreeViewer) viewer) {
			/**
			 * {@inheritDoc}
			 */
			@Override
			protected IDeferredWorkbenchAdapter getAdapter(final Object element) {
				if (element instanceof IProject) {
					return LibraryContentProvider.this.adapter;
				}
				return null;
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			protected PendingUpdateAdapter createPendingUpdateAdapter() {
				return new LibraryPendingUpdateAdapter();
			}
		};
	}

}
