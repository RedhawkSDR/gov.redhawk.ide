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
package gov.redhawk.ide.graphiti.dcd.ui.diagram;

import gov.redhawk.ide.graphiti.dcd.ui.diagram.providers.DCDDiagramTypeProvider;
import gov.redhawk.ide.graphiti.ui.diagram.GraphitiDiagramEditor;
import gov.redhawk.ide.graphiti.ui.diagram.providers.ChalkboardContextMenuProvider;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteBehavior;

import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultMarkerBehavior;
import org.eclipse.graphiti.ui.editor.DefaultPaletteBehavior;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.IDiagramEditorInput;
import org.eclipse.jface.util.TransferDropTargetListener;

public class GraphitiDcdDiagramEditor extends GraphitiDiagramEditor {

	public GraphitiDcdDiagramEditor(EditingDomain editingDomain) {
		super(editingDomain);
		addContext("gov.redhawk.ide.dcd.graphiti.ui.contexts.diagram");
	}

	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

			//Override Marker behavior because it modifies the underlying sad resource
			//and the user will be prompted if they would like to replace their file with what's on disk
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
					retVal.add(0, new DiagramDropTargetListener(getDiagramContainer().getGraphicalViewer(), this));
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

	/**
	 * Every time the diagram receives focus update the diagram's components and connections
	 */
	@Override
	public void setFocus() {
		super.setFocus();

		//briefly turn on graphiti runtime auto update (turn off at end of method)
		//this will allow graphiti to update the contents of the diagram automatically
		//We don't want this value true all the time because edits in the text editor would cause diagram changes constantly
		DCDDiagramTypeProvider provider = (DCDDiagramTypeProvider) getDiagramTypeProvider();
		provider.setAutoUpdateAtRuntime(true);
		updateDiagram();
		provider.setAutoUpdateAtRuntime(false);
	}
	
}
