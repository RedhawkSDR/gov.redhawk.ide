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
package gov.redhawk.ide.graphiti.sad.internal.ui.page.components;

import org.eclipse.emf.common.ui.viewer.IViewerProvider;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.menus.IMenuService;

import gov.redhawk.ui.editor.SCAFormEditor;
import gov.redhawk.ui.editor.ScaFormPage;

public class SadComponentsPage extends ScaFormPage implements IViewerProvider {

	public static final String PAGE_ID = "sadComponentsPage";
	private static final String TOOLBAR_ID = "gov.redhawk.ide.graphiti.sad.internal.ui.editor.components.toolbar";
	private final ComponentsBlock fBlock;

	public SadComponentsPage(SCAFormEditor editor) {
		super(editor, SadComponentsPage.PAGE_ID, "Components");
		this.fBlock = new ComponentsBlock(this);
	}

	@Override
	protected void createFormContent(IManagedForm managedForm) {
		managedForm.getForm().setText("Components");
		this.fBlock.createContent(managedForm);
		super.createFormContent(managedForm);

		final ToolBarManager manager = (ToolBarManager) managedForm.getForm().getToolBarManager();
		final IMenuService service = (IMenuService) getSite().getService(IMenuService.class);
		service.populateContributionManager(manager, "toolbar:" + SadComponentsPage.TOOLBAR_ID);
		manager.update(true);
	}

	@Override
	protected void refresh(Resource resource) {
		this.fBlock.refresh(resource);
	}

	@Override
	public Viewer getViewer() {
		return this.fBlock.getSection().getStructuredViewerPart().getViewer();
	}

}
