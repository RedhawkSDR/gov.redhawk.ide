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
package gov.redhawk.ide.swtbot.diagram;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.gef.finder.SWTGefBot;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditor;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.ui.IEditorReference;

/**
 * Uses RHTestBotEditor for improved connection drawing in Graphiti diagrams
 */
public class RHSWTGefBot extends SWTGefBot {

	/**
	 * @see SWTGefBot#gefEditor(String)
	 */
	public RHBotGefEditor rhGefEditor(String fileName) throws WidgetNotFoundException {
		return (RHBotGefEditor) gefEditor(fileName, 0);
	}

	@Override
	protected SWTBotGefEditor createEditor(IEditorReference reference, SWTWorkbenchBot bot) {
		return new RHBotGefEditor(reference, bot);
	}

}
