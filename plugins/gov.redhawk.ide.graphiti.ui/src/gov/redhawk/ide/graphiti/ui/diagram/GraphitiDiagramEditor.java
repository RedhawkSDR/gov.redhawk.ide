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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Anchor;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;

public class GraphitiDiagramEditor extends DiagramEditor {

	private List<String> contexts = new ArrayList<String>();
	private List<IContextActivation> contextActivations = new ArrayList<IContextActivation>();
	private MouseListener mouseListener = null;

	protected EditingDomain editingDomain;

	public GraphitiDiagramEditor(EditingDomain editingDomain) {
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
					GraphitiDiagramEditor.this.handleMouseDown(e);
				}
			};
		}
		return mouseListener;
	}

}
