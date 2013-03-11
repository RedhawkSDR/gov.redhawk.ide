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
package gov.redhawk.ide.sad.internal.ui.editor;

import gov.redhawk.diagram.DiagramUtil;
import gov.redhawk.diagram.editor.URIEditorInputProxy;
import gov.redhawk.ide.internal.ui.handlers.CleanUpComponentFilesAction;
import gov.redhawk.ide.sad.ui.SadUiActivator;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.util.ModelUtil;
import gov.redhawk.sca.sad.diagram.SadDiagramUtilHelper;
import gov.redhawk.sca.sad.diagram.part.SadDiagramEditor;
import gov.redhawk.sca.util.PluginUtil;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.prf.provider.PrfItemProviderAdapterFactory;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;
import mil.jpeojtrs.sca.scd.provider.ScdItemProviderAdapterFactory;
import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.impl.EStructuralFeatureImpl.ContainmentUpdatingFeatureMapEntry;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.DelegatingWrapperItemProvider;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramEditPart;
import org.eclipse.gmf.runtime.diagram.ui.editparts.IGraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramGraphicalViewer;
import org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart;
import org.eclipse.gmf.runtime.diagram.ui.properties.views.PropertiesBrowserPage;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.ide.document.FileEditorInputProxy;
import org.eclipse.gmf.runtime.emf.core.util.EMFCoreUtil;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import CF.Application;

/**
 * This is an example of a Sad model editor.
 */
public class SadEditor extends SCAFormEditor implements ITabbedPropertySheetPageContributor, IDiagramWorkbenchPart, IViewerProvider {

	public static final String ID = "gov.redhawk.ide.sad.ui.editor.presentation.SadEditorID";

	public static final String EDITING_DOMAIN_ID = SadDiagramEditor.EDITING_DOMAIN_ID;

	/**
	 * This is the content outline page.
	 */
	private IContentOutlinePage contentOutlinePage;

	/**
	 * This is a kludge...
	 */
	private IStatusLineManager contentOutlineStatusLineManager;

	/**
	 * This is the content outline page's viewer.
	 */
	private TreeViewer contentOutlineViewer;

	/**
	 * The graphical diagram editor embedded into this editor.
	 */
	private SadDiagramEditor diagramEditor;

	/**
	 * This keeps track of the active content viewer, which may be either one of
	 * the viewers in the pages or the content outline viewer.
	 */
	private Viewer currentViewer;

	/**
	 * This selection provider coordinates the selections of the various editor
	 * parts.
	 */
	private final MultiPageSelectionProvider selectionProvider;

	private IEditorInput wrappedInput;

	private ResourceListener nameListener;

	private IFormPage overviewPage;

	private class ResourceListener extends AdapterImpl {
		private SoftwareAssembly sad;
		private final Resource sadResource;

		public ResourceListener(final Resource spdResource) {
			this.sadResource = spdResource;
			if (this.sadResource != null) {
				this.sadResource.eAdapters().add(this);
				this.sad = getSoftwareAssembly();
				if (this.sad != null) {
					this.sad.eAdapters().add(this);
					updateTitle();
				}
			}
		}

		/**
		 * Gets the soft pkg.
		 * 
		 * @return the soft pkg
		 */
		private SoftwareAssembly getSoftwareAssembly() {
			return ModelUtil.getSoftwareAssembly(this.sadResource);
		}

		public void dispose() {
			if (this.sad != null) {
				this.sad.eAdapters().remove(this);
			}
			if (this.sadResource != null) {
				this.sadResource.eAdapters().remove(this);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void notifyChanged(final Notification msg) {
			if (msg.getNotifier() instanceof Resource) {
				switch (msg.getFeatureID(Resource.class)) {
				case Resource.RESOURCE__IS_LOADED:
					if (this.sad != null) {
						this.sad.eAdapters().remove(this);
						this.sad = null;
					}
					if (this.sadResource.isLoaded()) {
						this.sad = getSoftwareAssembly();
						if (this.sad != null) {
							this.sad.eAdapters().add(this);
							updateTitle();
						}
					}
					break;
				default:
					break;
				}
			} else if (msg.getNotifier() instanceof SoftwareAssembly) {
				final int featureID = msg.getFeatureID(SoftwareAssembly.class);

				if (featureID == SadPackage.SOFTWARE_ASSEMBLY__NAME) {
					if (msg.getEventType() == Notification.SET) {
						updateTitle();
					}
				}
			}
		}
	}

	/**
	 * This creates a model editor.
	 */
	public SadEditor() {
		super();
		this.selectionProvider = new MultiPageSelectionProvider(this);
		this.selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(final SelectionChangedEvent event) {
				setStatusLineManager(event.getSelection());
			}
		});
	}

