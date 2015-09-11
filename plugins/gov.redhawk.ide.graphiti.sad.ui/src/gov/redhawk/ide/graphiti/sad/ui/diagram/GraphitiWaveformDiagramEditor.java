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

import gov.redhawk.ide.graphiti.ui.diagram.AbstractGraphitiDiagramEditor;

import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.graphiti.ui.editor.DiagramBehavior;
import org.eclipse.jface.util.TransferDropTargetListener;

public class GraphitiWaveformDiagramEditor extends AbstractGraphitiDiagramEditor {

	public GraphitiWaveformDiagramEditor(EditingDomain editingDomain) {
		super(editingDomain);
		addContext("gov.redhawk.ide.graphiti.sad.ui.contexts.diagram");
	}

	@Override
	protected TransferDropTargetListener createDropTargetListener(GraphicalViewer viewer, DiagramBehavior behavior) {
		return new DiagramDropTargetListener(viewer, behavior);
	}

}
