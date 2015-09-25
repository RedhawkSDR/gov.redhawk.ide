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
package gov.redhawk.ide.graphiti.dcd.internal.ui.editor;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.commands.NonDirtyingCommand;
import mil.jpeojtrs.sca.dcd.DeviceConfiguration;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

public class GraphitiDcdExplorerEditor extends GraphitiDcdSandboxEditor {

	public GraphitiDcdExplorerEditor() {
	}

	@Override
	public String getDiagramContext(Resource sadResource) {
		return DUtil.DIAGRAM_CONTEXT_EXPLORER;
	}

	@Override
	protected void createModel() {
		super.createModel();
		setDcd(DeviceConfiguration.Util.getDeviceConfiguration(super.getMainResource()));
		initModelMap();
	}

	@Override
	protected void addPages() {
		super.addPages();
		try {
			IEditorPart textEditor = createTextEditor(getEditorInput());
			setTextEditor(textEditor);
			if (textEditor != null) {
				final int dcdSourcePageNum = addPage(-1, textEditor, getEditorInput(), getMainResource());
				for (String s : getDeviceManager().getProfile().split("/")) {
					if (s.contains(".xml")) {
						this.setPageText(dcdSourcePageNum, s);
						break;
					}
					this.setPageText(dcdSourcePageNum, getDeviceManager().getLabel());
				}
			}
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to add pages.", e));
		}

		final Diagram diagram = this.getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram();
		this.setPartName(getDeviceManager().getLabel());
		NonDirtyingCommand.execute(diagram, new NonDirtyingCommand() {
			@Override
			public void execute() {
				diagram.setGridUnit(-1); // hide grid on diagram by setting grid units to -1
			}
		});
	}
}
