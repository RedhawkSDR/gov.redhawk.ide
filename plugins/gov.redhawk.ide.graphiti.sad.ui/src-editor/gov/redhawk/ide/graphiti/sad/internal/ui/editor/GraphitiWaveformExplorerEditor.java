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
package gov.redhawk.ide.graphiti.sad.internal.ui.editor;

import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;

public class GraphitiWaveformExplorerEditor extends GraphitiWaveformSandboxEditor {

	public GraphitiWaveformExplorerEditor() {
	}

	@Override
	public String getDiagramContext(Resource sadResource) {
		return DUtil.DIAGRAM_CONTEXT_EXPLORER;
	}

	@Override
	protected void addPages() {
		super.addPages();

		final Diagram diagram = this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram();
		setPartName("Waveform Explorer");
		ScaModelCommand.execute(diagram, new ScaModelCommand() {
			@Override
			public void execute() {
				diagram.setGridUnit(-1); // hide grid on diagram by setting grid units to -1
			}
		});
	}
}