	/**
	 * This is here for the listener to be able to call it.
	 */
	@Override
	protected void firePropertyChange(final int action) {
		super.firePropertyChange(action);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActivePage(final int pageIndex) {
		if (pageIndex >= 0 && pageIndex < getPageCount()) {
			super.setActivePage(pageIndex);
		}
	}

	/**
	 * Lazy initialization of the wrapped editor input.
	 * 
	 * @return
	 */
	protected IEditorInput getWrappedInput() {
		if (this.wrappedInput == null) {
			if (getEditorInput() instanceof IFileEditorInput) {
				this.wrappedInput = new FileEditorInputProxy((IFileEditorInput) getEditorInput(), (TransactionalEditingDomain) getEditingDomain());
			} else if (getEditorInput() instanceof URIEditorInput) {
				this.wrappedInput = new URIEditorInputProxy((URIEditorInput) getEditorInput(), (TransactionalEditingDomain) getEditingDomain());
			} else {
				// should not happen, but who knows...
				this.wrappedInput = getEditorInput();
			}
		}
		return this.wrappedInput;
	}

	/**
	 * This sets the selection into whichever viewer is active.
	 */
	@Override
	public void setSelectionToViewer(final Collection< ? > collection) {
		final Collection< ? > theSelection = collection;
		// Make sure it's okay.
		//
		if (theSelection != null && !theSelection.isEmpty()) {
			// I don't know if this should be run this deferred
			// because we might have to give the editor a chance to process the
			// viewer update events
			// and hence to update the views first.
			//
			//
			final Runnable runnable = new Runnable() {
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					//
					if (SadEditor.this.currentViewer != null) {
						SadEditor.this.currentViewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
					}
				}
			};
			runnable.run();
		}
	}

	/**
	 * This makes sure that one content viewer, either for the current page or
	 * the outline view, if it has focus, is the current one.
	 */
	public void setCurrentViewer(final Viewer viewer) {
		// If it is changing...
		//
		if (this.currentViewer != viewer) {
			// Remember it.
			//
			this.currentViewer = viewer;
		}
	}

