/**
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 * 
 * This file is part of REDHAWK IDE.
 * 
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 *
 */
package gov.redhawk.ide.graphiti.ui.diagram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultRefreshBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ChalkboardContextMenuProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteBehavior;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;

public abstract class AbstractGraphitiDiagramEditor extends DiagramEditor {

	private List<String> contexts = new ArrayList<String>();
	private List<IContextActivation> contextActivations = new ArrayList<IContextActivation>();
	private MouseListener mouseListener = null;

	private EditingDomain editingDomain;

	public AbstractGraphitiDiagramEditor(EditingDomain editingDomain) {
		super();
		this.editingDomain = editingDomain;
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);

		// Activate contexts specified pre-init()
		for (String context : contexts) {
			activateContext(context);
		}
	}

	@Override
	public void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();

		// Set a margin around the diagram border to make it easier to scroll a little bit to the side
		EditPart ep = getGraphicalViewer().getRootEditPart().getContents();
		if (ep instanceof AbstractGraphicalEditPart) {
			IFigure fig = ((AbstractGraphicalEditPart) ep).getFigure();
			fig.setBorder(new MarginBorder(50));
		}
	}

	@Override
	public void dispose() {
		deactivateAllContexts();
		super.dispose();
	}

	/**
	 * Update the diagram's components and connections
	 */
	protected void updateDiagram() {
		final Diagram diagram = getDiagramTypeProvider().getDiagram();
		NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
			public void execute() {

				Diagram diagram = getDiagramTypeProvider().getDiagram();
				IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

				// update all components if necessary
				for (Shape s : getDiagramTypeProvider().getDiagram().getChildren()) {
					if (s instanceof RHContainerShape) {
						if (s != null && featureProvider != null) {
							final UpdateContext updateContext = new UpdateContext(s);
							final IUpdateFeature updateFeature = featureProvider.getUpdateFeature(updateContext);
							final IReason updateNeeded = updateFeature.updateNeeded(updateContext);
							if (updateNeeded.toBoolean()) {
								updateFeature.update(updateContext);
							}
						}
					}
				}

				// update diagram, don't ask if it should update because we want to redraw all connections each time
				if (diagram != null && featureProvider != null) {
					final UpdateContext updateContext = new UpdateContext(diagram);
					final IUpdateFeature updateFeature = featureProvider.getUpdateFeature(updateContext);
					updateFeature.update(updateContext);
				}
			}
		});
	}

	@Override
	public void hookGraphicalViewer() {
		super.hookGraphicalViewer();

		// IDE-1029: Normally in Graphiti, right-clicking on anchors will not select them, which means that a context
		// menu for the currently selected object pops up (often the wrong object). In order to get the context menu
		// for a port without having to left-click first, we intercept mouse down events on the viewer's control, and
		// if it looks like an anchor we forcibly update the selection.
		//
		// It does not appear to be necessary to remove the listener in dispose(), as the CanvasViewer has already
		// been disposed at that point.
		getGraphicalControl().addMouseListener(getMouseListener());
	}

	protected void handleMouseDown(MouseEvent e) {
		if (e.button == 3) {
			GraphicalViewer viewer = getGraphicalViewer();
			if (viewer != null) {
				EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
				if (part.getModel() instanceof Anchor) {
					if (part.getSelected() == EditPart.SELECTED_NONE) {
						viewer.select(part);
					}
				}
			}
		}
	}

	public void addContext(String context) {
		contexts.add(context);
	}

	protected void activateContext(String context) {
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		if (contextService != null) {
			IContextActivation activation = contextService.activateContext(context);
			contextActivations.add(activation);
		}

	}

	private void deactivateAllContexts() {
		if (!contextActivations.isEmpty()) {
			IContextService contextService = (IContextService) getSite().getService(IContextService.class);
			for (IContextActivation activation : contextActivations) {
				contextService.deactivateContext(activation);
			}
		}
	}

	private MouseListener getMouseListener() {
		if (mouseListener == null) {
			mouseListener = new MouseAdapter() {

				@Override
				public void mouseDown(MouseEvent e) {
					AbstractGraphitiDiagramEditor.this.handleMouseDown(e);
				}
			};
		}
		return mouseListener;
	}

	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

			// Override Marker behavior because it modifies the underlying sad resource
			// and the user will be prompted if they would like to replace their file with what's on disk
			@Override
			public DefaultMarkerBehavior createMarkerBehavior() {
				return new DefaultMarkerBehavior(this) {

					public Diagnostic analyzeResourceProblems(Resource resource, Exception exception) {
						return Diagnostic.OK_INSTANCE;
					}
				};

			};

			@Override
			protected DefaultRefreshBehavior createRefreshBehavior() {
				return new RunExclusiveRefreshBehavior(this);
			}

			@Override
			protected DefaultUpdateBehavior createUpdateBehavior() {
				return new DefaultUpdateBehavior(this) {

					// We need to provide our own editing domain so that all editors are working on the same resource.
					// In order to work with a Graphiti diagram, our form creates an editing domain with the Graphiti
					// supplied Command stack.
					@Override
					protected void createEditingDomain(IDiagramEditorInput input) {
						initializeEditingDomain((TransactionalEditingDomain) editingDomain);
					}

					@Override
					protected boolean handleDirtyConflict() {
						return true;
					}

					@Override
					protected Delegate createWorkspaceSynchronizerDelegate() {
						return null;
					}

					@Override
					protected void closeContainer() {
					}

					@Override
					protected void disposeEditingDomain() {
					}

				};
			}

			@Override
			protected DefaultPaletteBehavior createPaletteBehaviour() {
				final DefaultPaletteBehavior paletteBehavior = new RHGraphitiPaletteBehavior(this);
				return paletteBehavior;
			}

			@Override
			protected List<TransferDropTargetListener> createBusinessObjectDropTargetListeners() {
				List<TransferDropTargetListener> retVal = super.createBusinessObjectDropTargetListeners();
				Diagram diagram = getDiagramBehavior().getDiagramTypeProvider().getDiagram();

				// This check stops users from adding from the Target SDR to Graphiti Waveform Explorer
				if (!DUtil.isDiagramExplorer(diagram)) {
					retVal.add(0, createDropTargetListener(getDiagramContainer().getGraphicalViewer(), this));
				}

				return retVal;
			}

			@Override
			protected ContextMenuProvider createContextMenuProvider() {
				if (DUtil.isDiagramRuntime(getDiagramTypeProvider().getDiagram())) {
					return new ChalkboardContextMenuProvider(getDiagramContainer().getGraphicalViewer(), getDiagramContainer().getActionRegistry(),
						getConfigurationProvider());
				}
				return super.createContextMenuProvider();
			}
		};
	}

	protected abstract TransferDropTargetListener createDropTargetListener(GraphicalViewer viewer, DiagramBehavior behavior);
}
