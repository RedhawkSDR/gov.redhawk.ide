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

package gov.redhawk.ide.dcd.internal.ui.editor;

import gov.redhawk.ide.dcd.internal.ui.HelpContextIds;
import gov.redhawk.ide.dcd.internal.ui.ScaIdeConstants;
import gov.redhawk.ui.editor.ScaFormPage;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

// TODO: Auto-generated Javadoc
/**
 * The Class ImplementationPage.
 */
public class DevicesPage extends ScaFormPage {

	/** The Constant PAGE_ID. */
	public static final String PAGE_ID = "devices"; //$NON-NLS-1$
	private final DevicesBlock fBlock;

	/**
	 * Instantiates a new properties form page.
	 * 
	 * @param editor the editor
	 */
	public DevicesPage(final NodeEditor editor) {
		super(editor, DevicesPage.PAGE_ID, "Devices");
		this.fBlock = new DevicesBlock(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NodeEditor getEditor() {
		return (NodeEditor) super.getEditor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getHelpResource() {
		return ScaIdeConstants.PLUGIN_DOC_ROOT + "guide/tools/editors/node_editor/devices.htm"; //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		final ScrolledForm form = managedForm.getForm();
		form.setText("Devices");

		// TODO
		// form.setImage(PDEPlugin.getDefault().getLabelProvider().get(PDEPluginImages.DESC_EXTENSIONS_OBJ));
		this.fBlock.createContent(managedForm);

		// refire selection
		this.fBlock.getSection().fireSelection();
		PlatformUI.getWorkbench().getHelpSystem().setHelp(form.getBody(), HelpContextIds.NODE_OVERVIEW);
		super.createFormContent(managedForm);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void refresh(final Resource resource) {
		this.fBlock.refresh(resource);
	}

}
