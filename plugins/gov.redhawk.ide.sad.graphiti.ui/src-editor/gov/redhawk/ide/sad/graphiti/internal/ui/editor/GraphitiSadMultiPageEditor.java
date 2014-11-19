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
package gov.redhawk.ide.sad.graphiti.internal.ui.editor;

import gov.redhawk.ide.internal.ui.handlers.CleanUpComponentFilesAction;
import gov.redhawk.ide.sad.graphiti.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.sad.graphiti.ui.diagram.RHGraphitiDiagramEditor;
import gov.redhawk.ide.sad.graphiti.ui.diagram.SadDiagramUtilHelper;
import gov.redhawk.ide.sad.graphiti.ui.diagram.providers.SADDiagramTypeProvider;
import gov.redhawk.ide.sad.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.model.sca.util.ModelUtil;
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

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalCommandStack;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.gef.EditPart;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramLink;
import org.eclipse.graphiti.mm.pictograms.PictogramsFactory;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.DiagramEditorInput;
import org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

import CF.Application;

/**
 * This is an example of a Sad model editor.
 */
public class GraphitiSadMultiPageEditor extends SCAFormEditor implements ITabbedPropertySheetPageContributor, IViewerProvider {

	public static final String ID = "gov.redhawk.ide.sad.graphiti.ui.editor.presentation.SadEditorID";

	public static final String EDITING_DOMAIN_ID = "mil.jpeojtrs.sca.sad.diagram.EditingDomain";

	private static final String DIAGRAM_PAGE_ID = "2";

	private boolean isDirtyAllowed = true;

	/**
	 * The graphical diagram editor embedded into this editor.
	 */
	private DiagramEditor diagramEditor;

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

	private ResourceListener nameListener;

	private IFormPage overviewPage;

	private IFormPage propertiesPage;

