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

/**
 * Mix-in interface for patterns that support dialog editing.
 */
public interface IDialogEditingPattern extends IDialogEditing {
	/**
	 * Gets the edit name.
	 *
	 * @return name for UI representation
	 */
	public String getEditName();
}
