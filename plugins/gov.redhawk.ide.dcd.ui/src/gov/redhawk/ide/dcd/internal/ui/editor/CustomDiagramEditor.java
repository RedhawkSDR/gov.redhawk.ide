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
package gov.redhawk.ide.dcd.internal.ui.editor;

import gov.redhawk.sca.dcd.diagram.part.DcdDiagramEditor;
import mil.jpeojtrs.sca.dcd.diagram.part.DcdDiagramActionBarContributor;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gmf.runtime.diagram.ui.editparts.DiagramRootEditPart;
import org.eclipse.gmf.runtime.diagram.ui.internal.editparts.DiagramRootTreeEditPart;
import org.eclipse.gmf.runtime.diagram.ui.internal.l10n.DiagramUIPluginImages;
import org.eclipse.gmf.runtime.diagram.ui.l10n.DiagramUIMessages;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditor;
import org.eclipse.gmf.runtime.draw2d.ui.internal.parts.ScrollableThumbnailEx;
import org.eclipse.gmf.runtime.draw2d.ui.internal.parts.ThumbnailEx;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
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
public class CustomDiagramEditor extends DcdDiagramEditor implements IFormPage {

	private static final String TOOL_PREFIX = "spdTool.";
	private boolean disposed;
	public static final String PAGE_ID = "diagram";
	protected static final long REFRESH_PALLETE_DELAY = 100;
	private int index;
	private boolean active;
	private final NodeEditor editor;
	private IContentOutlinePage outlinePage;

	public CustomDiagramEditor(final NodeEditor editor) {
		super();
		this.editor = editor;
	}

	class CustomDiagramOutlinePage extends ContentOutlinePage {

		private PageBook pageBook;

		private IContentOutlinePage outline;

		private Canvas overview;

		private IAction showOutlineAction, showOverviewAction;

		private boolean overviewInitialized;

		private ThumbnailEx thumbnail;

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

		/**
		 * configures the outline viewer
		 */
		protected void configureOutlineViewer() {
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(getOutlineViewEditPartFactory());

			final MenuManager outlineContextMenuProvider = getOutlineContextMenuProvider(getViewer());
			if (outlineContextMenuProvider != null) {
				getViewer().setContextMenu(outlineContextMenuProvider);
			}

			getViewer().setKeyHandler(getKeyHandler());
			// getViewer().addDropTargetListener(
			// new LogicTemplateTransferDropTargetListener(getViewer()));
			final IToolBarManager tbm = this.getSite().getActionBars().getToolBarManager();
			this.showOutlineAction = new Action() {

				@Override
				public void run() {
					showPage(DiagramEditor.ID_OUTLINE);
				}
			};
			this.showOutlineAction.setImageDescriptor(DiagramUIPluginImages.DESC_OUTLINE);
			this.showOutlineAction.setToolTipText(DiagramUIMessages.OutlineView_OutlineTipText);
			tbm.add(this.showOutlineAction);
			this.showOverviewAction = new Action() {

				@Override
				public void run() {
					showPage(DiagramEditor.ID_OVERVIEW);
				}
			};
			this.showOverviewAction.setImageDescriptor(DiagramUIPluginImages.DESC_OVERVIEW);
			this.showOverviewAction.setToolTipText(DiagramUIMessages.OutlineView_OverviewTipText);
			tbm.add(this.showOverviewAction);
			showPage(getDefaultOutlineViewMode());
		}

		@Override
		public void createControl(final Composite parent) {
			this.pageBook = new PageBook(parent, SWT.NONE);
			this.outline = new NodeOutlinePage(CustomDiagramEditor.this.editor, this.pageBook);
			this.overview = new Canvas(this.pageBook, SWT.NONE);
			this.pageBook.showPage(this.outline.getControl());
			configureOutlineViewer();
		}

		@Override
		public void dispose() {
			if (this.thumbnail != null) {
				this.thumbnail.deactivate();
			}
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
			final LightweightSystem lws = new LightweightSystem(this.overview);
			final RootEditPart rep = getGraphicalViewer().getRootEditPart();
			final DiagramRootEditPart root = (DiagramRootEditPart) rep;
			this.thumbnail = new ScrollableThumbnailEx((Viewport) root.getFigure());
			this.thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));

			lws.setContents(this.thumbnail);
			this.disposeListener = new DisposeListener() {

				@Override
				public void widgetDisposed(final DisposeEvent e) {
					if (CustomDiagramOutlinePage.this.thumbnail != null) {
						CustomDiagramOutlinePage.this.thumbnail.deactivate();
						CustomDiagramOutlinePage.this.thumbnail = null;
					}
				}
			};
			getEditor().addDisposeListener(this.disposeListener);
			this.overviewInitialized = true;
		}

		/**
		 * show page with a specific ID, possibel values are ID_OUTLINE and
		 * ID_OVERVIEW
		 * 
		 * @param id
		 */
		protected void showPage(final int id) {
			if (id == DiagramEditor.ID_OUTLINE) {
				this.showOutlineAction.setChecked(true);
				this.showOverviewAction.setChecked(false);
				this.pageBook.showPage(this.outline.getControl());
				if (this.thumbnail != null) {
					this.thumbnail.setVisible(false);
				}

			} else if (id == DiagramEditor.ID_OVERVIEW) {
				if (!this.overviewInitialized) {
					initializeOverview();
				}
				this.showOutlineAction.setChecked(false);
				this.showOverviewAction.setChecked(true);
				this.pageBook.showPage(this.overview);
				this.thumbnail.setVisible(true);
			}
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
	public Object getAdapter(@SuppressWarnings({ "rawtypes" }) final Class type) {
		if (type == IContentOutlinePage.class) {
			final TreeViewer viewer = new TreeViewer();
			viewer.setRootEditPart(new DiagramRootTreeEditPart());
			IContentOutlinePage page = this.outlinePage;
			if (page == null) {
				this.outlinePage = new CustomDiagramOutlinePage(viewer);
				page = this.outlinePage;
			}
			return page;
		}
		if (type == IEditorActionBarContributor.class) {
			return new DcdDiagramActionBarContributor();
		}
		return super.getAdapter(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		this.disposed = true;
		super.dispose();
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
		return CustomDiagramEditor.PAGE_ID;
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
		(this.editor).handleContentOutlineSelection(object);
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
