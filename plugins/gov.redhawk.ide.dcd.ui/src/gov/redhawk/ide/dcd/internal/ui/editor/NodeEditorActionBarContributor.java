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

import gov.redhawk.ui.editor.ScaMultipageActionBarContributor;

import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorPart;

/**
 * @since 1.1
 */
public class NodeEditorActionBarContributor extends ScaMultipageActionBarContributor {

	public NodeEditorActionBarContributor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IEditorActionBarContributor getSubActionBarContributor(final IEditorPart activeEditor) {
		/* Disabled to fix ticket #126, add this back if useful contributions are needed */
		if (activeEditor instanceof NodeEditor) {
			return new DcdActionBarContributor();
		}

		return super.getSubActionBarContributor(activeEditor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getType(final IEditorPart activeEditor) {
		if (activeEditor != null) {
			return activeEditor.getTitle();
		}
		return "";
	}

}
