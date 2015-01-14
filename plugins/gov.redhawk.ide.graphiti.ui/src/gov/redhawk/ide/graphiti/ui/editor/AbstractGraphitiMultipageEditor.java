package gov.redhawk.ide.graphiti.ui.editor;

import gov.redhawk.ide.graphiti.ui.Activator;
import gov.redhawk.ide.graphiti.ui.diagram.RHCommandStackImpl;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ui.editor.SCAFormEditor;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.operations.DefaultOperationHistory;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EValidator;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.provider.AdapterFactoryItemDelegator;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.IWorkspaceCommandStack;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageSelectionProvider;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

public abstract class AbstractGraphitiMultipageEditor extends SCAFormEditor implements ITabbedPropertySheetPageContributor, IViewerProvider {

	public static final String ID = "gov.redhawk.ide.sad.graphiti.ui.editor.presentation.SadEditorID";

	public static final String EDITING_DOMAIN_ID = "mil.jpeojtrs.sca.sad.diagram.EditingDomain";

	private static final String DIAGRAM_PAGE_ID = "2";

	/**
	 * This is used to manually override the dirty state. It can be used to avoid marking the editor as dirty on trivial
	 * or hidden actions, such as linking the diagram to the sad.xml
	 */
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

	private IFormPage overviewPage;

	private IFormPage propertiesPage;

	private IEditorPart textEditor;
	
	/**
	 * This creates a model editor.
	 */
	public AbstractGraphitiMultipageEditor() {
		super();
		this.selectionProvider = new MultiPageSelectionProvider(this);
		this.selectionProvider.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				setStatusLineManager(event.getSelection());
			}
		});
	}
	
	abstract public void setSelectionToViewer(final Collection< ? > collection);
	
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
	
	abstract public Object getAdapter(@SuppressWarnings("rawtypes") final Class key);
	
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
			StatusManager.getManager().handle(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Failed to go to marker.", exception),
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

	/**
	 * Determines if this multi-page editor show dirty state
	 */
	@Override
	protected boolean computeDirtyState() {
		//an override to ignore potentially dirty state
		if (!isDirtyAllowed()) {
			setDirtyAllowed(true);
			return false;
		}
		
		//text editor dirty state
		if (textEditor.isDirty()) {
			return true;
		}
		
		//state of resources in command stack diagram file,
		//sad file is taken care of above with text editor
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

	abstract public void dispose();
	
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
	
	abstract public String getTitle();
	
	abstract protected void addPages();

	public IFormPage getPropertiesPage() {
		return propertiesPage;
	}
	
	abstract protected IFormPage createPropertiesPage(Resource resource);

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
	
	abstract protected void addNameListener(final Resource resource);
	
	abstract protected IEditorInput createDiagramInput(final Resource resource) throws IOException, CoreException;
	
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
	
	abstract protected DiagramEditor createDiagramEditor();
	
	protected void setDiagramEditor(final DiagramEditor diagramEditor) {
		this.diagramEditor = diagramEditor;
	}
	
	abstract protected IFormPage createOverviewPage(final Resource resource);
	
	protected void setOverviewPage(final IFormPage overviewPage) {
		this.overviewPage = overviewPage;
	}
	
	abstract public String getEditingDomainId();
	
	abstract protected AdapterFactory getSpecificAdapterFactory();
	
	public IActionBars getActionBars() {
		return getActionBarContributor().getActionBars();
	}
	
	abstract public List<Object> getOutlineItems();
	
	abstract public boolean isPersisted(final Resource resource);

	/**
	 * The Text editor always stays in sync with other editor changes therefore always save it.
	 * Calling emfDoSave handles saving the graphiti diagram resource
	 */
	@Override
	public void doSave(final IProgressMonitor monitor) {
		try {
			this.editorSaving = true;
			if (textEditor.isDirty()) {
				textEditor.doSave(monitor);
				commitPages(true);
				emfDoSave(new SubProgressMonitor(monitor, 1));
			} else {
				//saving diagram files that had no effect on SAD resource
				commitPages(true);
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


	@Override
	public void reload() {
		super.reload();
		diagramEditor.getDiagramBehavior().getUpdateBehavior().setResourceChanged(true);
		diagramEditor.getDiagramBehavior().getUpdateBehavior().handleActivate();
	}

	@Override
	protected void emfDoSave(IProgressMonitor progressMonitor) {
		diagramEditor.doSave(progressMonitor);
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
		final IWorkspaceCommandStack workspaceCommandStack = new RHCommandStackImpl(new DefaultOperationHistory());

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
	
	/**
	 * Adds a complete editor part to the multi-page editor and associates an editor document with a resource; adds a
	 * document listener to allow document changes to be propagated to the resource.
	 * 
	 * @param editor the nested editor
	 * @param input the input of the nested editor
	 * @param resource the resource to associate with the editor
	 * @return the index of the page in the editor
	 * @throws PartInitException
	 */
	public int addPage(int index, final IEditorPart editor, final IEditorInput input, final Resource resource) throws PartInitException {
		if (index == -1) {
			index = super.addPage(editor, input);
		} else {
			super.addPage(index, editor, input);
		}
		if (editor instanceof TextEditor) {
			final IDocument document = ((TextEditor) editor).getDocumentProvider().getDocument(editor.getEditorInput());
			getResourceToDocumentMap().put(resource, document);
			document.addDocumentListener(new IDocumentListener() {

				@Override
				public void documentAboutToBeChanged(final DocumentEvent documentEvent) {
					// Ignore
				}

				@Override
				public void documentChanged(final DocumentEvent documentEvent) {
					try {
						handleDocumentChange(resource);
					} catch (final Exception exception) { // SUPPRESS CHECKSTYLE Fallback
						// PASS
					}
				}
			});
		}
		return index;
	}
	
}
