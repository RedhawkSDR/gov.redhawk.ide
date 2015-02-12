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
package gov.redhawk.ide.graphiti.sad.ui.diagram;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.sad.ui.diagram.providers.SADDiagramTypeProvider;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ChalkboardContextMenuProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteBehavior;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;

public class GraphitiWaveformDiagramEditor extends DiagramEditor {

	private EditingDomain editingDomain;
	private List<String> contexts = new ArrayList<String>();
	private List<IContextActivation> contextActivations = new ArrayList<IContextActivation>();

	public GraphitiWaveformDiagramEditor(EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

			// Override Marker behavior because it modifies the underlying sad resource
			// and the user will be prompted if they would like to replace their file with what's on disk
			@Override
			public DefaultMarkerBehavior createMarkerBehavior() {
				return new DefaultMarkerBehavior(this) {
					protected void updateProblemIndication() {
						return;
					}

					public Diagnostic analyzeResourceProblems(Resource resource, Exception exception) {
						return Diagnostic.OK_INSTANCE;
					}
				};

			};

			@Override
			protected DefaultUpdateBehavior createUpdateBehavior() {
				return new DefaultUpdateBehavior(this) {

					// We need to provide our own editing domain so that all editors are working on the same resource.
					// In order to work with a Graphiti diagram, our form creates an editing domain with the Graphiti
					// supplied Command stack.
					@Override
					protected void createEditingDomain() {
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
					retVal.add(0, new DiagramDropTargetListener(getDiagramContainer().getGraphicalViewer(), this));
				}

				return retVal;
			}

			@Override
			protected ContextMenuProvider createContextMenuProvider() {
				if (DUtil.isDiagramLocal(getDiagramTypeProvider().getDiagram())) {
					return new ChalkboardContextMenuProvider(getDiagramContainer().getGraphicalViewer(), getDiagramContainer().getActionRegistry(),
						getConfigurationProvider());
				}
				return super.createContextMenuProvider();
			}
		};
	}

	/**
	 * Every time the diagram receives focus update the diagram's components and connections
	 */
	@Override
	public void setFocus() {
		super.setFocus();

		// briefly turn on graphiti runtime auto update (turn off at end of method)
		// this will allow graphiti to update the contents of the diagram automatically
		// We don't want this value true all the time because edits in the text editor would cause diagram changes
		// constantly
		((SADDiagramTypeProvider) getDiagramTypeProvider()).setAutoUpdateAtRuntime(true);

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

		((SADDiagramTypeProvider) getDiagramTypeProvider()).setAutoUpdateAtRuntime(false);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		activateContext("gov.redhawk.ide.sad.graphiti.ui.contexts.diagram");
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

	private void deactivateAllContexts() {
		if (!contextActivations.isEmpty()) {
			IContextService contextService = (IContextService) getSite().getService(IContextService.class);
			for (IContextActivation activation : contextActivations) {
				contextService.deactivateContext(activation);
			}
		}
	}

	/* 
	 * For use before init()
	 */
	public void addContext(String context) {
		contexts.add(context);
	}

	public void activateContext(String context) {
		IContextService contextService = (IContextService) getSite().getService(IContextService.class);
		if (contextService != null) {
			IContextActivation activation = contextService.activateContext(context);
			contextActivations.add(activation);
		}

	}

}
