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
package gov.redhawk.ide.sad.graphiti.ui.diagram;

import java.util.List;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.workspace.util.WorkspaceSynchronizer.Delegate;
import org.eclipse.graphiti.features.IFeatureProvider;
import org.eclipse.graphiti.features.IReason;
import org.eclipse.graphiti.features.IUpdateFeature;
import org.eclipse.graphiti.features.context.impl.UpdateContext;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.util.TransferDropTargetListener;

public class RHGraphitiDiagramEditor extends DiagramEditor {

	private EditingDomain editingDomain;

	public RHGraphitiDiagramEditor(EditingDomain editingDomain) {
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
			protected List<TransferDropTargetListener> createBusinessObjectDropTargetListeners() {
				List<TransferDropTargetListener> retVal = super.createBusinessObjectDropTargetListeners();
				retVal.add(0, new DiagramDropTargetListener(getDiagramContainer().getGraphicalViewer(), this));
				return retVal;
			}

		};
	}

	@Override
	public void setFocus() {
		super.setFocus();
		Diagram diagram = getDiagramTypeProvider().getDiagram();
		IFeatureProvider featureProvider = getDiagramTypeProvider().getFeatureProvider();

		if (diagram != null && featureProvider != null) {
			final UpdateContext updateContext = new UpdateContext(diagram);
			final IUpdateFeature updateFeature = featureProvider.getUpdateFeature(new UpdateContext(diagram));
			final IReason updateNeeded = updateFeature.updateNeeded(updateContext);
			if (updateNeeded.toBoolean()) {
				updateFeature.update(updateContext);
			}
		}
	}
}