	private IEditorPart textEditor;

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
	public GraphitiSadMultiPageEditor() {
		super();
		this.selectionProvider = new MultiPageSelectionProvider(this);
		this.selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				setStatusLineManager(event.getSelection());
			}
		});
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
				@Override
				public void run() {
					// Try to select the items in the current content viewer of
					// the editor.
					//
					if (GraphitiSadMultiPageEditor.this.currentViewer != null) {
						GraphitiSadMultiPageEditor.this.currentViewer.setSelection(new StructuredSelection(theSelection.toArray()), true);
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
	 * This returns the viewer as required by the {@link IViewerProvider} interface.
	 */
	@Override
	public Viewer getViewer() {
		return this.currentViewer;
	}

	/**
	 * This is how the framework determines which interfaces we implement.
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class key) {
//		if (key.equals(IPropertySheetPage.class)) {
//			return getPropertySheetPage();
//		} else if (key.equals(IGotoMarker.class)) {
//			return this;
//		} else if (key.equals(ScaWaveform.class)) {
//			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
//		} else if (key.isAssignableFrom(Application.class)) {
//			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
//		} else {
//			return super.getAdapter(key);
//		}

		if (key.equals(IGotoMarker.class)) {
			return this;
		} else if (key.equals(ScaWaveform.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else if (key.isAssignableFrom(Application.class)) {
			return PluginUtil.adapt(ScaWaveform.class, getSoftwareAssembly());
		} else {
			return super.getAdapter(key);
		}
	}

//	/**
//	 * This accesses a cached version of the property sheet.
//	 */
//	public IPropertySheetPage getPropertySheetPage() {
//		return new PropertiesBrowserPage(this);
//	}

	@Override
	protected String getPropertyEditorPageKey(final IFileEditorInput input) {
		final String retVal = super.getPropertyEditorPageKey(input);
		if (retVal == null) {
			return DIAGRAM_PAGE_ID;
		}
		return retVal;
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
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to go to marker.", exception),
				StatusManager.LOG | StatusManager.SHOW);
		}
	}

	private Object getWrapper(final EObject eObject) {
		return AdapterFactoryEditingDomain.getWrapper(eObject, getEditingDomain());
	}

	public void setStatusLineManager(final ISelection selection) {
		final IStatusLineManager statusLineManager;

		statusLineManager = getActionBars().getStatusLineManager();

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

	@Override
	protected boolean computeDirtyState() {
		if (!isDirtyAllowed()) {
			setDirtyAllowed(true);
			return false;
		}
		int activePage = getActivePage();
		if (activePage == -1) {
			return false;
		} else if (activePage == getPageCount() - 1) {
			return textEditor.isDirty();
		}
		BasicCommandStack commandStack = (BasicCommandStack) diagramEditor.getEditingDomain().getCommandStack();
		return commandStack.isSaveNeeded();
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
	@Override
	public String getContributorId() {
		if (this.diagramEditor == null) {
			return null;
		}
		return this.diagramEditor.getContributorId();
	}

//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see
//	 * org.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#getDiagram
//	 * ()
//	 */
//	@Override
//	public Diagram getDiagram() {
//		if (this.diagramEditor == null) {
//			return null;
//		}
//		return this.diagramEditor.getDiagram();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
//	 * getDiagramEditDomain()
//	 */
//	@Override
//	public IDiagramEditDomain getDiagramEditDomain() {
//		if (this.diagramEditor == null) {
//			return null;
//		}
//		return this.diagramEditor.getDiagramEditDomain();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
//	 * getDiagramEditPart()
//	 */
//	@Override
//	public DiagramEditPart getDiagramEditPart() {
//		if (this.diagramEditor == null) {
//			return null;
//		}
//		return this.diagramEditor.getDiagramEditPart();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @seeorg.eclipse.gmf.runtime.diagram.ui.parts.IDiagramWorkbenchPart#
//	 * getDiagramGraphicalViewer()
//	 */
//	@Override
//	public IDiagramGraphicalViewer getDiagramGraphicalViewer() {
//		if (this.diagramEditor == null) {
//			return null;
//		}
//		return this.getDiagramEditor().getDiagramBehavior().getDiagramContainer().getGraphicalViewer();
//	}

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

		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				int pageIndex = 0;

				final Resource sadResource = getMainResource();

				addNameListener(sadResource);

				final IFormPage page = createOverviewPage(sadResource);
				setOverviewPage(page);
				this.addPage(page);

				this.propertiesPage = createPropertiesPage(sadResource);
				addPage(propertiesPage);

				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput diagramInput = createDiagramInput(sadResource);
				pageIndex = addPage(editor, diagramInput);
				setPageText(pageIndex, "Diagram");

				// set layout for target-sdr editors
				DUtil.layout(editor);

				textEditor = createTextEditor();
				if (textEditor != null) {
					final int sadSourcePageNum = addPage(textEditor, getEditorInput());
					this.setPageText(sadSourcePageNum, getEditorInput().getName());
				}

			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}

	public IFormPage getPropertiesPage() {
		return propertiesPage;
	}

	protected IFormPage createPropertiesPage(Resource sadResource) {
		GraphitiSadPropertiesPage retVal = new GraphitiSadPropertiesPage(this, "propertiesPage", "Properties", true);
		retVal.setInput(sadResource);
		return retVal;
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

	/**
	 * Given the sad resource (sad.xml) create a Diagram resource, associate it with the sad resource and return
	 * diagram editor input
	 * @param sadResource
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	protected IEditorInput createDiagramInput(final Resource sadResource) throws IOException, CoreException {

		final URI diagramURI = DUtil.getDiagramResourceURI(SadDiagramUtilHelper.INSTANCE, sadResource);

		DUtil.initializeDiagramResource(SadDiagramUtilHelper.INSTANCE, diagramURI, sadResource);

		Resource diagramResource = getEditingDomain().getResourceSet().getResource(diagramURI, true);

		// load diagram from resource
		final Diagram diagram = (Diagram) diagramResource.getContents().get(0);

		// load sad from resource
		final SoftwareAssembly sad = SoftwareAssembly.Util.getSoftwareAssembly(sadResource);

		// link diagram with SoftwareAssembly
		TransactionalCommandStack stack = (TransactionalCommandStack) getEditingDomain().getCommandStack();
		stack.execute(new RecordingCommand((TransactionalEditingDomain) getEditingDomain()) {
			@Override
			protected void doExecute() {

				// set property specifying diagram context (design, local, domain)
				Graphiti.getPeService().setPropertyValue(diagram, DUtil.DIAGRAM_CONTEXT, getDiagramContext(sadResource));

				// link diagram and sad
				GraphitiSadMultiPageEditor.this.setDirtyAllowed(false);
				PictogramLink link = PictogramsFactory.eINSTANCE.createPictogramLink();
				link.getBusinessObjects().add(sad);
				diagram.setLink(link);
			}
		});

		// return editor input from diagram with sad diagram type
		return DiagramEditorInput.createEditorInput(diagram, SADDiagramTypeProvider.PROVIDER_ID);

	}

	/**
	 * Returns the property value that should be set for the Diagram container's DIAGRAM_CONTEXT property.
	 * Indicates the mode the diagram is operating in.
	 * @return
	 */
	public String getDiagramContext(Resource sadResource) {
		if (sadResource.getURI().toString().matches(".*" + System.getenv("SDRROOT") + ".*")) {
			return DUtil.DIAGRAM_CONTEXT_TARGET_SDR;
		}

		return DUtil.DIAGRAM_CONTEXT_DESIGN;
	}

	protected DiagramEditor createDiagramEditor() {
		RHGraphitiDiagramEditor d = new RHGraphitiDiagramEditor((TransactionalEditingDomain) getEditingDomain());
		return d;

	}

	protected void setDiagramEditor(final DiagramEditor diagramEditor) {
		this.diagramEditor = diagramEditor;
	}

	/**
	 * 
	 */
	protected IFormPage createOverviewPage(final Resource sadResource) {
		final GraphitiSadOverviewPage retVal = new GraphitiSadOverviewPage(this);
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
		for (final Object part : this.getDiagramEditor().getDiagramBehavior().getContentEditPart().getChildren()) {
			if (part instanceof EditPart) {
				((EditPart) part).refresh();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEditingDomainId() {
		return GraphitiSadMultiPageEditor.EDITING_DOMAIN_ID;
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
	public boolean isPersisted(final Resource resource) {
		return resource.getURI().equals(getSoftwareAssembly().eResource().getURI()) && super.isPersisted(resource);
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		final CleanUpComponentFilesAction cleanAction = new CleanUpComponentFilesAction();
		cleanAction.setRoot(getSoftwareAssembly());
		cleanAction.run();

		try {
			this.editorSaving = true;
			int activePage = getActivePage();
			if (textEditor.isDirty() && activePage == getPageCount() - 1) {
				textEditor.doSave(monitor);
				reload();
				emfDoSave(new SubProgressMonitor(monitor, 1));
			} else {
				commitPages(true);
				monitor.beginTask("Saving " + this.getTitle(), this.getPageCount() + 2);
//				internalDoValidate(new SubProgressMonitor(monitor, 1));
				emfDoSave(new SubProgressMonitor(monitor, 1));
			}
			BasicCommandStack commandStack = (BasicCommandStack) diagramEditor.getEditingDomain().getCommandStack();
			commandStack.saveIsDone();
			editorDirtyStateChanged();
		} catch (final OperationCanceledException e) {
			// PASS
		} finally {
			monitor.done();
			this.editorSaving = false;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resourceChanged(final IResource resource, final IResourceDelta delta) {
		// Make sure we don't call resource changed on a non sad resource
		if (this.isValidSadResource(resource) && !delta.getResource().getWorkspace().isAutoBuilding()) {
			super.resourceChanged(resource, delta);
		}
		validate();
	}

	@Override
	public void reload() {
		super.reload();
		diagramEditor.getDiagramBehavior().getUpdateBehavior().setResourceChanged(true);
		diagramEditor.getDiagramBehavior().getUpdateBehavior().handleActivate();
	}

	@Override
	protected void emfDoSave(IProgressMonitor progressMonitor) {
		diagramEditor.doSave(progressMonitor);
//		super.emfDoSave(progressMonitor);
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

	public DiagramEditor getDiagramEditor() {
		return this.diagramEditor;
	}

	@Override
	protected IContentOutlinePage createContentOutline() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@SuppressWarnings("restriction")
	protected TransactionalEditingDomain createEditingDomain() {

		final ResourceSet resourceSet = new ResourceSetImpl();
		final IWorkspaceCommandStack workspaceCommandStack = new GFWorkspaceCommandStackImpl(new DefaultOperationHistory());

		TransactionalEditingDomain domain = new TransactionalEditingDomainImpl(new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE),
			workspaceCommandStack, resourceSet);
		WorkspaceEditingDomainFactory.INSTANCE.mapResourceSet((TransactionalEditingDomain) domain);
		domain.setID(getEditingDomainId());

		// Create an adapter factory that yields item providers.
		//
		final ComposedAdapterFactory localAdapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

		localAdapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
		localAdapterFactory.addAdapterFactory(getSpecificAdapterFactory());
		localAdapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		((AdapterFactoryEditingDomain) domain).setAdapterFactory(localAdapterFactory);
		return domain;
	}

	public boolean isDirtyAllowed() {
		return isDirtyAllowed;
	}

	public void setDirtyAllowed(boolean isDirtyAllowed) {
		this.isDirtyAllowed = isDirtyAllowed;
	}
}
