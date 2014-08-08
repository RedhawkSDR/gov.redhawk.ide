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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.graphiti.ui.editor.DefaultUpdateBehavior;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.swt.widgets.Display;

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
		
		//save editor automatically
		//When a change has been made to the sad.xml directly and the user switches to the Diagram and update runs
		Job saveJob = new Job("Save Editor Job") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				Display.getDefault().asyncExec(new Runnable() {
					@Override
					public void run() {
						doSave(monitor);
					}

				});
				return Status.OK_STATUS;
			}
		};
		saveJob.setUser(true);
		saveJob.schedule();
	}

}
