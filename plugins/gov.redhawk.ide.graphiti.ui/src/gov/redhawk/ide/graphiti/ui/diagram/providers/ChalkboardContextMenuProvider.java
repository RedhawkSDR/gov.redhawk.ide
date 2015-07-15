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
package gov.redhawk.ide.graphiti.ui.diagram.providers;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.graphiti.ui.editor.DiagramEditorContextMenuProvider;
import org.eclipse.graphiti.ui.platform.GraphitiShapeEditPart;
import org.eclipse.graphiti.ui.platform.IConfigurationProvider;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.ActionFactory;

/**
 * 
 */
public class ChalkboardContextMenuProvider extends DiagramEditorContextMenuProvider {

	/**
	 * @param viewer
	 * @param registry
	 * @param configurationProvider
	 */
	public ChalkboardContextMenuProvider(EditPartViewer viewer, ActionRegistry registry, IConfigurationProvider configurationProvider) {
		super(viewer, registry, configurationProvider);
	}

	/**
	 * Method needed to change text of DeleteAction from "Delete" to "Release" if component is selected
	 */
	@Override
	public void buildContextMenu(IMenuManager manager) {
		super.buildContextMenu(manager);
		IContributionItem[] items = manager.getItems();

		ActionContributionItem actionItem = null;
		for (IContributionItem item : items) {
			if (item instanceof ActionContributionItem) {
				actionItem = (ActionContributionItem) item;
				IAction action = actionItem.getAction();
				if (action != null && action.getId() != null && action.getId().equals(ActionFactory.DELETE.getId())
					&& this.getViewer().getSelection() instanceof StructuredSelection) {
					StructuredSelection selection = (StructuredSelection) this.getViewer().getSelection();
					if (selection.getFirstElement() instanceof GraphitiShapeEditPart
						&& ((GraphitiShapeEditPart) selection.getFirstElement()).getModel() instanceof RHContainerShape) {
						action.setText("Release");
						break;
					} else {
						action.setText("Delete");
						break;
					}
				}
			}
		}

		// Move Release and Terminate to the bottom of the context menu
		if (actionItem != null && "Release".equals(actionItem.getAction().getText())) {
			ActionContributionItem terminateAction = findTerminateAction(items);
			if (terminateAction != null) {
				terminateAction.setId("terminate");
				manager.remove(actionItem);
				manager.insertBefore(terminateAction.getId(), actionItem);
			}
		}
	}

	private ActionContributionItem findTerminateAction(IContributionItem[] items) {
		for (IContributionItem item : items) {
			if (item instanceof ActionContributionItem) {
				ActionContributionItem actionItem = (ActionContributionItem) item;
				IAction action = actionItem.getAction();
				if (action.getId() == null && "Terminate".equals(action.getText())) {
					return actionItem;
				}
			}
		}
		return null;
	}
}
