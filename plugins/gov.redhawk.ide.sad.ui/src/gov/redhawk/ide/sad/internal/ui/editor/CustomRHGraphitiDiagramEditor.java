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

import gov.redhawk.ide.sad.graphiti.ui.diagram.RHGraphitiDiagramEditor;
import gov.redhawk.ui.editor.SCAFormEditor;

import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * 
 */
public class CustomRHGraphitiDiagramEditor extends RHGraphitiDiagramEditor implements IFormPage {
	public static final String PAGE_ID = "diagram";
	private int index;
	private boolean active;
	private final SCAFormEditor editor;
	private IContentOutlinePage outlinePage;

	public CustomRHGraphitiDiagramEditor(TransactionalEditingDomain editingDomain, final SCAFormEditor editor) {
		super(editingDomain);
		this.editor = editor;
	}

	class CustomDiagramOutlinePage extends ContentOutlinePage {

		private PageBook pageBook;

		private IContentOutlinePage outline;

		private Canvas overview;

		private IAction showOutlineAction, showOverviewAction;

		private boolean overviewInitialized;

		private DisposeListener disposeListener;

		/**
		 * @param viewer
		 */
		public CustomDiagramOutlinePage(final EditPartViewer viewer) {
			super(viewer);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.ui.part.Page#init(org.eclipse.ui.part.IPageSite)
		 */
		@Override
		public void init(final IPageSite pageSite) {
			super.init(pageSite);
			final ActionRegistry registry = getActionRegistry();
			final IActionBars bars = pageSite.getActionBars();
			String id = ActionFactory.UNDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.REDO.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			id = ActionFactory.DELETE.getId();
			bars.setGlobalActionHandler(id, registry.getAction(id));
			bars.updateActionBars();

			// Toolbar refresh to solve linux defect RATLC525198
			bars.getToolBarManager().markDirty();
		}



		@Override
		public void createControl(final Composite parent) {
			this.pageBook = new PageBook(parent, SWT.NONE);
			this.outline = new WaveformOutlinePage(CustomRHGraphitiDiagramEditor.this.editor, this.pageBook);
			this.overview = new Canvas(this.pageBook, SWT.NONE);
			this.pageBook.showPage(this.outline.getControl());
		}

		@Override
		public void dispose() {
			this.overviewInitialized = false;
			super.dispose();
		}

		@Override
		public Control getControl() {
			return this.pageBook;
		}

		/**
		 * initialize the overview
		 */
		protected void initializeOverview() {
//			final LightweightSystem lws = new LightweightSystem(this.overview);
//			final RootEditPart rep = getGraphicalViewer().getRootEditPart();
//			final DiagramRootEditPart root = (DiagramRootEditPart) rep;
//			this.thumbnail = new ScrollableThumbnailEx((Viewport) root.getFigure());
//			this.thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
//
//			lws.setContents(this.thumbnail);
//			this.disposeListener = new DisposeListener() {
//
//				@Override
//				public void widgetDisposed(final DisposeEvent e) {
//					if (CustomDiagramOutlinePage.this.thumbnail != null) {
//						CustomDiagramOutlinePage.this.thumbnail.deactivate();
//						CustomDiagramOutlinePage.this.thumbnail = null;
//					}
//				}
//			};
//			getEditor().addDisposeListener(this.disposeListener);
//			this.overviewInitialized = true;
		}



		/**
		 * getter for the editor conrolo
		 * 
		 * @return <code>Control</code>
		 */
		protected Control getEditor() {
			return getGraphicalViewer().getControl();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class type) {
//		if (type == IContentOutlinePage.class) {
//			final TreeViewer viewer = new TreeViewer();
//			viewer.setRootEditPart(new DiagramRootTreeEditPart());
//			IContentOutlinePage page = this.outlinePage;
//			if (page == null) {
//				this.outlinePage = new CustomDiagramOutlinePage(viewer);
//				page = this.outlinePage;
//			}
//			return page;
//		}
//		if (type == IEditorActionBarContributor.class) {
//			return new SadDiagramActionBarContributor();
//		}
		return super.getAdapter(type);
	}

	@Override
	public boolean canLeaveThePage() {
		return true;
	}

	@Override
	public FormEditor getEditor() {
		return this.editor;
	}

	@Override
	public String getId() {
		return CustomRHGraphitiDiagramEditor.PAGE_ID;
	}

	@Override
	public int getIndex() {
		return this.index;
	}

	@Override
	public IManagedForm getManagedForm() {
		return null;
	}

	@Override
	public Control getPartControl() {
		return super.getGraphicalControl();
	}

	@Override
	public void initialize(final FormEditor editor) {
	}

	@Override
	public boolean isActive() {
		return this.active;
	}

	@Override
	public boolean isEditor() {
		return true;
	}

	@Override
	public boolean selectReveal(final Object object) {
		((SadEditor) this.editor).handleContentOutlineSelection(object);
		return false;
	}

	@Override
	public void setActive(final boolean active) {
		this.active = active;
	}

	@Override
	public void setIndex(final int index) {
		this.index = index;
	}
}
