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

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.ide.graphiti.ui.palette.RHGraphitiPaletteBehavior;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.graphiti.mm.pictograms.Diagram;
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

public class GraphitiDcdDiagramEditor extends DiagramEditor {

	private EditingDomain editingDomain;
	private List<String> contexts = new ArrayList<String>();
	private List<IContextActivation> contextActivations = new ArrayList<IContextActivation>();

	public GraphitiDcdDiagramEditor(EditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	@Override
	protected DiagramBehavior createDiagramBehavior() {
		return new DiagramBehavior(this) {

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
		};
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		activateContext("gov.redhawk.ide.dcd.graphiti.ui.contexts.diagram");
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
