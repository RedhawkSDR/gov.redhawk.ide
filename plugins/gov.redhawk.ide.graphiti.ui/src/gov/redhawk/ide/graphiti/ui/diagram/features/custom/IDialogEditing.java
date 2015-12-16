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
package gov.redhawk.ide.graphiti.ui.diagram.features.custom;

import org.eclipse.graphiti.features.context.ICustomContext;

/**
 * Interface to support displaying an editing dialog for an pattern or as a standalone feature.
 */
public interface IDialogEditing {
	/**
	 * Checks whether an editing dialog can be displayed for the pictogram element of the given context.
	 *
	 * @param context the Graphiti context
	 * @return true if dialog can be displayed
	 */
	public boolean canDialogEdit(ICustomContext context);

	/**
	 * Opens a dialog to edit the pictogram element of the given context. If dialog was canceled, the return
	 * value should be false so that the feature is not added to the undo stack.
	 *
	 * @param context the Graphiti context
	 * @return false if the dialog was canceled, or true if the changes were accepted
	 */
	public boolean dialogEdit(ICustomContext context);
}