	/**
	 * This returns the viewer as required by the {@link IViewerProvider}
	 * interface.
	 */
	@Override
	public Viewer getViewer() {
		return this.currentViewer;
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings({
		"unchecked"
	})
	@Override
	public Object getAdapter(final Class key) {
		if (key.equals(IPropertySheetPage.class)) {
			return getPropertySheetPage();
		} else if (key.equals(IGotoMarker.class)) {
			return this;
		} else if (key.equals(ScaWaveform.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else if (key.isAssignableFrom(Application.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else {
			return super.getAdapter(key);
		}
	}

	/**
	 * This accesses a cached version of the content outliner.
	 */
	public IContentOutlinePage getContentOutlinePage() {
		return this.contentOutlinePage;
	}

	/**
	 * This accesses a cached version of the property sheet.
	 */
	public IPropertySheetPage getPropertySheetPage() {
		return new PropertiesBrowserPage(this);
	}

	@Override
	protected String getPropertyEditorPageKey(final IFileEditorInput input) {
		final String retVal = super.getPropertyEditorPageKey(input);
		if (retVal == null) {
			return "1";
		}
		return retVal;
	}

	/**
	 * This deals with how we want selection in the outliner to affect the other
	 * views.
	 */
	@SuppressWarnings("unchecked")
	public void handleContentOutlineSelection(final Object selection) {
		// // If the diagram viewer is active, we need to map the selection
		// // to the corresponding EditParts.
		final List<Object> selectionList = new ArrayList<Object>();
		if (selection instanceof DelegatingWrapperItemProvider) {
			final Object item = ((DelegatingWrapperItemProvider) selection).getValue();
			if (item instanceof ContainmentUpdatingFeatureMapEntry) {
				final Object value = ((ContainmentUpdatingFeatureMapEntry) item).getValue();
				if (value instanceof EObject) {
					final String elementID = EMFCoreUtil.getProxyID((EObject) value);
					selectionList.addAll(this.diagramEditor.getDiagramGraphicalViewer().findEditPartsForElement(elementID, IGraphicalEditPart.class));
				}
				this.selectionProvider.setSelection(new StructuredSelection(selectionList));
			} else if (item instanceof DelegatingWrapperItemProvider) {
				this.handleContentOutlineSelection(item);
			}
		}
	}

	@Override
	public void gotoMarker(final IMarker marker) {
		try {
			if (marker.getType().equals(EValidator.MARKER)) {
				final String uriAttribute = marker.getAttribute(EValidator.URI_ATTRIBUTE, null);
				if (uriAttribute != null) {
					final URI uri = URI.createURI(uriAttribute);
					final EObject eObject = getEditingDomain().getResourceSet().getEObject(uri, true);
					if (eObject != null) {
						setSelectionToViewer(Collections.singleton(getWrapper(eObject)));
					}
				}
			}
		} catch (final CoreException exception) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to go to marker.", exception),
			        StatusManager.LOG | StatusManager.SHOW);
		}
	}

	private Object getWrapper(final EObject eObject) {
		return AdapterFactoryEditingDomain.getWrapper(eObject, getEditingDomain());
	}

	public void setStatusLineManager(final ISelection selection) {
		final IStatusLineManager statusLineManager;
		if (this.currentViewer != null && this.currentViewer == this.contentOutlineViewer) {
			statusLineManager = this.contentOutlineStatusLineManager;
		} else {
			statusLineManager = getActionBars().getStatusLineManager();
		}

		if (statusLineManager != null) {
			if (selection instanceof IStructuredSelection) {
				final Collection< ? > collection = ((IStructuredSelection) selection).toList();
				switch (collection.size()) {
				case 0:
					statusLineManager.setMessage("Selected Nothing");
					break;
				case 1:
					final String text = new AdapterFactoryItemDelegator(getAdapterFactory()).getText(collection.iterator().next());
					statusLineManager.setMessage(MessageFormat.format("Selected Object: {0}", text));
					break;
				default:
					statusLineManager.setMessage(MessageFormat.format("Selected {0} Objects", Integer.toString(collection.size())));
					break;
				}
			} else {
				statusLineManager.setMessage("");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDirty() {
		if (this.getMainResource() != null && !this.getMainResource().getURI().isPlatform()) {
			return false;
		}
		return super.isDirty();
	}

	/**
	 * This implements {@link org.eclipse.jface.action.IMenuListener} to help
	 * fill the context menus with contributions from the Edit menu.
	 */
	@Override
	public void menuAboutToShow(final IMenuManager menuManager) {
		((IMenuListener) getEditorSite().getActionBarContributor()).menuAboutToShow(menuManager);
	}

	@Override
	public void dispose() {
		if (this.contentOutlinePage != null) {
			this.contentOutlinePage.dispose();
		}

		if (this.nameListener != null) {
			this.nameListener.dispose();
			this.nameListener = null;
		}
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor
	 * #getContributorId()
	 */
	public String getContributorId() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getContributorId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#getDiagram
	 * ()
	 */
	public Diagram getDiagram() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getDiagram();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
	 * getDiagramEditDomain()
	 */
	public IDiagramEditDomain getDiagramEditDomain() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getDiagramEditDomain();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
	 * getDiagramEditPart()
	 */
	public DiagramEditPart getDiagramEditPart() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getDiagramEditPart();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
	 * getDiagramGraphicalViewer()
	 */
	public IDiagramGraphicalViewer getDiagramGraphicalViewer() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getDiagramGraphicalViewer();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTitle() {
		String name = null;
		final SoftwareAssembly sad = getSoftwareAssembly();
		if (sad != null) {
			name = sad.getName();
			if (name == null) {
				name = getEditorInput().getName();
			}
		}
		if (name != null) {
			return name;
		} else {
			return super.getTitle();
		}
	}

	/**
	 * @return
	 */
	private SoftwareAssembly getSoftwareAssembly() {
		return ModelUtil.getSoftwareAssembly(getMainResource());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void addPages() {
		// Only creates the other pages if there is something that can be edited
		//
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
		        && !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource sadResource = getMainResource();

				addNameListener(sadResource);

				final IFormPage page = createOverviewPage(sadResource);
				setOverviewPage(page);
				this.addPage(page);

				final SadDiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput diagramInput = createDiagramInput(sadResource);
				pageIndex = addPage(editor, diagramInput);
				setPageText(pageIndex, "Diagram");

				final IEditorPart textEditor = createTextEditor();
				if (textEditor != null) {
					final int sadSourcePageNum = addPage(textEditor, this.getEditorInput());
					this.setPageText(sadSourcePageNum, this.getEditorInput().getName());
				}

				getEditingDomain().getCommandStack().removeCommandStackListener(getCommandStackListener());

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				        StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				        StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SadUiActivator.getPluginId(), "Failed to create editor parts.", e),
				        StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}

	protected IEditorPart createTextEditor() {
		// StructuredTextEditors only work on workspace entries
		// because
		// org.eclipse.wst.sse.core.FileBufferModelManager:bufferCreated()
		// assumes that the editor input is in the workspace.
		if (getEditorInput() instanceof FileEditorInput) {
			try {
				return new org.eclipse.wst.sse.ui.StructuredTextEditor();
			} catch (final NoClassDefFoundError e) {
				return new TextEditor();
			}
		} else if (!getMainResource().getURI().isPlatformPlugin()) {
			return new TextEditor();
		}

		return null;
	}

	protected void addNameListener(final Resource sadResource) {
		this.nameListener = new ResourceListener(sadResource);
	}

	protected IEditorInput createDiagramInput(final Resource sadResource) throws IOException, CoreException {
		final URI diagramURI = DiagramUtil.getDiagramResourceURI(SadDiagramUtilHelper.INSTANCE, sadResource);

		DiagramUtil.initializeDiagramResource(SadDiagramUtilHelper.INSTANCE, diagramURI, sadResource);

		return DiagramUtil.getDiagramWrappedInput(diagramURI, (TransactionalEditingDomain) this.getEditingDomain());
	}

	protected SadDiagramEditor createDiagramEditor() {
		return new CustomDiagramEditor(this);
	}

	protected void setDiagramEditor(final SadDiagramEditor diagramEditor) {
		this.diagramEditor = diagramEditor;
	}

	/**
	 * 
	 */
	protected IFormPage createOverviewPage(final Resource sadResource) {
		final SadOverviewPage retVal = new SadOverviewPage(this);
		retVal.setInput(sadResource);
		return retVal;
	}

	protected void setOverviewPage(final IFormPage overviewPage) {
		this.overviewPage = overviewPage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void handleDocumentChange(final Resource resource) {
		super.handleDocumentChange(resource);
		for (final Object part : this.getDiagramEditPart().getChildren()) {
			if (part instanceof EditPart) {
				((EditPart) part).refresh();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IContentOutlinePage createContentOutline() {
		IContentOutlinePage myOutline = (IContentOutlinePage) this.diagramEditor.getAdapter(IContentOutlinePage.class);
		if (myOutline == null) {
			myOutline = new WaveformOutlinePage(this);
		}
		this.contentOutlinePage = myOutline;
		// Listen to selection so that we can handle it is a special way.
		// //
		this.contentOutlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
			// // This ensures that we handle selections correctly.
			// //
			public void selectionChanged(final SelectionChangedEvent event) {
				handleContentOutlineSelection(event.getSelection());
			}
		});
		return this.contentOutlinePage;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditingDomainId() {
		return SadEditor.EDITING_DOMAIN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AdapterFactory getSpecificAdapterFactory() {
		final ComposedAdapterFactory factory = new ComposedAdapterFactory();
		factory.addAdapterFactory(new SadItemProviderAdapterFactory());
		factory.addAdapterFactory(new EcoreItemProviderAdapterFactory());
		factory.addAdapterFactory(new SpdItemProviderAdapterFactory());
		factory.addAdapterFactory(new ScdItemProviderAdapterFactory());
		factory.addAdapterFactory(new PrfItemProviderAdapterFactory());
		return factory;
	}

	public IActionBars getActionBars() {
		return getActionBarContributor().getActionBars();
	}

	@Override
	public List<Object> getOutlineItems() {
		final List<Object> myList = new ArrayList<Object>();
		if (this.overviewPage != null) {
			myList.add(this.overviewPage);
		}
		if (getSoftwareAssembly().getPartitioning() != null) {
			myList.add(getSoftwareAssembly().getPartitioning());
		}
		return myList;
	}

	@Override
	protected void emfDoSave(final IProgressMonitor progressMonitor) {
		// Refresh the necessary state. We are delegating the save to the diagram page
		//
		((BasicCommandStack) getEditingDomain().getCommandStack()).saveIsDone();
	}

	@Override
	public boolean isPersisted(final Resource resource) {
		return resource.getURI().equals(getSoftwareAssembly().eResource().getURI()) && super.isPersisted(resource);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();
		cleanAction.setSoftwareAssembly(getSoftwareAssembly());
		cleanAction.run();

		super.doSave(monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resourceChanged(final IResource resource, final IResourceDelta delta) {
		//Make sure we don't call resource changed on a non sad resource
		if (this.isValidSadResource(resource) && !delta.getResource().getWorkspace().isAutoBuilding()) {
			super.resourceChanged(resource, delta);
		}
		validate();
	}

	/**
	 * Evaluate the given resource to determine if it is a resource that can be associated with the SadEditor.
	 * 
	 * @param resource The IResource to evaluate
	 * @return <code> true </code> if this is an sad resource; <code> false </code> otherwise
	 */
	private boolean isValidSadResource(final IResource resource) {
		final String path = resource.getFullPath().toOSString();
		if (path.endsWith(SadPackage.FILE_EXTENSION)) {
			return true;
		}
		return false;
	}

	public SadDiagramEditor getDiagramEditor() {
		return this.diagramEditor;
	}
}
