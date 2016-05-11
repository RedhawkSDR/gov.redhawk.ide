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

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.statushandlers.StatusManager;

import gov.redhawk.ide.graphiti.dcd.ui.DCDUIGraphitiPlugin;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;

public class GraphitiDcdSandboxEditor extends GraphitiDcdExplorerEditor {
	public static final String EDITOR_ID = "gov.redhawk.ide.graphiti.dcd.ui.editor.DcdSandbox";

	@Override
	public String getDiagramContext(Resource dcdResource) {
		return DUtil.DIAGRAM_CONTEXT_LOCAL;
	}

	@Override
	protected void addPages() {
		if (!getEditingDomain().getResourceSet().getResources().isEmpty()
			&& !(getEditingDomain().getResourceSet().getResources().get(0)).getContents().isEmpty()) {
			try {
				final Resource dcdResource = getMainResource();

				final DiagramEditor editor = createDiagramEditor();
				setDiagramEditor(editor);
				final IEditorInput input = createDiagramInput(dcdResource);
				int pageIndex = addPage(editor, input);
				setPageText(pageIndex, "Diagram");
				if (DUtil.isDiagramExplorer(getDiagramEditor().getDiagramBehavior().getDiagramTypeProvider().getDiagram())) {
					setPartName("Device Manager");
				} else {
					setPartName("Device Manager Chalkboard");
				}

				getEditingDomain().getCommandStack().removeCommandStackListener(getCommandStackListener());

				// make sure diagram elements reflect current runtime state
				this.modelMap.reflectRuntimeStatus();

				// set layout for sandbox editors
				DUtil.layout(editor);
			} catch (final PartInitException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final IOException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			} catch (final CoreException e) {
				StatusManager.getManager().handle(new Status(IStatus.ERROR, DCDUIGraphitiPlugin.PLUGIN_ID, "Failed to create editor parts.", e),
					StatusManager.LOG | StatusManager.SHOW);
			}
		}
	}
}
