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

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.graphiti.ui.internal.editor.GFWorkspaceCommandStackImpl;

import gov.redhawk.ide.sad.internal.ui.editor.SadPropertiesPage;
import gov.redhawk.ui.editor.SCAFormEditor;

public class GraphitiWaveformPropertiesPage extends SadPropertiesPage {

	public GraphitiWaveformPropertiesPage(SCAFormEditor editor, String id, String title, boolean newStyleHeader) {
		super(editor, id, title, newStyleHeader);
	}

	public GraphitiWaveformPropertiesPage(SCAFormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * @return the common command stack provided by the parent editor
	 */
	protected BasicCommandStack getCommandStack() {
		return ((GFWorkspaceCommandStackImpl) getEditingDomain().getCommandStack());
	}
}
