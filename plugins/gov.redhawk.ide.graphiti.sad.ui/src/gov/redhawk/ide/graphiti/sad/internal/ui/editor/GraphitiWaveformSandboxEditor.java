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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.graphiti.sad.ui.SADUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaWaveform;

/**
 * The multi-page sandbox editor for waveforms ({@link ScaWaveform}). Includes a Graphiti diagram.
 */
public class GraphitiWaveformSandboxEditor extends GraphitiWaveformExplorerEditor {
	public static final String EDITOR_ID = "gov.redhawk.ide.graphiti.sad.ui.editor.localMultiPageSca";

	@Override
	public String getDiagramContext(Resource sadResource) {
		return DUtil.DIAGRAM_CONTEXT_LOCAL;
	}

	@Override
	protected void addPages() {
		// Only creates the other pages if there is something that can be edited
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				final Resource sadResource = getMainResource();

				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);

				initModelMap();

				final IEditorInput input = createDiagramInput(sadResource);
				int pageIndex = addPage(editor, input);
				setPageText(pageIndex, "Diagram");

				// set layout for diagram editors
				DUtil.layout(editor);

				getEditingDomain().getCommandStack().removeCommandStackListener(getCommandStackListener());

				// reflect runtime aspects here
				getModelMap().reflectRuntimeStatus();

				// set layout for sandbox editors
				DUtil.layout(editor);
			} catch (IOException | CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, SADUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}
}
